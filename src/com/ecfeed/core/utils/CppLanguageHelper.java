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

import java.util.Arrays;

public final class CppLanguageHelper {
	
	private static String[] CPP_TYPES = {
		
//			"short",
//			"short int",
//			"signed short",
//			"signed short int",
//			"unsigned short",
//			"unsigned short int",
//			"unsigned short int",
//			"int",
//			"signed",
//			signed int
//			unsigned
//			unsigned int
//			long
//			long int
//			long int
//			signed long
//			signed long int
//			unsigned long
//			unsigned long int
//			unsigned long int
//			long long
//			long long int
//			long long int
//			signed long long
//			signed long long int
//			unsigned long long
//			unsigned long long int
//			unsigned long long int
//			
//			"char",
//			"char16_t",
//			"char32_t",
//			"wchar_t",
//			"int",
//			"double",
//			
//			"signed char",
//			
//			"string",
//			"bool"
	};

//	private static class TypeConversion()
	public static boolean isAllowedType(String typeName) {

		if (Arrays.asList(CPP_TYPES).contains(typeName)) {
			return true;
		}
		
		return false;
	}

	public String convertToJavaType(String cppType) {
		return null;
	}
}
