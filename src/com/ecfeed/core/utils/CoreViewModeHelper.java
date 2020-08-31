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

import com.ecfeed.core.model.ModelOperationException;

public class CoreViewModeHelper { // TODO - SIMPLE-VIEW rename

	public static String adjustTextToConvention(String text, ExtLanguage viewMode) throws ModelOperationException { // use in operations only

		if (viewMode == ExtLanguage.SIMPLE) {

			if (text.contains("_")) {
				ModelOperationException.report("Underline chars are not allowed in simple view.");
			}

			text = text.replace(" ", "_");
		}

		return text;
	}

	// TODO SIMPLE-VIEW - make method more general
	public static String getNewNameInJavaConvention(String newName, ExtLanguage viewMode)  {

		if (viewMode == ExtLanguage.SIMPLE) {

			String result = SimpleTypeHelper.convertTextFromSimpleToJavaConvention(newName);

			return result; 
		}

		return newName;
	}

	// TODO SIMPLE-VIEW rename
	public static String convertTextToConvention(String text, ExtLanguage viewMode) {

		if (viewMode == ExtLanguage.SIMPLE) {
			text = SimpleTypeHelper.convertTextFromJavaToSimpleConvention(text);
		}

		return text;
	}

	public static String convertTypeToConvention(String type, ExtLanguage viewMode) {

		if (viewMode == ExtLanguage.SIMPLE) {
			type = SimpleTypeHelper.convertJavaTypeToSimpleType(type);
		}

		return type;
	}

	public static String convertSpecialValueToConvention(String value, String type, ExtLanguage viewMode) {
		
		if (viewMode == ExtLanguage.SIMPLE) {
			value = JavaTypeHelper.convertConditionallySpecialValue(type, value);
		}
		
		return value;
	}
	
}
