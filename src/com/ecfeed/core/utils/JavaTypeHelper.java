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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class JavaTypeHelper {

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

	public static Set<String> getSpecialValues(String typeName, ViewMode viewMode) {
		
		Set<String> items;
		
		if (viewMode == ViewMode.JAVA) {
			items = new LinkedHashSet<String>(getSpecialValues(typeName));
		} else {
			items = new LinkedHashSet<String>();
		}
		
		return items;
	}
	
	public static List<String> getSpecialValues(String typeName) {
		
		List<String> result = new ArrayList<String>();

		switch(typeName){
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			result.addAll(Arrays.asList(JavaTypeHelper.SPECIAL_VALUES_FOR_BOOLEAN));
			break;
		case JavaTypeHelper.TYPE_NAME_CHAR:
			result.addAll(Arrays.asList(JavaTypeHelper.DEFAULT_EXPECTED_CHAR_VALUE));
			break;
		case JavaTypeHelper.TYPE_NAME_BYTE:
		case JavaTypeHelper.TYPE_NAME_INT:
		case JavaTypeHelper.TYPE_NAME_LONG:
		case JavaTypeHelper.TYPE_NAME_SHORT:
			result.addAll(Arrays.asList(JavaTypeHelper.SPECIAL_VALUES_FOR_INTEGER));
			break;
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			result.addAll(Arrays.asList(JavaTypeHelper.SPECIAL_VALUES_FOR_FLOAT));
			break;
		case JavaTypeHelper.TYPE_NAME_STRING:
			result.addAll(Arrays.asList(com.ecfeed.core.utils.CommonConstants.STRING_SPECIAL_VALUES));
			break;
		default:
			break;
		}
		return result;
	}
	
	public static String getDefaultExpectedValue(String type) {
		switch(type){
		case JavaTypeHelper.TYPE_NAME_BYTE:
			return JavaTypeHelper.DEFAULT_EXPECTED_BYTE_VALUE;
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			return JavaTypeHelper.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case JavaTypeHelper.TYPE_NAME_CHAR:
			return JavaTypeHelper.DEFAULT_EXPECTED_CHAR_VALUE;
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
			return JavaTypeHelper.DEFAULT_EXPECTED_DOUBLE_VALUE;
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			return JavaTypeHelper.DEFAULT_EXPECTED_FLOAT_VALUE;
		case JavaTypeHelper.TYPE_NAME_INT:
			return JavaTypeHelper.DEFAULT_EXPECTED_INT_VALUE;
		case JavaTypeHelper.TYPE_NAME_LONG:
			return JavaTypeHelper.DEFAULT_EXPECTED_LONG_VALUE;
		case JavaTypeHelper.TYPE_NAME_SHORT:
			return JavaTypeHelper.DEFAULT_EXPECTED_SHORT_VALUE;
		case JavaTypeHelper.TYPE_NAME_STRING:
			return JavaTypeHelper.DEFAULT_EXPECTED_STRING_VALUE;
		default:
			return "VALUE";
		}
	}
	
	public static String convertConditionallySpecialValue(String typeName, String value) { // TODO SIMPLE-VIEW test
		
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

	public static String convertConditionallySpecialValueToByteTxt(String valueString) {
		
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
			return JavaTypeHelper.TYPE_NAME_BOOLEAN;
		}
		if (cannonicalName.equals(byte.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_BYTE;
		}
		if (cannonicalName.equals(char.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_CHAR;
		}
		if (cannonicalName.equals(double.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_DOUBLE;
		}
		if (cannonicalName.equals(float.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_FLOAT;
		}
		if (cannonicalName.equals(int.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_INT;
		}
		if (cannonicalName.equals(long.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_LONG;
		}
		if (cannonicalName.equals(short.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_SHORT;
		}
		if (cannonicalName.equals(String.class.getName())) {
			return JavaTypeHelper.TYPE_NAME_STRING;
		}

		return cannonicalName;
	}

	public static boolean isUserType(String typeName) {

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

		return JavaTypeHelper.TYPE_NAME_BOOLEAN;
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

	public static boolean isConvertibleToNumber(String text) {

		if (parseDoubleValue(text, ERunMode.QUIET) != null) {
			return true;
		}

		if (parseLongValue(text, ERunMode.QUIET) != null) {
			return true;
		}		

		return false;
	}

	public static Double convertNumericToDouble(
			String typeName, String value, ERunMode conversionMode) {

		if (isByteTypeName(typeName)) {
			return convertToDouble(parseByteValue(value, conversionMode));
		}
		if (isIntTypeName(typeName)) {
			return convertToDouble(parseIntValue(value, conversionMode));
		}
		if (isShortTypeName(typeName)) {
			return convertToDouble(parseShortValue(value, conversionMode));
		}
		if (isLongTypeName(typeName)) {
			return convertToDouble(parseLongValue(value, conversionMode));
		}		
		if (isFloatTypeName(typeName)) {
			return convertToDouble(parseFloatValue(value, conversionMode));
		}
		if (isDoubleTypeName(typeName)) {
			return convertToDouble(parseDoubleValue(value, conversionMode));
		}

		ExceptionHelper.reportRuntimeException("Invalid type in numeric conversion");
		return null;
	}

	private static <T> Double convertToDouble(T valueWithNull) {

		if (valueWithNull == null) {
			return null;
		}

		return (Double) valueWithNull;
	}

	private static Double convertToDouble(Float valueWithNull) {

		if (valueWithNull == null) {
			return null;
		}

		return new Double(valueWithNull);
	}	

	public static Object parseJavaType(String valueString, String typeName, ERunMode conversionMode) {

		if(typeName == null || valueString == null){
			return null;
		}

		switch(typeName){
		case TYPE_NAME_BOOLEAN:
			return parseBooleanValue(valueString);
		case TYPE_NAME_BYTE:
			return parseByteValue(valueString, conversionMode);
		case TYPE_NAME_CHAR:
			return parseCharValue(valueString);
		case TYPE_NAME_DOUBLE:
			return parseDoubleValue(valueString, conversionMode);
		case TYPE_NAME_FLOAT:
			return parseFloatValue(valueString, conversionMode);
		case TYPE_NAME_INT:
			return parseIntValue(valueString, conversionMode);
		case TYPE_NAME_LONG:
			return parseLongValue(valueString, conversionMode);
		case TYPE_NAME_SHORT:
			return parseShortValue(valueString, conversionMode);
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

	public static Byte parseByteValue(String valueString, ERunMode conversionMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Byte.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Byte.MIN_VALUE;
		}

		if (conversionMode == ERunMode.QUIET) {
			try {
				return Byte.parseByte(valueString);
			} catch(NumberFormatException e){
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

	public static Double parseDoubleValue(String valueString, ERunMode conversionMode) {

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

		if (conversionMode == ERunMode.QUIET) {
			try {
				return Double.parseDouble(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Double.parseDouble(valueString);
		}
	}

	public static Float parseFloatValue(String valueString, ERunMode conversionMode) {

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

		if (conversionMode == ERunMode.QUIET) {
			try {
				return Float.parseFloat(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Float.parseFloat(valueString);
		}
	}

	public static Integer parseIntValue(String valueString, ERunMode conversionMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Integer.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Integer.MIN_VALUE;
		}

		if (conversionMode == ERunMode.QUIET) {
			try {
				return Integer.parseInt(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Integer.parseInt(valueString);
		}
	}

	public static Long parseLongValue(String valueString, ERunMode conversionMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Long.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Long.MIN_VALUE;
		}

		if (conversionMode == ERunMode.QUIET) {
			try {
				return Long.parseLong(valueString);
			} catch(NumberFormatException e){
				return null;
			} 
		} else {
			return Long.parseLong(valueString);
		}
	}

	public static Short parseShortValue(String valueString, ERunMode conversionMode) {

		if(valueString.equals(SPECIAL_VALUE_MAX)){
			return Short.MAX_VALUE;
		}
		if(valueString.equals(SPECIAL_VALUE_MIN)){
			return Short.MIN_VALUE;
		}

		if (conversionMode == ERunMode.QUIET) {
			try {
				return Short.parseShort(valueString);
			} catch(NumberFormatException e){
				return null;
			}
		} else {
			return Short.parseShort(valueString);
		}
	}
	
	public static Number parseNumberValue(String valueString, String type, ERunMode conversionMode) {
		
		if (type.equals(TYPE_NAME_INT)) {
			if (conversionMode == ERunMode.QUIET) {
				try {
					return Integer.parseInt(valueString);
				} catch(NumberFormatException e) {
					return null;
				}
			} else {
				return Integer.parseInt(valueString);
			}
		}
		
		if (type.equals(TYPE_NAME_LONG)) {
			if (conversionMode == ERunMode.QUIET) {
				try {
					return Long.parseLong(valueString);
				} catch(NumberFormatException e) {
					return null;
				}
			} else {
				return Long.parseLong(valueString);
			}
		}
		
		if (type.equals(TYPE_NAME_DOUBLE)) {
			if (conversionMode == ERunMode.QUIET) {
				try {
					return Double.parseDouble(valueString);
				} catch(NumberFormatException e) {
					return null;
				}
			} else {
				return Double.parseDouble(valueString);
			}
		}
		
		return 0;
	}

	public static String parseStringValue(String valueString) {

		if(valueString.equals(VALUE_REPRESENTATION_NULL)){
			return null;
		}
		return valueString;
	}

	public static String convertValueString(String valueString, String typeName) {
		return parseJavaType(valueString, typeName, ERunMode.QUIET).toString();
	}

	public static String getSubstituteType(String typeName1, String typeName2) {

		if (typeName1 == null || typeName2 == null) {
			return null;
		}

		if (JavaTypeHelper.isBooleanTypeName(typeName1) || JavaTypeHelper.isBooleanTypeName(typeName2)) {
			return TYPE_NAME_BOOLEAN;
		}

		if (JavaTypeHelper.isTypeWithChars(typeName1) && JavaTypeHelper.isTypeWithChars(typeName2)) {
			return TYPE_NAME_STRING;
		}		

		if (JavaTypeHelper.isFloatingPointTypeName(typeName1) || JavaTypeHelper.isFloatingPointTypeName(typeName2)) {
			return TYPE_NAME_DOUBLE;
		}

		return TYPE_NAME_LONG;
	}

	public static String getSubstituteType(String typeName1) {

		if (typeName1 == null) {
			return null;
		}

		if (JavaTypeHelper.isTypeWithChars(typeName1)) {
			return TYPE_NAME_STRING;
		}		

		if (JavaTypeHelper.isFloatingPointTypeName(typeName1)) {
			return TYPE_NAME_DOUBLE;
		}

		if (JavaTypeHelper.isExtendedIntTypeName(typeName1)) {
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

	public static String parseToJavaView(String text) {
		String returnText = text;
		
		returnText = returnText.trim();
		
		if (JavaLanguageHelper.isJavaKeyword(returnText)) {
			return "_" + returnText;
		}
		
		returnText = returnText.replaceAll("_", RegexHelper.REGEX_SPECIAL_CHARACTER);
		
		if (returnText.matches("^[0-9].*")) {
			returnText = "_" + returnText;
		}
		
		while (returnText.contains("  ")) {
			returnText = returnText.replaceAll("  ", " ");
		}
		
		returnText = returnText.replaceAll(" ", "_");
		
		return returnText;
	}
	
	public static String getCompatibleNumericType(String value) { // TODO SIMPLE-VIEW tests
		
		try {
			StringHelper.convertToByte(value);
			return TYPE_NAME_BYTE;
		} catch (NumberFormatException e) {
		}
		
		try {
			StringHelper.convertToShort(value);
			return TYPE_NAME_SHORT;
		} catch (NumberFormatException e) {
		}
		
		try {
			StringHelper.convertToShort(value);
			return TYPE_NAME_SHORT;
		} catch (NumberFormatException e) {
		}

		try {
			StringHelper.convertToInteger(value);
			return TYPE_NAME_INT;
		} catch (NumberFormatException e) {
		}
		
		try {
			StringHelper.convertToLong(value);
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
	
	public static boolean isNumericTypeLarger(String numericTypeToCompare, String currentNumericType) {  // TODO SIMPLE-VIEW tests
		
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
}
