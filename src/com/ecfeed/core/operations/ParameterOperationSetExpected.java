/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelConstants;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ExtLanguage;

public class ParameterOperationSetExpected extends AbstractModelOperation {

	private MethodParameterNode fTarget;
	private boolean fExpected;
	private List<TestCaseNode> fOriginalTestCases;
	private List<ConstraintNode> fOriginalConstraints;
	private List<ChoiceNode> fOriginalChoices;
	private String fOriginalDefaultValue;

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation(ExtLanguage extLanguage) {
			super(ParameterOperationSetExpected.this.getName(), extLanguage);
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fTarget);

			fTarget.setExpected(!fExpected);
			if (fTarget.getMethod() != null) { 
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
			}
			fTarget.replaceChoices(fOriginalChoices);
			fTarget.setDefaultValueString(fOriginalDefaultValue);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ParameterOperationSetExpected(fTarget, fExpected, getExtLanguage());
		}

	}

	public ParameterOperationSetExpected(MethodParameterNode target, boolean expected, ExtLanguage extLanguage){
		super(OperationNames.SET_EXPECTED_STATUS, extLanguage);
		fTarget = target;
		fExpected = expected;

		MethodNode method = target.getMethod(); 
		if(method != null){
			fOriginalTestCases = new ArrayList<TestCaseNode>();
			fOriginalTestCases.addAll(method.getTestCases());
			fOriginalConstraints = new ArrayList<ConstraintNode>();
			fOriginalConstraints.addAll(method.getConstraintNodes());
		}
		fOriginalChoices = new ArrayList<ChoiceNode>();
		fOriginalChoices.addAll(fTarget.getChoices());
		fOriginalDefaultValue = fTarget.getDefaultValue();
	}

	@Override
	public void execute() throws ModelOperationException {

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
					fTarget.addChoice(new ChoiceNode("choice", fTarget.getModelChangeRegistrator(), currentDefaultValue));
				}
			}
		}

		MethodNode methodNode = fTarget.getMethod(); 
		if(methodNode != null){
			int index = fTarget.getMyIndex();
			ListIterator<TestCaseNode> tcIt = methodNode.getTestCases().listIterator();
			while(tcIt.hasNext()){
				TestCaseNode testCase = tcIt.next();
				if(fExpected){
					ChoiceNode p = 
							new ChoiceNode(
									ModelConstants.EXPECTED_VALUE_CHOICE_NAME, 
									fTarget.getModelChangeRegistrator(), 
									fTarget.getDefaultValue());
					
					p.setParent(fTarget);
					TestCaseNode newTestCase = testCase.makeClone();
					newTestCase.setParent(methodNode);
					newTestCase.getTestData().set(index, p.makeClone());
					tcIt.set(newTestCase);
				}
				else{
					tcIt.remove();
				}
			}

			MethodNode.ConstraintsItr constraintItr = methodNode.getIterator();
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
		return new ReverseOperation(getExtLanguage());
	}

	protected MethodParameterNode getOwnNode(){
		return fTarget;
	}

	protected boolean getExpected(){
		return fExpected;
	}

}
