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

		String errorMessage = ExtLanguageHelper.verifySeparatorsInName("abc", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("abc", ExtLanguage.SIMPLE);
		assertNull(errorMessage);

		// underline

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("ab_c", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("ab_c", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);

		// space

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("ab c", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("ab c", ExtLanguage.SIMPLE);
		assertNull(errorMessage);
		}

	@Test
	public void validateTypeTest() {

		String errorMessage = ExtLanguageHelper.validateType("int", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ExtLanguageHelper.validateType("intr", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = ExtLanguageHelper.validateType("Number", ExtLanguage.SIMPLE);
		assertNull(errorMessage);

		errorMessage = ExtLanguageHelper.validateType("Num", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);
	}

	@Test
	public void convertTextToExtLanguageTest() {

		String text = ExtLanguageHelper.convertTextFromIntrToExtLanguage("ab_c", ExtLanguage.JAVA);
		assertEquals("ab_c", text);

		text = ExtLanguageHelper.convertTextFromIntrToExtLanguage("ab_c", ExtLanguage.SIMPLE);
		assertEquals("ab c", text);

		try {
			ExtLanguageHelper.convertTextFromIntrToExtLanguage("ab c", ExtLanguage.JAVA);
			fail();
		} catch (Exception e) {
		}

		try {
			ExtLanguageHelper.convertTextFromIntrToExtLanguage("ab c", ExtLanguage.SIMPLE);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertTextToIntrLanguageTest() {

		String text = ExtLanguageHelper.convertTextFromExtToIntrLanguage("ab_c", ExtLanguage.JAVA);
		assertEquals("ab_c", text);

		text = ExtLanguageHelper.convertTextFromExtToIntrLanguage("ab c", ExtLanguage.SIMPLE);
		assertEquals("ab_c", text);

		try {
			ExtLanguageHelper.convertTextFromExtToIntrLanguage("ab c", ExtLanguage.JAVA);
			fail();
		} catch (Exception e) {
		}

		try {
			ExtLanguageHelper.convertTextFromExtToIntrLanguage("ab_c", ExtLanguage.SIMPLE);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertTypeFromIntrToExtLanguageTest() {

		String type = ExtLanguageHelper.convertTypeFromIntrToExtLanguage("int", ExtLanguage.JAVA);
		assertEquals("int", type);

		type = ExtLanguageHelper.convertTypeFromIntrToExtLanguage("int", ExtLanguage.SIMPLE);
		assertEquals("Number", type);

		type = ExtLanguageHelper.convertTypeFromIntrToExtLanguage("x", ExtLanguage.JAVA);
		assertEquals("x", type);

		type = ExtLanguageHelper.convertTypeFromIntrToExtLanguage("x", ExtLanguage.SIMPLE);
		assertNull(type);
	}

	@Test
	public void convertTypeToIntrLanguageTest() {

		String text = ExtLanguageHelper.convertTypeFromExtToIntrLanguage("int", ExtLanguage.JAVA);
		assertEquals("int", text);

		text = ExtLanguageHelper.convertTypeFromExtToIntrLanguage("Number", ExtLanguage.SIMPLE);
		assertEquals("double", text);

		try {
			ExtLanguageHelper.convertTypeFromExtToIntrLanguage("x", ExtLanguage.JAVA);
			fail();
		} catch (Exception e) {
		}

		try {
			ExtLanguageHelper.convertTypeFromExtToIntrLanguage("x", ExtLanguage.SIMPLE);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertSpecialValueToExtLanguageTest() {

		String text = ExtLanguageHelper.conditionallyConvertSpecialValueToExtLanguage("MAX_VALUE","int", ExtLanguage.JAVA);
		assertEquals("MAX_VALUE", text);

		text = ExtLanguageHelper.conditionallyConvertSpecialValueToExtLanguage("MAX_VALUE", "int", ExtLanguage.SIMPLE);
		assertEquals("2147483647", text);

		// invalid type

		try {
			ExtLanguageHelper.conditionallyConvertSpecialValueToExtLanguage("MAX_VALUE", "Z", ExtLanguage.JAVA);
			fail();
		} catch (Exception e) {
		}

		try {
			ExtLanguageHelper.conditionallyConvertSpecialValueToExtLanguage("MAX_VALUE", "Z", ExtLanguage.SIMPLE);
			fail();
		} catch (Exception e) {
		}

		// invalid value

		text = ExtLanguageHelper.conditionallyConvertSpecialValueToExtLanguage("x", "int", ExtLanguage.JAVA);
		assertEquals("x", text);

		text = ExtLanguageHelper.conditionallyConvertSpecialValueToExtLanguage("x", "int", ExtLanguage.SIMPLE);
		assertEquals("x", text);
	}

	@Test
	public void getSymbolicNamesTest() {

		List<String> names = ExtLanguageHelper.getSymbolicNamesOfSpecialValues("int", ExtLanguage.JAVA);
		assertNotEquals(0, names.size());

		names = ExtLanguageHelper.getSymbolicNamesOfSpecialValues("int", ExtLanguage.SIMPLE);
		assertEquals(0, names.size());
	}

}
