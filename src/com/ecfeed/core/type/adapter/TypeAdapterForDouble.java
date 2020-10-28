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

public class TypeAdapterForDouble extends TypeAdapterFloatingPoint<Double>{

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_DOUBLE;
	}

	@Override
	public String convertSingleValue(String value, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {
		
		if (checkIsSymboliclValue(value, extLanguageManager)) {
			return value;
		}

		try {
			return String.valueOf(JavaLanguageHelper.parseDoubleValue(value, ERunMode.WITH_EXCEPTION));
		} catch(NumberFormatException e) {
			return TypeAdapterHelper.handleConversionError(value, getMyTypeName(), conversionMode);
		}
	}

	@Override
	public Double generateValue(String rangeTxt) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		if (StringHelper.isEqual(range[0], range[1])) {
			return JavaLanguageHelper.parseDoubleValue(range[0], ERunMode.QUIET);
		}		
		
		return ThreadLocalRandom.current().nextDouble(
				JavaLanguageHelper.parseDoubleValue(range[0], ERunMode.QUIET),
				JavaLanguageHelper.parseDoubleValue(range[1], ERunMode.QUIET));

	}

	protected final Double getLowerDouble(String range) {
		return Double.parseDouble(range.split(DELIMITER)[0]);
	}

	protected final Double getUpperDouble(String range) {
		return Double.parseDouble(range.split(DELIMITER)[1]);
	}	

	@Override
	protected String[] getSymbolicValues() {
		return JavaLanguageHelper.SPECIAL_VALUES_FOR_DOUBLE;
	}	

}
