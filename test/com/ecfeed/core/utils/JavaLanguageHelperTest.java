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

		String errorMessage = JavaLanguageHelper.verifySeparatorsInText("abc");
		assertNull(errorMessage);

		errorMessage = JavaLanguageHelper.verifySeparatorsInText("a_b");
		assertNull(errorMessage);

		errorMessage = JavaLanguageHelper.verifySeparatorsInText("a b");
		assertNotNull(errorMessage);

		errorMessage = JavaLanguageHelper.verifySeparatorsInText("_ab");
		assertNull(errorMessage);
	}

	@Test
	public void isJavaKeywordExcludingTypes() {

		assertTrue(JavaLanguageHelper.isJavaKeywordExcludingTypes("switch"));
		assertFalse(JavaLanguageHelper.isJavaKeywordExcludingTypes("int"));
	}

	@Test
	public void isAllowedTypeTest() {

		assertTrue(JavaLanguageHelper.isAllowedType("double"));
		assertTrue(JavaLanguageHelper.isAllowedType("boolean"));
		assertTrue(JavaLanguageHelper.isAllowedType("String"));
		assertTrue(JavaLanguageHelper.isAllowedType("int"));

		assertFalse(JavaLanguageHelper.isAllowedType("Number"));
		assertFalse(JavaLanguageHelper.isAllowedType("Text"));
		assertFalse(JavaLanguageHelper.isAllowedType("Logical"));

		assertTrue(JavaLanguageHelper.isAllowedType("com.User"));
		assertFalse(JavaLanguageHelper.isAllowedType("Logical-1"));
		assertFalse(JavaLanguageHelper.isAllowedType("A^b"));
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
	public void getJavaKeywords() {

		String[] keywords = JavaLanguageHelper.getJavaKeywords();

		assertEquals(53, keywords.length);
	}

	@Test
	public void isValidTypeNameTest() {

        assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("boolean"));
        assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("String"));
        assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("int"));
        assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("Integer"));
		assertTrue(JavaLanguageHelper.isValidComplexTypeIdentifier("default.UserType"));
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

		value =	JavaLanguageHelper.getDefaultValue("x");
		assertEquals("VALUE", value);
	}

	@Test
	public void conditionallyConvertSpecialValueToNumericTest() {

		// byte

		String numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("byte", "MAX_VALUE");
		assertEquals("127", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("byte", "MIN_VALUE");
		assertEquals("-128", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("byte", "7");
		assertEquals("7", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("byte", "AB");
		assertEquals("AB", numeric);

		// short

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("short", "MAX_VALUE");
		assertEquals("32767", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("short", "MIN_VALUE");
		assertEquals("-32768", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("short", "32760");
		assertEquals("32760", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("short", "XY");
		assertEquals("XY", numeric);

		// int

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("int", "MAX_VALUE");
		assertEquals("2147483647", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("int", "MIN_VALUE");
		assertEquals("-2147483648", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("int", "XY");
		assertEquals("XY", numeric);

		// long

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("long", "MAX_VALUE");
		assertEquals("9223372036854775807", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("long", "MIN_VALUE");
		assertEquals("-9223372036854775808", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("long", "5");
		assertEquals("5", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("long", "ABC");
		assertEquals("ABC", numeric);

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

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("float", "-1.0");
		assertEquals("-1.0", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("float", "XY");
		assertEquals("XY", numeric);

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

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("double", "5.1");
		assertEquals("5.1", numeric);

		numeric = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("double", "XY");
		assertEquals("XY", numeric);

		// String

		assertEquals("X", JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("String", "X"));

		// char

		assertEquals("0", JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("char", "0"));

		// boolean

		assertEquals("false", JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric("boolean", "false"));
	}

	@Test
	public void getTypeNameTest() {

		assertEquals("String", JavaLanguageHelper.getTypeName("java.lang.String"));
		assertEquals("com.User", JavaLanguageHelper.getTypeName("com.User"));

		assertEquals("boolean", JavaLanguageHelper.getTypeName("boolean"));
		assertEquals("double", JavaLanguageHelper.getTypeName("double"));
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
	public void getTypeNamesTest() {

		assertEquals("String", JavaLanguageHelper.getStringTypeName());
		assertEquals("boolean", JavaLanguageHelper.getBooleanTypeName());
		assertEquals("boolean", JavaLanguageHelper.getBooleanTypeName());

		assertTrue(JavaLanguageHelper.isStringTypeName("String"));
		assertFalse(JavaLanguageHelper.isStringTypeName("int"));

		assertTrue(JavaLanguageHelper.isCharTypeName("char"));
		assertFalse(JavaLanguageHelper.isCharTypeName("String"));

		assertTrue(JavaLanguageHelper.isBooleanTypeName("boolean"));
		assertFalse(JavaLanguageHelper.isBooleanTypeName("String"));

		assertTrue(JavaLanguageHelper.isByteTypeName("byte"));
		assertFalse(JavaLanguageHelper.isByteTypeName("String"));

		assertTrue(JavaLanguageHelper.isIntTypeName("int"));
		assertFalse(JavaLanguageHelper.isIntTypeName("String"));

		assertTrue(JavaLanguageHelper.isShortTypeName("short"));
		assertFalse(JavaLanguageHelper.isShortTypeName("String"));

		assertTrue(JavaLanguageHelper.isLongTypeName("long"));
		assertFalse(JavaLanguageHelper.isLongTypeName("String"));

		assertTrue(JavaLanguageHelper.isFloatTypeName("float"));
		assertFalse(JavaLanguageHelper.isLongTypeName("String"));

		assertTrue(JavaLanguageHelper.isDoubleTypeName("double"));
		assertFalse(JavaLanguageHelper.isDoubleTypeName("String"));

		assertTrue(JavaLanguageHelper.isFloatingPointTypeName("float"));
		assertTrue(JavaLanguageHelper.isFloatingPointTypeName("double"));
		assertFalse(JavaLanguageHelper.isFloatingPointTypeName("String"));
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
	public void parseBooleanValueTest() {

		assertTrue(JavaLanguageHelper.parseBooleanValue("true"));
		assertFalse(JavaLanguageHelper.parseBooleanValue("false"));
		assertNull(JavaLanguageHelper.parseBooleanValue("1"));
	}

	@Test
	public void parseByteValueTest() {

		assertEquals(new Byte((byte)127), JavaLanguageHelper.parseByteValue("MAX_VALUE",  ERunMode.QUIET));
		assertEquals(new Byte((byte)-128), JavaLanguageHelper.parseByteValue("MIN_VALUE",  ERunMode.QUIET));
		assertEquals(new Byte((byte)1), JavaLanguageHelper.parseByteValue("1",  ERunMode.QUIET));
		assertEquals(null, JavaLanguageHelper.parseByteValue("a",  ERunMode.QUIET));
	}

	@Test
	public void parseDoubleValueTest() {

		assertEquals(new Double(1.7976931348623157E308), JavaLanguageHelper.parseDoubleValue("MAX_VALUE",  ERunMode.QUIET));
		assertEquals(new Double(4.9E-324), JavaLanguageHelper.parseDoubleValue("MIN_VALUE",  ERunMode.QUIET));
		assertEquals(new Double(1), JavaLanguageHelper.parseDoubleValue("1",  ERunMode.QUIET));
		assertEquals(null, JavaLanguageHelper.parseDoubleValue("a",  ERunMode.QUIET));
	}

	@Test
	public void parseFloatValueTest() {

		assertEquals(new Float(3.4028235E38), JavaLanguageHelper.parseFloatValue("MAX_VALUE",  ERunMode.QUIET));
		assertEquals(new Float(1.4E-45), JavaLanguageHelper.parseFloatValue("MIN_VALUE",  ERunMode.QUIET));
		assertEquals(new Float(1), JavaLanguageHelper.parseFloatValue("1",  ERunMode.QUIET));
		assertEquals(null, JavaLanguageHelper.parseFloatValue("a",  ERunMode.QUIET));
	}

	@Test
	public void parseIntValueTest() {

		assertEquals(new Integer(2147483647), JavaLanguageHelper.parseIntValue("MAX_VALUE",  ERunMode.QUIET));
		assertEquals(new Integer(-2147483648), JavaLanguageHelper.parseIntValue("MIN_VALUE",  ERunMode.QUIET));
		assertEquals(new Integer(1), JavaLanguageHelper.parseIntValue("1",  ERunMode.QUIET));
		assertEquals(null, JavaLanguageHelper.parseIntValue("a",  ERunMode.QUIET));
	}

	@Test
	public void parseLongValueTest() {

		assertEquals(new  Long(Long.MAX_VALUE), JavaLanguageHelper.parseLongValue("MAX_VALUE",  ERunMode.QUIET));
		assertEquals(new Long(Long.MIN_VALUE), JavaLanguageHelper.parseLongValue("MIN_VALUE",  ERunMode.QUIET));
		assertEquals(new Long(1), JavaLanguageHelper.parseLongValue("1",  ERunMode.QUIET));
		assertEquals(null, JavaLanguageHelper.parseLongValue("a",  ERunMode.QUIET));
	}

	@Test
	public void parseShorValueTest() {

		assertEquals(new  Short(Short.MAX_VALUE), JavaLanguageHelper.parseShortValue("MAX_VALUE",  ERunMode.QUIET));
		assertEquals(new Short(Short.MIN_VALUE), JavaLanguageHelper.parseShortValue("MIN_VALUE",  ERunMode.QUIET));
		assertEquals(new Short((short) 1), JavaLanguageHelper.parseShortValue("1",  ERunMode.QUIET));
		assertEquals(null, JavaLanguageHelper.parseShortValue("a",  ERunMode.QUIET));
	}

	@Test
	public void parseStringValueTest() {

		assertEquals("Abc", JavaLanguageHelper.parseStringValue("Abc"));
		assertNull(JavaLanguageHelper.parseStringValue("/null"));
	}

	@Test
	public void parseValueToStringTest() {

		String result = JavaLanguageHelper.parseJavaValueToString("a%b^c", "byte");
		assertNull(result);

		result = JavaLanguageHelper.parseJavaValueToString("11", "byte");
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

		type = JavaLanguageHelper.getSubstituteType("String", "x");
		assertEquals("x", type);

		type = JavaLanguageHelper.getSubstituteType("x", "String");
		assertEquals("x", type);

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

	@Test
	public void convertToByteTest() {

		assertEquals(new Byte(Byte.MAX_VALUE), JavaLanguageHelper.convertToByte("127"));
		assertEquals(new Byte(Byte.MIN_VALUE), JavaLanguageHelper.convertToByte("-128"));

		try {
			JavaLanguageHelper.convertToByte("128");
			fail();
		} catch (Exception e) {
		}

		try {
			JavaLanguageHelper.convertToByte("-129");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertToShortTest() {

		assertEquals(new Short(Short.MAX_VALUE), JavaLanguageHelper.convertToShort("32767"));
		assertEquals(new Short(Short.MIN_VALUE), JavaLanguageHelper.convertToShort("-32768"));

		try {
			JavaLanguageHelper.convertToShort("32768");
			fail();
		} catch (Exception e) {
		}

		try {
			JavaLanguageHelper.convertToShort("-32769");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertToIntegerTest() {

		assertEquals(new Integer(Integer.MAX_VALUE), JavaLanguageHelper.convertToInteger("2147483647"));
		assertEquals(new Integer(Integer.MIN_VALUE), JavaLanguageHelper.convertToInteger("-2147483648"));

		try {
			JavaLanguageHelper.convertToInteger("2147483648");
			fail();
		} catch (Exception e) {
		}

		try {
			JavaLanguageHelper.convertToInteger("-2147483650");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void convertToLongTest() {

		assertEquals(new Long(Long.MAX_VALUE), JavaLanguageHelper.convertToLong("9223372036854775807"));
		assertEquals(new Long(Long.MIN_VALUE), JavaLanguageHelper.convertToLong("-9223372036854775808"));

		assertEquals(new Long(Long.MAX_VALUE), JavaLanguageHelper.convertToLongDirectly("9223372036854775807"));
		assertEquals(new Long(Long.MIN_VALUE), JavaLanguageHelper.convertToLongDirectly("-9223372036854775808"));
	}

	@Test
    public void isNumericTypeLager() {

        assertTrue(JavaLanguageHelper.isNumericTypeLarger("short", "byte"));
        assertFalse(JavaLanguageHelper.isNumericTypeLarger("byte", "short"));

        assertTrue(JavaLanguageHelper.isNumericTypeLarger("int", "short"));
        assertFalse(JavaLanguageHelper.isNumericTypeLarger("short", "int"));

        assertTrue(JavaLanguageHelper.isNumericTypeLarger("long", "int"));
        assertFalse(JavaLanguageHelper.isNumericTypeLarger("int", "long"));

        assertTrue(JavaLanguageHelper.isNumericTypeLarger("float", "long"));
        assertFalse(JavaLanguageHelper.isNumericTypeLarger("long", "float"));

        assertTrue(JavaLanguageHelper.isNumericTypeLarger("double", "float"));
        assertFalse(JavaLanguageHelper.isNumericTypeLarger("float", "double"));

        try {
            JavaLanguageHelper.isNumericTypeLarger("String", "int");
            fail();
        } catch (Exception e) {
        }

        try {
            JavaLanguageHelper.isNumericTypeLarger("int", "String");
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void validateTypeTest() {

        assertNull(JavaLanguageHelper.validateBasicJavaType("byte"));
        assertNull(JavaLanguageHelper.validateBasicJavaType("short"));
        assertNull(JavaLanguageHelper.validateBasicJavaType("int"));
        assertNull(JavaLanguageHelper.validateBasicJavaType("long"));
        assertNull(JavaLanguageHelper.validateBasicJavaType("float"));
        assertNull(JavaLanguageHelper.validateBasicJavaType("double"));
	    assertNull(JavaLanguageHelper.validateBasicJavaType("boolean"));
        assertNull(JavaLanguageHelper.validateBasicJavaType("char"));
        assertNull(JavaLanguageHelper.validateBasicJavaType("String"));

        assertNotNull(JavaLanguageHelper.validateBasicJavaType("User"));
    }

	@Test
	public void createQualifiedName() {

		assertEquals("com.xx.User", JavaLanguageHelper.createQualifiedName("com.xx", "User"));
		assertEquals("User", JavaLanguageHelper.createQualifiedName(null, "User"));
		assertEquals("User", JavaLanguageHelper.createQualifiedName("", "User"));
	}

}