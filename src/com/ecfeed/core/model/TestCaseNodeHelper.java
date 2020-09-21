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

import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageHelper;

public class TestCaseNodeHelper {

	// TODO SIMPLE-VIEW unit tests
	public static String createSignature(TestCaseNode testCaseNode, ExtLanguage extLanguage) {

		String methodName = getMethodName(testCaseNode, extLanguage);

		String testCaseNodeName = getTestCaseNodeName(testCaseNode, extLanguage);

		String result = "[" + testCaseNodeName + "]";

		if (methodName != null) {
			result += ": " + methodName + "(";
			result += testCaseNode.testDataString();
			result += ")";
		}

		return result;

		//		return ExtLanguageHelper.convertTextFromIntrToExtLanguage(result, extLanguage);
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
