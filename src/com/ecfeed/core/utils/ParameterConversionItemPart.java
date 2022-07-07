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

public abstract class ParameterConversionItemPart implements IParameterConversionItemPart {

	private String fStr;

	public abstract Integer getTypeSortOrder();

	public ParameterConversionItemPart(String str) {

		if (str == null) {
			ExceptionHelper.reportRuntimeException("Invalid conversion item. Src name should not be empty.");
		}

		fStr = str;
	}

	@Override
	public String toString() {

		return fStr;
	}

	@Override
	public String getStr() {
		return fStr;
	}

	@Override
	public void setName(String name) {
		fStr = name;
	}

	@Override
	public boolean isMatch(IParameterConversionItemPart otherPart) {

		if (StringHelper.isEqual(fStr, otherPart.getStr())) {
			return true;
		}

		return false;
	}

	@Override
	public Integer getSortOrder() {

		return StringHelper.countOccurencesOfChar(fStr, ':');
	}

	@Override
	public int compareTo(IParameterConversionItemPart other) {

		return getStr().compareTo(other.getStr());

	}

	public String getDescription(String code) {
		
		String typeDescription = ItemPartType.convertCodeToDescription(code);
		return getStr() + "[" + typeDescription +"]";
	}
}

