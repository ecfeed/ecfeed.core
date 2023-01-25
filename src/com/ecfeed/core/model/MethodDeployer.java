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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SignatureHelper;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;

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
		methodNode.setDeployedParameters(deployedParameters); // TODO MO-RE add by operation
	}

	public static List<TestCase> revertToOriginalChoices(NodeMapper mapper, List<TestCase> deployedTestCases) { // TODO MO-RE mapper as last parameter

		List<TestCase> result = new ArrayList<>();

		for (TestCase deployedTestCase : deployedTestCases) {

			TestCase revertedTestCaseNode = revertToOriginalTestCase(mapper, deployedTestCase);

			result.add(revertedTestCaseNode);
		}

		return result;
	}

	private static void deployParameters(MethodNode methodSource, MethodNode methodTarget, NodeMapper mapper) {

		String prefix = "";

		deployParametersRecursively(methodSource.getParameters(), methodTarget, prefix, mapper);
	}

	private static void deployParametersRecursively(
			List<AbstractParameterNode> sourceParameters, MethodNode targetMethodNode, String prefix, NodeMapper mapper) {

		for (AbstractParameterNode sourceParameter : sourceParameters) {
			if (sourceParameter instanceof BasicParameterNode) {
				handleBasicParameter((BasicParameterNode) sourceParameter, targetMethodNode, prefix, mapper);
			} else	if (sourceParameter instanceof CompositeParameterNode) {
				handleCompositeParameter((CompositeParameterNode) sourceParameter, targetMethodNode, prefix, mapper);
			}
		}
	}

	private static void handleBasicParameter(
			BasicParameterNode sourceParameter, MethodNode targetMethodNode, String prefix, NodeMapper mapper) {

		deployBasicParameter(sourceParameter, targetMethodNode, prefix, mapper);
	}

	private static void handleCompositeParameter(
			CompositeParameterNode sourceParameter, MethodNode targetMethodNode, String prefix, NodeMapper mapper) {

		List<AbstractParameterNode> childSourceParameters = new ArrayList<>();

		if (sourceParameter.isLinked()) {
			handleCompositeParameterLinked(sourceParameter, childSourceParameters);
		} else {
			handleCompositeParameterNotLinked(sourceParameter, childSourceParameters);
		}

		String childPrefix = prefix + sourceParameter.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR;

		deployParametersRecursively(childSourceParameters, targetMethodNode, childPrefix, mapper);
	}

	private static void handleCompositeParameterLinked(
			CompositeParameterNode sourceParameter, List<AbstractParameterNode> childSourceParameters) {

		CompositeParameterNode sourceParameterGlobal = (CompositeParameterNode) sourceParameter.getLinkToGlobalParameter();

		for (AbstractParameterNode node : sourceParameterGlobal.getParameters()) {
			if (node instanceof CompositeParameterNode) {
				handleCompositeParameterLinkedGetComposite(node, childSourceParameters);
			} else {
				handleCompositeParameterLinkedGetBasic(node, childSourceParameters);
			}
		}
	}

	private static void handleCompositeParameterNotLinked(
			CompositeParameterNode parameter, List<AbstractParameterNode> childSourceParameters) {

		childSourceParameters.addAll(parameter.getParameters());
	}

	private static void handleCompositeParameterLinkedGetComposite(
			AbstractParameterNode parameter, List<AbstractParameterNode> childSourceParameters) {
// Create a mock deployment parameter.
		CompositeParameterNode composite = new CompositeParameterNode(parameter.getName(), null);
		composite.setLinkToGlobalParameter(parameter);

		childSourceParameters.add(composite);
	}

	private static void handleCompositeParameterLinkedGetBasic(
			AbstractParameterNode parameter, List<AbstractParameterNode> childSourceParameters) {
// Create a mock deployment parameter.
		BasicParameterNode basic = new BasicParameterNode(parameter.getName(), ((BasicParameterNode) parameter).getType(), null);
		basic.setLinkToGlobalParameter(parameter);

		childSourceParameters.add(basic);
	}

	private static void deployBasicParameter(
			BasicParameterNode sourceParameter, MethodNode targetMethodNode, String prefix, NodeMapper mapper) {

		BasicParameterNode copy = sourceParameter.createCopy(mapper);

		copy.setCompositeName(prefix + copy.getName());

		targetMethodNode.addParameter(copy);
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
		
		//constraintNodes.forEach(e -> targetMethod.addConstraint(e.createCopy(mapper))); // TODO MO-RE use prefix
		
		for (ConstraintNode constraintNode : constraintNodes) {
			
			ConstraintNode copyOfConstraintNode = constraintNode.createCopy(nodeMapper);
			copyOfConstraintNode.setName(prefix + constraintNode.getName());
			
			targetMethod.addConstraint(copyOfConstraintNode);
		}
	}

	private static TestCase revertToOriginalTestCase(NodeMapper mapper, TestCase deployedTestCase) {

		List<ChoiceNode> revertedChoices = new ArrayList<>();

		List<ChoiceNode> deployedChoices = deployedTestCase.getListOfChoiceNodes();

		for (ChoiceNode deployedChoiceNode : deployedChoices) {

			ChoiceNode originalChoiceNode = mapper.getMappedNodeSource(deployedChoiceNode);

			revertedChoices.add(originalChoiceNode);
		}

		return new TestCase(revertedChoices);
	}

}
