/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.IConstraintsParentNode;
import com.ecfeed.core.model.utils.BasicParameterWithChoice;

public class UsageOfChoicesInConstraints { // XYX rename

	private Map<BasicParameterWithChoice, List<String>> fMapOfUsages;


	public UsageOfChoicesInConstraints(BasicParameterNode basicParameterNode) {

		IConstraintsParentNode methodNode = (IConstraintsParentNode) basicParameterNode.getParent();

		fMapOfUsages = new HashMap<>();

		List<Constraint> constraints = methodNode.getConstraints();

		for (Constraint constraint : constraints) {

			//List<ChoiceNode> choiceNodesUsedInConstraint = constraint.getChoices(basicParameterNode);
			
			List<BasicParameterWithChoice> itemsUsedInConstraint = 
					getParametersWithChoicesUsedInConstraint(constraint, basicParameterNode);

			updateMapOfUsages(constraint, itemsUsedInConstraint);
		}
	}

	private List<BasicParameterWithChoice> getParametersWithChoicesUsedInConstraint(
			Constraint constraint,
			BasicParameterNode basicParameterNode) {
		
		List<BasicParameterWithChoice> result = new ArrayList<>();
		
		List<ChoiceNode> choiceNodesUsedInConstraint = constraint.getChoices(basicParameterNode);
		
		for (ChoiceNode choiceNode : choiceNodesUsedInConstraint) {
			
			BasicParameterWithChoice basicParameterWithChoice = 
					new BasicParameterWithChoice(basicParameterNode, choiceNode);
			
			result.add(basicParameterWithChoice);
		}
		
		return result;
	}

	public List<String> getConstraintNames(BasicParameterWithChoice basicParameterWithChoice) {

		List<String> constraintNames = fMapOfUsages.get(basicParameterWithChoice);

		return constraintNames;
	}

	public List<String> getConstraintNames(String parameterName, String choiceName) {

		for (BasicParameterWithChoice basicParameterWithChoice : fMapOfUsages.keySet()) {

			if (!StringHelper.isEqual(basicParameterWithChoice.getBasicParameterNode().getName(), parameterName)) {
				continue;
			}
			
			if (!StringHelper.isEqual(basicParameterWithChoice.getChoiceNode().getQualifiedName(), choiceName)) {
				continue;
			}

			List<String> choiceNames = fMapOfUsages.get(basicParameterWithChoice);
			return choiceNames;
		}

		return null;
	}

	public boolean choiceNameExists(String parameterName, String choiceName) { // XYX rename

		for (BasicParameterWithChoice basicParameterWithChoice : fMapOfUsages.keySet()) {

			String currentParameterName = basicParameterWithChoice.getBasicParameterNode().getName();
			String currentChoiceName = basicParameterWithChoice.getChoiceNode().getQualifiedName();

			if (StringHelper.isEqual(currentChoiceName, choiceName) && 
					StringHelper.isEqual(currentParameterName, parameterName)) {
				return true;
			}
		}
        	
		return false;
	}

	private void updateMapOfUsages(
			Constraint constraint, 
			List<BasicParameterWithChoice> itemsUsedInConstraint) {

		for (BasicParameterWithChoice choiceNode : itemsUsedInConstraint) {

			if (fMapOfUsages.containsKey(choiceNode)) {

				updateExistingElement(choiceNode, constraint);
				return;
			}

			addNewElement(choiceNode, constraint);
		}
	}

	private void addNewElement(BasicParameterWithChoice basicParameterWithChoice, Constraint constraint) {

		List<String> constraintNames = new ArrayList<>();
		constraintNames.add(constraint.getName());

		fMapOfUsages.put(basicParameterWithChoice, constraintNames);
	}


	private void updateExistingElement(BasicParameterWithChoice basicParameterWithChoice, Constraint constraint) {

		List<String> constraintNames = fMapOfUsages.get(basicParameterWithChoice);

		String constraintName = constraint.getName();

		if (constraintNames.contains(constraintName)) {
			return;
		}

		constraintNames.add(constraintName);
	}


}
