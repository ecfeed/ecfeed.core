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

		String result = testCaseNode.toString();

		return ExtLanguageHelper.convertTextFromIntrToExtLanguage(result, extLanguage);
	}

}
