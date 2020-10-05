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

public abstract class AbstractParameterNodeHelper {

	public static String validateParameterType(String parameterTypeInExtLanguage, IExtLanguageManager extLanguage) {

		return extLanguage.validateType(parameterTypeInExtLanguage);
	}

	public static String getName(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguage) {

		String name = extLanguage.convertTextFromIntrToExtLanguage(abstractParameterNode.getName());
		return name;
	}

	public static String createSignatureOfOneParameterByIntrLanguage(
			String parameterTypeInIntrLanguage,
			String parameterNameInIntrLanguage,
			Boolean expectedFlag,
			IExtLanguageManager extLanguage) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		parameterTypeInIntrLanguage = extLanguage.convertTypeFromIntrToExtLanguage(parameterTypeInIntrLanguage);

		signature += parameterTypeInIntrLanguage;

		if (parameterNameInIntrLanguage != null) {

			signature += " ";
			parameterNameInIntrLanguage = extLanguage.convertTextFromIntrToExtLanguage(parameterNameInIntrLanguage);

			signature += parameterNameInIntrLanguage;
		}

		return signature;
	}

	public static String createSignature(
			AbstractParameterNode abstractParameterNode, 
			boolean isExpected,
			IExtLanguageManager extLanguage) {

		String signature = 
				createSignature(
						createTypeSignature(abstractParameterNode, extLanguage),
						createNameSignature(abstractParameterNode, extLanguage),
						isExpected);

		return signature;
	}

	public static String createSignature(
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

	public static String createParameterSignature(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguage) {

		String name = abstractParameterNode.getName();
		name = extLanguage.convertTextFromIntrToExtLanguage(name);


		String type = createTypeSignature(abstractParameterNode, extLanguage);

		String label = type + " " + name;
		return label;
	}


	public static String createTypeSignature(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguage) {

		String type = abstractParameterNode.getType();
		type = extLanguage.convertTypeFromIntrToExtLanguage(type);
		return type;
	}

	public static String createNameSignature(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguage) {

		String name = abstractParameterNode.getName();
		name = extLanguage.convertTextFromIntrToExtLanguage(name);
		return name;
	}

	public static List<String> convertParameterTypesToExtLanguage(
			List<String> parameterTypes,
			IExtLanguageManager extLanguage) {

		List<String> result = new ArrayList<String>();

		for (String parameterType : parameterTypes) {

			parameterType = extLanguage.convertTypeFromIntrToExtLanguage(parameterType);
			result.add(parameterType);
		}

		return result;
	}

}
