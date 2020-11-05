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


public class ParameterCondition implements IStatementCondition {

	private MethodParameterNode fRightParameterNode;
	private RelationStatement fParentRelationStatement;
	private IExtLanguageManager fExtLanguageManager;

	public ParameterCondition(
			MethodParameterNode rightParameter, 
			RelationStatement parentRelationStatement,
			IExtLanguageManager extLanguageManager) {

		fRightParameterNode = rightParameter;
		fParentRelationStatement = parentRelationStatement;
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> choices) {

		if (isLeftChoiceRandomizedString(choices)) {
			return EvaluationResult.TRUE; 
		}

		if (isRightChoiceRandomizedString(choices)) {
			return EvaluationResult.TRUE;
		}

		String substituteType = 
				JavaLanguageHelper.getSubstituteType(
						fParentRelationStatement.getLeftParameter().getType(), fRightParameterNode.getType());

		return evaluateForLeftAndRightString(choices, substituteType);
	}

	private boolean isLeftChoiceRandomizedString(List<ChoiceNode> choices) {

		return isChoiceRandomizedString(choices, fParentRelationStatement.getLeftParameter());
	}

	private boolean isRightChoiceRandomizedString(List<ChoiceNode> choices) {

		return isChoiceRandomizedString(choices, fRightParameterNode);
	}

	private boolean isChoiceRandomizedString(
			List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		ChoiceNode leftChoiceNode = getChoiceNode(choices, methodParameterNode);

		if (JavaLanguageHelper.isStringTypeName(methodParameterNode.getType())
				&& leftChoiceNode.isRandomizedValue()) {

			return true;
		}

		return false;
	}

	private EvaluationResult evaluateForLeftAndRightString(List<ChoiceNode> choices, String substituteType) {

		String rightChoiceStr = getChoiceString(choices, fRightParameterNode);
		if (rightChoiceStr == null) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		String leftChoiceStr = getChoiceString(choices, fParentRelationStatement.getLeftParameter());
		if (leftChoiceStr == null) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		EMathRelation relation = fParentRelationStatement.getRelation();

		boolean isRandomizedChoice = 
				StatementConditionHelper.getChoiceRandomized(
						choices, fParentRelationStatement.getLeftParameter());

		if (isRandomizedChoice) {
			return evaluateForRandomizedChoice(leftChoiceStr, rightChoiceStr, relation, substituteType);
		}

		return evaluateForConstantChoice(leftChoiceStr, rightChoiceStr, relation, substituteType);

	}

	private EvaluationResult evaluateForRandomizedChoice(
			String leftChoiceStr, String rightChoiceStr, EMathRelation relation, String substituteType) {

		if (JavaLanguageHelper.isStringTypeName(substituteType)) {

			return EvaluationResult.TRUE;

		} else {

			boolean result = 
					RangeHelper.isRightRangeInLeftRange(
							leftChoiceStr, rightChoiceStr, relation, substituteType);

			return EvaluationResult.convertFromBoolean(result);
		}
	}

	private EvaluationResult evaluateForConstantChoice(
			String leftChoiceStr, String rightChoiceStr, EMathRelation relation, String substituteType) {

		if (RelationMatcher.isMatchQuiet(relation, substituteType, leftChoiceStr, rightChoiceStr)) {
			return EvaluationResult.TRUE;
		}

		return EvaluationResult.FALSE;
	}

	private static String getChoiceString(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		ChoiceNode choiceNode = getChoiceNode(choices, methodParameterNode);

		if (choiceNode == null) {
			return null;
		}

		return choiceNode.getValueString();
	}

	private static ChoiceNode getChoiceNode(List<ChoiceNode> choices, MethodParameterNode methodParameterNode) {

		return StatementConditionHelper.getChoiceForMethodParameter(choices, methodParameterNode);
	}


	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ParameterCondition getCopy() {

		return new ParameterCondition(fRightParameterNode.makeClone(), fParentRelationStatement, fExtLanguageManager);
	}

	@Override
	public boolean updateReferences(MethodNode methodNode) {

		MethodParameterNode tmpParameterNode = methodNode.findMethodParameter(fRightParameterNode.getName());
		if (tmpParameterNode == null) {
			return false;
		}
		fRightParameterNode = tmpParameterNode;

		return true;
	}

	@Override
	public Object getCondition(){

		return fRightParameterNode;
	}

	@Override
	public boolean compare(IStatementCondition otherCondition) {

		if (!(otherCondition instanceof ParameterCondition)) {
			return false;
		}

		ParameterCondition otherParamCondition = (ParameterCondition)otherCondition;

		if (fParentRelationStatement.getRelation() != otherParamCondition.fParentRelationStatement.getRelation()) {
			return false;
		}		

		if (!fRightParameterNode.isMatch(otherParamCondition.fRightParameterNode)) {
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

		return StatementConditionHelper.createParameterDescription(fRightParameterNode.getName());
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		return StatementConditionHelper.createParameterDescription(
				MethodParameterNodeHelper.getName(fRightParameterNode, extLanguageManager));

	}

	@Override
	public boolean mentions(MethodParameterNode methodParameterNode) {

		if (fRightParameterNode == methodParameterNode) {
			return true;
		}

		return false;
	}	

	public MethodParameterNode getRightParameterNode() {

		return fRightParameterNode;
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> domain, MessageStack messageStack) {

		String substituteType = ConditionHelper.getSubstituteType(fParentRelationStatement);

		int leftParameterIndex = fParentRelationStatement.getLeftParameter().getMyIndex();
		List<ChoiceNode> leftChoices = domain.get(leftParameterIndex);

		int rightIndex = fRightParameterNode.getMyIndex();
		List<ChoiceNode> rightChoices = domain.get(rightIndex);

		EMathRelation relation = fParentRelationStatement.getRelation();

		return isAmbiguousForLeftAndRightChoices(
				leftChoices, rightChoices, relation, substituteType, fExtLanguageManager, messageStack);					
	}

	private boolean isAmbiguousForLeftAndRightChoices(
			List<ChoiceNode> leftChoices,
			List<ChoiceNode> rightChoices,
			EMathRelation relation,
			String substituteType,
			IExtLanguageManager extLanguageManager,
			MessageStack messageStack) {

		for (ChoiceNode leftChoiceNode : leftChoices) {
			for (ChoiceNode rightChoiceNode : rightChoices) {

				if (isChoicesPairAmbiguous(
						leftChoiceNode, rightChoiceNode, relation, substituteType, extLanguageManager, messageStack)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isChoicesPairAmbiguous(
			ChoiceNode leftChoiceNode, 
			ChoiceNode rightChoiceNode,
			EMathRelation relation,
			String substituteType,
			IExtLanguageManager extLanguageManager,
			MessageStack messageStack) {

		if (areBothChoicesFixed(leftChoiceNode, rightChoiceNode)) {
			return false;
		}

		if (ConditionHelper.isRandomizedChoiceAmbiguous(
				leftChoiceNode, rightChoiceNode.getValueString(),
				fParentRelationStatement, relation, substituteType)) {

			ConditionHelper.addValuesMessageToStack(
					ChoiceNodeHelper.createSignature(leftChoiceNode, extLanguageManager), 
					relation, 
					ChoiceNodeHelper.createSignature(rightChoiceNode, extLanguageManager), 
					messageStack);

			return true;
		}

		return false;
	}

	private boolean areBothChoicesFixed(ChoiceNode leftChoiceNode, ChoiceNode rightChoiceNode) {

		if (leftChoiceNode.isRandomizedValue()) {
			return false;
		}

		if (rightChoiceNode.isRandomizedValue()) {
			return false;
		}		

		return true;
	}

	@Override
	public List<ChoiceNode> getListOfChoices() {
		return new ArrayList<ChoiceNode>();
	}

}	


