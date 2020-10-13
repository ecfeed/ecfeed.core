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
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.StringHelper;


public class ClassNodeHelper {

	public static final String LINK_NOT_SET_PROBLEM = "The link to global parameter is not defined";
	public static final String METHODS_INCOMPATIBLE_PROBLEM = "The converted methods do not have the same parameter count and types";

	public static String getNonQualifiedName(ClassNode classNode, IExtLanguageManager extLanguageManager) {

		String name = ModelHelper.getNonQualifiedName(classNode.getName());
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);

		return name;
	}

	public static String getQualifiedName(ClassNode classNode, IExtLanguageManager extLanguageManager) {

		String name = classNode.getName();
		name = extLanguageManager.convertTextFromExtToIntrLanguage(name);

		return name;
	}

	public static String getPackageName(ClassNode classNode, IExtLanguageManager extLanguageManager) {

		String name = classNode.getName();

		return extLanguageManager.getPackageName(name);
	}

	public static String validateClassName(String nameInExternalLanguage, IExtLanguageManager extLanguageManager) {

		String errorMessage = extLanguageManager.verifySeparators(nameInExternalLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		String nameInInternalLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(nameInExternalLanguage);

		if (!classNameCompliesWithJavaNamingRules(nameInInternalLanguage)) {
			return RegexHelper.createMessageAllowedCharsForClass(extLanguageManager);
		}

		return null;
	}

	public static String verifyNewMethodSignatureIsValidAndUnique(
			ClassNode classNode,
			String methodNameInExtLanguage,
			List<String> parameterTypesInExtLanguage,
			IExtLanguageManager extLanguageManager) {

		String errorMessage = MethodNodeHelper.verifyMethodSignatureIsValid(methodNameInExtLanguage, parameterTypesInExtLanguage, extLanguageManager);

		if (errorMessage != null)
			return errorMessage;

		if (findMethodByExtLanguage(classNode, methodNameInExtLanguage, parameterTypesInExtLanguage, extLanguageManager) != null) {

			String newMethodSignature =
					MethodNodeHelper.createSignature(
							methodNameInExtLanguage,
							parameterTypesInExtLanguage,
							null,
							null);


			String classSignature = createSignature(classNode, extLanguageManager);

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
			IExtLanguageManager extLanguageManager) {

		String errorMessage =
				MethodNodeHelper.verifyMethodSignatureIsValid(
						startMethodNameInExtLanguage, parameterTypesInExtLanguage, extLanguageManager);

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
					extLanguageManager);

			if (methodNode == null) {
				return newMethodNameInExtLanguage;
			}
		}
	}

	public static String createSignature(ClassNode classNode, IExtLanguageManager extLanguageManager) {

		return getQualifiedName(classNode, extLanguageManager);
		}

	public static String createMethodSignatureDuplicateMessage(
			ClassNode classNode,
			MethodNode duplicateMethodNode,
			IExtLanguageManager extLanguageManager) {


		String classSignature = createSignature(classNode, extLanguageManager);

		String methodSignature = MethodNodeHelper.createSignature(duplicateMethodNode, extLanguageManager);

		String message =
				"Class: "
						+ classSignature
						+ " already contains method with identical signature: " + methodSignature + ".";

		return message;
	}

	public static MethodNode findMethodByExtLanguage(
			ClassNode classNode,
			String methodNameInExternalLanguage,
			List<String> parameterTypesInExternalLanguage,
			IExtLanguageManager extLanguageManager) {

		List<MethodNode> methods = classNode.getMethods();

		for (MethodNode methodNode : methods) {

			String currentMethodName = MethodNodeHelper.getName(methodNode, extLanguageManager);
			
			List<String> currentParameterTypes = MethodNodeHelper.getParameterTypes(methodNode, extLanguageManager);

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
