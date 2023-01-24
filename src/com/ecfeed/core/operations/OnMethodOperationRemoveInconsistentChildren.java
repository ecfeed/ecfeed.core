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

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestCaseParentNodeHelper;
import com.ecfeed.core.utils.BooleanHolder;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnMethodOperationRemoveInconsistentChildren extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private List<ConstraintNode> fOriginalConstraints;
	private List<TestCaseNode> fOriginalTestCases;

	public OnMethodOperationRemoveInconsistentChildren(MethodNode target, IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.MAKE_CONSISTENT, extLanguageManager);
		
		fMethodNode = target;
		fOriginalConstraints = null;
		fOriginalTestCases = null;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fMethodNode);

		fOriginalConstraints = new ArrayList<ConstraintNode>(fMethodNode.getConstraintNodes());
		fOriginalTestCases = new ArrayList<TestCaseNode>(fMethodNode.getTestCases());

		BooleanHolder modelUpdated = new BooleanHolder(false);

		TestCaseParentNodeHelper.removeInconsistentTestCases(fMethodNode, modelUpdated);

		ConstraintsParentNodeHelper.removeInconsistentConstraints(fMethodNode, modelUpdated);

		if (modelUpdated.get()) {
			markModelUpdated();
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OperationNames.MAKE_CONSISTENT, extLanguageManager);
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fMethodNode);
			fMethodNode.replaceTestCases(fOriginalTestCases);
			fMethodNode.replaceConstraints(fOriginalConstraints);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OnMethodOperationRemoveInconsistentChildren(fMethodNode, getExtLanguageManager());
		}

	}

}
