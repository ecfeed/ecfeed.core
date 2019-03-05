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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestResultDescriptionTest {


	@Test
	public void shouldReturnTestResult() {

		List<String> testcases = new ArrayList<String>();
		boolean result = false;

		TestResultDescription testresult = new TestResultDescription(testcases, result);

		assertEquals(testcases, testresult.getTestArguments());
		assertEquals(result, testresult.getResult());
	}
}
