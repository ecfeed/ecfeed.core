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

public class SimpleLanguageHelper {

	public static void verifySeparatorsInName(String name) {

		if (name.contains("_")) {
			ExceptionHelper.reportRuntimeException("Underline chars are not allowed in name.");
		}

		if (name.startsWith(" ")) {
			ExceptionHelper.reportRuntimeException("Name should not begin with space char.");
		}
	}

}
