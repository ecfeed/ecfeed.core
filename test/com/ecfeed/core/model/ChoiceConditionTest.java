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

public class ChoiceConditionTest {

	enum AssertType {
		TRUE,
		FALSE,
	}

	public void evaluateOne(
			MethodParameterNode leftMethodParameterNode,
			String leftChoiceValue,
			EMathRelation statementRelation,
			String rightChoiceValue,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod", null);
		methodNode.addParameter(leftMethodParameterNode);

		ChoiceNode rightChoiceNode = new ChoiceNode("Label" + rightChoiceValue, rightChoiceValue, null);
		rightChoiceNode.setParent(leftMethodParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithChoiceCondition(
						leftMethodParameterNode, statementRelation, rightChoiceNode);

		ChoiceNode leftChoiceNode = new ChoiceNode("Label" + leftChoiceValue, leftChoiceValue, null);
		leftChoiceNode.setParent(leftMethodParameterNode);

		EvaluationResult result = statement.evaluate(createList(leftChoiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	public void evaluateRandomizedOne(
			MethodParameterNode leftMethodParameterNode,
			String leftChoiceValue,
			EMathRelation statementRelation,
			String rightChoiceValue,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod", null);
		methodNode.addParameter(leftMethodParameterNode);

		ChoiceNode rightChoiceNode = new ChoiceNode("Label" + rightChoiceValue, rightChoiceValue, null);
		rightChoiceNode.setParent(leftMethodParameterNode);

		rightChoiceNode.setRandomizedValue(true);
		RelationStatement statement = 
				RelationStatement.createStatementWithChoiceCondition(
						leftMethodParameterNode, statementRelation, rightChoiceNode);

		ChoiceNode leftChoiceNode = new ChoiceNode("Label" + leftChoiceValue, leftChoiceValue, null);
		leftChoiceNode.setRandomizedValue(true);

		leftChoiceNode.setParent(leftMethodParameterNode);

		EvaluationResult result = statement.evaluate(createList(leftChoiceNode));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	public void evaluateRandomizeAmbiguousOne(
			MethodParameterNode leftMethodParameterNode,
			String leftChoiceValue,
			EMathRelation statementRelation,
			String rightChoiceValue,
			AssertType assertResult) {

		MethodNode methodNode = new MethodNode("TestMethod", null);
		methodNode.addParameter(leftMethodParameterNode);

		ChoiceNode rightChoiceNode = new ChoiceNode("Label" + rightChoiceValue, rightChoiceValue, null);
		rightChoiceNode.setParent(leftMethodParameterNode);

		rightChoiceNode.setRandomizedValue(true);
		RelationStatement statement = 
				RelationStatement.createStatementWithChoiceCondition(
						leftMethodParameterNode, statementRelation, rightChoiceNode);

		ChoiceNode leftChoiceNode = new ChoiceNode("Label" + leftChoiceValue, leftChoiceValue, null);
		leftChoiceNode.setRandomizedValue(true);

		leftChoiceNode.setParent(leftMethodParameterNode);

		EvaluationResult result = 
				EvaluationResult.convertFromBoolean(
						statement.isAmbiguous(Arrays.asList(createList(leftChoiceNode)), new MessageStack(), new ExtLanguageManagerForJava()));

		if (assertResult == AssertType.TRUE) {
			assertEquals(EvaluationResult.TRUE, result);
		} else {
			assertEquals(EvaluationResult.FALSE, result);
		}
	}

	private List<ChoiceNode> createList(ChoiceNode choiceNode1) {
		return Arrays.asList(choiceNode1);
	}
	
	@Test
	public void evaluateForStrings() {

		MethodParameterNode leftParam = 
				new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);

		evaluateOne(leftParam, "a", EMathRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.EQUAL, "A", AssertType.FALSE);

		evaluateOne(leftParam, "a", EMathRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.NOT_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_THAN, "b", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(leftParam, "abc", EMathRelation.LESS_THAN, "abd", AssertType.TRUE);
		evaluateOne(leftParam, "abc", EMathRelation.LESS_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(leftParam, "abc", EMathRelation.NOT_EQUAL, "abd", AssertType.TRUE);
		evaluateOne(leftParam, "abc", EMathRelation.EQUAL, "abd", AssertType.FALSE);
	}

	public void evaluateForIntegerTypes(String parameterType) {

		MethodParameterNode leftParam = new MethodParameterNode("par1", parameterType, "", false, null);

		evaluateOne(leftParam, "1", EMathRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, "1", EMathRelation.NOT_EQUAL, "1", AssertType.FALSE);
		evaluateOne(leftParam, "1", EMathRelation.EQUAL, "2", AssertType.FALSE);
		evaluateOne(leftParam, "1", EMathRelation.NOT_EQUAL, "2", AssertType.TRUE);

		evaluateOne(leftParam, "1", EMathRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(leftParam, "1", EMathRelation.LESS_EQUAL,    "2", AssertType.TRUE);
		evaluateOne(leftParam, "1", EMathRelation.GREATER_THAN,  "2", AssertType.FALSE);
		evaluateOne(leftParam, "1", EMathRelation.GREATER_EQUAL, "2", AssertType.FALSE);

		// For integer types allow greater values than type range e.g. 256 for Byte
		evaluateOne(leftParam, "99999", EMathRelation.LESS_THAN, "100000", AssertType.TRUE); 

		evaluateOne(leftParam, "1", EMathRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(leftParam, "1", EMathRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(leftParam, "1", EMathRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(leftParam, "1", EMathRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(leftParam, "1", EMathRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(leftParam, "1", EMathRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, "a", EMathRelation.EQUAL,         "1", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.NOT_EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.LESS_THAN,     "1", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.LESS_EQUAL,    "1", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_THAN,  "1", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_EQUAL, "1", AssertType.FALSE);
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

		evaluateRandomizedOne(methodParameterNode, "2:2", EMathRelation.GREATER_THAN, "2:2", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "10:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "11:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:-1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.GREATER_THAN, "1:9", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "2:2", EMathRelation.EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizedOne(methodParameterNode, "2:2", EMathRelation.LESS_EQUAL, "2:2", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "11:100", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizedOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "1:9", AssertType.TRUE);

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

	public void evaluateForFloatTypes(String parameterType) {

		MethodParameterNode leftParam = new MethodParameterNode("par1", parameterType, "", false, null);

		evaluateOne(leftParam, "1", EMathRelation.EQUAL,     "1", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EMathRelation.EQUAL,   "1.0", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EMathRelation.EQUAL,   "1", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.EQUAL,   "1.000", AssertType.FALSE);
		evaluateOne(leftParam, "1234.5678", EMathRelation.EQUAL,   "1234.5678", AssertType.TRUE);		

		evaluateOne(leftParam, "1",   EMathRelation.NOT_EQUAL, "1",   AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.NOT_EQUAL, "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.EQUAL,     "2.0", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.NOT_EQUAL, "2.0", AssertType.TRUE);

		evaluateOne(leftParam, "1", EMathRelation.LESS_THAN,     "2", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EMathRelation.LESS_THAN,     "2.0", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EMathRelation.LESS_EQUAL,    "2.0", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EMathRelation.GREATER_THAN,  "2.0", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.GREATER_EQUAL, "2.0", AssertType.FALSE);

		evaluateOne(leftParam, "99999", EMathRelation.LESS_THAN, "100000", AssertType.TRUE);

		evaluateOne(leftParam, "1.0", EMathRelation.EQUAL,         "a", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.NOT_EQUAL,     "a", AssertType.TRUE);
		evaluateOne(leftParam, "1.0", EMathRelation.LESS_THAN,     "a", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.LESS_EQUAL,    "a", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.GREATER_THAN,  "a", AssertType.FALSE);
		evaluateOne(leftParam, "1.0", EMathRelation.GREATER_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, "a", EMathRelation.EQUAL,         "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.NOT_EQUAL,     "1.0", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.LESS_THAN,     "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.LESS_EQUAL,    "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_THAN,  "1.0", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_EQUAL, "1.0", AssertType.FALSE);		
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

		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EMathRelation.EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EMathRelation.NOT_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.NOT_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.NOT_EQUAL, "10:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.NOT_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.NOT_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.NOT_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.NOT_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.NOT_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.NOT_EQUAL, "1:9", AssertType.TRUE);

		evaluateRandomizeAmbiguousOne(methodParameterNode, "2:2", EMathRelation.LESS_EQUAL, "2:2", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "9:100", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "10:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "11:100", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:-1", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:0", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-10:1", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "-1:11", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_EQUAL, "1:9", AssertType.TRUE);

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

		evaluateRandomizeAmbiguousOne(methodParameterNode, "0:10", EMathRelation.LESS_THAN, "1:2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1:10", EMathRelation.LESS_THAN, "2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "1:10", EMathRelation.GREATER_THAN, "2", AssertType.TRUE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "5:10", EMathRelation.LESS_THAN, "1:4", AssertType.FALSE);
		evaluateRandomizeAmbiguousOne(methodParameterNode, "5:10", EMathRelation.LESS_THAN, "1", AssertType.FALSE);
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
	public void evaluateForFloat() {
		evaluateForFloatTypes(JavaLanguageHelper.TYPE_NAME_FLOAT);
	}

	@Test
	public void evaluateForDouble() {
		evaluateForFloatTypes(JavaLanguageHelper.TYPE_NAME_DOUBLE);
	}	

	@Test
	public void evaluateForBoolean() {

		MethodParameterNode leftParam = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_BOOLEAN, "", false, null);

		evaluateOne(leftParam, "true", EMathRelation.EQUAL, "true", AssertType.TRUE);
		evaluateOne(leftParam, "true", EMathRelation.EQUAL, "false", AssertType.FALSE);

		evaluateOne(leftParam, "true", EMathRelation.EQUAL, "x", AssertType.FALSE);
		evaluateOne(leftParam, "true", EMathRelation.NOT_EQUAL, "x", AssertType.TRUE);
		evaluateOne(leftParam, "x", EMathRelation.EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, "x", EMathRelation.NOT_EQUAL, "false", AssertType.TRUE);

		evaluateOne(leftParam, "true", EMathRelation.LESS_THAN, "true", AssertType.FALSE);
		evaluateOne(leftParam, "true", EMathRelation.LESS_THAN, "false", AssertType.FALSE);
		evaluateOne(leftParam, "true", EMathRelation.LESS_EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, "true", EMathRelation.LESS_EQUAL, "false", AssertType.FALSE);

		evaluateOne(leftParam, "true", EMathRelation.GREATER_THAN, "true", AssertType.FALSE);
		evaluateOne(leftParam, "true", EMathRelation.GREATER_THAN, "false", AssertType.FALSE);
		evaluateOne(leftParam, "true", EMathRelation.GREATER_EQUAL, "true", AssertType.FALSE);
		evaluateOne(leftParam, "true", EMathRelation.GREATER_EQUAL, "false", AssertType.FALSE);
	}

	@Test
	public void evaluateChar() {

		MethodParameterNode leftParam = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_CHAR, "", false, null);

		evaluateOne(leftParam, "a", EMathRelation.EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.NOT_EQUAL, "a", AssertType.FALSE);

		evaluateOne(leftParam, "a", EMathRelation.EQUAL, "b", AssertType.FALSE);
		evaluateOne(leftParam, "a", EMathRelation.NOT_EQUAL, "b", AssertType.TRUE);

		evaluateOne(leftParam, "a", EMathRelation.LESS_THAN, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_THAN, "b", AssertType.FALSE);

		evaluateOne(leftParam, "a", EMathRelation.LESS_EQUAL, "b", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_EQUAL, "b", AssertType.FALSE);

		evaluateOne(leftParam, "a", EMathRelation.LESS_EQUAL, "a", AssertType.TRUE);
		evaluateOne(leftParam, "a", EMathRelation.GREATER_EQUAL, "a", AssertType.TRUE);


		evaluateOne(leftParam, "b", EMathRelation.EQUAL, "a", AssertType.FALSE);
		evaluateOne(leftParam, "b", EMathRelation.NOT_EQUAL, "a", AssertType.TRUE);

		evaluateOne(leftParam, "b", EMathRelation.LESS_THAN, "a", AssertType.FALSE);
		evaluateOne(leftParam, "b", EMathRelation.GREATER_THAN, "a", AssertType.TRUE);

		evaluateOne(leftParam, "b", EMathRelation.LESS_EQUAL, "a", AssertType.FALSE);
		evaluateOne(leftParam, "b", EMathRelation.GREATER_EQUAL, "a", AssertType.TRUE);
	}	

	@Test
	public void copyAndEqualityTest() {
		MethodParameterNode leftParam = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		MethodParameterNode rightParam = new MethodParameterNode("par2", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);

		RelationStatement statement = 
				RelationStatement.createStatementWithParameterCondition(
						leftParam, EMathRelation.EQUAL, rightParam);

		RelationStatement copy = statement.getCopy();

		boolean result = statement.compare(copy);
		assertEquals(true, result);

	}

	@Test
	public void updateReferencesTest() {
		MethodNode method1 = new MethodNode("method1", null);
		MethodParameterNode method1LeftParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		method1.addParameter(method1LeftParameterNode);
		MethodParameterNode method1RightParameterNode = new MethodParameterNode("par2", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		method1.addParameter(method1RightParameterNode);

		RelationStatement statement = 
				RelationStatement.createStatementWithParameterCondition(
						method1LeftParameterNode, EMathRelation.EQUAL, method1RightParameterNode);

		MethodNode method2 = new MethodNode("method2", null);
		MethodParameterNode method2LeftParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		method2.addParameter(method2LeftParameterNode);
		MethodParameterNode method2RightParameterNode = new MethodParameterNode("par2", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		method2.addParameter(method2RightParameterNode);


		ParameterCondition parameterCondition = (ParameterCondition)statement.getCondition();

		assertNotEquals(method2LeftParameterNode.hashCode(), statement.getLeftParameter().hashCode());
		assertNotEquals(method2RightParameterNode.hashCode(), parameterCondition.getRightParameterNode().hashCode());

		statement.updateReferences(method2);

		assertEquals(method2LeftParameterNode.hashCode(), statement.getLeftParameter().hashCode());
		assertEquals(method2RightParameterNode.hashCode(), parameterCondition.getRightParameterNode().hashCode());
	}	

}
