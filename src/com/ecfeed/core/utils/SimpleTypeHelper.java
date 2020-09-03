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

	// TODO SIMPLE-VIEW remove and use method from ExtLanguageHelper
	public static String convertConditionallyJavaTypeToSimpleType(String javaType, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.JAVA) {
			return javaType;
		} 

		String result = SimpleTypeHelper.convertJavaTypeToSimpleType(javaType);
		return result;
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

	// TODO SIMPLE-VIEW remove all types of nodes from this file
	
	public static String createMethodSimpleSignature(MethodNode methodNode) {

		return methodNode.getName() + "(" + getSimpleParameters(methodNode) + ")";
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
		//		result += " ";
		//		result += abstractParameterNode.getName();

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

}


//public static void convertTypeSimpleToJava(AbstractParameterNode node) {
//if (isJavaType(node.getType())) {
//	return;
//}
//
//Set<ChoiceNode> choiceNodeSet = node.getAllChoices();
//
//Optional<String> nodeTypeHidden = node.getSuggestedType();
//node.setSuggestedType(null);
//
//if (nodeTypeHidden.isPresent()) {
//	String newType = isConvertableToTypeHidden(choiceNodeSet, nodeTypeHidden.get());
//	
//	if (!newType.equals("")) {
//		switch (newType) {
//			case TYPE_NAME_BYTE : 
//				convertSpecialChoicesSimpleToJavaByte(node);
//				break;
//			case TYPE_NAME_SHORT : 
//				convertSpecialChoicesSimpleToJavaShort(node);
//				break;
//			case TYPE_NAME_INT : 
//				convertSpecialChoicesSimpleToJavaInt(node);
//				break;
//			case TYPE_NAME_LONG : 
//				convertSpecialChoicesSimpleToJavaLong(node);
//				break;
//			case TYPE_NAME_FLOAT : 
//				convertSpecialChoicesSimpleToJavaFloat(node);
//				break;
//			case TYPE_NAME_DOUBLE : 
//				convertSpecialChoicesSimpleToJavaDouble(node);
//				break;
//		}
//		
//		node.setType(newType);
//		return;
//	}		
//}
//
//if (node.getType().equals(SimpleTypeHelper.TYPE_NAME_NUMBER)) {
//	if (choiceNodeSet.size() == 0) {
//		node.setType(TYPE_NAME_INT);
//		return;
//	}
//	
//	if (!isConvertableToInt(choiceNodeSet).equals("")) {
//		node.setType(TYPE_NAME_INT);
//		return;
//	}
//	if (!isConvertableToLong(choiceNodeSet).equals("")) {
//		node.setType(TYPE_NAME_LONG);
//		return;
//	}
//	if (!isConvertableToDouble(choiceNodeSet).equals("")) {
//		node.setType(TYPE_NAME_DOUBLE);
//		return;
//	}
//}
//
//if (node.getType().equals(SimpleTypeHelper.TYPE_NAME_TEXT)) {
//	node.setType(TYPE_NAME_STRING);
//	return;
//}
//
//if (node.getType().equals(SimpleTypeHelper.TYPE_NAME_LOGICAL)) {
//	node.setType(TYPE_NAME_BOOLEAN);
//	return;
//}
//
//}



//private static String isConvertableToTypeHidden(Set<ChoiceNode> choiceNodeSet, String nodeTypeHidden) {
//switch(nodeTypeHidden) {
//	case TYPE_NAME_BYTE : return isConvertableToByte(choiceNodeSet);
//	case TYPE_NAME_SHORT : return isConvertableToShort(choiceNodeSet);
//	case TYPE_NAME_INT : return isConvertableToInt(choiceNodeSet);
//	case TYPE_NAME_LONG : return isConvertableToLong(choiceNodeSet);
//	case TYPE_NAME_FLOAT : return isConvertableToFloat(choiceNodeSet);
//	case TYPE_NAME_DOUBLE : return isConvertableToDouble(choiceNodeSet);
//	case TYPE_NAME_CHAR : return isConvertableToChar(choiceNodeSet);
//	case TYPE_NAME_BOOLEAN : return isConvertableToBoolean(choiceNodeSet);
//	case TYPE_NAME_STRING : return TYPE_NAME_STRING;
//	default : return "";
//}
//}

//private static String isConvertableToByte(Set<ChoiceNode> choiceNodeSet) {
//for (ChoiceNode choice : choiceNodeSet) {
//	try {
//		Byte.parseByte(choice.getValueString());
//	} catch (NumberFormatException e) {
//		return "";
//	}
//}
//
//return TYPE_NAME_BYTE;
//}

//private static String isConvertableToShort(Set<ChoiceNode> choiceNodeSet) {
//for (ChoiceNode choice : choiceNodeSet) {
//	try {
//		Short.parseShort(choice.getValueString());
//	} catch (NumberFormatException e) {
//		return "";
//	}
//}
//
//return TYPE_NAME_SHORT;
//}

//private static String isConvertableToInt(Set<ChoiceNode> choiceNodeSet) {
//for (ChoiceNode choice : choiceNodeSet) {
//	try {
//		Integer.parseInt(choice.getValueString());
//	} catch (NumberFormatException e) {
//		return "";
//	}
//}
//
//return TYPE_NAME_INT;
//}

//private static String isConvertableToLong(Set<ChoiceNode> choiceNodeSet) {
//for (ChoiceNode choice : choiceNodeSet) {
//	try {
//		Long.parseLong(choice.getValueString());
//	} catch (NumberFormatException e) {
//		return "";
//	}
//}
//
//return TYPE_NAME_LONG;
//}

//private static String isConvertableToFloat(Set<ChoiceNode> choiceNodeSet) {
//for (ChoiceNode choice : choiceNodeSet) {
//	String choiceValue = choice.getValueString();
//	
//	try {
//		if (choiceValue.equals(SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE) || choiceValue.equals(SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE)) {
//			continue;
//		}
//		
//		Float.parseFloat(choiceValue);
//	} catch (NumberFormatException e) {
//		return "";
//	}
//}
//
//return TYPE_NAME_FLOAT;
//}

//private static String isConvertableToDouble(Set<ChoiceNode> choiceNodeSet) {
//for (ChoiceNode choice : choiceNodeSet) {
//	String choiceValue = choice.getValueString();
//	
//	try {
//		if (choiceValue.equals(SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE) || choiceValue.equals(SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE)) {
//			continue;
//		}
//		
//		Double.parseDouble(choice.getValueString());
//	} catch (NumberFormatException e) {
//		return "";
//	}
//}
//
//return TYPE_NAME_DOUBLE;
//}

//private static String isConvertableToChar(Set<ChoiceNode> choiceNodeSet) {
//for (ChoiceNode choice : choiceNodeSet) {
//	if (choice.getValueString().length() > 1) {
//		return "";
//	}
//}
//
//return TYPE_NAME_CHAR;
//}

//private static String isConvertableToBoolean(Set<ChoiceNode> choiceNodeSet) {
//for (ChoiceNode choice : choiceNodeSet) {
//	if (!choice.toString().equals(SPECIAL_VALUE_TRUE) && !choice.toString().equals(SPECIAL_VALUE_FALSE)) {
//		return "";
//	}
//}
//
//return TYPE_NAME_BOOLEAN;
//}

//private static void convertSpecialChoicesSimpleToJavaByte(AbstractParameterNode node) { // TODO SIMPLE-VIEW remove ?
//
//for (ChoiceNode choiceNode : node.getAllChoices()) {
//	String valueString = choiceNode.getValueString();
//	
//	if (valueString.equals(Byte.MIN_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MIN);
//	} else if (valueString.equals(Byte.MAX_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MAX);
//	} 
//}
//
//}

//private static void convertSpecialChoicesSimpleToJavaShort(AbstractParameterNode node) {
//
//for (ChoiceNode choiceNode : node.getAllChoices()) {
//	String valueString = choiceNode.getValueString();
//	
//	if (valueString.equals(Short.MIN_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MIN);
//	} else if (valueString.equals(Short.MAX_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MAX);
//	} 
//}
//
//}

//private static void convertSpecialChoicesSimpleToJavaInt(AbstractParameterNode node) {
//
//for (ChoiceNode choiceNode : node.getAllChoices()) {
//	String valueString = choiceNode.getValueString();
//	
//	if (valueString.equals(Integer.MIN_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MIN);
//	} else if (valueString.equals(Integer.MAX_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MAX);
//	} 
//}
//
//}

//private static void convertSpecialChoicesSimpleToJavaLong(AbstractParameterNode node) {
//
//for (ChoiceNode choiceNode : node.getAllChoices()) {
//	String valueString = choiceNode.getValueString();
//	
//	if (valueString.equals(Long.MIN_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MIN);
//	} else if (valueString.equals(Long.MAX_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MAX);
//	} 
//}
//
//}

//private static void convertSpecialChoicesSimpleToJavaFloat(AbstractParameterNode node) {
//
//for (ChoiceNode choiceNode : node.getAllChoices()) {
//	String valueString = choiceNode.getValueString();
//	
//	if (valueString.equals(Float.MIN_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MIN);
//	} else if (valueString.equals(Float.MAX_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MAX);
//	} else if (valueString.equals("-" + Float.MIN_VALUE)) {
//		choiceNode.setValueString(SPECIAL_VALUE_MINUS_MIN);
//	} else if (valueString.equals("-" + Float.MAX_VALUE)) {
//		choiceNode.setValueString(SPECIAL_VALUE_MINUS_MAX);
//	} else if (valueString.equals(SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE)) {
//		choiceNode.setValueString(SPECIAL_VALUE_NEGATIVE_INF);
//	} else if (valueString.equals(SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE)) {
//		choiceNode.setValueString(SPECIAL_VALUE_POSITIVE_INF);
//	} 
//}
//
//}

//private static void convertSpecialChoicesSimpleToJavaDouble(AbstractParameterNode node) {
//
//for (ChoiceNode choiceNode : node.getAllChoices()) {
//	String valueString = choiceNode.getValueString();
//	
//	if (valueString.equals(Double.MIN_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MIN);
//	} else if (valueString.equals(Double.MAX_VALUE + "")) {
//		choiceNode.setValueString(SPECIAL_VALUE_MAX);
//	} else if (valueString.equals("-" + Double.MIN_VALUE)) {
//		choiceNode.setValueString(SPECIAL_VALUE_MINUS_MIN);
//	} else if (valueString.equals("-" + Double.MAX_VALUE)) {
//		choiceNode.setValueString(SPECIAL_VALUE_MINUS_MAX);
//	} else if (valueString.equals(SimpleTypeHelper.SPECIAL_VALUE_NEGATIVE_INF_SIMPLE)) {
//		choiceNode.setValueString(SPECIAL_VALUE_NEGATIVE_INF);
//	} else if (valueString.equals(SimpleTypeHelper.SPECIAL_VALUE_POSITIVE_INF_SIMPLE)) {
//		choiceNode.setValueString(SPECIAL_VALUE_POSITIVE_INF);
//	} 
//}
//
//}
