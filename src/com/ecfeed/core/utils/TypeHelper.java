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


}
