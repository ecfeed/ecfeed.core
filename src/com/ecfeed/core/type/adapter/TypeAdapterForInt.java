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

public class TypeAdapterForInt extends TypeAdapterNonFloatingPoint<Integer> {

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_INT;
	}

	@Override
	public String adaptSingleValue(String value, ERunMode runMode, IExtLanguageManager extLanguageManager) {

		if (isSymbolicValue(value)) {
			return handleConversionOfSymbolicValue(value, runMode, extLanguageManager);
		}

		try {
			Integer integer = JavaLanguageHelper.convertToInteger(value);
			return String.valueOf(integer);
		} catch (NumberFormatException e) {

			if (runMode == ERunMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaLanguageHelper.TYPE_NAME_INT);
				return null;
			} else {
				return getDefaultValue();
			}
		}
	}

	@Override
	public Integer generateValue(String rangeTxt, String context) {

		String[] range = RangeHelper.splitToRange(rangeTxt);

		String range0 = range[0];
		String range1 = range[1];

		if (StringHelper.isEqual(range0, range1)) {
			return JavaLanguageHelper.parseIntValue(range0, ERunMode.QUIET);
		}

		return ThreadLocalRandom.current().nextInt(
				JavaLanguageHelper.parseIntValue(range0, ERunMode.QUIET),
				1 + JavaLanguageHelper.parseIntValue(range1, ERunMode.QUIET));
	}

	@Override
	protected String[] getSymbolicValues() {
		return JavaLanguageHelper.SPECIAL_VALUES_FOR_INTEGER;
	}

	@Override
	public boolean isConvertibleTo(String destinationType) {

		if (destinationType.equals(getMyTypeName())) {
			return true;
		}

		if (destinationType.equals(JavaLanguageHelper.TYPE_NAME_DOUBLE)) {
			return true;
		}

		if (destinationType.equals(JavaLanguageHelper.TYPE_NAME_LONG)) {
			return true;
		}

		return false;
	}

}
