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

public class StringSimilarityCalculatorTest {

	@Test
	public void checkSimilarityOfStrings1() {

		double result1 = StringSimilarityCalculator.calculateSimilarityIndex("AB", "A");
		double result2 = StringSimilarityCalculator.calculateSimilarityIndex("A", "AB");
		assertTrue(result1 == result2);
	}

	@Test
	public void checkSimilarityOfStrings2() {

		double result1 = StringSimilarityCalculator.calculateSimilarityIndex("MC1", "RC1");
		double result2 = StringSimilarityCalculator.calculateSimilarityIndex("MC1", "RC2");

		assertTrue(result1 > result2);
	}

	@Test
	public void checkSimilarityOfStrings3() {

		double result1 = StringSimilarityCalculator.calculateSimilarityIndex("MC1", "MC2");
		double result2 = StringSimilarityCalculator.calculateSimilarityIndex("MC1", "MCA");

		assertTrue(result1 > result2);
	}	

	@Test
	public void checkSimilarityOfStrings4() {

		double result1 = StringSimilarityCalculator.calculateSimilarityIndex("ABCD", "ABD");
		double result2 = StringSimilarityCalculator.calculateSimilarityIndex("ABCD", "AB");
		assertTrue(result1 > result2);

		double result3 = StringSimilarityCalculator.calculateSimilarityIndex("ABCD", "B");
		assertTrue(result2 > result3);
	}

}
