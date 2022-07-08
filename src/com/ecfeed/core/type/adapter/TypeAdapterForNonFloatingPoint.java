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
import com.ecfeed.core.utils.RangeHelper;
import com.ecfeed.core.utils.StringHelper;

public abstract class TypeAdapterForNonFloatingPoint<T extends Number> extends TypeAdapterForNumericType<T>{

	@Override
	public boolean canCovertWithoutLossOfData(String value, boolean isRandomized) {

		String newValue = adapt(value, isRandomized, ERunMode.QUIET, new ExtLanguageManagerForJava());

		if (!isRandomized) {

			if (isEqualForSingleValue(value, newValue)) {
				return true;
			}

			return false;
		}

		if (isEqualForRange(value, newValue)) {
			return true;
		}

		return false;
	}

	private boolean isEqualForRange(String value, String newValue) {

		String[] range1 = RangeHelper.splitToRange(value);
		String[] range2 = RangeHelper.splitToRange(newValue);

		if (!isEqualForSingleValue(range1[0], range2[0])) {
			return false;
		}

		if (!isEqualForSingleValue(range1[1], range2[1])) {
			return false;
		}

		return true;
	}

	private boolean isEqualForSingleValue(String value, String newValue) {

		if (StringHelper.isEqual(newValue, value)) {
			return true;
		}

		if (isEqualExcludingZero(value, newValue)) {
			return true;
		}

		return false;		
	}


	private boolean isEqualExcludingZero(String value, String newValue) {

		String valueTrimmedConditionally = StringHelper.getFirstToken(value, ".0");

		if (StringHelper.isEqual(valueTrimmedConditionally, newValue)) {
			return true;
		}

		return false;
	}

}

