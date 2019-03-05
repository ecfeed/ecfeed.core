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

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;

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
	
	public static void convertTypeSimpleToJava(AbstractParameterNode node) {
		if (isJavaType(node.getType())) {
			return;
		}
		
		Set<ChoiceNode> choiceNodeSet = node.getAllChoices();
		
		Optional<String> nodeTypeHidden = node.getSuggestedType();
		node.setSuggestedType(null);
		
		if (nodeTypeHidden.isPresent()) {
			String newType = isConvertableToTypeHidden(choiceNodeSet, nodeTypeHidden.get());
			
			if (!newType.equals("")) {
				switch (newType) {
					case TYPE_NAME_BYTE : 
						convertSpecialChoicesSimpleToJavaByte(node);
						break;
					case TYPE_NAME_SHORT : 
						convertSpecialChoicesSimpleToJavaShort(node);
						break;
					case TYPE_NAME_INT : 
						convertSpecialChoicesSimpleToJavaInt(node);
						break;
					case TYPE_NAME_LONG : 
						convertSpecialChoicesSimpleToJavaLong(node);
						break;
					case TYPE_NAME_FLOAT : 
						convertSpecialChoicesSimpleToJavaFloat(node);
						break;
					case TYPE_NAME_DOUBLE : 
						convertSpecialChoicesSimpleToJavaDouble(node);
						break;
				}
				
				node.setType(newType);
				return;
			}		
		}
		
		if (node.getType().equals(SimpleTypeHelper.TYPE_NAME_NUMBER)) {
			if (choiceNodeSet.size() == 0) {
				node.setType(TYPE_NAME_INT);
				return;
			}
			
			if (!isConvertableToInt(choiceNodeSet).equals("")) {
				node.setType(TYPE_NAME_INT);
				return;
			}
			if (!isConvertableToLong(choiceNodeSet).equals("")) {
				node.setType(TYPE_NAME_LONG);
				return;
			}
			if (!isConvertableToDouble(choiceNodeSet).equals("")) {
				node.setType(TYPE_NAME_DOUBLE);
				return;
			}
		}
		
		if (node.getType().equals(SimpleTypeHelper.TYPE_NAME_TEXT)) {
			node.setType(TYPE_NAME_STRING);
			return;
		}
		
		if (node.getType().equals(SimpleTypeHelper.TYPE_NAME_LOGICAL)) {
			node.setType(TYPE_NAME_BOOLEAN);
			return;
		}
		
		node.setType(TYPE_NAME_STRING);
	}
	
	private static String isConvertableToTypeHidden(Set<ChoiceNode> choiceNodeSet, String nodeTypeHidden) {
		switch(nodeTypeHidden) {
			case TYPE_NAME_BYTE : return isConvertableToByte(choiceNodeSet);
			case TYPE_NAME_SHORT : return isConvertableToShort(choiceNodeSet);
			case TYPE_NAME_INT : return isConvertableToInt(choiceNodeSet);
			case TYPE_NAME_LONG : return isConvertableToLong(choiceNodeSet);
			case TYPE_NAME_FLOAT : return isConvertableToFloat(choiceNodeSet);
			case TYPE_NAME_DOUBLE : return isConvertableToDouble(choiceNodeSet);
			case TYPE_NAME_CHAR : return isConvertableToChar(choiceNodeSet);
			case TYPE_NAME_BOOLEAN : return isConvertableToBoolean(choiceNodeSet);
			case TYPE_NAME_STRING : return TYPE_NAME_STRING;
			default : return "";
		}
	}
	
	private static String isConvertableToByte(Set<ChoiceNode> choiceNodeSet) {
		for (ChoiceNode choice : choiceNodeSet) {
			try {
				Byte.parseByte(choice.getValueString());
			} catch (NumberFormatException e) {
				return "";
			}
		}
		
		return TYPE_NAME_BYTE;
	}
	
	private static String isConvertableToShort(Set<ChoiceNode> choiceNodeSet) {
		for (ChoiceNode choice : choiceNodeSet) {
			try {
				Short.parseShort(choice.getValueString());
			} catch (NumberFormatException e) {
				return "";
			}
		}
		
		return TYPE_NAME_SHORT;
	}
	
	private static String isConvertableToInt(Set<ChoiceNode> choiceNodeSet) {
		for (ChoiceNode choice : choiceNodeSet) {
			try {
				Integer.parseInt(choice.getValueString());
			} catch (NumberFormatException e) {
				return "";
			}
		}
		
		return TYPE_NAME_INT;
	}
	
	private static String isConvertableToLong(Set<ChoiceNode> choiceNodeSet) {
		for (ChoiceNode choice : choiceNodeSet) {
			try {
				Long.parseLong(choice.getValueString());
			} catch (NumberFormatException e) {
				return "";
			}
		}
		
		return TYPE_NAME_LONG;
	}
	
	private static String isConvertableToFloat(Set<ChoiceNode> choiceNodeSet) {
		for (ChoiceNode choice : choiceNodeSet) {
			String choiceValue = choice.getValueString();
			
			try {
				if (choiceValue.equals(SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE) || choiceValue.equals(SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE)) {
					continue;
				}
				
				Float.parseFloat(choiceValue);
			} catch (NumberFormatException e) {
				return "";
			}
		}
		
		return TYPE_NAME_FLOAT;
	}
	
	private static String isConvertableToDouble(Set<ChoiceNode> choiceNodeSet) {
		for (ChoiceNode choice : choiceNodeSet) {
			String choiceValue = choice.getValueString();
			
			try {
				if (choiceValue.equals(SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE) || choiceValue.equals(SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE)) {
					continue;
				}
				
				Double.parseDouble(choice.getValueString());
			} catch (NumberFormatException e) {
				return "";
			}
		}
		
		return TYPE_NAME_DOUBLE;
	}
	
	private static String isConvertableToChar(Set<ChoiceNode> choiceNodeSet) {
		for (ChoiceNode choice : choiceNodeSet) {
			if (choice.getValueString().length() > 1) {
				return "";
			}
		}
		
		return TYPE_NAME_CHAR;
	}
	
	private static String isConvertableToBoolean(Set<ChoiceNode> choiceNodeSet) {
		for (ChoiceNode choice : choiceNodeSet) {
			if (!choice.toString().equals(SPECIAL_VALUE_TRUE) && !choice.toString().equals(SPECIAL_VALUE_FALSE)) {
				return "";
			}
		}
		
		return TYPE_NAME_BOOLEAN;
	}
	
	private static void convertSpecialChoicesSimpleToJavaByte(AbstractParameterNode node) {
		
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();
			
			if (valueString.equals(Byte.MIN_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MIN);
			} else if (valueString.equals(Byte.MAX_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MAX);
			} 
		}
		
	}
	
	private static void convertSpecialChoicesSimpleToJavaShort(AbstractParameterNode node) {
		
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();
			
			if (valueString.equals(Short.MIN_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MIN);
			} else if (valueString.equals(Short.MAX_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MAX);
			} 
		}
		
	}
	
	private static void convertSpecialChoicesSimpleToJavaInt(AbstractParameterNode node) {
	
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();
			
			if (valueString.equals(Integer.MIN_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MIN);
			} else if (valueString.equals(Integer.MAX_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MAX);
			} 
		}
		
	}
	
	private static void convertSpecialChoicesSimpleToJavaLong(AbstractParameterNode node) {
	
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();
			
			if (valueString.equals(Long.MIN_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MIN);
			} else if (valueString.equals(Long.MAX_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MAX);
			} 
		}
		
	}
	
	private static void convertSpecialChoicesSimpleToJavaFloat(AbstractParameterNode node) {
		
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();
			
			if (valueString.equals(Float.MIN_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MIN);
			} else if (valueString.equals(Float.MAX_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MAX);
			} else if (valueString.equals("-" + Float.MIN_VALUE)) {
				choiceNode.setValueString(SPECIAL_VALUE_MINUS_MIN);
			} else if (valueString.equals("-" + Float.MAX_VALUE)) {
				choiceNode.setValueString(SPECIAL_VALUE_MINUS_MAX);
			} else if (valueString.equals(SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE)) {
				choiceNode.setValueString(SPECIAL_VALUE_NEGATIVE_INF);
			} else if (valueString.equals(SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE)) {
				choiceNode.setValueString(SPECIAL_VALUE_POSITIVE_INF);
			} 
		}
		
	}
	
	private static void convertSpecialChoicesSimpleToJavaDouble(AbstractParameterNode node) {
		
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();
			
			if (valueString.equals(Double.MIN_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MIN);
			} else if (valueString.equals(Double.MAX_VALUE + "")) {
				choiceNode.setValueString(SPECIAL_VALUE_MAX);
			} else if (valueString.equals("-" + Double.MIN_VALUE)) {
				choiceNode.setValueString(SPECIAL_VALUE_MINUS_MIN);
			} else if (valueString.equals("-" + Double.MAX_VALUE)) {
				choiceNode.setValueString(SPECIAL_VALUE_MINUS_MAX);
			} else if (valueString.equals(SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE)) {
				choiceNode.setValueString(SPECIAL_VALUE_NEGATIVE_INF);
			} else if (valueString.equals(SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE)) {
				choiceNode.setValueString(SPECIAL_VALUE_POSITIVE_INF);
			} 
		}
		
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

		return new Double((double)valueWithNull);
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
}
