package com.ecfeed.core.utils;

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;

public final class SimpleTypeHelper {

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

		if (javaType.equals(JavaTypeHelper.TYPE_NAME_BYTE)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaTypeHelper.TYPE_NAME_SHORT)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaTypeHelper.TYPE_NAME_INT)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaTypeHelper.TYPE_NAME_LONG)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaTypeHelper.TYPE_NAME_FLOAT)) {
			return TYPE_NAME_NUMBER;
		} 

		if (javaType.equals(JavaTypeHelper.TYPE_NAME_DOUBLE)) {
			return TYPE_NAME_NUMBER;
		} 

		if (JavaTypeHelper.isTypeWithChars(javaType)) {
			return TYPE_NAME_TEXT;
		} 

		if (JavaTypeHelper.isBooleanTypeName(javaType)) {
			return TYPE_NAME_LOGICAL;
		} 

		return null;
	}

	public static String convertSimpleTypeToJavaType(String javaType) {

		if (javaType.equals(TYPE_NAME_NUMBER)) {
			return JavaTypeHelper.TYPE_NAME_DOUBLE;
		}
		
		if (javaType.equals(TYPE_NAME_LOGICAL)) {
			return JavaTypeHelper.getBooleanTypeName();
		}
		
		if (javaType.equals(TYPE_NAME_TEXT)) {
			return JavaTypeHelper.getStringTypeName();
		}
		
		return null;
	}
	
	public static String createMethodSimpleSignature(MethodNode methodNode) {

		return methodNode.getFullName() + "(" + getSimpleParameters(methodNode) + ")";
	}

	private static String getSimpleParameters(MethodNode methodNode) {

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		String result = ""; 

		int countOfParameters = parameters.size();

		for (int index = 0; index < countOfParameters; index++) {

			AbstractParameterNode abstractParameterNode = parameters.get(index);

			result += createSimpleParameterSignature(abstractParameterNode);

			if (index < countOfParameters - 1) {
				result += ", ";
			}
		}

		return result;
	}

	private static String createSimpleParameterSignature(AbstractParameterNode abstractParameterNode) {

		String result = convertJavaTypeToSimpleType(abstractParameterNode.getType()); 
		result += " ";
		result += abstractParameterNode.getFullName();

		return result;
	}

	// TODO SIMPLE-VIEW
	public static void convertSpecialChoicesJavaToSimpleByte(AbstractParameterNode node) {
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();

			if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
				choiceNode.setValueString(Byte.MIN_VALUE + "");
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
				choiceNode.setValueString(Byte.MAX_VALUE + "");
			} 
		}
	}

	public static void convertSpecialChoicesJavaToSimpleShort(AbstractParameterNode node) {
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();

			if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
				choiceNode.setValueString(Short.MIN_VALUE + "");
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
				choiceNode.setValueString(Short.MAX_VALUE + "");
			} 
		}
	}

	public static void convertSpecialChoicesJavaToSimpleInt(AbstractParameterNode node) {
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();

			if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
				choiceNode.setValueString(Integer.MIN_VALUE + "");
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
				choiceNode.setValueString(Integer.MAX_VALUE + "");
			} 
		}
	}

	public static void convertSpecialChoicesJavaToSimpleLong(AbstractParameterNode node) {
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();

			if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
				choiceNode.setValueString(Long.MIN_VALUE + "");
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
				choiceNode.setValueString(Long.MAX_VALUE + "");
			} 
		}
	}

	public static void convertSpecialChoicesJavaToSimpleFloat(AbstractParameterNode node) {
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();

			if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
				choiceNode.setValueString(Float.MIN_VALUE + "");
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
				choiceNode.setValueString(Float.MAX_VALUE + "");
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MINUS_MIN)) {
				choiceNode.setValueString("-" + Float.MIN_VALUE);
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MINUS_MAX)) {
				choiceNode.setValueString("-" + Float.MAX_VALUE);
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_NEGATIVE_INF)) {
				choiceNode.setValueString(SPECIAL_VALUE_NEGATIVE_INF_SIMPLE);
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_POSITIVE_INF)) {
				choiceNode.setValueString(SPECIAL_VALUE_POSITIVE_INF_SIMPLE);
			} 
		}
	}

	public static void convertSpecialChoicesJavaToSimpleDouble(AbstractParameterNode node) {
		for (ChoiceNode choiceNode : node.getAllChoices()) {
			String valueString = choiceNode.getValueString();

			if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MIN)) {
				choiceNode.setValueString(Double.MIN_VALUE + "");
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MAX)) {
				choiceNode.setValueString(Double.MAX_VALUE + "");
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MINUS_MIN)) {
				choiceNode.setValueString("-" + Double.MIN_VALUE);
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_MINUS_MAX)) {
				choiceNode.setValueString("-" + Double.MAX_VALUE);
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_NEGATIVE_INF)) {
				choiceNode.setValueString(SPECIAL_VALUE_NEGATIVE_INF_SIMPLE);
			} else if (valueString.equals(JavaTypeHelper.SPECIAL_VALUE_POSITIVE_INF)) {
				choiceNode.setValueString(SPECIAL_VALUE_POSITIVE_INF_SIMPLE);
			} 
		}
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

	public static String parseToSimpleView(String text) { // TODO SIMPLE-VIEW - remove ?
		String returnText = text;

//		if (returnText.equals("")) {
//			return DEFAULT_PACKAGE;
//		}

		returnText = returnText.replaceAll("_", " ");

		while (returnText.contains("  ")) {
			returnText = returnText.replaceAll("  ", " ");
		}

		returnText = returnText.replaceAll(RegexHelper.REGEX_SPECIAL_CHARACTER, "_");
		returnText = returnText.trim();

		return returnText;
	}

}
