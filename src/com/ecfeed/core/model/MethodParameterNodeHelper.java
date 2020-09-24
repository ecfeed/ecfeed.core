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

import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageHelper;

public class MethodParameterNodeHelper {


	public static String getName(MethodParameterNode methodParameterNode, ExtLanguage extLanguage) {
		return ExtLanguageHelper.convertTextFromIntrToExtLanguage(
				methodParameterNode.getName(), extLanguage);
		
	}

	// TODO SIMPLE-VIEW unit tests
	public static String createSignature(
			MethodParameterNode methodParameterNode,
			ExtLanguage extLanguage) {

		String type = AbstractParameterNodeHelper.createTypeSignature(methodParameterNode, extLanguage);
		String name = AbstractParameterNodeHelper.createNameSignature(methodParameterNode, extLanguage);

		String signature = 
				AbstractParameterNodeHelper.createSignature(
						type,
						name,
						methodParameterNode.isExpected());

		if (methodParameterNode.isLinked()) {
			signature += "[LINKED]->" + methodParameterNode.getLink().getQualifiedName();
		}

		return signature;
	}

}
