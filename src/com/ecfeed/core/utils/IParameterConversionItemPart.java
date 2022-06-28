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

public interface IParameterConversionItemPart  {

	enum ItemPartType {

		CHOICE,
		LABEL,
		NAME;
	}

	public String getName();
	public void setName(String name);
	public boolean isMatch(IParameterConversionItemPart otherPart);
	public int getTypeSortOrder();
	public int getSortOrder();
	public ItemPartType getType();
}

