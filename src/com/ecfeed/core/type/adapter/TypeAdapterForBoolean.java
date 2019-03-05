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
import java.util.concurrent.ThreadLocalRandom;

import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SimpleTypeHelper;

public class TypeAdapterForBoolean implements ITypeAdapter<Boolean>{

	private final String[] TYPES_CONVERTABLE_TO_BOOLEAN = new String[]{
		JavaTypeHelper.TYPE_NAME_STRING,
		JavaTypeHelper.TYPE_NAME_BOOLEAN,
		SimpleTypeHelper.TYPE_NAME_TEXT,
		SimpleTypeHelper.TYPE_NAME_LOGICAL
	};

	@Override
	public String getMyTypeName() {
		return JavaTypeHelper.TYPE_NAME_BOOLEAN;
	}

	@Override
	public boolean isRandomizable() {
		return false;
	}

	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TYPES_CONVERTABLE_TO_BOOLEAN).contains(type);
	}

	public String convert(String value, boolean isRandomized, ERunMode conversionMode){
	
		if (conversionMode == ERunMode.WITH_EXCEPTION) {
			return convertForExceptionMode(value);
		}

		return convertForQuietMode(value);
	}

	private String convertForExceptionMode(String value) {

		if (value.equals(JavaTypeHelper.SPECIAL_VALUE_TRUE)) {
			return JavaTypeHelper.SPECIAL_VALUE_TRUE;
		}

		if (value.equals(JavaTypeHelper.SPECIAL_VALUE_FALSE)) {
			return JavaTypeHelper.SPECIAL_VALUE_FALSE;
		}

		reportRuntimeException(value);
		return null;
	}

	private String convertForQuietMode(String value) {

		if (value.toLowerCase().equals(JavaTypeHelper.SPECIAL_VALUE_TRUE.toLowerCase())) {
			return JavaTypeHelper.SPECIAL_VALUE_TRUE;
		}

		if(value.toLowerCase().equals(JavaTypeHelper.SPECIAL_VALUE_FALSE.toLowerCase())){
			return JavaTypeHelper.SPECIAL_VALUE_FALSE;
		}

		return getDefaultValue();
	}
	
	protected void reportRuntimeException(String value) {
		TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaTypeHelper.TYPE_NAME_BOOLEAN);
	}

	@Override
	public String getDefaultValue() {
		return JavaTypeHelper.DEFAULT_EXPECTED_BOOLEAN_VALUE;
	}

	@Override
	public boolean isNullAllowed() {
		return false;
	}

	@Override
	public Boolean generateValue(String range) {
		return ThreadLocalRandom.current().nextBoolean();
	}

	@Override
	public String generateValueAsString(String range) {
		return String.valueOf(generateValue(range));
	}

}
