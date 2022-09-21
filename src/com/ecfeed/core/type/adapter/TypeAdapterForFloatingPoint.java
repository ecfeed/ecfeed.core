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
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RangeHelper;
import com.ecfeed.core.utils.StringHelper;

public abstract class TypeAdapterForFloatingPoint<T extends Number> extends TypeAdapterForNumericType<T>{

	@Override
	public boolean canCovertWithoutLossOfData(String oldType, String value, boolean isRandomized) {

		if (!canConvertFromToBoolean(oldType, getMyTypeName())) {
			return false;
		}

		String newValue = adapt(value, isRandomized, ERunMode.QUIET, new ExtLanguageManagerForJava());

		if (!isRandomized) {
			return isMatchForFloatingPointStrings(value, newValue);
		}

		String[] range1 = RangeHelper.splitToRange(value);
		String[] range2 = RangeHelper.splitToRange(value);

		if (!isMatchForFloatingPointStrings(range1[0], range2[0])) {
			return false;
		}

		if (!isMatchForFloatingPointStrings(range1[1], range2[1])) {
			return false;
		}

		return true;
	}

	private boolean isMatchForFloatingPointStrings(String value, String newValue) {

		String formattedValueStr = formatValueStringForMatching(value);
		String formattedNewValueStr = formatValueStringForMatching(newValue);

		if (StringHelper.isEqual(formattedValueStr, formattedNewValueStr)) {
			return true;
		}

		return false;
	}

	String formatValueStringForMatching(String valueString) {

		if (isSymbolicValue(valueString)) {
			return valueString;
		}

		try {
			Double number = Double.parseDouble(valueString);
			return number.toString();

		} catch (NumberFormatException e) {
			return valueString;
		}
	}

	@Override
	protected String adaptSingleValue(String value, ERunMode runMode, IExtLanguageManager extLanguageManager) {

		if (isSymbolicValue(value)) {
			return handleConversionOfSymbolicValue(value, runMode, extLanguageManager);
		}

		try {
			Float number = Float.parseFloat(value);
			return number.toString();
		}
		catch (NumberFormatException e) {
			return getDefaultValue();
		}
	}

	@Override
	public String getDefaultValue(){
		return JavaLanguageHelper.DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
	}

}

