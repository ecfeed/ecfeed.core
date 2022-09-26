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
import java.util.Set;

import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.TypeHelper.TypeCathegory;
import com.ecfeed.ui.editor.AbstractParameterConversionHelper;
import com.ecfeed.ui.modelif.AbstractParameterInterface;

public abstract class AbstractParameterNodeHelper {

	public static String getName(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = extLanguageManager.convertTextFromIntrToExtLanguage(abstractParameterNode.getName());
		return name;
	}

	public static String validateParameterName(String nameInExternalLanguage, IExtLanguageManager extLanguageManager) {

		String errorMessage = extLanguageManager.verifySeparatorsInName(nameInExternalLanguage);

		return errorMessage;
	}

	public static String getType(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String type = abstractParameterNode.getType();
		type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);
		return type;
	}

	public static String createSignatureOfOneParameterByIntrLanguage(
			String parameterTypeInIntrLanguage,
			String parameterNameInIntrLanguage,
			Boolean expectedFlag,
			IExtLanguageManager extLanguageManager) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		parameterTypeInIntrLanguage = extLanguageManager.convertTypeFromIntrToExtLanguage(parameterTypeInIntrLanguage);

		signature += parameterTypeInIntrLanguage;

		if (parameterNameInIntrLanguage != null) {

			signature += extLanguageManager.getTypeSeparator();

			signature += " ";
			parameterNameInIntrLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(parameterNameInIntrLanguage);

			signature += parameterNameInIntrLanguage;
		}

		return signature;
	}

	public static String createSignature(
			AbstractParameterNode abstractParameterNode, 
			boolean isExpected,
			IExtLanguageManager extLanguageManager) {

		String signature = 
				createSignature(
						getType(abstractParameterNode, extLanguageManager),
						createNameSignature(abstractParameterNode, extLanguageManager),
						isExpected,
						extLanguageManager);

		return signature;
	}

	public static String createSignature(
			String parameterType,
			String parameterName,
			Boolean expectedFlag,
			IExtLanguageManager extLanguageManager) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		signature += parameterType;

		if (parameterName != null) {

			signature += extLanguageManager.getTypeSeparator();
			signature += " ";
			signature += parameterName;
		}

		return signature;
	}

	public static String createReverseSignature(
			String parameterType,
			String parameterName,
			Boolean expectedFlag) {

		String signature = "";

		if (expectedFlag != null) {
			String expectedDecoration = createExpectedDecoration(expectedFlag);
			signature += expectedDecoration;
		}

		if (parameterName != null) {
			signature += parameterName;
		}

		signature += " : ";

		signature += parameterType;

		return signature;
	}

	private static String createExpectedDecoration(Boolean expectedFlag) {

		String signature = "";

		if (expectedFlag != null) {
			if (expectedFlag == true) {
				signature += "[e]";
			}
		}

		return signature;
	}

	public static String createParameterSignature(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = abstractParameterNode.getName();
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);


		String type = getType(abstractParameterNode, extLanguageManager);

		String label = type + " " + name;
		return label;
	}

	public static String createNameSignature(AbstractParameterNode abstractParameterNode, IExtLanguageManager extLanguageManager) {

		String name = abstractParameterNode.getName();
		name = extLanguageManager.convertTextFromIntrToExtLanguage(name);
		return name;
	}

	public static List<String> convertParameterTypesToExtLanguage(
			List<String> parameterTypes,
			IExtLanguageManager extLanguageManager) {

		List<String> result = new ArrayList<String>();

		for (String parameterType : parameterTypes) {

			parameterType = extLanguageManager.convertTypeFromIntrToExtLanguage(parameterType);
			result.add(parameterType);
		}

		return result;
	}

	public static boolean hasRandomizedChoices(AbstractParameterNode abstractParameterNode) {

		Set<ChoiceNode> choices = abstractParameterNode.getAllChoices();

		for (ChoiceNode choice : choices) {

			if (choice.isRandomizedValue()) {
				return true;
			}
		}

		return false;
	}

	public static void setTypeOfAbstractParameter(
			String newTypeInExtLanguage, 
			TypeCathegory typeCathegory,
			ParameterConversionDefinition parameterConversionDefinition, 
			AbstractParameterInterface abstractParameterInterface,
			IExtLanguageManager extLanguageManager) {

		AbstractParameterNode abstractParameterNode = abstractParameterInterface.getOwnNode();

		if (!AbstractParameterConversionHelper.fillParameterConversionDefinition(
				newTypeInExtLanguage, abstractParameterNode, parameterConversionDefinition)) {
			return;
		}

		String newTypeInIntrLanguage = 
				calculateJavaType(
						newTypeInExtLanguage, typeCathegory, parameterConversionDefinition, extLanguageManager);

		abstractParameterInterface.setType(newTypeInIntrLanguage, parameterConversionDefinition);
	}

	private static String calculateJavaType(
			String newTypeInExtLanguage,
			TypeCathegory typeCathegory,
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager) {

		if (extLanguageManager.getLanguage() == ExtLanguage.JAVA) {
			return extLanguageManager.convertTypeFromExtToIntrLanguage(newTypeInExtLanguage);
		}

		String newTypeInIntrLanguage = calculateTypeForSimpleType(newTypeInExtLanguage, typeCathegory,
				parameterConversionDefinition, extLanguageManager);

		return newTypeInIntrLanguage;
	}

	private static String calculateTypeForSimpleType(
			String newTypeInExtLanguage, 
			TypeCathegory typeCathegory,
			ParameterConversionDefinition parameterConversionDefinition, 
			IExtLanguageManager extLanguageManager) {

		String newTypeInIntrLanguage = 
				AbstractParameterConversionHelper.getMaxJavaTypeFromConversionDefinition(
						typeCathegory,
						parameterConversionDefinition);

		if (newTypeInIntrLanguage == null) {
			newTypeInIntrLanguage = extLanguageManager.convertTypeFromExtToIntrLanguage(newTypeInExtLanguage);
		}

		return newTypeInIntrLanguage;
	}

}
