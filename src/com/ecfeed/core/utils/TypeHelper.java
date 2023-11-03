/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import com.ecfeed.core.type.adapter.ITypeAdapter;

public class TypeHelper {

	public enum TypeCathegory {
		NUMERIC,
		ALFANUMERIC,
		BOOLEAN
	}

	public static TypeCathegory getTypeCathegory(String typeName) {

		if (SimpleLanguageHelper.isTextTypeName(typeName)) {
			return TypeCathegory.ALFANUMERIC;
		}

		if (SimpleLanguageHelper.isNumberTypeName(typeName)) {
			return TypeCathegory.NUMERIC;
		}

		if (SimpleLanguageHelper.isLogicalTypeName(typeName)) {
			return TypeCathegory.BOOLEAN;
		}

		if (JavaLanguageHelper.isTypeWithChars(typeName)) {
			return TypeCathegory.ALFANUMERIC;
		}

		if (JavaLanguageHelper.isNumericTypeName(typeName)) {
			return TypeCathegory.NUMERIC;
		}

		if (JavaLanguageHelper.isBooleanTypeName(typeName)) {
			return TypeCathegory.BOOLEAN;
		}

		ExceptionHelper.reportRuntimeException("Invalid type.");
		return null;
	}

	public static void compareTypes(String type1, String type2) {

		if(type1.equals(type2) == false){
			ExceptionHelper.reportRuntimeException("Different types: " + type1 + ", " + type2);
		}
	}

	public static void compareIntegers(int size, int size2, String message) {

		if (size == size2) {
			return;
		}

		ExceptionHelper.reportRuntimeException("Integers do not match." + " " + message);
	}

	public static boolean isValueCompatibleWithType(
			String value, 
			String newType, 
			boolean isChoiceRandomized) {

		ITypeAdapter<?> typeAdapter = JavaLanguageHelper.getTypeAdapter(newType);

		boolean isCompatible = typeAdapter.isValueCompatibleWithType(value, isChoiceRandomized);

		return isCompatible;
	}

}
