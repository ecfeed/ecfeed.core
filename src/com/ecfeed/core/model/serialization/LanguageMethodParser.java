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
import com.ecfeed.core.utils.ExceptionHelper;
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

		if (methodName == null) {
			ExceptionHelper.reportRuntimeException("Method name not found.");
		}

		MethodNode methodNode  = new MethodNode(methodName, null);

		return methodNode;
	}

}
