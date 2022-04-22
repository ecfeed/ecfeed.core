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

public class IntegerParameterTest {
	@Test
	public void constructorWithAllowedValuesTest() {
		try {
			@SuppressWarnings("unused")
            ParameterDefinitionInteger parameter = new ParameterDefinitionInteger("parameter",  0, new Integer[]{-1, 0, 1});
		} catch (Exception e) {
			fail("Unexpected Exception");
		}
		try {
			@SuppressWarnings("unused")
            ParameterDefinitionInteger parameter = new ParameterDefinitionInteger("parameter",  5, new Integer[]{-1, 0, 1});
			fail("Exception expected");
		} catch (Exception e) {
		}
	}

	@Test
	@SuppressWarnings("unused")
	public void constructorWithBoundsTest() {
		try {
			ParameterDefinitionInteger parameter = new ParameterDefinitionInteger("parameter",  0, -1, 1);
			parameter = new ParameterDefinitionInteger("parameter",  -1, -1, 1);
			parameter = new ParameterDefinitionInteger("parameter",  1, -1, 1);
		} catch (Exception e) {
			fail("Unexpected Exception");
		}
		try {
			ParameterDefinitionInteger parameter = new ParameterDefinitionInteger("parameter",  2, -1, 1);
			fail("Exception expected");
		} catch (Exception e) {
		}
	}

	@Test
	public void allowedValuesTest(){
		try {
			ParameterDefinitionInteger parameter = new ParameterDefinitionInteger("parameter",  0, -1, 1);
			assertArrayEquals(null, parameter.getAllowedValues());
			Integer[] allowed = new Integer[]{0, 1, 2};
			parameter = new ParameterDefinitionInteger("parameter",  0, allowed);
			assertArrayEquals(allowed, parameter.getAllowedValues());
		} catch (Exception e) {
			fail("Unexpected Exception");
		}
	}

	@Test
	public void testTest(){
		try {
			ParameterDefinitionInteger boundedParameter;
			boundedParameter = new ParameterDefinitionInteger("parameter",  0, -1, 1);
			assertTrue(boundedParameter.test(0));
			assertTrue(boundedParameter.test(-1));
			assertTrue(boundedParameter.test(1));
			assertFalse(boundedParameter.test(3));
			assertFalse(boundedParameter.test(1.0));
			
			ParameterDefinitionInteger parameter = new ParameterDefinitionInteger("parameter",  0, new Integer[]{-1, 0, 1});
			assertTrue(parameter.test(0));
			assertFalse(parameter.test(5));
		} catch (Exception e) {
			fail("Unexpected Exception");
		}
	}
}
