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

import java.util.concurrent.ThreadLocalRandom;

import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RangeHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class TypeAdapterForNumber extends TypeAdapterForNumericType<Number>{

	@Override
	public String getMyTypeName() {
		return SimpleLanguageHelper.TYPE_NAME_NUMBER;
	}

	@Override
	public String convertSingleValue(String value, ERunMode conversionMode) {
		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			return String.valueOf(Long.parseLong(value));
		} catch(NumberFormatException e){}
		
		try {
			return String.valueOf(Double.parseDouble(value));
		} catch(NumberFormatException e){}
		
		return TypeAdapterHelper.handleConversionError(value, getMyTypeName(), conversionMode);
	}

	@Override
	public Number generateValue(String rangeTxt) {
		String[] range = RangeHelper.splitToRange(rangeTxt);
		String type = parseValue(range);
		
		if (StringHelper.isEqual(range[0], range[1])) {
			return JavaLanguageHelper.parseNumberValue(range[0], type, ERunMode.QUIET);
		}

		return generateNumber(range, type);
	}
	
	private String parseValue(String[] range) {
		try {
			Integer.parseInt(range[0]);
			Integer.parseInt(range[1]);
			return JavaLanguageHelper.TYPE_NAME_INT;
		} catch (NumberFormatException e1) {
		}
		
		return parseLong(range);
	}
	
	private String parseLong(String[] range) {
		try {
			Long.parseLong(range[0]);
			Long.parseLong(range[1]);
			return JavaLanguageHelper.TYPE_NAME_LONG;
		} catch (NumberFormatException e2) {
		}
		
		return parseDouble(range);
	}
	
	private String parseDouble(String[] range) {
		try {
			Double.parseDouble(range[0]);
			Double.parseDouble(range[1]);
			return JavaLanguageHelper.TYPE_NAME_DOUBLE;
		} catch (NumberFormatException e3) {
		}
		
		return parseDefault(range);
	}
	
	private String parseDefault(String[] range) {
		range[0] = "0";
		range[1] = "0";
		return JavaLanguageHelper.TYPE_NAME_INT;
	}
	
	private Number generateNumber(String[] range, String type) {
		switch(type) {
			case JavaLanguageHelper.TYPE_NAME_INT :	return generateInt(range, type);
			case JavaLanguageHelper.TYPE_NAME_LONG : return generateLong(range, type);
			case JavaLanguageHelper.TYPE_NAME_DOUBLE : return generateDouble(range, type);
			default : return 0;
		}
	}
	
	private Integer generateInt(String[] range, String type) {
		return ThreadLocalRandom.current().nextInt(
				(Integer) JavaLanguageHelper.parseNumberValue(range[0], type, ERunMode.QUIET),
				(Integer) JavaLanguageHelper.parseNumberValue(range[1], type, ERunMode.QUIET));
	}
	
	private Long generateLong(String[] range, String type) {
		return ThreadLocalRandom.current().nextLong(
				(Long) JavaLanguageHelper.parseNumberValue(range[0], type, ERunMode.QUIET),
				(Long) JavaLanguageHelper.parseNumberValue(range[1], type, ERunMode.QUIET));
	}
	
	private Double generateDouble(String[] range, String type) {
		return ThreadLocalRandom.current().nextDouble(
				(Double) JavaLanguageHelper.parseNumberValue(range[0], type, ERunMode.QUIET),
				(Double) JavaLanguageHelper.parseNumberValue(range[1], type, ERunMode.QUIET));
	}

	@Override
	protected String[] getSpecialValues() {
		return new String[0];
	}

}
