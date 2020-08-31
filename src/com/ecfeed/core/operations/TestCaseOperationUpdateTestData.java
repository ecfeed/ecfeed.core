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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExtLanguage;

public class TestCaseOperationUpdateTestData extends AbstractModelOperation {

	private ChoiceNode fNewValue;
	private ChoiceNode fPreviousValue;
	private int fIndex;
	private TestCaseNode fTarget;

	public TestCaseOperationUpdateTestData(TestCaseNode target, int index, ChoiceNode value, ExtLanguage viewMode) {
		super(OperationNames.UPDATE_TEST_DATA, viewMode);
		fTarget = target;
		fIndex = index;
		fNewValue = value;
		fPreviousValue = target.getTestData().get(index);
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);

		if(fNewValue.getParameter() != fTarget.getTestData().get(fIndex).getParameter()){
			ModelOperationException.report(OperationMessages.TEST_DATA_CATEGORY_MISMATCH_PROBLEM);
		}

		fTarget.getTestData().set(fIndex, fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new TestCaseOperationUpdateTestData(fTarget, fIndex, fPreviousValue, getViewMode());
	}

}
