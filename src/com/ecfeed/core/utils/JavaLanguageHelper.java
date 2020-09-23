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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public final class JavaLanguageHelper {

	public static final String TYPE_NAME_BOOLEAN = "boolean";
	public static final String TYPE_NAME_BYTE = "byte";
	public static final String TYPE_NAME_CHAR = "char";
	public static final String TYPE_NAME_DOUBLE = "double";
	public static final String TYPE_NAME_FLOAT = "float";
	public static final String TYPE_NAME_INT = "int";
	public static final String TYPE_NAME_LONG = "long";
	public static final String TYPE_NAME_SHORT = "short";
	public static final String TYPE_NAME_STRING = "String";

	public static final String SPECIAL_VALUE_NULL = "/null";
	public static final String SPECIAL_VALUE_TRUE = "true";
	public static final String SPECIAL_VALUE_FALSE = "false";
	public static final String SPECIAL_VALUE_MIN = "MIN_VALUE";	
	public static final String SPECIAL_VALUE_MAX = "MAX_VALUE";
	public static final String SPECIAL_VALUE_MINUS_MIN = "-MIN_VALUE";	
	public static final String SPECIAL_VALUE_MINUS_MAX = "-MAX_VALUE";
	public static final String SPECIAL_VALUE_NEGATIVE_INF = "NEGATIVE_INFINITY";

	public static final String SPECIAL_VALUE_POSITIVE_INF = "POSITIVE_INFINITY";


	public static final String VALUE_REPRESENTATION_NULL = "/null";

	public static final String[] SPECIAL_VALUES_FOR_BOOLEAN = {
			SPECIAL_VALUE_TRUE, SPECIAL_VALUE_FALSE};

	public static final String[] SPECIAL_VALUES_FOR_INTEGER = {
			SPECIAL_VALUE_MIN, SPECIAL_VALUE_MAX};

	public static final String[] SPECIAL_VALUES_FOR_FLOAT = {
			SPECIAL_VALUE_NEGATIVE_INF, SPECIAL_VALUE_POSITIVE_INF,
			SPECIAL_VALUE_MIN, SPECIAL_VALUE_MAX,
			SPECIAL_VALUE_MINUS_MIN, SPECIAL_VALUE_MINUS_MAX };

	public static final String[] SPECIAL_VALUES_FOR_STRING = {SPECIAL_VALUE_NULL};

	public static final String[] SPECIAL_VALUES_FOR_SHORT = SPECIAL_VALUES_FOR_INTEGER;

	public static final String[] SPECIAL_VALUES_FOR_LONG = SPECIAL_VALUES_FOR_INTEGER;

	public static final String[] SPECIAL_VALUES_FOR_BYTE = SPECIAL_VALUES_FOR_INTEGER;

	public static final String[] SPECIAL_VALUES_FOR_DOUBLE = SPECIAL_VALUES_FOR_FLOAT;

	public static final String DEFAULT_EXPECTED_NUMERIC_VALUE = "0";
	public static final String DEFAULT_EXPECTED_FLOATING_POINT_VALUE = "0.0";
	public static final String DEFAULT_EXPECTED_BOOLEAN_VALUE = SPECIAL_VALUE_FALSE;
	public static final String DEFAULT_EXPECTED_CHAR_VALUE = "0";
	public static final String DEFAULT_EXPECTED_BYTE_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_DOUBLE_VALUE = DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
	public static final String DEFAULT_EXPECTED_FLOAT_VALUE = DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
	public static final String DEFAULT_EXPECTED_INT_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_LONG_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_SHORT_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_STRING_VALUE = "";
	public static final String DEFAULT_EXPECTED_ENUM_VALUE = "VALUE";

	private static final String[] SUPPORTED_JAVA_TYPES = new String[] {
			TYPE_NAME_INT,
			TYPE_NAME_BYTE,
			TYPE_NAME_SHORT,
			TYPE_NAME_LONG,
			TYPE_NAME_FLOAT,
			TYPE_NAME_DOUBLE,
			TYPE_NAME_STRING,
			TYPE_NAME_CHAR,
			TYPE_NAME_BOOLEAN
	};

	private static final String[] JAVA_KEYWORDS = new String[] {
			"abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do",
			"if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public",
			"throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char",
			"final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
			"native", "super", "while", "null", "true", "false" };

	private static final String[] JAVA_KEYWORDS_EXCLUDING_TYPES = new String[] {
			"abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "do",
			"if", "private", "this", "break", "implements", "protected", "throw", "else", "import", "public",
			"throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "try",
			"final", "interface", "static", "void", "class", "finally", "strictfp", "volatile", "const",
			"native", "super", "while", "null", "true", "false" };


	public static final String INVALID_JAVA_TYPE = "Invalid java type";

	public static String verifySeparators(String text) {

		if (text.contains(" ")) {
			return ("Spaces are not allowed in name.");
		}

		if (text.startsWith("_")) {
			return("Name should not begin with underline char.");
		}

		return null;
	}

	public static boolean isJavaKeywordExcludingTypes(String word) {
		return Arrays.asList(JAVA_KEYWORDS_EXCLUDING_TYPES).contains(word);
	}


	public static boolean isJavaKeyword(String word) {
		return Arrays.asList(JAVA_KEYWORDS).contains(word);
	}

	public static boolean isMatchWithJavaComplexIdenfifier(String value) {

		if (!value.matches(RegexHelper.REGEX_COMPLEX_JAVA_IDENTIFIER)) {
			return false;
		}

		return true;
	}

	public static boolean isMatchWithJavaSimpleIdenfifier(String value) {

		if (!value.matches(RegexHelper.REGEX_JAVA_IDENTIFIER)) {
			return false;
		}

		return true;
	}

	public static boolean isValidJavaIdentifier(String value) {

		if (!value.matches(RegexHelper.REGEX_JAVA_IDENTIFIER)) {
			return false;
		}

		if (isJavaKeyword(value)) {
			return false;
		}

		return true;
	}

	public static String[] getJavaKeywords() {

		return JAVA_KEYWORDS;
	}

	public static List<String> getSymbolicNamesOfSpecialValues(String typeName) {

		List<String> result = new ArrayList<String>();

		switch(typeName){
		case JavaLanguageHelper.TYPE_NAME_BOOLEAN:
			result.addAll(Arrays.asList(JavaLanguageHelper.SPECIAL_VALUES_FOR_BOOLEAN));
			break;
		case JavaLanguageHelper.TYPE_NAME_CHAR:
			result.addAll(Arrays.asList(JavaLanguageHelper.DEFAULT_EXPECTED_CHAR_VALUE));
			break;
		case JavaLanguageHelper.TYPE_NAME_BYTE:
		case JavaLanguageHelper.TYPE_NAME_INT:
		case JavaLanguageHelper.TYPE_NAME_LONG:
		case JavaLanguageHelper.TYPE_NAME_SHORT:
			result.addAll(Arrays.asList(JavaLanguageHelper.SPECIAL_VALUES_FOR_INTEGER));
			break;
		case JavaLanguageHelper.TYPE_NAME_DOUBLE:
		case JavaLanguageHelper.TYPE_NAME_FLOAT:
			result.addAll(Arrays.asList(JavaLanguageHelper.SPECIAL_VALUES_FOR_FLOAT));
			break;
		case JavaLanguageHelper.TYPE_NAME_STRING:
			result.addAll(Arrays.asList(com.ecfeed.core.utils.CommonConstants.STRING_SPECIAL_VALUES));
			break;
		default:
			break;
		}
		return result;
	}

	// TODO SIMPLE-VIEW use in function above
	public static List<String> getSymbolicNamesOfSpecialValuesForNonNumericTypes(String typeName) { 

		List<String> result = new ArrayList<String>();

		switch(typeName){
		case JavaLanguageHelper.TYPE_NAME_BOOLEAN:
			result.addAll(Arrays.asList(JavaLanguageHelper.SPECIAL_VALUES_FOR_BOOLEAN));
			break;
		case JavaLanguageHelper.TYPE_NAME_CHAR:
			result.addAll(Arrays.asList(JavaLanguageHelper.DEFAULT_EXPECTED_CHAR_VALUE));
			break;
		case JavaLanguageHelper.TYPE_NAME_STRING:
			result.addAll(Arrays.asList(com.ecfeed.core.utils.CommonConstants.STRING_SPECIAL_VALUES));
			break;
		default:
			break;
		}
		return result;
	}

	public static String getDefaultValue(String type) {
		switch(type){
		case JavaLanguageHelper.TYPE_NAME_BYTE:
			return JavaLanguageHelper.DEFAULT_EXPECTED_BYTE_VALUE;
		case JavaLanguageHelper.TYPE_NAME_BOOLEAN:
			return JavaLanguageHelper.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case JavaLanguageHelper.TYPE_NAME_CHAR:
			return JavaLanguageHelper.DEFAULT_EXPECTED_CHAR_VALUE;
		case JavaLanguageHelper.TYPE_NAME_DOUBLE:
			return JavaLanguageHelper.DEFAULT_EXPECTED_DOUBLE_VALUE;
		case JavaLanguageHelper.TYPE_NAME_FLOAT:
			return JavaLanguageHelper.DEFAULT_EXPECTED_FLOAT_VALUE;
		case JavaLanguageHelper.TYPE_NAME_INT:
			return JavaLanguageHelper.DEFAULT_EXPECTED_INT_VALUE;
		case JavaLanguageHelper.TYPE_NAME_LONG:
			return JavaLanguageHelper.DEFAULT_EXPECTED_LONG_VALUE;
		case JavaLanguageHelper.TYPE_NAME_SHORT:
			return JavaLanguageHelper.DEFAULT_EXPECTED_SHORT_VALUE;
		case JavaLanguageHelper.TYPE_NAME_STRING:
			return JavaLanguageHelper.DEFAULT_EXPECTED_STRING_VALUE;
		default:
			ExceptionHelper.reportRuntimeException("Invalid type. Cannot get default value.");
			return null;
		}
	}

	public static String conditionallyConvertSpecialValueToNumeric(String typeName, String value) {

		if  (!isNumericTypeName(typeName)) {
			return value; // TODO SIMPLE-VIEW unit tests for e.g boolean String
		}

		if (isByteTypeName(typeName)) {
			return convertConditionallySpecialValueToByteTxt(value);
		}

		if (isShortTypeName(typeName)) {
			return convertConditionallySpecialValueToShortTxt(value);
		}

		if (isIntTypeName(typeName)) {
			return convertConditionallySpecialValueToIntTxt(value);
		}

		if (isLongTypeName(typeName)) {
			return convertConditionallySpecialValueToLongTxt(value);
		}

		if (isFloatTypeName(typeName)) {
			return convertConditionallySpecialValueToFloatTxt(value);
		}

		if (isDoubleTypeName(typeName)) {
			return convertConditionallySpecialValueToDoubleTxt(value);
		}

		if (isTypeWithChars(typeName)) {
			return value;
		}

		if (isBooleanTypeName(typeName)) {
			return value;
		}

		ExceptionHelper.reportRuntimeException("Conversion of special value to numeric - type not supported.");
		return null;
	}

	private static String convertConditionallySpecialValueToDoubleTxt(String valueString) {

		if (valueString.equals(SPECIAL_VALUE_MIN)) {
			return (Double.MIN_VALUE + "");
		} 

		if (valueString.equals(SPECIAL_VALUE_MAX)) {
			return (Double.MAX_VALUE + "");
		} 

		if (valueString.equals(SPECIAL_VALUE_MINUS_MIN)) {
			return ("-" + Double.MIN_VALUE);
		} 

		if (valueString.equals(SPECIAL_VALUE_MINUS_MAX)) {
			return ("-" + Double.MAX_VALUE);
		} 

		if (valueString.equals(SPECIAL_VALUE_POSITIVE_INF)) {
			return SPECIAL_VALUE_POSITIVE_INF;
		} 

		if (valueString.equals(SPECIAL_VALUE_NEGATIVE_INF)) {
			return SPECIAL_VALUE_NEGATIVE_INF;
		} 

		return valueString;
	}

	private static String convertConditionallySpecialValueToFloatTxt(String valueString) {

		if (valueString.equals(SPECIAL_VALUE_MIN)) {
			return (Float.MIN_VALUE + "");
		} 

		if (valueString.equals(SPECIAL_VALUE_MAX)) {
			return (Float.MAX_VALUE + "");
		} 

		if (valueString.equals(SPECIAL_VALUE_MINUS_MIN)) {
			return ("-" + Float.MIN_VALUE);
		} 

		if (valueString.equals(SPECIAL_VALUE_MINUS_MAX)) {
			return ("-" + Float.MAX_VALUE);
		} 

		if (valueString.equals(SPECIAL_VALUE_POSITIVE_INF)) {
			return SPECIAL_VALUE_POSITIVE_INF;
		} 

		if (valueString.equals(SPECIAL_VALUE_NEGATIVE_INF)) {
			return SPECIAL_VALUE_NEGATIVE_INF;
		} 

		return valueString;
	}

	private static String convertConditionallySpecialValueToLongTxt(String valueString) {

		if (valueString.equals(SPECIAL_VALUE_MIN)) {
			return Long.MIN_VALUE + "";
		} 

		if (valueString.equals(SPECIAL_VALUE_MAX)) {
			return Long.MAX_VALUE + "";
		} 

		return null;
	}

	private static String convertConditionallySpecialValueToIntTxt(String valueString) {

		if (valueString.equals(SPECIAL_VALUE_MIN)) {
			return Integer.MIN_VALUE + "";
		} 

		if (valueString.equals(SPECIAL_VALUE_MAX)) {
			return Integer.MAX_VALUE + "";
		} 

		return valueString;
	}

	private static String convertConditionallySpecialValueToShortTxt(String valueString) {

		if (valueString.equals(SPECIAL_VALUE_MIN)) {
			return Short.MIN_VALUE + "";
		} 

		if (valueString.equals(SPECIAL_VALUE_MAX)) {
			return Short.MAX_VALUE + "";
		} 

		return valueString;
	}

	private static String convertConditionallySpecialValueToByteTxt(String valueString) {

		if (valueString.equals(SPECIAL_VALUE_MIN)) {
			return Byte.MIN_VALUE + "";
		} 

		if (valueString.equals(SPECIAL_VALUE_MAX)) {
			return Byte.MAX_VALUE + "";
		} 

		return valueString;
	}

	public static String getTypeName(String cannonicalName) {

		if (cannonicalName.equals(boolean.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_BOOLEAN;
		}
		if (cannonicalName.equals(byte.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_BYTE;
		}
		if (cannonicalName.equals(char.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_CHAR;
		}
		if (cannonicalName.equals(double.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_DOUBLE;
		}
		if (cannonicalName.equals(float.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_FLOAT;
		}
		if (cannonicalName.equals(int.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_INT;
		}
		if (cannonicalName.equals(long.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_LONG;
		}
		if (cannonicalName.equals(short.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_SHORT;
		}
		if (cannonicalName.equals(String.class.getName())) {
			return JavaLanguageHelper.TYPE_NAME_STRING;
		}

		return cannonicalName;
	}

	public static boolean isUserType(String typeName) {

		if (typeName == null) {
			return false;
		}

		if (isJavaKeyword(typeName)) {
			return false;
		}

		if (isJavaType(typeName)) {
			return false;
		}
		return true;
	}

	public static boolean isJavaType(String typeName) {

		if (typeName == null) {
			return false;
		}

		return Arrays.asList(SUPPORTED_JAVA_TYPES).contains(typeName);
	}

	public static boolean isValidComplexTypeIdentifier(String name) {

		if (name == null) {
			return false;
		}

		if (!isMatchWithJavaComplexIdenfifier(name)) {
			return false;
		}

		StringTokenizer tokenizer = new StringTokenizer(name, ".");

		while (tokenizer.hasMoreTokens()) {

			String segment = tokenizer.nextToken();

			if (isJavaKeywordExcludingTypes(segment)) {
				return false;
			}

			if (!isMatchWithJavaSimpleIdenfifier(segment)) {
				return false;
			}
		}

		return true;
	}

	public static String[] getSupportedJavaTypes() {
		return SUPPORTED_JAVA_TYPES;
	}

	public static String getStringTypeName() {
		return TYPE_NAME_STRING;
	}

	public static boolean hasLimitedValuesSet(String type) {

		if (isBooleanTypeName(type)) {
			return true;
		}

		return !isJavaType(type);
	}

	public static String getBooleanTypeName() {

		return JavaLanguageHelper.TYPE_NAME_BOOLEAN;
	}

	public static boolean isStringTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_STRING)) {
			return true;
		}
		return false;
	}

	public static boolean isCharTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_CHAR)) {
			return true;
		}
		return false;
	}	

	public static boolean isBooleanTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_BOOLEAN)) {
			return true;
		}
		return false;
	}		

	public static boolean isByteTypeName(String typeName) {

		if (typeName == null) {
			ExceptionHelper.reportRuntimeException("Empty type name is not allowed.");
		}

		if (typeName.equals(TYPE_NAME_BYTE)) {
			return true;
		}
		return false;
	}	

	public static boolean isIntTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_INT)) {
			return true;
		}
		return false;
	}	

	public static boolean isShortTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_SHORT)) {
			return true;
		}
		return false;
	}	

	public static boolean isLongTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_LONG)) {
			return true;
		}
		return false;
	}	

	public static boolean isFloatTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_FLOAT)) {
			return true;
		}
		return false;
	}	

	public static boolean isDoubleTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_DOUBLE)) {
			return true;
		}
		return false;
	}	

	public static boolean isFloatingPointTypeName(String typeName) {

		if (isFloatTypeName(typeName)) {
			return true;
		}

		if (isDoubleTypeName(typeName)) {
			return true;
		}

		return false;
	}

	public static boolean isExtendedIntTypeName(String typeName) {

		if (isByteTypeName(typeName)) {
			return true;
		}
		if (isIntTypeName(typeName)) {
			return true;
		}
		if (isLongTypeName(typeName)) {
			return true;
		}		
		if (isShortTypeName(typeName)) {
			return true;
		}
		return false;
	}

	public static boolean isNumericTypeName(String typeName) {

		if (isExtendedIntTypeName(typeName)) {
			return true;
		}
		if (isFloatTypeName(typeName)) {
			return true;
		}
		if (isDoubleTypeName(typeName)) {
			return true;
		}
		return false;
	}

	public static boolean isTypeWithChars(String typeName) {

		if (isCharTypeName(typeName)) {
			return true;
		}
		if (isStringTypeName(typeName)) {
			return true;
		}

		return false;
	}

	public static boolean isTypeComparableForLessGreater(String typeName) {

		if (isNumericTypeName(typeName)) {
			return true;
		}
		if (isTypeWithChars(typeName)) {
			return true;
		}
		return false;
	}

	//	public static boolean isConvertibleToNumber(String text) {
	//
	//		if (parseDoubleValue(text, ERunMode.QUIET) != null) {
	//			return true;
	//		}
	//
	//		if (parseLongValue(text, ERunMode.QUIET) != null) {
	//			return true;
	//		}
	//
	//		return false;
	//	}

	public static Double convertNumericToDouble(
			String typeName, String value, ERunMode runMode) {

		if (isByteTypeName(typeName)) {
			return convertToDouble(parseByteValue(value, runMode));
		}
		if (isIntTypeName(typeName)) {
			return convertToDouble(parseIntValue(value, runMode));
		}
		if (isShortTypeName(typeName)) {
			return convertToDouble(parseShortValue(value, runMode));
		}
		if (isLongTypeName(typeName)) {
			return convertToDouble(parseLongValue(value, runMode));
		}
		if (isFloatTypeName(typeName)) {
			return convertToDouble(parseFloatValue(value, runMode));
		}
		if (isDoubleTypeName(typeName)) {
			return convertToDouble(parseDoubleValue(value, runMode));
		}

		if (runMode == ERunMode.WITH_EXCEPTION) {
			ExceptionHelper.reportRuntimeException("Invalid type in numeric conversion");
		}

		return null;
	}

	private static Double convertToDouble(Byte valueWithNull) {

		if (valueWithNull == null) {
			return null;
		}

		return new Double(valueWithNull);
	}

	private static Double convertToDouble(Short valueWithNull) {

		if (valueWithNull == null) {
			return null;
		}

		return new Double(valueWithNull);
	}

	private static Double convertToDouble(Integer valueWithNull) {

		if (valueWithNull == null) {
			return null;
		}

		return new Double(valueWithNull);
	}

	private static Double convertToDouble(Long valueWithNull) {

		if (valueWithNull == null) {
			return null;
		}

		return new Double(valueWithNull);
	}

	private static Double convertToDouble(Float valueWithNull) {

		if (valueWithNull == null) {
			return null;
		}

		return new Double(valueWithNull);
	}

	private static Double convertToDouble(Double valueWithNull) {

		return valueWithNull;
	}

	public static Number parseValueToNumber(String valueString, String type, ERunMode runMode) {

		if  (type == null || valueString == null) {
			return null;
		}

		if (!isNumericTypeName(type)) {
			return null;
		}

		switch(type){
		case TYPE_NAME_BYTE:
			return parseByteValue(valueString, runMode);
		case TYPE_NAME_DOUBLE:
			return parseDoubleValue(valueString, runMode);
		case TYPE_NAME_FLOAT:
			return parseFloatValue(valueString, runMode);
		case TYPE_NAME_INT:
			return parseIntValue(valueString, runMode);
		case TYPE_NAME_LONG:
			return parseLongValue(valueString, runMode);
		case TYPE_NAME_SHORT:
			return parseShortValue(valueString, runMode);
		default:
			return null;
		}
	}

	public static Object parseJavaValueToObject(String valueString, String typeName, ERunMode runMode) {

		if(typeName == null || valueString == null){
			return null;
		}

		switch(typeName){
		case TYPE_NAME_BOOLEAN:
			return parseBooleanValue(valueString);
		case TYPE_NAME_BYTE:
			return parseByteValue(valueString, runMode);
		case TYPE_NAME_CHAR:
			return parseCharValue(valueString);
		case TYPE_NAME_DOUBLE:
			return parseDoubleValue(valueString, runMode);
		case TYPE_NAME_FLOAT:
			return parseFloatValue(valueString, runMode);
		case TYPE_NAME_INT:
			return parseIntValue(valueString, runMode);
		case TYPE_NAME_LONG:
			return parseLongValue(valueString, runMode);
		case TYPE_NAME_SHORT:
			return parseShortValue(valueString, runMode);
		case TYPE_NAME_STRING:
			return parseStringValue(valueString);
		default:
			return null;
		}
	}

	public static Boolean parseBooleanValue(String valueString) {

		if(valueString.toLowerCase().equals(SPECIAL_VALUE_TRUE.toLowerCase())){
			return true;
		}
		if(valueString.toLowerCase().equals(SPECIAL_VALUE_FALSE.toLowerCase())){
			return false;
		}
		return null;
	}

	public static Byte parseByteValue(String valueString, ERunMode runMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Byte.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Byte.MIN_VALUE;
		}

		if (runMode == ERunMode.QUIET) {
			try {
				return Byte.parseByte(valueString);
			} catch(Exception e){
				return null;
			}
		} else {
			return Byte.parseByte(valueString);
		}
	}

	public static Character parseCharValue(String valueString) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Character.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Character.MIN_VALUE;
		}
		if (valueString.charAt(0) == '\\') {
			return new Character((char)Integer.parseInt(valueString.substring(1)));
		} else if (valueString.length() == 1) {
			return valueString.charAt(0);
		}
		return null;
	}

	public static Double parseDoubleValue(String valueString, ERunMode runMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Double.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MINUS_MAX)){
			return (-1)*Double.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Double.MIN_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MINUS_MIN)){
			return (-1)*Double.MIN_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_POSITIVE_INF)){
			return Double.POSITIVE_INFINITY;
		}
		if(valueString.equals(SPECIAL_VALUE_NEGATIVE_INF)){
			return Double.NEGATIVE_INFINITY;
		}

		if (runMode == ERunMode.QUIET) {
			try {
				return Double.parseDouble(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Double.parseDouble(valueString);
		}
	}

	public static Float parseFloatValue(String valueString, ERunMode runMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Float.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MINUS_MAX)){
			return (-1)*Float.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Float.MIN_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MINUS_MIN)){
			return (-1)*Float.MIN_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_POSITIVE_INF)){
			return Float.POSITIVE_INFINITY;
		}
		if(valueString.equals(SPECIAL_VALUE_NEGATIVE_INF)){
			return Float.NEGATIVE_INFINITY;
		}

		if (runMode == ERunMode.QUIET) {
			try {
				return Float.parseFloat(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Float.parseFloat(valueString);
		}
	}

	public static Integer parseIntValue(String valueString, ERunMode runMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Integer.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Integer.MIN_VALUE;
		}

		if (runMode == ERunMode.QUIET) {
			try {
				return Integer.parseInt(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Integer.parseInt(valueString);
		}
	}

	public static Long parseLongValue(String valueString, ERunMode runMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Long.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Long.MIN_VALUE;
		}

		if (runMode == ERunMode.QUIET) {
			try {
				return Long.parseLong(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Long.parseLong(valueString);
		}
	}

	public static Short parseShortValue(String valueString, ERunMode runMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Short.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Short.MIN_VALUE;
		}

		if (runMode == ERunMode.QUIET) {
			try {
				return Short.parseShort(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Short.parseShort(valueString);
		}
	}

	public static String parseStringValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_NULL)){
			return null;
		}
		return valueString;
	}

	public static String parseJavaValueToString(String valueString, String typeName) {

		final Object object = parseJavaValueToObject(valueString, typeName, ERunMode.QUIET);

		if (object == null) {
			return null;
		}

		return object.toString();
	}

	public static String getSubstituteType(String typeName1, String typeName2) {

		if (typeName1 == null) {
			return null;
		}

		if (typeName2 == null) {
			return null;
		}

		if (!isJavaType(typeName1)) {
			return null;
		}

		if (!isJavaType(typeName2)) {
			return null;
		}

		if (JavaLanguageHelper.isBooleanTypeName(typeName1) || JavaLanguageHelper.isBooleanTypeName(typeName2)) {
			return TYPE_NAME_BOOLEAN;
		}

		if (JavaLanguageHelper.isTypeWithChars(typeName1) && JavaLanguageHelper.isTypeWithChars(typeName2)) {
			return TYPE_NAME_STRING;
		}		

		if (JavaLanguageHelper.isFloatingPointTypeName(typeName1) || JavaLanguageHelper.isFloatingPointTypeName(typeName2)) {
			return TYPE_NAME_DOUBLE;
		}

		return TYPE_NAME_LONG;
	}

	public static String getSubstituteType(String typeName1) {

		if (typeName1 == null) {
			return null;
		}

		if (JavaLanguageHelper.isTypeWithChars(typeName1)) {
			return TYPE_NAME_STRING;
		}		

		if (JavaLanguageHelper.isFloatingPointTypeName(typeName1)) {
			return TYPE_NAME_DOUBLE;
		}

		if (JavaLanguageHelper.isExtendedIntTypeName(typeName1)) {
			return TYPE_NAME_LONG;
		}

		return typeName1;
	}

	public static JustifyType getJustifyType(String typeName) {

		if (!isJavaType(typeName)) {
			return JustifyType.LEFT;
		}

		if (isNumericTypeName(typeName)) {
			return JustifyType.RIGHT;
		}

		return JustifyType.LEFT;
	}

	public static String getCompatibleNumericType(String value) {

		try {
			convertToByte(value);
			return TYPE_NAME_BYTE;
		} catch (NumberFormatException e) {
		}

		try {
			convertToShort(value);
			return TYPE_NAME_SHORT;
		} catch (NumberFormatException e) {
		}

		try {
			convertToInteger(value);
			return TYPE_NAME_INT;
		} catch (NumberFormatException e) {
		}

		try {
			convertToLong(value);
			return TYPE_NAME_LONG;
		} catch (NumberFormatException e) {
		}

		try {
			Float.parseFloat(value);
			return TYPE_NAME_FLOAT;
		} catch (NumberFormatException e) {
		}

		try {
			Double.parseDouble(value);
			return TYPE_NAME_DOUBLE;
		} catch (NumberFormatException e) {
		}

		return null;
	}

	public static Short convertToShort(String str) throws NumberFormatException {

		Long result = convertToLong(str);

		Long maxIntValue = new Long(Short.MAX_VALUE);
		if (result > maxIntValue) {
			throw new NumberFormatException();
		}

		Long minIntValue = new Long(Short.MIN_VALUE);
		if (result < minIntValue) {
			throw new NumberFormatException();
		}		

		return result.shortValue(); 
	}	

	public static Integer convertToInteger(String str) throws NumberFormatException {

		Long result = convertToLong(str);

		Long maxIntValue = new Long(Integer.MAX_VALUE);
		if (result > maxIntValue) {
			throw new NumberFormatException();
		}

		Long minIntValue = new Long(Integer.MIN_VALUE);
		if (result < minIntValue) {
			throw new NumberFormatException();
		}		

		return result.intValue(); 
	}

	public static Byte convertToByte(String str) throws NumberFormatException {

		Long result = convertToLong(str);

		Long maxIntValue = new Long(Byte.MAX_VALUE);
		if (result > maxIntValue) {
			throw new NumberFormatException();
		}

		Long minIntValue = new Long(Byte.MIN_VALUE);
		if (result < minIntValue) {
			throw new NumberFormatException();
		}		

		return result.byteValue(); 
	}

	public static Long convertToLong(String str) throws NumberFormatException {

		Long result = convertToLongDirectly(str);

		if (result != null) {
			return result;
		}

		return convertToLongViaDouble(str);
	}

	private static Long convertToLongViaDouble(String str) throws NumberFormatException {

		Double dblResult = null;

		dblResult = Double.parseDouble(str);

		if (dblResult != Math.floor(dblResult)) {
			throw new NumberFormatException();
		}

		return dblResult.longValue();
	}

	public static Long convertToLongDirectly(String str) {

		Long result = null;

		try {
			result = Long.parseLong(str);
		} catch (NumberFormatException e){
			return null;
		}

		return result;
	}

	public static boolean isNumericTypeLarger(String numericTypeToCompare, String currentNumericType) {

		if (!isNumericTypeName(currentNumericType)) {
			ExceptionHelper.reportRuntimeException("Current type is not numeric.");
		}

		if (!isNumericTypeName(numericTypeToCompare)) {
			ExceptionHelper.reportRuntimeException("Type to compare is not numeric.");
		}

		if (StringHelper.isEqual(currentNumericType, numericTypeToCompare)) {
			return false;
		}

		if (isByteTypeName(currentNumericType)) {
			return true;
		}

		if (isShortTypeName(currentNumericType)) {

			if (isByteTypeName(numericTypeToCompare)) {
				return false;
			}

			return true;
		}

		if (isIntTypeName(currentNumericType)) {

			if (isByteTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isShortTypeName(numericTypeToCompare)) {
				return false;
			}

			return true;
		}

		if (isLongTypeName(currentNumericType)) {

			if (isByteTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isShortTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isIntTypeName(numericTypeToCompare)) {
				return false;
			}

			return true;
		}

		if (isFloatTypeName(currentNumericType)) {

			if (isByteTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isShortTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isIntTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isLongTypeName(numericTypeToCompare)) {
				return false;
			}

			return true;
		}


		if (isDoubleTypeName(currentNumericType)) {

			if (isByteTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isShortTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isIntTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isLongTypeName(numericTypeToCompare)) {
				return false;
			}

			if (isFloatTypeName(numericTypeToCompare)) {
				return false;
			}

			return true;
		}

		return true;
	}

	public static String validateBasicJavaType(String type) {

		if (StringHelper.isEqual(type, TYPE_NAME_BOOLEAN)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_BYTE)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_CHAR)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_DOUBLE)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_FLOAT)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_INT)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_LONG)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_SHORT)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_STRING)) {
			return null;
		}

		return INVALID_JAVA_TYPE + ": " + type + ".";
	}

	//	public static String parseToJavaView(String text) {
	//		String returnText = text;
	//
	//		returnText = returnText.trim();
	//
	//		if (isJavaKeyword(returnText)) {
	//			return "_" + returnText;
	//		}
	//
	//		returnText = returnText.replaceAll("_", RegexHelper.REGEX_SPECIAL_CHARACTER);
	//
	//		if (returnText.matches("^[0-9].*")) {
	//			returnText = "_" + returnText;
	//		}
	//
	//		while (returnText.contains("  ")) {
	//			returnText = returnText.replaceAll("  ", " ");
	//		}
	//
	//		returnText = returnText.replaceAll(" ", "_");
	//
	//		return returnText;
	//	}

	//	public static List<String> getEnumValuesNames(URLClassLoader loader, String enumTypeName) {
	//		List<String> values = new ArrayList<String>();
	//
	//		try {
	//			Class<?> enumType = loader.loadClass(enumTypeName);
	//
	//			if(enumType != null && enumType.isEnum()){
	//				for (Object object: enumType.getEnumConstants()) {
	//					values.add(((Enum<?>)object).name());
	//				}
	//			}
	//		} catch (ClassNotFoundException e) {
	//		}
	//
	//		return values;
	//	}

}
