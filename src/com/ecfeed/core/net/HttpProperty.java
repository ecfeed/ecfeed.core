/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.net;

import com.ecfeed.core.utils.Pair;


public class HttpProperty {
	Pair<String, String> fPair;

	public HttpProperty(String key, String value) {
		fPair = new Pair<String, String>(key, value);
	}

	public String getKey() {
		return fPair.getFirst();
	}

	public String getValue() {
		return fPair.getSecond();
	}		
}



