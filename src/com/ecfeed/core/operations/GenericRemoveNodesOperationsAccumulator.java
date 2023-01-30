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
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.operations.nodes.OnBasicParameterOperationRemove;
import com.ecfeed.core.operations.nodes.OnClassOperationRemove;
import com.ecfeed.core.operations.nodes.OnConstraintOperationRemove;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveFromClass;
import com.ecfeed.core.operations.nodes.OnParameterOperationRemoveFromComposite;
import com.ecfeed.core.operations.nodes.OnTestCaseOperationRemove;
import com.ecfeed.core.operations.nodes.OnTestSuiteOperationRemoveFromMethod;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesOperationsAccumulator {

	public static List<IModelOperation> convertNodesToOperations(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate) {

		List<IModelOperation> result = new ArrayList<>();

		if (!outAffectedNodesByType.getConstraints().isEmpty()) {
			addOperationsForConstraints(outAffectedNodesByType, extLanguageManager, result);
		}

		if (!outAffectedNodesByType.getTestSuiteNodes().isEmpty()) {
			addOperationsForTestSuites(outAffectedNodesByType, extLanguageManager, result);
		}
		
		if (!outAffectedNodesByType.getTestCaseNodes().isEmpty()) {
			addOperationsForTestCases(outAffectedNodesByType, extLanguageManager, result);
		}

		if (!outAffectedNodesByType.getChoices().isEmpty()) {
			addOperationsForChoices(outAffectedNodesByType, validate, extLanguageManager, result);
		}

		if (!outAffectedNodesByType.getBasicParameters().isEmpty()) {
			addOperationsForBasicParameters(outAffectedNodesByType, validate, extLanguageManager, result);
		}

		if (!outAffectedNodesByType.getCompositeParameters().isEmpty()) {
			addOperationsForCompositeParameters(outAffectedNodesByType, validate, extLanguageManager, result);
		}

		if (!outAffectedNodesByType.getMethods().isEmpty()) {
			addOperationsForMethods(outAffectedNodesByType, extLanguageManager, result);
		}

		if (!outAffectedNodesByType.getClasses().isEmpty()) {
			addOperationsForClasses(outAffectedNodesByType, extLanguageManager, result);
		}

		return result;
	}

	private static void addOperationsForTestSuites(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {

		Set<TestSuiteNode> testSuiteNodes = outAffectedNodesByType.getTestSuiteNodes();

		for (TestSuiteNode testSuiteNode : testSuiteNodes) {
			
			OnTestSuiteOperationRemoveFromMethod operation =
					new OnTestSuiteOperationRemoveFromMethod(
					testSuiteNode.getMethod(), testSuiteNode, extLanguageManager);

			result.add(operation);
		}
	}

	
	private static void addOperationsForTestCases(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {


		Set<TestCaseNode> testCaseNodes = outAffectedNodesByType.getTestCaseNodes();

		for (TestCaseNode testCaseNode : testCaseNodes) {

			OnTestCaseOperationRemove operation = 
					new OnTestCaseOperationRemove(testCaseNode.getMethod(), testCaseNode, extLanguageManager);

			result.add(operation);
		}
	}

	private static void addOperationsForChoices(
			NodesByType outAffectedNodesByType,
			boolean validate,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {

		Set<ChoiceNode> testCaseNodes = outAffectedNodesByType.getChoices();

		for (ChoiceNode choiceNode : testCaseNodes) {

			IAbstractNode abstractParent = choiceNode.getParent();

			if (!(abstractParent instanceof IChoicesParentNode)) {
				ExceptionHelper.reportRuntimeException("Invalid type of choice parent.");
			}

			IChoicesParentNode choicesParentNode = (IChoicesParentNode)abstractParent; 

			IModelOperation modelOperation = 
					new GenericOperationRemoveChoice(
							choicesParentNode, choiceNode, new TypeAdapterProviderForJava(), validate, extLanguageManager);

			result.add(modelOperation);
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
						//new MethodOperationRemoveConstraint(
						//		(MethodNode) abstractParent, constraintNode, extLanguageManager);
						new OnConstraintOperationRemove(
								(MethodNode)abstractParent, constraintNode, extLanguageManager);
						
				result.add(operation);
				continue;
			}

			if (abstractParent instanceof CompositeParameterNode) {

				IModelOperation operation = 
						//new CompositeParameterOperationRemoveConstraint(
						//		(CompositeParameterNode) abstractParent, constraintNode, extLanguageManager);
						
						new OnConstraintOperationRemove(
								(CompositeParameterNode)abstractParent, constraintNode, extLanguageManager);

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

			IAbstractNode parent = basicParameterNode.getParent();

			if (basicParameterNode.isGlobalParameter()) {

				IModelOperation operation = 
						new GenericOperationRemoveGlobalParameter(
								(IParametersParentNode)basicParameterNode.getParametersParent(), 
								basicParameterNode,
								extLanguageManager);

				result.add(operation);
				continue;

			} 

			// TODO MO-RE merge operations ? (regardless of parent)

			if (parent instanceof MethodNode) {

				IModelOperation operation = 
						new OnBasicParameterOperationRemove(
								(MethodNode)parent, basicParameterNode, validate, extLanguageManager);

				result.add(operation);
				continue;
			}

			if (parent instanceof CompositeParameterNode) {

				IModelOperation operation = 
						new OnParameterOperationRemoveFromComposite(
								(CompositeParameterNode)parent, basicParameterNode, extLanguageManager);
				result.add(operation);
				continue;
			}
		}
	}

	private static void addOperationsForCompositeParameters(
			NodesByType outAffectedNodesByType,
			boolean validate,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {

		Set<CompositeParameterNode> basicParameterNodes = outAffectedNodesByType.getCompositeParameters();

		for (CompositeParameterNode basicParameterNode : basicParameterNodes) {

			IAbstractNode parent = basicParameterNode.getParent();

			if (parent instanceof MethodNode) {

				IModelOperation modelOperation = 
						new OnBasicParameterOperationRemove(
								(MethodNode)parent, basicParameterNode, validate, extLanguageManager);

				result.add(modelOperation);
				continue;
			} 

			if (parent instanceof CompositeParameterNode) {

				IModelOperation modelOperation = 
						new OnParameterOperationRemoveFromComposite(
								(CompositeParameterNode)parent, basicParameterNode, extLanguageManager);

				result.add(modelOperation);
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

			OnClassOperationRemove operation = 
					new OnClassOperationRemove(classNode.getRoot(), classNode, extLanguageManager);

			result.add(operation);
		}
	}

	private static void addOperationsForMethods(
			NodesByType outAffectedNodesByType,
			IExtLanguageManager extLanguageManager, 
			List<IModelOperation> result) {

		Set<MethodNode> methodNodes = outAffectedNodesByType.getMethods();

		for (MethodNode methodNode : methodNodes) {

			OnMethodOperationRemoveFromClass operation = 
					new OnMethodOperationRemoveFromClass(methodNode.getClassNode(), methodNode, extLanguageManager);

			result.add(operation);
		}
	}

}
