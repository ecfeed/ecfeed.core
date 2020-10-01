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

import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.StringHelper;


public class ClassNodeHelper {

	public static final String LINK_NOT_SET_PROBLEM = "The link to global parameter is not defined";
	public static final String METHODS_INCOMPATIBLE_PROBLEM = "The converted methods do not have the same parameter count and types";

	public static String getNonQualifiedName(ClassNode classNode, ExtLanguage extLanguage) {

		String name = ModelHelper.getNonQualifiedName(classNode.getName());
		name = ExtLanguageHelper.convertTextFromIntrToExtLanguage(name,  extLanguage);

		return name;
	}

	public static String getQualifiedName(ClassNode classNode, ExtLanguage extLanguage) {

		String name = classNode.getName();
		name = ExtLanguageHelper.getQualifiedName(name, extLanguage);

		return name;
	}

	public static String getPackageName(ClassNode classNode, ExtLanguage extLanguage) {

		String name = classNode.getName();

		return ExtLanguageHelper.getPackageName(name, extLanguage);
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

		String errorMessage = MethodNodeHelper.verifyMethodSignatureIsValid(methodNameInExtLanguage, parameterTypesInExtLanguage, extLanguage);

		if (errorMessage != null)
			return errorMessage;

		if (findMethodByExtLanguage(classNode, methodNameInExtLanguage, parameterTypesInExtLanguage, extLanguage) != null) {

			String newMethodSignature =
					MethodNodeHelper.createSignature(
							methodNameInExtLanguage,
							parameterTypesInExtLanguage,
							null,
							null);


			String classSignature = createSignature(classNode, extLanguage);

			errorMessage =
					"Class: "
							+ classSignature
							+ " already contains method with identical signature: " + newMethodSignature + ".";

			return errorMessage;
		}

		return null;
	}

	public static String generateNewMethodName(
			ClassNode classNode,
			String startMethodNameInExtLanguage,
			List<String> parameterTypesInExtLanguage,
			ExtLanguage extLanguage) {

		String errorMessage =
				MethodNodeHelper.verifyMethodSignatureIsValid(
						startMethodNameInExtLanguage, parameterTypesInExtLanguage, extLanguage);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
			return null;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(startMethodNameInExtLanguage);

		for (int i = 1;   ; i++) {

			String newMethodNameInExtLanguage = oldNameCore + String.valueOf(i);

			MethodNode methodNode = findMethodByExtLanguage(
					classNode,
					newMethodNameInExtLanguage,
					parameterTypesInExtLanguage,
					extLanguage);

			if (methodNode == null) {
				return newMethodNameInExtLanguage;
			}
		}
	}

	public static String createSignature(ClassNode classNode, ExtLanguage extLanguage) {

		String className = classNode.getName();

		String signature = ExtLanguageHelper.createClassNameSignature(className, extLanguage);

		return signature;
	}

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

	// TODO SIMPLE VIEW add method findMethodByExtLanguage(classNode, methodNode, extLanguage) and use where possible instead of method below

	public static MethodNode findMethodByExtLanguage(
			ClassNode classNode,
			String methodNameInExternalLanguage,
			List<String> parameterTypesInExternalLanguage,
			ExtLanguage extLanguage) {

		List<MethodNode> methods = classNode.getMethods();

		for (MethodNode methodNode : methods) {

			List<String> currentParameterTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, extLanguage);

			String currentMethodName = MethodNodeHelper.getName(methodNode, extLanguage);

			if (currentMethodName.equals(methodNameInExternalLanguage) && currentParameterTypes.equals(parameterTypesInExternalLanguage)){
				return methodNode;
			}
		}

		return null;
	}

	private static boolean classNameCompliesWithJavaNamingRules(String className) {

		if (className.matches(RegexHelper.REGEX_CLASS_NODE_NAME)) {
			return true;
		}

		return false;
	}

}
