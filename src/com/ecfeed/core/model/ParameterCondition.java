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
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.RangeHelper;
import com.ecfeed.core.utils.RelationMatcher;


public class ParameterCondition implements IStatementCondition {

	private BasicParameterNode fRightParameterNode;
	private CompositeParameterNode fRightParameterLinkingContext;
	private RelationStatement fParentRelationStatement;

	public ParameterCondition(
			BasicParameterNode rightParameter,
			CompositeParameterNode rightParameterLinkingContext,
			RelationStatement parentRelationStatement) {

		fRightParameterNode = rightParameter;
		fRightParameterLinkingContext = rightParameterLinkingContext;
		fParentRelationStatement = parentRelationStatement;
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

	@Override
	public RelationStatement getParentRelationStatement() {
		return fParentRelationStatement;
	}

	private boolean isLeftChoiceRandomizedString(List<ChoiceNode> choices) {

		return isChoiceRandomizedString(choices, fParentRelationStatement.getLeftParameter());
	}

	private boolean isRightChoiceRandomizedString(List<ChoiceNode> choices) {

		return isChoiceRandomizedString(choices, fRightParameterNode);
	}

	private boolean isChoiceRandomizedString(
			List<ChoiceNode> choices, BasicParameterNode methodParameterNode) {

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

	private static String getChoiceString(List<ChoiceNode> choices, BasicParameterNode methodParameterNode) {

		ChoiceNode choiceNode = getChoiceNode(choices, methodParameterNode);

		if (choiceNode == null) {
			return null;
		}

		return choiceNode.getValueString();
	}

	private static ChoiceNode getChoiceNode(List<ChoiceNode> choices, BasicParameterNode methodParameterNode) {

		return StatementConditionHelper.getChoiceForMethodParameter(choices, methodParameterNode);
	}


	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ParameterCondition makeClone() {

		// parameters are not cloned
		return new ParameterCondition(fRightParameterNode, fRightParameterLinkingContext, fParentRelationStatement);
	}

	@Override
	public ParameterCondition createCopy(RelationStatement statement, NodeMapper mapper) {
		BasicParameterNode parameter = mapper.getDeployedNode(fRightParameterNode);

		return new ParameterCondition(parameter, fRightParameterLinkingContext, statement);
	}

	//	@Override
	//	public boolean updateReferences(IParametersParentNode methodNode) {
	//
	//		String compositeName = AbstractParameterNodeHelper.getCompositeName(fRightParameterNode);
	//		
	//		BasicParameterNode basicParameterNode = 
	//				BasicParameterNodeHelper.findBasicParameterByQualifiedIntrName(compositeName, methodNode);
	//
	//		if (basicParameterNode == null) {
	//			return false;
	//		}
	//
	//		fRightParameterNode = basicParameterNode;
	//
	//		return true;
	//	}

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

		String name = AbstractParameterNodeHelper.getCompositeName(fRightParameterNode);
		String parameterDescription = StatementConditionHelper.createParameterDescription(name);
		return parameterDescription;
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		String name = 
				AbstractParameterNodeHelper.getQualifiedName(
						fRightParameterNode, fRightParameterLinkingContext);
		
		return StatementConditionHelper.createParameterDescription(name);
	}

	@Override
	public boolean mentions(AbstractParameterNode abstractParameterNode) {

		if (fRightParameterNode == abstractParameterNode) {
			return true;
		}

		return false;
	}	

	public BasicParameterNode getRightParameterNode() {

		return fRightParameterNode;
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> domain, MessageStack messageStack, IExtLanguageManager extLanguageManager) {

		String substituteType = ConditionHelper.getSubstituteType(fParentRelationStatement);

		int leftParameterIndex = fParentRelationStatement.getLeftParameter().getMyIndex();
		List<ChoiceNode> leftChoices = domain.get(leftParameterIndex);

		int rightIndex = fRightParameterNode.getMyIndex();
		List<ChoiceNode> rightChoices = domain.get(rightIndex);

		EMathRelation relation = fParentRelationStatement.getRelation();

		return isAmbiguousForLeftAndRightChoices(
				leftChoices, rightChoices, relation, substituteType, extLanguageManager, messageStack);					
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

		if (extLanguageManager == null) {
			extLanguageManager = new ExtLanguageManagerForJava();
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
	public List<ChoiceNode> getChoices() {
		return new ArrayList<ChoiceNode>();
	}

	@Override
	public List<ChoiceNode> getChoices(BasicParameterNode methodParameterNode) {
		return new ArrayList<ChoiceNode>();
	}

	@Override
	public void derandomize() {
	}

	@Override
	public void convert(
			ParameterConversionItem parameterConversionItem) {
	}

	@Override
	public boolean mentionsChoiceOfParameter(BasicParameterNode abstractParameterNode) {
		return false;
	}

	@Override
	public String getLabel(BasicParameterNode methodParameterNode) {
		return null;
	}

	//	@Override
	//	public IStatementCondition createDeepCopy(DeploymentMapper deploymentMapper) {
	//
	//		BasicParameterNode sourcMethodParameterNode = getRightParameterNode();
	//		BasicParameterNode deployedMethodParameterNode =
	//				deploymentMapper.getDeployedParameterNode(sourcMethodParameterNode);
	//
	//		RelationStatement deployedParentRelationStatement =
	//				deploymentMapper.getDeployedRelationStatement(fParentRelationStatement);
	//
	//		ParameterCondition deployedParameterCondition =
	//				new ParameterCondition(
	//						deployedMethodParameterNode,
	//						deployedParentRelationStatement);
	//
	//		return deployedParameterCondition;
	//	}

}	