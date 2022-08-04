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

public class GlobalParameterNodeHelper {

	public enum SignatureType {
		WITH_TYPE,
		WITHOUT_TYPE
	}

	public static ChoiceNode addNewChoiceToGlobalParameter(
			GlobalParameterNode globalParameterNode, 
			String choiceNodeName, 
			String valueString,
			IModelChangeRegistrator modelChangeRegistrator) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, modelChangeRegistrator);
		globalParameterNode.addChoice(choiceNode);

		return choiceNode;
	}


	public static String getName(MethodParameterNode methodParameterNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(methodParameterNode, extLanguageManager);
	}

	public static String createSignature(
			GlobalParameterNode globalParameterNode, 
			SignatureType signatureType,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = getQualifiedName(globalParameterNode, extLanguageManager);

		if (signatureType == SignatureType.WITHOUT_TYPE) {
			return qualifiedName;
		}

		String type = getType(globalParameterNode, extLanguageManager);

		return type + " " + qualifiedName;
	}

	public static String getQualifiedName(
			GlobalParameterNode globalParameterNode,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = globalParameterNode.getQualifiedName();
		qualifiedName = extLanguageManager.convertTextFromIntrToExtLanguage(qualifiedName);

		return qualifiedName;
	}

	public static String getType(GlobalParameterNode globalParameterNode, IExtLanguageManager extLanguageManager) {

		String type = globalParameterNode.getType();
		type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);

		return type;
	}

}
