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

	public static final String SIGNATURE_SEPARATOR = " : ";

	public static String joinElementsWithSeparator(String str1, String str2) {
		
		return str1 + SIGNATURE_SEPARATOR + str2;
		
	}
	
}
