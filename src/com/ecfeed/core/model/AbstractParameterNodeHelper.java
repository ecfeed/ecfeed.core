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

import com.ecfeed.core.utils.ExtLanguageHelper;
import com.ecfeed.core.utils.ExtLanguage;

// TODO SIMPLE-VIEW unit tests
public abstract class AbstractParameterNodeHelper {

	public static String createSignatureOfOneParameter(
			String parameterTypeInIntrLanguage,
			String parameterName,
			Boolean expectedFlag,
			ExtLanguage extLanguageOfTheResult) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		parameterTypeInIntrLanguage = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(parameterTypeInIntrLanguage, extLanguageOfTheResult);

		signature += parameterTypeInIntrLanguage;

		if (parameterName != null) {

			signature += " ";
			parameterName = ExtLanguageHelper.convertTextFromIntrToExtLanguage(parameterName, extLanguageOfTheResult);

			signature += parameterName;
		}

		return signature;
	}

	private static String createExpectedDecoration(Boolean expectedFlag) {

		String signature = "";

		if (expectedFlag != null) {
			if (expectedFlag == true) {
				signature += "[e]";
			}
		}

		return signature;
	}


	// TODO SIMPLE-VIEW remove
	public static String createLabel(AbstractParameterNode abstractParameterNode, ExtLanguage extLanguage) {

		String name = abstractParameterNode.getName();
		name = ExtLanguageHelper.convertTextFromIntrToExtLanguage(name, extLanguage);

		String type = abstractParameterNode.getType();
		type = createTypeLabel(type, extLanguage);

		String label = name + ": " + type;
		return label;
	}


	public static String createTypeLabel(String javaType, ExtLanguage extLanguage) {

		String type = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(javaType, extLanguage);
		return type;
	}
}
