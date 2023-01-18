/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNodeHelper;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesOperationsCreator {

	private final Set<IAbstractNode> fSelectedNodes;

	private final Set<TestCaseNode> fAffectedTestCases = new HashSet<>();
	private final Set<ConstraintNode> fAffectedConstraints = new HashSet<>();

	List<IModelOperation> fOperations;

	public GenericRemoveNodesOperationsCreator(
			Set<IAbstractNode> selectedNodes, 
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fSelectedNodes = selectedNodes;
		removeNodesWithAncestorsOnList(fSelectedNodes);

		fOperations = new ArrayList<>(); 

		createModelOperationsDeletingNodes(
				fSelectedNodes,
				getAllConstraintNodes(fSelectedNodes),
				getAllTestCaseNodes(fSelectedNodes),

				fAffectedConstraints,
				fAffectedTestCases,
				fOperations,

				extLanguageManager,
				typeAdapterProvider, 
				validate);
	}

	public List<IModelOperation> getOperations() {
		return fOperations;
	}

	public Set<ConstraintNode> getAffectedConstraints() {
		return fAffectedConstraints;
	}

	public Set<TestCaseNode> getAffectedTestCases() {
		return fAffectedTestCases;
	}

	private void removeNodesWithAncestorsOnList(Set<IAbstractNode> selectedNodes) {

		Iterator<IAbstractNode> iterator = selectedNodes.iterator();

		while (iterator.hasNext()) {

			IAbstractNode currentNode = iterator.next();

			List<IAbstractNode> ancestors = currentNode.getAncestors();

			for (IAbstractNode ancestor : ancestors) {

				if (selectedNodes.contains(ancestor)) {

					// node is deleted because ancestor will be remove with the whole sub-tree which includes current node 
					iterator.remove(); 
					break;
				}
			}
		}
	}

	private static void createModelOperationsDeletingNodes(
			Set<IAbstractNode> selectedNodes,
			Set<ConstraintNode> allConstraintNodes,
			Set<TestCaseNode> allTestCaseNodes,

			Set<ConstraintNode> outAffectedConstraints,
			Set<TestCaseNode> outAffectedTestCases,
			List<IModelOperation> outOperations,

			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate){

		Set<IAbstractNode> affectedNodes = new HashSet<>();

		processNodes(
				selectedNodes, allConstraintNodes, allTestCaseNodes, 
				affectedNodes, outAffectedConstraints, outAffectedTestCases, 
				outOperations, 
				extLanguageManager, validate);

		if (!outAffectedConstraints.isEmpty()) {
			addRemoveOperationsForAffectedConstraints(
					outAffectedConstraints, outOperations, 
					extLanguageManager, typeAdapterProvider, validate);
		}

		if (!outAffectedTestCases.isEmpty()) {
			addRemoveOperationsForAffectedTestCases(
					outAffectedTestCases, outOperations, 
					extLanguageManager, typeAdapterProvider, validate);
		}

		if (!affectedNodes.isEmpty()) {
			addRemoveOperationsForAffectedNodes(
					affectedNodes, outOperations, 
					extLanguageManager, typeAdapterProvider, validate);
		}
	}

	private static void processNodes(
			Set<IAbstractNode> selectedNodes, 
			Set<ConstraintNode> allConstraintNodes, 
			Set<TestCaseNode> allTestCaseNodes, 
			Set<IAbstractNode> outAffectedNodes, 
			Set<ConstraintNode> outAffectedConstraints, 
			Set<TestCaseNode> outAffectedTestCases,
			List<IModelOperation> outOperations, IExtLanguageManager extLanguageManager, boolean validate) {

		NodesByType selectedNodesByType = new NodesByType(selectedNodes);

		processClassesAndMethods(selectedNodesByType, outAffectedNodes);

		processParametersAndChoices(
				selectedNodesByType, 
				allConstraintNodes, allTestCaseNodes, 
				outAffectedNodes, outAffectedConstraints, outAffectedTestCases);

		processConstraintsAndTestCases(selectedNodesByType, outAffectedNodes);

		processOtherNodes(selectedNodesByType, outAffectedNodes);
	}

	private static void processClassesAndMethods(
			NodesByType selectedNodesByType, 
			Set<IAbstractNode> outAffectedNodes) {

		ArrayList<ClassNode> classNodes = selectedNodesByType.getClasses();

		if (!classNodes.isEmpty()) {
			processClasses(classNodes, outAffectedNodes);
		}

		ArrayList<MethodNode> methods = selectedNodesByType.getMethods();

		if (!methods.isEmpty()) {
			processMethods(methods, outAffectedNodes);
		}
	}

	private static void processParametersAndChoices(
			NodesByType selectedNodesByType,
			Set<ConstraintNode> allConstraintNodes, 
			Set<TestCaseNode> allTestCaseNodes,
			Set<IAbstractNode> outAffectedNodes, 
			Set<ConstraintNode> outAffectedConstraints,
			Set<TestCaseNode> outAffectedTestCases) {

		processParameters(selectedNodesByType, allConstraintNodes, outAffectedConstraints, outAffectedNodes);

		ArrayList<ChoiceNode> choiceNodes = selectedNodesByType.getChoices();

		if (!choiceNodes.isEmpty()) {
			processChoicesFilteringConstraintsAndTestCases(
					choiceNodes, 
					allConstraintNodes, allTestCaseNodes, 
					outAffectedConstraints,	outAffectedTestCases, 
					outAffectedNodes);
		}
	}

	private static void processConstraintsAndTestCases(
			NodesByType selectedNodesByType,
			Set<IAbstractNode> inOutAffectedNodes) {

		ArrayList<TestCaseNode> testCaseNodes = selectedNodesByType.getTestCaseNodes();

		if (!testCaseNodes.isEmpty()) {
			processTestCases(testCaseNodes, inOutAffectedNodes);
		}

		ArrayList<ConstraintNode> constraints = selectedNodesByType.getConstraints();

		if (!constraints.isEmpty()) {
			processConstraints(constraints, inOutAffectedNodes);
		}
	}

	private static void processOtherNodes(
			NodesByType selectedNodesByType, 
			Set<IAbstractNode> outAffectedNodes) {

		ArrayList<IAbstractNode> otherNodes = selectedNodesByType.getOtherNodes();

		if (!otherNodes.isEmpty()) {
			processOtherNodes(otherNodes, outAffectedNodes);
		}
	}

	private static void processParameters(
			NodesByType selectedNodesByType, 
			Set<ConstraintNode> allConstraintNodes,
			Set<ConstraintNode> inOutAffectedConstraints,
			Set<IAbstractNode> inOutAffectedNodes) {

		ArrayList<BasicParameterNode> globalParameters = selectedNodesByType.getGlobalParameters();

		if (!globalParameters.isEmpty()) {
			processGlobalParameters(
					selectedNodesByType, 
					allConstraintNodes, 
					inOutAffectedConstraints, 
					inOutAffectedNodes);
		}

		ArrayList<BasicParameterNode> localParameters = selectedNodesByType.getLocalParameters();

		if (!localParameters.isEmpty()) {
			processLocalParameters(
					selectedNodesByType, 
					allConstraintNodes, 
					inOutAffectedConstraints, inOutAffectedNodes);
		}
	}

	private static void processConstraints(ArrayList<ConstraintNode> constraintNodes, Set<IAbstractNode> affectedNodes) {

		for (ConstraintNode constraint : constraintNodes) {
			affectedNodes.add(constraint);
		}
	}

	private static void processMethods(ArrayList<MethodNode> methods, Set<IAbstractNode> affectedNodes) {

		//Removing methods - information for model map has been already taken
		//ArrayList<MethodNode> methods = selectedNodesByType.getMethods();

		Iterator<MethodNode> methodItr = methods.iterator();

		while (methodItr.hasNext()) {
			MethodNode method = methodItr.next();
			affectedNodes.add(method);
			methodItr.remove();
		}
	}

	private static void processLocalParameters(
			NodesByType selectedNodesByType, 
			Set<ConstraintNode> allConstraintNodes,
			//HashMap<ClassNode, HashMap<String, HashMap<MethodNode, List<String>>>> duplicatesMap,
			//HashMap<MethodNode, List<BasicParameterNode>> parameterMap, 
			Set<ConstraintNode> outAffectedConstraints,
			Set<IAbstractNode> affectedNodes) {
		/*
		 * Iterate through parameters. If parent method is potential duplicate -
		 * add it to map for further validation. Replace values of to-be-deleted
		 * param with NULL to remove them later without disturbing parameters
		 * order. If parameters method is not potential duplicate - simply
		 * forward it for removal and remove it from to-remove list.
		 */
		Iterator<BasicParameterNode> paramItr = selectedNodesByType.getLocalParameters().iterator();
		while (paramItr.hasNext()) {
			BasicParameterNode param = paramItr.next();
			IAbstractNode parent = param.getParent();

			if (parent instanceof MethodNode) {
				//remove mentioning constraints from the list to avoid duplicates
				createAffectedConstraints(param, allConstraintNodes, outAffectedConstraints);
				affectedNodes.add(param);
				paramItr.remove();
			}

			if (parent instanceof CompositeParameterNode) {
				affectedNodes.add(param);
			}

		}
	}

	private static void processGlobalParameters(NodesByType selectedNodesByType,
			Set<ConstraintNode> allConstraintNodes,
			//HashMap<ClassNode, HashMap<String, HashMap<MethodNode, List<String>>>> duplicatesMap, 
			//HashMap<MethodNode, List<BasicParameterNode>> parameterMap,
			Set<ConstraintNode> outAffectedConstraints,
			Set<IAbstractNode> inOutAffectedNodes) {

		/*
		 * Iterate through global params. Do the same checks as for method
		 * parameters with every linker. If no linker is in potentially
		 * duplicate method - just proceed to remove global and all linkers and
		 * remove it from the lists.
		 */

		ArrayList<BasicParameterNode> globalParameters = selectedNodesByType.getGlobalParameters();

		Iterator<BasicParameterNode> globalItr = globalParameters.iterator();
		
		while (globalItr.hasNext()) {
			BasicParameterNode global = globalItr.next();
			List<BasicParameterNode> linkedParameters = GlobalParameterNodeHelper.getLinkedParameters(global);
			
			boolean isDependent = false;
			if (!isDependent) {
				//remove mentioning constraints from the list to avoid duplicates
				createAffectedConstraints(global, allConstraintNodes, outAffectedConstraints);
				inOutAffectedNodes.add(global);
				//	globalItr.remove(); 
				/*
				 * in case linkers contain parameters assigned to removal -
				 * remove them from list; Global param removal will handle them.
				 */
				for (BasicParameterNode param : linkedParameters) {
					selectedNodesByType.getLocalParameters().remove(param);
				}
			}
		}
	}

	private static void processOtherNodes(ArrayList<IAbstractNode> otherNodes, Set<IAbstractNode> affectedNodes) {

		for (IAbstractNode abstractNode : otherNodes) {
			affectedNodes.add(abstractNode);
		}
	}

	private static void processTestCases(ArrayList<TestCaseNode> testCaseNodes, Set<IAbstractNode> affectedNodes) {

		for (TestCaseNode testCaseNode : testCaseNodes) {
			affectedNodes.add(testCaseNode);
		}
	}

	private static void processChoicesFilteringConstraintsAndTestCases(
			ArrayList<ChoiceNode> choiceNodes, 
			Set<ConstraintNode> allConstraintNodes,	Set<TestCaseNode> allTestCaseNodes, 
			Set<ConstraintNode> outAffectedConstraints, Set<TestCaseNode> outAffectedTestCases, 
			Set<IAbstractNode> affectedNodes) {

		for (ChoiceNode choiceNode : choiceNodes) {
			createAffectedConstraints(choiceNode, allConstraintNodes, outAffectedConstraints);
			createAffectedTestCases(choiceNode, allTestCaseNodes, outAffectedTestCases);
			affectedNodes.add(choiceNode);
		}
	}

	private static void processClasses(ArrayList<ClassNode> classsNodes, Set<IAbstractNode> inOutAffectedNodes) {

		for (ClassNode classNode : classsNodes) {
			inOutAffectedNodes.add(classNode);
		}
	}

	private static void addRemoveOperationsForAffectedConstraints(
			Set<ConstraintNode> affectedConstraints, 
			List<IModelOperation> outOperations,
			IExtLanguageManager extLanguageManager, 
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate) {

		affectedConstraints.stream().forEach(
				e-> addOperation(
						FactoryRemoveOperation.getRemoveOperation(e, typeAdapterProvider, validate, extLanguageManager),
						outOperations));
	}

	private static void addRemoveOperationsForAffectedTestCases(
			Set<TestCaseNode> affectedTestCases,
			List<IModelOperation> outOperations, 
			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate) {

		affectedTestCases.stream().forEach(
				e-> addOperation(
						FactoryRemoveOperation.getRemoveOperation(e, typeAdapterProvider, validate, extLanguageManager),
						outOperations));
	}

	private static void addRemoveOperationsForAffectedNodes(
			Set<IAbstractNode> affectedNodes,
			List<IModelOperation> outOperations, 
			IExtLanguageManager extLanguageManager, 
			ITypeAdapterProvider typeAdapterProvider,
			boolean validate) {

		affectedNodes.stream().forEach(
				e-> addOperation(
						FactoryRemoveOperation.getRemoveOperation(e, typeAdapterProvider, validate, extLanguageManager),
						outOperations));
	}

	private static void addOperation(IModelOperation operation, List<IModelOperation> fOperations) {

		if (operation == null) {
			ExceptionHelper.reportRuntimeException("Attempt to add empty operation.");
		}

		fOperations.add(operation);
	}

	private static Set<ConstraintNode> getAllConstraintNodes(Set<IAbstractNode> selectedNodes) { 

		// TODO MO-RE rewrite using services of nodes
		// List<IAbstractNode> IAbstractNode.getAllFilteredChilden(NodeTypeFilter) TODO
		// IConstraintsParentNode.getConstraintNodes()

		return selectedNodes.iterator().next()
				.getRoot()
				.getChildren()
				.stream()
				.filter(e -> (e instanceof ClassNode))
				.map(m -> m.getChildren()
						.stream()
						.filter(e -> (e instanceof MethodNode))
						.collect(Collectors.toList()))
				.flatMap(f -> f.stream())
				.map(m -> m.getChildren()
						.stream()
						.filter(e -> (e instanceof ConstraintNode))
						.collect(Collectors.toList()))
				.flatMap(f -> f.stream())
				.map(e -> (ConstraintNode) e)
				.collect(Collectors.toSet());
	}

	private static Set<TestCaseNode> getAllTestCaseNodes(Set<IAbstractNode> selectedNodes) {

		// TODO MO-RE rewrite using services of nodes
		// List<IAbstractNode> IAbstractNode.getAllFilteredChilden(NodeTypeFilter) TODO
		// ITestCasesParentNode.getTestCaseNodes()

		return selectedNodes.iterator().next()
				.getRoot()
				.getChildren()
				.stream()
				.filter(e -> (e instanceof ClassNode))
				.map(m -> m.getChildren()
						.stream()
						.filter(e -> (e instanceof MethodNode))
						.collect(Collectors.toList()))
				.flatMap(f -> f.stream())
				.map(m -> m.getChildren()
						.stream()
						.filter(e -> (e instanceof TestCaseNode))
						.collect(Collectors.toList()))
				.flatMap(f -> f.stream())
				.map(e -> (TestCaseNode) e)
				.collect(Collectors.toSet());
	}

	private static void createAffectedConstraints(
			IAbstractNode node, 
			Set<ConstraintNode> allConstraintNodes,
			Set<ConstraintNode> outAffectedConstraints) {

		if (node instanceof ChoiceNode) {
			Iterator<ConstraintNode> itr = allConstraintNodes.iterator();
			while (itr.hasNext()) {
				ConstraintNode constraintNode = itr.next();
				if (constraintMentionsChoiceNodeOrAnyChild((ChoiceNode)node, constraintNode)) {
					outAffectedConstraints.add(constraintNode);
				}
			}
		} else if (node instanceof BasicParameterNode) {
			Iterator<ConstraintNode> itr = allConstraintNodes.iterator();
			while (itr.hasNext()) {
				ConstraintNode constraint = itr.next();
				if (constraint.mentions((BasicParameterNode)node)) {
					outAffectedConstraints.add(constraint);
				}
			}
		}
	}

	private static boolean constraintMentionsChoiceNodeOrAnyChild(ChoiceNode choiceNode, ConstraintNode constraintNode) {

		if (constraintNode.mentions(choiceNode)) {
			return true;
		}

		List<ChoiceNode> childChoiceNodes = choiceNode.getChoices();

		for (ChoiceNode childChoiceNode : childChoiceNodes) {

			if (constraintMentionsChoiceNodeOrAnyChild(childChoiceNode, constraintNode)) {
				return true;
			}
		}

		return false;
	}

	private static void createAffectedTestCases(
			IAbstractNode node, 
			Set<TestCaseNode> allTestCaseNodes,
			Set<TestCaseNode> outAffectedTestCases) {

		Iterator<TestCaseNode> itr = allTestCaseNodes.iterator();
		while (itr.hasNext()) {
			TestCaseNode testCase = itr.next();
			if (testCase.mentions((ChoiceNode)node)) {
				outAffectedTestCases.add(testCase);
			}
		}

	}

}
