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

import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SimpleTypeHelper;

import nl.flotsam.xeger.Xeger;

public class TypeAdapterForString implements ITypeAdapter<String>{

	private final String[] TYPES_CONVERTABLE_TO_STRING = new String[]{
			JavaTypeHelper.TYPE_NAME_INT, 
			JavaTypeHelper.TYPE_NAME_FLOAT, 
			JavaTypeHelper.TYPE_NAME_DOUBLE, 
			JavaTypeHelper.TYPE_NAME_LONG, 
			JavaTypeHelper.TYPE_NAME_SHORT, 
			JavaTypeHelper.TYPE_NAME_STRING, 
			JavaTypeHelper.TYPE_NAME_BYTE,
			JavaTypeHelper.TYPE_NAME_CHAR,
			JavaTypeHelper.TYPE_NAME_BOOLEAN,
			SimpleTypeHelper.TYPE_NAME_TEXT,
			SimpleTypeHelper.TYPE_NAME_NUMBER,
			SimpleTypeHelper.TYPE_NAME_LOGICAL,
			TypeAdapterHelper.USER_TYPE
	};

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_STRING;
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
	public String convert(String value, boolean isRandomized, ERunMode conversionMode) {
		return value;
	}

	@Override
	public String getDefaultValue() {
		return JavaTypeHelper.DEFAULT_EXPECTED_STRING_VALUE;
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
