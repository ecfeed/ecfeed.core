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

import com.ecfeed.core.type.adapter.IPrimitiveTypePredicate;
import com.ecfeed.core.utils.*;

public class ExpectedValueStatement extends AbstractStatement implements IRelationalStatement {

	private BasicParameterNode fLeftMethodParameterNode;
	private ChoiceNode fChoiceNode;
	private IPrimitiveTypePredicate fPredicate; // TODO NE-TE remove ?

	public ExpectedValueStatement(
			BasicParameterNode methodParameterNode,
			ChoiceNode choiceNode, 
			IPrimitiveTypePredicate predicate) {

		super(methodParameterNode.getModelChangeRegistrator());

		fLeftMethodParameterNode = methodParameterNode;
		fChoiceNode = choiceNode.makeClone();
		fPredicate = predicate;
	}

	@Override
	public String getLeftParameterName() {
		return fLeftMethodParameterNode.getName();
	}

	@Override
	public boolean mentions(AbstractParameterNode parameter) {
		return parameter == fLeftMethodParameterNode;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {
		return EvaluationResult.TRUE;
	}

	@Override
	public boolean setExpectedValues(List<ChoiceNode> testCaseChoices) {


		if (testCaseChoices == null) {
			return true;
		}

		if  (fLeftMethodParameterNode.getMethod() != null) {

			int index = fLeftMethodParameterNode.getMethod().getParameters().indexOf(fLeftMethodParameterNode);
			testCaseChoices.set(index, fChoiceNode.makeClone());
		}

		return true;
	}

	@Override
	public EMathRelation[] getAvailableRelations() {
		return new EMathRelation[]{EMathRelation.EQUAL};
	}

	@Override
	public EMathRelation getRelation() {
		return EMathRelation.EQUAL;
	}

	@Override
	public void setRelation(EMathRelation relation) {
	}

	@Override
	public boolean mentions(int methodParameterIndex) {

		MethodNode methodNode = fLeftMethodParameterNode.getMethod();
		AbstractParameterNode methodParameterNode = methodNode.getMethodParameter(methodParameterIndex);

		if (mentions(methodParameterNode)) {
			return true;
		}

		return false;
	}	

	@Override
	public List<ChoiceNode> getChoices() {

		List<ChoiceNode> result = new ArrayList<ChoiceNode>();
		result.add(fChoiceNode);

		return result;
	}

	@Override
	public List<ChoiceNode> getChoices(BasicParameterNode methodParameterNode) {

		BasicParameterNode abstractParameterNode = fChoiceNode.getParameter();

		if (!(abstractParameterNode instanceof BasicParameterNode)) {
			return null;
		}

		BasicParameterNode methodParameterNode2 = (BasicParameterNode) abstractParameterNode;

		if (!methodParameterNode2.equals(methodParameterNode)) {
			return null;
		}

		List<ChoiceNode> result = new ArrayList<ChoiceNode>();
		result.add(fChoiceNode);

		return result;
	}

	@Override
	public void derandomize() {
		fChoiceNode.derandomize();

	}

	public BasicParameterNode getLeftMethodParameterNode() {
		return fLeftMethodParameterNode;
	}

	public ChoiceNode getChoice() {
		return fChoiceNode;
	}

	public IPrimitiveTypePredicate getPredicate() {
		return fPredicate;
	}

	@Override
	public String toString(){
		return getLeftMethodParameterNode().getName() + getRelation().toString() + fChoiceNode.getValueString();
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager){

		final BasicParameterNode parameter = getLeftMethodParameterNode();

		return MethodParameterNodeHelper.getName(parameter, extLanguageManager) + getRelation().toString() + fChoiceNode.getValueString();
	}

	@Override
	public ExpectedValueStatement makeClone(){
		return new ExpectedValueStatement(fLeftMethodParameterNode, fChoiceNode.makeClone(), fPredicate);
	}

	@Override
	public ExpectedValueStatement createCopy(IParametersAndConstraintsParentNode method) {

		return new ExpectedValueStatement(makeCloneParameter(method), fChoiceNode.makeClone(), fPredicate);
	}

	public BasicParameterNode makeCloneParameter(IParametersAndConstraintsParentNode method) {

		for (AbstractParameterNode parameter : method.getParameters()) {
			if (parameter.getName().equals(fLeftMethodParameterNode.getName())) {
				return (BasicParameterNode) parameter;
			}
		}

		ExceptionHelper.reportRuntimeException("The referenced method does not contain the required parameter");

		return null;
	}

	@Override
	public boolean updateReferences(IParametersAndConstraintsParentNode method){
		BasicParameterNode parameter = (BasicParameterNode)method.findParameter(fLeftMethodParameterNode.getName());
		if(parameter != null && parameter.isExpected()){
			fLeftMethodParameterNode = parameter;
			fChoiceNode.setParent(parameter);
			String type = parameter.getType();

			if(fPredicate.isPrimitive(type) == false){
				ChoiceNode choice = parameter.getChoice(fChoiceNode.getQualifiedName());
				if(choice != null){
					fChoiceNode = choice;
				}
				else{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isEqualTo(IStatement statement){
		if(statement instanceof ExpectedValueStatement == false){
			return false;
		}

		ExpectedValueStatement compared = (ExpectedValueStatement)statement;
		if(getLeftMethodParameterNode().getName().equals(compared.getLeftMethodParameterNode().getName()) == false){
			return false;
		}

		if(getChoice().getValueString().equals(compared.getChoice().getValueString()) == false){
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	public boolean isParameterPrimitive(){
		return fPredicate.isPrimitive(fLeftMethodParameterNode.getType());
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> values, MessageStack messageStack, IExtLanguageManager extLanguageManager) {
		return false;
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> values) {
		return false;
	}

	@Override
	protected void convert(ParameterConversionItem parameterConversionItem) {

		ChoiceNode choiceNode = getChoice();
		String valueString = choiceNode.getValueString();

		String srcString = parameterConversionItem.getSrcPart().getStr();

		if (StringHelper.isEqual(srcString, valueString)) {
			String dstString = parameterConversionItem.getDstPart().getStr();
			choiceNode.setValueString(dstString);
		}

	}

	@Override
	public boolean mentionsChoiceOfParameter(BasicParameterNode parameter) {
		return false;
	}

	@Override
	public List<String> getLabels(BasicParameterNode methodParameterNode) {
		return new ArrayList<>();
	}

//	@Override
//	public AbstractStatement createDeepCopy(DeploymentMapper deploymentMapper) {
//
//		BasicParameterNode sourceParameter = getLeftMethodParameterNode();
//		BasicParameterNode deployedParameter = deploymentMapper.getDeployedParameterNode(sourceParameter);
//
//		ChoiceNode sourceChoiceNode = getChoice();
//		ChoiceNode deployedChoiceNode = deploymentMapper.getDeployedChoiceNode(sourceChoiceNode);
//
//		IPrimitiveTypePredicate deployedPredicate = getPredicate();
//
//		ExpectedValueStatement expectedValueStatement =
//				new ExpectedValueStatement(
//						deployedParameter,
//						deployedChoiceNode,
//						deployedPredicate);
//
//		return expectedValueStatement;
//	}

}
