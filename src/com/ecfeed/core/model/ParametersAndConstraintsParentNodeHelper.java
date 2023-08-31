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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.utils.BasicParameterWithChoice;
import com.ecfeed.core.utils.*;

public class ParametersAndConstraintsParentNodeHelper {

	public static String getParameterName(String parameterExtendedName) {

		String[] dstParamNameParts = StringHelper.splitIntoTokens(parameterExtendedName, ":");

		if (dstParamNameParts.length == 2) {
			return dstParamNameParts[1]; 
		}

		return dstParamNameParts[0];
	}

	//	public static Collection<ConstraintNode> getAffectedConstraints(
	//			CompositeParameterNode methodParameter) {
	//
	//		Set<ConstraintNode> constraints = new HashSet<>();
	//
	//		IAbstractNode container = methodParameter.getParent();
	//
	//		while ((container != null) && (container instanceof IParametersAndConstraintsParentNode)) {
	//
	//			constraints.addAll(((IParametersAndConstraintsParentNode) container).getConstraintNodes());
	//			container = container.getParent();
	//		}
	//
	//		return constraints;
	//	}

	public static Collection<ChoiceNode> getChoicesUsedInConstraints(
			CompositeParameterNode methodParameter, Collection<ConstraintNode> constraints) {

		Set<ChoiceNode> choices = new HashSet<>();

		for (BasicParameterNode parameterBasic : methodParameter.getNestedBasicParameters(true)) {
			for (ConstraintNode constraint : constraints) {

				choices.addAll(ConstraintNodeHelper.getChoicesUsedInConstraint(constraint, parameterBasic));
			}
		}

		return choices;
	}

	public static Collection<BasicParameterNode> getParametersUsedInConstraints(
			CompositeParameterNode methodParameter, Collection<ConstraintNode> constraints) {

		Set<BasicParameterNode> parameters = new HashSet<>();

		List<BasicParameterNode> parametersNested = methodParameter.getNestedBasicParameters(true);

		for (ConstraintNode constraint : constraints) {
			Set<BasicParameterNode> parametersConstraint = constraint.getConstraint().getReferencedParameters();

			for (BasicParameterNode parameterBasic : parametersNested) {

				if (parametersConstraint.contains(parameterBasic)) {
					parameters.add(parameterBasic);
				}
			}
		}

		return parameters;
	}

	public static Collection<String> getLabelsUsedInConstraints(
			CompositeParameterNode methodParameter, Collection<ConstraintNode> constraints) {

		Set<String> labels = new HashSet<>();

		for (BasicParameterNode parameterBasic : methodParameter.getNestedBasicParameters(true)) {
			for (ConstraintNode constraint : constraints) {

				labels.addAll(ConstraintNodeHelper.getLabelsUsedInConstraint(constraint, parameterBasic));
			}
		}

		return labels;
	}

	public static List<BasicParameterWithChoice> getParametersWithChoicesUsedInConstraintsForLocalTopParameter( // TODO test
			AbstractParameterNode localTopParameterNode) {

		IParametersAndConstraintsParentNode parent =
				(IParametersAndConstraintsParentNode) localTopParameterNode.getParent();

		if (!(parent instanceof MethodNode)) {
			ExceptionHelper.reportRuntimeException("Invalid position of parameter - top parameter expected.");
		}

		MethodNode parentMethodNode = (MethodNode)parent;

		Set<BasicParameterWithChoice> resultSet = new HashSet<BasicParameterWithChoice>();

		List<BasicParameterNode> basicParameterNodes = getBasicChildParameterNodes(localTopParameterNode);

		List<ConstraintNode> constraintNodes = MethodNodeHelper.getChildConstraintNodes(parentMethodNode);

		for (ConstraintNode constraintNode : constraintNodes) {

			Set<BasicParameterWithChoice> resultForOneConstraint = 
					getParametersWithChoicesUsedInOneConstraint(constraintNode, basicParameterNodes);

			resultSet.addAll(resultForOneConstraint);
		}

		return new ArrayList<>(resultSet);
	}

	private static Set<BasicParameterWithChoice> getParametersWithChoicesUsedInOneConstraint(
			ConstraintNode constraintNode, List<BasicParameterNode> basicParameterNodes) {

		Set<BasicParameterWithChoice> result = new HashSet<>();

		for (int parameterIndex = 0; parameterIndex < basicParameterNodes.size(); parameterIndex++) {

			BasicParameterNode basicParameterNode = basicParameterNodes.get(parameterIndex);

			Set<BasicParameterWithChoice> resultForOneParameter = 
					getParameterWithChoicesUsedInConstraint(constraintNode, basicParameterNode);			

			result.addAll(resultForOneParameter);
		}

		return result; 
	}

	private static Set<BasicParameterWithChoice> getParameterWithChoicesUsedInConstraint(
			ConstraintNode constraintNode,
			BasicParameterNode basicParameterNode) {

		List<ChoiceNode> choiceNodesForConstraint = 
				ConstraintNodeHelper.getChoicesUsedInConstraint(
						constraintNode, basicParameterNode);

		Set<BasicParameterWithChoice> result = new HashSet<>();

		for (int choiceIndex = 0; choiceIndex < choiceNodesForConstraint.size(); choiceIndex++) {

			ChoiceNode choiceNode = choiceNodesForConstraint.get(choiceIndex); 

			result.add(new BasicParameterWithChoice(basicParameterNode, choiceNode));
		}

		return result;
	}

	private static List<BasicParameterNode> getBasicChildParameterNodes(AbstractParameterNode localTopParameterNode) { // XYX TO HELPER

		if (localTopParameterNode instanceof BasicParameterNode) {

			List<BasicParameterNode> result = new ArrayList<>();
			result.add((BasicParameterNode) localTopParameterNode);

			return result;
		}

		CompositeParameterNode topCompositeParameterNode = (CompositeParameterNode) localTopParameterNode;

		List<BasicParameterNode> result = 
				BasicParameterNodeHelper.getBasicParametersForParentNodeSubtree(topCompositeParameterNode);

		return result;
	}

	public static List<ChoiceNode> getChoicesUsedInConstraints(BasicParameterNode methodParameterNode) { // XYX obsolete ?

		List<ChoiceNode> resultChoiceNodes = new ArrayList<ChoiceNode>();

		IParametersAndConstraintsParentNode parametersAndConstraintsParentNode = 
				(IParametersAndConstraintsParentNode) methodParameterNode.getParent();

		List<ConstraintNode> constraintNodes = parametersAndConstraintsParentNode.getConstraintNodes();

		for (ConstraintNode constraintNode : constraintNodes) {

			List<ChoiceNode> choiceNodesForConstraint = 
					ConstraintNodeHelper.getChoicesUsedInConstraint(
							constraintNode, methodParameterNode);

			resultChoiceNodes.addAll(choiceNodesForConstraint);
		}

		resultChoiceNodes = ChoiceNodeHelper.removeDuplicates(resultChoiceNodes);

		return resultChoiceNodes;
	}

	public static List<String> getLabelsUsedInConstraints(BasicParameterNode methodParameterNode) {

		List<String> resultLabels = new ArrayList<>();

		IParametersAndConstraintsParentNode parametersAndConstraintsParentNode = 
				(IParametersAndConstraintsParentNode) methodParameterNode.getParent();

		List<ConstraintNode> constraintNodes = parametersAndConstraintsParentNode.getConstraintNodes();

		for (ConstraintNode constraintNode : constraintNodes) {

			List<String> labelsOfConstraint = 
					ConstraintNodeHelper.getLabelsUsedInConstraint(
							constraintNode, methodParameterNode);

			resultLabels.addAll(labelsOfConstraint);

			resultLabels = StringHelper.removeDuplicates(resultLabels);
		}

		return resultLabels;
	}

	public static void convertConstraints(
			List<ConstraintNode> constraintNodes,
			ParameterConversionItem parameterConversionItem) {

		for (ConstraintNode constraintNode : constraintNodes) {
			ConstraintNodeHelper.convertConstraint(
					constraintNode, 
					parameterConversionItem);
		}
	}

	public static BasicParameterNode addBasicParameterToParent(
			IParametersParentNode parametersParentNode, String name, String type) {

		BasicParameterNode methodParameterNode = new BasicParameterNode(name, type, "0", false, null);

		parametersParentNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

	public static BasicParameterNode addGlobalParameterToParent(
			IParametersParentNode parametersParentNode, String name, String type) {

		BasicParameterNode methodParameterNode = new BasicParameterNode(name, type, null, false, null);

		parametersParentNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

	public static CompositeParameterNode addNewCompositeParameter(
			IParametersParentNode parentNode, 
			String compositeParameterName) {

		CompositeParameterNode compositeMethodParameterNode2 = 
				new CompositeParameterNode(compositeParameterName, null);

		parentNode.addParameter(compositeMethodParameterNode2);

		return compositeMethodParameterNode2;
	}

	public static BasicParameterNode addLinkedParameter(
			IParametersParentNode parametersParentNode, 
			String name, 
			String type, 
			AbstractParameterNode linkToGlobalParameter) {

		BasicParameterNode methodParameterNode = new BasicParameterNode(name, type, "0", false, null);
		methodParameterNode.setLinkToGlobalParameter(linkToGlobalParameter);
		parametersParentNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

	public static BasicParameterNode addExpectedParameterToMethod(
			IParametersParentNode parametersParentNode, String name, String type, String defaultValue) {

		BasicParameterNode methodParameterNode = new BasicParameterNode(name, type, defaultValue, true, null);
		parametersParentNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

	public static AbstractParameterNode createParameter(
			IParametersParentNode parametersParentNode,
			AbstractParameterNode.ParameterType parameterType,
			IExtLanguageManager extLanguageManager) {

		String name = ParametersAndConstraintsParentNodeHelper.generateNewParameterName(parametersParentNode);

		IModelChangeRegistrator modelChangeRegistrator = parametersParentNode.getModelChangeRegistrator();

		if (parameterType == AbstractParameterNode.ParameterType.COMPOSITE) {

			CompositeParameterNode compositeParameterNode =
					new CompositeParameterNode(name, modelChangeRegistrator);

			return compositeParameterNode;
		}


		String type = JavaLanguageHelper.TYPE_NAME_STRING;

		String defaultValue = JavaLanguageHelper.getDefaultValue(type);

		BasicParameterNode parameter =
				new BasicParameterNode(name, type, defaultValue, false, modelChangeRegistrator);

		return parameter;
	}

	public static BasicParameterNode createBasicParameter(
			IParametersParentNode parametersParentNode, IExtLanguageManager extLanguageManager) {

		BasicParameterNode basicParameterNode =
				(BasicParameterNode) createParameter(
						parametersParentNode,
						AbstractParameterNode.ParameterType.BASIC,
						extLanguageManager);

		return basicParameterNode;
	}

	public static CompositeParameterNode createNewCompositeParameter(
			IParametersParentNode parametersParentNode, IExtLanguageManager extLanguageManager) {

		String name = ParametersAndConstraintsParentNodeHelper.generateNewParameterName(parametersParentNode);

		CompositeParameterNode parameter =
				new CompositeParameterNode(name, parametersParentNode.getModelChangeRegistrator());

		return parameter;
	}

	public static String generateNewParameterName(IParametersParentNode parametersParentNode) {

		int i = 1;

		String name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;

		while(parametersParentNode.findParameter(name) != null) {
			name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}

		return name;
	}

	public static String generateNewCompositeParameterName(IParametersParentNode parametersParentNode) {

		int i = 1;

		String name = CommonConstants.DEFAULT_NEW_COMPOSITE_PARAMETER_NAME + i++;

		while(parametersParentNode.findParameter(name) != null) {
			name = CommonConstants.DEFAULT_NEW_COMPOSITE_PARAMETER_NAME + i++;
		}

		return name;
	}

	public static BasicParameterNode findExpectedParameterNotUsedInAssignment(
			IParametersAndConstraintsParentNode parametersAndConstraintsParentNode, 
			Constraint constraint) {

		if (constraint.getType() != ConstraintType.ASSIGNMENT) {
			return null;
		}

		AbstractStatement postcondition = constraint.getPostcondition();

		if (!(postcondition instanceof StatementArray)) {
			return null;
		}

		StatementArray statementArray = (StatementArray)postcondition;

		BasicParameterNode parameterNode = findNotUsedExpectedParameter(parametersAndConstraintsParentNode, statementArray);
		return parameterNode;
	}

	public static BasicParameterNode findNotUsedExpectedParameter(
			IParametersAndConstraintsParentNode parametersAndConstraintsParentNode, StatementArray statementArray) {

		List<AbstractParameterNode> parameters = parametersAndConstraintsParentNode.getParameters();

		for (AbstractParameterNode abstractParameterNode : parameters) {

			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				continue;
			}

			BasicParameterNode basicParameterNode = (BasicParameterNode)abstractParameterNode;

			if (!basicParameterNode.isExpected()) {
				continue;
			}

			if (!isParameterUsedInAssignment(basicParameterNode, statementArray)) {
				return basicParameterNode;
			}
		}
		return null;
	}

	public static boolean isParameterUsedInAssignment(BasicParameterNode parameterNode, StatementArray statementArray) {

		List<AbstractStatement> statements = statementArray.getStatements();

		for (AbstractStatement abstractStatement : statements) {

			if (!(abstractStatement instanceof AssignmentStatement)) {
				continue;
			}

			AssignmentStatement assignmentStatement = (AssignmentStatement)abstractStatement;

			BasicParameterNode leftParameter = assignmentStatement.getLeftParameter();

			if (leftParameter == parameterNode) {
				return true;
			}

		}

		return false;
	}

	public static List<String> getStatementValuesForParameter(
			IParametersAndConstraintsParentNode parametersAndConstraintsParentNode,
			BasicParameterNode methodParameterNode) {

		List<Constraint> constraints = parametersAndConstraintsParentNode.getConstraints();

		List<String> values = new ArrayList<>();

		for (Constraint constraint : constraints) {

			List<String> valuesOfConstraint = constraint.getStatementValuesForParameter(); 

			if (valuesOfConstraint != null && !valuesOfConstraint.isEmpty()) {
				values.addAll(valuesOfConstraint);
			}
		}

		values = StringHelper.removeDuplicates(values);

		return values;
	}

}
