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


public class DimensionedItem<E> {
	
	protected int fDimension; // e.g. index of method parameter
	protected E fItem;

	public DimensionedItem(int dimension, E item) {
		fDimension = dimension;
		fItem = item;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof DimensionedItem))
			return false;

		DimensionedItem<?> var = (DimensionedItem<?>) obj;

		if (var.fDimension == this.fDimension && this.fItem.equals(var.fItem)) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("dim:");
		sb.append(fDimension);
		sb.append(", item:");
		sb.append(fItem);
		//sb.append("]");

		return sb.toString();
	}
	
	public int getDimension() {
		return fDimension;
	}
	
	public E getItem() {
		return fItem;
	}

}
