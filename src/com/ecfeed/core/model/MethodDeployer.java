/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SignatureHelper;

public abstract class MethodDeployer {

	public static String POSTFIX = "deployed";

	public static MethodNode deploy(MethodNode sourceMethodNode, NodeMapper mapper) {

		if (sourceMethodNode == null) {
			ExceptionHelper.reportRuntimeException("The source method is not defined.");
		}

		MethodNode targetMethodNode = new MethodNode(sourceMethodNode.getName() + "_" +  POSTFIX);

		deployParameters(sourceMethodNode, targetMethodNode, mapper);
		deployConstraints(sourceMethodNode, targetMethodNode, mapper);

		return targetMethodNode;
	}

	public static boolean deployedParametersDiffer(
			MethodNode methodNode,
			MethodNode deployedMethodNode) {

		List<BasicParameterNode> oldDeployedParameters = methodNode.getDeployedMethodParameters();

		List<AbstractParameterNode> newDeployedParameters = deployedMethodNode.getParameters();

		List<BasicParameterNode> convertedNewParameters = 
				BasicParameterNodeHelper.convertAbstractListToBasicList(newDeployedParameters);

		if (BasicParameterNodeHelper.propertiesOfBasicParametrsMatch(oldDeployedParameters, convertedNewParameters)) {
			return false;
		}

		return true;
	}

	public static void copyDeployedParameters(MethodNode deployedMethodNode, MethodNode methodNode) {

		List<BasicParameterNode> deployedParameters = deployedMethodNode.getParametersAsBasic();
		methodNode.setDeployedParameters(deployedParameters);
	}

	private static void deployParameters(MethodNode methodSource, MethodNode methodTarget, NodeMapper nodeMapper) {

		List<ParameterWithLinkingContext> nestedBasicParameters = 
				getNestedBasicParametersWithLinkingContexts(methodSource);

		nestedBasicParameters.stream().forEach(e -> deployBasicParameter(e, methodTarget, nodeMapper));
	}

	private static List<ParameterWithLinkingContext> getNestedBasicParametersWithLinkingContexts( // TODO MO-RE MOVE TO HELPER
			MethodNode methodSource) {
		
		List<ParameterWithLinkingContext> result = new ArrayList<>();
		
		List<AbstractParameterNode> parameters = methodSource.getParameters();
		
		for (AbstractParameterNode abstractParameterNode : parameters) {

			accumulateBasicParametersAndContextsRecursive(
					abstractParameterNode, 
					abstractParameterNode.getLinkToGlobalParameter(), 
					result);
		}
		
		return result;
	}

	private static void accumulateBasicParametersAndContextsRecursive(
			AbstractParameterNode currentAbstractParameterNode, 
			AbstractParameterNode linkingContext,
			List<ParameterWithLinkingContext> inOutResult) {
		
		if (currentAbstractParameterNode instanceof BasicParameterNode) {
			accumulateBasicParameter(currentAbstractParameterNode, linkingContext, inOutResult);
			return;
		}
		
		CompositeParameterNode currentCompositeParameterNode = 
				(CompositeParameterNode) currentAbstractParameterNode;
		
		AbstractParameterNode linkToGlobalParameter = currentAbstractParameterNode.getLinkToGlobalParameter();
		
		if (linkToGlobalParameter != null) {
			
			accumulateBasicParametersAndContextsRecursive(
					linkToGlobalParameter, currentCompositeParameterNode, inOutResult);
			
			return;
		}
		
		accumulateBasicParametersInChildComposites(linkingContext, inOutResult, currentCompositeParameterNode);
	}

	private static void accumulateBasicParametersInChildComposites(
			AbstractParameterNode currentParameterNode,
			List<ParameterWithLinkingContext> inOutResult, CompositeParameterNode currentCompositeParameterNode) {
		
		List<AbstractParameterNode> parameters = currentCompositeParameterNode.getParameters();
		
		
		for (AbstractParameterNode childAbstractParameterNode : parameters) {

			accumulateBasicParametersAndContextsRecursive(
					childAbstractParameterNode, currentParameterNode, inOutResult);
		}
	}

	private static void accumulateBasicParameter(
			AbstractParameterNode currentAbstractParameterNode,
			AbstractParameterNode linkingContext, 
			List<ParameterWithLinkingContext> inOutResult) {
		
		ParameterWithLinkingContext parameterWithLinkingContext = 
				new ParameterWithLinkingContext(currentAbstractParameterNode, linkingContext);
		
		inOutResult.add(parameterWithLinkingContext);
	}

	private static void deployBasicParameter(
			ParameterWithLinkingContext parameterWithLinkingContext,
			MethodNode targetMethodNode, 
			NodeMapper nodeMapper) {

		BasicParameterNode copy = parameterWithLinkingContext.getBasicParameter().createCopy(nodeMapper);

		copy.setCompositeName(copy.getName());

		targetMethodNode.addParameter(copy, parameterWithLinkingContext.getLinkingContext());
	}

	private static void deployConstraints(
			MethodNode sourceMethod, MethodNode targetMethod, NodeMapper mapper) {

		List<ConstraintNode> constraintNodes = sourceMethod.getConstraintNodes();
		constraintNodes.forEach(e -> targetMethod.addConstraint(e.createCopy(mapper)));

		String prefix = ""; 
		List<AbstractParameterNode> parameters = sourceMethod.getParameters();

		parameters.forEach(e -> deployConstraintsForCompositeParameterRecursively(e, targetMethod, prefix, mapper));
	}

	private static void deployConstraintsForCompositeParameterRecursively(
			AbstractParameterNode parameter, MethodNode targetMethod, String prefix, NodeMapper mapper) {

		if (parameter instanceof BasicParameterNode) {
			return;
		}

		String childPrefix = prefix + parameter.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR;

		CompositeParameterNode compositeParameterNode = (CompositeParameterNode) parameter;

		deployCurrentConstraintsOfCompositeParameter(compositeParameterNode, targetMethod, childPrefix, mapper);

		for (AbstractParameterNode abstractParameterNode : compositeParameterNode.getParameters()) {

			if (abstractParameterNode instanceof CompositeParameterNode) {
				deployConstraintsForCompositeParameterRecursively(abstractParameterNode, targetMethod, childPrefix, mapper);
			}
		}
	}

	private static void deployCurrentConstraintsOfCompositeParameter(
			CompositeParameterNode compositeParameterNode, MethodNode targetMethod, String prefix, NodeMapper nodeMapper) {

		List<ConstraintNode> constraintNodes = compositeParameterNode.getConstraintNodes();

		for (ConstraintNode constraintNode : constraintNodes) {

			ConstraintNode copyOfConstraintNode = constraintNode.createCopy(nodeMapper);
			copyOfConstraintNode.setName(prefix + constraintNode.getName());

			targetMethod.addConstraint(copyOfConstraintNode);
		}
	}

	public static List<TestCase> revertToOriginalTestCases(List<TestCase> deployedTestCases, NodeMapper mapper) {

		List<TestCase> result = new ArrayList<>();

		for (TestCase deployedTestCase : deployedTestCases) {

			TestCase revertedTestCaseNode = revertToOriginalTestCase(deployedTestCase, mapper);

			result.add(revertedTestCaseNode);
		}

		return result;
	}

	private static TestCase revertToOriginalTestCase(TestCase deployedTestCase, NodeMapper mapper) {

		List<ChoiceNode> revertedChoices = new ArrayList<>();

		List<ChoiceNode> deployedChoices = deployedTestCase.getListOfChoiceNodes();

		for (ChoiceNode deployedChoiceNode : deployedChoices) {

			ChoiceNode originalChoiceNode = mapper.getSourceNode(deployedChoiceNode);

			revertedChoices.add(originalChoiceNode);
		}

		return new TestCase(revertedChoices);
	}

}
