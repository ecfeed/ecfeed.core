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
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, new String[]{"true", "false"}));

		symbolicNames = JavaLanguageHelper.getSymbolicNamesOfSpecialValues("char");
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, new String[]{"0"}));

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
		assertTrue(CollectionHelper.isTheSameContent(symbolicNames, new String[]{"/null"}));
	}

	@Test
	public void getDefaultExpectedValueTest() {

		String value = JavaLanguageHelper.getDefaultValue("byte");
		assertEquals("0", value);

		value = JavaLanguageHelper.getDefaultValue("short");
		assertEquals("0", value);

		value = JavaLanguageHelper.getDefaultValue("int");
		assertEquals("0", value);

		value = JavaLanguageHelper.getDefaultValue("long");
		assertEquals("0", value);

		value = JavaLanguageHelper.getDefaultValue("float");
		assertEquals("0.0", value);

		value = JavaLanguageHelper.getDefaultValue("double");
		assertEquals("0.0", value);

		value = JavaLanguageHelper.getDefaultValue("String");
		assertEquals("", value);

		value = JavaLanguageHelper.getDefaultValue("char");
		assertEquals("0", value);

		value = JavaLanguageHelper.getDefaultValue("boolean");
		assertEquals("false", value);

		try {
			JavaLanguageHelper.getDefaultValue("x");
			fail();
		} catch (Exception e) {
		}
	}


	@Test
	public void convertSpecialValueToNumericTest() {

		// byte

		String numeric = JavaLanguageHelper.convertSpecialValueToNumeric("byte", "MAX_VALUE");
		assertEquals("127", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("byte", "MIN_VALUE");
		assertEquals("-128", numeric);

		// short

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("short", "MAX_VALUE");
		assertEquals("32767", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("short", "MIN_VALUE");
		assertEquals("-32768", numeric);

		// int

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("int", "MAX_VALUE");
		assertEquals("2147483647", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("int", "MIN_VALUE");
		assertEquals("-2147483648", numeric);

		// long

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("long", "MAX_VALUE");
		assertEquals("9223372036854775807", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("long", "MIN_VALUE");
		assertEquals("-9223372036854775808", numeric);

		// float

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("float", "MAX_VALUE");
		assertEquals("3.4028235E38", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("float", "MIN_VALUE");
		assertEquals("1.4E-45", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("float", "-MAX_VALUE");
		assertEquals("-3.4028235E38", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("float", "-MIN_VALUE");
		assertEquals("-1.4E-45", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("float", "POSITIVE_INFINITY");
		assertEquals("POSITIVE_INFINITY", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("float", "NEGATIVE_INFINITY");
		assertEquals("NEGATIVE_INFINITY", numeric);

		// double

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("double", "MAX_VALUE");
		assertEquals("1.7976931348623157E308", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("double", "MIN_VALUE");
		assertEquals("4.9E-324", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("double", "-MAX_VALUE");
		assertEquals("-1.7976931348623157E308", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("double", "-MIN_VALUE");
		assertEquals("-4.9E-324", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("double", "POSITIVE_INFINITY");
		assertEquals("POSITIVE_INFINITY", numeric);

		numeric = JavaLanguageHelper.convertSpecialValueToNumeric("double", "NEGATIVE_INFINITY");
		assertEquals("NEGATIVE_INFINITY", numeric);


		// String

		try {
			JavaLanguageHelper.convertSpecialValueToNumeric("String", "X");
			fail();
		} catch (Exception e) {
		}

		// char

		try {
			JavaLanguageHelper.convertSpecialValueToNumeric("char", "0");
			fail();
		} catch (Exception e) {
		}

		// boolean

		try {
			JavaLanguageHelper.convertSpecialValueToNumeric("boolean", "false");
			fail();
		} catch (Exception e) {
		}

	}
}