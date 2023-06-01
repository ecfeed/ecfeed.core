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
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestCaseNodeHelper;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.EvaluationResult;
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

		for (TestCaseNode srcTestCaseNode : srcTestCaseNodes) {

			boolean isTestCaseQualified = 
					qualifyTestCaseNode(
							srcTestCaseNode, constraintNodes, testCasesFilteringDirection, includeAmbiguousTestCases);

			if (isTestCaseQualified) {
				TestCaseNode destTestCaseNode = 
						new TestCaseNode(
								dstTestSuiteName, 
								methodNode.getModelChangeRegistrator(), 
								srcTestCaseNode.getTestData());

				addOperation(
						new OnTestCaseOperationAddToMethod(
								methodNode, 
								destTestCaseNode, 
								getExtLanguageManager()));

				outCountOfAddedTestCases.increment();
			}
		}
	}

	private boolean qualifyTestCaseNode(
			TestCaseNode testCaseNode, 
			List<ConstraintNode> constraintNodes,
			TestCasesFilteringDirection testCasesFilteringDirection,
			boolean includeAmbiguousTestCases) {

		if (TestCaseNodeHelper.isTestCaseNodeAmbiguous(testCaseNode, constraintNodes)) {

			if (includeAmbiguousTestCases) {
				return true;
			} else {
				return false;
			}
		}

		for (ConstraintNode constraintNode : constraintNodes) {

			ConstraintType constraintType = constraintNode.getConstraint().getType();

			if (constraintType == ConstraintType.ASSIGNMENT) {
				continue;
			}

			if (!qualifyTestCaseNodeByOneConstraint(
					testCaseNode, constraintNode, testCasesFilteringDirection, includeAmbiguousTestCases)) {
				return false;
			}
		}

		return true;
	}

	private boolean qualifyTestCaseNodeByOneConstraint(
			TestCaseNode testCaseNode, 
			ConstraintNode constraintNode,
			TestCasesFilteringDirection testCasesFilteringDirection,
			boolean includeAmbiguousTestCases) {

		EvaluationResult evaluationResult =  constraintNode.evaluate(testCaseNode.getTestData());

		if (evaluationResult == EvaluationResult.INSUFFICIENT_DATA) {

			if (includeAmbiguousTestCases) {
				return true;
			} else {
				return false;
			}
		}

		if (evaluationResult == EvaluationResult.TRUE 
				&& testCasesFilteringDirection == TestCasesFilteringDirection.POSITIVE) {
			return true;
		}

		if (evaluationResult == EvaluationResult.FALSE 
				&& testCasesFilteringDirection == TestCasesFilteringDirection.NEGATIVE) {
			return true;
		}

		return false;
	}

}
