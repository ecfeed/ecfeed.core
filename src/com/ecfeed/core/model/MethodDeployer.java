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

	public static MethodNode deploy(NodeMapper mapper, MethodNode methodSource) {

		if (methodSource == null) {
			ExceptionHelper.reportRuntimeException("The source method is not defined.");
		}

		MethodNode methodTarget = new MethodNode(methodSource.getName() + "_" +  POSTFIX);

		extractParameters(methodSource, methodTarget, mapper);
		extractConstraints(methodSource, methodTarget, mapper);

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
		BasicParameterNode parameterDeployed = ((BasicParameterNode) parameterSource).createCopy(mapper);
		parameterDeployed.setCompositeName(prefix + parameterDeployed.getName());
		methodTarget.addParameter(parameterDeployed);
	}

	private static void extractParametersComposite(String prefix, MethodNode methodTarget, AbstractParameterNode parameterSource, NodeMapper mapper) {
		CompositeParameterNode parameterParsed = (CompositeParameterNode) parameterSource;
		extractParameters(prefix, methodTarget, parameterParsed.getParameters(), mapper);
	}

	private static void extractConstraints(MethodNode methodSource, MethodNode methodTarget, NodeMapper mapper) {

		methodSource.getConstraintNodes().forEach(e -> methodTarget.addConstraint(e.createCopy(mapper)));

		methodSource.getParameters().forEach(e -> extractConstraintsComposite(e, methodTarget, mapper));
	}

	private static void extractConstraintsComposite(AbstractParameterNode parameter, MethodNode methodTarget, NodeMapper mapper) {

		if (parameter instanceof BasicParameterNode) {
			return;
		}

		CompositeParameterNode parameterParsed = (CompositeParameterNode) parameter;

		parameterParsed.getConstraintNodes().forEach(e -> methodTarget.addConstraint(e.createCopy(mapper)));


		for (AbstractParameterNode parameterComposite : parameterParsed.getParameters()) {

			if (parameterComposite instanceof CompositeParameterNode) {
				extractConstraintsComposite(parameterComposite, methodTarget, mapper);
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
