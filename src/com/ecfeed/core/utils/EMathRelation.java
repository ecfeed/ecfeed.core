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

import java.util.ArrayList;
import java.util.List;

class StatementRelationNames {
	static final String RELATION_EQUAL = "=";
	static final String RELATION_NOT_EQUAL = "\u2260";
	static final String RELATION_LESS_THAN = "<";
	static final String RELATION_LESS_EQUAL = "<=";
	static final String RELATION_GREATER_THAN = ">";
	static final String RELATION_GREATER_EQUAL = ">=";
}

public enum EMathRelation{

	EQUAL(StatementRelationNames.RELATION_EQUAL) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return StringHelper.isEqual(leftString, rightString);
		}
	}, 
	NOT_EQUAL(StatementRelationNames.RELATION_NOT_EQUAL) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	},
	LESS_THAN(StatementRelationNames.RELATION_LESS_THAN) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	}, 
	LESS_EQUAL(StatementRelationNames.RELATION_LESS_EQUAL) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	},
	GREATER_THAN(StatementRelationNames.RELATION_GREATER_THAN) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	},
	GREATER_EQUAL(StatementRelationNames.RELATION_GREATER_EQUAL) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	};

	public abstract boolean isMatch(String typeName, String leftString, String rightString);

	public EvaluationResult evalAsEvaluationResult(String typeName, String leftString, String rightString) {
		return EvaluationResult.convertFromBoolean(this.isMatch(typeName, leftString, rightString));
	}

	private String fName;

	private EMathRelation(String name) {
		fName = name;
	}

	public String getName() {
		return fName; 
	}

	public String toString() {
		return fName; 
	}

	public static boolean isRelationEqual(String name) {

		if (name.equals(StatementRelationNames.RELATION_EQUAL)) {
			return true;
		}
		return false;
	}

	public static boolean isRelationNotEqual(String name) {

		if (name.equals(StatementRelationNames.RELATION_NOT_EQUAL)) {
			return true;
		}

		return false;
	}	

	public static boolean isOrderRelation(EMathRelation relation) {

		if (isEquivalenceRelation(relation)) {
			return false;
		}

		return true;
	}

	public static boolean isEquivalenceRelation(EMathRelation relation) {

		if (relation == EMathRelation.EQUAL) {
			return true;
		}
		if (relation == EMathRelation.NOT_EQUAL) {
			return true;
		}		

		return false;
	}	

	public static EMathRelation getRelation(String name) {

		for (EMathRelation relation : EMathRelation.values()) {
			if (name.equals(relation.getName())) {
				return relation;
			}
		}

		return null;
	}

	public static EMathRelation[] getAvailableRelations(String parameterType) {

		List<EMathRelation> relations = new ArrayList<EMathRelation>();

		for (EMathRelation relation : EMathRelation.values()) {
			if (isRelationForParameterType(relation, parameterType)) {
				relations.add(relation);
			}
		}

		return relations.toArray(new EMathRelation[relations.size()]);
	}

	public static String[] getAvailableRelationNames(String parameterTypeName) {

		return relationCodesToNames(getAvailableRelations(parameterTypeName)); 
	}

	public static String[] relationCodesToNames(EMathRelation[] relationCodes) {

		List<String> relationNames = new ArrayList<String>();

		for (EMathRelation relation : relationCodes) {
			relationNames.add(relation.getName());
		}

		return relationNames.toArray(new String[relationNames.size()]);
	}

	public static boolean isRelationForParameterType(EMathRelation relation, String parameterTypeName) {

		if (JavaTypeHelper.isTypeComparableForLessGreater(parameterTypeName)) {
			return true;
		}
		if (relation == EQUAL || relation == NOT_EQUAL) {
			return true;
		}
		return false;
	}

	public static boolean isMatch(EMathRelation relation, double leftValue, double rightValue) {

		int compareResult = Double.compare(leftValue, rightValue);

		if (isMatch(relation, compareResult)) {
			return true;
		}
		return false;
	}
	
	public static boolean isMatch(EMathRelation relation, long leftValue, long rightValue) {

		int compareResult = Long.compare(leftValue, rightValue);

		if (isMatch(relation, compareResult)) {
			return true;
		}
		return false;
	}

	public static boolean isMatch(EMathRelation relation, String actualValue, String valueToMatch) {

		int compareResult = actualValue.compareTo(valueToMatch);

		if (isMatch(relation, compareResult)) {
			return true;
		}
		return false;
	}

	private static boolean isMatch(EMathRelation relation, int compareResult) {

		switch(relation) {

		case EQUAL:
			return (compareResult == 0);

		case NOT_EQUAL:
			return (compareResult != 0);

		case LESS_THAN:
			return (compareResult < 0);

		case LESS_EQUAL:
			return (compareResult < 0 || compareResult == 0);

		case GREATER_THAN:
			return (compareResult > 0);

		case GREATER_EQUAL:
			return (compareResult > 0 || compareResult == 0);

		default:
			ExceptionHelper.reportRuntimeException("Invalid relation.");
			return false;
		}

	}

	public static boolean isEqualityMatch(EMathRelation relation, String actualValue, String valueToMatch) {

		switch(relation) {

		case EQUAL:
			return StringHelper.isEqual(actualValue, valueToMatch);

		case NOT_EQUAL:
			return !(StringHelper.isEqual(actualValue, valueToMatch));

		default:
			ExceptionHelper.reportRuntimeException("Invalid relation: " + relation.toString() + " in match for equality.");
			return false;
		}
	}	

	public static boolean isEqualityMatchForBooleans(EMathRelation relation, String actualValue, String valueToMatch) {

		if (JavaTypeHelper.parseBooleanValue(actualValue) == null) {
			return false;
		}

		if (JavaTypeHelper.parseBooleanValue(valueToMatch) == null) {
			return false;
		}		

		return isEqualityMatch(relation, actualValue, valueToMatch);
	}
}
