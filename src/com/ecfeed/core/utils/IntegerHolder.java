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

public class IntegerHolder extends ItemHolder<Integer> {

	public IntegerHolder(Integer initialValue) {
		super(initialValue);
	}
	
	public void increment() {
		set(get()+1);
	}

}
