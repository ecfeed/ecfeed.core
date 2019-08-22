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

import static com.ecfeed.core.generators.RandomGenerator.DUPLICATES_PARAMETER_NAME;
import static com.ecfeed.core.generators.RandomGenerator.LENGTH_PARAMETER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import org.junit.Test;

import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.algorithms.RandomAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;
import com.ecfeed.core.model.IConstraint;

public class RandomGeneratorTest {
	@Test
	public void initializeTest(){
		try {
			RandomGenerator<String> generator = new RandomGenerator<String>();
			List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3, 3);
			List<IGeneratorValue> arguments = new ArrayList<>();

			GeneratorValue generatorArgumentLength = new GeneratorValue(generator.getDefinitionLength(), "100");
			arguments.add(generatorArgumentLength);

			generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
			IAlgorithm<String> algorithm = generator.getAlgorithm(); 
			assertTrue(algorithm instanceof RandomAlgorithm);
			assertEquals(false, ((RandomAlgorithm<String>)algorithm).getDuplicates());
			assertEquals(100, ((RandomAlgorithm<String>)algorithm).getLength());
			
			try{

				GeneratorValue generatorArgumentDuplicates = new GeneratorValue(generator.getDefinitionDuplicates(), "true");
				arguments.add(generatorArgumentDuplicates);
				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, new SimpleProgressMonitor());
				assertEquals(false, ((RandomAlgorithm<String>)algorithm).getDuplicates());
			}catch(GeneratorException e) {
				fail("Unexpected GeneratorException: " + e.getMessage());
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}

}
