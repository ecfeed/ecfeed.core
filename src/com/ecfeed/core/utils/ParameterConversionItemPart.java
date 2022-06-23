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

	private String fItemName;

	public abstract int getItemTypeLevel();

	public ParameterConversionItemPart(String itemName) {

		if (itemName == null) {
			ExceptionHelper.reportRuntimeException("Invalid conversion item. Src name should not be empty.");
		}

		fItemName = itemName;
	}

	@Override
	public String toString() {

		return fItemName;
	}

	@Override
	public String getName() {
		return fItemName;
	}

	public int getItemLevel() {

		return StringHelper.countOccurencesOfChar(fItemName, ':');
	}

}

