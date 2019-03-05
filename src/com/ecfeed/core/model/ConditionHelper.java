package com.ecfeed.core.model;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.RangeHelper;

public class ConditionHelper {

	public static String getSubstituteType(RelationStatement parentRelationStatement) {

		String substituteType = 
				JavaTypeHelper.getSubstituteType(
						parentRelationStatement.getLeftParameter().getType(), 
						JavaTypeHelper.getStringTypeName());

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

		if (JavaTypeHelper.isStringTypeName(substituteType)) {
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

		messageStack.addMessage(createMessage("Values", left + relation.toString() + right));
	}

	public static void addRelStatementToMesageStack(
			RelationStatement relationStatement, MessageStack messageStack) {

		messageStack.addMessage(createMessage("Statement", relationStatement.toString()));
	}

	public static void addConstraintNameToMesageStack(
			String constraintName, MessageStack messageStack) {

		messageStack.addMessage(createMessage("Constraint", constraintName.toString()));
	}

	public static String createMessage(String name, String value) {
		return name + " [" + value + "].";
	}

	public static String createMessage(String name, String value, String additionalMessage) {
		return name + " [" + value + "] " + additionalMessage + ".";
	}	
}
