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

			Set<ConstraintNode> outAffectedConstraints,
			Set<TestCaseNode> outAffectedTestCases,
			List<IModelOperation> outOperations,

			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate){

		Set<IAbstractNode> affectedNodes = new HashSet<>();

		processNodes(
				selectedNodes, 
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
			Set<IAbstractNode> outAffectedNodes, 
			Set<ConstraintNode> outAffectedConstraints, 
			Set<TestCaseNode> outAffectedTestCases,
			List<IModelOperation> outOperations, IExtLanguageManager extLanguageManager, boolean validate) {

		NodesByType selectedNodesByType = new NodesByType(selectedNodes);

		processClassesAndMethods(selectedNodesByType, outAffectedNodes);

		processParametersAndChoices(
				selectedNodesByType, 
				outAffectedNodes, outAffectedConstraints, outAffectedTestCases);

		processConstraintsAndTestCases(selectedNodesByType, outAffectedNodes);
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
			Set<IAbstractNode> outAffectedNodes, 
			Set<ConstraintNode> outAffectedConstraints,
			Set<TestCaseNode> outAffectedTestCases) {

		processParameters(
				selectedNodesByType, 
				outAffectedConstraints, 
				outAffectedNodes);

		processChoices(
				selectedNodesByType, 
				outAffectedNodes,
				outAffectedConstraints, 
				outAffectedTestCases);
	}

	private static void processChoices(
			NodesByType selectedNodesByType, 
			Set<IAbstractNode> outAffectedNodes,
			Set<ConstraintNode> outAffectedConstraints, 
			Set<TestCaseNode> outAffectedTestCases) {

		ArrayList<ChoiceNode> choiceNodes = selectedNodesByType.getChoices();

		if (!choiceNodes.isEmpty()) {
			processChoicesFilteringConstraintsAndTestCases(
					choiceNodes, 
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

	private static void processParameters(
			NodesByType selectedNodesByType, 
			Set<ConstraintNode> inOutAffectedConstraints,
			Set<IAbstractNode> inOutAffectedNodes) {

		ArrayList<AbstractParameterNode> globalParameters = selectedNodesByType.getGlobalParameters();

		if (!globalParameters.isEmpty()) {
			processGlobalParameters(
					selectedNodesByType, 
					inOutAffectedConstraints, 
					inOutAffectedNodes);
		}

		ArrayList<AbstractParameterNode> localParameters = selectedNodesByType.getLocalParameters();

		if (!localParameters.isEmpty()) {
			processLocalParameters(
					selectedNodesByType, 
					inOutAffectedConstraints, inOutAffectedNodes);
		}
	}

	private static void processConstraints(ArrayList<ConstraintNode> constraintNodes, Set<IAbstractNode> affectedNodes) {

		for (ConstraintNode constraint : constraintNodes) {
			affectedNodes.add(constraint);
		}
	}

	private static void processMethods(ArrayList<MethodNode> methods, Set<IAbstractNode> affectedNodes) {

		Iterator<MethodNode> methodItr = methods.iterator();

		while (methodItr.hasNext()) {
			MethodNode method = methodItr.next();
			affectedNodes.add(method);
		}
	}

	private static void processLocalParameters(
			NodesByType selectedNodesByType, 
			Set<ConstraintNode> outAffectedConstraints,
			Set<IAbstractNode> outAffectedNodes) {

		processLocalBasicParameters(
				selectedNodesByType, 
				outAffectedConstraints, outAffectedNodes);

		processLocalBasicChildrenOfCompositeParameters(
				selectedNodesByType, 
				outAffectedConstraints, outAffectedNodes);

		processLocalCompositeParameters(selectedNodesByType, outAffectedNodes);
	}

	private static void processLocalBasicParameters(
			NodesByType selectedNodesByType,
			Set<ConstraintNode> outAffectedConstraints,
			Set<IAbstractNode> outAffectedNodes) {

		Iterator<AbstractParameterNode> paramItr = selectedNodesByType.getLocalParameters().iterator();

		while (paramItr.hasNext()) {

			AbstractParameterNode param = paramItr.next();

			if (!(param instanceof BasicParameterNode)) {
				continue;
			}

			processBasicParameter(
					param, 
					outAffectedConstraints, 
					outAffectedNodes);
		}
	}

	private static void processBasicParameter(
			AbstractParameterNode param,
			Set<ConstraintNode> outAffectedConstraints,
			Set<IAbstractNode> outAffectedNodes) {
		IAbstractNode parent = param.getParent();

		if ((parent instanceof MethodNode) || (parent instanceof CompositeParameterNode)) {

			accumulateAffectedConstraints(param, outAffectedConstraints);
			outAffectedNodes.add(param);
		}
	}

	private static void processLocalBasicChildrenOfCompositeParameters(
			NodesByType selectedNodesByType,
			Set<ConstraintNode> outAffectedConstraints,
			Set<IAbstractNode> affectedNodes) {

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
			Set<IAbstractNode> affectedNodes) {

		Iterator<AbstractParameterNode> paramItr = selectedNodesByType.getLocalParameters().iterator();

		while (paramItr.hasNext()) {
			AbstractParameterNode param = paramItr.next();

			if (param instanceof CompositeParameterNode)
				affectedNodes.add(param);
		}
	}

	private static void processGlobalParameters(NodesByType selectedNodesByType,
			Set<ConstraintNode> outAffectedConstraints,
			Set<IAbstractNode> inOutAffectedNodes) {

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
				accumulateAffectedConstraints(
						global, 
						//allConstraintNodes, 
						outAffectedConstraints);
				inOutAffectedNodes.add(global);
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

	private static void processTestCases(ArrayList<TestCaseNode> testCaseNodes, Set<IAbstractNode> affectedNodes) {

		for (TestCaseNode testCaseNode : testCaseNodes) {
			affectedNodes.add(testCaseNode);
		}
	}

	private static void processChoicesFilteringConstraintsAndTestCases(
			ArrayList<ChoiceNode> choiceNodes, 
			Set<ConstraintNode> outAffectedConstraints, Set<TestCaseNode> outAffectedTestCases, 
			Set<IAbstractNode> affectedNodes) {

		for (ChoiceNode choiceNode : choiceNodes) {

			accumulateAffectedConstraints(choiceNode, outAffectedConstraints);

			accumulateAffectedTestCases(
					choiceNode, outAffectedTestCases);

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

	private static void accumulateAffectedConstraints(
			IAbstractNode abstractNode, Set<ConstraintNode> outAffectedConstraints) {

		if (abstractNode instanceof ChoiceNode) {
			Set<ConstraintNode> mentioningConstraintNodes = 
					ChoiceNodeHelper.getMentioningConstraints((ChoiceNode) abstractNode);

			outAffectedConstraints.addAll(mentioningConstraintNodes);
			return;
		} 

		if (abstractNode instanceof BasicParameterNode) {
			Set<ConstraintNode> mentioningConstraintNodes = 
					BasicParameterNodeHelper.getMentioningConstraints((BasicParameterNode) abstractNode);

			outAffectedConstraints.addAll(mentioningConstraintNodes);
			return;
		}
	}

	private static void accumulateAffectedTestCases(
			ChoiceNode choiceNode, Set<TestCaseNode> outAffectedTestCases) {

		Set<TestCaseNode> mentioningConstraintNodes = 
				ChoiceNodeHelper.getMentioningTestCases(choiceNode);

		outAffectedTestCases.addAll(mentioningConstraintNodes);
	}

}
