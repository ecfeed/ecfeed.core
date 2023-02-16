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
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForChoice;
import com.ecfeed.core.utils.ParameterConversionItemPartForLabel;
import com.ecfeed.core.utils.StringHelper;

public class RelationStatement extends AbstractStatement implements IRelationalStatement{

	private BasicParameterNode fLeftParameter;
	private CompositeParameterNode fLeftParameterLinkingContext;
	private EMathRelation fRelation;
	private IStatementCondition fRightCondition;

	public static RelationStatement createRelationStatementWithLabelCondition(
			BasicParameterNode leftParameter,
			CompositeParameterNode leftParameterLinkingContext,
			EMathRelation relation,
			String label) {

		RelationStatement relationStatement = 
				new RelationStatement(leftParameter, leftParameterLinkingContext, relation, null);

		IStatementCondition condition = new LabelCondition(label, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}

	public static RelationStatement createRelationStatementWithChoiceCondition(
			BasicParameterNode leftParameter,
			CompositeParameterNode leftParameterLinkingContext,
			EMathRelation relation,
			ChoiceNode choiceNode) {

		RelationStatement relationStatement = 
				new RelationStatement(leftParameter, leftParameterLinkingContext, relation, null);

		IStatementCondition condition = new ChoiceCondition(choiceNode, relationStatement);

		relationStatement.setCondition(condition);

		return relationStatement;
	}

	public static RelationStatement createRelationStatementWithParameterCondition(
			BasicParameterNode leftParameter,
			CompositeParameterNode leftParameterLinkingContext,
			EMathRelation relation,
			BasicParameterNode rightParameter) {

		RelationStatement relationStatement = 
				new RelationStatement(leftParameter, leftParameterLinkingContext, relation, null);

		CompositeParameterNode rightParameterLinkingContext = leftParameterLinkingContext;
		
		IStatementCondition condition = 
				new ParameterCondition(rightParameter, rightParameterLinkingContext, relationStatement);

		relationStatement.setCondition(condition);

		return relationStatement;
	}

	public static RelationStatement createRelationStatementWithValueCondition(
			BasicParameterNode leftParameter,
			CompositeParameterNode leftParameterLinkingContext,
			EMathRelation relation,
			String textValue) {

		RelationStatement relationStatement = 
				new RelationStatement(leftParameter, leftParameterLinkingContext, relation, null);

		IStatementCondition condition = new ValueCondition(textValue, relationStatement);
		relationStatement.setCondition(condition);

		return relationStatement;
	}

	protected RelationStatement(
			BasicParameterNode leftParameter,
			CompositeParameterNode leftParameterLinkinContext,
			EMathRelation relation, 
			IStatementCondition condition) {

		super(leftParameter.getModelChangeRegistrator());

		fLeftParameter = leftParameter;
		fLeftParameterLinkingContext = leftParameterLinkinContext;
		fRelation = relation;
		fRightCondition = condition;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {

		EvaluationResult result;
		try {
			result = fRightCondition.evaluate(values);
		} catch (Exception e) {
			LogHelperCore.logCatch(e);
			return EvaluationResult.FALSE;
		}

		return result;
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> testDomain, MessageStack messageStack, IExtLanguageManager extLanguageManager) {

		try {
			if (fRightCondition.isAmbiguous(testDomain, messageStack, extLanguageManager)) {
				ConditionHelper.addRelStatementToMesageStack(this, messageStack, extLanguageManager);
				return true;
			}
			return false;
		}
		catch (Exception e) {
			LogHelperCore.logCatch(e);
			return false;
		}
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> testDomain) {

		try {
			if (fRightCondition.isAmbiguous(testDomain, null, null)) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			LogHelperCore.logCatch(e);
			return false;
		}
	}

	@Override
	public void setRelation(EMathRelation relation) {
		fRelation = relation;
	}

	@Override
	public EMathRelation getRelation() {
		return fRelation;
	}

	@Override
	public String getLeftOperandName() {

		BasicParameterNode leftParameter = getLeftParameter();
		CompositeParameterNode linkingContext = getLeftParameterLinkingContext();

		String nameInIntrLanguage = 
				AbstractParameterNodeHelper.getQualifiedName(leftParameter, linkingContext);

		return nameInIntrLanguage;
	}

	@Override
	public String toString() {

		return getLeftOperandName() + getRelation() + fRightCondition.toString();
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		String conditionSignature = fRightCondition.createSignature(extLanguageManager);

		BasicParameterNode leftBasicParameterNode = getLeftParameter();
		CompositeParameterNode leftParameterLinkingCondition = getLeftParameterLinkingContext();

		String parameterName = 
				AbstractParameterNodeHelper.getQualifiedName(leftBasicParameterNode, leftParameterLinkingCondition);

		return parameterName + getRelation() + conditionSignature;
	}

	@Override
	public EMathRelation[] getAvailableRelations() {

		return EMathRelation.getAvailableComparisonRelations(getLeftParameter().getType());
	}

	@Override
	public RelationStatement makeClone() {

		return 
				new RelationStatement(
						fLeftParameter, fLeftParameterLinkingContext, fRelation, fRightCondition.makeClone());
	}

	@Override
	public RelationStatement createCopy(NodeMapper mapper) {

		BasicParameterNode parameter = mapper.getMappedNodeDeployment(fLeftParameter);

		RelationStatement statement = 
				new RelationStatement(parameter, fLeftParameterLinkingContext, fRelation, null);

		IStatementCondition condition = fRightCondition.createCopy(statement, mapper);
		statement.setCondition(condition);

		return statement;
	}

	//	@Override
	//	public boolean updateReferences(IParametersAndConstraintsParentNode parent) {
	//
	//		String compositeName = AbstractParameterNodeHelper.getCompositeName(fLeftParameter);
	//
	//		BasicParameterNode basicParameterNode = 
	//				BasicParameterNodeHelper.findBasicParameterByQualifiedIntrName(
	//						compositeName, parent);
	//
	//		if (basicParameterNode == null) {
	//			return false;
	//		}
	//
	//		if (basicParameterNode.isExpected()) {
	//			return true;
	//		}
	//
	//				if (fRightCondition.updateReferences(parent)) {
	//					fLeftParameter = basicParameterNode;
	//				}
	//
	//		return true;
	//	}

	@Override
	public boolean isEqualTo(IStatement statement) {

		if ( !(statement instanceof RelationStatement) ) {
			return false;
		}

		RelationStatement compared = (RelationStatement)statement;

		if ( !(getLeftParameter().getName().equals(compared.getLeftParameter().getName())) ) {
			return false;
		}

		if (getRelation() != compared.getRelation()) {
			return false;
		}

		if (!getCondition().compare(compared.getCondition())) {
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public boolean mentions(AbstractParameterNode abstractParameterNode) {

		if (getLeftParameter() == abstractParameterNode) {
			return true;
		}

		if (fRightCondition.mentions(abstractParameterNode)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean mentions(AbstractParameterNode parameter, String label) {

		return getLeftParameter() == parameter && getConditionValue().equals(label);
	}

	@Override
	public boolean mentionsParameterAndOrderRelation(AbstractParameterNode parameter) {

		if (!(parameter.isMatch(fLeftParameter))) {
			return false;
		}

		if (EMathRelation.isOrderRelation(fRelation)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean mentions(ChoiceNode choice) {

		return getConditionValue() == choice;
	}

	@Override
	public boolean mentions(int methodParameterIndex) {

		IParametersParentNode methodNode = (IParametersParentNode) fLeftParameter.getParent();
		AbstractParameterNode methodParameterNode = methodNode.getParameter(methodParameterIndex);

		if (mentions(methodParameterNode)) {
			return true;
		}

		return false;
	}	

	@Override
	public void derandomize() {
		fRightCondition.derandomize();
	}


	@Override
	public BasicParameterNode getLeftParameter() {
		return fLeftParameter;
	}

	@Override
	public CompositeParameterNode getLeftParameterLinkingContext() {
		return fLeftParameterLinkingContext;
	}

	public void setCondition(IStatementCondition condition) {

		if (condition instanceof ParameterCondition) {

			ParameterCondition parameterCondition = (ParameterCondition)condition;

			BasicParameterNode rightParameter = parameterCondition.getRightParameterNode();

			if (!isRightParameterTypeAllowed(rightParameter.getType())) {
				ExceptionHelper.reportRuntimeException("Invalid type of right parameter in relation statement.");
			}
		}

		fRightCondition = condition;
	}

	public void setCondition(String label) {
		fRightCondition = new LabelCondition(label, this);
	}

	public void setCondition(ChoiceNode choice) {
		fRightCondition = new ChoiceCondition(choice, this);
	}

	public void setCondition(BasicParameterNode parameter, ChoiceNode choice) {
		fRightCondition = new ChoiceCondition(choice, this);
	}

	public IStatementCondition getCondition() {
		return fRightCondition;
	}

	public Object getConditionValue() {
		return fRightCondition.getCondition();
	}

	public String getConditionName() {
		return fRightCondition.toString();
	}

	public String getConditionSignature(IExtLanguageManager extLanguageManager) {
		return fRightCondition.createSignature(extLanguageManager);
	}

	@Override
	public List<ChoiceNode> getChoices() {
		return fRightCondition.getChoices();
	}

	@Override
	public List<ChoiceNode> getChoices(BasicParameterNode methodParameterNode) {
		return fRightCondition.getChoices(methodParameterNode);
	}

	public boolean isRightParameterTypeAllowed(String rightParameterType) {

		BasicParameterNode leftParameter = getLeftParameter();
		String leftParameterType =  leftParameter.getType();

		if (JavaLanguageHelper.isBooleanTypeName(leftParameterType) 
				&& !JavaLanguageHelper.isBooleanTypeName(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isBooleanTypeName(leftParameterType) 
				&& JavaLanguageHelper.isBooleanTypeName(rightParameterType)) {

			return false;
		}

		if (JavaLanguageHelper.isTypeWithChars(leftParameterType)
				&& !JavaLanguageHelper.isTypeWithChars(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isTypeWithChars(leftParameterType)
				&& JavaLanguageHelper.isTypeWithChars(rightParameterType)) {

			return false;
		}

		if (JavaLanguageHelper.isNumericTypeName(leftParameterType)
				&& !JavaLanguageHelper.isNumericTypeName(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isNumericTypeName(leftParameterType)
				&& JavaLanguageHelper.isNumericTypeName(rightParameterType)) {

			return false;
		}

		return true;
	}

	@Override
	protected void convert(ParameterConversionItem parameterConversionItem) {

		IParameterConversionItemPart srcPart = parameterConversionItem.getSrcPart();
		IParameterConversionItemPart dstPart = parameterConversionItem.getDstPart();

		IParameterConversionItemPart.ItemPartType srcType = srcPart.getType();
		IParameterConversionItemPart.ItemPartType dstType = dstPart.getType();

		if (srcType == dstType) {
			fRightCondition.convert(parameterConversionItem);
			return;
		}

		if (srcType == IParameterConversionItemPart.ItemPartType.LABEL && 
				fRightCondition instanceof LabelCondition) {

			convertLabelPartToChoicePart(srcPart, dstPart);
			return;
		}

		if (srcType == IParameterConversionItemPart.ItemPartType.CHOICE && 
				fRightCondition instanceof ChoiceCondition) {

			convertChoicePartToLabelPart(srcPart, dstPart);
			return;
		}

	}

	private void convertLabelPartToChoicePart(
			IParameterConversionItemPart srcPart,
			IParameterConversionItemPart dstPart) {

		LabelCondition labelCondition = (LabelCondition) fRightCondition;
		ParameterConversionItemPartForLabel parameterConversionItemPartForLabel = 
				(ParameterConversionItemPartForLabel) srcPart;

		String labelOfCondition = labelCondition.getRightLabel();
		String labelOfItemPart = parameterConversionItemPartForLabel.getLabel();


		if (!StringHelper.isEqual(labelOfCondition, labelOfItemPart)) {
			return;
		}

		ParameterConversionItemPartForChoice parameterConversionItemPartForChoice = 
				(ParameterConversionItemPartForChoice) dstPart;

		ChoiceNode choiceNode = parameterConversionItemPartForChoice.getChoiceNode();

		ChoiceCondition choiceCondition = new ChoiceCondition(choiceNode,	this);

		fRightCondition = choiceCondition;
	}

	private void convertChoicePartToLabelPart(
			IParameterConversionItemPart srcPart,
			IParameterConversionItemPart dstPart) {

		ChoiceCondition choiceCondition = (ChoiceCondition) fRightCondition;

		ParameterConversionItemPartForChoice parameterConversionItemPartForChoice = 
				(ParameterConversionItemPartForChoice) srcPart;

		ChoiceNode choiceOfCondition = choiceCondition.getRightChoice();
		ChoiceNode choiceOfItemPart = parameterConversionItemPartForChoice.getChoiceNode();


		if (!choiceOfCondition.equals(choiceOfItemPart)) {
			return;
		}

		ParameterConversionItemPartForLabel parameterConversionItemPartForLabel = 
				(ParameterConversionItemPartForLabel) dstPart;

		String label = parameterConversionItemPartForLabel.getLabel();

		LabelCondition labelCondition = new LabelCondition(label, this);

		fRightCondition = labelCondition;
	}

	@Override
	public boolean mentionsChoiceOfParameter(BasicParameterNode parameter) {
		return fRightCondition.mentionsChoiceOfParameter(parameter);
	}

	@Override
	public List<String> getLabels(BasicParameterNode methodParameterNode) {

		List<String> result = new ArrayList<>();

		String label = fRightCondition.getLabel(methodParameterNode);

		if (label != null) {
			result.add(label);
		}

		return result;
	}

	//	@Override
	//	public AbstractStatement createDeepCopy(DeploymentMapper deploymentMapper) {
	//
	//		BasicParameterNode sourceParameter = getLeftParameter();
	//		BasicParameterNode deployedParameter = deploymentMapper.getDeployedParameterNode(sourceParameter);
	//
	//		IStatementCondition sourceCondition = getCondition();
	//		IStatementCondition deployedStatementCondition = sourceCondition.createDeepCopy(deploymentMapper);
	//
	//		EMathRelation sourceRelation = getRelation();
	//
	//		RelationStatement deployedRelationStatement =
	//				new RelationStatement(
	//						deployedParameter,
	//						sourceRelation,
	//						deployedStatementCondition);
	//
	//		deploymentMapper.addRelationStatementMappings(this, deployedRelationStatement);
	//
	//		return deployedRelationStatement;
	//	}
}

