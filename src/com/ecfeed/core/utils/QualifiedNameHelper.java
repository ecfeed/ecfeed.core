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

public class QualifiedNameHelper {


	public static final String PACKAGE_CLASS_SEPARATOR = ".";

	public static String getPackage(String packageWithClass) {
		return StringHelper.getAllBeforeLastToken(packageWithClass, PACKAGE_CLASS_SEPARATOR);		
	}

	public static String getNonQualifiedName(String qualifiedName) { // TODO SIMPLE-VIEW test
		return StringHelper.getLastToken(qualifiedName, PACKAGE_CLASS_SEPARATOR);
	}

	public static String removeDefaultPackagePrefix(String packageWithClass) {
		return StringHelper.removeToPrefix(".", packageWithClass);
	}

	public static boolean hasPackageName(String packageWithClass) {
		String trimmedPackageWithClass = packageWithClass.trim();

		if (trimmedPackageWithClass.startsWith(".") || !trimmedPackageWithClass.contains(".")) {
			return false;
		}
		return true;
	}	

}
