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
import java.util.Set;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.MessageStack;

public class LabelCondition implements IStatementCondition {

	private String fRightLabel;
	private RelationStatement fParentRelationStatement;

	public LabelCondition(String label, RelationStatement parentRelationStatement) {

		fRightLabel = label;
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

		return evaluateContainsLabel(choice);
	}

	//	@Override
	//	public boolean updateReferences(IParametersParentNode methodNode) {
	//
	//		return true;
	//	}

	@Override
	public Object getCondition() {
		return fRightLabel;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public boolean compare(IStatementCondition condition) {

		if(condition instanceof LabelCondition == false) {
			return false;
		}

		LabelCondition compared = (LabelCondition)condition;

		return (getCondition().equals(compared.getCondition()));
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public String toString() {

		return StatementConditionHelper.createLabelDescription(fRightLabel);
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		return StatementConditionHelper.createLabelDescription(fRightLabel);
	}

	@Override
	public IStatementCondition makeClone(RelationStatement statement, Optional<NodeMapper> mapper) {
		
		return new LabelCondition(fRightLabel, statement);
	}

	@Override
	public LabelCondition makeClone() {
		return new LabelCondition(fRightLabel, fParentRelationStatement);
	}

	@Override
	public LabelCondition createCopy(RelationStatement statement, NodeMapper mapper) {

		return new LabelCondition(fRightLabel, statement);
	}

	public String getRightLabel() {
		return fRightLabel;
	}

	@Override
	public RelationStatement getParentRelationStatement() {
		return fParentRelationStatement;
	}

	@Override
	public void setParentRelationStatement(RelationStatement relationStatement) {
		fParentRelationStatement = relationStatement;
	}

	@Override
	public boolean mentions(AbstractParameterNode abstractParameterNode) {

		return false;
	}

	private EvaluationResult evaluateContainsLabel(ChoiceNode choice) {

		boolean containsLabel = choice.getAllLabels().contains(fRightLabel);

		EMathRelation relation = fParentRelationStatement.getRelation(); 

		switch (relation) {

		case EQUAL:
			return EvaluationResult.convertFromBoolean(containsLabel);
		case NOT_EQUAL:
			return EvaluationResult.convertFromBoolean(!containsLabel);
		default:
			return EvaluationResult.FALSE;
		}

	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> domain, MessageStack messageStack, IExtLanguageManager extLanguageManager) {
		return false;
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
	public void convert(ParameterConversionItem parameterConversionItem) {

		String srcLabel = ParameterConversionItemPartHelper.getLabel(parameterConversionItem.getSrcPart());

		if (StringHelper.isNullOrEmpty(srcLabel)) {
			return;
		}

		String dstLabel = ParameterConversionItemPartHelper.getLabel(parameterConversionItem.getDstPart());

		if (dstLabel == null) {
			return;
		}

		if (!StringHelper.isEqual(fRightLabel, srcLabel)) {
			return;
		}

		fRightLabel = dstLabel;
	}

	@Override
	public boolean mentionsChoiceOfParameter(BasicParameterNode abstractParameterNode) {
		return false;
	}

	@Override
	public String getLabel(BasicParameterNode methodParameterNode) {

		if (fParentRelationStatement.getLeftParameter() == methodParameterNode) {
			return fRightLabel;
		}

		return null;
	}

	@Override
	public boolean isConsistent(MethodNode parentMethodNode) {
		
		BasicParameterNode basicParameterNode = getParmameterWhichKeepsChoices();
		
		if (basicParameterNode == null) {
			return false;
		}
		
		Set<String> labels = basicParameterNode.getAllLabels();
		
		if (labels.contains(fRightLabel)) {
			return true;
		}
		
		return false;
	}

	private BasicParameterNode getParmameterWhichKeepsChoices() {
		
		BasicParameterNode basicParameterNode = fParentRelationStatement.getLeftParameter();
		AbstractParameterNode linkingContext = fParentRelationStatement.getLeftParameterLinkingContext();
		
		if (linkingContext == null) {
			return basicParameterNode;
		}
		
		if (linkingContext instanceof BasicParameterNode) {
			return (BasicParameterNode) linkingContext;
		}
		
		return null;
	}

	//	@Override
	//	public IStatementCondition createDeepCopy(DeploymentMapper deploymentMapper) {
	//
	//		String developedLabel = getRightLabel();
	//
	//		RelationStatement deployedParentRelationStatement =
	//				deploymentMapper.getDeployedRelationStatement(fParentRelationStatement);
	//
	//		LabelCondition deployedLabelCondition =
	//				new LabelCondition(
	//						developedLabel,
	//						deployedParentRelationStatement);
	//
	//		return deployedLabelCondition;
	//	}

}

