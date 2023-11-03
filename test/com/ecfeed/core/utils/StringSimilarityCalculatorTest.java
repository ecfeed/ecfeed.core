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

import com.ecfeed.core.model.BasicParameterNode;

public class StringSimilarityCalculatorTest {

	@Test
	public void checkSimilarityOfStrings1() {

		BasicParameterNode basicParameterNode = new BasicParameterNode("p1", "String", "", true, null);

		double result1 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "AB"), 
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "A"));

		double result2 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "A"), 
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "AB"));

		assertTrue(result1 == result2);
	}

	@Test
	public void checkSimilarityOfStrings2() {

		BasicParameterNode basicParameterNode = new BasicParameterNode("p1", "String", "", true, null);

		double result1 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "MC1"), 
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "RC1"));

		double result2 =
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "MC1"), 
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "RC2"));

		assertTrue(result1 > result2);
	}

	@Test
	public void checkSimilarityOfStrings3() {

		BasicParameterNode basicParameterNode = new BasicParameterNode("p1", "String", "", true, null);

		double result1 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "MC1"), 
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "MC2"));

		double result2 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "MC1"),
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "MCA"));

		assertTrue(result1 > result2);
	}	

	@Test
	public void checkSimilarityOfStrings4() {

		BasicParameterNode basicParameterNode = new BasicParameterNode("p1", "String", "", true, null);

		double result1 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "ABCD"), 
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "ABD"));

		double result2 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "ABCD"), 
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "AB"));

		assertTrue(result1 > result2);

		double result3 = 
				StringSimilarityCalculator.calculateSimilarityIndex(
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "ABCD"),
						new ParameterConversionItemPartForRaw(basicParameterNode, null, "C", "B"));

		assertTrue(result2 > result3);
	}

}
