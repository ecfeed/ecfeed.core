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
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public abstract class TypeAdapterFloatingPoint<T extends Number> extends TypeAdapterForNumericType<T>{

	@Override
	public boolean canCovertWithoutLossOfData(String value, boolean isRandomized) {
		
		if (isRandomized) {
			return false; // TODO DE-NO 
		}
		
		String newValue = adapt(value, isRandomized, ERunMode.QUIET, new ExtLanguageManagerForJava());
		
		Double parsedValue = Double.parseDouble(value);
		Double parsedNewValue = Double.parseDouble(newValue);
		
		String formattedValueStr = parsedValue.toString();
		String formattedNewValueStr = parsedNewValue.toString();
		
		if (StringHelper.isEqual(formattedValueStr, formattedNewValueStr)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isCompatible(String type){
		return Arrays.asList(TypeAdapterHelper.TYPES_CONVERTIBLE_TO_NUMBERS).contains(type);
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

