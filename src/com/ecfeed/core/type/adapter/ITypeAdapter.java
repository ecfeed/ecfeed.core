/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.type.adapter;

import com.ecfeed.core.utils.ERunMode;

public interface ITypeAdapter<T> {

	public boolean isRandomizable();
	public boolean isCompatible(String type);
	public String convert(String value, boolean isRandomized, ERunMode conversionMode);
	public String getDefaultValue();
	public boolean isNullAllowed();
	public T generateValue(String range);
	public String generateValueAsString(String range);
	public String getMyTypeName();

}
