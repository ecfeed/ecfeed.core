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

public final class CShartLanguageHelper {

	private static Type[] CPP_TYPES = {

			
			new Type("sbyte", "short"),
			new Type("byte", "short"),
			new Type("short", "short"),
			new Type("ushort int","short"),
			
			new Type("int", "int"),
			new Type("uint", "int"),
			
			new Type("long", "long"),
			new Type("ulong", "long"),
			
			new Type("float", "float"),
			new Type("double", "double"),
			
			new Type("decimal", "float"),

			new Type("bool", "boolean"),

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
