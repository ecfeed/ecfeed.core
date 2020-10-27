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

import java.util.Arrays;

import com.ecfeed.core.library.Xeger;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;

public class TypeAdapterForString implements ITypeAdapter<String>{

	private final String[] TYPES_CONVERTABLE_TO_STRING = new String[]{
			JavaLanguageHelper.TYPE_NAME_INT,
			JavaLanguageHelper.TYPE_NAME_FLOAT,
			JavaLanguageHelper.TYPE_NAME_DOUBLE,
			JavaLanguageHelper.TYPE_NAME_LONG,
			JavaLanguageHelper.TYPE_NAME_SHORT,
			JavaLanguageHelper.TYPE_NAME_STRING,
			JavaLanguageHelper.TYPE_NAME_BYTE,
			JavaLanguageHelper.TYPE_NAME_CHAR,
			JavaLanguageHelper.TYPE_NAME_BOOLEAN,
			SimpleLanguageHelper.TYPE_NAME_TEXT,
			SimpleLanguageHelper.TYPE_NAME_NUMBER,
			SimpleLanguageHelper.TYPE_NAME_LOGICAL,
			TypeAdapterHelper.USER_TYPE
	};

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_STRING;
	}

	@Override
	public boolean isRandomizable() {
		return true;
	}

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TYPES_CONVERTABLE_TO_STRING).contains(type);
	}

	@Override
	public String convert(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {
		return value;
	}

	@Override
	public String getDefaultValue() {
		return JavaLanguageHelper.DEFAULT_EXPECTED_STRING_VALUE;
	}

	@Override
	public boolean isNullAllowed() {
		return true;
	}

	@Override
	public String generateValue(String regex) {

		String result = null;

		try {
			Xeger xeger = new Xeger(regex);

			result = xeger.generate();
		} catch (Throwable ex) {

			final String CAN_NOT_GENERATE = 
					"Cannot generate value from expression: " + regex + 
					" (Xeger problem). Reason:" + ex.getClass().getName() + ", Message:" + ex.getMessage();

			ExceptionHelper.reportRuntimeException(CAN_NOT_GENERATE);
		}

		return result;
	}

	@Override
	public String generateValueAsString(String range) {
		return generateValue(range);
	}

}
