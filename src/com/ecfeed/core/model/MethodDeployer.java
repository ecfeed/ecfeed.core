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

public abstract class MethodDeployer {

	public static MethodNode deploy(MethodNode sourceMethodNode) {

		if (sourceMethodNode == null) {
			ExceptionHelper.reportRuntimeException("No method.");
		}

		MethodNode deployedMethodNode = new MethodNode(sourceMethodNode.getName());
		deployedMethodNode.setParent(sourceMethodNode.getParent());

		deployedMethodNode.setProperties(sourceMethodNode.getProperties());

		for (MethodParameterNode methodParameterNode : sourceMethodNode.getMethodParameters()) {

			MethodParameterNode developedMethodParameter = deployParameter(methodParameterNode);
			deployedMethodNode.addParameter(developedMethodParameter);
		}

		for (ConstraintNode constraint : sourceMethodNode.getConstraintNodes()) {

			constraint = constraint.getCopy(deployedMethodNode); // TODO DE-NE
			deployedMethodNode.addConstraint(constraint);
		}

		return deployedMethodNode;
	}

	public static MethodParameterNode deployParameter(MethodParameterNode sourceMethodParameterNode) {

		MethodParameterNode deployedMethodParameterNode = 
				new MethodParameterNode(
						sourceMethodParameterNode.getName(), 
						sourceMethodParameterNode.getType(), 
						sourceMethodParameterNode.getDefaultValue(), 
						sourceMethodParameterNode.isExpected(), 
						null);

		deployedMethodParameterNode.setParent(sourceMethodParameterNode.getParent());

		deployedMethodParameterNode.setLinked(sourceMethodParameterNode.isLinked());
		deployedMethodParameterNode.setLinkToGlobalParameter(sourceMethodParameterNode.getLinkToGlobalParameter());
		deployedMethodParameterNode.setProperties(sourceMethodParameterNode.getProperties());
		deployedMethodParameterNode.setDefaultValueString(sourceMethodParameterNode.getDefaultValue());

		for (ChoiceNode sourceChoiceNode : sourceMethodParameterNode.getChoices()) {

			ChoiceNode deployedChoiceNode = deployChoiceNode(sourceChoiceNode);
			deployedMethodParameterNode.addChoice(deployedChoiceNode);
		}

		return deployedMethodParameterNode;
	}

	public static ChoiceNode deployChoiceNode(ChoiceNode sourceChoiceNode) {

		ChoiceNode deployedChoiceNode = new ChoiceNode(sourceChoiceNode.getName(), sourceChoiceNode.getValueString());

		deployedChoiceNode.setProperties(sourceChoiceNode.getProperties());
		deployedChoiceNode.setRandomizedValue(sourceChoiceNode.isRandomizedValue());
		deployedChoiceNode.setParent(sourceChoiceNode.getParent());
		deployedChoiceNode.setOtherChoice(sourceChoiceNode);

		for (String label : sourceChoiceNode.getLabels()) {

			deployedChoiceNode.addLabel(label);
		}

		for (ChoiceNode childChoiceNode : sourceChoiceNode.getChoices()) {

			ChoiceNode deployChoiceNode = deployChoiceNode(childChoiceNode);
			deployedChoiceNode.addChoice(deployChoiceNode);
		}

		return deployedChoiceNode;
	}

	// XYX use !
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
	
}
