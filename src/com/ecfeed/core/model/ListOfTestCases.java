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

import java.util.ArrayList;
import java.util.List;

public class ListOfTestCases {

	private List<TestCase> fTestCases;

	public ListOfTestCases() {
		
		fTestCases = new ArrayList<>();
	}

	public void add(TestCase testCase) {
		
		fTestCases.add(testCase);
	}

	public int getSize() {
		
		return fTestCases.size();
	}
	
	public List<TestCase> getList() {
		
		return fTestCases;
	}
	
}
