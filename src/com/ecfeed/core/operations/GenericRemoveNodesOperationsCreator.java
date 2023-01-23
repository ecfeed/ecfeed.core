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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.CompositeParameterNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNodeHelper;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesOperationsCreator {

	private final Set<IAbstractNode> fSelectedNodes;
	private NodesByType fAffectedNodesByType;
	private List<IModelOperation> fOperations;

	public GenericRemoveNodesOperationsCreator(
			Set<IAbstractNode> selectedNodes, 
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fSelectedNodes = selectedNodes;
		fAffectedNodesByType = new NodesByType();

		removeNodesWithAncestorsOnList(fSelectedNodes);

		fOperations = 
				createDeletingNodesOperations(
						fSelectedNodes,
						fAffectedNodesByType,

						extLanguageManager,
						typeAdapterProvider, 
						validate);
	}

	public List<IModelOperation> getOperations() {
		return fOperations;
	}

	public Set<ConstraintNode> getAffectedConstraints() {
		return fAffectedNodesByType.getConstraints();
	}

	public Set<TestCaseNode> getAffectedTestCases() {
		return fAffectedNodesByType.getTestCaseNodes();
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

	private static List<IModelOperation> createDeletingNodesOperations(
			Set<IAbstractNode> selectedNodes,
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate) {

		processNodes(
				selectedNodes, 
				outAffectedNodesByType,
				extLanguageManager, 
				validate);
		
		List<IModelOperation> resultOperations = 
				createOperationsFromAffectedNodes(
						outAffectedNodesByType, 
						extLanguageManager, typeAdapterProvider, validate);

		return resultOperations;
	}

	private static List<IModelOperation> createOperationsFromAffectedNodes(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate) {
		
		List<IModelOperation> result = new ArrayList<>();

		if (!outAffectedNodesByType.getConstraints().isEmpty()) {
			addOperationsForConstraints(outAffectedNodesByType, extLanguageManager, result);
		}

		if (!outAffectedNodesByType.getTestCaseNodes().isEmpty()) {
			addOperationsForTestCases(outAffectedNodesByType, extLanguageManager, result);
		}
		
		// TODO MO-RE choices
		
		if (!outAffectedNodesByType.getBasicParameters().isEmpty()) {
			addOperationsForBasicParameters(outAffectedNodesByType, validate, extLanguageManager, result);
		}
		
		if (!outAffectedNodesByType.getMethods().isEmpty()) {
			addOperationsForMethods(outAffectedNodesByType, extLanguageManager, result);
		}
		
		if (!outAffectedNodesByType.getClasses().isEmpty()) {
			addOperationsForClasses(outAffectedNodesByType, extLanguageManager, result);
		}
			
		return result;
		
//		List<IModelOperation> resultOperations = new ArrayList<>();
//
//		Set<ConstraintNode> affectedConstraints = outAffectedNodesByType.getConstraints();
//
//		if (!affectedConstraints.isEmpty()) {
//			addRemoveOperationsForAffectedConstraints(
//					affectedConstraints, resultOperations, 
//					extLanguageManager, typeAdapterProvider, validate);
//		}
//
//		Set<TestCaseNode> affectedTestCases = outAffectedNodesByType.getTestCases();
//
//		if (!affectedTestCases.isEmpty()) {
//			addRemoveOperationsForAffectedTestCases(
//					affectedTestCases, resultOperations, 
//					extLanguageManager, typeAdapterProvider, validate);
//		}
//
//		Set<IAbstractNode> affectedOtherNodes = outAffectedNodesByType.getOtherNodes();
//
//		if (!affectedOtherNodes.isEmpty()) {
//			addRemoveOperationsForAffectedNodes(
//					affectedOtherNodes, resultOperations, 
//					extLanguageManager, typeAdapterProvider, validate);
//		}

//		return resultOperations;
	}

	private static void addOperationsForTestCases(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {

		
		Set<TestCaseNode> testCaseNodes = outAffectedNodesByType.getTestCaseNodes();
		
		for (TestCaseNode testCaseNode : testCaseNodes) {
			
			MethodOperationRemoveTestCase operation = 
					new MethodOperationRemoveTestCase(testCaseNode.getMethod(), testCaseNode, extLanguageManager);
		
			result.add(operation);
		}
	}

	private static void addOperationsForConstraints(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {
		
		Set<ConstraintNode> constraintNodes = outAffectedNodesByType.getConstraints();
		
		for (ConstraintNode constraintNode : constraintNodes) {
			
			IAbstractNode abstractParent = constraintNode.getParent();
			
			// TODO MO-RE merge MethodOperationRemoveConstraint and CompositeParameterOperationRemoveConstraint into one operation 
			if (abstractParent instanceof MethodNode) {
			
				IModelOperation operation = 
						new MethodOperationRemoveConstraint(
								(MethodNode) abstractParent, constraintNode, extLanguageManager);
				result.add(operation);
				continue;
			}
			
			if (abstractParent instanceof CompositeParameterNode) {
				
				IModelOperation operation = 
					new CompositeParameterOperationRemoveConstraint(
						(CompositeParameterNode) abstractParent, constraintNode, extLanguageManager);
				
				result.add(operation);
				continue;
			}
			
			ExceptionHelper.reportRuntimeException("Invalid parent of constraint.");
		}
	}

	private static void addOperationsForBasicParameters(
			NodesByType outAffectedNodesByType,
			boolean validate,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {

		Set<BasicParameterNode> basicParameterNodes = outAffectedNodesByType.getBasicParameters();

		for (BasicParameterNode basicParameterNode : basicParameterNodes) {

			if (basicParameterNode.isGlobalParameter()) {

				IModelOperation operation = 
						new GenericOperationRemoveGlobalParameter(
								(IParametersParentNode)basicParameterNode.getParametersParent(), 
								basicParameterNode,
								extLanguageManager);

				result.add(operation);
				continue;

			} else {

				IModelOperation operation = 
						new RemoveBasicParameterOperation(
								(MethodNode)basicParameterNode.getParent(), basicParameterNode, validate, extLanguageManager);

				result.add(operation);
				continue;
			}
		}
	}

	private static void addOperationsForClasses(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {
		
		Set<ClassNode> classNodes = outAffectedNodesByType.getClasses();
		
		for (ClassNode classNode : classNodes) {
			
			RootOperationRemoveClass operation = 
					new RootOperationRemoveClass(classNode.getRoot(), classNode, extLanguageManager);
		
			result.add(operation);
		}
	}

	private static void addOperationsForMethods(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {
		
		Set<MethodNode> methodNodes = outAffectedNodesByType.getMethods();
		
		for (MethodNode methodNode : methodNodes) {
			
			ClassOperationRemoveMethod operation = 
					new ClassOperationRemoveMethod(methodNode.getClassNode(), methodNode, extLanguageManager);
		
			result.add(operation);
		}
	}

	private static void processNodes(
			Set<IAbstractNode> selectedNodes, 
			NodesByType outAffectedNodes,
			//List<IModelOperation> outOperations, 
			IExtLanguageManager extLanguageManager, boolean validate) {

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
			NodesByType outAffectedNodes) {

		Set<ClassNode> classNodes = selectedNodesByType.getClasses();

		if (!classNodes.isEmpty()) {
			processClasses(classNodes, outAffectedNodes);
		}

		Set<MethodNode> methods = selectedNodesByType.getMethods();

		if (!methods.isEmpty()) {
			processMethods(methods, outAffectedNodes);
		}
	}

	private static void processParametersAndChoices(NodesByType selectedNodesByType, NodesByType inOutAffectedNodes) {

		processParameters(selectedNodesByType, inOutAffectedNodes);

		processChoices(selectedNodesByType,	inOutAffectedNodes);
	}

	private static void processChoices(
			NodesByType selectedNodesByType, 
			NodesByType inOutAffectedNodes) {

		Set<ChoiceNode> choiceNodes = selectedNodesByType.getChoices();

		if (!choiceNodes.isEmpty()) {
			processChoicesFilteringConstraintsAndTestCases(
					choiceNodes, 
					inOutAffectedNodes);
		}
	}

	private static void processConstraintsAndTestCases(
			NodesByType selectedNodesByType,
			NodesByType inOutAffectedNodes) {

		Set<TestCaseNode> testCaseNodes = selectedNodesByType.getTestCaseNodes();

		if (!testCaseNodes.isEmpty()) {
			processTestCases(testCaseNodes, inOutAffectedNodes);
		}

		Set<ConstraintNode> constraints = selectedNodesByType.getConstraints();

		if (!constraints.isEmpty()) {
			processConstraints(constraints, inOutAffectedNodes);
		}
	}

	private static void processParameters(
			NodesByType selectedNodesByType, NodesByType inOutAffectedNodes) {

		//		Set<AbstractParameterNode> globalParameters = selectedNodesByType.getGlobalParameters();
		//
		//		if (!globalParameters.isEmpty()) {
		//			processGlobalParameters(selectedNodesByType, inOutAffectedNodes);
		//		}
		//
		//		Set<AbstractParameterNode> localParameters = selectedNodesByType.getLocalParameters();
		//
		//		if (!localParameters.isEmpty()) {
		//			processLocalParameters(selectedNodesByType, inOutAffectedNodes);
		//		}
		
				Set<BasicParameterNode> basicParameters = selectedNodesByType.getBasicParameters();
		
				if (!basicParameters.isEmpty()) {
					processBasicParameters(basicParameters, inOutAffectedNodes);
				}
		
				Set<CompositeParameterNode> compositeParameters = selectedNodesByType.getCompositeParameters();
		
				if (!compositeParameters.isEmpty()) {
					processCompositeParameters(compositeParameters, inOutAffectedNodes);
				}
		
	}

	private static void processConstraints(Set<ConstraintNode> constraintNodes, NodesByType inOutAffectedNodes) {

		for (ConstraintNode constraint : constraintNodes) {
			inOutAffectedNodes.addNode(constraint);
		}
	}

	private static void processMethods(Set<MethodNode> methods, NodesByType inOutAffectedNodes) {

		Iterator<MethodNode> methodItr = methods.iterator();

		while (methodItr.hasNext()) {
			MethodNode method = methodItr.next();
			inOutAffectedNodes.addNode(method);
		}
	}

	private static void processCompositeParameters(
			Set<CompositeParameterNode> compositeParameters, 
			NodesByType inOutAffectedNodes) {

		//processLocalBasicParameters(selectedNodesByType, inOutAffectedNodes);

		processLocalBasicChildrenOfCompositeParameters(compositeParameters,	inOutAffectedNodes);

		processLocalCompositeParameters(compositeParameters, inOutAffectedNodes);
	}

	private static void processLocalBasicParameters(
			NodesByType selectedNodesByType, NodesByType inOutAffectedNodes) {

		Set<BasicParameterNode> basicParameterNodes = selectedNodesByType.getBasicParameters();

		for (BasicParameterNode basicParameterNode : basicParameterNodes) {

			if (!basicParameterNode.isGlobalParameter()) {
				processBasicParameter(basicParameterNode, inOutAffectedNodes);
			}
		}
	}

	private static void processBasicParameter(
			BasicParameterNode abstractParameterNode,
			NodesByType inOutAffectedNodes) {

		IAbstractNode parent = abstractParameterNode.getParent();

		if ((parent instanceof MethodNode) || (parent instanceof CompositeParameterNode)) {

			accumulateAffectedConstraints(abstractParameterNode, inOutAffectedNodes);
			inOutAffectedNodes.addNode(abstractParameterNode);
		}
	}

	private static void processLocalBasicChildrenOfCompositeParameters(
			Set<CompositeParameterNode> compositeParameters,
			NodesByType inOutAffectedNodes) {

		for (CompositeParameterNode compositeParameterNode : compositeParameters) {
			processAllChildBasicParametersOfCompositeParameter(compositeParameterNode, inOutAffectedNodes);
		}
	}

	private static void processAllChildBasicParametersOfCompositeParameter(
			CompositeParameterNode compositeParameterNode,
			NodesByType inOutAffectedNodes) {

		List<BasicParameterNode> basicParameterNodes = 
				CompositeParameterNodeHelper.getAllChildBasicParameters(compositeParameterNode);

		for (BasicParameterNode basicParameterNode : basicParameterNodes) {
			inOutAffectedNodes.addNode(basicParameterNode);
		}
	}

	private static void processLocalCompositeParameters(
			Set<CompositeParameterNode> compositeParameters,
			NodesByType inOutAffectedNodes) {

		for (CompositeParameterNode compositeParameterNode : compositeParameters) {

			if (!compositeParameterNode.isGlobalParameter()) {
				inOutAffectedNodes.addNode(compositeParameterNode);
			}
		}
	}

	private static void processBasicParameters(
			Set<BasicParameterNode> basicParameters,
			NodesByType inOutAffectedNodes) {

		//		/*
		//		 * Iterate through global params. Do the same checks as for method
		//		 * parameters with every linker. If no linker is in potentially
		//		 * duplicate method - just proceed to remove global and all linkers and
		//		 * remove it from the lists.
		//		 */
		//
		//		Set<AbstractParameterNode> globalParameters = selectedNodesByType.getGlobalParameters();
		//
		//		Iterator<AbstractParameterNode> globalItr = globalParameters.iterator();
		//
		//		while (globalItr.hasNext()) {
		//			AbstractParameterNode global = globalItr.next();
		//			//List<AbstractParameterNode> linkedParameters = GlobalParameterNodeHelper.getLinkedParameters(global);
		//
		//			boolean isDependent = false;
		//			if (!isDependent) {
		//				//remove mentioning constraints from the list to avoid duplicates
		//				accumulateAffectedConstraints(global, inOutAffectedNodes);
		//
		//				inOutAffectedNodes.addOtherNode(global);
		//			}
		//		}
		
		for (BasicParameterNode basicParameterNode : basicParameters) {
			
			accumulateAffectedConstraints(basicParameterNode, inOutAffectedNodes);
			accumulateAffectedTestCases(basicParameterNode, inOutAffectedNodes);
			
			inOutAffectedNodes.addNode(basicParameterNode);
		}
	}

	private static void accumulateAffectedTestCases(
			BasicParameterNode basicParameterNode,
			NodesByType inOutAffectedNodes) {
		
		if (basicParameterNode.isGlobalParameter()) {
			
			List<AbstractParameterNode> linkedParameters = 
					GlobalParameterNodeHelper.getLinkedParameters(basicParameterNode);
			
			for (AbstractParameterNode abstractParameterNode : linkedParameters) {
				
				MethodNode methodNode = MethodNodeHelper.findMethodNode(abstractParameterNode);
				
				inOutAffectedNodes.addTestCases(methodNode.getTestCases());
			}
		} else {
			
			MethodNode methodNode = MethodNodeHelper.findMethodNode(basicParameterNode);
			inOutAffectedNodes.addTestCases(methodNode.getTestCases());
			
		}
	}

	private static void processTestCases(Set<TestCaseNode> testCaseNodes, NodesByType inOutAffectedNodes) {

		for (TestCaseNode testCaseNode : testCaseNodes) {
			inOutAffectedNodes.addNode(testCaseNode);
		}
	}

	private static void processChoicesFilteringConstraintsAndTestCases(
			Set<ChoiceNode> choiceNodes, 
			NodesByType inOutAffectedNodes) {

		for (ChoiceNode choiceNode : choiceNodes) {

			accumulateAffectedConstraints(choiceNode, inOutAffectedNodes);

			accumulateAffectedTestCases(choiceNode, inOutAffectedNodes);

			inOutAffectedNodes.addNode(choiceNode);
		}
	}

	private static void processClasses(Set<ClassNode> classsNodes, NodesByType outAffectedNodesByType) {

		for (ClassNode classNode : classsNodes) {
			outAffectedNodesByType.addNode(classNode);
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
			IAbstractNode abstractNode, NodesByType inOutAffectedNodes) {

		if (abstractNode instanceof ChoiceNode) {
			Set<ConstraintNode> mentioningConstraintNodes = 
					ChoiceNodeHelper.getMentioningConstraints((ChoiceNode) abstractNode);

			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
			return;
		} 

		if (abstractNode instanceof BasicParameterNode) {
			Set<ConstraintNode> mentioningConstraintNodes = 
					BasicParameterNodeHelper.getMentioningConstraints((BasicParameterNode) abstractNode);

			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
			return;
		}
	}

	private static void accumulateAffectedTestCases(
			ChoiceNode choiceNode, NodesByType inOutAffectedNodes) {

		Set<TestCaseNode> mentioningTestCaseNodes = 
				ChoiceNodeHelper.getMentioningTestCases(choiceNode);

		inOutAffectedNodes.addTestCases(mentioningTestCaseNodes);
	}

	//	private static class NodesByCathegory {
	//
	//		private Set<TestCaseNode> fTestCases;
	//		private Set<ConstraintNode> fConstraints;
	//		private Set<IAbstractNode> fOtherNodes;
	//
	//		public NodesByCathegory() {
	//			fTestCases = new HashSet<>();
	//			fConstraints = new HashSet<>();
	//			fOtherNodes = new HashSet<>();
	//		}
	//
	//		public void addConstraint(ConstraintNode constraint) {
	//			fConstraints.add(constraint);
	//		}
	//
	//		public void addAllConstraints(Collection<ConstraintNode> constraintNodes) {
	//			fConstraints.addAll(constraintNodes);
	//		}
	//
	//		public void addAllTestCases(Collection<TestCaseNode> testCaseNodes) {
	//			fTestCases.addAll(testCaseNodes);
	//		}
	//
	//		public void addTestCase(TestCaseNode testCaseNode) {
	//			fTestCases.add(testCaseNode);
	//		}
	//
	//		public void addOtherNode(IAbstractNode abstractNode) {
	//			fOtherNodes.add(abstractNode);
	//		}
	//
	//		public void addBasicParameters(List<BasicParameterNode> abstractNode) {
	//			fOtherNodes.addAll(abstractNode);
	//		}
	//
	//		public Set<TestCaseNode> getTestCases() {
	//			return fTestCases;
	//		}
	//
	//		public Set<ConstraintNode> getConstraints() {
	//			return fConstraints;
	//		}
	//
	//		public Set<IAbstractNode> getOtherNodes() {
	//			return fOtherNodes;
	//		}
	//	}

}
