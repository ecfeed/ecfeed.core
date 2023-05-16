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

import java.util.*;

import com.ecfeed.core.utils.*;

public class ConstraintHelper {

	public static void verifyConversionOfConstraints(
			BasicParameterNode basicParameterNode, 
			String newType,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		IConstraintsParentNode constraintParentNode = (IConstraintsParentNode) basicParameterNode.getParent();

		String oldType = basicParameterNode.getType();

		List<Constraint> constraints = constraintParentNode.getConstraints();

		for (Constraint constraint : constraints) {

			constraint.verifyConversionOfParameterFromToType(
					basicParameterNode, oldType, newType, inOutParameterConversionDefinition);
		}
	}

	public static void convertValuesOfConstraintsToType(
			BasicParameterNode basicParameterNode, 
			ParameterConversionDefinition parameterConversionDefinition) {

		IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) basicParameterNode.getParent();

		List<Constraint> constraints = constraintsParentNode.getConstraints();

		for (Constraint constraint : constraints) {

			constraint.convertValues(basicParameterNode, parameterConversionDefinition);
		}
	}

	public static Map<Integer, String> getOriginalConstraintValues(IConstraintsParentNode parametersParentNode) {

		Map<Integer, String> resultValues = new HashMap<>();

		List<Constraint> constraints = parametersParentNode.getConstraints();

		for (Constraint constraint : constraints) {

			constraint.saveValues(resultValues);
		}

		return resultValues;
	}

	public static void restoreOriginalConstraintValues(
			IConstraintsParentNode constraintsParentNode,
			Map<Integer, String> originalValues) {

		List<Constraint> constraints = constraintsParentNode.getConstraints();

		for (Constraint constraint : constraints) {

			constraint.restoreValues(originalValues);
		}
	}

	public static String createSignatureOfConditions(Constraint constraint, IExtLanguageManager extLanguageManager) {

		if (constraint == null) {
			return "EMPTY";
		}

		String postconditionSignature = 
				AbstractStatementHelper.createSignature(constraint.getPostcondition(), extLanguageManager);

		if (constraint.getType() == ConstraintType.BASIC_FILTER) {
			return postconditionSignature;
		}

		String preconditionSignature = 
				AbstractStatementHelper.createSignature(constraint.getPrecondition(), extLanguageManager);

		return preconditionSignature + " => " + postconditionSignature;
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
			BasicParameterNode methodParameterNode) {

		List<ChoiceNode> result = new ArrayList<>();

		AbstractStatement precondition = constraint.getPrecondition();
		List<ChoiceNode> choicesFromPrecondition = precondition.getChoices();

		if (choicesFromPrecondition != null) {
			List<ChoiceNode> choicesOfParameter = filterChoicesByParameter(choicesFromPrecondition, methodParameterNode);
			result.addAll(choicesOfParameter);
		}

		AbstractStatement postcondition = constraint.getPostcondition();
		List<ChoiceNode> choicesFromPostcondition = postcondition.getChoices();

		if (choicesFromPostcondition != null) {
			List<ChoiceNode> choicesOfParameter = filterChoicesByParameter(choicesFromPostcondition, methodParameterNode);
			result.addAll(choicesOfParameter);
		}

		result = ChoiceNodeHelper.removeDuplicates(result);

		return result;
	}

	public static List<String> getLabelsUsedInConstraints(Constraint constraint,
			BasicParameterNode methodParameterNode) {

		List<String> result = new ArrayList<>();

		AbstractStatement precondition = constraint.getPrecondition();
		List<String> labelsFromPrecondition = precondition.getLabels(methodParameterNode);

		if (labelsFromPrecondition != null && labelsFromPrecondition.size() > 0) {
			result.addAll(labelsFromPrecondition);
		}

		AbstractStatement postcondition = constraint.getPostcondition();
		List<String> labelsFromPostcondition = postcondition.getLabels(methodParameterNode);

		if (labelsFromPostcondition != null && labelsFromPostcondition.size() > 0) {
			result.addAll(labelsFromPostcondition);
		}

		result = StringHelper.removeDuplicates(result);

		return result;
	}

	private static List<ChoiceNode> filterChoicesByParameter(
			List<ChoiceNode> choicesFromPrecondition,
			BasicParameterNode methodParameterNode) {

		List<ChoiceNode> result = new ArrayList<>();

		BasicParameterNode abstractParameterNodeForComparison = 
				getParameterNodeForComparison(methodParameterNode);

		for (ChoiceNode choiceNode : choicesFromPrecondition) {

			BasicParameterNode abstractParameterNode = choiceNode.getParameter();

			if (abstractParameterNodeForComparison.equals(abstractParameterNode)) {
				result.add(choiceNode);
			}
		}

		return result;
	}

	private static BasicParameterNode getParameterNodeForComparison(BasicParameterNode basicParameterNode) {

		if (basicParameterNode.isLinked()) {
			return (BasicParameterNode) basicParameterNode.getLinkToGlobalParameter();
		} else {
			return basicParameterNode;
		}
	}

	public static Set<MethodNode> getMethods(Constraint constraint) {
		Constraint.CollectingMethodVisitor visitor = new Constraint.CollectingMethodVisitor();

		try {
			constraint.getPrecondition().accept(visitor);
			constraint.getPostcondition().accept(visitor);
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Something is wrong");
		}

		return visitor.getMethods();
	}

	public static void compareConstraints(Constraint constraint1, Constraint constraint2) {

		if (constraint1.getType() != constraint2.getType()) {
			ExceptionHelper.reportRuntimeException("Constraint types different.");
		}

		AbstractStatementHelper.compareStatements(constraint1.getPrecondition(), constraint2.getPrecondition());
		AbstractStatementHelper.compareStatements(constraint1.getPostcondition(), constraint2.getPostcondition());
	}
}