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

	@Test
	public void attachDetachParameterNodeTest() {

		// TODO ADD TEST CASES

		MethodNode methodNode = new MethodNode("method", null);

		// create and add parameter, choice

		String par1Name = "par1";
		final String oldChoiceName = "oldChoice";

		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
		ChoiceNode oldChoiceNode = addNewChoiceToMethodParameter(oldMethodParameterNode, oldChoiceName, "0");

		addNewTestCaseToMethod(methodNode, oldChoiceNode);
		addNewSimpleConstraintToMethod(methodNode, oldMethodParameterNode, oldChoiceNode);

		// detaching

		methodNode.detachParameterNode(par1Name);
		assertTrue(oldMethodParameterNode.isDetached());
		assertTrue(oldChoiceNode.isDetached());

		assertEquals(0, methodNode.getParametersCount());
		assertEquals(1, methodNode.getDetachedParametersCount());

		// check choice node from test case

		TestCaseNode testCaseNode = methodNode.getTestCases().get(0);
		List<ChoiceNode> testData = testCaseNode.getTestData();
		ChoiceNode choiceFromTestCase = testData.get(0);
		assertEquals(oldChoiceNode, choiceFromTestCase);

		// check choice nodes from constraint

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode);
		assertEquals(oldChoiceNode, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode);
		assertEquals(oldChoiceNode, choiceNodeFromPostcondition);

		// adding new parameter

		String newPar1Name = "newPar1";
		MethodParameterNode newMethodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");

		// adding new choice
		String newChoiceName = "newChoice";
		ChoiceNode newChoiceNode = addNewChoiceToMethodParameter(newMethodParameterNode, newChoiceName, "0");

		// choice conversion list 
		List<ChoiceConversionItem> choiceConversionItems = new ArrayList<>();
		ChoiceConversionItem choiceConversionItem = new ChoiceConversionItem(oldChoiceName, newChoiceName);
		choiceConversionItems.add(choiceConversionItem);

		// attach - should replace old choice with new choice and oldParameter with new parameter

		methodNode.attachParameterNode(par1Name, newPar1Name, choiceConversionItems);

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(0, methodNode.getDetachedParametersCount());

		// check parameter from constraint

		MethodParameterNode methodParameterNodeFromConstraint = 
				getMethodParameterNodeFromConstraintPrecondition(methodNode);
		assertEquals(methodParameterNodeFromConstraint, newMethodParameterNode);

		methodParameterNodeFromConstraint = 
				getMethodParameterNodeFromConstraintPostcondition(methodNode);
		assertEquals(methodParameterNodeFromConstraint, newMethodParameterNode);


		// check choice nodes from constraint

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode);
		assertEquals(choiceNodeFromPrecondition, newChoiceNode);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode);
		assertEquals(choiceNodeFromPostcondition, newChoiceNode);
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

	private void addNewTestCaseToMethod(MethodNode methodNode, ChoiceNode choiceNode) {

		List<ChoiceNode> listOfChoicesForTestCase = new ArrayList<ChoiceNode>();
		listOfChoicesForTestCase.add(choiceNode);

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
