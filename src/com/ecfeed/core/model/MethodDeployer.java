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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecfeed.core.utils.ExceptionHelper;

public abstract class MethodDeployer {

	public static MethodNode constructMethod(List<MethodParameterNode> parameters, List<ConstraintNode> constraints) {
		MethodNode method = new MethodNode("construct");

		parameters.stream().map(MethodParameterNode::makeClone).forEach(method::addParameter);

		constraints.stream().forEach(e -> method.addConstraint(e.getCopy(method)));

		return method;
	}

	public static MethodNode deploy(MethodNode sourceMethodNode) {

		if (sourceMethodNode == null) {
			ExceptionHelper.reportRuntimeException("No method.");
		}

		MethodNode deployedMethodNode = new MethodNode(sourceMethodNode.getName());
		deployedMethodNode.setParent(sourceMethodNode.getParent());

		deployedMethodNode.setProperties(sourceMethodNode.getProperties());

		DeploymentMapper deploymentMapper = new DeploymentMapper();

		for (MethodParameterNode sourceMethodParameterNode : sourceMethodNode.getMethodParameters()) {

			MethodParameterNode developedMethodParameter = 
					deployParameter(sourceMethodParameterNode, deploymentMapper);

			deployedMethodNode.addParameter(developedMethodParameter);
		}

		for (ConstraintNode constraint : sourceMethodNode.getConstraintNodes()) {

			ConstraintNode deployedConstraintNode = 
					deployConstraintNode(constraint, deployedMethodNode, deploymentMapper);

			deployedMethodNode.addConstraint(deployedConstraintNode);
		}

		return deployedMethodNode;
	}

	private static ConstraintNode deployConstraintNode(
			ConstraintNode sourceConstraintNode, 
			MethodNode deployedMethodNode,
			DeploymentMapper deploymentMapper) {

		Constraint sourceConstraint = sourceConstraintNode.getConstraint();
		Constraint deployedConstraint = deployConstraint(sourceConstraint, deploymentMapper);

		ConstraintNode deployedConstraintNode = new ConstraintNode("name", deployedConstraint); 
		deployedConstraintNode.setProperties(sourceConstraintNode.getProperties());

		return deployedConstraintNode;
	}

	private static Constraint deployConstraint(
			Constraint sourceConstraint,
			DeploymentMapper deploymentMapper) {

		AbstractStatement sourcePrecondition = sourceConstraint.getPrecondition();
		AbstractStatement deployedPrecondition = deployStatement(sourcePrecondition, deploymentMapper);

		AbstractStatement sourcePostcondition = sourceConstraint.getPostcondition();
		AbstractStatement deployedPostcondition = deployStatement(sourcePostcondition, deploymentMapper);

		Constraint deployedConstraint = 
				new Constraint(
						sourceConstraint.getName(), 
						sourceConstraint.getType(), 
						deployedPrecondition, 
						deployedPostcondition);

		return deployedConstraint;
	}

	private static AbstractStatement deployStatement(
			AbstractStatement abstractStatement,
			DeploymentMapper deploymentMapper) {

		AbstractStatement deployedStatement = abstractStatement.createDeepCopy(deploymentMapper);

		return deployedStatement;
	}


	public static MethodParameterNode deployParameter(
			MethodParameterNode sourceMethodParameterNode,
			DeploymentMapper deploymentMapper) {

		MethodParameterNode deployedMethodParameterNode = 
				new MethodParameterNode(
						sourceMethodParameterNode.getName(), 
						sourceMethodParameterNode.getType(), 
						sourceMethodParameterNode.getDefaultValue(), 
						sourceMethodParameterNode.isExpected(), 
						null);

		deployedMethodParameterNode.setParent(sourceMethodParameterNode.getParent());

		// MethodParameterNode linkedMethod = deployedMethodParameterNode.getLinkToMethod();
		// MethodAndStructureParent linked = deployedMethodParameterNode.getLinkToMethodAndStructureParent();

		deployedMethodParameterNode.setLinked(sourceMethodParameterNode.isLinked());
		deployedMethodParameterNode.setLinkToGlobalParameter(sourceMethodParameterNode.getLinkToGlobalParameter());
		deployedMethodParameterNode.setProperties(sourceMethodParameterNode.getProperties());
		deployedMethodParameterNode.setDefaultValueString(sourceMethodParameterNode.getDefaultValue());
		
		deploymentMapper.addParameterMappings(sourceMethodParameterNode, deployedMethodParameterNode);

		for (ChoiceNode sourceChoiceNode : sourceMethodParameterNode.getChoices()) {

			ChoiceNode deployedChoiceNode = deployChoiceNode(sourceChoiceNode, deploymentMapper);
			deployedMethodParameterNode.addChoice(deployedChoiceNode);
		}

		return deployedMethodParameterNode;
	}

	public static ChoiceNode deployChoiceNode(
			ChoiceNode sourceChoiceNode,
			DeploymentMapper deploymentMapper) {

		ChoiceNode deployedChoiceNode = new ChoiceNode(sourceChoiceNode.getName(), sourceChoiceNode.getValueString());

		deployedChoiceNode.setProperties(sourceChoiceNode.getProperties());
		deployedChoiceNode.setRandomizedValue(sourceChoiceNode.isRandomizedValue());
		deployedChoiceNode.setParent(sourceChoiceNode.getParent());
		deployedChoiceNode.setOtherChoice(sourceChoiceNode);

		deploymentMapper.addChoiceMappings(sourceChoiceNode, deployedChoiceNode);

		for (String label : sourceChoiceNode.getLabels()) {

			deployedChoiceNode.addLabel(label);
		}

		for (ChoiceNode childChoiceNode : sourceChoiceNode.getChoices()) {

			ChoiceNode deployChoiceNode = deployChoiceNode(childChoiceNode, deploymentMapper);
			deployedChoiceNode.addChoice(deployChoiceNode);
		}

		return deployedChoiceNode;
	}

	// TODO NE-TE use!
	public static List<MethodParameterNode> getDevelopedParametersWithChoices(MethodNode methodNode) {

		List<MethodParameterNode> developedParameters = new ArrayList<>();

		List<MethodParameterNode> parameters = methodNode.getMethodParameters();

		for (MethodParameterNode methodParameterNode : parameters) {

			developOneParameter(methodParameterNode, developedParameters);
		}

		return developedParameters;
	}

	private static void developOneParameter(
			MethodParameterNode methodParameterNode,
			List<MethodParameterNode> inOutDevelopedParameters) {

		MethodNode linkedMethodNode = methodParameterNode.getLinkToMethod();

		if (linkedMethodNode == null) {
			MethodParameterNode clonedMethodParameterNode = methodParameterNode.makeClone();
			clonedMethodParameterNode.clearChoices();

			ChoiceNodeHelper.cloneChoiceNodesRecursively(methodParameterNode, clonedMethodParameterNode);

			inOutDevelopedParameters.add(clonedMethodParameterNode);
			return;
		}

		developChildParameters(methodParameterNode, linkedMethodNode, inOutDevelopedParameters);
	}

	private static void developChildParameters(
			AbstractParameterNode abstractParameterNode, 
			MethodNode linkedMethodNode,
			List<MethodParameterNode> inOutDevelopedParameters) {

		List<MethodParameterNode> linkedParametersWithChoices = getDevelopedParametersWithChoices(linkedMethodNode);

		for (MethodParameterNode linkedParameterWithChoices : linkedParametersWithChoices) {

			String parameterName = abstractParameterNode.getName() + "_" + linkedParameterWithChoices.getName();
			String parameterType = linkedParameterWithChoices.getType();
			String defaultValue = linkedParameterWithChoices.getDefaultValue();
			boolean isExpected = linkedParameterWithChoices.isExpected();

			MethodParameterNode clonedMethodParameterNode = 
					new MethodParameterNode(
							parameterName,
							parameterType,
							defaultValue,
							isExpected,
							false,
							null,
							null);

			ChoiceNodeHelper.cloneChoiceNodesRecursively(linkedParameterWithChoices, clonedMethodParameterNode);

			inOutDevelopedParameters.add(clonedMethodParameterNode);
		}
	}

	public static List<TestCase> revertToOriginalChoices(List<TestCase> deployedTestCases) {

		List<TestCase> result = new ArrayList<>();

		for (TestCase deployedTestCase : deployedTestCases) {

			TestCase revertedTestCaseNode = revertToOriginalTestCase(deployedTestCase);

			result.add(revertedTestCaseNode);
		}

		return result;
	}

	private static TestCase revertToOriginalTestCase(TestCase deployedTestCase) {

		List<ChoiceNode> revertedChoices = new ArrayList<>();

		List<ChoiceNode> deployedChoices = deployedTestCase.getListOfChoiceNodes();

		for (ChoiceNode deployedChoiceNode : deployedChoices) {

			ChoiceNode originalChoiceNode = deployedChoiceNode.getOtherChoice();

			revertedChoices.add(originalChoiceNode);
		}

		return new TestCase(revertedChoices);
	}

}
