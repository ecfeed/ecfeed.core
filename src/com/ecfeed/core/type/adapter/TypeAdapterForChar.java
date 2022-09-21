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

import com.ecfeed.core.library.Xeger;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class TypeAdapterForChar implements ITypeAdapter<Character>{

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_CHAR;
	}

	@Override
	public String getDefaultValue() {
		return JavaLanguageHelper.DEFAULT_EXPECTED_CHAR_VALUE;
	}

	@Override
	public boolean isNullAllowed() {
		return false;
	}

	@Override
	public Character generateValue(String regex, String context) {
		return new Xeger(regex).generate().charAt(0);
	}

	@Override
	public String generateValueAsString(String range, String context) {
		return String.valueOf(generateValue(range, context));
	}

	@Override
	public boolean isRandomizable() {
		return false;
	}

	@Override
	public String adapt(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {

		if (value.length() == 1) {
			return value;
		}

		return TypeAdapterHelper.handleConversionError(value, getMyTypeName(), conversionMode);
	}

	@Override
	public boolean isConvertibleTo(String destinationType) {

		if (destinationType.equals(getMyTypeName())) {
			return true;
		}

		if (destinationType.equals(JavaLanguageHelper.TYPE_NAME_STRING)) {
			return true;
		}

		return false;
	}

}
