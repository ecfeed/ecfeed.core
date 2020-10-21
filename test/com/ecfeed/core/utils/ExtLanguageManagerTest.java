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

import com.ecfeed.core.model.RootNode;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;



public class ExtLanguageManagerTest {

	@Test
	public void verifySeparatorsTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();


		String errorMessage = javaExtLanguageManager.verifySeparators("abc");
		assertNull(errorMessage);

		errorMessage = simpleExtLanguageManager.verifySeparators("abc");
		assertNull(errorMessage);

		// underline

		errorMessage = javaExtLanguageManager.verifySeparators("ab_c");
		assertNull(errorMessage);

		errorMessage = simpleExtLanguageManager.verifySeparators("ab_c");
		assertNotNull(errorMessage);

		// space

		errorMessage = javaExtLanguageManager.verifySeparators("ab c");
		assertNotNull(errorMessage);

		errorMessage = simpleExtLanguageManager.verifySeparators("ab c");
		assertNull(errorMessage);
		}

	@Test
	public void validateTypeTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		String errorMessage = javaExtLanguageManager.verifyIsAllowedType("int");
		assertNull(errorMessage);

		errorMessage = javaExtLanguageManager.verifyIsAllowedType("intr");
		assertNull(errorMessage);

		errorMessage = simpleExtLanguageManager.verifyIsAllowedType("Number");
		assertNull(errorMessage);

		errorMessage = simpleExtLanguageManager.verifyIsAllowedType("Num");
		assertNull(errorMessage);
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

		type = simpleExtLanguageManager.convertTypeFromIntrToExtLanguage("int");
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

		String text = javaExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage(
				"MAX_VALUE","int");
		assertEquals("MAX_VALUE", text);

		text = simpleExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage(
				"MAX_VALUE", "int");
		assertEquals("2147483647", text);

		// invalid type

		text = javaExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage(
				"MAX_VALUE", "Z");
		assertEquals("MAX_VALUE", text);

		text = simpleExtLanguageManager.conditionallyConvertSpecialValueToExtLanguage(
					"MAX_VALUE", "Z");
		assertEquals("MAX_VALUE", text);

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

	@Test
	public void getPackageVisibilityTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		assertTrue(javaExtLanguageManager.getPackageVisibility());
		assertFalse(simpleExtLanguageManager.getPackageVisibility());
	}

	@Test
	public void getExtendedTypeForValueTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		assertEquals("short", simpleExtLanguageManager.getExtendedTypeForValue("1000", "byte"));
		assertEquals("byte", javaExtLanguageManager.getExtendedTypeForValue("1000", "byte"));

		assertEquals("String", simpleExtLanguageManager.getExtendedTypeForValue("AB", "char"));
		assertEquals("String", simpleExtLanguageManager.getExtendedTypeForValue("", "char"));


	}

	@Test
	public void createQualifiedNameTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		assertEquals("com.User", javaExtLanguageManager.createQualifiedName("com", "User"));
		assertEquals("User", simpleExtLanguageManager.createQualifiedName("com", "User"));
	}

	@Test
	public void getPackageNameTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		assertEquals("com", javaExtLanguageManager.getPackageName("com.User"));
		assertEquals("", simpleExtLanguageManager.getPackageName("com.User"));
	}

	@Test
	public void isModelCompatibleWithExtLanguageTest() {

		IExtLanguageManager javaExtLanguageManager = new ExtLanguageManagerForJava();
		IExtLanguageManager simpleExtLanguageManager = new ExtLanguageManagerForSimple();

		RootNode rootNode = new RootNode("root", null);

		assertNull(javaExtLanguageManager.checkIsModelCompatibleWithExtLanguage(rootNode));
		assertNull(simpleExtLanguageManager.checkIsModelCompatibleWithExtLanguage(rootNode));
	}
}
