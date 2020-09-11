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

// TODO SIMPLE-VIEW unit tests

public class ExtLanguageHelper {

	public static String verifySeparatorsInName(String name, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.JAVA) {
			return JavaLanguageHelper.verifySeparatorsInName(name);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			return SimpleLanguageHelper.verifySeparatorsInName(name);
		}

		ExceptionHelper.reportRuntimeException("Invalid external language.");
		return null;
	}

	public static String validateType(String parameterTypeInExtLanguage, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.JAVA) {
			return JavaLanguageHelper.validateType(parameterTypeInExtLanguage);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			return SimpleLanguageHelper.validateType(parameterTypeInExtLanguage);
		}

		ExceptionHelper.reportRuntimeException("Invalid external language.");
		return null;

	}


	public static String convertTextFromExtToIntrLanguage(String text, ExtLanguage extLanguage)  {

		if (extLanguage == ExtLanguage.SIMPLE) {
			text = SimpleLanguageHelper.convertTextFromSimpleToJavaConvention(text);
		}

		return text;
	}

	public static String convertTextFromIntrToExtLanguage(String text, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			text = SimpleLanguageHelper.convertTextFromJavaToSimpleConvention(text);
		}

		return text;
	}

	public static String convertTypeFromIntrToExtLanguage(String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			type = SimpleLanguageHelper.convertJavaTypeToSimpleType(type);
		}

		return type;
	}

	// TODO SIMPLE-VIEW move to classNodeHelper (and rename to createSignature?) or remove ?
	public static String convertClassFromIntrToExtLanguage(String className, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			className = StringHelper.getLastToken(className, ".");
		}

		return className;
	}

	public static String convertTypeFromExtToIntrLanguage(String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			type = SimpleLanguageHelper.convertSimpleTypeToJavaType(type);
		}

		return type;
	}

	public static String convertSpecialValueFromIntrToExtLanguage(String value, String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			value = JavaLanguageHelper.convertSpecialValueToSimpleLanguage(type, value);
		}

		return value;
	}

}
