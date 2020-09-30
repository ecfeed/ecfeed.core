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

	public static String getQualifiedName(
			GlobalParameterNode globalParameterNode,
			ExtLanguage extLanguage) {

		String qualifiedName = globalParameterNode.getQualifiedName();
		qualifiedName = ExtLanguageHelper.convertTextFromIntrToExtLanguage(qualifiedName, extLanguage);

		return qualifiedName;
	}

	public static String getType(GlobalParameterNode globalParameterNode, ExtLanguage extLanguage) {

		String type = globalParameterNode.getType();
		type = ExtLanguageHelper.convertTypeFromIntrToExtLanguage(type, extLanguage);

		return type;
	}

}
