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

public class GeneratorParameterStringTest {

	@Test
	public void testTest() {
		try {
			ParameterDefinitionString p1 = new ParameterDefinitionString("parameter",  "default", new String[]{"default", "value"});
			assertTrue(p1.test("default"));
			assertTrue(p1.test("value"));
			assertFalse(p1.test("other"));
			assertFalse(p1.test(5));
			ParameterDefinitionString p2 = new ParameterDefinitionString("parameter",  "default");
			assertTrue(p2.test("default"));
			assertTrue(p2.test("value"));
			assertTrue(p2.test("other"));
			assertFalse(p2.test(5));
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}

	@Test
	public void constructorTest() {
		try {
			ParameterDefinitionString parameter = new ParameterDefinitionString("parameter",  "default", new String[]{"default", "value"});
			assertEquals("default", parameter.getDefaultValue());
			assertEquals("parameter", parameter.getName());
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
		try {
			new ParameterDefinitionString("parameter",  "default", new String[]{"any", "value"});
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

}
