/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.LinkedHashSet;
import java.util.Set;

public class ExtLanguageHelper {

	public static String verifySeparatorsInName(String name, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.JAVA) {
			return JavaLanguageHelper.verifySeparators(name);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			return SimpleLanguageHelper.verifySeparators(name);
		}

		ExceptionHelper.reportRuntimeException("Invalid external language.");
		return null;
	}

	public static String validateType(String parameterTypeInExtLanguage, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.JAVA) {
			return JavaLanguageHelper.validateBasicJavaType(parameterTypeInExtLanguage);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			return SimpleLanguageHelper.validateType(parameterTypeInExtLanguage);
		}

		ExceptionHelper.reportRuntimeException("Invalid external language.");
		return null;
	}

	public static String convertTextFromExtToIntrLanguage(String text, ExtLanguage extLanguage)  {

		String errorMessage = verifySeparatorsInName(text, extLanguage);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			text = SimpleLanguageHelper.convertTextFromSimpleToJavaLanguage(text);
		}

		return text;
	}

	public static String convertTextFromIntrToExtLanguage(String text, ExtLanguage extLanguage) {

		String errorMessage = JavaLanguageHelper.verifySeparators(text);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			text = SimpleLanguageHelper.convertTextFromJavaToSimpleLanguage(text);
		}

		return text;
	}

	public static String convertTypeFromIntrToExtLanguage(String type, ExtLanguage extLanguage) {

		if (!JavaLanguageHelper.isValidComplexTypeIdentifier(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert an invalid identifier.");
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			type = SimpleLanguageHelper.convertJavaTypeToSimpleType(type);
		}

		return type;
	}

	public static String convertTypeFromExtToIntrLanguage(String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			type = SimpleLanguageHelper.convertSimpleTypeToJavaType(type);
		}

		if (!JavaLanguageHelper.isJavaType(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert non java type.");
		}

		return type;
	}

	public static String conditionallyConvertSpecialValueToExtLanguage(
			String valueInIntrLanguage, String typeInIntrLanguage, ExtLanguage extLanguage) {

		if (!JavaLanguageHelper.isJavaType(typeInIntrLanguage)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert special value for invalid Java type.");
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			valueInIntrLanguage = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric(typeInIntrLanguage, valueInIntrLanguage);
		}

		return valueInIntrLanguage;
	}

	// TODO SIMPLE-VIEW unit tests
	public static Set<String> getSymbolicNamesOfSpecialValues(String typeName, ExtLanguage extLanguage) {

		Set<String> items;

		if (extLanguage == ExtLanguage.JAVA) {
			items = new LinkedHashSet<String>(JavaLanguageHelper.getSymbolicNamesOfSpecialValues(typeName));
		} else {
			items = new LinkedHashSet<String>();
		}

		return items;
	}
	
}
