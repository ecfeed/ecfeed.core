/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.testutils;

import com.ecfeed.core.utils.JavaLanguageHelper;

public enum ETypeName {
	BOOLEAN(JavaLanguageHelper.TYPE_NAME_BOOLEAN),
	BYTE(JavaLanguageHelper.TYPE_NAME_BYTE),
	CHAR(JavaLanguageHelper.TYPE_NAME_CHAR),
	SHORT(JavaLanguageHelper.TYPE_NAME_SHORT),
	INT(JavaLanguageHelper.TYPE_NAME_INT),
	LONG(JavaLanguageHelper.TYPE_NAME_LONG),
	FLOAT(JavaLanguageHelper.TYPE_NAME_FLOAT),
	DOUBLE(JavaLanguageHelper.TYPE_NAME_DOUBLE),
	STRING(JavaLanguageHelper.TYPE_NAME_STRING),
	USER_TYPE("user.type");

	private String fName;

	private ETypeName(String name){
		fName = name;
	}

	public String getTypeName(){
		return fName;
	}

	public static final String[] SUPPORTED_TYPES = {
		JavaLanguageHelper.TYPE_NAME_BOOLEAN,
		JavaLanguageHelper.TYPE_NAME_BYTE,
		JavaLanguageHelper.TYPE_NAME_CHAR,
		JavaLanguageHelper.TYPE_NAME_DOUBLE,
		JavaLanguageHelper.TYPE_NAME_FLOAT,
		JavaLanguageHelper.TYPE_NAME_INT,
		JavaLanguageHelper.TYPE_NAME_LONG,
		JavaLanguageHelper.TYPE_NAME_SHORT,
		JavaLanguageHelper.TYPE_NAME_STRING,
	};

}
