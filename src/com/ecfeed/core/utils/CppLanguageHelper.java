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

public final class CppLanguageHelper {

	private static Type[] CPP_TYPES = {

			new Type("short", "short"),
			new Type("short int", "short"),
			new Type("signed short", "short"),
			new Type("signed short int","short"),
			new Type("unsigned short", "short"),
			new Type("unsigned short int", "short"),
			
			new Type("int", "int"),
			new Type("signed", "int"),
			new Type("signed int", "int"),
			new Type("unsigned", "int"),
			new Type("unsigned int", "int"),
			
			new Type("long", "long"),
			new Type("long int", "long"),
			new Type("long int", "long"),
			new Type("signed long",  "long"),
			new Type("signed long int", "long"),
			new Type("unsigned long", "long"),
			new Type("unsigned long int", "long"),
			new Type("unsigned long int", "long"),
			new Type("long long", "long"),
			new Type("long long int", "long"),
			new Type("long long int", "long"),
			new Type("signed long long", "long"),
			new Type("signed long long int", "long"),
			new Type("unsigned long long", "long"),
			new Type("unsigned long long int", "long"),
			new Type("unsigned long long int", "long"),


			new Type("float", "float"),
			new Type("double", "double"),
			new Type("long double", "double"),

			new Type("bool", "boolean"),

			new Type("signed char", "String"),
			new Type("unsigned char", "String"),
			new Type("unsigned char", "String"),
			new Type("char16_t", "String"),
			new Type("char32_t", "String"),
			new Type("wchar_t", "String"),
			new Type("string", "String"),
	};

	private static class Type {
		private String fCppTypeName;
		private String fJavaTypeName;

		Type(String cppTypeName, String javaTypeName) {

			fCppTypeName = cppTypeName;
			fJavaTypeName = javaTypeName;
		}

		String getCppTypeName() {
			return fCppTypeName;
		}

		String getJavaTypeName() {
			return fJavaTypeName;
		}

	}

	public static boolean isAllowedType(String cppTypeName) {


		for (Type type : CPP_TYPES)  {

			String cppTypeName2 = type.getCppTypeName();

			if (cppTypeName.equals(cppTypeName2)) {
				return true;
			}
		}

		return false;
	}

	public static String convertCppTypeToJavaType(String cppTypeName) {


		for (Type type : CPP_TYPES)  {

			String cppTypeName2 = type.getCppTypeName();

			if (cppTypeName.equals(cppTypeName2)) {

				String javaTypeName = type.getJavaTypeName();

				return javaTypeName;
			}
		}

		return null;
	}

	public String convertToJavaType(String cppType) {
		return null;
	}
}
