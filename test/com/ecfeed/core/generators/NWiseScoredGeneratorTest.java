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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.algorithms.NwiseShScoreEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.utils.SimpleProgressMonitor;

public class NWiseScoredGeneratorTest {

	@Test
	public void basicTest() {

		try {
			decreaseScoreTest();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private static void decreaseScoreTest() throws Exception {

		List<String> dim1 = new ArrayList<String>();
		dim1.add("V11");
		dim1.add("V12");
		dim1.add("V13");

		List<String> dim2 = new ArrayList<String>();
		dim2.add("V21");
		dim2.add("V22");
		dim2.add("V23");

		List<String> dim3 = new ArrayList<String>();
		dim3.add("V31");
		dim3.add("V32");
		dim3.add("V33");

		List<String> dim4 = new ArrayList<String>();
		dim4.add("V41");
		dim4.add("V42");
		dim4.add("V43");

		List<String> dim5 = new ArrayList<String>();
		dim5.add("V51");
		dim5.add("V52");
		dim5.add("V53");

		List<List<String>> testInput = new ArrayList<>();

		testInput.add(dim1);
		testInput.add(dim2);
		testInput.add(dim3);
		testInput.add(dim4);
		testInput.add(dim5);

		NwiseShScoreEvaluator<String> evaluator = new NwiseShScoreEvaluator<String>(2);
		evaluator.initialize(testInput, null);

		NWiseScoredGenerator<String> generator = new NWiseScoredGenerator<>();

		List<IGeneratorValue> generatorParameters = new ArrayList<>();
		generator.initialize(testInput, new DummyEvaluator<>(), generatorParameters, new SimpleProgressMonitor());

		int maxCounter = 20;

		for (int counter = 0; counter < 20; counter++) {

			List<String> testCase = generator.next();

			if (testCase == null) {
				return;
			}

			if (counter >= maxCounter) {
				fail("Too many generated test cases");
			}
		}
	}

}
