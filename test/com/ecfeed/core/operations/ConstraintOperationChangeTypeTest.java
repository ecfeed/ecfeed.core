/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StatementArrayOperator;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.operations.nodes.OnConstraintOperationChangeType;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.TestHelper;

public class ConstraintOperationChangeTypeTest {

	@Test
	public void changeToTheSameType() {

		StaticStatement initialPrecondition = new StaticStatement(true, null);

		StaticStatement initialPostcondition = new StaticStatement(true, null);

		Constraint constraint =
				new Constraint(
						"constraint",
						ConstraintType.EXTENDED_FILTER,
						initialPrecondition,
						initialPostcondition,
						null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.EXTENDED_FILTER,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, OnConstraintOperationChangeType.CANNOT_CHANGE_CONSTRAINT_TYPE_TO_THE_SAME_TYPE);
		}
	}

	@Test
	public void changeExtendedFilterToBasicFilter() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		RelationStatement initialPrecondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode1);

		RelationStatement initialPostcondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint = new Constraint("constraint", ConstraintType.EXTENDED_FILTER, initialPrecondition, initialPostcondition, null);

//		Constraint initialConstraint = constraint.makeClone();

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.BASIC_FILTER,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
			fail();
		}

		StaticStatement truePrecondition = new StaticStatement(true, null);
		checkConstraint(constraintNode, ConstraintType.BASIC_FILTER, truePrecondition, initialPostcondition);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.EXTENDED_FILTER, initialPrecondition, initialPostcondition);
	}

	@Test
	public void changeBasicFilterToExtendedFilter() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		StaticStatement initialPrecondition =
				new StaticStatement(true, null);

		RelationStatement initialPostcondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint =
				new Constraint("constraint", ConstraintType.BASIC_FILTER, initialPrecondition, initialPostcondition, null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.EXTENDED_FILTER,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.EXTENDED_FILTER, initialPrecondition, initialPostcondition);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.BASIC_FILTER, initialPrecondition, initialPostcondition);
	}

	@Test
	public void changeBasicFilterToAssignmentTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		StaticStatement initialPrecondition =
				new StaticStatement(true, null);

		RelationStatement initialPostcondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint =
				new Constraint("constraint", ConstraintType.BASIC_FILTER, initialPrecondition, initialPostcondition, null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.ASSIGNMENT,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
			fail();
		}

		StatementArray statementArrayAnd = new StatementArray(StatementArrayOperator.ASSIGN, null);

		checkConstraint(constraintNode, ConstraintType.ASSIGNMENT, initialPostcondition, statementArrayAnd);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.BASIC_FILTER, initialPrecondition, initialPostcondition);
	}

	@Test
	public void changeAssignmentToBasicFilterTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", true, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		RelationStatement initialPrecondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		AssignmentStatement assignmentWithChoiceCondition =
				AssignmentStatement.createAssignmentWithChoiceCondition(methodParameterNode2, choiceNode2);

		StatementArray postconditionStatementArray = new StatementArray(StatementArrayOperator.ASSIGN, null);
		postconditionStatementArray.addStatement(assignmentWithChoiceCondition);


		Constraint constraint =
				new Constraint("constraint", ConstraintType.ASSIGNMENT, initialPrecondition, postconditionStatementArray, null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.BASIC_FILTER,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
			fail();
		}

		StaticStatement truePrecondition = new StaticStatement(true, null);
		checkConstraint(constraintNode, ConstraintType.BASIC_FILTER, truePrecondition, initialPrecondition);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.ASSIGNMENT, initialPrecondition, postconditionStatementArray);
	}

	@Test
	public void changeArrayAssignmentToBasicFilterTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", true, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		RelationStatement initialPrecondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		AssignmentStatement assignmentStatement =
				AssignmentStatement.createAssignmentWithChoiceCondition(methodParameterNode2, choiceNode2);

		StatementArray statementArray = new StatementArray(StatementArrayOperator.ASSIGN, null);
		statementArray.addStatement(assignmentStatement);

		Constraint constraint =
				new Constraint("constraint", ConstraintType.ASSIGNMENT, initialPrecondition, statementArray, null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.BASIC_FILTER,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
			fail();
		}

		StaticStatement truePrecondition = new StaticStatement(true, null);
		checkConstraint(constraintNode, ConstraintType.BASIC_FILTER, truePrecondition, initialPrecondition);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.ASSIGNMENT, initialPrecondition, statementArray);
	}

	@Test
	public void changeExtendedFilterToAssignmentTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		RelationStatement initialPrecondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode1);

		RelationStatement initialPostcondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint =
				new Constraint("constraint", ConstraintType.EXTENDED_FILTER, initialPrecondition, initialPostcondition, null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.ASSIGNMENT,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
			fail();
		}

		StatementArray statementArrayAnd = new StatementArray(StatementArrayOperator.ASSIGN, null);

		checkConstraint(constraintNode, ConstraintType.ASSIGNMENT, initialPrecondition, statementArrayAnd);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.EXTENDED_FILTER, initialPrecondition, initialPostcondition);
	}

	@Test
	public void changeAssignment1ToExtendedFilterTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		// precondition as relation statement

		RelationStatement initialPrecondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode1);

		StatementArray initialPostcondition =
				new StatementArray(StatementArrayOperator.ASSIGN, null);

		Constraint constraint =
				new Constraint("constraint", ConstraintType.ASSIGNMENT, initialPrecondition, initialPostcondition, null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.EXTENDED_FILTER,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
			fail();
		}

		StaticStatement staticTrueStatement = new StaticStatement(true, null);

		checkConstraint(constraintNode, ConstraintType.EXTENDED_FILTER, initialPrecondition, staticTrueStatement);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.ASSIGNMENT, initialPrecondition, initialPostcondition);
	}

	@Test
	public void changeAssignment2ToExtendedFilterTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		// precondition as statement array

		StatementArray initialPrecondition = new StatementArray(StatementArrayOperator.AND, null);

		initialPrecondition.addStatement(
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode1));

		StatementArray initialPostcondition =
				new StatementArray(StatementArrayOperator.ASSIGN, null);

		Constraint constraint =
				new Constraint("constraint", ConstraintType.ASSIGNMENT, initialPrecondition, initialPostcondition, null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new OnConstraintOperationChangeType(
						constraintNode,
						ConstraintType.EXTENDED_FILTER,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
			fail();
		}

		StaticStatement staticTrueStatement = new StaticStatement(true, null);

		checkConstraint(constraintNode, ConstraintType.EXTENDED_FILTER, initialPrecondition, staticTrueStatement);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
			fail();
		}

		checkConstraint(constraintNode, ConstraintType.ASSIGNMENT, initialPrecondition, initialPostcondition);
	}

	public void checkConstraint(
			ConstraintNode constraintNode,
			ConstraintType constraintType,
			AbstractStatement initialPrecondition,
			AbstractStatement initialPostcondition) {

		final Constraint constraint = constraintNode.getConstraint();

		assertEquals(constraintType, constraint.getType());

		// checking precondition

		AbstractStatement precondition = constraint.getPrecondition();

		assertTrue(precondition.isEqualTo(initialPrecondition));

		// checking postcondition

		AbstractStatement postcondition = constraint.getPostcondition();

		assertTrue(postcondition.isEqualTo(initialPostcondition));
	}

}
