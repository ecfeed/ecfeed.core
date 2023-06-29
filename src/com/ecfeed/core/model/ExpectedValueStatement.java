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

import com.ecfeed.core.model.NodeMapper.MappingDirection;
import com.ecfeed.core.type.adapter.IPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.StringHelper;

public class ExpectedValueStatement extends AbstractStatement implements IRelationalStatement { // TODO MO-RE do we need it as there is assignment statement?

	private BasicParameterNode fLeftParameterNode;
	private CompositeParameterNode fLeftParameterLinkingContext;
	private ChoiceNode fChoiceNode;
	private IPrimitiveTypePredicate fPredicate; // TODO NE-TE remove ?

	public ExpectedValueStatement(
			BasicParameterNode basicParameterNode,
			CompositeParameterNode leftParameterLinkingContext,
			ChoiceNode choiceNode, 
			IPrimitiveTypePredicate predicate) {

		super(basicParameterNode.getModelChangeRegistrator());

		fLeftParameterNode = basicParameterNode;
		fChoiceNode = choiceNode.makeClone();
		fPredicate = predicate;
	}

	@Override
	public BasicParameterNode getLeftParameter() {
		return fLeftParameterNode;
	}

	@Override
	public CompositeParameterNode getLeftParameterLinkingContext() {
		return fLeftParameterLinkingContext;
	}

	@Override
	public String getLeftOperandName() {
		return fLeftParameterNode.getName();
	}

	@Override
	public boolean mentions(AbstractParameterNode parameter) {
		return parameter == fLeftParameterNode;
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

		IParametersParentNode parametersParent = (IParametersParentNode) fLeftParameterNode.getParent();
		if  (parametersParent != null) {

			int index = parametersParent.getParameters().indexOf(fLeftParameterNode);
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

		IParametersAndConstraintsParentNode methodNode = 
				(IParametersAndConstraintsParentNode) fLeftParameterNode.getParent();

		AbstractParameterNode methodParameterNode = methodNode.getParameter(methodParameterIndex);

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
		return fLeftParameterNode;
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
	public ExpectedValueStatement makeClone(Optional<NodeMapper> mapper) {

		if (mapper.isPresent()) {
			BasicParameterNode parameter = mapper.get().getDestinationNode(fLeftParameterNode); 
			ChoiceNode choice = mapper.get().getDestinationNode(fChoiceNode);

			return new ExpectedValueStatement(parameter, fLeftParameterLinkingContext, choice, fPredicate);
		}

		return new ExpectedValueStatement(
				fLeftParameterNode, fLeftParameterLinkingContext, fChoiceNode.makeClone(mapper), fPredicate);
	}
	
	@Override
	public void replaceReferences(NodeMapper nodeMapper, MappingDirection mappingDirection) {
		
		fLeftParameterNode = nodeMapper.getMappedNode(fLeftParameterNode, mappingDirection);
		fLeftParameterLinkingContext = nodeMapper.getMappedNode(fLeftParameterLinkingContext, mappingDirection);
		fChoiceNode = nodeMapper.getMappedNode(fChoiceNode, mappingDirection);
	}

	@Override
	public ExpectedValueStatement makeClone(){ // TODO MO-RE obsolete ?
		return new ExpectedValueStatement(
				fLeftParameterNode, fLeftParameterLinkingContext, fChoiceNode.makeClone(), fPredicate);
	}

	@Override
	public ExpectedValueStatement createCopy(NodeMapper mapper) { // TODO MO-RE obsolete ?
		BasicParameterNode parameter = mapper.getDestinationNode(fLeftParameterNode); 
		ChoiceNode choice = mapper.getDestinationNode(fChoiceNode);

		return new ExpectedValueStatement(parameter, fLeftParameterLinkingContext, choice, fPredicate);
	}

	//	@Override
	//	public boolean updateReferences(IParametersAndConstraintsParentNode method){
	//		BasicParameterNode parameter = (BasicParameterNode)method.findParameter(fLeftMethodParameterNode.getName());
	//		if(parameter != null && parameter.isExpected()){
	//			fLeftMethodParameterNode = parameter;
	//			fChoiceNode.setParent(parameter);
	//			String type = parameter.getType();
	//
	//			if(!fPredicate.isPrimitive(type)){
	//				ChoiceNode choice = parameter.getChoice(fChoiceNode.getQualifiedName());
	//				if(choice != null){
	//					fChoiceNode = choice;
	//				}
	//				else{
	//					return false;
	//				}
	//			}
	//			return true;
	//		}
	//		return false;
	//	}

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
		return fPredicate.isPrimitive(fLeftParameterNode.getType());
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

	@Override
	public boolean isConsistent(IParametersAndConstraintsParentNode parentMethodNode) {

		if (!BasicParameterNodeHelper.isParameterOfConstraintConsistent(
				fLeftParameterNode, fLeftParameterLinkingContext, parentMethodNode)) {

			return false;
		}

		return true;
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
