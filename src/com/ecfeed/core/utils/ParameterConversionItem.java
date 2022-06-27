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

	private ParameterConversionItemPart fSrcPart;
	private ParameterConversionItemPart fDstPart;
	String fNodesContainingSrcItem;

	public ParameterConversionItem(
			ParameterConversionItemPart srcPart, 
			ParameterConversionItemPart dstPart,
			String constraintsContainingSrcItem) { // TODO DE-NO rename

		if (srcPart == null) {
			ExceptionHelper.reportRuntimeException("Invalid conversion item. Src part should not be empty.");
		}

		if (dstPart == null) {
			ExceptionHelper.reportRuntimeException("Invalid conversion item. Dst part should not be empty.");
		}

		fSrcPart = srcPart;
		fDstPart = dstPart;

		fNodesContainingSrcItem = constraintsContainingSrcItem;
	}

	@Override
	public String toString() {

		return "(" + fSrcPart.toString() + " -> " + fDstPart.toString() + ")";
	}

	public IParameterConversionItemPart getSrcPart() {
		return fSrcPart;
	}

	public IParameterConversionItemPart getDstPart() {
		return fDstPart;
	}

	public String getConstraintsContainingSrcItem() {
		return fNodesContainingSrcItem;
	}

	public boolean isMatch(ParameterConversionItem otherItem) {

		if (!fSrcPart.isMatch(otherItem.fSrcPart)) {
			return false;
		}

		if (!fDstPart.isMatch(otherItem.fDstPart)) {
			return false;
		}

		return true;
	}

}

