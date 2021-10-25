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
import java.util.List;

import com.ecfeed.core.utils.IExtLanguageManager;

public class TestCaseNodeHelper {

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

	private static String getTestDataString(List<ChoiceNode> testData, IExtLanguageManager extLanguageManager) {

		String result = new String();

		for (int index = 0; index < testData.size(); index++) {

			ChoiceNode choice = testData.get(index);
			result += ChoiceNodeHelper.createTestDataLabel(choice, extLanguageManager);

			if (index < testData.size() - 1) {
				result += ", ";
			}
		}

		return result;
	}

	private static String getMethodName(TestCaseNode testCaseNode, IExtLanguageManager extLanguageManager) {

		AbstractNode parent = testCaseNode.getParent();

		if (parent == null) {
			return null;
		}

		return AbstractNodeHelper.getName(parent, extLanguageManager);
	}

	public static TestCaseNode makeCloneWithoutRandomization(TestCaseNode testCaseNode) {

		List<ChoiceNode> testData = testCaseNode.getTestData();

		List<ChoiceNode> clonedTestData = new ArrayList<>();

		for (ChoiceNode choice : testData) {

			ChoiceNode clonedChoiceNode = ChoiceNodeHelper.makeUnrandomizedClone(choice);
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

		TestCaseNode testCaseNode = new TestCaseNode(newTestCase.getListOfChoiceNodes());
		testCaseNode.setParent(methodNode);

		for (ChoiceNode choiceNode : testCaseNode.getTestData()) {
			choiceNode.setParent(testCaseNode);
		}

		return testCaseNode;
	}

}
