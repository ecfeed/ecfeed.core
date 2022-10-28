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

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;

import java.util.List;

public class AssignmentStatement extends RelationStatement {

	public static final String ASSIGNMENT_CHOICE_NAME = "@assignment";

	private AssignmentStatement(
			BasicParameterNode parameter, 
			IStatementCondition condition) {

		super(parameter, EMathRelation.ASSIGN, condition);
	}

	public static AssignmentStatement createAssignmentWithChoiceCondition(
			BasicParameterNode parameter,
			ChoiceNode choiceNode) {

		AssignmentStatement AssignmentStatement = new AssignmentStatement(parameter, null);

		IStatementCondition condition = new ChoiceCondition(choiceNode, AssignmentStatement);

		AssignmentStatement.setCondition(condition);

		return AssignmentStatement;
	}

	public static AssignmentStatement createAssignmentWithParameterCondition(
			BasicParameterNode parameter,
			BasicParameterNode rightParameter) {

		AssignmentStatement AssignmentStatement = new AssignmentStatement(parameter, null);

		IStatementCondition condition = new ParameterCondition(rightParameter, AssignmentStatement);

		AssignmentStatement.setCondition(condition);

		return AssignmentStatement;
	}

	public static AssignmentStatement createAssignmentWithValueCondition(
			BasicParameterNode parameter,
			String textValue) {

		AssignmentStatement AssignmentStatement = new AssignmentStatement(parameter, null);

		IStatementCondition condition = new ValueCondition(textValue, AssignmentStatement);

		AssignmentStatement.setCondition(condition);

		return AssignmentStatement;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> testCaseValues) {
		return EvaluationResult.TRUE;
	}

	@Override
	public boolean setExpectedValues(List<ChoiceNode> testCaseValues) {

		if (testCaseValues == null) {
			return true;
		}

		BasicParameterNode methodParameterNode = getLeftParameter();

		if (methodParameterNode == null) {
			return true;
		}

		MethodNode methodNode = methodParameterNode.getMethod();

		if (methodNode == null) {
			return true;
		}

		int countOfParameters = methodNode.getMethodParameterCount();

		if (testCaseValues.size() != countOfParameters) {
			ExceptionHelper.reportRuntimeException("Invalid size of test case values list.");
		}

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		int indexOfParameter = parameters.indexOf(methodParameterNode);

		if (indexOfParameter == -1) {
			ExceptionHelper.reportRuntimeException("Invalid index of parameter.");
		}

		IStatementCondition statementCondition = getCondition();

		ChoiceNode newChoiceNode =
				createChoiceNodeWithResultValue(testCaseValues, methodParameterNode, parameters, statementCondition);

		if (newChoiceNode == null) {
			return false;
		}

		testCaseValues.set(indexOfParameter, newChoiceNode);
		return true;
	}

	private ChoiceNode createChoiceNodeWithResultValue(
			List<ChoiceNode> testCaseValues,
			BasicParameterNode methodParameterNode,
			List<AbstractParameterNode> parameters,
			IStatementCondition statementCondition) {

		if (statementCondition instanceof ValueCondition) {

			ValueCondition valueCondition = (ValueCondition)statementCondition;

			return createChoiceNodeForValueCondition(methodParameterNode, valueCondition);
		}

		if (statementCondition instanceof ParameterCondition) {

			ParameterCondition parameterCondition = (ParameterCondition)statementCondition;

			return createChoiceNodeForParameterCondition(testCaseValues, parameters, parameterCondition);
		}

		if (statementCondition instanceof ChoiceCondition) {

			ChoiceCondition choiceCondition = (ChoiceCondition)statementCondition;

			return createChoiceNodeForChoiceCondition(choiceCondition);
		}

		ExceptionHelper.reportRuntimeException("Invalid type of statement condition.");
		return null;
	}

	private ChoiceNode createChoiceNodeForChoiceCondition(ChoiceCondition choiceCondition) {

		ChoiceNode newChoiceNode = choiceCondition.getRightChoice().makeClone();

		return newChoiceNode;
	}

	private ChoiceNode createChoiceNodeForParameterCondition(List<ChoiceNode> testCaseValues, List<AbstractParameterNode> parameters, ParameterCondition parameterCondition) {

		BasicParameterNode rightParameterNode = parameterCondition.getRightParameterNode();

		int indexOfRightParameter = parameters.indexOf(rightParameterNode);

		if (indexOfRightParameter == -1) {
			ExceptionHelper.reportRuntimeException("Invalid index of right parameter.");
		}

		ChoiceNode sourceChoiceNode = testCaseValues.get(indexOfRightParameter);

		ChoiceNode newChoiceNode = sourceChoiceNode.makeClone();

		return newChoiceNode;
	}

	private ChoiceNode createChoiceNodeForValueCondition(BasicParameterNode methodParameterNode, ValueCondition valueCondition) {

		String value = valueCondition.getRightValue();

		ChoiceNode newChoiceNode =  new ChoiceNode(AssignmentStatement.ASSIGNMENT_CHOICE_NAME, value, null);
		newChoiceNode.setParent(methodParameterNode);

		return newChoiceNode;
	}

	@Override
	public EMathRelation[] getAvailableRelations() {
		return new EMathRelation[]{EMathRelation.ASSIGN};
	}

	@Override
	public EMathRelation getRelation() {
		return EMathRelation.ASSIGN;
	}

	@Override
	public AssignmentStatement makeClone() {

		return new AssignmentStatement(getLeftParameter(), getCondition().makeClone());
	}
}

