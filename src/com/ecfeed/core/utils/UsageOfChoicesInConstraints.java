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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.model.IConstraintsParentNode;
import com.ecfeed.core.model.utils.BasicParameterWithChoice;

public class UsageOfChoicesInConstraints {

	private Map<BasicParameterWithChoice, ListOfStrings /* names of constraints */> fMapOfUsages;

	public UsageOfChoicesInConstraints(AbstractParameterNode abstractParameterNode) {

		IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) abstractParameterNode.getParent();

		List<ConstraintNode> constraintNodes = 
				ConstraintsParentNodeHelper.findChildConstraints(constraintsParentNode);

		List<BasicParameterNode> basicParameterNodes = 
				BasicParameterNodeHelper.getBasicChildParameterNodes(abstractParameterNode);

		fMapOfUsages = createMapOfUsages(constraintNodes, basicParameterNodes);
	}

	@Override
	public String toString() {
		return fMapOfUsages.toString();
	}

	private static Map<BasicParameterWithChoice, ListOfStrings> createMapOfUsages(
			List<ConstraintNode> constraintNodes,
			List<BasicParameterNode> basicParameterNodes) {

		Map<BasicParameterWithChoice, ListOfStrings> mapOfUsages = new HashMap<>();

		for (ConstraintNode constraintNode : constraintNodes) {

			Constraint constraint = constraintNode.getConstraint();

			for (BasicParameterNode basicParameterNode : basicParameterNodes) {

				List<BasicParameterWithChoice> itemsUsedInConstraint = 
						getParametersWithChoicesUsedInConstraint(
								constraint, basicParameterNode);

				updateMapOfUsages(constraint, itemsUsedInConstraint, mapOfUsages);
			}
		}

		return mapOfUsages;
	}

	private static List<BasicParameterWithChoice> getParametersWithChoicesUsedInConstraint(
			Constraint constraint,
			BasicParameterNode abstractParameterNode) {

		List<BasicParameterWithChoice> result = new ArrayList<>();

		List<ChoiceNode> choiceNodesUsedInConstraint = constraint.getChoices(abstractParameterNode);

		for (ChoiceNode choiceNode : choiceNodesUsedInConstraint) {

			BasicParameterWithChoice basicParameterWithChoice = 
					new BasicParameterWithChoice(abstractParameterNode, choiceNode);

			result.add(basicParameterWithChoice);
		}

		return result;
	}

	public ListOfStrings getConstraintNames(BasicParameterWithChoice basicParameterWithChoice) {

		ListOfStrings constraintNames = fMapOfUsages.get(basicParameterWithChoice);

		return constraintNames;
	}

	public ListOfStrings getConstraintNames(String parameterName, String choiceName) {

		for (BasicParameterWithChoice basicParameterWithChoice : fMapOfUsages.keySet()) {

			String currentParameterName = basicParameterWithChoice.getBasicParameterNode().getName();

			if (!StringHelper.isEqual(currentParameterName, parameterName)) {
				continue;
			}

			String currentChoiceName = basicParameterWithChoice.getChoiceNode().getQualifiedName();

			if (!StringHelper.isEqual(currentChoiceName, choiceName)) {
				continue;
			}

			ListOfStrings choiceNames = fMapOfUsages.get(basicParameterWithChoice);
			return choiceNames;
		}

		return null;
	}

	public boolean choiceNameExists(String parameterName, String choiceName) {

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

	private static void updateMapOfUsages(
			Constraint constraint, 
			List<BasicParameterWithChoice> itemsUsedInConstraint,
			Map<BasicParameterWithChoice, ListOfStrings> inOutMapOfUsages) {

		for (BasicParameterWithChoice basicParameterWithChoice : itemsUsedInConstraint) {

			if (inOutMapOfUsages.containsKey(basicParameterWithChoice)) {

				updateExistingElement(basicParameterWithChoice, constraint, inOutMapOfUsages);
				return;
			}

			addNewElement(basicParameterWithChoice, constraint, inOutMapOfUsages);
		}
	}

	private static void addNewElement(
			BasicParameterWithChoice basicParameterWithChoice, 
			Constraint constraint,
			Map<BasicParameterWithChoice, ListOfStrings> inOutMapOfUsages) {

		ListOfStrings constraintNames = new ListOfStrings();
		constraintNames.add(constraint.getName());

		inOutMapOfUsages.put(basicParameterWithChoice, constraintNames);
	}


	private static void updateExistingElement(
			BasicParameterWithChoice basicParameterWithChoice, 
			Constraint constraint,
			Map<BasicParameterWithChoice, ListOfStrings> inOutMapOfUsages) {

		ListOfStrings constraintNames = inOutMapOfUsages.get(basicParameterWithChoice);

		String constraintName = constraint.getName();

		if (constraintNames.contains(constraintName)) {
			return;
		}

		constraintNames.add(constraintName);
	}

}
