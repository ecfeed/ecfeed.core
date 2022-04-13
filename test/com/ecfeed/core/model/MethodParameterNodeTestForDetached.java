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

import com.ecfeed.core.utils.EMathRelation;

public class MethodParameterNodeTestForDetached {

	@Test
	public void attachDetachParameterNodeTest() {
		
		// TODO ADD CONSTRAINTS AND TEST CASES

		MethodNode methodNode = new MethodNode("method", null);

		// create and add parameter, choice
		
		String par1Name = "par1";
		final String oldChoiceNodeName = "old";

		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
		ChoiceNode oldChoiceNode = addNewChoiceToMethodParameter(oldMethodParameterNode, oldChoiceNodeName, "0");
		
		addNewTestCaseToMethod(methodNode, oldChoiceNode);
		addNewSimpleConstraintToMethod(methodNode, oldMethodParameterNode, oldChoiceNode);
		
		methodNode.detachParameterNode(par1Name);
		assertTrue(oldMethodParameterNode.isDetached());
		assertTrue(oldChoiceNode.isDetached());

		assertEquals(0, methodNode.getParametersCount());
		assertEquals(1, methodNode.getDetachedParametersCount());
		
		String newPar1Name = "newPar1";
		addParameterToMethod(methodNode, newPar1Name, "String");
		
		methodNode.attachParameterNode(par1Name, newPar1Name);
		
		assertEquals(1, methodNode.getParametersCount());
		assertEquals(0, methodNode.getDetachedParametersCount());
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
	

}
