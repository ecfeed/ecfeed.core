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

public class ChoiceConversionItem {

	private String fChoiceSrcName;
	private String fChoiceDstName;

	public ChoiceConversionItem(String choiceSrcName, String choiceDstName) {

		fChoiceSrcName = choiceSrcName;
		fChoiceDstName = choiceDstName;
	}

	public String getSrcName() {
		return fChoiceSrcName;
	}

	public String getDstName() {
		return fChoiceDstName;
	}

}