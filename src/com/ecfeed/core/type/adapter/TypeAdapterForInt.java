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
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RangeHelper;
import com.ecfeed.core.utils.StringHelper;

public class TypeAdapterForInt extends TypeAdapterForNumericType<Integer> {

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_INT;
	}

	@Override
	protected String convertSingleValue(String value, ERunMode conversionMode) {
		String result = super.convertSpecialValue(value);

		if (result != null) {
			return result;
		}

		try {
			Integer integer = JavaLanguageHelper.convertToInteger(value);
			return String.valueOf(integer);
		} catch (NumberFormatException e) {

			if (conversionMode == ERunMode.WITH_EXCEPTION) {
				TypeAdapterHelper.reportRuntimeExceptionCannotConvert(value, JavaLanguageHelper.TYPE_NAME_INT);
				return null;
			} else {
				return getDefaultValue();
			}
		}
	}

	@Override
	public Integer generateValue(String rangeTxt) {
		String[] range = RangeHelper.splitToRange(rangeTxt);
		
		if (StringHelper.isEqual(range[0], range[1])) {
			return JavaLanguageHelper.parseIntValue(range[0], ERunMode.QUIET);
		}

		return ThreadLocalRandom.current().nextInt(
				JavaLanguageHelper.parseIntValue(range[0], ERunMode.QUIET),
				JavaLanguageHelper.parseIntValue(range[1], ERunMode.QUIET));
	}
	
	@Override
	protected String[] getSpecialValues() {
		return JavaLanguageHelper.SPECIAL_VALUES_FOR_INTEGER;
	}

}
