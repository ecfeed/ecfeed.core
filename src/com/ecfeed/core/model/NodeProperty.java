/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import com.ecfeed.core.utils.StringHelper;


public class NodeProperty{
	private String fType;
	private String fValue;

	public NodeProperty(String type, String value) {
		fType = type;
		fValue = value;
	}

	public String getType() {
		return fType;
	}

	public String getValue() {
		return fValue;
	}

	public boolean isMatch(NodeProperty other) {
		if (!fType.equals(other.fType)) {
			return false;
		}
		if (!StringHelper.isEqual(fValue, other.fValue)) {
			return false;
		}
		return true;
	}
}
