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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecfeed.core.model.utils.JavaNodeNameHelper;
import com.ecfeed.core.utils.AmbiguousConstraintAction;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.NameHelper;
import com.ecfeed.core.utils.StringHelper;

public class TestCaseNodeHelper {

	public static String createShortSignature(
			TestCaseNode testCaseNode, boolean displayTestSuiteName, IExtLanguageManager extLanguageManager) {

		String methodName = getMethodName(testCaseNode, extLanguageManager);

		String result = "";

		if (displayTestSuiteName) { 
			String testCaseNodeName = testCaseNode.getName();

			result += "[" + testCaseNodeName + "]";
		}

		if (methodName != null) {
			result += " " + methodName + "(";
			result += getShortTestDataString(testCaseNode.getTestData(), extLanguageManager);
			result += ")";
		}

		return result;
	}

	public static String createSignature(
			TestCaseNode testCaseNode, boolean displayTestSuiteName, IExtLanguageManager extLanguageManager) {

		String methodName = getMethodName(testCaseNode, extLanguageManager);

		String result = "";

		if (displayTestSuiteName) { 
			String testCaseNodeName = testCaseNode.getName();

			result += "[" + testCaseNodeName + "]";
		}

		if (methodName != null) {
			result += " " + methodName + "(";
			result += getTestDataString(testCaseNode.getTestData(), extLanguageManager);
			result += ")";
		}

		return result;
	}

	public static String getTestDataString(TestCaseNode testCaseNode, IExtLanguageManager extLanguageManager) {

		return getTestDataString(testCaseNode.getTestData(), extLanguageManager);
	}

	public static String getShortTestDataString(TestCaseNode testCaseNode, IExtLanguageManager extLanguageManager) {

		return getShortTestDataString(testCaseNode.getTestData(), extLanguageManager);
	}

	private static String getTestDataString(List<ChoiceNode> testData, IExtLanguageManager extLanguageManager) {

		String result = new String();

		for (int index = 0; index < testData.size(); index++) {

			ChoiceNode choice = testData.get(index);
			result += createSignatureOfChoice(choice, extLanguageManager);

			if (index < testData.size() - 1) {
				result += ", ";
			}
		}

		return result;
	}

	public static String createSignatureOfChoice(ChoiceNode choiceNode, IExtLanguageManager extLanguageManager) {

		BasicParameterNode basicParameterNode = choiceNode.getParameter();	

		if (basicParameterNode.isExpected()) {
			String valueString = ChoiceNodeHelper.getValueString(choiceNode, extLanguageManager);
			return valueString;

		} else {
			String choiceQualifiedName = ChoiceNodeHelper.getQualifiedName(choiceNode, extLanguageManager);
			return choiceQualifiedName;
		}
	}

	private static String getShortTestDataString(List<ChoiceNode> testData, IExtLanguageManager extLanguageManager) {

		String result = new String();




		for (int index = 0; index < testData.size(); index++) {

			ChoiceNode choice = testData.get(index);
			result += choice.getName();

			if (index < testData.size() - 1) {
				result += ", ";
			}
		}

		return result;
	}

	private static String getMethodName(TestCaseNode testCaseNode, IExtLanguageManager extLanguageManager) {

		IAbstractNode parent = testCaseNode.getParent();

		if (parent == null) {
			return null;
		}

		return AbstractNodeHelper.getName(parent, extLanguageManager);
	}

	public static TestCaseNode makeDerandomizedClone(TestCaseNode testCaseNode) {

		List<ChoiceNode> testData = testCaseNode.getTestData();

		List<ChoiceNode> clonedTestData = new ArrayList<>();

		for (ChoiceNode choice : testData) {

			ChoiceNode clonedChoiceNode = ChoiceNodeHelper.makeDerandomizedClone(choice);
			clonedTestData.add(clonedChoiceNode);
		}

		TestCaseNode clonedTestCaseNode = new TestCaseNode(testCaseNode.getName(), null, clonedTestData);

		clonedTestCaseNode.setProperties(testCaseNode.getProperties());
		clonedTestCaseNode.setParent(testCaseNode.getMethod());

		return clonedTestCaseNode;
	}

	public static List<TestCaseNode> createListOfTestCaseNodes(
			List<TestCase> testCases, 
			MethodNode methodNode) {

		List<TestCaseNode> testCaseNodes = new ArrayList<>();

		for (TestCase testCase : testCases) {

			TestCaseNode testCaseNode = createTestCaseNode(testCase, methodNode);
			testCaseNodes.add(testCaseNode);
		}

		return testCaseNodes;
	}

	private static TestCaseNode createTestCaseNode(TestCase testCase, MethodNode methodNode) {

		TestCase newTestCase = new TestCase();

		List<ChoiceNode> choiceNodes = testCase.getListOfChoiceNodes(); 

		for (ChoiceNode choiceNode : choiceNodes) {

			ChoiceNode newChoiceNode = choiceNode.makeClone();
			newTestCase.add(newChoiceNode);
		}

		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, newTestCase.getListOfChoiceNodes());
		testCaseNode.setParent(methodNode);

		for (ChoiceNode choiceNode : testCaseNode.getTestData()) {
			choiceNode.setParent(testCaseNode);
		}

		return testCaseNode;
	}

	public static boolean evaluateConstraints(TestCaseNode testCase, List<ConstraintNode> constraintNodes) {

		for (ConstraintNode constraintNode : constraintNodes) {

			ConstraintType constraintType = constraintNode.getConstraint().getType();

			if (constraintType == ConstraintType.ASSIGNMENT) {
				continue;
			}

			EvaluationResult evaluationResult =  constraintNode.evaluate(testCase.getTestData());

			if (evaluationResult == EvaluationResult.FALSE) {
				return false;
			}
		}

		return true;
	}

	public static boolean assignExpectedValues(TestCaseNode testCase, List<ConstraintNode> constraintNodes) {

		for (ConstraintNode constraintNode : constraintNodes) {

			Constraint constraint = constraintNode.getConstraint();

			ConstraintType constraintType = constraint.getType();

			if (constraintType != ConstraintType.ASSIGNMENT) {
				continue;
			}

			constraint.setExpectedValues(testCase.getTestData());
		}

		return true;
	}

	public static boolean isTestCaseNodeAmbiguous(
			TestCaseNode testCaseNode,
			List<ConstraintNode> constraintNodes,
			MessageStack messageStack,
			IExtLanguageManager extLanguageManager) {

		TestCase testCase = testCaseNode.getTestCase();

		List<Constraint> constraints = ConstraintNodeHelper.createListOfConstraints(constraintNodes);

		boolean isAmbiguous = 
				TestCaseHelper.isTestCaseAmbiguous(
						testCase,
						constraints,
						messageStack,
						extLanguageManager);

		return isAmbiguous;
	}

	public static boolean isTestCaseNodeAmbiguous(
			TestCaseNode testCaseNode,
			List<ConstraintNode> constraintNodes) {

		TestCase testCase = testCaseNode.getTestCase();

		List<Constraint> constraints = ConstraintNodeHelper.createListOfConstraints(constraintNodes);

		boolean isAmbiguous = TestCaseHelper.isTestCaseAmbiguous(testCase, constraints);

		return isAmbiguous;
	}

	public static boolean isTestCaseNodeAmbiguousForListOfConstraints(
			TestCaseNode testCaseNode,
			List<Constraint> constraints) {

		TestCase testCase = testCaseNode.getTestCase();

		boolean isAmbiguous = TestCaseHelper.isTestCaseAmbiguous(testCase, constraints);

		return isAmbiguous;
	}

	public static List<TestCaseNode> makeDerandomizedCopyOfTestCaseNodes(List<TestCaseNode> testCaseNodes) {

		List<TestCaseNode> clonedTestCaseNodes = new ArrayList<TestCaseNode>();

		for (TestCaseNode testCaseNode : testCaseNodes) {

			TestCaseNode clonedCaseNode = makeDerandomizedClone(testCaseNode);
			clonedTestCaseNodes.add(clonedCaseNode);
		}

		return clonedTestCaseNodes;
	}

	public static List<TestCaseNode> filterTestCaseNodesVsAmbiguity(
			List<TestCaseNode> testCaseNodes, 
			List<ConstraintNode> constraintNodes, 
			AmbiguousConstraintAction ambiguousConstraintAction) {

		List<TestCaseNode> filteredTestCaseNodes = new ArrayList<TestCaseNode>();

		for (TestCaseNode testCaseNode : testCaseNodes) {

			if (!shouldIncludeTestCase1(constraintNodes, testCaseNode, ambiguousConstraintAction)) {
				continue;
			}

			filteredTestCaseNodes.add(testCaseNode);
		}

		return filteredTestCaseNodes;
	}

	private static boolean shouldIncludeTestCase1(
			List<ConstraintNode> constraintNodes, 
			TestCaseNode testCaseNode, 
			AmbiguousConstraintAction ambiguousConstraintAction) {

		if (ambiguousConstraintAction == AmbiguousConstraintAction.INCLUDE) {
			return true;
		}

		if (ambiguousConstraintAction == AmbiguousConstraintAction.EVALUATE) {
			return true;
		}

		if (ambiguousConstraintAction == AmbiguousConstraintAction.EXCLUDE) {

			boolean isAmbiguous = isTestCaseNodeAmbiguous(testCaseNode, constraintNodes);

			if (isAmbiguous) {
				return false;
			} else {
				return true;
			}
		}

		ExceptionHelper.reportRuntimeException("Invalid ambiguous constraint action.");
		return false;
	}

	public static List<TestCaseNode> filterNotAmbiguousTestCases(
			List<TestCaseNode> testCaseNodes,
			List<ConstraintNode> constraintNodes) {

		List<TestCaseNode> filteredTestCaseNodes = new ArrayList<TestCaseNode>();

		for (TestCaseNode testCaseNode : testCaseNodes) {

			if (!shouldIncludeTestCase2(constraintNodes, testCaseNode)) {
				continue;
			}

			filteredTestCaseNodes.add(testCaseNode);
		}

		return filteredTestCaseNodes;
	}

	private static boolean shouldIncludeTestCase2(
			List<ConstraintNode> constraintNodes, 
			TestCaseNode testCaseNode) {

		if (isTestCaseNodeAmbiguous(testCaseNode, constraintNodes)) {
			return true;
		}

		boolean isIncluded = TestCaseNodeHelper.evaluateConstraints(testCaseNode, constraintNodes);

		if (isIncluded) {
			return true;
		}

		return false;
	}

	public static List<List<ChoiceNode>> convertToDoubleListOfChoices(Collection<TestCaseNode> testCaseNodes) {

		List<List<ChoiceNode>> result = new ArrayList<>();

		if (testCaseNodes != null) {
			for (TestCaseNode testCaseNode : testCaseNodes) {

				List<ChoiceNode> listOfChoiceNodes = testCaseNode.getTestCase().getListOfChoiceNodes();

				result.add(listOfChoiceNodes);
			}
		}

		return result;
	}

	public static void compareTestCases(TestCaseNode testCase1, TestCaseNode testCase2) {

		NameHelper.compareNames(testCase1.getName(), testCase2.getName());

		AbstractNodeHelper.compareSizes(testCase1.getTestData(), testCase2.getTestData(), "Number of choices differs.");

		for(int i = 0; i < testCase1.getTestData().size(); i++){

			ChoiceNode choiceNode1 = testCase1.getTestData().get(i);
			ChoiceNode choiceNode2 = testCase2.getTestData().get(i);

			if(choiceNode1.getParameter() instanceof BasicParameterNode){
				StringHelper.compareStrings(choiceNode1.getValueString(), choiceNode2.getValueString(), "Choice values differ.");
			}
			else{
				ChoiceNodeHelper.compareChoices(testCase1.getTestData().get(i),testCase2.getTestData().get(i));
			}
		}
	}

	public static boolean isValidTestCaseName(String name) { // XYX use java node name helper

		return name.matches(JavaNodeNameHelper.REGEX_TEST_CASE_NODE_NAME);
	}

}
