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
import com.ecfeed.core.utils.IExtLanguageManager;

public interface ITypeAdapter<T> {

	public boolean isRandomizable();
	public boolean isCompatible(String type); // TODO DE-NO remove ?
	public boolean isConvertibleTo(String otherType);
	public String adapt(String value, boolean isRandomized, ERunMode conversionMode, IExtLanguageManager extLanguageManager);
	public String getDefaultValue();
	public boolean isNullAllowed();
	public T generateValue(String range, String context);
	public String generateValueAsString(String range, String context);
	public String getMyTypeName();

}
