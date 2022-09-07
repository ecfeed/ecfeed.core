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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ParameterConversionItem {

	private IParameterConversionItemPart fSrcPart;
	private IParameterConversionItemPart fDstPart;
	List<String> fDescription;

	public ParameterConversionItem(
			IParameterConversionItemPart srcPart, 
			IParameterConversionItemPart dstPart,
			String objectContainingSrcItem) {

		this(srcPart, dstPart, createListFromString(objectContainingSrcItem));
	}

	public ParameterConversionItem(
			IParameterConversionItemPart srcPart, 
			IParameterConversionItemPart dstPart,
			List<String> objectsContainingSrcItem) {

		if (srcPart == null) {
			ExceptionHelper.reportRuntimeException("Invalid conversion item. Src part should not be empty.");
		}

		fSrcPart = srcPart;
		fDstPart = dstPart;

		fDescription = new ArrayList<>(objectsContainingSrcItem);
	}

	@Override
	public String toString() {

		String dstPartDescription = "EMPTY";

		if (fDstPart != null) {
			dstPartDescription = fDstPart.toString();
		}

		return "(" + fSrcPart.toString() + " -> " + dstPartDescription + ")";
	}

	public IParameterConversionItemPart getSrcPart() {
		return fSrcPart;
	}

	public IParameterConversionItemPart getDstPart() {
		return fDstPart;
	}

	public String getConstraintsContainingSrcItem() {

		String result = "";
		boolean isFirst = true;

		for (String objectContainingSrcItem : fDescription) {

			if (!isFirst) {
				result += ", ";
			}

			result += objectContainingSrcItem;
			isFirst = false;
		}

		return result;
	}

	public boolean isMatch(ParameterConversionItem otherItem) {

		if (!ParameterConversionItemPart.isMatch(fSrcPart, otherItem.fSrcPart)) {
			return false;
		}

		if (!ParameterConversionItemPart.isMatch(fDstPart, otherItem.fDstPart)) {
			return false;
		}

		return true;
	}

	public void mergeDescriptions(ParameterConversionItem otherParameterConversionItem) {

		fDescription.addAll(otherParameterConversionItem.fDescription);
		removeDuplicates();
	}

	public ParameterConversionItem makeClone() {

		IParameterConversionItemPart srcPart = fSrcPart.makeClone();

		IParameterConversionItemPart dstPart = null;

		if (fDstPart != null) {
			dstPart = fDstPart.makeClone();
		}

		ParameterConversionItem clone = new ParameterConversionItem(srcPart, dstPart, fDescription);

		return clone;
	}

	private static List<String> createListFromString(String objectContainingSrcItem) {
		List<String> tmp = new ArrayList<>();
		tmp.add(objectContainingSrcItem);
		return tmp;
	}

	private void removeDuplicates() {
		HashSet<String>set = new HashSet<>(fDescription);
		fDescription = new ArrayList<>(set);
	}

}

