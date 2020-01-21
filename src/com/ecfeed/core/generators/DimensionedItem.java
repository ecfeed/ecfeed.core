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


public class DimensionedItem<E> implements Comparable<DimensionedItem<E>>{
	
	protected int fDimension; // e.g. index of method parameter
	protected E fItem;

	public DimensionedItem(int dimension, E item) {
		fDimension = dimension;
		fItem = item;
	}

	@Override
	public int compareTo(DimensionedItem<E> other)
	{
		return Integer.compare(this.fDimension, other.fDimension);
	}

	@Override
	public boolean equals(Object other) {

		if (!(other instanceof DimensionedItem))
			return false;

		DimensionedItem<?> otherItem = (DimensionedItem<?>) other;

		if (otherItem.fDimension == this.fDimension && this.fItem.equals(otherItem.fItem)) {
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

		return sb.toString();
	}
	
	public int getDimension() {
		return fDimension;
	}
	
	public E getItem() {
		return fItem;
	}

	public int hashCode()
	{
		if(fItem == null)
			return (17*fDimension + fDimension*fDimension); // TODO RVW - magic number, why such way

		return (17*fDimension) ^ fItem.hashCode();
	}

}
