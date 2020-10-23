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

public class MethodParameterNodeHelper {


	public static String getName(MethodParameterNode methodParameterNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(methodParameterNode, extLanguageManager);
	}

	public static String createSignature(
			MethodParameterNode methodParameterNode,
			IExtLanguageManager extLanguageManager) {

		String type = AbstractParameterNodeHelper.getType(methodParameterNode, extLanguageManager);
		String name = AbstractParameterNodeHelper.createNameSignature(methodParameterNode, extLanguageManager);

		String signature = 
				AbstractParameterNodeHelper.createSignature(
						type,
						name,
						methodParameterNode.isExpected());

		final GlobalParameterNode link = methodParameterNode.getLink();

		if (methodParameterNode.isLinked() && link != null) {
			signature += "[LINKED]->" + GlobalParameterNodeHelper.getQualifiedName(link, extLanguageManager);
		}

		return signature;
	}

	public static String createReverseSignature(
			MethodParameterNode methodParameterNode,
			IExtLanguageManager extLanguageManager) {

		String type = AbstractParameterNodeHelper.getType(methodParameterNode, extLanguageManager);
		String name = AbstractParameterNodeHelper.createNameSignature(methodParameterNode, extLanguageManager);

		String signature = 
				AbstractParameterNodeHelper.createReverseSignature(
						type,
						name,
						methodParameterNode.isExpected());

		final GlobalParameterNode link = methodParameterNode.getLink();

		if (methodParameterNode.isLinked() && link != null) {
			signature += "[LINKED]->" + GlobalParameterNodeHelper.getQualifiedName(link, extLanguageManager);
		}

		return signature;
	}

	public static String getType(MethodParameterNode methodParameterNode, IExtLanguageManager extLanguageManager) {

		String type = methodParameterNode.getType();
		type =  extLanguageManager.convertTypeFromIntrToExtLanguage(type);

		return type;
	}

}
