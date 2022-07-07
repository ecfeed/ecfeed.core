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
import com.ecfeed.core.utils.StringHelper;

public abstract class TypeAdapterNonFloatingPoint<T extends Number> extends TypeAdapterForNumericType<T>{

	@Override
	public boolean canCovertWithoutLossOfData(String value, boolean isRandomized) {

		if (isRandomized) {
			return false; // TODO DE-NO 
		}

		String newValue = adapt(value, isRandomized, ERunMode.QUIET, new ExtLanguageManagerForJava());

		if (StringHelper.isEqual(newValue, value)) {
			return true;
		}

		String valueTrimmedConditionally = StringHelper.getFirstToken(value, ".0");

		if (StringHelper.isEqual(newValue, valueTrimmedConditionally)) {
			return true;
		}

		return false;
	}

}

