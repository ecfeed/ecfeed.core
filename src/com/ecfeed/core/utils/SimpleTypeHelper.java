package com.ecfeed.core.utils;

import java.util.Arrays;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;

public final class SimpleTypeHelper {

	public static final String TYPE_NAME_TEXT = "Text";
	public static final String TYPE_NAME_NUMBER = "Number";
	public static final String TYPE_NAME_LOGICAL = "Logical";
	
	public static final String DEFAULT_EXPECTED_TEXT_VALUE = "";
	public static final String DEFAULT_EXPECTED_NUMBER_VALUE = "0";
	public static final String DEFAULT_EXPECTED_LOGICAL_VALUE = "false";
	
	public static final String SPECIAL_VALUE_NEGATIVE_INF_SIMPLE = "-Infinity";
	public static final String SPECIAL_VALUE_POSITIVE_INF_SIMPLE = "Infinity";
	
	private static final String[] SUPPORTED_SIMPLE_TYPES = new String[] {
			TYPE_NAME_TEXT,
			TYPE_NAME_NUMBER,
			TYPE_NAME_LOGICAL
		};
	
	public static boolean isSimpleType(String typeName) {
		
		if (typeName == null) {
			return false;
		}
		
		return Arrays.asList(SUPPORTED_SIMPLE_TYPES).contains(typeName);
	}
	
	public static String[] getSupportedSimpleTypes() {
		return SUPPORTED_SIMPLE_TYPES;
	}
	
	public static void convertTypeJavaToSimple(AbstractParameterNode node) {
		String nodeType = node.getType();
		
		if (isSimpleType(nodeType)) {
			return;
		}
		
		node.setSuggestedType(nodeType);
		if (nodeType.equals(JavaTypeHelper.TYPE_NAME_BYTE)) {
			node.setType(TYPE_NAME_NUMBER);
			convertSpecialChoicesJavaToSimpleByte(node);
		} else if (nodeType.equals(JavaTypeHelper.TYPE_NAME_SHORT)) {
			node.setType(TYPE_NAME_NUMBER);
			convertSpecialChoicesJavaToSimpleShort(node);
		} else if (nodeType.equals(JavaTypeHelper.TYPE_NAME_INT)) {
			node.setType(TYPE_NAME_NUMBER);
			convertSpecialChoicesJavaToSimpleInt(node);
		} else if (nodeType.equals(JavaTypeHelper.TYPE_NAME_LONG)) {
			node.setType(TYPE_NAME_NUMBER);
			convertSpecialChoicesJavaToSimpleLong(node);
		} else if (nodeType.equals(JavaTypeHelper.TYPE_NAME_FLOAT)) {
			node.setType(TYPE_NAME_NUMBER);
			convertSpecialChoicesJavaToSimpleFloat(node);
		} else if (nodeType.equals(JavaTypeHelper.TYPE_NAME_DOUBLE)) {
			node.setType(TYPE_NAME_NUMBER);
			convertSpecialChoicesJavaToSimpleDouble(node);
		} else if (JavaTypeHelper.isTypeWithChars(nodeType)) {
			node.setType(TYPE_NAME_TEXT);
		} else if (JavaTypeHelper.isBooleanTypeName(nodeType)) {
			node.setType(TYPE_NAME_LOGICAL);
		} 

	}
	
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
	
	public static String parseToSimpleView(String text) {
		String returnText = text;
		
		if (returnText.equals("")) {
			return "(default package)";
		}
		
		returnText = returnText.replaceAll("_", " ");
		
		while (returnText.contains("  ")) {
			returnText = returnText.replaceAll("  ", " ");
		}
		
		returnText = returnText.replaceAll(RegexHelper.REGEX_SPECIAL_CHARACTER, "_");
		returnText = returnText.trim();

		return returnText;
	}
	
}
