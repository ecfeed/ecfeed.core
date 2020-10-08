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

	public static String createSignature(TestCaseNode testCaseNode, IExtLanguageManager extLanguageManager) {

		String methodName = getMethodName(testCaseNode, extLanguageManager);

		String testCaseNodeName = testCaseNode.getName();

		String result = "[" + testCaseNodeName + "]";

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

}
