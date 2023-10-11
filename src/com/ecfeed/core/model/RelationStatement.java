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

import com.ecfeed.core.model.AbstractParameterSignatureHelper.Decorations;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.ExtendedName;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.TypeIncluded;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.TypeOfLink;
import com.ecfeed.core.model.NodeMapper.MappingDirection;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.ParameterConversionItem;

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
			BasicParameterNode rightParameter,
			CompositeParameterNode rightParameterLinkingContext) {

		RelationStatement relationStatement = 
				new RelationStatement(leftParameter, leftParameterLinkingContext, relation, null);

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
				AbstractParameterSignatureHelper.createSignatureOfParameterWithLinkNewStandard(
						linkingContext,
						ExtendedName.PATH_TO_TOP_CONTAINTER,
						TypeOfLink.NORMAL,
						leftParameter,
						ExtendedName.PATH_TO_TOP_CONTAINTER, // was PATH_TO_TOP_CONTAINTER_WITHOUT_TOP_LINKED_ITEM, buf statement editor requires full path 
						Decorations.NO,
						TypeIncluded.NO,
						new ExtLanguageManagerForJava());

		return nameInIntrLanguage;
	}

	@Override
	public String toString() {

		return createSignature(new ExtLanguageManagerForJava());
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		String conditionSignature = fRightCondition.createSignature(extLanguageManager);

		BasicParameterNode leftBasicParameterNode = getLeftParameter();
		CompositeParameterNode leftParameterLinkingCondition = getLeftParameterLinkingContext();

		String signatureNew = 
				AbstractParameterSignatureHelper.createSignatureOfParameterWithLinkNewStandard(
						leftParameterLinkingCondition,
						ExtendedName.PATH_TO_TOP_CONTAINTER,
						TypeOfLink.NORMAL,
						leftBasicParameterNode,
						ExtendedName.PATH_TO_TOP_CONTAINTER, // was PATH_TO_TOP_CONTAINTER_WITHOUT_TOP_LINKED_ITEM but display of signatures should be? with full paths
						Decorations.NO,
						TypeIncluded.NO,
						extLanguageManager);

		//		String parameterName = 
		//				AbstractParameterSignatureHelper.getQualifiedName(
		//						leftBasicParameterNode, leftParameterLinkingCondition, extLanguageManager);

		return signatureNew + getRelation() + conditionSignature;
	}

	@Override
	public EMathRelation[] getAvailableRelations() {

		return EMathRelation.getAvailableComparisonRelations(getLeftParameter().getType());
	}

	@Override
	public RelationStatement makeClone(Optional<NodeMapper> mapper) {

		if (mapper.isPresent()) {
			BasicParameterNode clonedParameter = mapper.get().getDestinationNode(fLeftParameter);

			RelationStatement clonedStatement = 
					new RelationStatement(clonedParameter, fLeftParameterLinkingContext, fRelation, null);

			IStatementCondition clonedCondition = fRightCondition.makeClone(clonedStatement, mapper);
			clonedStatement.setCondition(clonedCondition);

			return clonedStatement;
		}

		RelationStatement relationStatement = new RelationStatement(
				fLeftParameter, fLeftParameterLinkingContext, fRelation, fRightCondition.makeClone());

		return relationStatement;
	}

	@Override
	public void replaceReferences(NodeMapper nodeMapper, MappingDirection mappingDirection) {

		fLeftParameter = nodeMapper.getMappedNode(fLeftParameter, mappingDirection); 
		fLeftParameterLinkingContext  = nodeMapper.getMappedNode(fLeftParameterLinkingContext, mappingDirection);

		fRightCondition.replaceReferences(nodeMapper, mappingDirection);
	}

	@Override
	public RelationStatement makeClone() {

		return 
				new RelationStatement(
						fLeftParameter, fLeftParameterLinkingContext, fRelation, fRightCondition.makeClone());
	}

	@Override
	public RelationStatement createCopy(NodeMapper mapper) {

		BasicParameterNode parameter = mapper.getDestinationNode(fLeftParameter);

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

			BasicParameterNode leftParameter = getLeftParameter();
			String leftParameterType =  leftParameter.getType();

			if (!RelationStatementHelper.isRightParameterTypeAllowed(rightParameter.getType(), leftParameterType)) {
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

	//	public boolean isRightParameterTypeAllowed(String rightParameterType) {
	//
	//		BasicParameterNode leftParameter = getLeftParameter();
	//		String leftParameterType =  leftParameter.getType();
	//
	//		if (JavaLanguageHelper.isBooleanTypeName(leftParameterType) 
	//				&& !JavaLanguageHelper.isBooleanTypeName(rightParameterType)) {
	//
	//			return false;
	//		}
	//
	//		if (!JavaLanguageHelper.isBooleanTypeName(leftParameterType) 
	//				&& JavaLanguageHelper.isBooleanTypeName(rightParameterType)) {
	//
	//			return false;
	//		}
	//
	//		if (JavaLanguageHelper.isTypeWithChars(leftParameterType)
	//				&& !JavaLanguageHelper.isTypeWithChars(rightParameterType)) {
	//
	//			return false;
	//		}
	//
	//		if (!JavaLanguageHelper.isTypeWithChars(leftParameterType)
	//				&& JavaLanguageHelper.isTypeWithChars(rightParameterType)) {
	//
	//			return false;
	//		}
	//
	//		if (JavaLanguageHelper.isNumericTypeName(leftParameterType)
	//				&& !JavaLanguageHelper.isNumericTypeName(rightParameterType)) {
	//
	//			return false;
	//		}
	//
	//		if (!JavaLanguageHelper.isNumericTypeName(leftParameterType)
	//				&& JavaLanguageHelper.isNumericTypeName(rightParameterType)) {
	//
	//			return false;
	//		}
	//
	//		return true;
	//	}

	@Override
	protected void convert(ParameterConversionItem parameterConversionItem) {

		BasicParameterNode srcParameter = 
				(BasicParameterNode) parameterConversionItem.getSrcPart().getParameter();

		if (fLeftParameter != srcParameter) {
			return;
		}

		convertParameterAndLinkingContext(parameterConversionItem);

		StatementConditionHelper.convertRightCondition(parameterConversionItem, this, fRightCondition);
	}

	private void convertParameterAndLinkingContext(ParameterConversionItem parameterConversionItem) { // XYX move to helper

		BasicParameterNode dstParameter = 
				(BasicParameterNode) parameterConversionItem.getDstPart().getParameter();

		fLeftParameter = dstParameter;

		CompositeParameterNode dstLinkingContext = 
				parameterConversionItem.getDstPart().getLinkingContext();

		fLeftParameterLinkingContext = dstLinkingContext;
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

	@Override
	public boolean isConsistent(IParametersAndConstraintsParentNode parentMethodNode) {

		if (!BasicParameterNodeHelper.isParameterOfConstraintConsistent(
				fLeftParameter, fLeftParameterLinkingContext, parentMethodNode)) {

			return false;
		}

		if (!fRightCondition.isConsistent(parentMethodNode)) {
			return false;
		}

		return true;
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

