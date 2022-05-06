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
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;

public class ExpectedValueStatement extends AbstractStatement implements IRelationalStatement {

	MethodParameterNode fLeftParameter;
	ChoiceNode fChoiceNode;
	private IPrimitiveTypePredicate fPredicate;

	public ExpectedValueStatement(
			MethodParameterNode parameter, 
			ChoiceNode choiceNode, 
			IPrimitiveTypePredicate predicate) {

		super(parameter.getModelChangeRegistrator());

		fLeftParameter = parameter;
		fChoiceNode = choiceNode.makeClone();
		fPredicate = predicate;
	}

	@Override
	public String getLeftParameterName() {
		return fLeftParameter.getName();
	}

	@Override
	public boolean mentions(AbstractParameterNode parameter) {
		return parameter == fLeftParameter;
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

		if  (fLeftParameter.getMethod() != null) {

			int index = fLeftParameter.getMethod().getParameters().indexOf(fLeftParameter);
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

		MethodNode methodNode = fLeftParameter.getMethod();
		MethodParameterNode methodParameterNode = methodNode.getMethodParameter(methodParameterIndex);

		if (mentions(methodParameterNode)) {
			return true;
		}

		return false;
	}	

	@Override
	public List<ChoiceNode> getListOfChoices() {

		List<ChoiceNode> result = new ArrayList<ChoiceNode>();
		result.add(fChoiceNode);

		return result;
	}

	@Override
	public void derandomize() {
		fChoiceNode.derandomize();

	}

	public MethodParameterNode getParameter(){ // TODO RENAME TO getLeftParameter
		return fLeftParameter;
	}

	public ChoiceNode getCondition(){
		return fChoiceNode;
	}

	@Override
	public String toString(){
		return getParameter().getName() + getRelation().toString() + fChoiceNode.getValueString();
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager){

		final MethodParameterNode parameter = getParameter();

		return MethodParameterNodeHelper.getName(parameter, extLanguageManager) + getRelation().toString() + fChoiceNode.getValueString();
	}

	@Override
	public ExpectedValueStatement makeClone(){
		return new ExpectedValueStatement(fLeftParameter, fChoiceNode.makeClone(), fPredicate);
	}

	@Override
	public boolean updateReferences(MethodNode method){
		MethodParameterNode parameter = (MethodParameterNode)method.findParameter(fLeftParameter.getName());
		if(parameter != null && parameter.isExpected()){
			fLeftParameter = parameter;
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
		if(getParameter().getName().equals(compared.getParameter().getName()) == false){
			return false;
		}

		if(getCondition().getValueString().equals(compared.getCondition().getValueString()) == false){
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	public boolean isParameterPrimitive(){
		return fPredicate.isPrimitive(fLeftParameter.getType());
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
	protected void updateChoiceReferences(
			ChoiceNode oldChoiceNode, 
			ChoiceNode newChoiceNode, 
			ListOfModelOperations reverseOperations,
			IExtLanguageManager extLanguageManager) {
	}

	@Override
	protected void updateParameterReferences(
			MethodParameterNode srcMethodParameterNode,
			ChoicesParentNode dstParameterForChoices,
			ListOfModelOperations reverseOperations,
			IExtLanguageManager extLanguageManager) {
	}

}
