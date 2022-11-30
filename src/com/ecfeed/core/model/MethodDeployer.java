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

	public static MethodNode deploy(MethodNode methodSource) {

		if (methodSource == null) {
			ExceptionHelper.reportRuntimeException("The source method is not defined.");
		}

		NodeMapper mapper = new NodeMapper();

		MethodNode methodTarget = new MethodNode(methodSource.getName() + "_" +  POSTFIX);

		extractParameters(methodSource, methodTarget, mapper);
		extractConstraints(methodSource, methodTarget);

		methodSource.setDeployedParameters(methodTarget.getParametersAsBasic());

		return methodTarget;
	}

	private static void extractParameters(MethodNode methodSource, MethodNode methodTarget, NodeMapper mapper) {
		String prefix = "";

		extractParameters(prefix, methodTarget, methodSource.getParameters(), mapper);
	}

	private static void extractParameters(String prefix, MethodNode methodTarget, List<AbstractParameterNode> parametersSource, NodeMapper mapper) {

		for (AbstractParameterNode sourceParameter : parametersSource) {

			if (sourceParameter instanceof BasicParameterNode) {
				extractParametersBasic(prefix, methodTarget, sourceParameter, mapper);
			}

			if (sourceParameter instanceof CompositeParameterNode) {
				String prefixParsed = prefix + sourceParameter.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR;
				extractParametersComposite(prefixParsed, methodTarget, sourceParameter, mapper);
			}
		}
	}

	private static void extractParametersBasic(String prefix, MethodNode methodTarget, AbstractParameterNode parameterSource, NodeMapper mapper) {
		BasicParameterNode parameterParsed = ((BasicParameterNode) parameterSource).createCopy(mapper);
		parameterParsed.setCompositeName(prefix + parameterParsed.getName());
		methodTarget.addParameter(parameterParsed);
	}

	private static void extractParametersComposite(String prefix, MethodNode methodTarget, AbstractParameterNode parameterSource, NodeMapper mapper) {
		CompositeParameterNode parameterParsed = (CompositeParameterNode) parameterSource;
		extractParameters(prefix, methodTarget, parameterParsed.getParameters(), mapper);
	}

	private static void extractConstraints(MethodNode methodSource, MethodNode methodTarget) {

		methodSource.getConstraintNodes().forEach(e -> methodTarget.addConstraint(e.createCopy(methodTarget)));
	}

	public static MethodNode construct(List<BasicParameterNode> parameters, List<ConstraintNode> constraints, NodeMapper mapper) {

		if (parameters == null) {
			ExceptionHelper.reportRuntimeException("The list of parameters is not defined.");
		}

		if (constraints == null) {
			ExceptionHelper.reportRuntimeException("The list of constraints is not defined.");
		}

		MethodNode method = new MethodNode("construct");

		parameters.stream().map(e -> e.createCopy(mapper)).forEach(method::addParameter);
		constraints.forEach(e -> method.addConstraint(e.createCopy(method)));

		return method;
	}

	public static void updateDeploymentNameConsistency(MethodNode method) {

		if (!method.isDeployed()) {
			return;
		}

		List<BasicParameterNode> deployment = method.getDeployedMethodParameters();

		if (deployment == null) {
			return;
		}

		for (BasicParameterNode parameter : deployment) {
			AbstractParameterNode parameterReference = parameter.getDeploymentParameter();
			parameter.setNameUnsafe(getQualifiedDeploymentName(parameterReference));
		}
	}

	private static String getQualifiedDeploymentName(AbstractParameterNode parameter) {

		return getQualifiedDeploymentName(parameter, "");
	}

	private static String getQualifiedDeploymentName(AbstractParameterNode parameter, String prefix) {
		String prefixParsed = parameter.getName() + prefix;

		IAbstractNode parent = parameter.getParent();

		if (parent instanceof AbstractParameterNode) {
			return getQualifiedDeploymentName((AbstractParameterNode) parent, SignatureHelper.SIGNATURE_NAME_SEPARATOR + prefixParsed);
		}

		return prefixParsed;
	}

	public static boolean validateDeploymentSizeConsistency(MethodNode method) {

		if (!method.isDeployed()) {
			return false;
		}

		List<BasicParameterNode> deployment = method.getDeployedMethodParameters();

		if (deployment == null) {
			return false;
		}

		return getNestedSize(method) == deployment.size();
	}

	private static int getNestedSize(MethodNode method) {
		int size = 0;

		for (AbstractParameterNode parameter : method.getParameters()) {
			size = getNestedSize(parameter, size);
		}

		return size;
	}

	private static int getNestedSize(AbstractParameterNode parameter, int size) {

		if (parameter instanceof BasicParameterNode) {
			size++;
		}

		if (parameter instanceof CompositeParameterNode) {
			List<AbstractParameterNode> parameters = ((CompositeParameterNode) parameter).getParameters();

			for (AbstractParameterNode parameterNested : parameters) {
				size = getNestedSize(parameterNested, size);
			}
		}

		return size;
	}

	public static void validateDeploymentNameConsistency(MethodNode method) {

		if (!method.isDeployed()) {
			return;
		}

		List<BasicParameterNode> deployment = method.getDeployedMethodParameters();

		if (deployment == null) {
			return;
		}

		for (AbstractParameterNode parameter : deployment) {
			String[] parameterCandidateSegments = parameter.getName().split(SignatureHelper.SIGNATURE_NAME_SEPARATOR);

			try {
				AbstractParameterNode parameterCandidate = method.getParameter(method.getParameterIndex(parameterCandidateSegments[0]));
				getNestedBasicParameter(parameterCandidate, parameterCandidateSegments, 1);
			}
			catch (Exception e) {
				ExceptionHelper.reportRuntimeException("The method could not be serialized. At least one parameter is missing.");
			}
		}
	}

	public static BasicParameterNode getNestedBasicParameter(AbstractParameterNode parameter, String[] path, int index) {

		if (parameter instanceof BasicParameterNode) {
			return (BasicParameterNode) parameter;
		}

		try {
			CompositeParameterNode element = (CompositeParameterNode) parameter;
			AbstractParameterNode elementNested = element.getParameter(element.getParameterIndex(path[index]));

			return getNestedBasicParameter(elementNested, path, index + 1);
		}
		catch (Exception e) {
			ExceptionHelper.reportRuntimeException("The parameter '" + parameter.getName() + "'could not be parsed.");
		}

		return null;
	}







//
//	public static MethodNode deployOld(MethodNode sourceMethodNode) {
//
//		if (sourceMethodNode == null) {
//			ExceptionHelper.reportRuntimeException("No method.");
//		}
//
//		MethodNode deployedMethodNode = new MethodNode(sourceMethodNode.getName());
//		deployedMethodNode.setParent(sourceMethodNode.getParent());
//
//		deployedMethodNode.setProperties(sourceMethodNode.getProperties());
//
//		DeploymentMapper deploymentMapper = new DeploymentMapper();
//
//		for (MethodParameterNode sourceMethodParameterNode : sourceMethodNode.getMethodParameters()) {
//
//			MethodParameterNode developedMethodParameter =
//					deployParameter(sourceMethodParameterNode, deploymentMapper);
//
//			deployedMethodNode.addParameter(developedMethodParameter);
//		}
//
//		for (ConstraintNode constraint : sourceMethodNode.getConstraintNodes()) {
//
//			ConstraintNode deployedConstraintNode =
//					deployConstraintNode(constraint, deployedMethodNode, deploymentMapper);
//
//			deployedMethodNode.addConstraint(deployedConstraintNode);
//		}
//
//		return deployedMethodNode;
//	}
//
//	private static ConstraintNode deployConstraintNode(
//			ConstraintNode sourceConstraintNode,
//			MethodNode deployedMethodNode,
//			DeploymentMapper deploymentMapper) {
//
//		Constraint sourceConstraint = sourceConstraintNode.getConstraint();
//		Constraint deployedConstraint = deployConstraint(sourceConstraint, deploymentMapper);
//
//		ConstraintNode deployedConstraintNode = new ConstraintNode("name", deployedConstraint);
//		deployedConstraintNode.setProperties(sourceConstraintNode.getProperties());
//
//		return deployedConstraintNode;
//	}
//
//	private static Constraint deployConstraint(
//			Constraint sourceConstraint,
//			DeploymentMapper deploymentMapper) {
//
//		AbstractStatement sourcePrecondition = sourceConstraint.getPrecondition();
//		AbstractStatement deployedPrecondition = deployStatement(sourcePrecondition, deploymentMapper);
//
//		AbstractStatement sourcePostcondition = sourceConstraint.getPostcondition();
//		AbstractStatement deployedPostcondition = deployStatement(sourcePostcondition, deploymentMapper);
//
//		Constraint deployedConstraint =
//				new Constraint(
//						sourceConstraint.getName(),
//						sourceConstraint.getType(),
//						deployedPrecondition,
//						deployedPostcondition);
//
//		return deployedConstraint;
//	}
//
//	private static AbstractStatement deployStatement(
//			AbstractStatement abstractStatement,
//			DeploymentMapper deploymentMapper) {
//
//		AbstractStatement deployedStatement = abstractStatement.createDeepCopy(deploymentMapper);
//
//		return deployedStatement;
//	}
//
//
//	public static MethodParameterNode deployParameter(
//			MethodParameterNode sourceMethodParameterNode,
//			DeploymentMapper deploymentMapper) {
//
//		MethodParameterNode deployedMethodParameterNode =
//				new MethodParameterNode(
//						sourceMethodParameterNode.getName(),
//						sourceMethodParameterNode.getType(),
//						sourceMethodParameterNode.getDefaultValue(),
//						sourceMethodParameterNode.isExpected(),
//						null);
//
//		deployedMethodParameterNode.setParent(sourceMethodParameterNode.getParent());
//
//		// MethodParameterNode linkedMethod = deployedMethodParameterNode.getLinkToMethod();
//		// MethodAndStructureParent linked = deployedMethodParameterNode.getLinkToMethodAndStructureParent();
//
//		deployedMethodParameterNode.setLinked(sourceMethodParameterNode.isLinked());
//		deployedMethodParameterNode.setLinkToGlobalParameter(sourceMethodParameterNode.getLinkToGlobalParameter());
//		deployedMethodParameterNode.setProperties(sourceMethodParameterNode.getProperties());
//		deployedMethodParameterNode.setDefaultValueString(sourceMethodParameterNode.getDefaultValue());
//
//		deploymentMapper.addParameterMappings(sourceMethodParameterNode, deployedMethodParameterNode);
//
//		for (ChoiceNode sourceChoiceNode : sourceMethodParameterNode.getChoices()) {
//
//			ChoiceNode deployedChoiceNode = deployChoiceNode(sourceChoiceNode, deploymentMapper);
//			deployedMethodParameterNode.addChoice(deployedChoiceNode);
//		}
//
//		return deployedMethodParameterNode;
//	}
//
//	public static ChoiceNode deployChoiceNode(
//			ChoiceNode sourceChoiceNode,
//			DeploymentMapper deploymentMapper) {
//
//		ChoiceNode deployedChoiceNode = new ChoiceNode(sourceChoiceNode.getName(), sourceChoiceNode.getValueString());
//
//		deployedChoiceNode.setProperties(sourceChoiceNode.getProperties());
//		deployedChoiceNode.setRandomizedValue(sourceChoiceNode.isRandomizedValue());
//		deployedChoiceNode.setParent(sourceChoiceNode.getParent());
//		deployedChoiceNode.setOtherChoice(sourceChoiceNode);
//
//		deploymentMapper.addChoiceMappings(sourceChoiceNode, deployedChoiceNode);
//
//		for (String label : sourceChoiceNode.getLabels()) {
//
//			deployedChoiceNode.addLabel(label);
//		}
//
//		for (ChoiceNode childChoiceNode : sourceChoiceNode.getChoices()) {
//
//			ChoiceNode deployChoiceNode = deployChoiceNode(childChoiceNode, deploymentMapper);
//			deployedChoiceNode.addChoice(deployChoiceNode);
//		}
//
//		return deployedChoiceNode;
//	}
//
//	// TODO NE-TE use!
//	public static List<MethodParameterNode> getDevelopedParametersWithChoices(MethodNode methodNode) {
//
//		List<MethodParameterNode> developedParameters = new ArrayList<>();
//
//		List<MethodParameterNode> parameters = methodNode.getMethodParameters();
//
//		for (MethodParameterNode methodParameterNode : parameters) {
//
//			developOneParameter(methodParameterNode, developedParameters);
//		}
//
//		return developedParameters;
//	}
//
//	private static void developOneParameter(
//			MethodParameterNode methodParameterNode,
//			List<MethodParameterNode> inOutDevelopedParameters) {
//
//		MethodNode linkedMethodNode = methodParameterNode.getLinkToMethod();
//
//		if (linkedMethodNode == null) {
//			MethodParameterNode clonedMethodParameterNode = methodParameterNode.makeClone();
//			clonedMethodParameterNode.clearChoices();
//
//			ChoiceNodeHelper.cloneChoiceNodesRecursively(methodParameterNode, clonedMethodParameterNode);
//
//			inOutDevelopedParameters.add(clonedMethodParameterNode);
//			return;
//		}
//
//		developChildParameters(methodParameterNode, linkedMethodNode, inOutDevelopedParameters);
//	}
//
//	private static void developChildParameters(
//			AbstractParameterNode abstractParameterNode,
//			MethodNode linkedMethodNode,
//			List<MethodParameterNode> inOutDevelopedParameters) {
//
//		List<MethodParameterNode> linkedParametersWithChoices = getDevelopedParametersWithChoices(linkedMethodNode);
//
//		for (MethodParameterNode linkedParameterWithChoices : linkedParametersWithChoices) {
//
//			String parameterName = abstractParameterNode.getName() + "_" + linkedParameterWithChoices.getName();
//			String parameterType = linkedParameterWithChoices.getType();
//			String defaultValue = linkedParameterWithChoices.getDefaultValue();
//			boolean isExpected = linkedParameterWithChoices.isExpected();
//
//			MethodParameterNode clonedMethodParameterNode =
//					new MethodParameterNode(
//							parameterName,
//							parameterType,
//							defaultValue,
//							isExpected,
//							false,
//							null,
//							null);
//
//			ChoiceNodeHelper.cloneChoiceNodesRecursively(linkedParameterWithChoices, clonedMethodParameterNode);
//
//			inOutDevelopedParameters.add(clonedMethodParameterNode);
//		}
//	}
//
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

			ChoiceNode originalChoiceNode = deployedChoiceNode.getDeploymentChoiceNode();

			revertedChoices.add(originalChoiceNode);
		}

		return new TestCase(revertedChoices);
	}

}
