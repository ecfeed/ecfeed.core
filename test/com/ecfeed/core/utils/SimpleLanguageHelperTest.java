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

	@Test
	public void convertToSimpleTypeTest() {

		assertEquals("Number", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("byte"));
		assertEquals("Number", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("short"));
		assertEquals("Number", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("int"));
		assertEquals("Number", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("long"));
		assertEquals("Number", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("float"));
		assertEquals("Number", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("double"));

		assertEquals("Text", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("char"));
		assertEquals("Text", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("String"));

		assertEquals("Logical", SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType("boolean"));
	}

	@Test
	public void convertToJavaTypeTest() {

		assertEquals("double", SimpleLanguageHelper.conditionallyConvertSimpleTypeToJavaType("Number"));
		assertEquals("String", SimpleLanguageHelper.conditionallyConvertSimpleTypeToJavaType("Text"));
		assertEquals("boolean", SimpleLanguageHelper.conditionallyConvertSimpleTypeToJavaType("Logical"));
	}

	@Test
	public void convertTextTest() {

		assertEquals("a b", SimpleLanguageHelper.convertTextFromJavaToSimpleLanguage("a_b"));
		assertEquals("a_b", SimpleLanguageHelper.convertTextFromSimpleToJavaLanguage("a b"));
	}

	@Test
	public void validateTypeTest() {

		String errorMessage = SimpleLanguageHelper.validateType("Number");
		assertNull(errorMessage);

		errorMessage = SimpleLanguageHelper.validateType("Text");
		assertNull(errorMessage);

		errorMessage = SimpleLanguageHelper.validateType("Logical");
		assertNull(errorMessage);

		errorMessage = SimpleLanguageHelper.validateType("int");
		assertNotNull(errorMessage);
	}

}
