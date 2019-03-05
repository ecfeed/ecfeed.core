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


public class ObjectHelper {

	public static String convertToExtendedString(Object obj) {

		if (obj == null) {
			return "NULL";
		}

		return obj.toString();
	}

	public static boolean isEqual(Object object1, Object object2) {

		if (object1 == null || object2 == null) {
			return isEqualWhenOneOrTwoNulls(object1, object2);
		}

		if (object1.equals(object2)) {
			return true;
		}

		return false;
	}

	public static boolean isEqualWhenOneOrTwoNulls(Object object1, Object object2) {

		if (object1 != null && object2 != null) {
			ExceptionHelper.reportRuntimeException("Invalid use of isMatchWithNulls function");
		}

		if (object1 == null && object2 == null) {
			return true;
		}

		return false;
	}

}
