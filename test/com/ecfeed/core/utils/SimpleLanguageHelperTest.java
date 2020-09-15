/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleLanguageHelperTest {

	@Test
	public void verifySeparatorsTest() {

		String errorMessage = SimpleLanguageHelper.verifySeparators("abc");
		assertNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifySeparators("a_b");
		assertNotNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifySeparators(" ab");
		assertNotNull(errorMessage);
	}

	@Test
	public void isSimpleTypeTest() {

		assertTrue(SimpleLanguageHelper.isSimpleType("Number"));
		assertTrue(SimpleLanguageHelper.isSimpleType("Text"));
		assertTrue(SimpleLanguageHelper.isSimpleType("Logical"));
		assertFalse(SimpleLanguageHelper.isSimpleType("int"));
		assertFalse(SimpleLanguageHelper.isSimpleType("double"));
		assertFalse(SimpleLanguageHelper.isSimpleType("boolean"));
		assertFalse(SimpleLanguageHelper.isSimpleType("String"));
	}

	@Test
	public void getSupportedJavaTypesTest() {

		String[] simpleTypes = SimpleLanguageHelper.getSupportedSimpleViewTypes();
		assertEquals(3, simpleTypes.length);
		assertEquals("Text", simpleTypes[0]);
		assertEquals("Number", simpleTypes[1]);
		assertEquals("Logical", simpleTypes[2]);
	}

}
