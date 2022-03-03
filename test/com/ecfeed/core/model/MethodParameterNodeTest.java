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

import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class MethodParameterNodeTest {

	@Test
	public void createParameterTest() {

		try {
			new MethodParameterNode("par%1", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodParameterNode("!", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodParameterNode("a b", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void attachDetachSingleChoiceTest() {

		final String methodName = "method";

		MethodNode methodNode = new MethodNode(methodName, null);

		// create and add parameter, choice

		MethodParameterNode methodParameterNode = addParameterToMethod(methodNode);
		addNewChoiceToMethod(methodParameterNode, "choice1");

		// detach choice node

		methodParameterNode.detachChoiceNode("choice1");
		assertEquals(0, methodParameterNode.getChoiceCount());

		// check detached choice nodes

		assertEquals(1, methodParameterNode.getDetachedChoiceCount());

		ChoiceNode detachedChoiceNode = methodParameterNode.getDetachedChoices().get(0);
		assertTrue(detachedChoiceNode.isDetached());
		assertEquals("choice1", detachedChoiceNode.getName());

		// add new choice node to parameter

		ChoiceNode newChoiceNode = new ChoiceNode("newChoice1", "0", null);
		methodParameterNode.addChoice(newChoiceNode);
		assertEquals(1, methodParameterNode.getChoiceCount());

		// attach detached choice node

		methodParameterNode.attachChoiceNode(detachedChoiceNode.getName(), newChoiceNode.getName());

		assertEquals(0, methodParameterNode.getDetachedChoiceCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		ChoiceNode outChoiceNode1 = methodParameterNode.getChoices().get(0);
		assertEquals(outChoiceNode1, newChoiceNode);
		assertFalse(outChoiceNode1.isDetached());
	}

	@Test
	public void attachDetachLastChoiceTest() {

		final String methodName = "method";

		MethodNode methodNode = new MethodNode(methodName, null);

		// create and add parameter, 2 choices

		MethodParameterNode methodParameterNode = addParameterToMethod(methodNode);
		ChoiceNode oldChoiceNode1 = addNewChoiceToMethod(methodParameterNode, "choice1");
		addNewChoiceToMethod(methodParameterNode, "choice2");

		// detach the last choice node

		methodParameterNode.detachChoiceNode("choice2");
		assertEquals(1, methodParameterNode.getChoiceCount());

		// check detached choice nodes

		assertEquals(1, methodParameterNode.getDetachedChoiceCount());

		ChoiceNode detachedChoiceNode = methodParameterNode.getDetachedChoices().get(0);
		assertTrue(detachedChoiceNode.isDetached());
		assertEquals("choice2", detachedChoiceNode.getName());

		// add new choice node to parameter

		ChoiceNode newChoiceNode = new ChoiceNode("newChoice2", "0", null);
		methodParameterNode.addChoice(newChoiceNode);
		assertEquals(2, methodParameterNode.getChoiceCount());

		// attach detached choice node

		methodParameterNode.attachChoiceNode(detachedChoiceNode.getName(), newChoiceNode.getName());
		assertEquals(0, methodParameterNode.getDetachedChoiceCount());
		assertEquals(2, methodParameterNode.getChoiceCount());

		ChoiceNode outChoiceNode2 = methodParameterNode.getChoices().get(1);
		assertEquals(outChoiceNode2, newChoiceNode);
		assertFalse(outChoiceNode2.isDetached());

		ChoiceNode outChoiceNode1 = methodParameterNode.getChoices().get(0);
		assertEquals(outChoiceNode1, oldChoiceNode1);
	}

	@Test
	public void attachDetachFirstChoiceTest() {

		final String methodName = "method";

		MethodNode methodNode = new MethodNode(methodName, null);

		// create and add parameter, 2 choices

		MethodParameterNode methodParameterNode = addParameterToMethod(methodNode);
		addNewChoiceToMethod(methodParameterNode, "choice1");
		ChoiceNode oldChoiceNode2 = addNewChoiceToMethod(methodParameterNode, "choice2");

		// detach the first choice node

		methodParameterNode.detachChoiceNode("choice1");
		assertEquals(1, methodParameterNode.getChoiceCount());

		// check detached choice nodes

		assertEquals(1, methodParameterNode.getDetachedChoiceCount());

		ChoiceNode detachedChoiceNode = methodParameterNode.getDetachedChoices().get(0);
		assertTrue(detachedChoiceNode.isDetached());
		assertEquals("choice1", detachedChoiceNode.getName());

		// add new choice node to parameter

		ChoiceNode newChoiceNode = new ChoiceNode("newChoice1", "0", null);
		methodParameterNode.addChoice(newChoiceNode, 0);
		assertEquals(2, methodParameterNode.getChoiceCount());

		// attach detached choice node

		methodParameterNode.attachChoiceNode(detachedChoiceNode.getName(), newChoiceNode.getName());
		assertEquals(0, methodParameterNode.getDetachedChoiceCount());
		assertEquals(2, methodParameterNode.getChoiceCount());

		ChoiceNode outChoiceNode1 = methodParameterNode.getChoices().get(0);
		assertEquals(outChoiceNode1, newChoiceNode);
		assertFalse(outChoiceNode1.isDetached());

		ChoiceNode outChoiceNode2 = methodParameterNode.getChoices().get(1);
		assertEquals(outChoiceNode2, oldChoiceNode2);
	}

	@Test
	public void attachDetachChoiceWithConstraintAndTestCaseTest() {

		final String methodName = "method";
		final String oldChoiceNodeName = "old";
		final String newChoiceNodeName = "new";

		MethodNode methodNode = new MethodNode(methodName, null);

		// create and add parameter, choice, test case and constraint

		MethodParameterNode methodParameterNode = addParameterToMethod(methodNode);
		ChoiceNode oldChoiceNode = addNewChoiceToMethod(methodParameterNode, oldChoiceNodeName);
		addNewTestCaseToMethod(methodNode, oldChoiceNode);
		addNewSimpleConstraintToMethod(methodNode, methodParameterNode, oldChoiceNode);

		// detach choice node

		ChoiceNode choiceNode = methodParameterNode.getChoices().get(0);

		methodParameterNode.detachChoiceNode(choiceNode.getName());
		assertEquals(0, methodParameterNode.getChoiceCount());

		// check detached choice nodes

		assertEquals(1, methodParameterNode.getDetachedChoiceCount());

		ChoiceNode detachedChoiceNode = methodParameterNode.getDetachedChoices().get(0);
		assertTrue(detachedChoiceNode.isDetached());

		// check choice node from test case

		TestCaseNode testCaseNode = methodNode.getTestCases().get(0);
		List<ChoiceNode> testData = testCaseNode.getTestData();
		ChoiceNode choiceFromTestCase = testData.get(0);
		assertEquals(detachedChoiceNode, choiceFromTestCase);

		// check choice nodes from constraint

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode);
		assertEquals(detachedChoiceNode, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode);
		assertEquals(detachedChoiceNode, choiceNodeFromPostcondition);

		// add new choice node to parameter

		ChoiceNode newChoiceNode = new ChoiceNode(newChoiceNodeName, "0", null);
		methodParameterNode.addChoice(newChoiceNode);
		assertEquals(1, methodParameterNode.getChoiceCount());

		// attach detached choice node

		methodParameterNode.attachChoiceNode(detachedChoiceNode.getName(), newChoiceNode.getName());

		assertEquals(0, methodParameterNode.getDetachedChoiceCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		ChoiceNode choiceNode1 = methodParameterNode.getChoices().get(0);
		assertEquals(choiceNode1, newChoiceNode);

		assertFalse(choiceNode1.isDetached());

		// check choice node from test case

		testCaseNode = methodNode.getTestCases().get(0);
		testData = testCaseNode.getTestData();
		choiceFromTestCase = testData.get(0);
		assertEquals(newChoiceNode, choiceFromTestCase);

		// check choice nodes from constraint

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode);
		assertEquals(newChoiceNode, choiceNodeFromPrecondition);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode);
		assertEquals(newChoiceNode, choiceNodeFromPostcondition);
	}

	@Test
	public void attachWithUniqueNamesTest() {

		final String methodName = "method";

		MethodNode methodNode = new MethodNode(methodName, null);

		// create and add parameter, choice

		MethodParameterNode methodParameterNode = addParameterToMethod(methodNode);
		addNewChoiceToMethod(methodParameterNode, "choice1");

		String detachedChoiceName = methodParameterNode.detachChoiceNode("choice1");
		assertEquals("choice1", detachedChoiceName);
		
		// add new choice node with the same name 

		ChoiceNode newChoiceNode = new ChoiceNode("choice1", "0", null);
		methodParameterNode.addChoice(newChoiceNode);

		// detach - the name should be unique

		detachedChoiceName = methodParameterNode.detachChoiceNode("choice1");
		assertEquals("choice1-1", detachedChoiceName);

		// add the second choice node with the same name
		
		newChoiceNode = new ChoiceNode("choice1", "0", null);
		methodParameterNode.addChoice(newChoiceNode);
		
		// detach - the name should be unique

		detachedChoiceName = methodParameterNode.detachChoiceNode("choice1");
		assertEquals("choice1-2", detachedChoiceName);
	}
	
	private MethodParameterNode addParameterToMethod(MethodNode methodNode) {
		MethodParameterNode methodParameterNode = new MethodParameterNode("name", "type", "0", false, null);
		methodNode.addParameter(methodParameterNode);
		return methodParameterNode;
	}

	private ChoiceNode addNewChoiceToMethod(MethodParameterNode methodParameterNode, String oldChoiceNodeName) {

		ChoiceNode oldChoiceNode = new ChoiceNode(oldChoiceNodeName, "0", null);
		assertFalse(oldChoiceNode.isDetached());
		methodParameterNode.addChoice(oldChoiceNode);

		return oldChoiceNode;
	}

	private void addNewTestCaseToMethod(MethodNode methodNode, ChoiceNode oldChoiceNode) {

		List<ChoiceNode> listOfChoicesForTestCase = new ArrayList<ChoiceNode>();
		listOfChoicesForTestCase.add(oldChoiceNode);

		TestCaseNode testCaseNode = new TestCaseNode("name", null, listOfChoicesForTestCase);
		methodNode.addTestCase(testCaseNode);
	}

	private void addNewSimpleConstraintToMethod(
			MethodNode methodNode,
			MethodParameterNode methodParameterNode,
			ChoiceNode choiceNode) {

		RelationStatement relationStatement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode, EMathRelation.EQUAL, choiceNode);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode, EMathRelation.LESS_THAN, choiceNode);

		Constraint constraint = new Constraint(
				"c", 
				ConstraintType.EXTENDED_FILTER, 
				relationStatement1, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode("cn", constraint, null);

		methodNode.addConstraint(constraintNode);
	}

	private ChoiceNode getChoiceNodeFromConstraintPostcondition(MethodNode methodNode) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(0);

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();

		ChoiceNode choiceNode = getChoiceNodeFromChoiceCondition(postcondition);

		return choiceNode;
	}

	private ChoiceNode getChoiceNodeFromConstraintPrecondition(MethodNode methodNode) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(0);

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();

		ChoiceNode choiceNode = getChoiceNodeFromChoiceCondition(precondition);

		return choiceNode;
	}

	private ChoiceNode getChoiceNodeFromChoiceCondition(AbstractStatement abstractStatement) {

		RelationStatement relationStatement = (RelationStatement)abstractStatement; 

		IStatementCondition statementCondition = relationStatement.getCondition();

		ChoiceCondition choiceCondition = (ChoiceCondition)statementCondition;

		ChoiceNode choiceNode = choiceCondition.getRightChoice();

		return choiceNode;
	}


}
