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

public class Pair<A, B> {

	private A fA;
	private B fB;

	public Pair(A a, B b) {
		fA = a;
		fB = b;
	}

	public A getFirst() {
		return fA;
	}

	public B getSecond() {
		return fB;
	}	

}
