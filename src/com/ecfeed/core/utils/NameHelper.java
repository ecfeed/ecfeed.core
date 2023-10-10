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

public class NameHelper {

	public static void compareNames(String name, String name2) {
		if(name.equals(name2) == false){
			ExceptionHelper.reportRuntimeException("Different names: " + name + ", " + name2);
		}
	}

}
