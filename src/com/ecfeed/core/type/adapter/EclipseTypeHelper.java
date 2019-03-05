package com.ecfeed.core.type.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SimpleTypeHelper;

public class EclipseTypeHelper {

	private static final String MAX_VALUE_STRING_REPRESENTATION = "MAX_VALUE";
	private static final String MIN_VALUE_STRING_REPRESENTATION = "MIN_VALUE";

	public static final String DEFAULT_EXPECTED_NUMERIC_VALUE = "0";
	public static final String DEFAULT_EXPECTED_FLOATING_POINT_VALUE = "0.0";
	public static final String DEFAULT_EXPECTED_DOUBLE_VALUE = DEFAULT_EXPECTED_FLOATING_POINT_VALUE;

	private static final String BOOLEAN_TRUE_STRING_REPRESENTATION = "true";
	private static final String BOOLEAN_FALSE_STRING_REPRESENTATION = "false";
	private static final String DEFAULT_EXPECTED_BOOLEAN_VALUE = BOOLEAN_FALSE_STRING_REPRESENTATION;
	private static final String DEFAULT_EXPECTED_TRUE_FALSE_VALUE = BOOLEAN_FALSE_STRING_REPRESENTATION;
	public static final String DEFAULT_EXPECTED_FLOAT_VALUE = DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
	public static final String DEFAULT_EXPECTED_INT_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_LONG_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_SHORT_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_NUMBER_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;
	public static final String DEFAULT_EXPECTED_STRING_VALUE = "";
	public static final String DEFAULT_EXPECTED_TEXT_VALUE = "";
	public static final String DEFAULT_EXPECTED_ENUM_VALUE = "VALUE";

	private static final String[] BOOLEAN_SPECIAL_VALUES = {
			BOOLEAN_TRUE_STRING_REPRESENTATION,
			BOOLEAN_FALSE_STRING_REPRESENTATION };

	private static final String DEFAULT_EXPECTED_CHAR_VALUE = "0";
	public static final String DEFAULT_EXPECTED_BYTE_VALUE = DEFAULT_EXPECTED_NUMERIC_VALUE;

	private static final String[] INTEGER_SPECIAL_VALUES = {
			MIN_VALUE_STRING_REPRESENTATION, MAX_VALUE_STRING_REPRESENTATION };

	private static final String POSITIVE_INFINITY_STRING_REPRESENTATION = "POSITIVE_INFINITY";
	private static final String NEGATIVE_INFINITY_STRING_REPRESENTATION = "NEGATIVE_INFINITY";

	private static final String[] FLOAT_SPECIAL_VALUES = {
			NEGATIVE_INFINITY_STRING_REPRESENTATION,
			MIN_VALUE_STRING_REPRESENTATION, MAX_VALUE_STRING_REPRESENTATION,
			POSITIVE_INFINITY_STRING_REPRESENTATION };

	private static final String NULL_VALUE_STRING_REPRESENTATION = "/null";
	private static final String[] STRING_SPECIAL_VALUES = {NULL_VALUE_STRING_REPRESENTATION};	

	public static final String[] SHORT_SPECIAL_VALUES = INTEGER_SPECIAL_VALUES;
	public static final String[] LONG_SPECIAL_VALUES = INTEGER_SPECIAL_VALUES;
	public static final String[] BYTE_SPECIAL_VALUES = INTEGER_SPECIAL_VALUES;
	public static final String[] DOUBLE_SPECIAL_VALUES = FLOAT_SPECIAL_VALUES;

	public static List<String> getSpecialValues(String typeName) {

		List<String> result = new ArrayList<String>();

		switch(typeName){
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
		case SimpleTypeHelper.TYPE_NAME_LOGICAL:
			result.addAll(Arrays.asList(BOOLEAN_SPECIAL_VALUES));
			break;
		case JavaTypeHelper.TYPE_NAME_CHAR:
			result.addAll(Arrays.asList(DEFAULT_EXPECTED_CHAR_VALUE));
			break;
		case JavaTypeHelper.TYPE_NAME_BYTE:
		case JavaTypeHelper.TYPE_NAME_INT:
		case JavaTypeHelper.TYPE_NAME_LONG:
		case JavaTypeHelper.TYPE_NAME_SHORT:
			result.addAll(Arrays.asList(INTEGER_SPECIAL_VALUES));
			break;
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			result.addAll(Arrays.asList(FLOAT_SPECIAL_VALUES));
			break;
		case JavaTypeHelper.TYPE_NAME_STRING:
		case SimpleTypeHelper.TYPE_NAME_TEXT:
			result.addAll(Arrays.asList(STRING_SPECIAL_VALUES));
			break;
		case SimpleTypeHelper.TYPE_NAME_NUMBER:
			break;
		default:
			return null;
		}
		return result;
	}

	public static String getDefaultExpectedValue(String type) {
		switch(type){
		case JavaTypeHelper.TYPE_NAME_BYTE:
			return DEFAULT_EXPECTED_BYTE_VALUE;
		case JavaTypeHelper.TYPE_NAME_BOOLEAN:
			return DEFAULT_EXPECTED_BOOLEAN_VALUE;
		case SimpleTypeHelper.TYPE_NAME_LOGICAL:
			return DEFAULT_EXPECTED_TRUE_FALSE_VALUE;
		case JavaTypeHelper.TYPE_NAME_CHAR:
			return DEFAULT_EXPECTED_CHAR_VALUE;
		case JavaTypeHelper.TYPE_NAME_DOUBLE:
			return DEFAULT_EXPECTED_DOUBLE_VALUE;
		case JavaTypeHelper.TYPE_NAME_FLOAT:
			return DEFAULT_EXPECTED_FLOAT_VALUE;
		case JavaTypeHelper.TYPE_NAME_INT:
			return DEFAULT_EXPECTED_INT_VALUE;
		case JavaTypeHelper.TYPE_NAME_LONG:
			return DEFAULT_EXPECTED_LONG_VALUE;
		case JavaTypeHelper.TYPE_NAME_SHORT:
			return DEFAULT_EXPECTED_SHORT_VALUE;
		case SimpleTypeHelper.TYPE_NAME_NUMBER:
			return DEFAULT_EXPECTED_NUMBER_VALUE;
		case JavaTypeHelper.TYPE_NAME_STRING:
			return DEFAULT_EXPECTED_STRING_VALUE;
		case SimpleTypeHelper.TYPE_NAME_TEXT:
			return DEFAULT_EXPECTED_TEXT_VALUE;
		default:
			return null;
		}
	}
}
