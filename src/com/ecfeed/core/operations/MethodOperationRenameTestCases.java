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

import java.util.Collection;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExtLanguageManager;
import com.ecfeed.core.utils.RegexHelper;

public class MethodOperationRenameTestCases extends BulkOperation {

	public MethodOperationRenameTestCases(
			Collection<TestCaseNode> testCases, 
			String newName,
			ExtLanguageManager extLanguage) throws ModelOperationException {

		super(OperationNames.RENAME_TEST_CASE, false, getFirstParent(testCases), getFirstParent(testCases), extLanguage);

		if (newName.matches(RegexHelper.REGEX_TEST_CASE_NODE_NAME) == false) {
			ModelOperationException.report(OperationMessages.TEST_CASE_NOT_ALLOWED);
		}

		for(TestCaseNode testCase : testCases){
			addOperation(FactoryRenameOperation.getRenameOperation(testCase, newName, extLanguage));
		}
	}

	private static AbstractNode getFirstParent(Collection<TestCaseNode> testCases) {

		if (testCases.isEmpty()) {
			return null;
		}

		for (TestCaseNode testCaseNode : testCases) {
			return testCaseNode.getParent();
		}

		return null;
	}
}
