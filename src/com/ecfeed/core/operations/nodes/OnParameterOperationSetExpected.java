/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintNodeListHolder;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IConstraintsParentNode;
import com.ecfeed.core.model.ITestCasesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class OnParameterOperationSetExpected extends AbstractModelOperation {

	private BasicParameterNode fTarget;
	private boolean fExpected;
	private Optional<NodeMapper> fNodeMapper;
	private List<TestCaseNode> fOriginalTestCases;
	private List<ConstraintNode> fOriginalConstraints;
	private List<ChoiceNode> fOriginalChoices;
	private String fOriginalDefaultValue;

	public OnParameterOperationSetExpected(
			BasicParameterNode target, 
			boolean expected, 
			Optional<NodeMapper> nodeMapper,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.SET_EXPECTED_STATUS, extLanguageManager);
		fTarget = target;
		fExpected = expected;
		fNodeMapper = nodeMapper;

		IAbstractNode method = target.getParent(); 
		
		if(method != null){
			fOriginalTestCases = new ArrayList<TestCaseNode>();
			
			if (method instanceof ITestCasesParentNode) {
				fOriginalTestCases.addAll(((ITestCasesParentNode)method).getTestCases());
			}
			
			if (method instanceof IConstraintsParentNode) {
				fOriginalConstraints = new ArrayList<ConstraintNode>();
				fOriginalConstraints.addAll(((IConstraintsParentNode)method).getConstraintNodes());
			}
		}
		fOriginalChoices = new ArrayList<ChoiceNode>();
		fOriginalChoices.addAll(fTarget.getChoices());
		fOriginalDefaultValue = fTarget.getDefaultValue();
	}
	
	@Override
	public void execute() {

		setOneNodeToSelect(fTarget);

		fTarget.setExpected(fExpected);
		String type = fTarget.getType();
		if(fExpected && JavaLanguageHelper.hasLimitedValuesSet(type)){
			boolean validDefaultValue = false;
			String currentDefaultValue = fTarget.getDefaultValue();
			for(ChoiceNode leaf : fTarget.getLeafChoices()){
				if(currentDefaultValue.equals(leaf.getValueString())){
					validDefaultValue = true;
					break;
				}
			}
			if(validDefaultValue == false){
				if(fTarget.getLeafChoices().size() > 0){
					fTarget.setDefaultValueString(fTarget.getLeafChoices().toArray(new ChoiceNode[]{})[0].getValueString());
				}
				else{
					fTarget.addChoice(new ChoiceNode("choice", currentDefaultValue, fTarget.getModelChangeRegistrator()));
				}
			}
		}

		MethodNode methodNode = (MethodNode) fTarget.getParent(); 
		if(methodNode != null){
			int index = fTarget.getMyIndex();
			ListIterator<TestCaseNode> tcIt = methodNode.getTestCases().listIterator();
			while(tcIt.hasNext()){
				TestCaseNode testCase = tcIt.next();
				if(fExpected){
					ChoiceNode p = 
							new ChoiceNode(
									AssignmentStatement.ASSIGNMENT_CHOICE_NAME, 
									fTarget.getDefaultValue(), 
									fTarget.getModelChangeRegistrator());
					
					p.setParent(fTarget);
					TestCaseNode newTestCase = testCase.makeClone(fNodeMapper);
					newTestCase.setParent(methodNode);
					newTestCase.getTestData().set(index, p.makeClone());
					tcIt.set(newTestCase);
				}
				else{
					tcIt.remove();
				}
			}

			ConstraintNodeListHolder.ConstraintsItr constraintItr = methodNode.getIterator();
			while(methodNode.hasNextConstraint(constraintItr)){
				if(methodNode.getNextConstraint(constraintItr).mentions(fTarget)){
					methodNode.removeConstraint(constraintItr);
				}
			}
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	protected BasicParameterNode getOwnNode(){
		return fTarget;
	}

	protected boolean getExpected(){
		return fExpected;
	}

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OnParameterOperationSetExpected.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fTarget);

			fTarget.setExpected(!fExpected);
			
			IAbstractNode parent = fTarget.getParent();
			
			if (parent != null) { 
				
				if (parent instanceof IConstraintsParentNode) {
					((IConstraintsParentNode)parent).replaceConstraints(fOriginalConstraints);
				}
				
				if (parent instanceof ITestCasesParentNode) {
					((ITestCasesParentNode)parent).setTestCases(fOriginalTestCases);
				}
			}
			
			fTarget.replaceChoices(fOriginalChoices);
			fTarget.setDefaultValueString(fOriginalDefaultValue);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OnParameterOperationSetExpected(fTarget, fExpected, fNodeMapper, getExtLanguageManager());
		}

	}
	
}
