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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import org.junit.Test;

import com.ecfeed.core.generators.algorithms.NWiseAwesomeAlgorithmBase;
import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;

public class NWiseGeneratorTest {

	@Test
	public void initializeNTest() {
		try {
			/*
			 *	Should I change the way algorithms are initialized? I guess So. There is no real situation in which we alter parameters
			 *	once initialized, though. Awaiting approval.
			 */
			NWiseGenerator<String> generator = new NWiseGenerator<String>();
			List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3,
					3);
			List<IGeneratorValue> arguments = new ArrayList<>();

			GeneratorValue generatorArgumentN = new GeneratorValue(generator.getDefinitionN(), "2");
			arguments.add(generatorArgumentN);

			generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
			IAlgorithm<String> algorithm = generator.getAlgorithm();
			assertTrue(algorithm instanceof NWiseAwesomeAlgorithmBase);
			assertEquals(2, ((NWiseAwesomeAlgorithmBase<String>) algorithm).getN());

			try {
				GeneratorValue generatorArgumentN2 = new GeneratorValue(generator.getDefinitionN(), "5");
				arguments.add(generatorArgumentN2);
				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				GeneratorValue generatorArgumentN2 = new GeneratorValue(generator.getDefinitionN(), "-1");
				arguments.add(generatorArgumentN2);
				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				GeneratorValue generatorArgumentN2 = new GeneratorValue(generator.getDefinitionN(), "2");
				arguments.add(generatorArgumentN2);
				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException");
			}

		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}
	
	@Test
	public void initializeCoverageTest() {
		try {

			NWiseGenerator<String> generator = new NWiseGenerator<String>();
			List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3,
					3);
			List<IGeneratorValue> arguments = new ArrayList<>();

			GeneratorValue generatorArgumentN = new GeneratorValue(generator.getDefinitionN(), "2");
			arguments.add(generatorArgumentN);

			GeneratorValue generatorArgumentCoverage = new GeneratorValue(generator.getDefinitionCoverage(), "100");
			arguments.add(generatorArgumentCoverage);

			generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
			IAlgorithm<String> algorithm = generator.getAlgorithm();
			assertTrue(algorithm instanceof NWiseAwesomeAlgorithmBase);
			assertEquals(100,
					((NWiseAwesomeAlgorithmBase<String>) algorithm).getCoverage());

			try {
				GeneratorValue generatorArgumentCoverage2 = new GeneratorValue(generator.getDefinitionCoverage(), "101");
				arguments.add(generatorArgumentCoverage2);
				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				GeneratorValue generatorArgumentCoverage2 = new GeneratorValue(generator.getDefinitionCoverage(), "-1");
				arguments.add(generatorArgumentCoverage2);
				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				GeneratorValue generatorArgumentCoverage2 = new GeneratorValue(generator.getDefinitionCoverage(), "50");
				arguments.add(generatorArgumentCoverage2);
				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException");
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}
}
