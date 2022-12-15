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

public abstract class MethodDeployer {

	public static String POSTFIX = "deployed";

	public static MethodNode deploy(NodeMapper mapper, MethodNode sourceMethodNode) { // TODO MO-RE refactor - mapper as second parameter (additional)

		if (sourceMethodNode == null) {
			ExceptionHelper.reportRuntimeException("The source method is not defined.");
		}

		MethodNode targetMethodNode = new MethodNode(sourceMethodNode.getName() + "_" +  POSTFIX);

		deployParameters(sourceMethodNode, targetMethodNode, mapper);
		deployConstraints(sourceMethodNode, targetMethodNode, mapper);

		return targetMethodNode;
	}

	private static void deployParameters(MethodNode methodSource, MethodNode methodTarget, NodeMapper mapper) {
		
		String prefix = "";
		
		deployParametersRecursively(methodSource.getParameters(), methodTarget, prefix, mapper);
	}

	private static void deployParametersRecursively(
			List<AbstractParameterNode> sourceParameters, MethodNode targetMethodNode, String prefix, NodeMapper mapper) {

		for (AbstractParameterNode sourceParameter : sourceParameters) {

			if (sourceParameter instanceof BasicParameterNode) {
				
				deployBasicParameter(((BasicParameterNode) sourceParameter), targetMethodNode, prefix, mapper);
			}

			if (sourceParameter instanceof CompositeParameterNode) {
				
				String prefixParsed = prefix + sourceParameter.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR;
				
				List<AbstractParameterNode> childSourceParameters = ((CompositeParameterNode) sourceParameter).getParameters();
				
				deployParametersRecursively(childSourceParameters, targetMethodNode, prefixParsed, mapper);
			}
		}
	}

	private static void deployBasicParameter(
			BasicParameterNode sourceParameter, MethodNode targetMethodNode, String prefix, NodeMapper mapper) {
		
		BasicParameterNode copy = sourceParameter.createCopy(mapper);
		copy.setCompositeName(prefix + copy.getName());
		
		targetMethodNode.addParameter(copy);
	}

	private static void deployConstraints(MethodNode sourceMethod, MethodNode targetMethod, NodeMapper mapper) {

		sourceMethod.getConstraintNodes().forEach(e -> targetMethod.addConstraint(e.createCopy(mapper)));

		sourceMethod.getParameters().forEach(e -> deployConstraintsForCompositeParameterRecursively(e, targetMethod, mapper));
	}

	private static void deployConstraintsForCompositeParameterRecursively(
			AbstractParameterNode parameter, MethodNode targetMethod, NodeMapper mapper) {

		if (parameter instanceof BasicParameterNode) {
			return;
		}

		CompositeParameterNode compositeParameterNode = (CompositeParameterNode) parameter;

		compositeParameterNode.getConstraintNodes().forEach(e -> targetMethod.addConstraint(e.createCopy(mapper)));


		for (AbstractParameterNode abstractParameterNode : compositeParameterNode.getParameters()) {

			if (abstractParameterNode instanceof CompositeParameterNode) {
				deployConstraintsForCompositeParameterRecursively(abstractParameterNode, targetMethod, mapper);
			}
		}
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
		constraints.forEach(e -> method.addConstraint(e.createCopy(mapper)));

		return method;
	}

	public static void updateDeploymentNameConsistency(RootNode root) {

		for (ClassNode classNode : root.getClasses()) {
			for (MethodNode methodNode : classNode.getMethods()) {
				if (validateDeploymentSizeConsistency(methodNode)) {
					updateDeploymentNameConsistency(methodNode);
				}
			}
		}
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

	public static List<TestCase> revertToOriginalChoices(NodeMapper mapper, List<TestCase> deployedTestCases) {

		List<TestCase> result = new ArrayList<>();

		for (TestCase deployedTestCase : deployedTestCases) {

			TestCase revertedTestCaseNode = revertToOriginalTestCase(mapper, deployedTestCase);

			result.add(revertedTestCaseNode);
		}

		return result;
	}

	private static TestCase revertToOriginalTestCase(NodeMapper mapper, TestCase deployedTestCase) {

		List<ChoiceNode> revertedChoices = new ArrayList<>();

		List<ChoiceNode> deployedChoices = deployedTestCase.getListOfChoiceNodes();

		for (ChoiceNode deployedChoiceNode : deployedChoices) {

			ChoiceNode originalChoiceNode = (ChoiceNode) mapper.getMappedNode(deployedChoiceNode);

			revertedChoices.add(originalChoiceNode);
		}

		return new TestCase(revertedChoices);
	}

}
