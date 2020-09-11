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

	public static String DEFAULT_PACKAGE = "(default package)";

	private static final String[] SUPPORTED_SIMPLE_VIEW_TYPES = new String[] {
			TYPE_NAME_TEXT,
			TYPE_NAME_NUMBER,
			TYPE_NAME_LOGICAL
	};

	// TODO SIMPLE-VIEW unit test

	public static String verifySeparatorsInName(String name) {

		if (name.contains("_")) {
			return "Underline chars are not allowed in name.";
		}

		if (name.startsWith(" ")) {
			return "Name should not begin with space char.";
		}

		return null;
	}

	public static boolean isSimpleType(String typeName) {

		if (typeName == null) {
			return false;
		}

		return Arrays.asList(SUPPORTED_SIMPLE_VIEW_TYPES).contains(typeName);
	}

	public static String[] getSupportedSimpleViewTypes() {

		return SUPPORTED_SIMPLE_VIEW_TYPES;
	}

	public static String convertJavaTypeToSimpleType(String javaType) {

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

		return null;
	}

	public static String convertSimpleTypeToJavaType(String javaType) {

		if (javaType.equals(TYPE_NAME_NUMBER)) {
			return JavaLanguageHelper.TYPE_NAME_DOUBLE;
		}

		if (javaType.equals(TYPE_NAME_LOGICAL)) {
			return JavaLanguageHelper.getBooleanTypeName();
		}

		if (javaType.equals(TYPE_NAME_TEXT)) {
			return JavaLanguageHelper.getStringTypeName();
		}

		return null;
	}

	public static boolean isTextTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_TEXT)) {
			return true;
		}
		return false;
	}	

	public static boolean isNumberTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_NUMBER)) {
			return true;
		}
		return false;
	}	

	public static boolean isLogicalTypeName(String typeName) {

		if (typeName.equals(TYPE_NAME_LOGICAL)) {
			return true;
		}
		return false;
	}	

	public static String convertTextFromJavaToSimpleConvention(String text) {

		String result = text.replaceAll("_", " ");

		return result;
	}


	public static String convertTextFromSimpleToJavaConvention(String text1) {

		if (text1.contains("_")) {
			ExceptionHelper.reportRuntimeException("Underline chars are not allowed in simple view.");
		}

		String result = text1.replace(" ", "_");

		return result;
	}

	public static String validateType(String type) {

		if (StringHelper.isEqual(type, TYPE_NAME_TEXT)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_NUMBER)) {
			return null;
		}

		if (StringHelper.isEqual(type, TYPE_NAME_LOGICAL)) {
			return null;
		}

		return "Invalid simple type: " + type + ".";
	}
}
