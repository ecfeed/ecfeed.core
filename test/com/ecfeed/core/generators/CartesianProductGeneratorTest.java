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

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.generators.api.IParameterDefinition;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import org.junit.Test;

import com.ecfeed.core.generators.algorithms.CartesianProductAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;
import com.ecfeed.core.model.IConstraint;

public class CartesianProductGeneratorTest{
	@Test
	public void initializeTest(){
		CartesianProductGenerator<String> generator = null;
		try {
			generator = new CartesianProductGenerator<String>();
		}
		catch (GeneratorException e)
		{

		}
		
		List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3, 3);

		List<IGeneratorValue> parameters = new ArrayList<>();
		try {
			generator.initialize(inputDomain, new DummyEvaluator<>(), parameters, new SimpleProgressMonitor());
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
		assertTrue(generator.getAlgorithm() instanceof CartesianProductAlgorithm);
	}

}
