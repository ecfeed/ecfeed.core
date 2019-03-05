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

public class Trio<A, B, C> {

	private A fA;
	private B fB;
	private C fC;

	public Trio(A a, B b, C c) {
		fA = a;
		fB = b;
		fC = c;
	}

	public A getFirst() {
		return fA;
	}

	public B getSecond() {
		return fB;
	}	

	public C getThird() {
		return fC;
	}	

}
