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
import com.ecfeed.core.model.CompositeParameterNode;

public class ParameterConversionItemPartForValue extends ParameterConversionItemPart {

	public ParameterConversionItemPartForValue(
			AbstractParameterNode abstractParameterNode, 
			CompositeParameterNode linkingContext,
			String value) {
		super(abstractParameterNode, linkingContext, value);
	}

	@Override
	public ItemPartType getType() {
		return IParameterConversionItemPart.ItemPartType.VALUE;
	}

	@Override
	public Integer getTypeSortOrder() {
		return 1;
	}

	//	@Override
	//	public String getDescription() {
	//		return super.getDescription(ItemPartType.VALUE.getCode());
	//	}

	public String getValue() {
		return super.getStr();
	}

	@Override
	public IParameterConversionItemPart makeClone() {

		ParameterConversionItemPartForValue clone = 
				new ParameterConversionItemPartForValue(getParameter(), getLinkingContext(), getValue());

		return clone;
	}

}

