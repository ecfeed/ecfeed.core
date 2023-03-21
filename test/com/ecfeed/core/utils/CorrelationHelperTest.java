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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CorrelationHelperTest{

	@Test
	public void checkCorrelationsForStrings(){

		testCorrelations("ABC", "ABc", "ABe");
		testCorrelations("ABCD", "ABB", "ABX");
		testCorrelations("ABCD", "ABCD", "AbCD");
	}

	private void testCorrelations(String mainPattern, String moreSimilarPattern, String lessSimilarPattern) {

		double corr1 = CorrelationHelper.calculateCorrelation(mainPattern, moreSimilarPattern);

		double corr2 = CorrelationHelper.calculateCorrelation(mainPattern, lessSimilarPattern);

		assertTrue(corr1 > corr2);
	}

}
