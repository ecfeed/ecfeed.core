/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.utils.*;
import org.junit.Test;

public class ValueConditionTest {

	enum AssertType {
		TRUE,
		FALSE,
	}

	public void evaluateOne(
			MethodParameterNode methodParameterNode, 
			String choiceValue, 
			EMathRelation statementRelation, 
			String value,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod", null);
		methodNode.addParameter(methodParameterNode);

		RelationStatement statement = 
				RelationStatement.createRelationStatementWithValueCondition(
						methodParameterNode, statementRelation, value);

		ChoiceNode choiceNode = new ChoiceNode("Label" + choiceValue, choiceValue, null);

		EvaluationResult result = statement.evaluate(createList(choiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}


	private List<ChoiceNode> createList(ChoiceNode choiceNode) {
		return Arrays.asList(choiceNode);
	}

	public void evaluateRandomizedOne(
			MethodParameterNode methodParameterNode, 
			String choiceValue, 
			EMathRelation statementRelation, 
			String value,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod", null);
		methodNode.addParameter(methodParameterNode);

		RelationStatement statement = 
				RelationStatement.createRelationStatementWithValueCondition(
						methodParameterNode, statementRelation, value);

		ChoiceNode choiceNode = new ChoiceNode("Label" + choiceValue, choiceValue, null);
		choiceNode.setRandomizedValue(true);


		EvaluationResult result = statement.evaluate(createList(choiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	public void evaluateRandomizeAmbiguousOne(
			MethodParameterNode methodParameterNode, 
			String choiceValue, 
			EMathRelation statementRelation, 
			String value,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod", null);
		methodNode.addParameter(methodParameterNode);

		RelationStatement statement = 
				RelationStatement.createRelationStatementWithValueCondition(
						methodParameterNode, statementRelation, value);

		ChoiceNode choiceNode = new ChoiceNode("Label" + choiceValue, choiceValue, null);
		choiceNode.setRandomizedValue(true);


		EvaluationResult result = EvaluationResult.convertFromBoolean(
				statement.isAmbiguous(Arrays.asList(createList(choiceNode)), new MessageStack(), new ExtLanguageManagerForJava()));


		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	public void evaluateForRangeIntegerTypes(String parameterType) {
		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false, null);

		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.EQUAL, "2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL, "2", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.LESS_EQUAL,    "2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		evaluateRandomizedOne(methodParameterNode, "99999", EMathRelation.LESS_THAN, "100000", AssertType.TRUE); 

		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "1", EMathRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);

		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "2:2", EMathRelation.GREATER_EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "-10:-1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "0:10", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "2:2", EMathRelation.GREATER_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "10:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:-1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "1:9", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "2:2", EMathRelation.EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "2:2", EMathRelation.LESS_EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "11:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "0:10", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "2:2", EMathRelation.LESS_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "11:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "-10:-1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "-10:0", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "1:9", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);



		//tests from randomize-choice-value document
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "1:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1:10", EMathRelation.LESS_THAN, "2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "1:10", EMathRelation.GREATER_THAN, "2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "5:10", EMathRelation.LESS_THAN, "1:4", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "5:10", EMathRelation.LESS_THAN, "1", AssertType.FALSE);
		//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.LESS_THAN, "a-z", AssertType.FALSE);
		//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.EQUAL, "a-z", AssertType.FALSE);
		//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.EQUAL, "x", AssertType.FALSE);
	}
	
	@Test
	public void evaluateForRandomizedInteger() {
		evaluateForRangeIntegerTypes(JavaLanguageHelper.TYPE_NAME_INT);
	}

	@Test
	public void evaluateForRandomizedLong() {
		evaluateForRangeIntegerTypes(JavaLanguageHelper.TYPE_NAME_LONG);
	}	

	@Test
	public void evaluateForRandomizedShort() {
		evaluateForRangeIntegerTypes(JavaLanguageHelper.TYPE_NAME_SHORT);
	}	

	@Test
	public void evaluateForRandomizedByte() {
		evaluateForRangeIntegerTypes(JavaLanguageHelper.TYPE_NAME_BYTE);
	}	

	public void evaluateForAmbiguousIntegerTypes(String parameterType) {
		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false, null);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.EQUAL,     "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.EQUAL, "2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL, "2", AssertType.FALSE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.LESS_THAN,     "2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.LESS_EQUAL,    "2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "99999", EMathRelation.LESS_THAN, "100000", AssertType.FALSE); 

		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL,     "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1", EMathRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EMathRelation.GREATER_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "-10:0", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_EQUAL, "0:10", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EMathRelation.GREATER_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "10:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "10:10", EMathRelation.EQUAL, "10:10", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EMathRelation.EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "1:9", AssertType.TRUE);


		evaluateRandomizeAmbiguousOne(methodParameterNode, "1:3", EMathRelation.EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1:3", EMathRelation.EQUAL, "2", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "10:10", EMathRelation.LESS_EQUAL, "10:10", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EMathRelation.LESS_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "10:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "10:10", EMathRelation.LESS_THAN, "10:10", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EMathRelation.LESS_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "-10:0", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);

		//		//tests from randomize-choice-value document
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "1:2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1:10", EMathRelation.LESS_THAN, "2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1:10", EMathRelation.GREATER_THAN, "2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "5:10", EMathRelation.LESS_THAN, "1:4", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "5:10", EMathRelation.LESS_THAN, "1", AssertType.FALSE);
		//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.LESS_THAN, "a-z", AssertType.FALSE);
		//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.EQUAL, "a-z", AssertType.FALSE);
		//		evaluateRandomizedOne(methodParameterNode, "a-z", EStatementRelation.EQUAL, "x", AssertType.FALSE);

	}
	
	@Test
	public void evaluateForAmbiguousInteger() {
		evaluateForAmbiguousIntegerTypes(JavaLanguageHelper.TYPE_NAME_INT);
	}

	@Test
	public void evaluateForAmbiguousLong() {
		evaluateForAmbiguousIntegerTypes(JavaLanguageHelper.TYPE_NAME_LONG);
	}	

	@Test
	public void evaluateForAmbiguousShort() {
		evaluateForAmbiguousIntegerTypes(JavaLanguageHelper.TYPE_NAME_SHORT);
	}	

	@Test
	public void evaluateFoAmbiguousdByte() {
		evaluateForAmbiguousIntegerTypes(JavaLanguageHelper.TYPE_NAME_BYTE);
	}	
















	@Test
	public void evaluateForStrings() {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);

		evaluateOne(methodParameterNode, "a", EMathRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.EQUAL, "A", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EMathRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.NOT_EQUAL, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_THAN, "b", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(methodParameterNode, "abc", EMathRelation.LESS_THAN, "abd", AssertType.TRUE);
		evaluateOne(methodParameterNode, "abc", EMathRelation.LESS_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(methodParameterNode, "abc", EMathRelation.NOT_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(methodParameterNode, "abc", EMathRelation.EQUAL, "abd", AssertType.FALSE);
	}

	private void evaluateForIntegerTypes(String parameterType) {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false, null);

		evaluateOne(methodParameterNode, "1", EMathRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EMathRelation.EQUAL, "2", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL, "2", AssertType.TRUE);

		evaluateOne(methodParameterNode, "1", EMathRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1", EMathRelation.LESS_EQUAL,    "2", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1", EMathRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EMathRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		// For integer types allow greater values than type range e.g. 256 for Byte
		evaluateOne(methodParameterNode, "99999", EMathRelation.LESS_THAN, "100000", AssertType.TRUE); 

		evaluateOne(methodParameterNode, "1", EMathRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EMathRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1", EMathRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EMathRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EMathRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1", EMathRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EMathRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
	}	

	@Test
	public void evaluateForInteger() {
		evaluateForIntegerTypes(JavaLanguageHelper.TYPE_NAME_INT);
	}

	@Test
	public void evaluateForLong() {
		evaluateForIntegerTypes(JavaLanguageHelper.TYPE_NAME_LONG);
	}	

	@Test
	public void evaluateForShort() {
		evaluateForIntegerTypes(JavaLanguageHelper.TYPE_NAME_SHORT);
	}	

	@Test
	public void evaluateForByte() {
		evaluateForIntegerTypes(JavaLanguageHelper.TYPE_NAME_BYTE);
	}	

	public void evaluateForFloatTypes(String parameterType) {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", parameterType, "", false, null);

		evaluateOne(methodParameterNode, "1", EMathRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.EQUAL,   "1.0", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.EQUAL,   "1", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.EQUAL,   "1.000", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1234.5678", EMathRelation.EQUAL,   "1234.5678", AssertType.TRUE);		

		evaluateOne(methodParameterNode, "1",   EMathRelation.NOT_EQUAL, "1",   AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.NOT_EQUAL, "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.EQUAL,     "2.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.NOT_EQUAL, "2.0", AssertType.TRUE);

		evaluateOne(methodParameterNode, "1", EMathRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.LESS_THAN,     "2.0", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.LESS_EQUAL,    "2.0", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.GREATER_THAN,  "2.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.GREATER_EQUAL, "2.0", AssertType.FALSE);

		evaluateOne(methodParameterNode, "99999", EMathRelation.LESS_THAN, "100000", AssertType.TRUE);

		evaluateOne(methodParameterNode, "1.0", EMathRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "1.0", EMathRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EMathRelation.EQUAL,         "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.NOT_EQUAL,     "1.0", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_THAN,     "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_EQUAL,    "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_THAN,  "1.0", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "1.0", AssertType.FALSE);		
	}	

	@Test
	public void evaluateForFloat() {
		evaluateForFloatTypes(JavaLanguageHelper.TYPE_NAME_FLOAT);
	}

	@Test
	public void evaluateForDouble() {
		evaluateForFloatTypes(JavaLanguageHelper.TYPE_NAME_DOUBLE);
	}	

	@Test
	public void evaluateForBoolean() {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_BOOLEAN, "", false, null);

		evaluateOne(methodParameterNode, "true", EMathRelation.EQUAL, "true", AssertType.TRUE);
		evaluateOne(methodParameterNode, "true", EMathRelation.EQUAL, "false", AssertType.FALSE);

		evaluateOne(methodParameterNode, "true", EMathRelation.EQUAL, "x", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EMathRelation.NOT_EQUAL, "x", AssertType.TRUE);
		evaluateOne(methodParameterNode, "x", EMathRelation.EQUAL, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "x", EMathRelation.NOT_EQUAL, "false", AssertType.TRUE);

		evaluateOne(methodParameterNode, "true", EMathRelation.LESS_THAN, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EMathRelation.LESS_THAN, "false", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EMathRelation.LESS_EQUAL, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EMathRelation.LESS_EQUAL, "false", AssertType.FALSE);

		evaluateOne(methodParameterNode, "true", EMathRelation.GREATER_THAN, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EMathRelation.GREATER_THAN, "false", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EMathRelation.GREATER_EQUAL, "true", AssertType.FALSE);
		evaluateOne(methodParameterNode, "true", EMathRelation.GREATER_EQUAL, "false", AssertType.FALSE);
	}

	@Test
	public void evaluateChar() {

		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_CHAR, "", false, null);

		evaluateOne(methodParameterNode, "a", EMathRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.NOT_EQUAL, "a", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EMathRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(methodParameterNode, "a", EMathRelation.NOT_EQUAL, "b", AssertType.TRUE);

		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_THAN, "b", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(methodParameterNode, "a", EMathRelation.LESS_EQUAL, "a", AssertType.TRUE);
		evaluateOne(methodParameterNode, "a", EMathRelation.GREATER_EQUAL, "a", AssertType.TRUE);


		evaluateOne(methodParameterNode, "b", EMathRelation.EQUAL, "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "b", EMathRelation.NOT_EQUAL, "a", AssertType.TRUE);

		evaluateOne(methodParameterNode, "b", EMathRelation.LESS_THAN, "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "b", EMathRelation.GREATER_THAN, "a", AssertType.TRUE);

		evaluateOne(methodParameterNode, "b", EMathRelation.LESS_EQUAL, "a", AssertType.FALSE);
		evaluateOne(methodParameterNode, "b", EMathRelation.GREATER_EQUAL, "a", AssertType.TRUE);
	}	

	@Test
	public void copyAndEqualityTest() {
		MethodParameterNode methodParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);

		RelationStatement statement = 
				RelationStatement.createRelationStatementWithValueCondition(
						methodParameterNode, EMathRelation.EQUAL, "ABC");

		RelationStatement copy = statement.makeClone();

		boolean result = statement.isEqualTo(copy);
		assertEquals(true, result);
	}

	@Test
	public void updateReferencesTest() {
		MethodNode method1 = new MethodNode("method1", null);
		MethodParameterNode method1ParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		method1.addParameter(method1ParameterNode);

		RelationStatement statement = 
				RelationStatement.createRelationStatementWithValueCondition(
						method1ParameterNode, EMathRelation.EQUAL, "ABC");

		MethodNode method2 = new MethodNode("method2", null);
		MethodParameterNode method2ParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		method2.addParameter(method2ParameterNode);

		assertNotEquals(method2ParameterNode.hashCode(), statement.getLeftParameter().hashCode());

		statement.updateReferences(method2);

		assertEquals(method2ParameterNode.hashCode(), statement.getLeftParameter().hashCode());
	}	
}
