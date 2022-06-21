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

public interface IParameterConversionItem {

	public String getSrcName();
	public String getDstName();
	public boolean isMatch(IParameterConversionItem otherItem);
	public void setSrcName(String srcName);
	public void setDstName(String dstName);
	public int getItemTypeLevel();
	public int getItemLevel();
}
