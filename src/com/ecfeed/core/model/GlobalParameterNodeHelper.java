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

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;

public class GlobalParameterNodeHelper {


	public static String getName(MethodParameterNode methodParameterNode, IExtLanguageManager extLanguage) {

		return AbstractNodeHelper.getName(methodParameterNode, extLanguage);
	}

	public static String createSignature(GlobalParameterNode globalParameterNode, IExtLanguageManager extLanguage) {

		String type = getType(globalParameterNode, extLanguage);
		String qualifiedName = getQualifiedName(globalParameterNode, extLanguage);

		return type + " " + qualifiedName;
	}

	public static String getQualifiedName(
			GlobalParameterNode globalParameterNode,
			IExtLanguageManager extLanguage) {

		String qualifiedName = globalParameterNode.getQualifiedName();
		qualifiedName = ExtLanguageManagerForSimple.convertTextFromIntrToExtLanguage(qualifiedName, extLanguage);

		return qualifiedName;
	}

	public static String getType(GlobalParameterNode globalParameterNode, IExtLanguageManager extLanguage) {

		String type = globalParameterNode.getType();
		type = ExtLanguageManagerForSimple.convertTypeFromIntrToExtLanguage(type, extLanguage);

		return type;
	}

}
