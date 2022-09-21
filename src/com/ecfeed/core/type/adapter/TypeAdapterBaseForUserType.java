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
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class TypeAdapterBaseForUserType<T extends Enum<T>> implements ITypeAdapter<T> {

	@SuppressWarnings("unused")
	private String fType;

	public TypeAdapterBaseForUserType(String type){
		fType = type;
	}

	public String adapt(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager) {
		return JavaLanguageHelper.isValidJavaIdentifier(value) ? value : null;
	}

	@Override
	public String getDefaultValue() {
		return null;
	}

	@Override
	public boolean isNullAllowed() {
		return true;
	}

	@Override
	public T generateValue(String range, String context) {
		return null;
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
	public String getMyTypeName() {
		return "USER-TYPE";
	}

	@Override
	public boolean isConvertibleTo(String destinationType) {

		if (destinationType.equals(getMyTypeName())) {
			return true;
		}

		return false;
	}

}
