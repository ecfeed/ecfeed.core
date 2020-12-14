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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;

import static org.junit.Assert.*;

public class ConstraintTest {

	@Test
	public void createSignatureWithStaticStatementsTest() {

		MethodNode methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "1", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice1", "7", null);
		methodParameterNode1.addChoice(choiceNode11);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "int", "1", false, null);
		methodNode.addParameter(methodParameterNode2);

		// static statements

		StaticStatement falseStatement = new StaticStatement(false, null);

		StaticStatement trueStatement = new StaticStatement(true, null);

		// constraint with basic filter

		Constraint constraint =
				new Constraint(
						"c",
						ConstraintType.BASIC_FILTER,
						trueStatement,
						falseStatement,
						null);

		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		String signature = constraint.createSignature(extLanguageManager);

		assertEquals("false", signature);

		// constraint with extended filter

		constraint.setType(ConstraintType.EXTENDED_FILTER);

		signature = constraint.createSignature(extLanguageManager);
		assertEquals("true => false", signature);

		// value condition

		RelationStatement relationStatement =
				RelationStatement.createRelationStatementWithValueCondition(methodParameterNode1, EMathRelation.EQUAL, "5");

		constraint.setPostcondition(relationStatement);

		signature = constraint.createSignature(extLanguageManager);
		assertEquals("true => par1=5", signature);

		// label condition

		relationStatement =
				RelationStatement.createRelationStatementWithLabelCondition(methodParameterNode1, EMathRelation.EQUAL, "label1");
		constraint.setPostcondition(relationStatement);

		signature = constraint.createSignature(extLanguageManager);
		assertEquals("true => par1=label1[label]", signature);

		// choice condition

		relationStatement =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode11);
		constraint.setPostcondition(relationStatement);

		signature = constraint.createSignature(extLanguageManager);
		assertEquals("true => par1=choice1[choice]", signature);

		// parameter condition

		relationStatement =
				RelationStatement.createRelationStatementWithParameterCondition(methodParameterNode1, EMathRelation.EQUAL, methodParameterNode2);
		constraint.setPostcondition(relationStatement);

		signature = constraint.createSignature(extLanguageManager);
		assertEquals("true => par1=par2[parameter]", signature);
	}

	@Test
	public void createSignatureWithAssignmetStatementTest() {

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "1", true, null);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "int", "2", true, null);

		ChoiceNode choiceNode21 = new ChoiceNode("choice1", "5", null);
		methodParameterNode2.addChoice(choiceNode21);

		// precondition - static

		StaticStatement precondition = new StaticStatement(true, null);

		// postcondition - assignment with value condition

		StatementArray postconditionStatementArray = new StatementArray(StatementArrayOperator.ASSIGN, null);

		AssignmentStatement assignmentWithValueCondition =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode2, "3");

		postconditionStatementArray.addStatement(assignmentWithValueCondition);

		// invariant constraint

		Constraint constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						precondition,
						postconditionStatementArray,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		String signature = constraint.createSignature(extLanguageManager);

		assertEquals("true => (par2:=3)", signature);

		// postcondition - assignment with value condition

		AssignmentStatement assignmentWithChoiceCondition =
				AssignmentStatement.createAssignmentWithChoiceCondition(methodParameterNode2, choiceNode21);

		postconditionStatementArray.addStatement(assignmentWithChoiceCondition);

		signature = constraint.createSignature(extLanguageManager);
		assertEquals("true => (par2:=3 , par2:=choice1[choice])", signature);

		//  postcondition - assignment with parameter conditions

		AssignmentStatement assignmentStatementWithParameterCondition =
				AssignmentStatement.createAssignmentWithParameterCondition(methodParameterNode2, methodParameterNode1);

		postconditionStatementArray.addStatement(assignmentStatementWithParameterCondition);

		signature = constraint.createSignature(extLanguageManager);
		assertEquals("true => (par2:=3 , par2:=choice1[choice] , par2:=par1[parameter])", signature);
	}

	@Test
	public void verifyConstraintTest() {

		MethodNode  methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		// statements

		StaticStatement falseStatement = new StaticStatement(false, null);

		StaticStatement trueStatement = new StaticStatement(true, null);

		RelationStatement relationStatementWithChoice =
				RelationStatement.createRelationStatementWithChoiceCondition(
					methodParameterNode1,
					EMathRelation.EQUAL,
					choiceNode1);

		// constraints

		Constraint constraint =
				new Constraint(
						"c",
						ConstraintType.BASIC_FILTER,
						trueStatement,
						trueStatement,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		constraint =
				new Constraint(
						"c",
						ConstraintType.EXTENDED_FILTER,
						trueStatement,
						trueStatement,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		constraint =
				new Constraint(
						"c",
						ConstraintType.EXTENDED_FILTER,
						relationStatementWithChoice,
						trueStatement,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		constraint =
				new Constraint(
						"c",
						ConstraintType.BASIC_FILTER,
						relationStatementWithChoice,
						trueStatement,
						null);

		try {
			constraint.assertIsCorrect(new ExtLanguageManagerForJava());
			fail();
		} catch (Exception e) {
		}

		constraint =
				new Constraint(
						"c",
						ConstraintType.BASIC_FILTER,
						falseStatement,
						trueStatement,
						null);

		try {
			constraint.assertIsCorrect(new ExtLanguageManagerForJava());
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void verifyExpectedOutputConstraintTest() {

		MethodNode  methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "int", "0", true, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		// statements

		StaticStatement falseStatement = new StaticStatement(false, null);

		StaticStatement trueStatement = new StaticStatement(true, null);

		// constraints with static statements

		Constraint constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						trueStatement,
						trueStatement,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						falseStatement,
						trueStatement,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		// relation statement as precondition - ok

		RelationStatement relationStatementWithChoiceAndEqual =
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode1,
						EMathRelation.EQUAL,
						choiceNode1);

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						relationStatementWithChoiceAndEqual,
						trueStatement,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		// relation statement as postcondition - err

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						relationStatementWithChoiceAndEqual,
						relationStatementWithChoiceAndEqual,
						null);

		try {
			constraint.assertIsCorrect(new ExtLanguageManagerForJava());
			fail();
		} catch (Exception e) {
		}

		// assignment statement with choice condition

		AssignmentStatement assignmentStatement =
				AssignmentStatement.createAssignmentWithChoiceCondition(
						methodParameterNode2, choiceNode1);

		StatementArray statementArray = new StatementArray(StatementArrayOperator.ASSIGN, null);
		statementArray.addStatement(assignmentStatement);

		EMathRelation mathRelation = assignmentStatement.getRelation();
		assertEquals(EMathRelation.ASSIGN, mathRelation);
		assertEquals(EvaluationResult.TRUE, assignmentStatement.evaluate(null));

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						relationStatementWithChoiceAndEqual,
						statementArray,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		// assignment statement with parameter condition

		assignmentStatement =
				AssignmentStatement.createAssignmentWithParameterCondition(
						methodParameterNode2, methodParameterNode1);

		StatementArray statementArray1 = new StatementArray(StatementArrayOperator.ASSIGN, null);
		statementArray1.addStatement(assignmentStatement);

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						relationStatementWithChoiceAndEqual,
						statementArray1,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		// assignment statement with value condition

		assignmentStatement =
				AssignmentStatement.createAssignmentWithValueCondition(
						methodParameterNode2, "5");

		StatementArray statementArray2 = new StatementArray(StatementArrayOperator.ASSIGN, null);
		statementArray2.addStatement(assignmentStatement);

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						relationStatementWithChoiceAndEqual,
						statementArray2,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		// constraint with array of statements and one static statement

		StatementArray assignmentStatementArray = new StatementArray(StatementArrayOperator.AND, null);
		assignmentStatementArray.addStatement(trueStatement);

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						relationStatementWithChoiceAndEqual,
						assignmentStatementArray,
						null);
		try {
			constraint.assertIsCorrect(new ExtLanguageManagerForJava());
			fail();
		} catch (Exception e) {
		}

		// constraint with array of statements

		statementArray = new StatementArray(StatementArrayOperator.ASSIGN, null);
		statementArray.addStatement(assignmentStatement);

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						relationStatementWithChoiceAndEqual,
						statementArray,
						null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());


		// constraint with array of statements and OR operator

		statementArray = new StatementArray(StatementArrayOperator.OR, null);
		statementArray.addStatement(assignmentStatement);

		constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						relationStatementWithChoiceAndEqual,
						statementArray,
						null);

		try {
			constraint.assertIsCorrect(new ExtLanguageManagerForJava());
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testEvaluateOfExtendedFilter() {

		AbstractStatement trueStatement = new StaticStatement(true, null); 
		AbstractStatement falseStatement = new StaticStatement(false, null); 
		List<ChoiceNode> values = new ArrayList<ChoiceNode>();

		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, falseStatement, falseStatement, null).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, falseStatement, trueStatement, null).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, trueStatement, trueStatement, null).evaluate(values) == EvaluationResult.TRUE);
		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, trueStatement, falseStatement, null).evaluate(values) == EvaluationResult.FALSE);
	}

	@Test
	public void evaluateAssignmentConstraint() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode("par",  "int", "0", true, null);

		StaticStatement precondition =
				new StaticStatement(false, null);

		AssignmentStatement assignmentStatement =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode, "9");

		StatementArray postcondition = new StatementArray(StatementArrayOperator.ASSIGN, null);
		postcondition.addStatement(assignmentStatement);

		Constraint constraint  =
				new Constraint("cn", ConstraintType.ASSIGNMENT, precondition, postcondition, null);

		constraint.assertIsCorrect(new ExtLanguageManagerForJava());

		List<ChoiceNode> choiceNodes  =  new ArrayList<>();

		assertEquals(EvaluationResult.TRUE,  constraint.evaluate(choiceNodes));
	}

	@Test
	public void testSetPrecondition() {
		AbstractStatement statement1 = new StaticStatement(true, null); 
		AbstractStatement statement2 = new StaticStatement(false, null); 
		AbstractStatement statement3 = new StaticStatement(false, null);

		Constraint constraint = new Constraint("c", ConstraintType.EXTENDED_FILTER, statement1, statement2, null);
		assertTrue(constraint.getPrecondition().equals(statement1));
		constraint.setPrecondition(statement3);
		assertTrue(constraint.getPrecondition().equals(statement3));
	}

	@Test
	public void testSetPostcondition() {
		AbstractStatement statement1 = new StaticStatement(true, null); 
		AbstractStatement statement2 = new StaticStatement(false, null); 
		AbstractStatement statement3 = new StaticStatement(false, null);

		Constraint constraint = new Constraint("c", ConstraintType.EXTENDED_FILTER, statement1, statement2, null);
		assertTrue(constraint.getPostcondition().equals(statement2));
		constraint.setPostcondition(statement3);
		assertTrue(constraint.getPostcondition().equals(statement3));
	}

	@Test
	public void testMentions() {
		ChoiceNode choice = new ChoiceNode("choice", null, null);
		MethodParameterNode parameter = new MethodParameterNode("parameter", "type", "0", false, null);
		parameter.addChoice(choice);

		AbstractStatement mentioningStatement = 
				RelationStatement.createRelationStatementWithChoiceCondition(parameter, EMathRelation.EQUAL, choice);
		AbstractStatement notMentioningStatement = new StaticStatement(false, null);

		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, mentioningStatement, notMentioningStatement, null).mentions(parameter));
		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, mentioningStatement, notMentioningStatement, null).mentions(choice));

		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, notMentioningStatement, mentioningStatement, null).mentions(parameter));
		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, notMentioningStatement, mentioningStatement, null).mentions(choice));

		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, mentioningStatement, mentioningStatement, null).mentions(parameter));
		assertTrue(new Constraint("c", ConstraintType.EXTENDED_FILTER, mentioningStatement, mentioningStatement, null).mentions(choice));

		assertFalse(new Constraint("c", ConstraintType.EXTENDED_FILTER, notMentioningStatement, notMentioningStatement, null).mentions(parameter));
		assertFalse(new Constraint("c", ConstraintType.EXTENDED_FILTER, notMentioningStatement, notMentioningStatement, null).mentions(choice));

	}

	@Test
	public void testTupleWithNullsForValueCondition() {

		AbstractStatement precondition = createPreconditionWithValueCondition();
		AbstractStatement postcondition = createPostconditionWithValueCondition();

		Constraint constraint = new Constraint("c", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2", null);

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}

	@Test
	public void testTupleWithNullsForChoiceCondition() {

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2", null);

		AbstractStatement precondition = createPreconditionWithChoiceCondition(choice1);
		AbstractStatement postcondition = createPostconditionWithChoiceCondition(choice2);

		Constraint constraint = new Constraint("c", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}

	@Test
	public void testTupleWithNullsForParameterCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "type", "0", false, null);
		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "type", "0", false, null);

		AbstractStatement precondition = createStatementWithParameterCondition(parameter1, parameter2);
		AbstractStatement postcondition = createStatementWithParameterCondition(parameter1, parameter2);

		Constraint constraint = new Constraint("c", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ChoiceNode choice1 = new ChoiceNode("choice1", "value1", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2", null);

		evaluateConstraintWithNullValues(constraint, choice1, choice2);			
	}


	@Test
	public void testTupleWithNullPostconditionForChoiceCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "type", "0", false, null);
		ChoiceNode choice11 = new ChoiceNode("choice11", "value11", null);
		ChoiceNode choice12 = new ChoiceNode("choice12", "value12", null);
		choice11.setParent(parameter1);
		choice12.setParent(parameter1);

		AbstractStatement precondition =
				RelationStatement.createRelationStatementWithChoiceCondition(
						parameter1, EMathRelation.EQUAL, choice11);


		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "type", "0", false, null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "value2", null);
		choice2.setParent(parameter2);

		AbstractStatement postcondition =
				RelationStatement.createRelationStatementWithChoiceCondition(
						parameter2, EMathRelation.EQUAL, choice2);

		MethodNode methodNode = new MethodNode("methodNode", null);
		methodNode.addParameter(parameter1);
		methodNode.addParameter(parameter2);
		parameter1.setParent(methodNode);
		parameter2.setParent(methodNode);

		Constraint constraint = new Constraint("c", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

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

	private AbstractStatement createPreconditionWithValueCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "int", "0", false, null);

		AbstractStatement precondition =
				RelationStatement.createRelationStatementWithValueCondition(
						parameter1, EMathRelation.EQUAL, "A");

		return precondition;
	}

	private AbstractStatement createPostconditionWithValueCondition() {

		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "int", "0", false, null);

		AbstractStatement postcondition =
				RelationStatement.createRelationStatementWithValueCondition(
						parameter2, EMathRelation.EQUAL, "C");

		return postcondition;
	}

	private AbstractStatement createPreconditionWithChoiceCondition(ChoiceNode choiceNode) {

		MethodParameterNode parameter1 = new MethodParameterNode("parameter1", "int", "0", false, null);

		AbstractStatement precondition =
				RelationStatement.createRelationStatementWithChoiceCondition(
						parameter1, EMathRelation.EQUAL, choiceNode);

		return precondition;
	}

	private AbstractStatement createPostconditionWithChoiceCondition(ChoiceNode choiceNode) {

		MethodParameterNode parameter2 = new MethodParameterNode("parameter2", "int", "0", false, null);

		AbstractStatement postcondition =
				RelationStatement.createRelationStatementWithChoiceCondition(
						parameter2, EMathRelation.EQUAL, choiceNode);

		return postcondition;
	}

	private AbstractStatement createStatementWithParameterCondition(
			MethodParameterNode parameter1, MethodParameterNode parameter2) {

		AbstractStatement precondition =
				RelationStatement.createRelationStatementWithParameterCondition(
						parameter1, EMathRelation.EQUAL, parameter2);

		return precondition;
	}

}
