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

import org.junit.Test;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorParameterDoubleTest {
	@Test
	public void constructorWithAllowedValuesTest() {
		try {
			@SuppressWarnings("unused")
            ParameterDefinitionDouble parameter = new ParameterDefinitionDouble("parameter",  0.0f, new Double[]{-1.0, 0.0, 1.0});
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
		try {
			@SuppressWarnings("unused")
            ParameterDefinitionDouble parameter = new ParameterDefinitionDouble("parameter",  5.0f, new Double[]{-1.0, 0.0, 1.0});
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

	@Test
	public void constructorWithBoundsTest() {
		try {
			@SuppressWarnings("unused")
            ParameterDefinitionDouble parameter = new ParameterDefinitionDouble("parameter",  0.0, -1.0, 1.0);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
		try {
			@SuppressWarnings("unused")
            ParameterDefinitionDouble parameter = new ParameterDefinitionDouble("parameter",  2.0, -1.0, 1.0);
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

	@Test
	public void allowedValuesTest(){
		try {
			ParameterDefinitionDouble parameter = new ParameterDefinitionDouble("parameter",  0.0, -1.0, 1.0);
			assertArrayEquals(null, parameter.getAllowedValues());
			Double[] allowed = new Double[]{0.0, 1.0, 2.0};
			parameter = new ParameterDefinitionDouble("parameter",  0.0, allowed);
			assertArrayEquals(allowed, parameter.getAllowedValues());
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}

	@Test
	public void testTest(){
		ParameterDefinitionDouble boundedParameter;
		try {
			boundedParameter = new ParameterDefinitionDouble("parameter",  0.0, -1.0, 1.0);
			assertTrue(boundedParameter.test(0.00));
			assertTrue(boundedParameter.test(-1.00));
			assertTrue(boundedParameter.test(1.00));
			assertFalse(boundedParameter.test(3.0));

			boundedParameter = new ParameterDefinitionDouble("parameter",  0.0, new Double[]{-1.0, 0.0, 1.0});
			assertTrue(boundedParameter.test(0.0));
			assertTrue(boundedParameter.test(-1.0));
			assertTrue(boundedParameter.test(1.0));
			assertFalse(boundedParameter.test(3.0));
			assertFalse(boundedParameter.test(3.0f));
			assertFalse(boundedParameter.test("string"));
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}
}
