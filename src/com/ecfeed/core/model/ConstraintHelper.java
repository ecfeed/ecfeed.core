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

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.StringHelper;

public class ConstraintHelper {

	public static String createSignature(Constraint constraint, IExtLanguageManager extLanguageManager) {

		if (constraint == null) {
			return "EMPTY";
		}

		String name = constraint.getName();

		String signature2 = constraint.createSignature(extLanguageManager);
		return name + ": " + signature2;
	}

	public static List<String> createListOfConstraintNames(List<Constraint> constraints) {

		List<String> constraintNames = new ArrayList<>();

		for (IConstraint<ChoiceNode> iConstraint : constraints) {

			if (iConstraint instanceof Constraint) {

				Constraint constraint = (Constraint)iConstraint;
				constraintNames.add(constraint.getName());
			}
		}
		return constraintNames;
	}

	public static boolean containsConstraints(List<Constraint> iConstraints) {

		for (IConstraint<ChoiceNode> iConstraint : iConstraints) {

			if (iConstraint instanceof Constraint) {
				return true;
			}
		}

		return false;
	}

	public static Constraint getFirstAmbiguousConstraint(
			List<Constraint> constraints, 
			List<List<ChoiceNode>> input,
			IExtLanguageManager currentExtLanguageManager,
			MessageStack inOutMessageStack) {

		for (Constraint constraint : constraints) {

			if (constraint.isAmbiguous(input, inOutMessageStack, currentExtLanguageManager)) {

				return constraint; 
			}
		}

		return null;
	}

	public static List<ChoiceNode> getChoicesUsedInConstraints(
			Constraint constraint, 
			MethodParameterNode methodParameterNode) {

		List<ChoiceNode> result = new ArrayList<>();

		AbstractStatement precondition = constraint.getPrecondition();
		List<ChoiceNode> choicesFromPrecondition = precondition.getChoices();

		List<ChoiceNode> choicesOfParameter = filterChoicesByParameter(choicesFromPrecondition, methodParameterNode);
		result.addAll(choicesOfParameter);

		AbstractStatement postcondition = constraint.getPostcondition();
		List<ChoiceNode> choicesFromPostcondition = postcondition.getChoices();

		choicesOfParameter = filterChoicesByParameter(choicesFromPostcondition, methodParameterNode);
		result.addAll(choicesOfParameter);

		result = ChoiceNodeHelper.removeDuplicates(result);

		return result;
	}

	public static List<String> getLabelsUsedInConstraints(Constraint constraint,
			MethodParameterNode methodParameterNode) {

		List<String> result = new ArrayList<>();

		AbstractStatement precondition = constraint.getPrecondition();
		List<String> labelsFromPrecondition = precondition.getLabels(methodParameterNode);
		
		if (labelsFromPrecondition != null) {
			result.addAll(labelsFromPrecondition);
		}

		AbstractStatement postcondition = constraint.getPostcondition();
		List<String> labelsFromPostcondition = postcondition.getLabels(methodParameterNode);
		
		if (labelsFromPostcondition != null) {
			result.addAll(labelsFromPostcondition);
		}

		result = StringHelper.removeDuplicates(result);

		return result;
	}

	private static List<ChoiceNode> filterChoicesByParameter(
			List<ChoiceNode> choicesFromPrecondition,
			MethodParameterNode methodParameterNode) {

		List<ChoiceNode> result = new ArrayList<>();

		AbstractParameterNode abstractParameterNodeForComparison = 
				getParameterNodeForComparison(methodParameterNode);

		for (ChoiceNode choiceNode : choicesFromPrecondition) {

			AbstractParameterNode abstractParameterNode = choiceNode.getParameter();

			if (abstractParameterNodeForComparison.equals(abstractParameterNode)) {
				result.add(choiceNode);
			}
		}

		return result;
	}

	private static AbstractParameterNode getParameterNodeForComparison(MethodParameterNode methodParameterNode) {

		if (methodParameterNode.isLinked()) {
			return methodParameterNode.getLink();
		} else {
			return methodParameterNode;
		}
	}

}