/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package operations;

import com.ecfeed.core.model.*;
import com.ecfeed.core.operations.ConstraintOperationChangeType;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConstraintOperationChangeTypeTest {

	@Test
	public void changeImplicationToInvariantTest() {

		MethodNode methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		RelationStatement initialPrecondition =
				RelationStatement.createStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode1);

		RelationStatement initialPostcondition =
				RelationStatement.createStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint = new Constraint("constraint", ConstraintType.IMPLICATION, initialPrecondition, initialPostcondition, null);

		Constraint initialConstraint = constraint.makeClone();

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new ConstraintOperationChangeType(
						constraintNode,
						ConstraintType.INVARIANT,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
		}

		StaticStatement truePrecondition = new StaticStatement(true, null);
		checkConstraint(constraintNode, ConstraintType.INVARIANT, truePrecondition, initialPostcondition);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
		}

		checkConstraint(constraintNode, ConstraintType.IMPLICATION, initialPrecondition, initialPostcondition);
	}

	@Test
	public void renameInvariantToImplicationTest() {

		MethodNode methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		StaticStatement initialPrecondition =
				new StaticStatement(true, null);

		RelationStatement initialPostcondition =
				RelationStatement.createStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint =
				new Constraint("constraint", ConstraintType.INVARIANT, initialPrecondition, initialPostcondition, null);

		Constraint initialConstraint = constraint.makeClone();

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new ConstraintOperationChangeType(
						constraintNode,
						ConstraintType.IMPLICATION,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
		}

		checkConstraint(constraintNode, ConstraintType.IMPLICATION, initialPrecondition, initialPostcondition);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
		}

		checkConstraint(constraintNode, ConstraintType.INVARIANT, initialPrecondition, initialPostcondition);
	}

	@Test
	public void changeInvariantToAssignmentTest() {

		MethodNode methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		StaticStatement initialPrecondition =
				new StaticStatement(true, null);

		RelationStatement initialPostcondition =
				RelationStatement.createStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint =
				new Constraint("constraint", ConstraintType.INVARIANT, initialPrecondition, initialPostcondition, null);

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new ConstraintOperationChangeType(
						constraintNode,
						ConstraintType.EXPECTED_OUTPUT,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
		}

		StatementArray statementArrayAnd = new StatementArray(StatementArrayOperator.AND, null);

		checkConstraint(constraintNode, ConstraintType.EXPECTED_OUTPUT, initialPostcondition, statementArrayAnd);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
		}

		checkConstraint(constraintNode, ConstraintType.INVARIANT, initialPrecondition, initialPostcondition);
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
