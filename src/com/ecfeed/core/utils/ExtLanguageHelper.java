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

public class ExtLanguageHelper {

	public static String convertTextFromExtToIntrLanguage(String text, ExtLanguage extLanguage)  {

		if (extLanguage == ExtLanguage.SIMPLE) {
			text = SimpleTypeHelper.convertTextFromSimpleToJavaConvention(text);
		}

		return text;
	}

	public static String convertTextFromIntrToExtLanguage(String text, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			text = SimpleTypeHelper.convertTextFromJavaToSimpleConvention(text);
		}

		return text;
	}

	public static String convertTypeFromIntrToExtLanguage(String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			type = SimpleTypeHelper.convertJavaTypeToSimpleType(type);
		}

		return type;
	}

	public static String convertClassFromIntrToExtLanguage(String className, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			className = StringHelper.getLastToken(className, ".");
		}

		return className;
	}

	public static String convertTypeFromExtToIntrLanguage(String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			type = SimpleTypeHelper.convertSimpleTypeToJavaType(type);
		}

		return type;
	}

	public static String convertSpecialValueFromIntrToExtLanguage(String value, String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			value = JavaTypeHelper.convertSpecialValueToSimpleLanguage(type, value);
		}

		return value;
	}

}
