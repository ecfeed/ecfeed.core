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

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.ObjectHelper;
import com.ecfeed.core.utils.RangeHelper;
import com.ecfeed.core.utils.RelationMatcher;

public class ChoiceCondition implements IStatementCondition {

	private ChoiceNode fRightChoice;
	private RelationStatement fParentRelationStatement;

	public ChoiceCondition(
			ChoiceNode rightChoice, 
			RelationStatement parentRelationStatement) {

		fRightChoice = rightChoice;
		fParentRelationStatement = parentRelationStatement;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> choices) {

		ChoiceNode choice = 
				StatementConditionHelper.getChoiceForMethodParameter(
						choices, fParentRelationStatement.getLeftParameter());

		if (choice == null) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		return evaluateChoice(choice);
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ChoiceCondition makeClone() {
		// choices are not cloned
		return new ChoiceCondition(fRightChoice, fParentRelationStatement);
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

		String parameterName = fParentRelationStatement.getLeftParameter().getName();
		MethodParameterNode methodParameterNode = methodNode.findMethodParameter(parameterName);

		String choiceName = fRightChoice.getQualifiedName();
		ChoiceNode choiceNode = methodParameterNode.getChoice(choiceName);

		if (choiceNode == null) {
			return false;
		}
		fRightChoice = choiceNode;

		return true;
	}

	@Override
	public Object getCondition(){
		return fRightChoice;
	}

	@Override
	public boolean compare(IStatementCondition condition) {

		if (condition instanceof ChoiceCondition == false) {
			return false;
		}

		ChoiceCondition compared = (ChoiceCondition)condition;

		return (fRightChoice.isMatch((ChoiceNode)compared.getCondition()));
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public String toString() {

		return StatementConditionHelper.createChoiceDescription(fRightChoice.getQualifiedName());
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		String choiceSignature = ChoiceNodeHelper.createShortSignature(fRightChoice);

		return StatementConditionHelper.createChoiceDescription(choiceSignature);
	}

	@Override
	public boolean mentions(AbstractParameterNode methodParameterNode) {

		return false;
	}	

	@Override
	public List<ChoiceNode> getListOfChoices() {

		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		choices.add(fRightChoice);

		return choices;
	}

	public ChoiceNode getRightChoice() {
		return fRightChoice;
	}

	private EvaluationResult evaluateChoice(ChoiceNode actualLeftChoice) {

		String substituteType = getSubstituteType(actualLeftChoice);

		boolean isRandomizedChoice =
				fRightChoice.isRandomizedValue() ||
				StatementConditionHelper.getChoiceRandomized(
						actualLeftChoice, 
						fParentRelationStatement.getLeftParameter());

		if(isRandomizedChoice) {
			return evaluateForRandomizedChoice(
					actualLeftChoice.getValueString(), 
					substituteType);
		}

		return evaluateForConstantChoice(actualLeftChoice, substituteType);
	}

	private String getSubstituteType(ChoiceNode leftChoice) {

		String typeName = leftChoice.getParameter().getType();
		return JavaLanguageHelper.getSubstituteType(typeName);
	}

	private EvaluationResult evaluateForConstantChoice(
			ChoiceNode actualLeftChoice,
			String substituteType) {

		EMathRelation relation = fParentRelationStatement.getRelation();

		if (relation == EMathRelation.EQUAL || relation == EMathRelation.NOT_EQUAL) {
			return evaluateEqualityIncludingParents(relation, actualLeftChoice);
		}

		String actualLeftValue = JavaLanguageHelper.parseJavaValueToString(actualLeftChoice.getValueString(), substituteType);
		String rightValue = JavaLanguageHelper.parseJavaValueToString(fRightChoice.getValueString(), substituteType);

		if (RelationMatcher.isMatchQuiet(relation, substituteType, actualLeftValue, rightValue)) {
			return EvaluationResult.TRUE;
		}

		return EvaluationResult.FALSE;		
	}

	private EvaluationResult evaluateForRandomizedChoice(
			String leftChoiceStr,
			String substituteType) {

		EMathRelation relation = fParentRelationStatement.getRelation();
		String fRightValue = fRightChoice.getValueString();

		if (JavaLanguageHelper.isStringTypeName(substituteType)) {
			return EvaluationResult.TRUE;
		}

		boolean result = 
				RangeHelper.isRightRangeInLeftRange(
						leftChoiceStr, fRightValue, relation, substituteType);

		return EvaluationResult.convertFromBoolean(result);
	}

	private EvaluationResult evaluateEqualityIncludingParents(EMathRelation relation, ChoiceNode choice) {

		boolean isMatch = false;

		if (choice == null || fRightChoice == null) {
			isMatch = ObjectHelper.isEqualWhenOneOrTwoNulls(choice, fRightChoice);
		} else {
			isMatch = choice.isMatchIncludingParents(fRightChoice);
		}

		switch (relation) {

		case EQUAL:
			return EvaluationResult.convertFromBoolean(isMatch);
		case NOT_EQUAL:
			return EvaluationResult.convertFromBoolean(!isMatch);
		default:
			ExceptionHelper.reportRuntimeException("Invalid relation.");
			return EvaluationResult.FALSE;
		}
	}

	@Override
	public boolean isAmbiguous(
			List<List<ChoiceNode>> testDomain, 
			MessageStack messageStack, 
			IExtLanguageManager extLanguageManager) {

		String substituteType = ConditionHelper.getSubstituteType(fParentRelationStatement);

		int leftParameterIndex = fParentRelationStatement.getLeftParameter().getMyIndex();
		List<ChoiceNode> choicesForParameter = testDomain.get(leftParameterIndex);

		EMathRelation relation = fParentRelationStatement.getRelation();

		for (ChoiceNode leftChoiceNode : choicesForParameter) {

			if (isChoiceAmbiguous(leftChoiceNode, relation, substituteType, messageStack, extLanguageManager)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void derandomize() {
		fRightChoice.derandomize();
	}

	private boolean isChoiceAmbiguous(
			ChoiceNode leftChoiceNode,
			EMathRelation relation,
			String substituteType,
			MessageStack messageStack,
			IExtLanguageManager extLanguageManager) {

		if (!leftChoiceNode.isRandomizedValue()) {
			return false;
		}

		if (ConditionHelper.isRandomizedChoiceAmbiguous(
				leftChoiceNode, fRightChoice.getValueString(), 
				fParentRelationStatement, relation, substituteType)) {

			if (leftChoiceNode.equals(fRightChoice)) {
				return false;
			}

			if (extLanguageManager == null) {
				extLanguageManager = new ExtLanguageManagerForJava();
			}

			String leftSignature = ChoiceNodeHelper.createSignature(leftChoiceNode, extLanguageManager);
			String rightSignature = ChoiceNodeHelper.createSignature(fRightChoice, extLanguageManager);

			if (messageStack != null) {
				ConditionHelper.addValuesMessageToStack(
						leftSignature, 
						relation, 
						rightSignature,
						messageStack);
			}

			return true;
		}

		return false;
	}

	@Override
	public void updateChoiceReferences(
			ChoiceNode oldChoiceNode, 
			ChoiceNode newChoiceNode,
			ListOfModelOperations reverseOperations,
			IExtLanguageManager extLanguageManager) {

		if (fRightChoice == oldChoiceNode) {
			fRightChoice = newChoiceNode;
		}
	}

	@Override
	public boolean mentionsChoiceOfParameter(AbstractParameterNode abstractParameterNode) {

		if (fRightChoice.getParameter().equals(abstractParameterNode)) {
			return true;
		}

		return false;
	}

	@Override
	public String getLabel(MethodParameterNode methodParameterNode) {
		return null;
	}

}

