/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.TestCasesFilteringDirection;

import java.util.ArrayList;
import java.util.List;

public class TestCaseHelper {

	public static List<TestCase> createListOfTestCases(List<TestCaseNode> testCasesNodes) {

		List<TestCase> result = new ArrayList<>();

		for (TestCaseNode testCaseNode : testCasesNodes) {

			TestCase testCase = testCaseNode.getTestCase();
			result.add(testCase);
		}

		return result;
	}

	public static TestCase createTestCase(TestCaseNode testCaseNode) {

		TestCase testCase = new TestCase();

		for (ChoiceNode choiceNode : testCaseNode.getTestData()) {

			ChoiceNode newChoiceNode = choiceNode.makeClone();
			testCase.add(newChoiceNode);
		}

		return testCase;
	}

	public static boolean isTestCaseAmbiguous(
			TestCase testCase,
			List<Constraint> constraints,
			MessageStack messageStack,
			IExtLanguageManager extLanguageManager) {

		List<List<ChoiceNode>> testDomainForOneTestCase = createTestDomainWithOneTestCase(testCase);

		for (Constraint constraint : constraints) {

			if (constraint.isAmbiguous(testDomainForOneTestCase, messageStack, extLanguageManager)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isTestCaseAmbiguous(
			TestCase testCase,
			List<Constraint> constraints) {

		List<List<ChoiceNode>> testDomainForOneTestCase = createTestDomainWithOneTestCase(testCase);

		for (Constraint constraint : constraints) {

			if (constraint.isAmbiguous(testDomainForOneTestCase)) {
				return true;
			}
		}

		return false;
	}
	
	public static void setExpectedValuesToTestCaseChoices(
			List<ChoiceNode> listOfChoiceNodes,
			List<Constraint> constraints) {

		for (Constraint constraint : constraints) {
			constraint.setExpectedValues(listOfChoiceNodes);
		}
	}

	private static List<List<ChoiceNode>> createTestDomainWithOneTestCase(TestCase testCase) {

		List<List<ChoiceNode>> testDomainWithOneTestCase = new ArrayList<>();

		List<ChoiceNode> listOfChoices = testCase.getListOfChoiceNodes();

		for (ChoiceNode choiceNode : listOfChoices) {

			List<ChoiceNode> listWithOneChoice = new ArrayList<>();

			listWithOneChoice.add(choiceNode);

			testDomainWithOneTestCase.add(listWithOneChoice);
		}

		return testDomainWithOneTestCase;
	}

	public static boolean qualifyTestCaseNode(
			TestCase testCase, 
			List<Constraint> constraints,
			TestCasesFilteringDirection testCasesFilteringDirection,
			boolean includeAmbiguousTestCases) {

		if (TestCaseHelper.isTestCaseAmbiguous(testCase, constraints)) {

			if (includeAmbiguousTestCases) {
				return true;
			} else {
				return false;
			}
		}

		for (Constraint constraint : constraints) {

			ConstraintType constraintType = constraint.getType();

			if (constraintType == ConstraintType.ASSIGNMENT) {
				continue;
			}

			if (!qualifyTestCaseNodeByOneConstraint(
					testCase, constraint, testCasesFilteringDirection, includeAmbiguousTestCases)) {
				return false;
			}
		}

		return true;
	}

	private static boolean qualifyTestCaseNodeByOneConstraint(
			TestCase testCase, 
			Constraint constraint,
			TestCasesFilteringDirection testCasesFilteringDirection,
			boolean includeAmbiguousTestCases) {

		EvaluationResult evaluationResult =  constraint.evaluate(testCase.getListOfChoiceNodes());

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
