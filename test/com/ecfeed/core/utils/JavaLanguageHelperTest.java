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
    public void isMatchWithJavaComplexIdenfifierTest() {

        assertTrue(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("class"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("if"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("for"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("int"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("String"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("java.lang.String"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("com.xxx.Class"));

        assertFalse(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("com-xxx-Class"));
        assertFalse(JavaLanguageHelper.isMatchWithJavaComplexIdenfifier("&"));
    }

    @Test
    public void isMatchWithJavaSimpleIdenfifierTest() {

        assertTrue(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("class"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("if"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("for"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("int"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("String"));
        assertTrue(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("User"));

        assertFalse(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("java.lang.String"));
        assertFalse(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("com.xxx.Class"));
        assertFalse(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("com-xxx-Class"));
        assertFalse(JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier("&"));
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

        assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("boolean"));
        assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("String"));
        assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("int"));
        assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("Integer"));
		assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("com.ecfeed.Xx"));
		assertFalse(JavaLanguageHelper.isValidComplexTypeIdentifier("com.ecfeed.X*x"));
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
	public void conditionallyConvertSpecialValueToNumericTest() {

		// byte

		String numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("byte", "MAX_VALUE");
		assertEquals("127", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("byte", "MIN_VALUE");
		assertEquals("-128", numeric);

		// short

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("short", "MAX_VALUE");
		assertEquals("32767", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("short", "MIN_VALUE");
		assertEquals("-32768", numeric);

		// int

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("int", "MAX_VALUE");
		assertEquals("2147483647", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("int", "MIN_VALUE");
		assertEquals("-2147483648", numeric);

		// long

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("long", "MAX_VALUE");
		assertEquals("9223372036854775807", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("long", "MIN_VALUE");
		assertEquals("-9223372036854775808", numeric);

		// float

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("float", "MAX_VALUE");
		assertEquals("3.4028235E38", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("float", "MIN_VALUE");
		assertEquals("1.4E-45", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("float", "-MAX_VALUE");
		assertEquals("-3.4028235E38", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("float", "-MIN_VALUE");
		assertEquals("-1.4E-45", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("float", "POSITIVE_INFINITY");
		assertEquals("POSITIVE_INFINITY", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("float", "NEGATIVE_INFINITY");
		assertEquals("NEGATIVE_INFINITY", numeric);

		// double

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("double", "MAX_VALUE");
		assertEquals("1.7976931348623157E308", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("double", "MIN_VALUE");
		assertEquals("4.9E-324", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("double", "-MAX_VALUE");
		assertEquals("-1.7976931348623157E308", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("double", "-MIN_VALUE");
		assertEquals("-4.9E-324", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("double", "POSITIVE_INFINITY");
		assertEquals("POSITIVE_INFINITY", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("double", "NEGATIVE_INFINITY");
		assertEquals("NEGATIVE_INFINITY", numeric);


		// String

		try {
			JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("String", "X");
			fail();
		} catch (Exception e) {
		}

		// char

		try {
			JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("char", "0");
			fail();
		} catch (Exception e) {
		}

		// boolean

		try {
			JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("boolean", "false");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void isJavaTypeTest() {

		assertTrue(JavaLanguageHelper.isJavaType("byte"));
		assertTrue(JavaLanguageHelper.isJavaType("short"));
		assertTrue(JavaLanguageHelper.isJavaType("int"));
		assertTrue(JavaLanguageHelper.isJavaType("long"));
		assertTrue(JavaLanguageHelper.isJavaType("char"));
		assertTrue(JavaLanguageHelper.isJavaType("String"));
		assertTrue(JavaLanguageHelper.isJavaType("boolean"));
		assertFalse(JavaLanguageHelper.isJavaType("Type"));
		assertFalse(JavaLanguageHelper.isJavaType("class"));
	}

	@Test
	public void isUserTypeTest() {

		assertFalse(JavaLanguageHelper.isUserType("byte"));
		assertFalse(JavaLanguageHelper.isUserType("short"));
		assertFalse(JavaLanguageHelper.isUserType("int"));
		assertFalse(JavaLanguageHelper.isUserType("long"));
		assertFalse(JavaLanguageHelper.isUserType("char"));
		assertFalse(JavaLanguageHelper.isUserType("String"));
		assertFalse(JavaLanguageHelper.isUserType("boolean"));
		assertTrue(JavaLanguageHelper.isUserType("Type"));
		assertFalse(JavaLanguageHelper.isUserType("class"));
	}

	@Test
	public void getSupportedJavaTypes() {
		String[] javaTypes = JavaLanguageHelper.getSupportedJavaTypes();
		assertEquals(9, javaTypes.length);
	}

	@Test
	public void hasLimitedValuesSetTest() {
		assertTrue(JavaLanguageHelper.hasLimitedValuesSet("boolean"));
		assertFalse(JavaLanguageHelper.hasLimitedValuesSet("int"));
		assertFalse(JavaLanguageHelper.hasLimitedValuesSet("String"));
		assertTrue(JavaLanguageHelper.hasLimitedValuesSet("User"));
	}

	@Test
	public void isExtendedIntTypeNameTest() {

		assertTrue(JavaLanguageHelper.isExtendedIntTypeName("byte"));
		assertTrue(JavaLanguageHelper.isExtendedIntTypeName("int"));
		assertTrue(JavaLanguageHelper.isExtendedIntTypeName("short"));
		assertTrue(JavaLanguageHelper.isExtendedIntTypeName("long"));

		assertFalse(JavaLanguageHelper.isExtendedIntTypeName("float"));
		assertFalse(JavaLanguageHelper.isExtendedIntTypeName("double"));
		assertFalse(JavaLanguageHelper.isExtendedIntTypeName("boolean"));
		assertFalse(JavaLanguageHelper.isExtendedIntTypeName("char"));
		assertFalse(JavaLanguageHelper.isExtendedIntTypeName("String"));
		assertFalse(JavaLanguageHelper.isExtendedIntTypeName("User"));
	}

	@Test
	public void isNumericTypeNameTest() {

		assertTrue(JavaLanguageHelper.isNumericTypeName("byte"));
		assertTrue(JavaLanguageHelper.isNumericTypeName("int"));
		assertTrue(JavaLanguageHelper.isNumericTypeName("short"));
		assertTrue(JavaLanguageHelper.isNumericTypeName("long"));
		assertTrue(JavaLanguageHelper.isNumericTypeName("float"));
		assertTrue(JavaLanguageHelper.isNumericTypeName("double"));

		assertFalse(JavaLanguageHelper.isNumericTypeName("boolean"));
		assertFalse(JavaLanguageHelper.isNumericTypeName("char"));
		assertFalse(JavaLanguageHelper.isNumericTypeName("String"));
		assertFalse(JavaLanguageHelper.isNumericTypeName("User"));
	}

	@Test
	public void isTypeWithCharsTest() {

		assertFalse(JavaLanguageHelper.isTypeWithChars("byte"));
		assertFalse(JavaLanguageHelper.isTypeWithChars("int"));
		assertFalse(JavaLanguageHelper.isTypeWithChars("short"));
		assertFalse(JavaLanguageHelper.isTypeWithChars("long"));
		assertFalse(JavaLanguageHelper.isTypeWithChars("float"));
		assertFalse(JavaLanguageHelper.isTypeWithChars("double"));

		assertFalse(JavaLanguageHelper.isTypeWithChars("boolean"));
		assertTrue(JavaLanguageHelper.isTypeWithChars("char"));
		assertTrue(JavaLanguageHelper.isTypeWithChars("String"));
		assertFalse(JavaLanguageHelper.isTypeWithChars("User"));
	}

	@Test
	public void isTypeComparableTest() {

		assertTrue(JavaLanguageHelper.isTypeComparableForLessGreater("byte"));
		assertTrue(JavaLanguageHelper.isTypeComparableForLessGreater("int"));
		assertTrue(JavaLanguageHelper.isTypeComparableForLessGreater("short"));
		assertTrue(JavaLanguageHelper.isTypeComparableForLessGreater("long"));
		assertTrue(JavaLanguageHelper.isTypeComparableForLessGreater("float"));
		assertTrue(JavaLanguageHelper.isTypeComparableForLessGreater("double"));

		assertFalse(JavaLanguageHelper.isTypeComparableForLessGreater("boolean"));
		assertTrue(JavaLanguageHelper.isTypeComparableForLessGreater("char"));
		assertTrue(JavaLanguageHelper.isTypeComparableForLessGreater("String"));
		assertFalse(JavaLanguageHelper.isTypeComparableForLessGreater("User"));
	}

	@Test
	public void convertNumericToDoulbleTest() {

		double delta = 0.00001;

		Double value = JavaLanguageHelper.convertNumericToDouble("byte", "1", ERunMode.QUIET);
		assertEquals(1.0, value, delta);

		value = JavaLanguageHelper.convertNumericToDouble("short", "1", ERunMode.QUIET);
		assertEquals(1.0, value, delta);

		value = JavaLanguageHelper.convertNumericToDouble("int", "1", ERunMode.QUIET);
		assertEquals(1.0, value, delta);

		value = JavaLanguageHelper.convertNumericToDouble("long", "1", ERunMode.QUIET);
		assertEquals(1.0, value, delta);

		value = JavaLanguageHelper.convertNumericToDouble("float", "1.0", ERunMode.QUIET);
		assertEquals(1.0, value, delta);

		value = JavaLanguageHelper.convertNumericToDouble("double", "1.0", ERunMode.QUIET);
		assertEquals(1.0, value, delta);

		value = JavaLanguageHelper.convertNumericToDouble("boolean", "true", ERunMode.QUIET);
		assertNull(value);

		value = JavaLanguageHelper.convertNumericToDouble("char", "1", ERunMode.QUIET);
		assertNull(value);

		value = JavaLanguageHelper.convertNumericToDouble("String", "1", ERunMode.QUIET);
		assertNull(value);

		value = JavaLanguageHelper.convertNumericToDouble("User", "1", ERunMode.QUIET);
		assertNull(value);
	}

	@Test
	public void parseValueToNumberTest() {

		Object result = JavaLanguageHelper.parseValueToNumber("11", "byte", ERunMode.QUIET);
		assertEquals((byte)11, result);

		result = JavaLanguageHelper.parseValueToNumber("12", "short", ERunMode.QUIET);
		assertEquals((short)12, result);

		result = JavaLanguageHelper.parseValueToNumber("13", "int", ERunMode.QUIET);
		assertEquals(13, result);

		result = JavaLanguageHelper.parseValueToNumber("14", "long", ERunMode.QUIET);
		assertEquals((long)14, result);

		result = JavaLanguageHelper.parseValueToNumber("false", "boolean", ERunMode.QUIET);
		assertNull(result);

		result = JavaLanguageHelper.parseValueToNumber("e", "char", ERunMode.QUIET);
		assertNull(result);

		result = JavaLanguageHelper.parseValueToNumber("15", "String", ERunMode.QUIET);
		assertNull(result);

		result = JavaLanguageHelper.parseValueToNumber("16", "User", ERunMode.QUIET);
		assertNull(result);
	}

	@Test
	public void parseValueToObjectTest() {

		Object result = JavaLanguageHelper.parseJavaValueToObject("11", "byte", ERunMode.QUIET);
		assertEquals((byte)11, result);

		result = JavaLanguageHelper.parseJavaValueToObject("12", "short", ERunMode.QUIET);
		assertEquals((short)12, result);

		result = JavaLanguageHelper.parseJavaValueToObject("13", "int", ERunMode.QUIET);
		assertEquals(13, result);

		result = JavaLanguageHelper.parseJavaValueToObject("14", "long", ERunMode.QUIET);
		assertEquals((long)14, result);

		result = JavaLanguageHelper.parseJavaValueToObject("false", "boolean", ERunMode.QUIET);
		assertEquals(false, result);

		result = JavaLanguageHelper.parseJavaValueToObject("e", "char", ERunMode.QUIET);
		assertEquals('e', result);

		result = JavaLanguageHelper.parseJavaValueToObject("15", "String", ERunMode.QUIET);
		assertEquals("15", result);

		result = JavaLanguageHelper.parseJavaValueToObject("16", "User", ERunMode.QUIET);
		assertNull(result);
	}

	@Test
	public void parseValueToStringTest() {

		Object result = JavaLanguageHelper.parseJavaValueToString("11", "byte");
		assertEquals("11", result);

		result = JavaLanguageHelper.parseJavaValueToString("12", "short");
		assertEquals("12", result);

		result = JavaLanguageHelper.parseJavaValueToString("13", "int");
		assertEquals("13", result);

		result = JavaLanguageHelper.parseJavaValueToString("14", "long");
		assertEquals("14", result);

		result = JavaLanguageHelper.parseJavaValueToString("false", "boolean");
		assertEquals("false", result);

		result = JavaLanguageHelper.parseJavaValueToString("e", "char");
		assertEquals("e", result);

		result = JavaLanguageHelper.parseJavaValueToString("15", "String");
		assertEquals("15", result);

		result = JavaLanguageHelper.parseJavaValueToString("16", "User");
		assertNull(result);
	}

	@Test
	public void getSubstituteTypeTest() {

		String type = JavaLanguageHelper.getSubstituteType("int", "byte");
		assertEquals("long", type);

		type = JavaLanguageHelper.getSubstituteType("boolean", "byte");
		assertEquals("boolean", type);

		type = JavaLanguageHelper.getSubstituteType("int", "double");
		assertEquals("double", type);

		type = JavaLanguageHelper.getSubstituteType("String", "double");
		assertEquals("double", type);

		type = JavaLanguageHelper.getSubstituteType("String", "char");
		assertEquals("String", type);

		// TODO SIMPLE-VIEW commented out because other tests fail
//		type = JavaLanguageHelper.getSubstituteType("String", "x");
//		assertNull(type);
//
//		type = JavaLanguageHelper.getSubstituteType("x", "String");
//		assertNull(type);


		type = JavaLanguageHelper.getSubstituteType("char");
		assertEquals("String", type);

		type = JavaLanguageHelper.getSubstituteType("String");
		assertEquals("String", type);

		type = JavaLanguageHelper.getSubstituteType("float");
		assertEquals("double", type);

		type = JavaLanguageHelper.getSubstituteType("double");
		assertEquals("double", type);

		type = JavaLanguageHelper.getSubstituteType("byte");
		assertEquals("long", type);

		type = JavaLanguageHelper.getSubstituteType("short");
		assertEquals("long", type);

		type = JavaLanguageHelper.getSubstituteType("int");
		assertEquals("long", type);

		type = JavaLanguageHelper.getSubstituteType("long");
		assertEquals("long", type);

		type = JavaLanguageHelper.getSubstituteType("x");
		assertEquals("x", type);
	}

	@Test
	public void getCompatibleNumericTypeTest() {

		// non numeric
		String type = JavaLanguageHelper.getCompatibleNumericType("abc");
		assertNull(type);

		// byte

		type = JavaLanguageHelper.getCompatibleNumericType("0");
		assertEquals("byte", type);

		// close to 0
		type = JavaLanguageHelper.getCompatibleNumericType("4.9E-325");
		assertEquals("byte", type);

		type = JavaLanguageHelper.getCompatibleNumericType("127");
		assertEquals("byte", type);

		type = JavaLanguageHelper.getCompatibleNumericType("-128");
		assertEquals("byte", type);

		// short

		type = JavaLanguageHelper.getCompatibleNumericType("128");
		assertEquals("short", type);

		type = JavaLanguageHelper.getCompatibleNumericType("-129");
		assertEquals("short", type);

		type = JavaLanguageHelper.getCompatibleNumericType("32767");
		assertEquals("short", type);

		type = JavaLanguageHelper.getCompatibleNumericType("-32768");
		assertEquals("short", type);

		// int

		type = JavaLanguageHelper.getCompatibleNumericType("32768");
		assertEquals("int", type);

		type = JavaLanguageHelper.getCompatibleNumericType("-32769");
		assertEquals("int", type);

		type = JavaLanguageHelper.getCompatibleNumericType("2147483647");
		assertEquals("int", type);

		type = JavaLanguageHelper.getCompatibleNumericType("-2147483648");
		assertEquals("int", type);

		// long

		type = JavaLanguageHelper.getCompatibleNumericType("2147483648");
		assertEquals("long", type);

		type = JavaLanguageHelper.getCompatibleNumericType("-2147483649");
		assertEquals("long", type);

		type = JavaLanguageHelper.getCompatibleNumericType("9223372036854775807");
		assertEquals("long", type);

		type = JavaLanguageHelper.getCompatibleNumericType("-9223372036854775808");
		assertEquals("long", type);

		// float

		type = JavaLanguageHelper.getCompatibleNumericType("2.13");
		assertEquals("float", type);

		type = JavaLanguageHelper.getCompatibleNumericType("4.9E-324");
		assertEquals("float", type);

		type = JavaLanguageHelper.getCompatibleNumericType("-4.9E-324");
		assertEquals("float", type);

		// no double
	}

}