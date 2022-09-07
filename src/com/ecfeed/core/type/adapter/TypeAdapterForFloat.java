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
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RangeHelper;
import com.ecfeed.core.utils.StringHelper;

public class TypeAdapterForFloat extends TypeAdapterForFloatingPoint<Float>{

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_FLOAT;
	}

	@Override
	public String adaptSingleValue(String value, ERunMode runMode, IExtLanguageManager extLanguageManager) {

		String result = convert2(value, runMode, extLanguageManager);
		result = extLanguageManager.formatNumber(result);

		return result;
	}

	@Override
	public boolean isValueCompatibleWithType(String value, boolean isRandomized) {

		if (!isRandomized) {
			return isSingleValueCompatible(value);
		}

		String[] range = RangeHelper.splitToRange(value);

		if (!isSingleValueCompatible(range[0])) {
			return false;
		}

		if (!isSingleValueCompatible(range[1])) {
			return false;
		}

		return true;
	}

	private boolean isSingleValueCompatible(String value) {

		try {
			Float.parseFloat(value);
			return true;

		} catch (NumberFormatException e) {
			return false;
		}
	}

	public String convert2(String value, ERunMode runMode, IExtLanguageManager extLanguageManager) {

		if (isSymbolicValue(value)) {
			return handleConversionOfSymbolicValue(value, runMode, extLanguageManager);
		}

		try {
			String convertedValue = String.valueOf(Float.parseFloat(value));
			return convertedValue;

		} catch(NumberFormatException e) {
			String convertedValue = TypeAdapterHelper.handleConversionError(value, getMyTypeName(), runMode);

			return convertedValue;
		}
	}

	@Override
	public Float generateValue(String rangeTxt, String context) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		if (StringHelper.isEqual(range[0], range[1])) {
			return JavaLanguageHelper.parseFloatValue(range[0], ERunMode.QUIET);
		}

		return (float) ThreadLocalRandom.current().nextDouble(
				JavaLanguageHelper.parseFloatValue(range[0], ERunMode.QUIET),
				JavaLanguageHelper.parseFloatValue(range[1], ERunMode.QUIET));
	}

	@Override
	protected String[] getSymbolicValues() {
		return JavaLanguageHelper.SPECIAL_VALUES_FOR_FLOAT;
	}

	@Override
	public boolean isConvertibleTo(String destinationType) {

		if (destinationType.equals(getMyTypeName())) {
			return true;
		}

		if (destinationType.equals(JavaLanguageHelper.TYPE_NAME_DOUBLE)) {
			return true;
		}

		return false;
	}	

}
