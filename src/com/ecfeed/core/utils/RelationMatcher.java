package com.ecfeed.core.utils;


public class RelationMatcher {

	public static boolean isMatchQuiet(
			EMathRelation relation, String typeName, String leftString, String rightString) {

		boolean result = false;
		try {
			result = isRelationMatch(relation, typeName, leftString, rightString);
		} catch (Exception e) {
		}

		return result;
	}

	public static boolean isRelationMatch(
			EMathRelation relation, String typeName, String leftString, String rightString) {

		if (typeName == null) {
			return false;
		}		

		if (relation == EMathRelation.EQUAL && StringHelper.isEqual(leftString, rightString)) {
			return true;
		}
		if (relation == EMathRelation.NOT_EQUAL && !StringHelper.isEqual(leftString, rightString)) {
			return true;
		}		

		if (JavaTypeHelper.isNumericTypeName(typeName)) {
			if (isMatchForNumericTypes(typeName, relation, leftString, rightString)) {
				return true;
			}
			return false;
		}

		if (JavaTypeHelper.isTypeWithChars(typeName)) {
			if (EMathRelation.isMatch(relation, leftString, rightString)) {
				return true;
			}
			return false;
		}

		if (JavaTypeHelper.isBooleanTypeName(typeName)) {
			if (EMathRelation.isEqualityMatchForBooleans(relation, leftString, rightString)) {
				return true;
			}
			return false;
		}		

		if (EMathRelation.isEqualityMatch(relation, leftString, rightString)) {
			return true;
		}

		return false;
	}

	private static boolean isMatchForNumericTypes(
			String typeName, EMathRelation relation, String leftValue, String rightValue) {

		if (JavaTypeHelper.isFloatingPointTypeName(typeName)) {

			Double leftDouble = JavaTypeHelper.convertNumericToDouble(typeName, leftValue, ERunMode.QUIET);
			Double rightDouble = JavaTypeHelper.convertNumericToDouble(typeName, rightValue, ERunMode.QUIET);

			if (EMathRelation.isMatch(relation, leftDouble, rightDouble)) {
				return true;
			}

			return false;
		}

		Long leftLong = JavaTypeHelper.parseLongValue(leftValue, ERunMode.QUIET);
		Long rightLong = JavaTypeHelper.parseLongValue(rightValue, ERunMode.QUIET);

		if (EMathRelation.isMatch(relation, leftLong, rightLong)) {
			return true;
		}

		return false;
	}

}
