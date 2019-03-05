/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.List;

public class TestResultDescription {

	private List<String> fTestArguments;
	private boolean fResult;

	public TestResultDescription(List<String> testArguments, boolean result) {
		fTestArguments = testArguments;
		fResult = result;
	}

	List<String> getTestArguments() {
		return fTestArguments;
	}

	boolean getResult() {
		return fResult;
	}

}
