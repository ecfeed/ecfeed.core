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

import java.util.Collections;
import java.util.List;

public class ShifterOfListElements {

	public static void shiftElements(List<?> list, List<Integer> indices, int shift) {

		Collections.sort(indices);

		if (shift > 0) {
			Collections.reverse(indices);
		}

		for (int i = 0; i < indices.size(); i++) {
			shiftOneElement(list, indices.get(i), shift);
		}
	}

	public static void shiftOneElement(List<?> list, int index, int shift) {

		int minIndex = Math.min(index, index+shift);
		int maxIndex = Math.max(index, index+shift) + ((shift < 0) ? 1 : 0);

		List<?> rotated = list.subList(minIndex, (shift > 0) ? maxIndex + 1 : maxIndex);

		int rotation = (shift > 0) ? -1 : 1;

		Collections.rotate(rotated, rotation);
	}

}
