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

import java.util.List;

import static org.junit.Assert.*;

public class JavaLanguageHelperTest {

	@Test
	public void verifySeparatorsTest() {

		String errorMessage = JavaLanguageHelper.verifySeparators("abc");
		assertNull(errorMessage);

		errorMessage = JavaLanguageHelper.verifySeparators("a_b");
		assertNull(errorMessage);

		errorMessage = JavaLanguageHelper.verifySeparators("a b");
		assertNotNull(errorMessage);

		errorMessage = JavaLanguageHelper.verifySeparators("_ab");
		assertNotNull(errorMessage);
	}

	@Test
	public void isJavaKeywordTest() {

		assertTrue(JavaLanguageHelper.isJavaKeyword("class"));
		assertTrue(JavaLanguageHelper.isJavaKeyword("if"));
		assertTrue(JavaLanguageHelper.isJavaKeyword("for"));

		assertFalse(JavaLanguageHelper.isJavaKeyword("x"));
	}

	@Test
	public void isValidJavaIdentifierTest() {

		assertTrue(JavaLanguageHelper.isValidJavaIdentifier("abc"));
		assertTrue(JavaLanguageHelper.isValidJavaIdentifier("_a_b_"));
		assertFalse(JavaLanguageHelper.isValidJavaIdentifier("a%b"));
		assertFalse(JavaLanguageHelper.isValidJavaIdentifier("class"));
		assertFalse(JavaLanguageHelper.isValidJavaIdentifier("static"));
	}

	@Test
	public void isValidTypeNameTest() {
		assertTrue(JavaLanguageHelper.isValidTypeName("com.ecfeed.Xx"));
		assertFalse(JavaLanguageHelper.isValidTypeName("com.ecfeed.X*x"));
	}

	@Test
	public void getSymbolicNamesTest() {

		List<String> symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("boolean");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, new String[]{ "true", "false"}));

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("char");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, new String[]{ "0" }));

		final String[] symbolicNamesForIntegers = {"MIN_VALUE", "MAX_VALUE"};

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("byte");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, symbolicNamesForIntegers));

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("short");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, symbolicNamesForIntegers));

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("int");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, symbolicNamesForIntegers));

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("long");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, symbolicNamesForIntegers));

		final String[] symbolicNamesForFloats = {"NEGATIVE_INFINITY", "POSITIVE_INFINITY", "MIN_VALUE", "MAX_VALUE", "-MIN_VALUE", "-MAX_VALUE"};

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("float");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, symbolicNamesForFloats));

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("double");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, symbolicNamesForFloats));

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("String");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, new String[] { "/null"}));
	}

	@Test
	public void getDefaultExpectedValueTest() {

		String value = JavaLanguageHelper.getDefaultExpectedValue("byte");
		assertEquals("0",  value);

		value = JavaLanguageHelper.getDefaultExpectedValue("short");
		assertEquals("0",  value);

		value = JavaLanguageHelper.getDefaultExpectedValue("int");
		assertEquals("0",  value);

		value = JavaLanguageHelper.getDefaultExpectedValue("long");
		assertEquals("0",  value);

		value = JavaLanguageHelper.getDefaultExpectedValue("float");
		assertEquals("0.0",  value);

		value = JavaLanguageHelper.getDefaultExpectedValue("double");
		assertEquals("0.0",  value);

		value = JavaLanguageHelper.getDefaultExpectedValue("String");
		assertEquals("",  value);

		value = JavaLanguageHelper.getDefaultExpectedValue("char");
		assertEquals("0",  value);

		value = JavaLanguageHelper.getDefaultExpectedValue("boolean");
		assertEquals("false",  value);

		try {
			JavaLanguageHelper.getDefaultExpectedValue("x");
			fail();
		} catch (Exception e) {
		}
	}
}
