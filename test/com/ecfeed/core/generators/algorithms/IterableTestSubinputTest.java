/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.generators.api.GeneratorException;

public class IterableTestSubinputTest {

	@Test
	public void testWithOneElement() {

		List<Integer> paramDimensions = new ArrayList<Integer>();
		paramDimensions.add(0);

		List<Integer> paramValues = new ArrayList<Integer>();
		paramValues.add(5);
		List<List<Integer>> testInput = new ArrayList<List<Integer>>();
		testInput.add(paramValues);

		IterableTestSubinput<Integer> testInputList = null;

		try {
			testInputList = new IterableTestSubinput<Integer>(testInput, paramDimensions);
		} catch (GeneratorException e) {
			fail();
		}

		int loopCount = 0;

		for (List<Integer> values : testInputList) {

			assertEquals(1, values.size());

			int val = values.get(0);
			assertEquals(5, val);

			loopCount++;
		}

		assertEquals(1, loopCount);
	}

	@Test
	public void testWithTwoElementsInOneDimension() {

		List<Integer> paramDimensions = new ArrayList<Integer>();
		paramDimensions.add(0);

		List<Integer> paramValues = new ArrayList<Integer>();
		paramValues.add(5);
		paramValues.add(7);
		List<List<Integer>> testInput = new ArrayList<List<Integer>>();
		testInput.add(paramValues);

		IterableTestSubinput<Integer> testInputList = null;

		try {
			testInputList = new IterableTestSubinput<Integer>(testInput, paramDimensions);
		} catch (GeneratorException e) {
			fail();
		}

		int loopCount = 0;

		for (List<Integer> test : testInputList) {

			assertEquals(1, test.size());

			if (loopCount == 0) {
				int val0 = test.get(0);
				assertEquals(5, val0);
			}

			if (loopCount == 1) {
				int val1 = test.get(0);
				assertEquals(7, val1);
			}

			loopCount++;
		}

		assertEquals(2, loopCount);
	}

	@Test
	public void testWithTwoElementsInTwoDimensions() {

		List<Integer> paramDimensions = new ArrayList<Integer>();
		paramDimensions.add(0);
		paramDimensions.add(1);

		List<List<String>> testInput = new ArrayList<List<String>>();

		List<String> paramValues0 = new ArrayList<String>();
		paramValues0.add("0");
		paramValues0.add("1");
		testInput.add(paramValues0);

		List<String> paramValues1 = new ArrayList<String>();
		paramValues1.add("0");
		paramValues1.add("1");
		testInput.add(paramValues1);

		IterableTestSubinput<String> testInputList = null;
		try {
			testInputList = new IterableTestSubinput<String>(testInput, paramDimensions);
		} catch (GeneratorException e) {
			fail();
		}

		int loopCount = 0;

		for (List<String> test : testInputList) {

			assertEquals(2, test.size());

			String str = test.get(1) + test.get(0);

			checkResult(0, loopCount, "00", str);
			checkResult(1, loopCount, "01", str);
			checkResult(2, loopCount, "10", str);
			checkResult(3, loopCount, "11", str);

			loopCount++;
		}

		assertEquals(4, loopCount);
	}

	@Test
	public void testWithManyElementsInManyDimensions() {

		List<Integer> paramDimensions = new ArrayList<Integer>();
		paramDimensions.add(1);
		paramDimensions.add(3);

		List<List<String>> testInput = new ArrayList<List<String>>();

		List<String> paramValues0 = new ArrayList<String>();
		testInput.add(paramValues0);

		List<String> paramValues1 = new ArrayList<String>();
		paramValues1.add("0");
		paramValues1.add("1");
		testInput.add(paramValues1);

		List<String> paramValues2 = new ArrayList<String>();
		testInput.add(paramValues2);

		List<String> paramValues3 = new ArrayList<String>();
		paramValues3.add("A");
		paramValues3.add("B");
		paramValues3.add("C");
		testInput.add(paramValues3);		

		List<String> paramValues4 = new ArrayList<String>();
		testInput.add(paramValues4);		


		IterableTestSubinput<String> testInputList = null;

		try {
			testInputList = new IterableTestSubinput<String>(testInput, paramDimensions);
		} catch (GeneratorException e) {
			fail();
		}

		int loopCount = 0;

		for (List<String> test : testInputList) {

			assertEquals(2, test.size());

			String str = test.get(1) + test.get(0);

			checkResult(0, loopCount, "A0", str);
			checkResult(1, loopCount, "A1", str);
			checkResult(2, loopCount, "B0", str);
			checkResult(3, loopCount, "B1", str);
			checkResult(4, loopCount, "C0", str);
			checkResult(5, loopCount, "C1", str);			

			loopCount++;
		}

		assertEquals(6, loopCount);
	}


	private void checkResult(int expectedLoopCount, int realLoopCount, String expectedValue, String realValue) {

		if (realLoopCount != expectedLoopCount) {
			return;
		}

		assertEquals(expectedValue, realValue);
	}

}
