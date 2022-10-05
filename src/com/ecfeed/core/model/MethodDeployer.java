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

}
