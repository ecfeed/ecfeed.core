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

import com.ecfeed.core.utils.*;

public class MethodNodeHelper {


	public static String createSignature(MethodNode methodNode, ExtLanguage extLanguage) { 

		return MethodNodeHelper.createSignature(
				methodNode,
				false, extLanguage);
	}

	public static String createSignature(
			MethodNode methodNode,
			boolean isExpectedDecorationAdded, 
			ExtLanguage extLanguage) {

		String signature = 
				createSignature(
						methodNode.getName(),
						methodNode.getParameterTypes(),
						methodNode.getParametersNames(),
						getExpectedParametersFlags(methodNode.getMethodParameters()),
						isExpectedDecorationAdded,
						extLanguage);

		return signature;
	}

	public static String createLongSignature(MethodNode methodNode, ExtLanguage extLanguage) {

		String shortSignature = createSignature(methodNode, extLanguage);

		return methodNode.getParent().getName() + "." + shortSignature;
	}

	public static String createSignatureWithExpectedDecorations(MethodNode methodNode, ExtLanguage extLanguage) {

		String signature = createSignature(methodNode, true, extLanguage);

		return signature;
	}


	public static String createSignature(
			String fullName,
			List<String> parameterTypes,
			List<String> parameterNames, 
			List<Boolean> expectedFlags, 
			boolean isExpectedDecorationAdded, ExtLanguage extLanguage) {

		fullName = ExtLanguageHelper.convertTextFromIntrToExtLanguage(fullName, extLanguage);

		String signature = new String(fullName) + "(";

		String signaturesOfParameters = 
				createSignaturesOfParameters(
						parameterTypes, parameterNames, expectedFlags, 
						isExpectedDecorationAdded, extLanguage);

		signature += signaturesOfParameters;

		signature += ")";

		return signature;
	}

	private static String createSignaturesOfParameters(
			List<String> types, 
			List<String> parameterNames,
			List<Boolean> expectedFlags, 
			boolean isExpectedDecorationAdded, 
			ExtLanguage extLanguage) {

		String signature = "";

		for (int paramIndex = 0; paramIndex < types.size(); paramIndex++) {

			String parameterType = types.get(paramIndex);
			String parameterName = (parameterNames != null ? parameterNames.get(paramIndex) : null);
			Boolean expectedFlag = (expectedFlags != null ? expectedFlags.get(paramIndex) : null);

			String signatureOfOneParameter = 
					createSignatureOfOneParameter(
							parameterType,
							parameterName,
							expectedFlag, 
							isExpectedDecorationAdded,
							extLanguage);

			signature += signatureOfOneParameter;

			if (paramIndex < types.size() - 1) {
				signature += ", ";
			}
		}

		return signature;
	}

	private static String createSignatureOfOneParameter(
			String parameterType,
			String parameterName, 
			Boolean expectedFlag,
			boolean isExpectedDecorationAdded, 
			ExtLanguage extLanguage) {

		String signature = "";

		if (isExpectedDecorationAdded) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		parameterType = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(parameterType, extLanguage);

		signature += parameterType;

		if (parameterName != null) {

			signature += " ";
			parameterName = ExtLanguageHelper.convertTextFromIntrToExtLanguage(parameterName, extLanguage);

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

	private static List<Boolean> getExpectedParametersFlags(List<MethodParameterNode> methodParameters) {

		List<Boolean> expectedFlags = new ArrayList<Boolean>();

		for(MethodParameterNode methodParameter : methodParameters) {

			if (methodParameter.isExpected()) {
				expectedFlags.add(true);
			} else {
				expectedFlags.add(false);
			}

		}

		return expectedFlags;
	}

	// TODO SIMPLE-VIEW similar methods for other types of nodes exist (extract common code)
	public static String validateMethodName(String nameInExternalLanguage, ExtLanguage extLanguage) {

		String errorMessage = ExtLanguageHelper.verifySeparatorsInName(nameInExternalLanguage, extLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		String nameInIntrernalLanguage = ExtLanguageHelper.convertTextFromExtToIntrLanguage(nameInExternalLanguage, extLanguage);

		if (isValid(nameInIntrernalLanguage)) {
			return null;
		}

		return RegexHelper.createMessageAllowedCharsForMethod(extLanguage);
	}

	private static boolean isValid(String name) {

		if (!JavaLanguageHelper.isValidJavaIdentifier(name)) {
			return false;
		}

		return true;
	}

	public static List<String> getParameterNames(MethodNode method) {

		List<String> result = new ArrayList<String>();

		for(AbstractParameterNode parameter : method.getParameters()){
			result.add(parameter.getName());
		}

		return result;
	}

	public static List<String> getMethodParameterTypes(MethodNode method, ExtLanguage extLanguage) {

		List<String> result = new ArrayList<String>();

		for (AbstractParameterNode parameter : method.getParameters()) {

			String type = parameter.getType();

			type = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(type, extLanguage);

			result.add(type);
		}

		return result;
	}


}
