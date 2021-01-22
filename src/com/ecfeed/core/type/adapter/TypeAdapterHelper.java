/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.type.adapter;

import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;

public class TypeAdapterHelper {

	public static final String USER_TYPE = "USER_TYPE";

	public static final String[] TYPES_CONVERTABLE_TO_NUMBERS = new String[] {
		JavaLanguageHelper.TYPE_NAME_INT,
		JavaLanguageHelper.TYPE_NAME_FLOAT,
		JavaLanguageHelper.TYPE_NAME_DOUBLE,
		JavaLanguageHelper.TYPE_NAME_LONG,
		JavaLanguageHelper.TYPE_NAME_SHORT,
		JavaLanguageHelper.TYPE_NAME_STRING,
		JavaLanguageHelper.TYPE_NAME_BYTE,
		JavaLanguageHelper.TYPE_NAME_CHAR,
		SimpleLanguageHelper.TYPE_NAME_NUMBER
	};

	private static final String CANNOT_CONVERT_VALUE = "Cannot convert value";

	public static void reportRuntimeExceptionCannotConvert(String value, String typeName) {

		final String CANNOT_CONVERT_VALUE_MSG = CANNOT_CONVERT_VALUE + " [" + value + "] to " + typeName + ".";
		ExceptionHelper.reportRuntimeException(CANNOT_CONVERT_VALUE_MSG);
	}

	public static String handleConversionError(String value, String type, ERunMode conversionMode) {

		if (conversionMode == ERunMode.QUIET) {
			return JavaLanguageHelper.getDefaultValue(type);
		}

		TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, type);
		return null;
	}
}
