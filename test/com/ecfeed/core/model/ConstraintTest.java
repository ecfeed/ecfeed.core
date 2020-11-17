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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;

public class ConstraintTest {
	@Test
	public void testEvaluate() {
		AbstractStatement trueStatement = new StaticStatement(true, null); 
		AbstractStatement falseStatement = new StaticStatement(false, null); 
		List<ChoiceNode> values = new ArrayList<ChoiceNode>();

		assertTrue(new Constraint("c", null, falseStatement, falseStatement).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint("c", null, falseStatement, trueStatement).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint("c", null, trueStatement, trueStatement).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint("c", null, trueStatement, falseStatement).evaluate(values) == EvaluationResult.FALSE);
	}

	@Test
	public void testSetPremise() {
		AbstractStatement statement1 = new StaticStatement(true, null); 
		AbstractStatement statement2 = new StaticStatement(false, null); 
		AbstractStatement statement3 = new StaticStatement(false, null);

		Constraint constraint = new Constraint("c", null, statement1, statement2);
		assertTrue(constraint.getPremise().equals(statement1));
		constraint.setPremise(statement3);
		assertTrue(constraint.getPremise().equals(statement3));
	}

	@Test
	public void testSetConsequence() {
		AbstractStatement statement1 = new StaticStatement(true, null); 
		AbstractStatement statement2 = new StaticStatement(false, null); 
		AbstractStatement statement3 = new StaticStatement(false, null);

		Constraint constraint = new Constraint("c", null, statement1, statement2);
		assertTrue(constraint.getConsequence().equals(statement2));
		constraint.setConsequence(statement3);
		assertTrue(constraint.getConsequence().equals(statement3));
	}

	@Test
	public void testMentions() {
		ChoiceNode choice = new ChoiceNode("choice", null, null);
		MethodParameterNode parameter = new MethodParameterNode("parameter", "type", "0", false, null);
		parameter.addChoice(choice);

		AbstractStatement mentioningStatement = 
				RelationStatement.createStatementWithChoiceCondition(parameter, EMathRelation.EQUAL, choice);
		AbstractStatement notMentioningStatement = new StaticStatement(false, null);

		assertTrue(new Constraint("c", null, mentioningStatement, notMentioningStatement).mentions(parameter));
		assertTrue(new Constraint("c", null, mentioningStatement, notMentioningStatement).mentions(choice));

		assertTrue(new Constraint("c", null, notMentioningStatement, mentioningStatement).mentions(parameter));
		assertTrue(new Constraint("c", null, notMentioningStatement, mentioningStatement).mentions(choice));

		assertTrue(new Constraint("c", null, mentioningStatement, mentioningStatement).mentions(parameter));
		assertTrue(new Constraint("c", null, mentioningStatement, mentioningStatement).mentions(choice));

		assertFalse(new Constraint("c", null, notMentioningStatement, notMentioningStatement).mentions(parameter));
		assertFalse(new Constraint("c", null, notMentioningStatement, notMentioningStatement).mentions(choice));

	}

	@Test
	public void testTupleWithNullsForValueCondition() {

		AbstractStatement premise = createPremiseWithValueCondition();
		AbstractStatement consequence = createConsequenceWithValueCondition();

		Constraint constraint = new Constraint("c", null, premise, consequence);

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2", null);

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}

	@Test
	public void testTupleWithNullsForChoiceCondition() {

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2", null);

		AbstractStatement premise = createPremiseWithChoiceCondition(choice1);
		AbstractStatement consequence = createConsequenceWithChoiceCondition(choice2);

		Constraint constraint = new Constraint("c", null, premise, consequence);

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}

	@Test
	public void testTupleWithNullsForParameterCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "type", "0", false, null);
		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "type", "0", false, null);

		AbstractStatement premise = createStatementWithParameterCondition(parameter1, parameter2);
		AbstractStatement consequence = createStatementWithParameterCondition(parameter1, parameter2);

		Constraint constraint = new Constraint("c", null, premise, consequence);

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2", null);

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}


	@Test
	public void testTupleWithNullConsequenceForChoiceCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "type", "0", false, null);
		ChoiceNode choice11 = new ChoiceNode("choice11", "value11", null);
		ChoiceNode choice12 = new ChoiceNode("choice12", "value12", null);
		choice11.setParent(parameter1);
		choice12.setParent(parameter1);

		AbstractStatement premise = 
				RelationStatement.createStatementWithChoiceCondition(
						parameter1, EMathRelation.EQUAL, choice11);


		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "type", "0", false, null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2", null);
		choice2.setParent(parameter2);

		AbstractStatement consequence = 
				RelationStatement.createStatementWithChoiceCondition(
						parameter2, EMathRelation.EQUAL, choice2);

		MethodNode methodNode = new MethodNode("methodNode", null);
		methodNode.addParameter(parameter1);
		methodNode.addParameter(parameter2);
		parameter1.setParent(methodNode);
		parameter2.setParent(methodNode);

		Constraint constraint = new Constraint("c", null, premise, consequence);

		List<ChoiceNode> values = new ArrayList<ChoiceNode>();
		values.add(choice12);
		values.add(null);
		assertTrue(constraint.evaluate(values) == EvaluationResult.TRUE);
	}


	private void evaluateConstraintWithNullValues(Constraint constraint, ChoiceNode choice1, ChoiceNode choice2) {

		List<ChoiceNode> values = new ArrayList<ChoiceNode>();
		values.add(null);
		values.add(null);
		assertTrue(constraint.evaluate(values) == EvaluationResult.INSUFFICIENT_DATA);

		values.clear();
		values.add(choice1);
		values.add(null);
		assertTrue(constraint.evaluate(values) == EvaluationResult.INSUFFICIENT_DATA);

		values.clear();
		values.add(null);
		values.add(choice2);
		assertTrue(constraint.evaluate(values) == EvaluationResult.INSUFFICIENT_DATA);
	}

	private AbstractStatement createPremiseWithValueCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "int", "0", false, null);

		AbstractStatement premise = 
				RelationStatement.createStatementWithValueCondition(
						parameter1, EMathRelation.EQUAL, "A");

		return premise;
	}

	private AbstractStatement createConsequenceWithValueCondition() {

		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "int", "0", false, null);

		AbstractStatement consequence = 
				RelationStatement.createStatementWithValueCondition(
						parameter2, EMathRelation.EQUAL, "C");

		return consequence;
	}

	private AbstractStatement createPremiseWithChoiceCondition(ChoiceNode choiceNode) {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "int", "0", false, null);

		AbstractStatement premise = 
				RelationStatement.createStatementWithChoiceCondition(
						parameter1, EMathRelation.EQUAL, choiceNode);

		return premise;
	}

	private AbstractStatement createConsequenceWithChoiceCondition(ChoiceNode choiceNode) {

		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "int", "0", false, null);

		AbstractStatement consequence = 
				RelationStatement.createStatementWithChoiceCondition(
						parameter2, EMathRelation.EQUAL, choiceNode);

		return consequence;
	}

	private AbstractStatement createStatementWithParameterCondition(
			MethodParameterNode parameter1, MethodParameterNode parameter2) {

		AbstractStatement premise = 
				RelationStatement.createStatementWithParameterCondition(
						parameter1, EMathRelation.EQUAL, parameter2);

		return premise;
	}

}
