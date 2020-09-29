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

public class GlobalParameterNodeHelper {


	public static String getName(MethodParameterNode methodParameterNode, ExtLanguage extLanguage) {

		return AbstractNodeHelper.getName(methodParameterNode, extLanguage);
	}

	// TODO SIMPLE-VIEW unit tests
	public static String createSignature(
			GlobalParameterNode globalParameterNode,
			ExtLanguage extLanguage) {

		String qualifiedName = globalParameterNode.getQualifiedName();

		String signature = ExtLanguageHelper.convertTextFromIntrToExtLanguage(qualifiedName, extLanguage);

		return signature;
	}

	public static String getType(MethodParameterNode methodParameterNode, ExtLanguage extLanguage) {

		String type = methodParameterNode.getType();
		type =  ExtLanguageHelper.convertTypeFromIntrToExtLanguage(type, extLanguage);

		return type;
	}

}
