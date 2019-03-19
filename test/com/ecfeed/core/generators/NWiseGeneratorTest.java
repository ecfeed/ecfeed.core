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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.generators.api.IGeneratorArgument;
import org.junit.Test;

import com.ecfeed.core.generators.NWiseGenerator;
import com.ecfeed.core.generators.algorithms.AbstractNWiseAlgorithm;
import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;
import com.ecfeed.core.model.IConstraint;

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
			Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();
			Map<String, IGeneratorArgument> arguments = new HashMap<>();

			GeneratorArgumentN generatorArgumentN = new GeneratorArgumentN(2);
			arguments.put(generatorArgumentN.getName(), generatorArgumentN);

			generator.initialize(inputDomain, constraints, arguments, null);
			IAlgorithm<String> algorithm = generator.getAlgorithm();
			assertTrue(algorithm instanceof AbstractNWiseAlgorithm);
			assertEquals(2, ((AbstractNWiseAlgorithm<String>) algorithm).getN());

			try {
				GeneratorArgumentN generatorArgumentN2 = new GeneratorArgumentN(5);
				arguments.put(generatorArgumentN2.getName(), generatorArgumentN2);
				generator.initialize(inputDomain, constraints, arguments, null);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				GeneratorArgumentN generatorArgumentN2 = new GeneratorArgumentN(-1);
				arguments.put(generatorArgumentN2.getName(), generatorArgumentN2);
				generator.initialize(inputDomain, constraints, arguments, null);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				GeneratorArgumentN generatorArgumentN2 = new GeneratorArgumentN(2);
				arguments.put(generatorArgumentN2.getName(), generatorArgumentN2);
				generator.initialize(inputDomain, constraints, arguments, null);
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
			Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();
			Map<String, IGeneratorArgument> arguments = new HashMap<>();

			GeneratorArgumentN generatorArgumentN = new GeneratorArgumentN(2);
			arguments.put(generatorArgumentN.getName(), generatorArgumentN);

			GeneratorArgumentCoverage generatorArgumentCoverage = new GeneratorArgumentCoverage(100);
			arguments.put(generatorArgumentCoverage.getName(), generatorArgumentCoverage);

			generator.initialize(inputDomain, constraints, arguments, null);
			IAlgorithm<String> algorithm = generator.getAlgorithm();
			assertTrue(algorithm instanceof AbstractNWiseAlgorithm);
			assertEquals(100,
					((AbstractNWiseAlgorithm<String>) algorithm).getCoverage());

			try {
				GeneratorArgumentCoverage generatorArgumentCoverage2 = new GeneratorArgumentCoverage(101);
				arguments.put(generatorArgumentCoverage2.getName(), generatorArgumentCoverage2);
				generator.initialize(inputDomain, constraints, arguments, null);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				GeneratorArgumentCoverage generatorArgumentCoverage2 = new GeneratorArgumentCoverage(-1);
				arguments.put(generatorArgumentCoverage2.getName(), generatorArgumentCoverage2);
				generator.initialize(inputDomain, constraints, arguments, null);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				GeneratorArgumentCoverage generatorArgumentCoverage2 = new GeneratorArgumentCoverage(50);
				arguments.put(generatorArgumentCoverage2.getName(), generatorArgumentCoverage2);
				generator.initialize(inputDomain, constraints, arguments, null);
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException");
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}
}
