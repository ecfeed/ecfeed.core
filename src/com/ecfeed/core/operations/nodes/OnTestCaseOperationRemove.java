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

import java.util.Optional;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnTestCaseOperationRemove extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestCaseNode fTestCase;
	private int fIndex;
	private int fTestSuiteIndex;

	public OnTestCaseOperationRemove(
			MethodNode target, TestCaseNode testCase, IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_TEST_CASE, extLanguageManager);

		fMethodNode = target;
		fTestCase = testCase;
		fIndex = testCase.getMyIndex();
		fTestSuiteIndex = fMethodNode.findTestSuiteIndex(testCase.getName());
	}

	@Override
	public void execute() {
		setOneNodeToSelect(fMethodNode);
		fIndex = fTestCase.getMyIndex();
		fMethodNode.removeTestCase(fTestCase);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnTestCaseOperationAddToMethod(
				fMethodNode, 
				fTestCase, 
				fIndex, 
				Optional.of(fTestSuiteIndex),
				getExtLanguageManager());
	}

}
