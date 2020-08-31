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

import com.ecfeed.core.utils.CoreViewModeHelper;
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
			ClassNode parent, String methodName, List<String> argTypes) {

		return isNewMethodSignatureValid(parent, methodName, argTypes, null);
	}

	public static boolean isNewMethodSignatureValid(
			ClassNode parent, 
			String methodName, 
			List<String> argTypes,
			List<String> problems) {

		if (parent.getMethod(methodName, argTypes) != null) {
			return false;
		}

		return MethodNodeHelper.validateMethodName(methodName, problems);
	}

	public static void updateNewMethodsSignatureProblemList(
			ClassNode parent, String methodName, List<String> argTypes, List<String> problems, ExtLanguage extLanguage) {

		// TODO SIMPLE-VIEW check
		MethodNode methodNode = parent.getMethod(methodName, argTypes);

		if (methodNode != null) {

			if (problems != null) {
				problems.add(generateMethodSignatureDuplicateMessage(parent, methodNode, extLanguage));
			}

		}
	}

	public static String generateNewMethodName(ClassNode classNode, String startMethodName, List<String> argTypes) {

		if (isNewMethodSignatureValid(classNode, startMethodName, argTypes)) {
			return startMethodName;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(startMethodName);

		for (int i = 1;   ; i++) {

			String newMethodName = oldNameCore + String.valueOf(i);

			if (isNewMethodSignatureValid(classNode, newMethodName, argTypes)) {
				return newMethodName;
			}
		}
	}

	public static String getSignature(ClassNode classNode, ExtLanguage extLanguage) {

		String classSignature = classNode.getName();
		classSignature = CoreViewModeHelper.convertTextToConvention(classSignature, extLanguage);

		return classSignature;
	}


	public static String generateMethodSignatureDuplicateMessage(
			ClassNode classNode, 
			MethodNode duplicateMethodNode,
			ExtLanguage extLanguage) {


		String classSignature = getSignature(classNode, extLanguage);

		//  TODO SIMPLE-VIEW move to MethodNodeHelper
		String methodSignature = duplicateMethodNode.createShortSignature(extLanguage);

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
