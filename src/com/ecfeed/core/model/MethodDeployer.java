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

	public static MethodNode deploy(MethodNode sourceMethodNode, NodeMapper nodeMapper) {

		if (sourceMethodNode == null) {
			ExceptionHelper.reportRuntimeException("The source method is not defined.");
		}

		MethodNode targetMethodNode = new MethodNode(sourceMethodNode.getName() + "_" +  POSTFIX);

		deployParameters(sourceMethodNode, targetMethodNode, nodeMapper);
		deployConstraints(sourceMethodNode, targetMethodNode, nodeMapper);

		return targetMethodNode;
	}

	public static boolean isMatchFoDeployedParameters(
			MethodNode methodNode,
			MethodNode deployedMethodNode) {

		List<ParameterWithLinkingContext> oldDeployedParameters = 
				methodNode.getDeployedParametersWithLinkingContexs();

		List<ParameterWithLinkingContext> newDeployedParameters = 
				deployedMethodNode.getParametersWithLinkingContexts();

		if (BasicParameterNodeHelper.propertiesOfBasicParametrsMatch(
				oldDeployedParameters, newDeployedParameters)) {
			return true;
		}

		return false;
	}

	public static void copyDeployedParametersWithConversionToOriginals(
			MethodNode deployedMethodNode, 
			MethodNode methodNode, 
			NodeMapper nodeMapper) {

		List<ParameterWithLinkingContext> deployedParametersWithContexts = deployedMethodNode.getParametersWithLinkingContexts();

		List<ParameterWithLinkingContext> originalParametersWithContexts =
				convertDeployedParametersWithContextsToOriginals(deployedParametersWithContexts, nodeMapper);

		methodNode.setDeployedParametersWithContexts(originalParametersWithContexts);

		//		for (ParameterWithLinkingContext parameterWithLinkingContext : deployedParametersWithContexts) {
		//			System.out.println(ParameterWithLinkingContextHelper.createSignature(parameterWithLinkingContext));
		//		}

		//		List<ParameterWithLinkingContext> originalParametersWithContexts =
		//				convertDeployedParametersWithContextsToOriginal(deployedParametersWithContexts, nodeMapper);
		//		
		//		methodNode.setOriginalParametersWithContexts(originalParametersWithContexts);
	}

	private static List<ParameterWithLinkingContext> convertDeployedParametersWithContextsToOriginals(
			List<ParameterWithLinkingContext> deployedParametersWithContexts,
			NodeMapper nodeMapper) {

		List<ParameterWithLinkingContext> result = new ArrayList<>();

		for (ParameterWithLinkingContext deployedParameterWithContext : deployedParametersWithContexts) {

			AbstractParameterNode deployedParameter = deployedParameterWithContext.getParameter();
			AbstractParameterNode deployedLinkingContext = deployedParameterWithContext.getLinkingContext();

			AbstractParameterNode originalParameter = nodeMapper.getSourceNode(deployedParameter);
			AbstractParameterNode originalLinkingContext = nodeMapper.getSourceNode(deployedLinkingContext);

			ParameterWithLinkingContext original = 
					new ParameterWithLinkingContext(originalParameter, originalLinkingContext);

			result.add(original);
		}

		return result;
	}

	//	private static List<ParameterWithLinkingContext> convertDeployedParametersWithContextsToOriginal(
	//			List<ParameterWithLinkingContext> deployedParametersWithContexts,
	//			NodeMapper nodeMapper) {
	//		
	//		List<ParameterWithLinkingContext> result = new ArrayList<>();
	//		
	//		for (ParameterWithLinkingContext deployed : deployedParametersWithContexts) {
	//			
	//			System.out.println(ParameterWithLinkingContextHelper.createSignature(deployed));
	//			
	//			BasicParameterNode deployedBasicParameterNode = deployed.getParameterAsBasic();
	//			BasicParameterNode originalBasicParameterNode = nodeMapper.getSourceNode(deployedBasicParameterNode);
	//			
	//			CompositeParameterNode deployedCompositeParameterNode = 
	//					deployed.getLinkingContextAsCompositeParameter();
	//			
	//			CompositeParameterNode originalCompositeParameterNode =
	//					nodeMapper.getSourceNode(deployedCompositeParameterNode);
	//			
	//			ParameterWithLinkingContext original = 
	//					new ParameterWithLinkingContext(originalBasicParameterNode, originalCompositeParameterNode);
	//			
	//			System.out.println(ParameterWithLinkingContextHelper.createSignature(original));
	//			
	//			result.add(original);
	//		}
	//		
	//		return result;
	//	}

	private static void deployParameters(MethodNode methodSource, MethodNode methodTarget, NodeMapper nodeMapper) {

		List<ParameterWithLinkingContext> nestedBasicParameters = 
				MethodNodeHelper.getNestedBasicParametersWithLinkingContexts(methodSource);

		nestedBasicParameters.stream().forEach(e -> deployBasicParameter(e, methodTarget, nodeMapper));
	}

	private static void deployBasicParameter(
			ParameterWithLinkingContext parameterWithLinkingContext,
			MethodNode targetMethodNode, 
			NodeMapper nodeMapper) {

		// deployBasicParameterOldVersion(parameterWithLinkingContext, targetMethodNode, nodeMapper);
		deployBasicParameterNewVersion(parameterWithLinkingContext, targetMethodNode, nodeMapper);
	}

	//	private static void deployBasicParameterOldVersion(
	//			ParameterWithLinkingContext parameterWithLinkingContext,
	//			MethodNode targetMethodNode, NodeMapper nodeMapper) {
	//		
	//		BasicParameterNode basicParameterNode = parameterWithLinkingContext.getParameterAsBasic();
	//
	//		BasicParameterNode copy = basicParameterNode.createCopyForDeployment(nodeMapper);
	//
	//		AbstractParameterNode linkingContext = parameterWithLinkingContext.getLinkingContext();
	//		
	//		targetMethodNode.addParameter(copy, linkingContext);
	//	}

	private static void deployBasicParameterNewVersion(
			ParameterWithLinkingContext parameterWithLinkingContext,
			MethodNode deployedMethodNode, 
			NodeMapper nodeMapper) {

		BasicParameterNode sourceParameter = parameterWithLinkingContext.getParameterAsBasic();
		AbstractParameterNode sourceLinkingContext =  parameterWithLinkingContext.getLinkingContext();

		if (sourceLinkingContext == null) {

			BasicParameterNode deployedParameter = sourceParameter.createCopyForDeployment(nodeMapper);
			deployedParameter.setParent(deployedMethodNode);

			deployedMethodNode.addParameter(deployedParameter, null);
			return;
		}

		if (sourceLinkingContext instanceof CompositeParameterNode) {

			BasicParameterNode deployedParameter = sourceParameter.createCopyForDeployment(nodeMapper);
			deployedParameter.setParent(deployedMethodNode);

			deployedMethodNode.addParameter(deployedParameter, sourceLinkingContext);
			return;
		}

		if (sourceLinkingContext instanceof BasicParameterNode) {

			BasicParameterNode deployedParameter = 
					((BasicParameterNode)sourceLinkingContext).createCopyForDeployment(nodeMapper);
			deployedParameter.setParent(deployedMethodNode);

			deployedMethodNode.addParameter(deployedParameter, null);
			return;
		}

		ExceptionHelper.reportRuntimeException("Invalid configuration of parameter with context.");
	}	

	private static void deployConstraints(
			MethodNode sourceMethod, MethodNode targetMethod, NodeMapper nodeMapper) {

		List<ConstraintNode> constraintNodes = sourceMethod.getConstraintNodes();
		constraintNodes.forEach(e -> targetMethod.addConstraint(e.createCopy(nodeMapper)));

		String prefix = ""; 
		List<AbstractParameterNode> parameters = sourceMethod.getParameters();

		parameters.forEach(e -> deployConstraintsForCompositeParameterRecursively(e, targetMethod, prefix, nodeMapper));
	}

	private static void deployConstraintsForCompositeParameterRecursively(
			AbstractParameterNode parameter, MethodNode targetMethod, String prefix, NodeMapper nodeMapper) {

		if (parameter instanceof BasicParameterNode) {
			return;
		}

		String childPrefix = prefix + parameter.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR;

		CompositeParameterNode compositeParameterNode = (CompositeParameterNode) parameter;

		deployCurrentConstraintsOfCompositeParameter(compositeParameterNode, targetMethod, childPrefix, nodeMapper);

		for (AbstractParameterNode abstractParameterNode : compositeParameterNode.getParameters()) {

			if (abstractParameterNode instanceof CompositeParameterNode) {
				deployConstraintsForCompositeParameterRecursively(abstractParameterNode, targetMethod, childPrefix, nodeMapper);
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

	public static List<TestCase> revertToOriginalTestCases(List<TestCase> deployedTestCases, NodeMapper nodeMapper) {

		List<TestCase> result = new ArrayList<>();

		for (TestCase deployedTestCase : deployedTestCases) {

			TestCase revertedTestCaseNode = revertToOriginalTestCase(deployedTestCase, nodeMapper);

			result.add(revertedTestCaseNode);
		}

		return result;
	}

	private static TestCase revertToOriginalTestCase(TestCase deployedTestCase, NodeMapper nodeMapper) {

		List<ChoiceNode> revertedChoices = new ArrayList<>();

		List<ChoiceNode> deployedChoices = deployedTestCase.getListOfChoiceNodes();

		for (ChoiceNode deployedChoiceNode : deployedChoices) {

			ChoiceNode originalChoiceNode = nodeMapper.getSourceNode(deployedChoiceNode);

			revertedChoices.add(originalChoiceNode);
		}

		return new TestCase(revertedChoices);
	}

	public static String createSignatureOfOriginalNodes(
			ParameterWithLinkingContext deployedParameterWithLinkingContext,
			NodeMapper nodeMapper) {

		AbstractParameterNode parameter = nodeMapper.getSourceNode(deployedParameterWithLinkingContext.getParameter());
		AbstractParameterNode linkOfParameter = parameter.getLinkToGlobalParameter();
		AbstractParameterNode context = nodeMapper.getSourceNode(deployedParameterWithLinkingContext.getLinkingContext());

		if (context == null && linkOfParameter != null) {

			String signature = 
					AbstractParameterSignatureHelper.createSignatureOfParameterWithContext(
							linkOfParameter, parameter);

			return signature;
		}

		String signature = AbstractParameterSignatureHelper.createSignatureOfParameterWithContext(parameter, context);
		return signature;
	}

}
