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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.ecfeed.core.utils.StatementRelationNames.*;

class StatementRelationNames {
	
	static final String RELATION_ASSIGN = "assign";
	static final String RELATION_EQUAL = "equal"; 
	static final String RELATION_NOT_EQUAL = "notequal";
	static final String RELATION_LESS_THAN = "lessthan";
	static final String RELATION_LESS_THAN_EQUAL = "lessthanequal";
	static final String RELATION_GREATER_THAN = "greaterthan";
	static final String RELATION_GREATER_THAN_EQUAL = "greaterthanequal";
	
	static final String[] SYMBOL_RELATION_ASSIGN = { ":=" };
	static final String[] SYMBOL_RELATION_EQUAL = { "=" };
	static final String[] SYMBOL_RELATION_NOT_EQUAL = { "\u2260", "?" };
	static final String[] SYMBOL_RELATION_LESS_THAN = { "<", "&lt;" };
	static final String[] SYMBOL_RELATION_LESS_THAN_EQUAL = { "<=", "&le;", "&lt;="};
	static final String[] SYMBOL_RELATION_GREATER_THAN = { ">", "&gt;" };
	static final String[] SYMBOL_RELATION_GREATER_THAN_EQUAL = { ">=", "&ge;", "&gt;=",  };
}

public enum EMathRelation{

	ASSIGN(RELATION_ASSIGN, SYMBOL_RELATION_ASSIGN) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return false;
		}
	}, 
	EQUAL(RELATION_EQUAL, SYMBOL_RELATION_EQUAL) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return StringHelper.isEqual(leftString, rightString);
		}
	}, 
	NOT_EQUAL(RELATION_NOT_EQUAL, SYMBOL_RELATION_NOT_EQUAL) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	},
	LESS_THAN(RELATION_LESS_THAN, SYMBOL_RELATION_LESS_THAN) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	}, 
	LESS_EQUAL(RELATION_LESS_THAN_EQUAL, SYMBOL_RELATION_LESS_THAN_EQUAL) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	},
	GREATER_THAN(RELATION_GREATER_THAN, SYMBOL_RELATION_GREATER_THAN) {
		@Override
		public boolean isMatch(String typeName, String leftString, String rightString) {
			return RelationMatcher.isRelationMatch(this, typeName, leftString, rightString);
		}
	},
	GREATER_EQUAL(RELATION_GREATER_THAN_EQUAL, SYMBOL_RELATION_GREATER_THAN_EQUAL) {
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
	private String[] fSymbolArray;
	
	private EMathRelation(String name, String[] symbolArray) {
		fName = name;
		fSymbolArray = symbolArray;
	}

	public String[] getSymbolArray() {
		return fSymbolArray;
	}
	
	public String getName() {
		return fName; 
	}
	
	public String getSymbol() {
		return fSymbolArray[0]; 
	}
	
	public String toString() {
		return getSymbol(); 
	}

	public static boolean isRelationEqual(String name) {

		if (name.equals(RELATION_EQUAL)) {
			return true;
		}
		
		return Arrays.stream(SYMBOL_RELATION_EQUAL).anyMatch( name::equals );
	}

	public static boolean isRelationNotEqual(String name) {

		if (name.equals(RELATION_NOT_EQUAL)) {
			return true;
		}

		return Arrays.stream(SYMBOL_RELATION_NOT_EQUAL).anyMatch( name::equals );
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
		
		return Stream.of(EMathRelation.values())
			.filter( e -> Arrays.stream(e.getSymbolArray()).anyMatch( name::equals ) )
			.findFirst()
			.orElse(null);
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

		if (JavaLanguageHelper.isTypeComparableForLessGreater(parameterTypeName)) {
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

		if (JavaLanguageHelper.parseBooleanValue(actualValue) == null) {
			return false;
		}

		if (JavaLanguageHelper.parseBooleanValue(valueToMatch) == null) {
			return false;
		}		

		return isEqualityMatch(relation, actualValue, valueToMatch);
	}
}
