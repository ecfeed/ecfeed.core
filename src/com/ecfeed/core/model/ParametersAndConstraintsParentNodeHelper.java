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

import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

public class ParametersAndConstraintsParentNodeHelper {

	public static String getParameterName(String parameterExtendedName) {

		String[] dstParamNameParts = StringHelper.splitIntoTokens(parameterExtendedName, ":");

		if (dstParamNameParts.length == 2) {
			return dstParamNameParts[1]; 
		}

		return dstParamNameParts[0];
	}

	public static List<ChoiceNode> getChoicesUsedInConstraints(BasicParameterNode methodParameterNode) {

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

	public static BasicParameterNode addParameterToMethod(
			IParametersParentNode parametersParentNode, String name, String type) {

		BasicParameterNode methodParameterNode = new BasicParameterNode(name, type, "0", false, null);
		parametersParentNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

	public static BasicParameterNode addLinkedParameterToMethod(
			IParametersParentNode parametersParentNode, String name, String type, BasicParameterNode linkToGlobalParameter) {

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

		// TODO MO-RE divide into composite parameter helper and method node helper ? or rename method node helper
		String name = ParametersAndConstraintsParentNodeHelper.generateNewParameterName(parametersParentNode);

		IModelChangeRegistrator modelChangeRegistrator = parametersParentNode.getModelChangeRegistrator();

		if (parameterType == AbstractParameterNode.ParameterType.COMPOSITE) {

			CompositeParameterNode compositeParameterNode =
					new CompositeParameterNode(name, modelChangeRegistrator);

			return compositeParameterNode;
		}

		if (parametersParentNode instanceof MethodNode) {

			MethodNode methodNode = (MethodNode) parametersParentNode;

			String type = MethodNodeHelper.findNotUsedJavaTypeForParameter(
					methodNode, extLanguageManager);

			String defaultValue = JavaLanguageHelper.getDefaultValue(type);

			BasicParameterNode parameter =
					new BasicParameterNode(name, type, defaultValue, false, modelChangeRegistrator);

			return parameter;
		}

		if (parametersParentNode instanceof CompositeParameterNode) {

			String type = null;

			if (extLanguageManager instanceof ExtLanguageManagerForJava) {  // TODO MO-RE move to extManagers
				type = "int";
			} else {
				type = "Number";
			}

			String defaultValue = JavaLanguageHelper.getDefaultValue(type);

			BasicParameterNode parameter =
					new BasicParameterNode(name, type, defaultValue, false, modelChangeRegistrator);

			return parameter;
		}


		ExceptionHelper.reportRuntimeException("Not supported parameter type.");
		return null;
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

		int i = 0;

		String name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;

		while(parametersParentNode.findParameter(name) != null) {
			name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;
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

	public static BasicParameterNode findBasicParameterByQualifiedName( // TODO MO-RE move to helper
			String parameterNameToFindInExtLanguage, 
			IParametersParentNode parametersParentNode,
			IExtLanguageManager extLanguageManager) {
		
		String parameterNameToFindInIntrLanguage = 
				extLanguageManager.convertTextFromExtToIntrLanguage(parameterNameToFindInExtLanguage);
		
		return findParameterByQualifiedNameRecursive(
				parameterNameToFindInIntrLanguage, parametersParentNode,	extLanguageManager);
	}

	private static BasicParameterNode findParameterByQualifiedNameRecursive(
			String parameterNameToFindInIntrLanguage,
			IParametersParentNode parametersParentNode, 
			IExtLanguageManager extLanguageManager) {
		
		String firstToken = 
				StringHelper.getFirstToken(parameterNameToFindInIntrLanguage, SignatureHelper.SIGNATURE_NAME_SEPARATOR);
		
		if (firstToken == null) {
			AbstractParameterNode abstractParameterNode = 
					parametersParentNode.findParameter(parameterNameToFindInIntrLanguage);
			
			return (BasicParameterNode) abstractParameterNode;
		}
		
		String remainingPart = 
				StringHelper.removeToPrefix(SignatureHelper.SIGNATURE_NAME_SEPARATOR, parameterNameToFindInIntrLanguage);
		
		CompositeParameterNode compositeParameterNode = 
				(CompositeParameterNode) parametersParentNode.findParameter(firstToken);
		
		return findParameterByQualifiedNameRecursive(remainingPart, compositeParameterNode, extLanguageManager);
	}

}
