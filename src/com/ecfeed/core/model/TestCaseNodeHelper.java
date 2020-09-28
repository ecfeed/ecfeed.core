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

import java.util.List;

import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageHelper;

public class TestCaseNodeHelper {

	// TODO SIMPLE-VIEW unit tests
	public static String createSignature(TestCaseNode testCaseNode, ExtLanguage extLanguage) {

		String methodName = getMethodName(testCaseNode, extLanguage);

		String testCaseNodeName = getTestCaseNodeName(testCaseNode, extLanguage);

		String result = "[" + testCaseNodeName + "]";

		if (methodName != null) {
			result += " " + methodName + "(";
			result += getTestDataString(testCaseNode.getTestData(), extLanguage);
			result += ")";
		}

		return result;
	}

	public static String getTestDataString(TestCaseNode testCaseNode, ExtLanguage extLanguage) {

		return getTestDataString(testCaseNode.getTestData(), extLanguage);
	}

	// TODO SIMPLE-VIEW unit tests
	private static String getTestDataString(List<ChoiceNode> testData, ExtLanguage extLanguage) { 

		String result = new String();

		for (int index = 0; index < testData.size(); index++) {

			ChoiceNode choice = testData.get(index);
			result += ChoiceNodeHelper.createTestDataLabel(choice, extLanguage);

			if (index < testData.size() - 1) {
				result += ", ";
			}
		}

		return result;
	}

	private static String getTestCaseNodeName(
			TestCaseNode testCaseNode, 
			ExtLanguage extLanguage) { // TODO SIMPLE-VIEW not used

		return testCaseNode.getName();
	}

	private static String getMethodName(TestCaseNode testCaseNode, ExtLanguage extLanguage) {

		String methodName = "";

		AbstractNode parent = testCaseNode.getParent();

		if (parent != null){
			methodName = parent.getName();
			methodName = ExtLanguageHelper.convertTextFromIntrToExtLanguage(methodName, extLanguage);
		}

		return methodName;
	}

}
