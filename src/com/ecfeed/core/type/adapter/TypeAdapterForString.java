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
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class TypeAdapterForString implements ITypeAdapter<String>{

	@Override
	public String getMyTypeName() {
		return JavaLanguageHelper.TYPE_NAME_STRING;
	}

	@Override
	public boolean isRandomizable() {
		return true;
	}

	@Override
	public boolean isConvertibleTo(String destinationType) {

		if (destinationType.equals(getMyTypeName())) {
			return true;
		}

		return false;
	}

	@Override
	public String adapt(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {
		return value;
	}

	@Override
	public String getDefaultValue() {
		return JavaLanguageHelper.DEFAULT_EXPECTED_STRING_VALUE;
	}

	@Override
	public boolean isNullAllowed() {
		return true;
	}

	@Override
	public String generateValue(String regex, String context) {

		String result = null;

		try {
			Xeger xeger = new Xeger(regex);

			result = xeger.generate();
		} catch (Throwable ex) {

			final String CAN_NOT_GENERATE =	"Cannot generate value from regex expression: " + regex + ". " + context ; 

			ExceptionHelper.reportRuntimeException(CAN_NOT_GENERATE);
		}

		return result;
	}

	@Override
	public String generateValueAsString(String range, String context) {
		return generateValue(range, context);
	}

}
