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



public class ExtLanguageHelperTest {

	@Test
	public void verifySeparatorsTest() {

		// TODO SIMPLE-VIEW use ExtLanguageManager

		String errorMessage = JavaLanguageHelper.verifySeparators("abc");
		assertNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifySeparators("abc");
		assertNull(errorMessage);

		// underline

		errorMessage = JavaLanguageHelper.verifySeparators("ab_c");
		assertNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifySeparators("ab_c");
		assertNotNull(errorMessage);

		// space

		errorMessage = JavaLanguageHelper.verifySeparators("ab c");
		assertNotNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifySeparators("ab c");
		assertNull(errorMessage);
		}

	@Test
	public void validateTypeTest() {

		// TODO SIMPLE-VIEW use ExtLanguageManager

		String errorMessage = JavaLanguageHelper.verifyIsAllowedType("int");
		assertNull(errorMessage);

		errorMessage = JavaLanguageHelper.verifyIsAllowedType("intr");
		assertNotNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifyIsAllowedType("Number");
		assertNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifyIsAllowedType("Num");
		assertNotNull(errorMessage);
	}

	@Test
	public void convertTextToExtLanguageTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		String text = javaExtLanguageManager.convertTextFromIntrToExtLanguage("ab_c");
		assertEquals("ab_c", text);

		text = simpleExtLanguageManager.convertTextFromIntrToExtLanguage("ab_c");
		assertEquals("ab c", text);

		try {
			javaExtLanguageManager.convertTextFromIntrToExtLanguage("ab c");
			fail();
		} catch (Exception e) {
		}

		try {
			simpleExtLanguageManager.convertTextFromIntrToExtLanguage("ab c");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertTextToIntrLanguageTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		String text = javaExtLanguageManager.convertTextFromExtToIntrLanguage("ab_c");
		assertEquals("ab_c", text);

		text = simpleExtLanguageManager.convertTextFromExtToIntrLanguage("ab c");
		assertEquals("ab_c", text);

		try {
			javaExtLanguageManager.convertTextFromExtToIntrLanguage("ab c");
			fail();
		} catch (Exception e) {
		}

		try {
			simpleExtLanguageManager.convertTextFromExtToIntrLanguage("ab_c");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertTypeFromIntrToExtLanguageTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		String type = javaExtLanguageManager.convertTypeFromIntrToExtLanguage("int");
		assertEquals("int", type);

		type = javaExtLanguageManager.convertTypeFromIntrToExtLanguage("int");
		assertEquals("Number", type);

		type = javaExtLanguageManager.convertTypeFromIntrToExtLanguage("x");
		assertEquals("x", type);

		type = simpleExtLanguageManager.convertTypeFromIntrToExtLanguage("x");
		assertEquals("x", type);
	}

	@Test
	public void convertTypeToIntrLanguageTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		String text = javaExtLanguageManager.convertTypeFromExtToIntrLanguage("int");
		assertEquals("int", text);

		text = simpleExtLanguageManager.convertTypeFromExtToIntrLanguage("Number");
		assertEquals("double", text);

		try {
			javaExtLanguageManager.convertTypeFromExtToIntrLanguage("x");
			fail();
		} catch (Exception e) {
		}

		try {
			simpleExtLanguageManager.convertTypeFromExtToIntrLanguage("x");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertSpecialValueToExtLanguageTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		String text = javaExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage("MAX_VALUE","int");
		assertEquals("MAX_VALUE", text);

		text = simpleExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage("MAX_VALUE", "int");
		assertEquals("2147483647", text);

		// invalid type

		text = javaExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage("MAX_VALUE", "Z");
		assertEquals("MAX_VALUE", text);

		try {
			simpleExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage("MAX_VALUE", "Z");
			fail();
		} catch (Exception e) {
		}

		// invalid value

		text = javaExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage("x", "int");
		assertEquals("x", text);

		text = simpleExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage("x", "int");
		assertEquals("x", text);
	}

	@Test
	public void getSymbolicNamesTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		List<String> names = javaExtLanguageManager.getSymbolicNamesOfSpecialValues("int");
		assertNotEquals(0, names.size());

		names = simpleExtLanguageManager.getSymbolicNamesOfSpecialValues("int");
		assertEquals(0, names.size());
	}

	@Test
	public void isLogicalTypeNameTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		assertTrue(javaExtLanguageManager.isLogicalTypeName("boolean"));
		assertTrue(simpleExtLanguageManager.isLogicalTypeName("Logical"));

		assertFalse(simpleExtLanguageManager.isLogicalTypeName("boolean"));
		assertFalse(javaExtLanguageManager.isLogicalTypeName("Logical"));

		assertFalse(simpleExtLanguageManager.isLogicalTypeName("x"));
		assertFalse(javaExtLanguageManager.isLogicalTypeName("x"));
	}

}
