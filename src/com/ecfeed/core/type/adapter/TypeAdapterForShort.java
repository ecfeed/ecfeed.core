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

public class TypeAdapterForShort extends TypeAdapterForNumericType<Short> {

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_SHORT;
	}

	@Override
	public String adaptSingleValue(String value, ERunMode runMode, IExtLanguageManager extLanguageManager) {

		if (isSymbolicValue(value)) {
			return handleConversionOfSymbolicValue(value, runMode, extLanguageManager);
		}

		try {
			return String.valueOf(JavaLanguageHelper.convertToShort(value));
		} catch (NumberFormatException e) {
			return TypeAdapterHelper.handleConversionError(value, getMyTypeName(), runMode);
		}
	}

	@Override
	public Short generateValue(String rangeTxt, String context) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		if (StringHelper.isEqual(range[0], range[1])) {
			return JavaLanguageHelper.parseShortValue(range[0], ERunMode.QUIET);
		}

		return (short) ThreadLocalRandom.current().nextInt(
				JavaLanguageHelper.parseShortValue(range[0], ERunMode.QUIET),
				1 + JavaLanguageHelper.parseShortValue(range[1], ERunMode.QUIET));
	}

	@Override
	protected String[] getSymbolicValues() {
		return JavaLanguageHelper.SPECIAL_VALUES_FOR_SHORT;
	}

	@Override
	public boolean isConvertibleTo(String destinationType) {

		if (destinationType.equals(getMyTypeName())) {
			return true;
		}

		if (destinationType.equals(JavaLanguageHelper.TYPE_NAME_DOUBLE)) {
			return true;
		}

		if (destinationType.equals(JavaLanguageHelper.TYPE_NAME_FLOAT)) {
			return true;
		}

		if (destinationType.equals(JavaLanguageHelper.TYPE_NAME_LONG)) {
			return true;
		}

		if (destinationType.equals(JavaLanguageHelper.TYPE_NAME_INT)) {
			return true;
		}

		return false;
	}

}
