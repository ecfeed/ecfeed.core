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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.ChoiceConversionItem;
import com.ecfeed.core.utils.EMathRelation;

public class MethodParameterNodeTestForDetached {

	// TODO - add test to check if choice conversion list is complete

	@Test
	public void attachDetachParameterNodeTest() {

		MethodNode methodNode = new MethodNode("method", null);

		// create and add parameter, choice1, and child choice2

		final String par1Name = "par1";
		final String oldChoiceName1 = "oldChoice1";
		final String choiceNodeName2 = "oldChoice2";		

		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
		ChoiceNode oldChoiceNode1 = addNewChoiceToMethodParameter(oldMethodParameterNode, oldChoiceName1, "0");
		ChoiceNode oldChoiceNode2 = addNewChoiceToChoice(oldChoiceNode1, choiceNodeName2, "0");

		addNewTestCaseToMethod(methodNode, oldChoiceNode1);
		addNewSimpleConstraintToMethod(methodNode, oldMethodParameterNode, oldChoiceNode1, oldChoiceNode2);

		// detach parameter 

		methodNode.detachParameterNode(par1Name);
		assertTrue(oldMethodParameterNode.isDetached());
		assertTrue(oldChoiceNode1.isDetached());

		assertEquals(0, methodNode.getParametersCount());
		assertEquals(1, methodNode.getDetachedParametersCount());

		// check choice node 1 from test case - should not be changed

		TestCaseNode testCaseNode = methodNode.getTestCases().get(0);
		List<ChoiceNode> testData = testCaseNode.getTestData();
		ChoiceNode choiceFromTestCase1 = testData.get(0);
		assertTrue(choiceFromTestCase1.isDetached());
		assertEquals(oldChoiceNode1, choiceFromTestCase1);

		// check choice node 2 from test case - should not be changed

		ChoiceNode choiceFromTestCase2 = choiceFromTestCase1.getChoices().get(0);
		assertTrue(choiceFromTestCase2.isDetached());
		assertEquals(oldChoiceNode2, choiceFromTestCase2);

		// check choice nodes from constraint - should not be changed

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode);
		assertEquals(oldChoiceNode1, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode);
		assertEquals(oldChoiceNode2, choiceNodeFromPostcondition);

		// add new parameter and two choices to method

		String newPar1Name = "newPar1";
		MethodParameterNode newMethodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");

		String newChoiceName1 = "newChoice1";
		ChoiceNode newChoiceNode1 = addNewChoiceToMethodParameter(newMethodParameterNode, newChoiceName1, "0");

		String newChoiceName2 = "newChoice2";
		ChoiceNode newChoiceNode2 = addNewChoiceToChoice(newChoiceNode1, newChoiceName2, "0");

		// prepare choice conversion list for attachment

		List<ChoiceConversionItem> choiceConversionItems = new ArrayList<>();

		ChoiceConversionItem choiceConversionItem1 = 
				new ChoiceConversionItem(
						oldChoiceNode1.getQualifiedName(), newChoiceNode1.getQualifiedName());
		choiceConversionItems.add(choiceConversionItem1);

		ChoiceConversionItem choiceConversionItem2 = 
				new ChoiceConversionItem(
						oldChoiceNode2.getQualifiedName(), newChoiceNode2.getQualifiedName());
		choiceConversionItems.add(choiceConversionItem2);


		// attach - should replace old choice with new choice and oldParameter with new parameter

		methodNode.attachParameterNode(par1Name, newPar1Name, choiceConversionItems);

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(0, methodNode.getDetachedParametersCount());

		// check parameter from constraint - should be new 

		MethodParameterNode methodParameterNodeFromConstraint = 
				getMethodParameterNodeFromConstraintPrecondition(methodNode);
		assertEquals(methodParameterNodeFromConstraint, newMethodParameterNode);

		methodParameterNodeFromConstraint = 
				getMethodParameterNodeFromConstraintPostcondition(methodNode);
		assertEquals(methodParameterNodeFromConstraint, newMethodParameterNode);

		// check choices nodes from constraint - should be new

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode);
		assertEquals(choiceNodeFromPrecondition, newChoiceNode1);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode);
		assertEquals(choiceNodeFromPostcondition, newChoiceNode2);

		// check choice node from test case - should be new 

		testCaseNode = methodNode.getTestCases().get(0);
		testData = testCaseNode.getTestData();

		choiceFromTestCase1 = testData.get(0);
		assertEquals(choiceFromTestCase1, newChoiceNode1);

		choiceFromTestCase2 = choiceFromTestCase1.getChoices().get(0);
		assertEquals(choiceFromTestCase2, newChoiceNode2);
	}

	@Test
	public void attachWithoutChoicesTest() {

		MethodNode methodNode = new MethodNode("method", null);

		// create and add parameter, choice1, and child choice2

		final String par1Name = "par1";
		final String oldChoiceName1 = "choice1";
		final String oldChoiceName2 = "choice2";		

		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
		ChoiceNode oldChoiceNode1 = addNewChoiceToMethodParameter(oldMethodParameterNode, oldChoiceName1, "0");
		ChoiceNode oldChoiceNode2 = addNewChoiceToChoice(oldChoiceNode1, oldChoiceName2, "0");

		// detach parameter 

		methodNode.detachParameterNode(par1Name);

		// add new parameter without choices

		String newPar1Name = "newPar1";
		MethodParameterNode newMethodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");

		// attach - should create child choices in destination parameter

		methodNode.attachParameterNode(par1Name, newPar1Name, null);

		assertEquals(0, methodNode.getDetachedParametersCount());
		assertEquals(1, methodNode.getParametersCount());

		// abstract choices with child choices should be transferred to destination parameter

		List<ChoiceNode> choiceNodes1 = newMethodParameterNode.getChoices();
		assertEquals(1, choiceNodes1.size());

		ChoiceNode newChoiceNode1 = choiceNodes1.get(0);
		assertEquals(oldChoiceNode1.getQualifiedName(), newChoiceNode1.getQualifiedName());

		List<ChoiceNode> choiceNodes2  = newChoiceNode1.getChoices();
		assertEquals(1, choiceNodes2.size());

		ChoiceNode newChoiceNode2 = choiceNodes2.get(0);
		assertEquals(oldChoiceNode2.getQualifiedName(), newChoiceNode2.getQualifiedName());
	}

	@Test
	public void attachWithTheSameChoiceNameTest() {

		MethodNode methodNode = new MethodNode("method", null);

		// create and add parameter and choice1

		final String par1Name = "par1";
		final String choiceName1 = "choice1";

		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
		ChoiceNode choiceNode1 = addNewChoiceToMethodParameter(oldMethodParameterNode, choiceName1, "0");

		// detach parameter 

		methodNode.detachParameterNode(par1Name);

		// add new parameter without choices

		String newPar1Name = "newPar1";
		MethodParameterNode newMethodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");

		//  add choice with the same name to new parameter

		addNewChoiceToMethodParameter(newMethodParameterNode, choiceName1, "0");

		// attach - should create choice with name choice1-1

		methodNode.attachParameterNode(par1Name, newPar1Name, null);

		assertEquals(0, methodNode.getDetachedParametersCount());
		assertEquals(1, methodNode.getParametersCount());

		// check old choice1 and new choice1-1

		List<ChoiceNode> choiceNodes1 = newMethodParameterNode.getChoices();

		ChoiceNode newChoiceNode1 = choiceNodes1.get(0);
		String newName1 = newChoiceNode1.getQualifiedName();
		assertEquals(newName1, choiceName1);

		ChoiceNode newChoiceNode2 = choiceNodes1.get(1);
		String newName2 = newChoiceNode2.getName();
		assertEquals(newName2, choiceName1 + "-1");
	}

	private MethodParameterNode addParameterToMethod(MethodNode methodNode, String name, String type) {

		MethodParameterNode methodParameterNode = new MethodParameterNode(name, type, "0", false, null);
		methodNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

	private ChoiceNode addNewChoiceToMethodParameter(
			MethodParameterNode methodParameterNode, 
			String choiceNodeName, 
			String valueString) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, null);
		methodParameterNode.addChoice(choiceNode);

		return choiceNode;
	}

	private ChoiceNode addNewChoiceToChoice(
			ChoiceNode parentChoiceNode, String choiceNodeName, String valueString) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, null);
		parentChoiceNode.addChoice(choiceNode);

		return choiceNode;
	}

	private void addNewTestCaseToMethod(MethodNode methodNode, ChoiceNode choiceNode) {

		List<ChoiceNode> listOfChoicesForTestCase = new ArrayList<ChoiceNode>();
		listOfChoicesForTestCase.add(choiceNode);

		TestCaseNode testCaseNode = new TestCaseNode("name", null, listOfChoicesForTestCase);
		methodNode.addTestCase(testCaseNode);
	}

	private void addNewSimpleConstraintToMethod(
			MethodNode methodNode,
			MethodParameterNode methodParameterNode,
			ChoiceNode choiceNode1,
			ChoiceNode choiceNode2) {

		RelationStatement relationStatement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode, EMathRelation.EQUAL, choiceNode1);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode, EMathRelation.LESS_THAN, choiceNode2);

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

	private MethodParameterNode getMethodParameterNodeFromConstraintPrecondition(MethodNode methodNode) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(0);

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();

		RelationStatement relationStatement = (RelationStatement)precondition; 

		MethodParameterNode methodParameterNode = relationStatement.getLeftParameter();

		return methodParameterNode;
	}

	private MethodParameterNode getMethodParameterNodeFromConstraintPostcondition(MethodNode methodNode) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(0);

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();

		RelationStatement relationStatement = (RelationStatement)postcondition; 

		MethodParameterNode methodParameterNode = relationStatement.getLeftParameter();

		return methodParameterNode;
	}

}
