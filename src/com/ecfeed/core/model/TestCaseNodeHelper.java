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

import com.ecfeed.core.utils.IExtLanguageManager;

public class TestCaseNodeHelper {

	public static String createSignature(TestCaseNode testCaseNode, IExtLanguageManager extLanguage) {

		String methodName = getMethodName(testCaseNode, extLanguage);

		String testCaseNodeName = testCaseNode.getName();

		String result = "[" + testCaseNodeName + "]";

		if (methodName != null) {
			result += " " + methodName + "(";
			result += getTestDataString(testCaseNode.getTestData(), extLanguage);
			result += ")";
		}

		return result;
	}

	public static String getTestDataString(TestCaseNode testCaseNode, IExtLanguageManager extLanguage) {

		return getTestDataString(testCaseNode.getTestData(), extLanguage);
	}

	private static String getTestDataString(List<ChoiceNode> testData, IExtLanguageManager extLanguage) {

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

	private static String getMethodName(TestCaseNode testCaseNode, IExtLanguageManager extLanguage) {

		AbstractNode parent = testCaseNode.getParent();

		if (parent == null) {
			return null;
		}

		return AbstractNodeHelper.getName(parent, extLanguage);
	}

}
