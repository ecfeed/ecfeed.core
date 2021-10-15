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

import com.ecfeed.core.utils.*;


public class ValueCondition implements IStatementCondition {

	private String fRightValue;
	private RelationStatement fParentRelationStatement;

	public ValueCondition(String rightValue, RelationStatement parentRelationStatement) {
		fRightValue = rightValue;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> choices) {

		String substituteType = ConditionHelper.getSubstituteType(fParentRelationStatement);
		String leftChoiceStr = getChoiceString(choices, fParentRelationStatement.getLeftParameter());

		if (leftChoiceStr == null) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		EMathRelation relation = fParentRelationStatement.getRelation();

		boolean isRandomizedChoice = 
				StatementConditionHelper.getChoiceRandomized(
						choices, fParentRelationStatement.getLeftParameter());

		if(isRandomizedChoice) {
			if(JavaLanguageHelper.isStringTypeName(substituteType)) {
				return EvaluationResult.TRUE;
			}
			else {
				boolean result = RangeHelper.isRightRangeInLeftRange(leftChoiceStr, fRightValue, relation, substituteType);
				return EvaluationResult.convertFromBoolean(result);
			}
		}

		if (RelationMatcher.isMatchQuiet(relation, substituteType, leftChoiceStr, fRightValue)) {
			return EvaluationResult.TRUE;
		}

		return EvaluationResult.FALSE;
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> domain, MessageStack messageStack, IExtLanguageManager extLanguageManager) {

		String substituteType = ConditionHelper.getSubstituteType(fParentRelationStatement);		

		int leftParameterIndex = fParentRelationStatement.getLeftParameter().getMyIndex();
		List<ChoiceNode> choicesForParameter = domain.get(leftParameterIndex);

		EMathRelation relation = fParentRelationStatement.getRelation();

		for (ChoiceNode choice : choicesForParameter) {

			if (choice.isRandomizedValue()) {
				if (ConditionHelper.isRandomizedChoiceAmbiguous(
						choice, fRightValue, fParentRelationStatement, 
						relation, substituteType)) {

					ConditionHelper.addValuesMessageToStack(
							choice.toString(), relation, "value [" + fRightValue + "]", messageStack);

					return true;
				}
			}
		}

		return false;
	}

	private static String getChoiceString(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		ChoiceNode choiceNode = StatementConditionHelper.getChoiceForMethodParameter(choices, methodParameterNode);

		if (choiceNode == null) {
			return null;
		}

		return choiceNode.getValueString();
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ValueCondition makeClone() {

		return new ValueCondition(new String(fRightValue), fParentRelationStatement);
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

		return true;
	}

	@Override
	public Object getCondition(){

		return fRightValue;
	}

	@Override
	public boolean compare(IStatementCondition otherCondition) {

		if (!(otherCondition instanceof ValueCondition)) {
			return false;
		}

		ValueCondition otherValueCondition = (ValueCondition)otherCondition;

		if (fParentRelationStatement.getRelation() != otherValueCondition.fParentRelationStatement.getRelation()) {
			return false;
		}		

		if (!StringHelper.isEqual(fRightValue, otherValueCondition.fRightValue)) {
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {

		return visitor.visit(this);
	}

	@Override
	public String toString() {

		return fRightValue;
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		return fRightValue;
	}

	@Override
	public boolean mentions(AbstractParameterNode abstractParameterNode) {

		return false;
	}	

	public String getRightValue() {
		return fRightValue;
	}

	@Override
	public List<ChoiceNode> getListOfChoices() {
		return new ArrayList<ChoiceNode>();
	}
}	

