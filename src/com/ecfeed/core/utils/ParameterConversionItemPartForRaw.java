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

import com.ecfeed.core.model.AbstractParameterNode;

public class ParameterConversionItemPartForRaw extends ParameterConversionItemPart {

	String fCode;

	public ParameterConversionItemPartForRaw(
			AbstractParameterNode abstractParameterNode, String code, String name) {
		super(abstractParameterNode, name);

		fCode = code;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	@Override
	public String getDescription() {

		return super.getDescription(fCode);
	}

	@Override
	public ItemPartType getType() {
		return IParameterConversionItemPart.ItemPartType.RAW;
	}

	@Override
	public Integer getTypeSortOrder() {
		return 0;
	}

	@Override
	public int compareTo(IParameterConversionItemPart other) {

		int resultOfComparingTypes = getTypeSortOrder().compareTo(other.getSortOrder());

		if (resultOfComparingTypes != 0) {
			return resultOfComparingTypes;
		}

		return super.compareTo(other);
	}

	public String getCode() {

		return fCode;
	}

	public void setCode(String code) {

		fCode = code;
	}

	@Override
	public IParameterConversionItemPart makeClone() {

		ParameterConversionItemPartForRaw clone = 
				new ParameterConversionItemPartForRaw(getParameter(), getCode(), getStr());

		return clone;
	}

}

