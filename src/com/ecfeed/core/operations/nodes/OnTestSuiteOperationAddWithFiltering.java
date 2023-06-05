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

import java.util.List;

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IntegerHolder;

public class OnTestSuiteOperationAddWithFiltering extends CompositeOperation {

	public static enum TestCasesFilteringDirection {
		POSITIVE,
		NEGATIVE,
	}

	public OnTestSuiteOperationAddWithFiltering(
			MethodNode methodNode,
			List<TestCaseNode> srcTestCaseNodes,
			String dstTestSuiteName,
			List<ConstraintNode> constraintNodes, 
			TestCasesFilteringDirection testCasesFilteringDirection,
			boolean includeAmbiguousTestCases,
			IntegerHolder outCountOfAddedTestCases,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_FILTERED_TEST_SUITES, false, methodNode, methodNode, extLanguageManager);

		List<TestCaseNode> filteredCaseNodes = 
				MethodNodeHelper.filterTestCases(methodNode, srcTestCaseNodes, dstTestSuiteName, constraintNodes, testCasesFilteringDirection,
						includeAmbiguousTestCases, outCountOfAddedTestCases);

		for (TestCaseNode testCaseNode : filteredCaseNodes) {

			addOperation(
					new OnTestCaseOperationAddToMethod(
							methodNode, 
							testCaseNode, 
							getExtLanguageManager()));
		}
	}


}
