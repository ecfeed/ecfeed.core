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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;

public class UsageOfChoicesInConstraints {

	private Map<ChoiceNode, List<String>> fMapOfUsages;


	public UsageOfChoicesInConstraints(MethodParameterNode methodParameterNode) {

		MethodNode methodNode = methodParameterNode.getMethod();

		fMapOfUsages = new HashMap<>();

		List<Constraint> constraints = methodNode.getAllConstraints();

		for (Constraint constraint : constraints) {

			List<ChoiceNode> choiceNodesUsedInConstraint = constraint.getListOfChoices();

			updateMapOfUsages(constraint, choiceNodesUsedInConstraint);
		}
	}

	public List<String> getConstraintNames(ChoiceNode choiceNode) {

		List<String> choiceNames = fMapOfUsages.get(choiceNode);

		return choiceNames;
	}

	public List<String> getConstraintNames(String choiceName) {

		for (ChoiceNode choiceNode : fMapOfUsages.keySet()) {

			if (StringHelper.isEqual(choiceNode.getQualifiedName(), choiceName)) {

				List<String> choiceNames = fMapOfUsages.get(choiceNode);
				return choiceNames;
			}
		}

		return null;
	}

	public boolean choiceNameExists(String choiceName) {

		for (ChoiceNode choiceNode : fMapOfUsages.keySet()) {
		    
			String currentChoiceName = choiceNode.getQualifiedName();

			if (StringHelper.isEqual(currentChoiceName, choiceName)) {
				return true;
			}
		}
        	
		return false;
	}

	private void updateMapOfUsages(Constraint constraint, List<ChoiceNode> choiceNodesUsedInConstraint) {

		for (ChoiceNode choiceNode : choiceNodesUsedInConstraint) {

			if (fMapOfUsages.containsKey(choiceNode)) {

				updateExistingElement(choiceNode, constraint);
				return;
			}

			addNewElement(choiceNode, constraint);
		}

	}

	private void addNewElement(ChoiceNode choiceNode, Constraint constraint) {

		List<String> constraintNames = new ArrayList<>();
		constraintNames.add(constraint.getName());

		fMapOfUsages.put(choiceNode, constraintNames);
	}


	private void updateExistingElement(ChoiceNode choiceNode, Constraint constraint) {

		List<String> constraintNames = fMapOfUsages.get(choiceNode);

		String constraintName = constraint.getName();

		if (constraintNames.contains(constraintName)) {
			return;
		}

		constraintNames.add(constraintName);
	}


}
