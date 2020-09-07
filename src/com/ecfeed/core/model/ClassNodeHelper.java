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

	public static boolean classNameCompliesWithJavaNamingRules(String className) {

		if (className.matches(RegexHelper.REGEX_CLASS_NODE_NAME)) {
			return true;
		}

		return false;
	}

	public static boolean isNewMethodSignatureValidAndUnique(
			ClassNode classNode, 
			String methodName, 
			List<String> parameterTypes, 
			ExtLanguage extLanguage) {

		return isNewMethodSignatureValidAndUnique(classNode, methodName, parameterTypes, null, extLanguage);
	}

	public static boolean isNewMethodSignatureValidAndUnique(
			ClassNode classNode, 
			String methodName, 
			List<String> parameterTypes,
			List<String> problems,
			ExtLanguage extLanguage) {

		if (findMethod(classNode, methodName, parameterTypes, extLanguage) != null) {

			if (problems != null) {
				String newMethodSignature =  

						MethodNodeHelper.createSignature(
								methodName,
								parameterTypes,
								null, 
								null,
								false, 
								extLanguage);


				String classSignature = createSignature(classNode, extLanguage);

				String message =
						"Class: " 
								+ classSignature 
								+ " already contains method with identical signature: " + newMethodSignature + ".";

				problems.add(message);
			}

			return false;
		}

		return MethodNodeHelper.validateMethodName(methodName, problems, extLanguage);
	}

	public static String generateNewMethodName(
			ClassNode classNode, String startMethodName, List<String> argTypes, ExtLanguage extLanguage) {

		if (isNewMethodSignatureValidAndUnique(classNode, startMethodName, argTypes, extLanguage)) {
			return startMethodName;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(startMethodName);

		for (int i = 1;   ; i++) {

			String newMethodName = oldNameCore + String.valueOf(i);

			if (isNewMethodSignatureValidAndUnique(classNode, newMethodName, argTypes, extLanguage)) {
				return newMethodName;
			}
		}
	}

	public static String createSignature(ClassNode classNode, ExtLanguage extLanguage) {

		String className = classNode.getName();

		className = ExtLanguageHelper.convertClassFromIntrToExtLanguage(className, extLanguage);

		String classSignature = ExtLanguageHelper.convertTextFromIntrToExtLanguage(className, extLanguage);

		return classSignature;
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

	public static MethodNode findMethod(
			ClassNode classNode, 
			String methodNameToFind, 
			List<String> intrLanguageParameterTypesToFind, 
			ExtLanguage extLanguage) {

		List<String> extLanguageParameterTypesToFind = convertParameterTypesToExtLanguage(intrLanguageParameterTypesToFind, extLanguage);

		List<MethodNode> methods = classNode.getMethods();

		for (MethodNode methodNode : methods) {

			List<String> currentParameterTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, extLanguage);

			String currentMethodName = methodNode.getName();

			if (currentMethodName.equals(methodNameToFind) && currentParameterTypes.equals(extLanguageParameterTypesToFind)){
				return methodNode;
			}
		}

		return null;
	}

	private static List<String> convertParameterTypesToExtLanguage(
			List<String> parameterTypes,
			ExtLanguage extLanguage) {

		List<String> result = new ArrayList<String>();

		for (String parameterType : parameterTypes) {

			parameterType = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(parameterType, extLanguage);
			result.add(parameterType);
		}

		return result;
	}

}
