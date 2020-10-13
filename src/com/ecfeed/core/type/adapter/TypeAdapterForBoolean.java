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
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class TypeAdapterForBoolean implements ITypeAdapter<Boolean>{

	private final String[] TYPES_CONVERTABLE_TO_BOOLEAN = new String[]{
		JavaLanguageHelper.TYPE_NAME_STRING,
		JavaLanguageHelper.TYPE_NAME_BOOLEAN,
		SimpleLanguageHelper.TYPE_NAME_TEXT,
		SimpleLanguageHelper.TYPE_NAME_LOGICAL
	};

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_BOOLEAN;
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

		if (value.equals(JavaLanguageHelper.SPECIAL_VALUE_TRUE)) {
			return JavaLanguageHelper.SPECIAL_VALUE_TRUE;
		}

		if (value.equals(JavaLanguageHelper.SPECIAL_VALUE_FALSE)) {
			return JavaLanguageHelper.SPECIAL_VALUE_FALSE;
		}

		reportRuntimeException(value);
		return null;
	}

	private String convertForQuietMode(String value) {
		
		if (StringHelper.isNullOrEmpty(value)) {
			return JavaLanguageHelper.SPECIAL_VALUE_FALSE;
		}
		
		if (value.toLowerCase().equals(JavaLanguageHelper.SPECIAL_VALUE_TRUE.toLowerCase())) {
			return JavaLanguageHelper.SPECIAL_VALUE_TRUE;
		}

		if(value.toLowerCase().equals(JavaLanguageHelper.SPECIAL_VALUE_FALSE.toLowerCase())){
			return JavaLanguageHelper.SPECIAL_VALUE_FALSE;
		}

		value = StringHelper.removeFromPostfix(".", value);
		
		if (StringHelper.isEqual("1", value)) {
			return JavaLanguageHelper.SPECIAL_VALUE_TRUE;
		}
		
		if (StringHelper.isEqual("0", value)) {
			return JavaLanguageHelper.SPECIAL_VALUE_TRUE;
		}
		
		return getDefaultValue();
	}
	
	protected void reportRuntimeException(String value) {
		TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaLanguageHelper.TYPE_NAME_BOOLEAN);
	}

	@Override
	public String getDefaultValue() {
		return JavaLanguageHelper.DEFAULT_EXPECTED_BOOLEAN_VALUE;
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
