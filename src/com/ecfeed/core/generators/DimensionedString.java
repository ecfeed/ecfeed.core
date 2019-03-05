/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.generators;


public class DimensionedString extends DimensionedItem<String> {

	public DimensionedString(int dimension, String item) {
		super(dimension, item);
	}
	
	public static int compareForSort(DimensionedString dimItem1, DimensionedString dimItem2) {

		if (dimItem1.getDimension() > dimItem2.getDimension()) {
			return 1;
		}

		if (dimItem1.getDimension() < dimItem2.getDimension()) {
			return -1;
		}

		return dimItem1.fItem.compareTo(dimItem2.fItem);
	}

}
