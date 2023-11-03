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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;

public class ParameterConversionItemPartForRaw extends ParameterConversionItemPart {

	String fCode;

	public ParameterConversionItemPartForRaw(
			BasicParameterNode basicParameterNode, 
			CompositeParameterNode linkingContext,
			String code, 
			String name) {

		super(basicParameterNode, linkingContext, name);

		fCode = code;
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
				new ParameterConversionItemPartForRaw(getParameter(), getLinkingContext(), getCode(), getStr());

		return clone;
	}

	@Override
	public String getTypeDescription() {

		String rawTypeDescription = 
				IParameterConversionItemPart.ItemPartType.convertCodeToDescription(getCode());

		return rawTypeDescription;
	}

	//	public String createDescription() {
	//
	//		String rawTypeDescription = 
	//				IParameterConversionItemPart.ItemPartType.convertCodeToDescription(getCode());
	//		return 
	//				getParameter().getName() + 
	//				SignatureHelper.SIGNATURE_NAME_SEPARATOR + 
	//				getStr() + 
	//				"[" + rawTypeDescription + "]";
	//	}

}

