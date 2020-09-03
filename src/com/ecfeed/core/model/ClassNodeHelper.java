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

	public static boolean isNewMethodSignatureValid(
			ClassNode parent, String methodName, List<String> argTypes, ExtLanguage extLanguage) {

		return isNewMethodSignatureValid(parent, methodName, argTypes, null, extLanguage);
	}

	public static boolean isNewMethodSignatureValid(
			ClassNode classNode, 
			String methodName, 
			List<String> argTypes,
			List<String> problems,
			ExtLanguage extLanguage) {

		if (findMethod(classNode, methodName, argTypes, extLanguage) != null) {
			// TODO SIMPLE-VIEW add problem duplicate signature
			// problems.add(createMethodSignatureDuplicateMessage(classNode, methodNode, extLanguage));
			return false;
		}

		return MethodNodeHelper.validateMethodName(methodName, problems);
	}

	// TODO SIMPLE-VIEW remove ??
	public static void conditionallyAddDuplicateMethodSignatureProblem(
			ClassNode classNode, String methodName, List<String> argTypes, List<String> problems, ExtLanguage extLanguage) {

		MethodNode methodNode = findMethod(classNode, methodName, argTypes, extLanguage);

		if (methodNode != null) {

			if (problems != null) {
				problems.add(createMethodSignatureDuplicateMessage(classNode, methodNode, extLanguage));
			}

		}
	}

	public static String generateNewMethodName(
			ClassNode classNode, String startMethodName, List<String> argTypes, ExtLanguage extLanguage) {

		if (isNewMethodSignatureValid(classNode, startMethodName, argTypes, extLanguage)) {
			return startMethodName;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(startMethodName);

		for (int i = 1;   ; i++) {

			String newMethodName = oldNameCore + String.valueOf(i);

			if (isNewMethodSignatureValid(classNode, newMethodName, argTypes, extLanguage)) {
				return newMethodName;
			}
		}
	}

	// TODO SIMPLE-VIEW - inconsistent - creating method signature is in MethodNode class
	public static String createSignature(ClassNode classNode, ExtLanguage extLanguage) {

		String classSignature = classNode.getName();
		classSignature = ExtLanguageHelper.convertTextFromIntrToExtLanguage(classSignature, extLanguage);

		return classSignature;
	}

	public static String createMethodSignatureDuplicateMessage(
			ClassNode classNode, 
			MethodNode duplicateMethodNode,
			ExtLanguage extLanguage) {


		String classSignature = createSignature(classNode, extLanguage);

		String methodSignature = MethodNodeHelper.createShortSignature(duplicateMethodNode, extLanguage);

		String message =
				"Class: " 
						+ classSignature 
						+ " already contains method with identical signature: " + methodSignature + ".";

		return message;
	}

	public static MethodNode findMethod(
			ClassNode classNode, 
			String methodNameToFind, 
			List<String> parameterTypesToFind, 
			ExtLanguage extLanguage) {

		List<MethodNode> methods = classNode.getMethods();

		for (MethodNode methodNode : methods) {

			List<String> currentParameterTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, extLanguage);

			String currentMethodName = methodNode.getName();

			if (currentMethodName.equals(methodNameToFind) && currentParameterTypes.equals(parameterTypesToFind)){
				return methodNode;
			}
		}

		return null;
	}


}
