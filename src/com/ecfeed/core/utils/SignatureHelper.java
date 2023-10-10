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

public class SignatureHelper {

	public enum SignatureType {
		WITH_TYPE,
		WITHOUT_TYPE
	}

	public static final String SIGNATURE_NAME_SEPARATOR = ":";  // separates sections of composite 
	public static final String SIGNATURE_TYPE_SEPARATOR = " : ";  // name from type
	public static final String SIGNATURE_CONTENT_SEPARATOR = " : "; // name from content
	public static final String SIGNATURE_ROOT_MARKER = "@";  // informs that the first item is root node

	public static String getLastSegment(String name) {

		int index = name.lastIndexOf(SignatureHelper.SIGNATURE_NAME_SEPARATOR);

		if (index == -1) {
			return name;
		}

		String lastSegment = name.substring(index+1);
		return lastSegment;
	}

}
