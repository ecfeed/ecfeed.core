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

import java.util.List;

import com.ecfeed.core.model.ITestCasesParentNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OperationSimpleRemoveAllTestCases extends AbstractModelOperation {

	private static final String ADD_TEST_CASES = "Add test cases";

	ITestCasesParentNode fMethodNode;
	List<TestCaseNode> fOriginalTestCases;

	public OperationSimpleRemoveAllTestCases(
			ITestCasesParentNode methodNode,
			IExtLanguageManager extLanguageManager) {

		super(ADD_TEST_CASES, extLanguageManager);

		fMethodNode = methodNode;
		fOriginalTestCases = methodNode.getTestCases();
	}

	@Override
	public void execute() {

		fMethodNode.removeTestCases();
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super("Reverse operation to " + ADD_TEST_CASES, extLanguageManager);
		}

		@Override
		public void execute() {

			fMethodNode.replaceTestCases(fOriginalTestCases);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}

	}

}
