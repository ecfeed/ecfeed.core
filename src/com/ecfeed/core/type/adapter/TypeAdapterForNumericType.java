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
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public abstract class TypeAdapterForNumericType<T extends Number> extends TypeAdapterForTypeWithRange<T> {

	public static final String DELIMITER = ":";
	public static final String SPECIAL_VALUES_ARE_NOT_ALLOWED = "Special values are not allowed.";

	@Override
	public boolean isCompatible(String type) {
		return Arrays.asList(TypeAdapterHelper.TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
	}

	protected boolean isSymbolicValue(String value) {

		boolean isSymbolicValue = Arrays.asList(getSymbolicValues()).contains(value);

		return isSymbolicValue;
	}

	protected String handleConversionOfSymbolicValue(String symbolicValue, ERunMode runMode, IExtLanguageManager extLanguageManager) {

		if (extLanguageManager.isSymbolicValueAllowed()) {
			return symbolicValue;
		}

		if (runMode == ERunMode.QUIET) {
			return TypeAdapterHelper.handleConversionError(symbolicValue, getMyTypeName(), runMode);
		}

		ExceptionHelper.reportRuntimeException(SPECIAL_VALUES_ARE_NOT_ALLOWED);
		return null;
	}

	@Override
	public String getDefaultValue() {
		return JavaLanguageHelper.DEFAULT_EXPECTED_NUMERIC_VALUE;
	}

	@Override
	public boolean isNullAllowed() {
		return false;
	}

	@Override
	public String generateValueAsString(String range, String context) {
		return String.valueOf(generateValue(range, context));
	}

}
