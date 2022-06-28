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

public class ParameterConversionItemPartForName extends ParameterConversionItemPart {

	public ParameterConversionItemPartForName(String name) {
		super(name);
	}

	@Override
	public ItemPartType getType() {
		return IParameterConversionItemPart.ItemPartType.NAME;
	}
	
	@Override
	public int getTypeSortOrder() {
		return 2;
	}

	@Override
	public boolean isMatch(IParameterConversionItemPart otherPart) {
		
		if (!(otherPart instanceof ParameterConversionItemPartForName)) {
			return false;
		}
		
		String name = getName();
		String otherName = ((ParameterConversionItemPartForName)otherPart).getName();
		
		if (StringHelper.isEqual(name, otherName)) {
			return true;
		}
		
		return false;

	}
	
}

