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

public class StringHolder extends ItemHolder<String> {

	public StringHolder(String initialValue) {
		super(initialValue);
	}	

	public StringHolder() {
		super();
	}

	public void append(String str) {
		String oldValue = get();
		set(oldValue + str);
	}

}
