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
import com.ecfeed.core.utils.CoreViewModeHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.ExtLanguage;


public class MethodNodeHelper {

	public static String createSignature(
			String fullName,
			List<MethodParameterNode> methodParameters,
			List<String> types, // TODO SIMPLE-VIEW take types from method parameters
			List<String> parameterNames, // TODO SIMPLE-VIEW take names from method parameters
			ExtLanguage viewMode, boolean isExpectedDecorationAdded) {

		fullName = CoreViewModeHelper.convertTextToConvention(fullName, viewMode);

		String signature = new String(fullName) + "(";
		String type;

		for (int paramIndex = 0; paramIndex < types.size(); paramIndex++) {

			if (isExpectedDecorationAdded) {
				if (methodParameters.get(paramIndex).isExpected()) {
					signature += "[e]";
				}
			}

			type = types.get(paramIndex);
			type = CoreViewModeHelper.convertTypeToConvention(type, viewMode);

			signature += type;
			signature += " ";
			String parameterName = parameterNames.get(paramIndex);
			parameterName = CoreViewModeHelper.convertTextToConvention(parameterName, viewMode);

			signature += parameterName;

			if (paramIndex < types.size() - 1) {
				signature += ", ";
			}
		}

		signature += ")";

		return signature;
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

	public static List<String> getMethodParameterTypes(MethodNode method, ExtLanguage viewMode) {

		List<String> result = new ArrayList<String>();

		for (AbstractParameterNode parameter : method.getParameters()) {

			String type = parameter.getType();

			type = CoreViewModeHelper.convertTextToConvention(type, viewMode);

			result.add(type);
		}

		return result;
	}


}
