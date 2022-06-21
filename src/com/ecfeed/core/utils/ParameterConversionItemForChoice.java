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

public class ParameterConversionItemForChoice implements IParameterConversionItem {

	private String fSrcItemName;
	private String fDstItemName;
	private String fConstraintsContainingSrcItem;

	public ParameterConversionItemForChoice(
			String srcItemName, 
			String dstItemName,
			String constraintsContainingSrcItem) {

		fSrcItemName = srcItemName;
		fDstItemName = dstItemName;

		fConstraintsContainingSrcItem = constraintsContainingSrcItem;
	}

	@Override
	public String toString() {

		return "(" + fSrcItemName + ", " + fDstItemName + ")";
	}

	@Override
	public String getSrcName() {
		return fSrcItemName;
	}

	@Override
	public String getDstName() {
		return fDstItemName;
	}

	public String getConstraintsContainingSrcItem() {
		return fConstraintsContainingSrcItem;
	}

	@Override
	public void setSrcName(String srcName) {
		fSrcItemName = srcName;
	}

	@Override
	public void setDstName(String dstName) {
		fDstItemName = dstName;
	}

	@Override
	public boolean isMatch(IParameterConversionItem otherItem) {

		if (!fSrcItemName.equals(otherItem.getSrcName())) {
			return false;
		}

		if (!fDstItemName.equals(otherItem.getDstName())) {
			return false;
		}

		return true;
	}

	@Override
	public int getItemLevel() {

		return StringHelper.countOccurencesOfChar(fSrcItemName, ':');
	}

	@Override
	public int getItemTypeLevel() {
		return 2;
	}

}

