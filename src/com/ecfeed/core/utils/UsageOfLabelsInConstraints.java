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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.model.IConstraintsParentNode;

public class UsageOfLabelsInConstraints {

	private Map<String /* label */, ListOfStrings /* names of constraints */> fMapOfUsages;

	@Override
	public String toString() {
		return fMapOfUsages.toString();
	}

	public UsageOfLabelsInConstraints(AbstractParameterNode abstractParameterNode) {

		IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) abstractParameterNode.getParent();

		List<ConstraintNode> constraintNodes = 
				ConstraintsParentNodeHelper.findChildConstraints(constraintsParentNode);

		List<BasicParameterNode> basicParameterNodes = 
				BasicParameterNodeHelper.getBasicChildParameterNodes(abstractParameterNode);

		fMapOfUsages = createMapOfUsages(constraintNodes, basicParameterNodes);
	}

	private Map<String, ListOfStrings> createMapOfUsages
	(List<ConstraintNode> constraintNodes,
			List<BasicParameterNode> basicParameterNodes) {

		Map<String, ListOfStrings> mapOfUsages = new HashMap<>();

		for (ConstraintNode constraintNode : constraintNodes) {

			Constraint constraint = constraintNode.getConstraint();

			for (BasicParameterNode basicParameterNode : basicParameterNodes) {

				List<String> choiceNodesUsedInConstraint = constraint.getLabels(basicParameterNode);

				updateMapOfUsages(constraint, choiceNodesUsedInConstraint);
			}
		}

		return mapOfUsages;
	}

	public ListOfStrings getConstraintNames(String label) {

		ListOfStrings constraintNames = fMapOfUsages.get(label);

		return constraintNames;
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

		ListOfStrings labels = new ListOfStrings();
		labels.add(constraint.getName());

		fMapOfUsages.put(label, labels);
	}


	private void updateExistingElement(String label, Constraint constraint) {

		ListOfStrings constraintNames = fMapOfUsages.get(label);

		String constraintName = constraint.getName();

		if (constraintNames.contains(constraintName)) {
			return;
		}

		constraintNames.add(constraintName);
	}


}
