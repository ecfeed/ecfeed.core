package com.ecfeed.core.utils;

import java.util.Arrays;

public final class SimpleLanguageHelper {

	public static final String TYPE_NAME_TEXT = "Text";
	public static final String TYPE_NAME_NUMBER = "Number";
	public static final String TYPE_NAME_LOGICAL = "Logical";

	public static final String DEFAULT_EXPECTED_TEXT_VALUE = "";
	public static final String DEFAULT_EXPECTED_NUMBER_VALUE = "0";
	public static final String DEFAULT_EXPECTED_LOGICAL_VALUE = "false";

	public static final String SPECIAL_VALUE_NEGATIVE_INF_SIMPLE = "-Infinity";
	public static final String SPECIAL_VALUE_POSITIVE_INF_SIMPLE = "Infinity";
	public static final String INVALID_SIMPLE_TYPE = "Invalid simple type";
	public static final String UNDERLINE_CHARS_ARE_NOT_ALLOWED = "Underline characters are not allowed.";

	public static String DEFAULT_PACKAGE = "(default package)";

	private static final String[] SUPPORTED_SIMPLE_VIEW_TYPES = new String[] {
			TYPE_NAME_TEXT,
			TYPE_NAME_NUMBER,
			TYPE_NAME_LOGICAL
	};

	public static String verifySeparators(String name) {

		if (name.contains("_")) {
			return UNDERLINE_CHARS_ARE_NOT_ALLOWED;
		}

		if (name.startsWith(" ")) {
			return "Name should not begin with space character.";
		}

		return null;
	}

	public static boolean isSimpleType(String typeName) {

		if (typeName == null) {
			return false;
		}

		return Arrays.asList(SUPPORTED_SIMPLE_VIEW_TYPES).contains(typeName);
	}

	public static boolean isAllowedType(String typeName) {

		if (SimpleLanguageHelper.isSimpleType(typeName)) {
			return true;
		}

		if (JavaLanguageHelper.isJavaType(typeName)) {
			return false;
		}

		if (JavaLanguageHelper.isMatchWithJavaSimpleIdenfifier(typeName)) {
			return true;
		}

		return false;
	}

	public static String verifyIsAllowedType(String typeName) {

		if (isAllowedType(typeName)) {
			return null;
		}

		return "Parameter type must be a valid type identifier. It must be either a primitive type name: Number,Text,Logical or a valid user type";
	}


	public static String[] getSupportedSimpleViewTypes() {

		return SUPPORTED_SIMPLE_VIEW_TYPES;
	}

	public static String conditionallyConvertJavaTypeToSimpleType(String javaType) {

		if (javaType.equals(JavaLanguageHelper.TYPE_NAME_BYTE)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaLanguageHelper.TYPE_NAME_SHORT)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaLanguageHelper.TYPE_NAME_INT)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaLanguageHelper.TYPE_NAME_LONG)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaLanguageHelper.TYPE_NAME_FLOAT)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaLanguageHelper.TYPE_NAME_DOUBLE)) {
			return TYPE_NAME_NUMBER;
		} 

		if (JavaLanguageHelper.isTypeWithChars(javaType)) {
			return TYPE_NAME_TEXT;
		} 

		if (JavaLanguageHelper.isBooleanTypeName(javaType)) {
			return TYPE_NAME_LOGICAL;
		} 

		return javaType;
	}

	public static String conditionallyConvertSimpleTypeToJavaType(String simpleType) {

		if (simpleType.equals(TYPE_NAME_NUMBER)) {
			return JavaLanguageHelper.TYPE_NAME_DOUBLE;
		}

		if (simpleType.equals(TYPE_NAME_LOGICAL)) {
			return JavaLanguageHelper.getBooleanTypeName();
		}

		if (simpleType.equals(TYPE_NAME_TEXT)) {
			return JavaLanguageHelper.getStringTypeName();
		}

		return simpleType;
	}

	public static boolean isLogicalTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_LOGICAL)) {
			return true;
		}
		return false;
	}	

	public static String convertTextFromJavaToSimpleLanguage(String text) {

		String result = text.replaceAll("_", " ");

		return result;
	}

	public static String convertTextFromSimpleToJavaLanguage(String text1) {

		if (text1.contains("_")) {
			ExceptionHelper.reportRuntimeException(UNDERLINE_CHARS_ARE_NOT_ALLOWED);
		}

		String result = text1.replace(" ", "_");

		return result;
	}

	public static String convertToMinimalTypeFromExtToIntrLanguage(String type) {

		if (StringHelper.isEqual("Number", type)) {
			return "byte";
		}

		if (StringHelper.isEqual("Text", type)) {
			return "char";
		}

		if (StringHelper.isEqual("Logical", type)) {
			return "boolean";
		}

		if (JavaLanguageHelper.isJavaType(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert non simple type.");
		}

		return type;
	}

}
