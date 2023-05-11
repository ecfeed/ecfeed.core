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
import java.util.Optional;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.ObjectHelper;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartHelper;
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
	public ChoiceCondition makeClone(
			RelationStatement clonedParentRelationStatement, Optional<NodeMapper>nodeMapper) {

		if (nodeMapper.isPresent()) {

			ChoiceNode clonedChoiceNode = nodeMapper.get().getDestinationNode(fRightChoice);

			return new ChoiceCondition(clonedChoiceNode, clonedParentRelationStatement);
		}

		return new ChoiceCondition(fRightChoice, fParentRelationStatement);
	}

	@Override
	public ChoiceCondition makeClone() {  // TODO MO-RE obsolete
		// choices are not cloned
		return new ChoiceCondition(fRightChoice, fParentRelationStatement);
	}

	@Override
	public ChoiceCondition createCopy(RelationStatement statement, NodeMapper mapper) { // TODO MO-RE obsolete

		return new ChoiceCondition(updateChoiceReference(mapper), statement);
	}

	private ChoiceNode updateChoiceReference(NodeMapper mapper) {

		ChoiceNode node;

		if (isSourceLinked()) {
			node = fRightChoice;
		} else {
			node = mapper.getDestinationNode(fRightChoice);
		}

		node.setOrigChoiceNode(null);

		return node;
	}

	boolean isSourceLinked() {
		// If the source node is linked, there is no complementary deployment node.
		// The copy is not created, and it is safe to use the original node (linked) instead.
		// Also, there is no way to check whether it is a linked node "from the inside".
		IAbstractNode node = fRightChoice.getParameter();

		node = node.getParent();
		while (node instanceof CompositeParameterNode) {
			node = node.getParent();
		}

		return !(node instanceof MethodNode);
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
	public List<ChoiceNode> getChoices() {

		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		choices.add(fRightChoice);

		return choices;
	}

	@Override
	public List<ChoiceNode> getChoices(BasicParameterNode methodParameterNode) {

		BasicParameterNode methodParameterNode2 = fParentRelationStatement.getLeftParameter();

		if (!(methodParameterNode.equals(methodParameterNode2))) {
			return new ArrayList<ChoiceNode>();
		}

		List<ChoiceNode> choices = new ArrayList<ChoiceNode>();
		choices.add(fRightChoice);

		return choices;
	}

	@Override
	public RelationStatement getParentRelationStatement() {
		return fParentRelationStatement;
	}

	@Override
	public void setParentRelationStatement(RelationStatement relationStatement) {
		fParentRelationStatement = relationStatement;
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
	public void convert(ParameterConversionItem parameterConversionItem) {

		ChoiceNode srcChoiceNode = ParameterConversionItemPartHelper.getChoice(parameterConversionItem.getSrcPart());

		if (srcChoiceNode == null) {
			return;
		}

		ChoiceNode dstChoiceNode = ParameterConversionItemPartHelper.getChoice(parameterConversionItem.getDstPart());

		if (dstChoiceNode == null) {
			return;
		}

		if (!srcChoiceNode.equals(fRightChoice)) {
			return;
		}

		fRightChoice = dstChoiceNode;
	}

	@Override
	public boolean mentionsChoiceOfParameter(BasicParameterNode abstractParameterNode) {

		if (fRightChoice.getParameter().equals(abstractParameterNode)) {
			return true;
		}

		return false;
	}

	@Override
	public String getLabel(BasicParameterNode methodParameterNode) {
		return null;
	}

	public void conditionallyConvertChoice(ChoiceNode oldChoiceNode, ChoiceNode newChoiceNode) {

		if (fRightChoice == oldChoiceNode) {
			fRightChoice = newChoiceNode;
		}
	}

	@Override
	public boolean isConsistent(MethodNode parentMethodNode) {

		/// XYX use: BasicParameterNodeHelper.getParameterWithChoices(basicParameterNode, linkingContext);

		RelationStatement parentRelationStatement = getParentRelationStatement();

		BasicParameterNode leftBasicParameterNode = parentRelationStatement.getLeftParameter();

		AbstractParameterNode linkToGlobalParameter = leftBasicParameterNode.getLinkToGlobalParameter();

		if (linkToGlobalParameter instanceof BasicParameterNode) {

			BasicParameterNode linkAsBasicParameter = (BasicParameterNode) linkToGlobalParameter;

			if (BasicParameterNodeHelper.choiceNodeExists(linkAsBasicParameter, fRightChoice)) {
				return true;
			}

			return false;
		}

		if (BasicParameterNodeHelper.choiceNodeExists(leftBasicParameterNode, fRightChoice)) {
			return true;
		}

		// TODO XYX
		return false;
	}

	//	@Override
	//	public IStatementCondition createDeepCopy(DeploymentMapper deploymentMapper) {
	//
	//		ChoiceNode sourceChoiceNode = getRightChoice();
	//		ChoiceNode deployedChoiceNode = deploymentMapper.getDeployedChoiceNode(sourceChoiceNode);
	//
	//		RelationStatement sourceParentRelationStatement = fParentRelationStatement;
	//		RelationStatement deployedParentRelationStatement =
	//				deploymentMapper.getDeployedRelationStatement(sourceParentRelationStatement);
	//
	//		if (deployedParentRelationStatement == null) {
	//			ExceptionHelper.reportRuntimeException("Empyt parent relation statement.");
	//		}
	//
	//		IStatementCondition deployeChoiceCondition =
	//				new ChoiceCondition(
	//						deployedChoiceNode,
	//						deployedParentRelationStatement);
	//
	//		return deployeChoiceCondition;
	//	}

}

