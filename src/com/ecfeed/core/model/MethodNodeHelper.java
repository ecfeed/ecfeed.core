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

import com.ecfeed.core.operations.OperationMessages;
import com.ecfeed.core.utils.ExtLanguageHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.ExtLanguage;


public class MethodNodeHelper {


	public static String createShortSignature(MethodNode methodNode, ExtLanguage extLanguage) { // TODO SIMPLE-VIEW - rename to createSignature 

		return MethodNodeHelper.createSignature(
				methodNode,
				false, extLanguage);
	}

	public static String createSignature(
			MethodNode methodNode,
			boolean isExpectedDecorationAdded, 
			ExtLanguage extLanguage) {

		String signature = 
				createSignature(
						methodNode.getName(),
						methodNode.getParameterTypes(),
						methodNode.getParametersNames(),
						getExpectedParametersFlags(methodNode.getMethodParameters()),
						isExpectedDecorationAdded,
						extLanguage);

		return signature;
	}
	
	public static String createLongSignature(MethodNode methodNode, ExtLanguage extLanguage) {

		String shortSignature = createShortSignature(methodNode, extLanguage);
		
		return methodNode.getParent().getName() + "." + shortSignature;
	}
	
	public String createSignatureWithExpectedDecorations(MethodNode methodNode, ExtLanguage extLanguage) {

		String signature = createSignature(methodNode, true, extLanguage);
		
		return signature;
	}
	

	public static String createSignature(
			String fullName,
			List<String> types,
			List<String> parameterNames, 
			List<Boolean> expectedFlags, 
			boolean isExpectedDecorationAdded, ExtLanguage extLanguage) {

		fullName = ExtLanguageHelper.convertTextFromIntrToExtLanguage(fullName, extLanguage);

		String signature = new String(fullName) + "(";
		String type;

		for (int paramIndex = 0; paramIndex < types.size(); paramIndex++) {

			if (isExpectedDecorationAdded) {
				if (expectedFlags.get(paramIndex) == true) {
					signature += "[e]";
				}
			}

			type = types.get(paramIndex);
			type = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(type, extLanguage);

			signature += type;
			signature += " ";
			String parameterName = parameterNames.get(paramIndex);
			parameterName = ExtLanguageHelper.convertTextFromIntrToExtLanguage(parameterName, extLanguage);

			signature += parameterName;

			if (paramIndex < types.size() - 1) {
				signature += ", ";
			}
		}

		signature += ")";

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


	public static boolean validateMethodName(String name) {

		return validateMethodName(name, null);
	}

	public static boolean validateMethodName(String name, List<String> problems) {

		if (isValid(name)) {
			return true;
		}

		if(problems != null){
			problems.add(OperationMessages.METHOD_NAME_REGEX_PROBLEM);
		}

		return false;
	}

	private static boolean isValid(String name) {


		if (!name.matches(RegexHelper.REGEX_METHOD_NODE_NAME)) {
			return false;
		}

		if (!JavaLanguageHelper.isValidJavaIdentifier(name)) {
			return false;
		}

		return true;
	}

	public static List<String> getArgNames(MethodNode method) {

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

			type = ExtLanguageHelper.convertTextFromIntrToExtLanguage(type, extLanguage);

			result.add(type);
		}

		return result;
	}


}
