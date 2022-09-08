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

public class ParameterConversionItemPartForLabel extends ParameterConversionItemPart {

	public ParameterConversionItemPartForLabel(String label) {
		super(label);
	}

	@Override
	public ItemPartType getType() {
		return IParameterConversionItemPart.ItemPartType.LABEL;
	}

	@Override
	public Integer getTypeSortOrder() {
		return 1;
	}

	@Override
	public String getDescription() {
		return super.getDescription(ItemPartType.LABEL.getCode());
	}

	public String getLabel() {
		return super.getStr();
	}

	@Override
	public IParameterConversionItemPart makeClone() {

		ParameterConversionItemPartForLabel clone = 
				new ParameterConversionItemPartForLabel(getLabel());

		return clone;
	}


}

