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

import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;

public class UsageOfLabelsInConstraints {

	private Map<String, List<String>> fMapOfUsages;


	public UsageOfLabelsInConstraints(MethodParameterNode methodParameterNode) {

		MethodNode methodNode = methodParameterNode.getMethod();

		fMapOfUsages = new HashMap<>();

		List<Constraint> constraints = methodNode.getAllConstraints();

		for (Constraint constraint : constraints) {

			List<String> choiceNodesUsedInConstraint = constraint.getLabels(methodParameterNode);

			updateMapOfUsages(constraint, choiceNodesUsedInConstraint);
		}
	}

	public List<String> getConstraintNames(String label) {

		for (String tmpLabel : fMapOfUsages.keySet()) {

			if (StringHelper.isEqual(label, tmpLabel)) {

				List<String> choiceNames = fMapOfUsages.get(label);
				return choiceNames;
			}
		}

		return null;
	}

	public boolean choiceNameExists(String choiceName) {

		for (String tmpLabel : fMapOfUsages.keySet()) {

			if (StringHelper.isEqual(tmpLabel, choiceName)) {
				return true;
			}
		}

		return false;
	}

	private void updateMapOfUsages(Constraint constraint, List<String> labelsUsedInConstraint) {

		for (String label : labelsUsedInConstraint) {

			if (fMapOfUsages.containsKey(label)) {

				updateExistingElement(label, constraint);
				return;
			}

			addNewElement(label, constraint);
		}
	}

	private void addNewElement(String label, Constraint constraint) {

		List<String> labels = new ArrayList<>();
		labels.add(constraint.getName());

		fMapOfUsages.put(label, labels);
	}


	private void updateExistingElement(String label, Constraint constraint) {

		List<String> constraintNames = fMapOfUsages.get(label);

		String constraintName = constraint.getName();

		if (constraintNames.contains(constraintName)) {
			return;
		}

		constraintNames.add(constraintName);
	}


}
