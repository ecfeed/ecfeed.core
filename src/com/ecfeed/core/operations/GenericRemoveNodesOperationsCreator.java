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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesOperationsCreator {

	private final Set<IAbstractNode> fSelectedNodes;
	private NodesByCathegory fAffectedNodesByCathegory;
	private List<IModelOperation> fOperations;

	public GenericRemoveNodesOperationsCreator(
			Set<IAbstractNode> selectedNodes, 
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fSelectedNodes = selectedNodes;
		fAffectedNodesByCathegory = new NodesByCathegory();
		fOperations = new ArrayList<>();
		removeNodesWithAncestorsOnList(fSelectedNodes);

		createDeletingNodesOperations(
				fSelectedNodes,
				fAffectedNodesByCathegory,
				fOperations,

				extLanguageManager,
				typeAdapterProvider, 
				validate);
	}

	public List<IModelOperation> getOperations() {
		return fOperations;
	}

	public Set<ConstraintNode> getAffectedConstraints() {
		return fAffectedNodesByCathegory.getConstraints();
	}

	public Set<TestCaseNode> getAffectedTestCases() {
		return fAffectedNodesByCathegory.getTestCases();
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

	private static void createDeletingNodesOperations(
			Set<IAbstractNode> selectedNodes,
			NodesByCathegory outAffectedNodesByCathegory,
			List<IModelOperation> outOperations, // XYX refactor - return

			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate){

		processNodes(
				selectedNodes, 
				outAffectedNodesByCathegory,
				outOperations, 
				extLanguageManager, validate);

		createOperations(outAffectedNodesByCathegory, outOperations, extLanguageManager, typeAdapterProvider, validate);
	}

	private static void createOperations(
			NodesByCathegory outAffectedNodesByCathegory,
			List<IModelOperation> outOperations, 
			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate) {

		Set<ConstraintNode> affectedConstraints = outAffectedNodesByCathegory.getConstraints();

		if (!affectedConstraints.isEmpty()) {
			addRemoveOperationsForAffectedConstraints(
					affectedConstraints, outOperations, 
					extLanguageManager, typeAdapterProvider, validate);
		}

		Set<TestCaseNode> affectedTestCases = outAffectedNodesByCathegory.getTestCases();

		if (!affectedTestCases.isEmpty()) {
			addRemoveOperationsForAffectedTestCases(
					affectedTestCases, outOperations, 
					extLanguageManager, typeAdapterProvider, validate);
		}

		Set<IAbstractNode> affectedOtherNodes = outAffectedNodesByCathegory.getOtherNodes();

		if (!affectedOtherNodes.isEmpty()) {
			addRemoveOperationsForAffectedNodes(
					affectedOtherNodes, outOperations, 
					extLanguageManager, typeAdapterProvider, validate);
		}
	}

	private static void processNodes(
			Set<IAbstractNode> selectedNodes, 
			NodesByCathegory outAffectedNodes,
			List<IModelOperation> outOperations, IExtLanguageManager extLanguageManager, boolean validate) {

		NodesByType selectedNodesByType = new NodesByType(selectedNodes);

		processClassesAndMethods(selectedNodesByType, outAffectedNodes);

		processParametersAndChoices(
				selectedNodesByType, 
				//outAffectedNodes, outAffectedConstraints, outAffectedTestCases
				outAffectedNodes);

		processConstraintsAndTestCases(selectedNodesByType, outAffectedNodes);
	}

	private static void processClassesAndMethods(
			NodesByType selectedNodesByType, 
			NodesByCathegory outAffectedNodes) {

		ArrayList<ClassNode> classNodes = selectedNodesByType.getClasses();

		if (!classNodes.isEmpty()) {
			processClasses(classNodes, outAffectedNodes);
		}

		ArrayList<MethodNode> methods = selectedNodesByType.getMethods();

		if (!methods.isEmpty()) {
			processMethods(methods, outAffectedNodes);
		}
	}

	private static void processParametersAndChoices(NodesByType selectedNodesByType, NodesByCathegory inOutAffectedNodes) {

		processParameters(selectedNodesByType, inOutAffectedNodes);

		processChoices(selectedNodesByType,	inOutAffectedNodes);
	}

	private static void processChoices(
			NodesByType selectedNodesByType, 
			NodesByCathegory inOutAffectedNodes) {

		ArrayList<ChoiceNode> choiceNodes = selectedNodesByType.getChoices();

		if (!choiceNodes.isEmpty()) {
			processChoicesFilteringConstraintsAndTestCases(
					choiceNodes, 
					inOutAffectedNodes);
		}
	}

	private static void processConstraintsAndTestCases(
			NodesByType selectedNodesByType,
			NodesByCathegory inOutAffectedNodes) {

		ArrayList<TestCaseNode> testCaseNodes = selectedNodesByType.getTestCaseNodes();

		if (!testCaseNodes.isEmpty()) {
			processTestCases(testCaseNodes, inOutAffectedNodes);
		}

		ArrayList<ConstraintNode> constraints = selectedNodesByType.getConstraints();

		if (!constraints.isEmpty()) {
			processConstraints(constraints, inOutAffectedNodes);
		}
	}

	private static void processParameters(
			NodesByType selectedNodesByType, NodesByCathegory inOutAffectedNodes) {

		ArrayList<AbstractParameterNode> globalParameters = selectedNodesByType.getGlobalParameters();

		if (!globalParameters.isEmpty()) {
			processGlobalParameters(selectedNodesByType, inOutAffectedNodes);
		}

		ArrayList<AbstractParameterNode> localParameters = selectedNodesByType.getLocalParameters();

		if (!localParameters.isEmpty()) {
			processLocalParameters(selectedNodesByType, inOutAffectedNodes);
		}
	}

	private static void processConstraints(ArrayList<ConstraintNode> constraintNodes, NodesByCathegory inOutAffectedNodes) {

		for (ConstraintNode constraint : constraintNodes) {
			inOutAffectedNodes.addConstraint(constraint);
		}
	}

	private static void processMethods(ArrayList<MethodNode> methods, NodesByCathegory inOutAffectedNodes) {

		Iterator<MethodNode> methodItr = methods.iterator();

		while (methodItr.hasNext()) {
			MethodNode method = methodItr.next();
			inOutAffectedNodes.addOtherNode(method);
		}
	}

	private static void processLocalParameters(
			NodesByType selectedNodesByType, 
			NodesByCathegory inOutAffectedNodes) {

		processLocalBasicParameters(selectedNodesByType, inOutAffectedNodes);

		processLocalBasicChildrenOfCompositeParameters(selectedNodesByType,	inOutAffectedNodes);

		processLocalCompositeParameters(selectedNodesByType, inOutAffectedNodes);
	}

	private static void processLocalBasicParameters(
			NodesByType selectedNodesByType, NodesByCathegory inOutAffectedNodes) {

		Iterator<AbstractParameterNode> paramItr = selectedNodesByType.getLocalParameters().iterator();

		while (paramItr.hasNext()) {

			AbstractParameterNode param = paramItr.next();

			if (!(param instanceof BasicParameterNode)) {
				continue;
			}

			processBasicParameter(param, inOutAffectedNodes);
		}
	}

	private static void processBasicParameter(
			AbstractParameterNode param,
			NodesByCathegory inOutAffectedNodes) {

		IAbstractNode parent = param.getParent();

		if ((parent instanceof MethodNode) || (parent instanceof CompositeParameterNode)) {

			accumulateAffectedConstraints(param, inOutAffectedNodes);
			inOutAffectedNodes.addOtherNode(param);
		}
	}

	private static void processLocalBasicChildrenOfCompositeParameters(
			NodesByType selectedNodesByType,
			NodesByCathegory inOutNodesByCathegory // TODO MO-RE use
			) {

		Iterator<AbstractParameterNode> paramItr = selectedNodesByType.getLocalParameters().iterator();

		while (paramItr.hasNext()) {
			AbstractParameterNode param = paramItr.next();

			if (param instanceof CompositeParameterNode) {
				processBasicChildren((CompositeParameterNode)param);
			}
		}
	}

	private static void processBasicChildren(CompositeParameterNode param) {

		// TODO MO-RE: find all basic parameters (children of composite parameter)
		//and call processBasicParameter for each

	}

	private static void processLocalCompositeParameters(
			NodesByType selectedNodesByType,
			NodesByCathegory inOutAffectedNodes) {

		Iterator<AbstractParameterNode> paramItr = selectedNodesByType.getLocalParameters().iterator();

		while (paramItr.hasNext()) {
			AbstractParameterNode param = paramItr.next();

			if (param instanceof CompositeParameterNode)
				inOutAffectedNodes.addOtherNode(param);
		}
	}

	private static void processGlobalParameters(
			NodesByType selectedNodesByType,
			NodesByCathegory inOutAffectedNodes) {

		/*
		 * Iterate through global params. Do the same checks as for method
		 * parameters with every linker. If no linker is in potentially
		 * duplicate method - just proceed to remove global and all linkers and
		 * remove it from the lists.
		 */

		ArrayList<AbstractParameterNode> globalParameters = selectedNodesByType.getGlobalParameters();

		Iterator<AbstractParameterNode> globalItr = globalParameters.iterator();

		while (globalItr.hasNext()) {
			AbstractParameterNode global = globalItr.next();
			//List<AbstractParameterNode> linkedParameters = GlobalParameterNodeHelper.getLinkedParameters(global);

			boolean isDependent = false;
			if (!isDependent) {
				//remove mentioning constraints from the list to avoid duplicates
				accumulateAffectedConstraints(global, inOutAffectedNodes);

				inOutAffectedNodes.addOtherNode(global);
				//	globalItr.remove(); 
				/*
				 * in case linkers contain parameters assigned to removal -
				 * remove them from list; Global param removal will handle them.
				 */
				//				for (AbstractParameterNode param : linkedParameters) {
				//					selectedNodesByType.getLocalParameters().remove(param);
				//				}
			}
		}
	}

	private static void processTestCases(ArrayList<TestCaseNode> testCaseNodes, NodesByCathegory inOutAffectedNodes) {

		for (TestCaseNode testCaseNode : testCaseNodes) {
			inOutAffectedNodes.addTestCase(testCaseNode);
		}
	}

	private static void processChoicesFilteringConstraintsAndTestCases(
			ArrayList<ChoiceNode> choiceNodes, NodesByCathegory inOutAffectedNodes) {

		for (ChoiceNode choiceNode : choiceNodes) {

			accumulateAffectedConstraints(choiceNode, inOutAffectedNodes);

			accumulateAffectedTestCases(choiceNode, inOutAffectedNodes);

			inOutAffectedNodes.addOtherNode(choiceNode);
		}
	}

	private static void processClasses(ArrayList<ClassNode> classsNodes, NodesByCathegory outAffectedNodesByCathegory) {

		for (ClassNode classNode : classsNodes) {
			outAffectedNodesByCathegory.addOtherNode(classNode);
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

	private static void accumulateAffectedConstraints(
			IAbstractNode abstractNode, NodesByCathegory inOutAffectedNodes) {

		if (abstractNode instanceof ChoiceNode) {
			Set<ConstraintNode> mentioningConstraintNodes = 
					ChoiceNodeHelper.getMentioningConstraints((ChoiceNode) abstractNode);

			inOutAffectedNodes.addAllConstraints(mentioningConstraintNodes);
			return;
		} 

		if (abstractNode instanceof BasicParameterNode) {
			Set<ConstraintNode> mentioningConstraintNodes = 
					BasicParameterNodeHelper.getMentioningConstraints((BasicParameterNode) abstractNode);

			inOutAffectedNodes.addAllConstraints(mentioningConstraintNodes);
			return;
		}
	}

	private static void accumulateAffectedTestCases(
			ChoiceNode choiceNode, NodesByCathegory inOutAffectedNodes) {

		Set<TestCaseNode> mentioningTestCaseNodes = 
				ChoiceNodeHelper.getMentioningTestCases(choiceNode);

		inOutAffectedNodes.addAllTestCases(mentioningTestCaseNodes);
	}

	private static class NodesByCathegory {

		private Set<TestCaseNode> fTestCases;
		private Set<ConstraintNode> fConstraints;
		private Set<IAbstractNode> fOtherNodes;

		public NodesByCathegory() {
			fTestCases = new HashSet<>();
			fConstraints = new HashSet<>();
			fOtherNodes = new HashSet<>();
		}

		public void addConstraint(ConstraintNode constraint) {
			fConstraints.add(constraint);
		}

		public void addAllConstraints(Set<ConstraintNode> constraintNodes) {
			fConstraints.addAll(constraintNodes);
		}

		public void addAllTestCases(Set<TestCaseNode> testCaseNodes) {
			fTestCases.addAll(testCaseNodes);
		}

		public void addTestCase(TestCaseNode testCaseNode) {
			fTestCases.add(testCaseNode);
		}

		public void addOtherNode(IAbstractNode abstractNode) {
			fOtherNodes.add(abstractNode);
		}

		public Set<TestCaseNode> getTestCases() {
			return fTestCases;
		}

		public Set<ConstraintNode> getConstraints() {
			return fConstraints;
		}

		public Set<IAbstractNode> getOtherNodes() {
			return fOtherNodes;
		}
	}

}
