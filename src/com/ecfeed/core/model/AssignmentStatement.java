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
	public boolean setValues(List<ChoiceNode> values) {

		// TODO CONSTRAINTS-NEW
		//		if (values == null) {
		//			return true;
		//		}
		//
		//		if  (fParameter.getMethod() != null) {
		//
		//			int index = fParameter.getMethod().getParameters().indexOf(fParameter);
		//			values.set(index, fCondition.makeClone());
		//		}

		return true;
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
