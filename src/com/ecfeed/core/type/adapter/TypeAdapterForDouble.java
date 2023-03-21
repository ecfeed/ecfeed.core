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

public class TypeAdapterForDouble extends TypeAdapterForFloatingPoint<Double>{

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_DOUBLE;
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
			return isSingleValueCompatibleWithType(value);
		}

		String[] range = null;
		try {
			range = RangeHelper.splitToRange(value);
		} catch (Exception e) {
			return false;
		}

		if (!isSingleValueCompatibleWithType(range[0])) {
			return false;
		}

		if (!isSingleValueCompatibleWithType(range[1])) {
			return false;
		}

		return true;
	}

	private boolean isSingleValueCompatibleWithType(String value) {

		try {
			Double.parseDouble(value);
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
			String convertedValue = String.valueOf(JavaLanguageHelper.parseDoubleValue(value, ERunMode.WITH_EXCEPTION));
			convertedValue = extLanguageManager.formatNumber(convertedValue);

			return convertedValue;

		} catch(NumberFormatException e) {
			return TypeAdapterHelper.handleConversionError(value, getMyTypeName(), runMode);
		}
	}

	@Override
	public Double generateValue(String rangeTxt, String context) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		if (StringHelper.isEqual(range[0], range[1])) {
			return JavaLanguageHelper.parseDoubleValue(range[0], ERunMode.QUIET);
		}		

		return ThreadLocalRandom.current().nextDouble(
				JavaLanguageHelper.parseDoubleValue(range[0], ERunMode.QUIET),
				JavaLanguageHelper.parseDoubleValue(range[1], ERunMode.QUIET));

	}

	protected final Double getLowerDouble(String range) {
		return Double.parseDouble(range.split(RangeHelper.DELIMITER)[0]);
	}

	protected final Double getUpperDouble(String range) {
		return Double.parseDouble(range.split(RangeHelper.DELIMITER)[1]);
	}	

	@Override
	protected String[] getSymbolicValues() {
		return JavaLanguageHelper.SPECIAL_VALUES_FOR_DOUBLE;
	}

	@Override
	public boolean isConvertibleTo(String destinationType) {

		if (destinationType.equals(getMyTypeName())) {
			return true;
		}

		return false;
	}	

}
