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

public class ParameterConversionItem {

	private IParameterConversionItemPart fSrcPart;
	private IParameterConversionItemPart fDstPart;
	String fObjectsContainingSrcItem;

	public ParameterConversionItem(
			IParameterConversionItemPart srcPart, 
			IParameterConversionItemPart dstPart,
			String objectsContainingSrcItem) {

		if (srcPart == null) {
			ExceptionHelper.reportRuntimeException("Invalid conversion item. Src part should not be empty.");
		}

		fSrcPart = srcPart;
		fDstPart = dstPart;

		fObjectsContainingSrcItem = objectsContainingSrcItem;
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
		return fObjectsContainingSrcItem;
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

	public ParameterConversionItem makeClone() {

		IParameterConversionItemPart srcPart = fSrcPart.makeClone();

		IParameterConversionItemPart dstPart = null;

		if (fDstPart != null) {
			dstPart = fDstPart.makeClone();
		}

		ParameterConversionItem clone = new ParameterConversionItem(srcPart, dstPart, fObjectsContainingSrcItem);

		return clone;
	}

}

