/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class LanguageMethodParser {

	public static MethodNode parseJavaMethodSignature(String methodSignature) {


		String firstPart = StringHelper.getFirstToken(methodSignature, "(");

		if (firstPart == null) {
			ExceptionHelper.reportRuntimeException("Starting bracket not found.");
		}

		String mainPart = StringHelper.getFirstToken(methodSignature, ")");

		if (mainPart == null) {
			ExceptionHelper.reportRuntimeException("Ending bracket not found.");
		}
		
		String methodName = StringHelper.getLastToken(firstPart, " ");
		
		if (!JavaLanguageHelper.isValidJavaIdentifier(methodName)) {
			ExceptionHelper.reportRuntimeException("Method name: " +  methodName + " is not a valid Java identifier.");
		}

		if (methodName == null) {
			ExceptionHelper.reportRuntimeException("Method name not found.");
		}

		MethodNode methodNode  = new MethodNode(methodName, null);
		
		String parametersPart = StringHelper.getLastToken(mainPart, "(");
		
		parseParameters(parametersPart, methodNode);

		return methodNode;
	}

	private static void parseParameters(String parametersPart, MethodNode inOutMethodNode) {
		
		if (StringHelper.isTrimmedEmpty(parametersPart)) {
			return;
		}
		
		String[] parameters = parametersPart.split(",");
		
		for (String parameterText : parameters) {
			
			MethodParameterNode methodParameterNode = createMethodParameter(parameterText);
			
			inOutMethodNode.addParameter(methodParameterNode);
		}
	}

	private static MethodParameterNode createMethodParameter(String parameterText) {
		
		String paramTextTrimmed = parameterText.trim();
		
		String type = StringHelper.getFirstToken(paramTextTrimmed, " ");
		
		type = type.trim();
		
		if (!JavaLanguageHelper.isJavaType(type)) {
			ExceptionHelper.reportRuntimeException("Not allowed type: " + type);
		}
		
		String name = StringHelper.getLastToken(paramTextTrimmed, " ");
		
		name = name.trim();
		
		MethodParameterNode methodParameterNode = 
				new MethodParameterNode(
						name, type, JavaLanguageHelper.getDefaultValue(type), false, false, null, null);
				
		return methodParameterNode;
	}

}
