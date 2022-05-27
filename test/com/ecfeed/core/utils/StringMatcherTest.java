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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringMatcherTest{

	@Test
	public void checkCorrelationsForStrings() {
		
		List<String> strings1 = new ArrayList<>();
		strings1.add("RC1");
		strings1.add("RC2");
		
		List<String> strings2 = new ArrayList<>();
		strings2.add("MC1");
		strings2.add("MC2");
		
		StringMatcher.createListOfSimilarStringPairs(strings1, strings2, 0.1);
	}

}
