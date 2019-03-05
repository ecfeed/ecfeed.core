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

import com.ecfeed.core.utils.JavaTypeHelper;

public enum ETypeName {
	BOOLEAN(JavaTypeHelper.TYPE_NAME_BOOLEAN), 
	BYTE(JavaTypeHelper.TYPE_NAME_BYTE), 
	CHAR(JavaTypeHelper.TYPE_NAME_CHAR), 
	SHORT(JavaTypeHelper.TYPE_NAME_SHORT), 
	INT(JavaTypeHelper.TYPE_NAME_INT), 
	LONG(JavaTypeHelper.TYPE_NAME_LONG), 
	FLOAT(JavaTypeHelper.TYPE_NAME_FLOAT), 
	DOUBLE(JavaTypeHelper.TYPE_NAME_DOUBLE), 
	STRING(JavaTypeHelper.TYPE_NAME_STRING), 
	USER_TYPE("user.type");

	private String fName;

	private ETypeName(String name){
		fName = name;
	}

	public String getTypeName(){
		return fName;
	}

	public static final String[] SUPPORTED_TYPES = {
		JavaTypeHelper.TYPE_NAME_BOOLEAN,
		JavaTypeHelper.TYPE_NAME_BYTE,
		JavaTypeHelper.TYPE_NAME_CHAR,
		JavaTypeHelper.TYPE_NAME_DOUBLE,
		JavaTypeHelper.TYPE_NAME_FLOAT,
		JavaTypeHelper.TYPE_NAME_INT,
		JavaTypeHelper.TYPE_NAME_LONG,
		JavaTypeHelper.TYPE_NAME_SHORT,
		JavaTypeHelper.TYPE_NAME_STRING,
	};

}
