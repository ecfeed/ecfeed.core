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
import java.util.List;

public class CollectionHelper {

	public static boolean isTheSameContent(List<String> list1,  String[] array) {

		List<String> list2 = Arrays.asList(array);

		if (list1.equals(list2)) {
			return true;
		}

		return false;
	}
}
