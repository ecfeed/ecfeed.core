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

	private AssignmentStatement(
			MethodParameterNode parameter, 
			IStatementCondition condition) {

		super(parameter, EMathRelation.ASSIGN, condition);
	}

	public static AssignmentStatement createAssignmentWithChoiceCondition(
			MethodParameterNode parameter,
			ChoiceNode choiceNode) {

		AssignmentStatement AssignmentStatement = new AssignmentStatement(parameter, null);

		IStatementCondition condition = new ChoiceCondition(choiceNode, AssignmentStatement);

		AssignmentStatement.setCondition(condition);

		return AssignmentStatement;
	}

	public static AssignmentStatement createAssignmentWithParameterCondition(
			MethodParameterNode parameter,
			MethodParameterNode rightParameter) {

		AssignmentStatement AssignmentStatement = new AssignmentStatement(parameter, null);

		IStatementCondition condition = new ParameterCondition(rightParameter, AssignmentStatement);

		AssignmentStatement.setCondition(condition);

		return AssignmentStatement;
	}

	public static AssignmentStatement createAssignmentWithValueCondition(
			MethodParameterNode parameter,
			String textValue) {

		AssignmentStatement AssignmentStatement = new AssignmentStatement(parameter, null);

		IStatementCondition condition = new ValueCondition(textValue, AssignmentStatement);

		AssignmentStatement.setCondition(condition);

		return AssignmentStatement;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {
		return EvaluationResult.TRUE;
	}

	@Override
	public boolean setExpectedValue(List<ChoiceNode> values) {

		if (values == null) {
			return true;
		}

		MethodParameterNode methodParameterNode = getLeftParameter();

		if (methodParameterNode == null) {
			return true;
		}

		MethodNode methodNode = methodParameterNode.getMethod();

		if (methodNode == null) {
			return true;
		}

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		int indexOfParameter = parameters.indexOf(methodParameterNode);

		if (indexOfParameter == -1) {
			ExceptionHelper.reportRuntimeException("Invalid index of parameter.");
		}

		IStatementCondition statementCondition = getCondition();

		ChoiceNode newChoiceNode = null;

		if (statementCondition instanceof ChoiceCondition) {

			ChoiceCondition choiceCondition = (ChoiceCondition)statementCondition;
			newChoiceNode = choiceCondition.getRightChoice().makeClone();

			values.set(indexOfParameter, newChoiceNode);
			return true;
		}

		if (statementCondition instanceof ValueCondition) {

			ValueCondition valueCondition = (ValueCondition)statementCondition;

			String value = valueCondition.getRightValue();

			newChoiceNode =  new ChoiceNode("assignment", value, null);
			newChoiceNode.setParent(methodParameterNode);

			values.set(indexOfParameter, newChoiceNode);
			return true;
		}

		ExceptionHelper.reportRuntimeException("Invalid type of statement condition.");
		return false;
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

		return new AssignmentStatement(getLeftParameter(), getCondition().getCopy());
	}

}

