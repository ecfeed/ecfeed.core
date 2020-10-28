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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public abstract class TypeAdapterForNumericType<T extends Number> extends TypeAdapterForTypeWithRange<T> {

	public static final String DELIMITER = ":";		

	@Override
	public boolean isCompatible(String type) {
		return Arrays.asList(TypeAdapterHelper.TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
	}

	protected boolean checkIsSpecialValue(String value, IExtLanguageManager extLanguageManager){
		
		// TODO SIMPLE-VIEW - check if special values are allowed
		
		boolean isSpecialValue = Arrays.asList(getSpecialValues()).contains(value);
		
		if (!isSpecialValue) {
			return false;
		}
		
		if (!extLanguageManager.isSpecialValueAllowed()) {
			ExceptionHelper.reportRuntimeException("Special values are not allowed.");
		}
			
		return true;
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
	public String generateValueAsString(String range) {
		return String.valueOf(generateValue(range));
	}

}
