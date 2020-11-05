package com.ecfeed.core.model;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.RangeHelper;

public class ConditionHelper {

	public static String getSubstituteType(RelationStatement parentRelationStatement) {

		final MethodParameterNode leftParameter = parentRelationStatement.getLeftParameter();

		final String type = leftParameter.getType();

		String substituteType =
				JavaLanguageHelper.getSubstituteType(
						type,
						JavaLanguageHelper.getStringTypeName());

		if (substituteType == null) {
			final String MESSAGE = "Substitute type must not be null.";
			ExceptionHelper.reportRuntimeException(MESSAGE);
		}

		return substituteType;
	}

	public static boolean isAmbiguousForStringType(
			ChoiceNode leftChoiceNode,
			RelationStatement parentRelationStatement) {

		MethodParameterNode methodParameterNode = (MethodParameterNode)leftChoiceNode.getParameter();

		if (parentRelationStatement.mentions(methodParameterNode)) {
			return true;
		}

		return false;
	}	

	public static boolean isRandomizedChoiceAmbiguous(
			ChoiceNode leftChoiceNode,
			String rightValue,
			RelationStatement parentRelationStatement,
			EMathRelation relation,
			String substituteType) {

		if (JavaLanguageHelper.isStringTypeName(substituteType)) {
			return ConditionHelper.isAmbiguousForStringType(leftChoiceNode, parentRelationStatement);
		}

		if (RangeHelper.isAmbiguous(
				leftChoiceNode.getValueString(), 
				rightValue, 
				relation, 
				substituteType)) {
			return true;
		}

		return false;
	}	
	public static void addValuesMessageToStack(
			String left, EMathRelation relation, String right, MessageStack messageStack) {

		if (messageStack == null) {
			return;
		}

		String message = createMessage("Values", left + relation.toString() + right);
		
		messageStack.addMessage(message);
	}

	public static void addRelStatementToMesageStack(
			RelationStatement relationStatement, MessageStack messageStack) {

		String message = createMessage("Statement", relationStatement.toString());
		
		messageStack.addMessage(message);
	}

	public static void addConstraintNameToMesageStack(
			String constraintName, MessageStack messageStack) {

		String message = createMessage("Constraint", constraintName.toString());
		
		messageStack.addMessage(message);
	}

	public static String createMessage(String name, String value) {
		return name + " [" + value + "].";
	}

	public static String createMessage(String name, String value, String additionalMessage) {
		return name + " [" + value + "] " + additionalMessage + ".";
	}	
}
