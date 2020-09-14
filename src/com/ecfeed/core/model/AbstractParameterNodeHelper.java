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

public abstract class AbstractParameterNodeHelper {

	public static String validateParameterType(String parameterTypeInExtLanguage, ExtLanguage extLanguage) {

		return ExtLanguageHelper.validateType(parameterTypeInExtLanguage, extLanguage);
	}

	public static String createSignatureOfOneParameterByIntrLanguage(
			String parameterTypeInIntrLanguage,
			String parameterNameInIntrLanguage,
			Boolean expectedFlag,
			ExtLanguage extLanguageOfTheResult) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		parameterTypeInIntrLanguage = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(parameterTypeInIntrLanguage, extLanguageOfTheResult);

		signature += parameterTypeInIntrLanguage;

		if (parameterNameInIntrLanguage != null) {

			signature += " ";
			parameterNameInIntrLanguage = ExtLanguageHelper.convertTextFromIntrToExtLanguage(parameterNameInIntrLanguage, extLanguageOfTheResult);

			signature += parameterNameInIntrLanguage;
		}

		return signature;
	}

	public static String createSignatureOfOneParameter(
			String parameterType,
			String parameterName,
			Boolean expectedFlag) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		signature += parameterType;

		if (parameterName != null) {

			signature += " ";
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

	public static String createParameterLabel(AbstractParameterNode abstractParameterNode, ExtLanguage extLanguage) {

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