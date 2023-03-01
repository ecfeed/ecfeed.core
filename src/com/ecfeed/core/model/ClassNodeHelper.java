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
	public static final String CONTAINS_METHOD_WITH_IDENTICAL_NAME = "contains method with identical name";


	public static BasicParameterNode addGlobalBasicParameterToClass(
			ClassNode classNode, 
			String name, 
			String type,
			IModelChangeRegistrator modelChangeRegistrator) {

		BasicParameterNode globalParameterNode = 
				BasicParameterNode.createGlobalParameter(name, type, modelChangeRegistrator);
		
		classNode.addParameter(globalParameterNode);

		return globalParameterNode;
	}

	public static MethodNode addMethodToClass(
			ClassNode classNode, String name, IModelChangeRegistrator modelChangeRegistrator) {

		MethodNode globalParameterNode = new MethodNode(name, modelChangeRegistrator);
		classNode.addMethod(globalParameterNode);

		return globalParameterNode;
	}

	public static String getNonQualifiedName(ClassNode classNode, IExtLanguageManager extLanguageManager) {

		String name = ModelHelper.getNonQualifiedName(classNode.getName());
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);

		return name;
	}

	public static String getQualifiedName(ClassNode classNode, IExtLanguageManager extLanguageManager) {

		String name = classNode.getName();

		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);

		name = extLanguageManager.getQualifiedName(name);

		return name;
	}

	public static String getPackageName(ClassNode classNode, IExtLanguageManager extLanguageManager) {

		String name = classNode.getName();

		return extLanguageManager.getPackageName(name);
	}

	public static String validateClassName(String nameInExternalLanguage, IExtLanguageManager extLanguageManager) {

		String errorMessage = extLanguageManager.verifySeparatorsInName(nameInExternalLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		String nameInInternalLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(nameInExternalLanguage);

		if (!classNameCompliesWithJavaNamingRules(nameInInternalLanguage)) {
			return RegexHelper.createMessageAllowedCharsForClass(extLanguageManager);
		}

		return null;
	}

	public static String verifyNewMethodSignatureIsValid(
			ClassNode classNode,
			String methodNameInExtLanguage,
			IExtLanguageManager extLanguageManager) {

		String errorMessage = MethodNodeHelper.verifyMethodNameIsValid(methodNameInExtLanguage, extLanguageManager);

		if (errorMessage != null)
			return errorMessage;

		//		if (findMethodByExtLanguage(classNode, methodNameInExtLanguage, extLanguageManager) != null) {
		//
		//			String classSignature = createSignature(classNode, extLanguageManager);
		//
		//			errorMessage =
		//					"Class: "
		//							+ classSignature
		//							+ " already " +
		//							CONTAINS_METHOD_WITH_IDENTICAL_NAME +
		//							": " + methodNameInExtLanguage + ".";
		//
		//			return errorMessage;
		//		}

		return null;
	}

	public static String generateNewMethodName(
			ClassNode classNode,
			String startMethodNameInExtLanguage,
			IExtLanguageManager extLanguageManager) {

		String errorMessage =
				MethodNodeHelper.verifyMethodNameIsValid(
						startMethodNameInExtLanguage, extLanguageManager);

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
					extLanguageManager);

			if (methodNode == null) {
				return newMethodNameInExtLanguage;
			}
		}
	}

	public static String createSignature(ClassNode classNode, IExtLanguageManager extLanguageManager) {

		return getQualifiedName(classNode, extLanguageManager);
	}

	public static String createMethodNameDuplicateMessage(
			ClassNode classNode,
			MethodNode duplicateMethodNode,
			boolean isParamNameAdded,
			IExtLanguageManager extLanguageManager) {


		String classSignature = createSignature(classNode, extLanguageManager);

		//		String methodSignature = MethodNodeHelper.createSignature(duplicateMethodNode, isParamNameAdded, extLanguageManager);

		String message =
				"Class: "
						+ classSignature
						+ " already " +
						CONTAINS_METHOD_WITH_IDENTICAL_NAME +
						": " + duplicateMethodNode.getName() + ".";

		return message;
	}

	public static MethodNode findMethodByExtLanguage(
			ClassNode classNode,
			String methodNameInExternalLanguage,
			IExtLanguageManager extLanguageManager) {

		List<MethodNode> methods = classNode.getMethods();

		for (MethodNode methodNode : methods) {

			String currentMethodName = AbstractNodeHelper.getName(methodNode, extLanguageManager);

			if (currentMethodName.equals(methodNameInExternalLanguage)){
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

	public static ClassNode findClassNode(IAbstractNode anyNode) {

		IAbstractNode parent = anyNode;

		while (parent != null) {

			if (parent instanceof ClassNode) {
				return (ClassNode) parent;
			}

			parent = parent.getParent();
		}

		return null;
	}
}
