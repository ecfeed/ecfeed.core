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
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public interface ITypeAdapter<T> {

	public boolean isRandomizable();
	public boolean isCompatible(String type); // TODO DE-NO remove ?
	public boolean isConvertibleTo(String otherType);
	public String adapt(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager);
	public String getDefaultValue();
	public boolean isNullAllowed();
	public T generateValue(String range, String context);
	public String generateValueAsString(String range, String context);
	public String getMyTypeName();

	public default boolean canCovertWithoutLossOfData(String oldType, String value, boolean isRandomized) {

		String newType = getMyTypeName();

		if (oldType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN) 
				|| newType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {

			return canConvertFromToBoolean(oldType, newType);
		}

		if (isRandomized) {
			return false; // TODO DE-NO 
		}

		if (isValueCompatibleWithType(value, isRandomized)) {
			return true;
		}

		return false;
	}

	public default boolean isValueCompatibleWithType(String value, boolean isRandomized) {

		String newValue = adapt(value, isRandomized, ERunMode.QUIET, new ExtLanguageManagerForJava());

		if (StringHelper.isEqual(newValue, value)) {
			return true;
		}

		return false;
	}

	default boolean canConvertFromToBoolean(String oldType, String newType) {

		if (oldType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {

			if (newType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {
				return true;
			}

			return false;
		}

		if (newType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {

			if (oldType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {
				return true;
			}

			return false;
		}

		return true;
	}

}
