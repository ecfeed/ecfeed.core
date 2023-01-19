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

import com.ecfeed.core.model.ITestCasesParentNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OperationSimpleSetTestCases extends AbstractModelOperation {

	private static final String ADD_TEST_CASES = "Add test cases";

	ITestCasesParentNode fMethodNode;
	List<TestCaseNode> fTestCases;

	public OperationSimpleSetTestCases(
			ITestCasesParentNode methodNode,
			List<TestCaseNode> testCases,
			IExtLanguageManager extLanguageManager) {

		super(ADD_TEST_CASES, extLanguageManager);

		fMethodNode = methodNode;
		fTestCases = new ArrayList<>(testCases);
	}

	@Override
	public void execute() {

		fMethodNode.replaceTestCases(fTestCases);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OperationSimpleSetTestCases.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {

			fMethodNode.removeAllTestCases();
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OperationSimpleSetTestCases(fMethodNode, fTestCases, getExtLanguageManager());
		}

	}

}
