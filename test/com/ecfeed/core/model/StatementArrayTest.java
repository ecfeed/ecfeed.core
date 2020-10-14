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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;

public class StatementArrayTest {

	private static MethodNode fMethod;
	private static MethodParameterNode fParameter1;
	private static ChoiceNode fChoice11;
	private static ChoiceNode fChoice12;
	private static ChoiceNode fChoice13;
	private static MethodParameterNode fParameter2;
	private static ChoiceNode fChoice21;
	private static ChoiceNode fChoice22;
	private static ChoiceNode fChoice23;

	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method", null);
		fParameter1 = new MethodParameterNode("parameter1", "type", "0", false, null);
		fChoice11 = new ChoiceNode("choice11", null, null);
		fChoice12 = new ChoiceNode("choice12", null, null);
		fChoice13 = new ChoiceNode("choice13", null, null);
		fParameter1.addChoice(fChoice11);
		fParameter1.addChoice(fChoice12);
		fParameter1.addChoice(fChoice13);
		fParameter2 = new MethodParameterNode("parameter2", "type", "0", false, null);
		fChoice21 = new ChoiceNode("choice21", null, null);
		fChoice22 = new ChoiceNode("choice22", null, null);
		fChoice23 = new ChoiceNode("choice23", null, null);
		fParameter2.addChoice(fChoice21);
		fParameter2.addChoice(fChoice22);
		fParameter2.addChoice(fChoice23);
		fMethod.addParameter(fParameter1);
		fMethod.addParameter(fParameter2);
	}


	@Test
	public void testEvaluate() {
		StatementArray arrayOr = new StatementArray(EStatementOperator.OR, null);
		StatementArray arrayAnd = new StatementArray(EStatementOperator.AND, null);
		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice11);

		RelationStatement statement2 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter2, EMathRelation.EQUAL, fChoice21);

		arrayOr.addStatement(statement1);
		arrayOr.addStatement(statement2);
		arrayAnd.addStatement(statement1);
		arrayAnd.addStatement(statement2);

		List<ChoiceNode> bothFulfill = new ArrayList<ChoiceNode>();
		bothFulfill.add(fChoice11);
		bothFulfill.add(fChoice21);
		assertTrue(arrayOr.evaluate(bothFulfill) == EvaluationResult.TRUE);
		assertTrue(arrayAnd.evaluate(bothFulfill) == EvaluationResult.TRUE);

		List<ChoiceNode> oneFulfills = new ArrayList<ChoiceNode>();
		oneFulfills.add(fChoice12);
		oneFulfills.add(fChoice21);
		assertTrue(arrayOr.evaluate(oneFulfills) == EvaluationResult.TRUE);
		assertTrue(arrayAnd.evaluate(oneFulfills) == EvaluationResult.FALSE);

		List<ChoiceNode> noneFulfills = new ArrayList<ChoiceNode>();
		noneFulfills.add(fChoice12);
		noneFulfills.add(fChoice22);
		assertTrue(arrayOr.evaluate(noneFulfills) == EvaluationResult.FALSE);
		assertTrue(arrayAnd.evaluate(noneFulfills) == EvaluationResult.FALSE);
	}

	@Test
	public void testEvaluateAndStatementsWithNulls() {
		StatementArray arrayAnd = new StatementArray(EStatementOperator.AND, null);

		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(
						fParameter1, EMathRelation.EQUAL, fChoice11);

		arrayAnd.addStatement(statement1);


		RelationStatement statement2 = 
				RelationStatement.createStatementWithChoiceCondition(
						fParameter2, EMathRelation.EQUAL, fChoice21);

		arrayAnd.addStatement(statement2);

		List<ChoiceNode> values0 = new ArrayList<ChoiceNode>();
		values0.add(null);
		values0.add(null);
		assertTrue(arrayAnd.evaluate(values0) == EvaluationResult.INSUFFICIENT_DATA);

		List<ChoiceNode> values1A = new ArrayList<ChoiceNode>();
		values1A.add(null);
		values1A.add(fChoice21);
		assertTrue(arrayAnd.evaluate(values1A) == EvaluationResult.INSUFFICIENT_DATA);

		List<ChoiceNode> values1B = new ArrayList<ChoiceNode>();
		values1B.add(fChoice11);
		values1B.add(null);
		assertTrue(arrayAnd.evaluate(values1B) == EvaluationResult.INSUFFICIENT_DATA);

		List<ChoiceNode> values2A = new ArrayList<ChoiceNode>();
		values2A.add(null);
		values2A.add(fChoice22);
		assertTrue(arrayAnd.evaluate(values2A) == EvaluationResult.FALSE);

		List<ChoiceNode> values2B = new ArrayList<ChoiceNode>();
		values2B.add(fChoice12);
		values2B.add(null);
		assertTrue(arrayAnd.evaluate(values2B) == EvaluationResult.FALSE);
	}

	@Test
	public void testEvaluateOrStatementsWithNulls() {
		StatementArray arrayAnd = new StatementArray(EStatementOperator.OR, null);

		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(
						fParameter1, EMathRelation.EQUAL, fChoice11);

		arrayAnd.addStatement(statement1);


		RelationStatement statement2 = 
				RelationStatement.createStatementWithChoiceCondition(
						fParameter2, EMathRelation.EQUAL, fChoice21);

		arrayAnd.addStatement(statement2);

		List<ChoiceNode> values0 = new ArrayList<ChoiceNode>();
		values0.add(null);
		values0.add(null);
		assertTrue(arrayAnd.evaluate(values0) == EvaluationResult.INSUFFICIENT_DATA);

		List<ChoiceNode> values1A = new ArrayList<ChoiceNode>();
		values1A.add(null);
		values1A.add(fChoice21);
		assertTrue(arrayAnd.evaluate(values1A) == EvaluationResult.TRUE);

		List<ChoiceNode> values1B = new ArrayList<ChoiceNode>();
		values1B.add(fChoice11);
		values1B.add(null);
		assertTrue(arrayAnd.evaluate(values1B) == EvaluationResult.TRUE);

		List<ChoiceNode> values2A = new ArrayList<ChoiceNode>();
		values2A.add(null);
		values2A.add(fChoice22);
		assertTrue(arrayAnd.evaluate(values2A) == EvaluationResult.INSUFFICIENT_DATA);

		List<ChoiceNode> values2B = new ArrayList<ChoiceNode>();
		values2B.add(fChoice12);
		values2B.add(null);
		assertTrue(arrayAnd.evaluate(values2B) == EvaluationResult.INSUFFICIENT_DATA);
	}


	@Test
	public void testGetChildren() {
		StatementArray array = new StatementArray(EStatementOperator.OR, null);
		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice11);

		RelationStatement statement2 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter2, EMathRelation.EQUAL, fChoice21);

		RelationStatement statement3 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter2, EMathRelation.EQUAL, fChoice21);

		array.addStatement(statement1);
		array.addStatement(statement2);

		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
		assertFalse(array.getChildren().contains(statement3));
	}

	@Test
	public void testMentionsChoiceNode() {
		StatementArray array = new StatementArray(EStatementOperator.OR, null);
		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice11);

		RelationStatement statement2 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter2, EMathRelation.EQUAL, fChoice21);

		array.addStatement(statement1);
		array.addStatement(statement2);
		assertTrue(array.mentions(fChoice11));
		assertFalse(array.mentions(fChoice13));
	}

	@Test
	public void testMentionsParameterNode() {
		StatementArray array = new StatementArray(EStatementOperator.OR, null);
		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice11);

		array.addStatement(statement1);
		assertTrue(array.mentions((MethodParameterNode)fChoice11.getParameter()));
		assertFalse(array.mentions((MethodParameterNode)fChoice21.getParameter()));
	}

	@Test
	public void testSetOperator() {
		StatementArray array = new StatementArray(EStatementOperator.OR, null);
		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice11);

		RelationStatement statement2 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter2, EMathRelation.EQUAL, fChoice21);

		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(EStatementOperator.OR, array.getOperator());
		array.setOperator(EStatementOperator.AND);
		assertEquals(EStatementOperator.AND, array.getOperator());
		//check that children statements were not changed
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
	}

	@Test
	public void testReplaceChild() {
		StatementArray array = new StatementArray(EStatementOperator.OR, null);
		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice11);

		RelationStatement statement2 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice12);

		RelationStatement statement3 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice13);

		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));
		assertFalse(array.getChildren().contains(statement3));

		array.replaceChild(statement2, statement3);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertFalse(array.getChildren().contains(statement2));
		assertTrue(array.getChildren().contains(statement3));
	}

	@Test
	public void testRemoveChild() {
		StatementArray array = new StatementArray(EStatementOperator.OR, null);
		RelationStatement statement1 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice11);

		RelationStatement statement2 = 
				RelationStatement.createStatementWithChoiceCondition(fParameter1, EMathRelation.EQUAL, fChoice12);

		array.addStatement(statement1);
		array.addStatement(statement2);
		assertEquals(2, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertTrue(array.getChildren().contains(statement2));

		array.removeChild(statement2);
		assertEquals(1, array.getChildren().size());
		assertTrue(array.getChildren().contains(statement1));
		assertFalse(array.getChildren().contains(statement2));
	}

	/*****************compare()**********************/
	@Test
	public void compareOperatorTest(){
		StatementArray or1 = new StatementArray(EStatementOperator.OR, null);
		StatementArray or2 = new StatementArray(EStatementOperator.OR, null);
		StatementArray and1 = new StatementArray(EStatementOperator.AND, null);
		StatementArray and2 = new StatementArray(EStatementOperator.AND, null);

		assertTrue(or1.compare(or2));
		assertTrue(and1.compare(and2));
		assertFalse(or1.compare(and1));
		assertFalse(and1.compare(or1));
	}

	@Test
	public void compareChildrenTest(){
		StatementArray s1 = new StatementArray(EStatementOperator.OR, null);
		StatementArray s2 = new StatementArray(EStatementOperator.OR, null);

		StaticStatement ss1 = new StaticStatement(true, null);
		StaticStatement ss2 = new StaticStatement(true, null);
		assertTrue(s1.compare(s2));

		s1.addStatement(ss1);
		assertFalse(s1.compare(s2));
		s2.addStatement(ss2);
		assertTrue(s1.compare(s2));

		ss1.setValue(false);;
		assertFalse(s1.compare(s2));
		ss2.setValue(false);
		assertTrue(s1.compare(s2));
	}
}
