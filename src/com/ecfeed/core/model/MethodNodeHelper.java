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


	public static String createSignatureByIntrLanguage(MethodNode methodNode, ExtLanguage extLanguage) {

		return MethodNodeHelper.createSignatureByIntrLanguage(
				methodNode,
				false, extLanguage);
	}

	public static String createSignatureByIntrLanguage(
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

		String shortSignature = createSignatureByIntrLanguage(methodNode, extLanguage);

		return methodNode.getParent().getName() + "." + shortSignature;
	}

	public static String createSignatureWithExpectedDecorations(MethodNode methodNode, ExtLanguage extLanguage) {

		String signature = createSignatureByIntrLanguage(methodNode, true, extLanguage);

		return signature;
	}


	// TODO SIMPLE-VIEW unit test
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

	// TODO SIMPLE-VIEW unit test
	public static String createSignatureByExtLanguage(
			String nameInExtLanguage,
			List<String> parameterTypesInExtLanguage,
			List<String> parameterNames,
			List<Boolean> expectedFlags) {

		String signature = new String(nameInExtLanguage) + "(";

		String signaturesOfParameters =
				createSignaturesOfParametersByExtLanguage( // TODO SIMPLE-VIEW ext
						parameterTypesInExtLanguage, parameterNames, expectedFlags);

		signature += signaturesOfParameters;

		signature += ")";

		return signature;
	}

	private static String createSignaturesOfParametersByExtLanguage(
			List<String> parameterTypesInExtLanguage,
			List<String> parameterNames,
			List<Boolean> expectedFlags) {

		String signature = "";

		for (int paramIndex = 0; paramIndex < parameterTypesInExtLanguage.size(); paramIndex++) {

			String parameterType = parameterTypesInExtLanguage.get(paramIndex);
			String parameterName = (parameterNames != null ? parameterNames.get(paramIndex) : null);
			Boolean expectedFlag = (expectedFlags != null ? expectedFlags.get(paramIndex) : null);

			String signatureOfOneParameter =
					AbstractParameterNodeHelper.createSignatureOfOneParameterByExtLanguage( // XYX by ext language
							parameterType,
							parameterName,
							expectedFlag);

			signature += signatureOfOneParameter;

			if (paramIndex < parameterTypesInExtLanguage.size() - 1) {
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

	// TODO SIMPLE-VIEW similar methods for other types of nodes exist (extract common code)
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

	private static boolean isValid(String name) {

		if (!JavaLanguageHelper.isValidJavaIdentifier(name)) {
			return false;
		}

		return true;
	}

	public static String getMethodName(MethodNode methodNode, ExtLanguage extLanguage) {

		String nameInIntrLanguage = methodNode.getName();

		String nameInExtLanguage = ExtLanguageHelper.convertTextFromIntrToExtLanguage(nameInIntrLanguage, extLanguage);
		return nameInExtLanguage;
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
