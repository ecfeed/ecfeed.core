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

// TODO SIMPLE-VIEW unit tests

public class SimpleLanguageHelper {

	public static String verifySeparatorsInName(String name) {

		if (name.contains("_")) {
			return "Underline chars are not allowed in name.";
		}

		if (name.startsWith(" ")) {
			return "Name should not begin with space char.";
		}

		return null;
	}

}
