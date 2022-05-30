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
import java.util.HashSet;
import java.util.List;

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;

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
			MethodParameterNode methodParameterNode,
			List<ChoiceNode> inOutChoiceNodes) {

		AbstractStatement precondition = constraint.getPrecondition();
		AbstractStatement postcondition = constraint.getPostcondition();

		List<ChoiceNode> choicesFromPrecondition = precondition.getListOfChoices();
		addChoicesOfParameter(choicesFromPrecondition, methodParameterNode, inOutChoiceNodes);


		List<ChoiceNode> choicesFromPostcondition = postcondition.getListOfChoices();
		addChoicesOfParameter(choicesFromPostcondition, methodParameterNode, inOutChoiceNodes);

		List<ChoiceNode> result = removeDuplicates(inOutChoiceNodes);
		return result;
	}

	private static void addChoicesOfParameter(List<ChoiceNode> choicesFromPrecondition,
			MethodParameterNode methodParameterNode, List<ChoiceNode> inOutChoiceNodes) {
		
		AbstractParameterNode abstractParameterNodeForComparison = 
				getParameterNodeForComparison(methodParameterNode);
		
		for (ChoiceNode choiceNode : choicesFromPrecondition) {

			AbstractParameterNode abstractParameterNode = choiceNode.getParameter();

			if (abstractParameterNodeForComparison.equals(abstractParameterNode)) {
				inOutChoiceNodes.add(choiceNode);
			}
		}
	}

	private static AbstractParameterNode getParameterNodeForComparison(MethodParameterNode methodParameterNode) {
		
		if (methodParameterNode.isLinked()) {
			return methodParameterNode.getLink();
		} else {
			return methodParameterNode;
		}
	}

	private static List<ChoiceNode> removeDuplicates(List<ChoiceNode> choiceNodes) {

		HashSet<ChoiceNode>set = new HashSet<ChoiceNode>(choiceNodes);
		List<ChoiceNode> choiceNodes2 = new ArrayList<ChoiceNode>(set);

		return choiceNodes2;
	}

}