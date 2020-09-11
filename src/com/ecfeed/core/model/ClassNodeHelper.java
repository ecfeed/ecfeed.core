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

import com.ecfeed.core.utils.ExtLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.ExtLanguage;


public class ClassNodeHelper {

	public static final String LINK_NOT_SET_PROBLEM = "The link to global parameter is not defined";
	public static final String METHODS_INCOMPATIBLE_PROBLEM = "The converted methods do not have the same parameter count and types";

	public static String getSimpleName(ClassNode classNode) {

		return ModelHelper.getNonQualifiedName(classNode.getName());
	}

	public static String getQualifiedName(ClassNode classNode) {

		return classNode.getName();
	}

	public static String getPackageName(ClassNode classNode) {

		return ModelHelper.getPackageName(classNode.getName());
	}

	public static String validateClassName(String nameInExternalLanguage, ExtLanguage extLanguage) {

		String errorMessage = ExtLanguageHelper.verifySeparatorsInName(nameInExternalLanguage, extLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		String nameInInternalLanguage = ExtLanguageHelper.convertTextFromExtToIntrLanguage(nameInExternalLanguage, extLanguage);

		if (!classNameCompliesWithJavaNamingRules(nameInInternalLanguage)) {
			return RegexHelper.createMessageAllowedCharsForClass(extLanguage);
		}

		return null;
	}

	public static String verifyNewMethodSignatureIsValidAndUnique(
			ClassNode classNode,
			String methodNameInExtLanguage,
			List<String> parameterTypesInExtLanguage,
			ExtLanguage extLanguage) {

		if (findMethodByExtLanguage(classNode, methodNameInExtLanguage, parameterTypesInExtLanguage, extLanguage) != null) {

			String newMethodSignature =
					MethodNodeHelper.createSignatureByExtLanguage(
							methodNameInExtLanguage,
							parameterTypesInExtLanguage,
							null,
							null);


			String classSignature = createSignature(classNode, extLanguage);

			String errorMessage =
					"Class: "
							+ classSignature
							+ " already contains method with identical signature: " + newMethodSignature + ".";

			return errorMessage;
		}

		String errorMessage = MethodNodeHelper.validateMethodName(methodNameInExtLanguage, extLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	// TODO SIMPLE-VIEW unit tests
	public static String generateNewMethodName(
			ClassNode classNode,
			String startMethodNameInExtLanguage,
			List<String> parameterTypesInExtLanguage,
			ExtLanguage extLanguage) {

		String errorMessage =
				verifyNewMethodSignatureIsValidAndUnique(
						classNode, startMethodNameInExtLanguage, parameterTypesInExtLanguage, extLanguage);

		if (errorMessage == null) {
			return startMethodNameInExtLanguage;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(startMethodNameInExtLanguage);

		for (int i = 1;   ; i++) {

			String newMethodName = oldNameCore + String.valueOf(i);

			errorMessage = verifyNewMethodSignatureIsValidAndUnique(classNode, newMethodName, parameterTypesInExtLanguage, extLanguage);
			if (errorMessage == null) {
				return newMethodName;
			}
		}
	}

	// TODO SIMPLE-VIEW unit tests
	public static String createSignature(ClassNode classNode, ExtLanguage extLanguage) {

		String className = classNode.getName();

		if (extLanguage == ExtLanguage.SIMPLE) {
			className = StringHelper.getLastTokenOrInputString(className, ".");
		}

		className = ExtLanguageHelper.convertTextFromIntrToExtLanguage(className, extLanguage);

		return className;
	}

	// TODO SIMPLE-VIEW unit tests
	public static String createMethodSignatureDuplicateMessage(
			ClassNode classNode,
			MethodNode duplicateMethodNode,
			ExtLanguage extLanguage) {


		String classSignature = createSignature(classNode, extLanguage);

		String methodSignature = MethodNodeHelper.createSignature(duplicateMethodNode, extLanguage);

		String message =
				"Class: "
						+ classSignature
						+ " already contains method with identical signature: " + methodSignature + ".";

		return message;
	}


	// TODO SIMPLE-VIEW unit tests
	public static MethodNode findMethodByExtLanguage(
			// TODO SIMPLE-VIEW check usages
			ClassNode classNode,
			String methodNameInExternalLanguage,
			List<String> parameterTypesInExternalLanguage,
			ExtLanguage extLanguage) {

		// TODO SIMPLE-VIEW validate methodNameInExternalLanguage
		validateParameterTypes(parameterTypesInExternalLanguage, ExtLanguage.JAVA);

		List<MethodNode> methods = classNode.getMethods();

		for (MethodNode methodNode : methods) {

			List<String> currentParameterTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, extLanguage);

			String currentMethodName = MethodNodeHelper.getMethodName(methodNode, extLanguage);

			if (currentMethodName.equals(methodNameInExternalLanguage) && currentParameterTypes.equals(parameterTypesInExternalLanguage)){
				return methodNode;
			}
		}

		return null;
	}

	private static void validateParameterTypes(List<String> parameterTypesInExternalLanguage, ExtLanguage extLanguage) {
		// TODO SIMPLE-VIEW
	}

	// TODO SIMPLE-VIEW unit tests
	public static MethodNode findMethodByIntrLanguage( // TODO SIMPLE-VIEW check usages
			ClassNode classNode,
			String methodNameInIntrLanguage,
			List<String> parameterTypesInIntrLanguage) {

		MethodNode methodNode = classNode.findMethodWithTheSameSignature(methodNameInIntrLanguage, parameterTypesInIntrLanguage);
		return methodNode;
	}

	// TODO SIMPLE-VIEW unit tests
	public static List<String> convertParameterTypesToExtLanguage(
			List<String> parameterTypes,
			ExtLanguage extLanguage) {

		List<String> result = new ArrayList<String>();

		for (String parameterType : parameterTypes) {

			parameterType = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(parameterType, extLanguage);
			result.add(parameterType);
		}

		return result;
	}

	// TODO SIMPLE-VIEW unit tests
	private static boolean classNameCompliesWithJavaNamingRules(String className) {

		if (className.matches(RegexHelper.REGEX_CLASS_NODE_NAME)) {
			return true;
		}

		return false;
	}

}
