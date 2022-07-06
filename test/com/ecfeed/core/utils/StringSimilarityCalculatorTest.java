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

		double result1 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "AB"), 
						new ParameterConversionItemPartForRaw("C", "A"));
		
		double result2 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "A"), 
						new ParameterConversionItemPartForRaw("C", "AB"));
		
		assertTrue(result1 == result2);
	}

	@Test
	public void checkSimilarityOfStrings2() {

		double result1 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "MC1"), 
						new ParameterConversionItemPartForRaw("C", "RC1"));
		
		double result2 =
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "MC1"), 
						new ParameterConversionItemPartForRaw("C", "RC2"));

		assertTrue(result1 > result2);
	}

	@Test
	public void checkSimilarityOfStrings3() {

		double result1 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "MC1"), 
						new ParameterConversionItemPartForRaw("C", "MC2"));
		
		double result2 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "MC1"),
						new ParameterConversionItemPartForRaw("C", "MCA"));

		assertTrue(result1 > result2);
	}	

	@Test
	public void checkSimilarityOfStrings4() {

		double result1 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "ABCD"), 
						new ParameterConversionItemPartForRaw("C", "ABD"));
		
		double result2 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "ABCD"), 
						new ParameterConversionItemPartForRaw("C", "AB"));
		
		assertTrue(result1 > result2);

		double result3 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw("C", "ABCD"),
						new ParameterConversionItemPartForRaw("C", "B"));
		
		assertTrue(result2 > result3);
	}

}
