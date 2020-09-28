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

import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;

public class MethodNodeHelper {

	public static String getName(MethodNode methodNode, ExtLanguage extLanguage) {

		return AbstractNodeHelper.getName(methodNode, extLanguage);
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

	public static String validateMethodName(String nameInExternalLanguage, ExtLanguage extLanguage) {

		String errorMessage = ExtLanguageHelper.verifySeparatorsInName(nameInExternalLanguage, extLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		String nameInInternalLanguage = ExtLanguageHelper.convertTextFromExtToIntrLanguage(nameInExternalLanguage, extLanguage);

		if (isValid(nameInInternalLanguage)) {
			return null;
		}

		return RegexHelper.createMessageAllowedCharsForMethod(extLanguage);
	}

	public static String verifyMethodSignatureIsValid(
			String methodNameInExtLanguage,
			List<String> parameterTypesInExtLanguage,
			ExtLanguage extLanguage) {

		String errorMessage = MethodNodeHelper.validateMethodName(methodNameInExtLanguage, extLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		errorMessage = MethodNodeHelper.validateMethodParameterTypes(parameterTypesInExtLanguage, extLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	public static String validateMethodParameterTypes(List<String> parameterTypesInExtLanguage,
			ExtLanguage extLanguage) {

		String errorMessage;

		for (String parameterTypeInExtLanguage : parameterTypesInExtLanguage) {

			errorMessage = AbstractParameterNodeHelper.validateParameterType(parameterTypeInExtLanguage, extLanguage);

			if (errorMessage != null) {
				return errorMessage;
			}
		}

		return null;
	}

	public static String createSignature(MethodNode methodNode, ExtLanguage extLanguage) {

		return MethodNodeHelper.createSignature(
				methodNode,
				false, extLanguage);
	}

	public static String createSignature(
			MethodNode methodNode,
			boolean isExpectedDecorationAdded, 
			ExtLanguage extLanguageOfTheResult) {


		final List<Boolean> expectedParametersFlags =
				(isExpectedDecorationAdded ? getExpectedParametersFlags(methodNode.getMethodParameters()) : null);

		String signature =
				createSignatureByIntrLanguage(
						methodNode.getName(),
						methodNode.getParameterTypes(),
						methodNode.getParametersNames(),
						expectedParametersFlags,
						extLanguageOfTheResult);

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

	public static String createSignatureByIntrLanguage(
			String nameInIntrLanguage,
			List<String> parameterTypesInIntrLanguage,
			List<String> parameterNames, 
			List<Boolean> expectedFlags, 
			ExtLanguage extLanguageOfTheResult) {

		String nameInExtLanguage = ExtLanguageHelper.convertTextFromIntrToExtLanguage(nameInIntrLanguage, extLanguageOfTheResult);

		String signature = new String(nameInExtLanguage) + "(";

		String signaturesOfParameters = 
				createSignaturesOfParametersByIntrLanguage(
						parameterTypesInIntrLanguage, parameterNames, expectedFlags,
						extLanguageOfTheResult);

		signature += signaturesOfParameters;

		signature += ")";

		return signature;
	}

	public static String createSignature(
			String methodName,
			List<String> parameterTypes,
			List<String> parameterNames,
			List<Boolean> expectedFlags) {

		String signature = new String(methodName) + "(";

		String signaturesOfParameters =
				createSignaturesOfParameters(
						parameterTypes, parameterNames, expectedFlags);

		signature += signaturesOfParameters;

		signature += ")";

		return signature;
	}

	private static String createSignaturesOfParameters(
			List<String> parameterTypes,
			List<String> parameterNames,
			List<Boolean> expectedFlags) {

		String signature = "";

		for (int paramIndex = 0; paramIndex < parameterTypes.size(); paramIndex++) {

			String parameterType = parameterTypes.get(paramIndex);
			String parameterName = (parameterNames != null ? parameterNames.get(paramIndex) : null);
			Boolean expectedFlag = (expectedFlags != null ? expectedFlags.get(paramIndex) : null);

			String signatureOfOneParameter =
					AbstractParameterNodeHelper.createSignature(
							parameterType, parameterName, expectedFlag);

			signature += signatureOfOneParameter;

			if (paramIndex < parameterTypes.size() - 1) {
				signature += ", ";
			}
		}

		return signature;
	}

	// TODO - SIMPLE-VIEW tests
	public static String createSignaturesOfParameters(
			MethodNode methodNode,
			ExtLanguage extLanguage) {

		String signature = "";
		int paramCount = methodNode.getParametersCount();


		for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {

			MethodParameterNode methodParameterNode = methodNode.getMethodParameter(paramIndex);



			String signatureOfOneParameter = 
					AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
							methodParameterNode.getType(),
							methodParameterNode.getName(),
							methodParameterNode.isExpected(), 
							extLanguage);

			signature += signatureOfOneParameter;

			if (paramIndex < paramCount - 1) {
				signature += ", ";
			}
		}

		return signature;

	}	
	private static String createSignaturesOfParametersByIntrLanguage(
			List<String> parameterTypesInIntrLanguage,
			List<String> parameterNames,
			List<Boolean> expectedFlags, 
			ExtLanguage extLanguage) {

		String signature = "";

		for (int paramIndex = 0; paramIndex < parameterTypesInIntrLanguage.size(); paramIndex++) {

			String parameterType = parameterTypesInIntrLanguage.get(paramIndex);
			String parameterName = (parameterNames != null ? parameterNames.get(paramIndex) : null);
			Boolean expectedFlag = (expectedFlags != null ? expectedFlags.get(paramIndex) : null);

			String signatureOfOneParameter = 
					AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
							parameterType,
							parameterName,
							expectedFlag, 
							extLanguage);

			signature += signatureOfOneParameter;

			if (paramIndex < parameterTypesInIntrLanguage.size() - 1) {
				signature += ", ";
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


	private static boolean isValid(String name) {

		if (!JavaLanguageHelper.isValidJavaIdentifier(name)) {
			return false;
		}

		return true;
	}

}
