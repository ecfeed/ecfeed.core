/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import com.ecfeed.core.model.BasicParameterNode;

public class BasicParameterWithString {

	private BasicParameterNode fBasicParameterNode;
	private String fString;

	@Override
	public String toString() {
		return fBasicParameterNode.getName() + "(" + fString + ")";
	}
	
	public BasicParameterWithString(BasicParameterNode basicParameterNode, String str) {

		fBasicParameterNode = basicParameterNode;
		fString = str;
	}

	public BasicParameterNode getBasicParameterNode() {
		return fBasicParameterNode;
	}

	public String getStr() {
		return fString;
	}

}
