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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ecfeed.core.evaluator.HomebrewConstraintEvaluator;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import org.junit.Test;

import com.ecfeed.core.generators.algorithms.FastNWiseAlgorithm;
import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.algorithms.OptimalNWiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;
import com.ecfeed.core.model.IConstraint;

public class FastNWiseAlgorithmTest extends NWiseAlgorithmTest {

	@Test
	public void testCorrectness() {
		testCorrectness(FastNWiseAlgorithm.class);
	}

	@Test
	public void testConstraints() {
		testCorrectness(FastNWiseAlgorithm.class);
	}
	

	@Test
	public void testSpeed(){
		for(int variables : new int[]{1, 2, 5}){
		for(int choices : new int[]{1, 2, 5}){
			List<List<String>> input = GeneratorTestUtils.prepareInput(variables, choices);
			for(int n = 1; n <= variables; n++){
				testSpeed(input, n);
			}
		}
		}
	}

	private void testSpeed(List<List<String>> input, int n) {
		try{
			IAlgorithm<String> fastAlgorithm = new FastNWiseAlgorithm<String>(n, 100);
			IAlgorithm<String> referenceAlgorithm = new OptimalNWiseAlgorithm<String>(n, 100);
			Collection<IConstraint<String>> constraints = GeneratorTestUtils.generateRandomConstraints(input);
			fastAlgorithm.initialize(input, new HomebrewConstraintEvaluator<>(constraints), new SimpleProgressMonitor());
			referenceAlgorithm.initialize(input, new HomebrewConstraintEvaluator<>(constraints), new SimpleProgressMonitor());

			long timestampStart = new Date().getTime();
			GeneratorTestUtils.algorithmResult(fastAlgorithm);
			long timestampEnd = new Date().getTime();
			long duration = timestampEnd - timestampStart;

			timestampStart = new Date().getTime();
			GeneratorTestUtils.algorithmResult(referenceAlgorithm);
			timestampEnd = new Date().getTime();
			long referenceDuration = timestampEnd - timestampStart;

			assertTrue(duration <= referenceDuration + 100 /*initialization margin for small generated sets*/);
		}catch(GeneratorException e){
			fail("Unexpected generator exception: " + e.getMessage());
		}
	}

}
