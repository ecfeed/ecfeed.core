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

import com.ecfeed.core.utils.SimpleTypeHelper;

public class ClassNodeHelper {

	public static final String LINK_NOT_SET_PROBLEM = "The link to global parameter is not defined";
	public static final String METHODS_INCOMPATIBLE_PROBLEM = "The converted methods do not have the same parameter count and types";
	
	public static String getSimpleName(ClassNode classNode) {

		return ModelHelper.getSimpleName(classNode.getFullName());
	}

	public static String getQualifiedName(ClassNode classNode) {

		return classNode.getFullName();
	}
	
	public static String getPackageName(ClassNode classNode) {

		return ModelHelper.getPackageName(classNode.getFullName());
	}

	public static boolean isNewMethodSignatureValid(ClassNode parent, String methodName, List<String> argTypes) {

		return isNewMethodSignatureValid(parent, methodName, argTypes, null);
	}

	public static boolean isNewMethodSignatureValid(ClassNode parent, String methodName, List<String> argTypes, List<String> problems) {

		if (parent.getMethod(methodName, argTypes) != null) {
			return false;
		}
		
		return MethodNodeHelper.validateMethodName(methodName, problems);
	}
	
	public static void updateNewMethodsSignatureProblemList(ClassNode parent, String methodName, List<String> argTypes, List<String> problems) {
		
		if (parent.getMethod(methodName, argTypes) != null) {

			if (problems != null) {
				problems.add(generateMethodSignatureDuplicateMessage(parent, methodName));
			}
			
		}
	}

	public static String generateNewMethodName(ClassNode classNode, String startMethodName, List<String> argTypes) {

		for (int i = 1;   ; i++) {

			String newMethodName = startMethodName + String.valueOf(i);

			if (isNewMethodSignatureValid(classNode, newMethodName, argTypes)) {
				return newMethodName;
			}
		}
	}
	
	public static String generateMethodSignatureDuplicateMessage(ClassNode classNode, String methodName) {
	
		if (isHiddenTypeAvailable(classNode)) {
			return  "The class: '" 
					+ SimpleTypeHelper.parseToSimpleView(classNode.getSimpleName()) 
					+ "' already contains model of a method: '" 
					+ SimpleTypeHelper.parseToSimpleView(methodName) 
					+ "' with identical signature";
		} else {
			return  "The class: '" 
					+ classNode.getFullName() 
					+ "' already contains model of a method: '" 
					+ methodName 
					+ "' with identical signature";
		}
	}
	
	private static boolean isHiddenTypeAvailable(ClassNode classNode) {
		
		try {
			return classNode.getMethods().get(0).getParameters().get(0).getSuggestedType().isPresent();
		} catch (Exception e) {
			return false;
		}
		
	}

}
