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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;

public class MethodNodeHelper {

	public static String getName(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(methodNode, extLanguageManager);
	}

	public static void setName(MethodNode methodNode, String name, IExtLanguageManager extLanguageManager) {

		AbstractNodeHelper.setName(methodNode, name, extLanguageManager);
	}

	public static List<String> getParameterNames(MethodNode method) {

		List<String> result = new ArrayList<String>();

		for(AbstractParameterNode parameter : method.getParameters()){
			result.add(parameter.getName());
		}

		return result;
	}

	public static List<String> getMethodParameterTypes(MethodNode method, IExtLanguageManager extLanguageManager) {

		List<String> result = new ArrayList<String>();

		for (AbstractParameterNode parameter : method.getParameters()) {

			String type = parameter.getType();

			type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);

			result.add(type);
		}

		return result;
	}

	public static String validateMethodName(String nameInExternalLanguage, IExtLanguageManager extLanguageManager) {

		String errorMessage = extLanguageManager.verifySeparators(nameInExternalLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		String nameInInternalLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(nameInExternalLanguage);

		if (isValid(nameInInternalLanguage)) {
			return null;
		}

		return RegexHelper.createMessageAllowedCharsForMethod(extLanguageManager);
	}

	public static String verifyMethodSignatureIsValid(
			String methodNameInExtLanguage,
			List<String> parameterTypesInExtLanguage,
			IExtLanguageManager extLanguageManager) {

		String errorMessage = MethodNodeHelper.validateMethodName(methodNameInExtLanguage, extLanguageManager);

		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	public static String createSignature(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		return MethodNodeHelper.createSignature(
				methodNode,
				false, extLanguageManager);
	}

	public static String createSignature(
			MethodNode methodNode,
			boolean isExpectedDecorationAdded, 
			IExtLanguageManager extLanguageOfTheResult) {


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

	public static String createLongSignature(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		String shortSignature = createSignature(methodNode, extLanguageManager);

		return methodNode.getParent().getName() + "." + shortSignature;
	}

	public static String createSignatureWithExpectedDecorations(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		String signature = createSignature(methodNode, true, extLanguageManager);

		return signature;
	}

	public static String createSignatureByIntrLanguage(
			String nameInIntrLanguage,
			List<String> parameterTypesInIntrLanguage,
			List<String> parameterNames, 
			List<Boolean> expectedFlags, 
			IExtLanguageManager extLanguageManager) {

		String nameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(nameInIntrLanguage);

		String signature = new String(nameInExtLanguage) + "(";

		String signaturesOfParameters = 
				createSignaturesOfParametersByIntrLanguage(
						parameterTypesInIntrLanguage, parameterNames, expectedFlags,
						extLanguageManager);

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

	public static String createSignaturesOfParameters(
			MethodNode methodNode,
			IExtLanguageManager extLanguageManager) {

		String signature = "";
		int paramCount = methodNode.getParametersCount();


		for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {

			MethodParameterNode methodParameterNode = methodNode.getMethodParameter(paramIndex);



			String signatureOfOneParameter = 
					AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
							methodParameterNode.getType(),
							methodParameterNode.getName(),
							methodParameterNode.isExpected(), 
							extLanguageManager);

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
			IExtLanguageManager extLanguageManager) {

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
							extLanguageManager);

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

	public static Set<String> getConstraintNames(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		Set<String> constraintNames = methodNode.getConstraintsNames();

		constraintNames = convertConstraintNamesToExtLanguage(constraintNames, extLanguageManager);

		return constraintNames;
	}

	private static Set<String> convertConstraintNamesToExtLanguage(Set<String> constraintNames, IExtLanguageManager extLanguageManager) {

		Set<String> result = new HashSet<String>();

		for(String constraintName : constraintNames) {

			String nameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(constraintName);
			result.add(nameInExtLanguage);
		}

		return result;
	}

}
