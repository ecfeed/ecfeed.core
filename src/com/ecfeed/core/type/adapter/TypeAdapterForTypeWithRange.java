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
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.RangeHelper;

public abstract class TypeAdapterForTypeWithRange<T> implements ITypeAdapter<T> {

	protected abstract String adaptSingleValue(String value, ERunMode conversionMode, IExtLanguageManager extLanguageManager);

	protected abstract String[] getSymbolicValues();

	@Override
	public boolean isRandomizable() {
		return true;
	}

	@Override
	public String adapt(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {

		if (!RangeHelper.isRange(value)) {
			return adaptNotRange(value, isRandomized, conversionMode, extLanguageManager);
		}

		return adaptRange(value, isRandomized, conversionMode, extLanguageManager);
	}

	private String adaptNotRange(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {
		
		String result = adaptSingleValue(value, conversionMode, extLanguageManager);

		if (!isRandomized) {
			return result;
		}

		if (conversionMode == ERunMode.QUIET) {
			return RangeHelper.createRange(result);
		}

		final String VALUE_IS_INVALID = "Value [" + value + "] is not allowed for randomized choice.";
		ExceptionHelper.reportRuntimeException(VALUE_IS_INVALID);
		return null;
	}

	private String adaptRange(
			String value, 
			boolean isRandomized, 
			ERunMode conversionMode, 
			IExtLanguageManager extLanguageManager) { // TODO  SIMPLE-VIEW check  for MAX_VALUE and Simple
		
		String[] range = RangeHelper.splitToRange(value);

		if (isRandomized) {
			String firstValue = adaptSingleValue(range[0], conversionMode, extLanguageManager);
			String secondValue = adaptSingleValue(range[1], conversionMode, extLanguageManager);

			checkRange(range, conversionMode);		

			return RangeHelper.createRange(firstValue, secondValue);
		}

		if (conversionMode == ERunMode.QUIET) {
			return range[0];
		}

		final String VALUE_IS_INVALID = "Value [" + value + "] is not allowed for not randomized choice.";
		ExceptionHelper.reportRuntimeException(VALUE_IS_INVALID);
		return null;
	}

	private void checkRange(String[] range, ERunMode conversionMode) {

		if (conversionMode != ERunMode.WITH_EXCEPTION) {
			return;
		}

		if (!RangeHelper.isRangeCorrect(range, getMyTypeName())) {
			final String RANGE_IS_INVALID = "Range [" + range[0] + ", " + range[1] + "] is invalid.";
			ExceptionHelper.reportRuntimeException(RANGE_IS_INVALID);
		}
	}

}
