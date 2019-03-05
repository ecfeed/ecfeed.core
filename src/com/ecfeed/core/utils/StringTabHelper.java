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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringTabHelper {

	public static String[] intersect(String[] array1, String[] array2) {

		Set<String> s1 = new HashSet<String>(Arrays.asList(array1));
		Set<String> s2 = new HashSet<String>(Arrays.asList(array2));

		s1.retainAll(s2);

		return s1.toArray(new String[s1.size()]);
	}

	public static boolean isOneOfValues(String valueToTest, String[] array) {

		for (String value : array) {
			if (StringHelper.isEqual(value, valueToTest)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isOneOfValuesIgnoreCase(String valueToTest, String[] array) {

		for (String value : array) {
			if (StringHelper.isEqualIgnoreCase(value, valueToTest)) {
				return true;
			}
		}

		return false;
	}	

	public static String getFirstValue(String[] array) {

		if (array.length < 1) {
			return null;
		}

		return array[0];
	}
}
