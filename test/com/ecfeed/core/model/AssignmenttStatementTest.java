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

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AssignmenttStatementTest {

	@Test
	public void assignmentStatementTest() {

		// method node

		MethodNode methodNode = new MethodNode("method", null);

		// method parameter 1 node with choice

		MethodParameterNode methodParameterNode1 = new MethodParameterNode(
				"par1",
				"int",
				"1",
				false,
				null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// method parameter 2 node with choice

		MethodParameterNode methodParameterNode2 = new MethodParameterNode(
				"par2",
				"int",
				"2",
				true,
				null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode21 = new ChoiceNode("choice21", "21",  null);
		methodParameterNode2.addChoice(choiceNode21);

		// assignment with value condition

		AbstractStatement assignmentWithValueCondition =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode2, "5");

		ChoiceNode testCaseChoiceNode1 = new ChoiceNode("result1", "0", null);
		ChoiceNode testCaseChoiceNode2 = new ChoiceNode("result2", "0", null);

		List<ChoiceNode> testCaseChoiceNodes = new ArrayList<>();
		testCaseChoiceNodes.add(testCaseChoiceNode1);
		testCaseChoiceNodes.add(testCaseChoiceNode2);

		assignmentWithValueCondition.setExpectedValue(testCaseChoiceNodes);

		ChoiceNode resultChoiceNode = testCaseChoiceNodes.get(0);
		String resultValue = resultChoiceNode.getValueString();
		assertEquals("0", resultValue);

		resultChoiceNode = testCaseChoiceNodes.get(1);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("5", resultValue);

		// assignment with parameter condition

		AbstractStatement assignmentWithParameterCondition =
				AssignmentStatement.createAssignmentWithParameterCondition(methodParameterNode2, methodParameterNode1);

		testCaseChoiceNode1 = new ChoiceNode("result1", "33", null);
		testCaseChoiceNode2 = new ChoiceNode("result2", "0", null);

		testCaseChoiceNodes = new ArrayList<>();
		testCaseChoiceNodes.add(testCaseChoiceNode1);
		testCaseChoiceNodes.add(testCaseChoiceNode2);

		assignmentWithParameterCondition.setExpectedValue(testCaseChoiceNodes);

		resultChoiceNode = testCaseChoiceNodes.get(0);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("33", resultValue);

		resultChoiceNode = testCaseChoiceNodes.get(1);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("33", resultValue);

		// assignment with choice condition

		ChoiceNode choiceNodeForStatement = new ChoiceNode("choice", "123", null);

		AbstractStatement assignmentWithChoiceCondition =
				AssignmentStatement.createAssignmentWithChoiceCondition(methodParameterNode2, choiceNodeForStatement);

		testCaseChoiceNode1 = new ChoiceNode("result1", "1", null);
		testCaseChoiceNode2 = new ChoiceNode("result2", "2", null);

		testCaseChoiceNodes = new ArrayList<>();
		testCaseChoiceNodes.add(testCaseChoiceNode1);
		testCaseChoiceNodes.add(testCaseChoiceNode2);

		assignmentWithChoiceCondition.setExpectedValue(testCaseChoiceNodes);

		resultChoiceNode = testCaseChoiceNodes.get(0);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("1", resultValue);

		resultChoiceNode = testCaseChoiceNodes.get(1);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("123", resultValue);
	}

	@Test
	public void assignmentStatementArrayTest() {

		// method node

		MethodNode methodNode = new MethodNode("method", null);

		// method parameter 1 node with choice

		MethodParameterNode methodParameterNode1 = new MethodParameterNode(
				"par1",
				"int",
				"1",
				false,
				null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// method parameter 2 node with choice

		MethodParameterNode methodParameterNode2 = new MethodParameterNode(
				"par2",
				"int",
				"2",
				true,
				null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode21 = new ChoiceNode("choice21", "21",  null);
		methodParameterNode2.addChoice(choiceNode21);

		// method parameter 3 node with choice

        MethodParameterNode methodParameterNode3 = new MethodParameterNode(
                "par3",
                "int",
                "3",
                true,
                null);
        methodNode.addParameter(methodParameterNode3);

        ChoiceNode choiceNode31 = new ChoiceNode("choice31", "31",  null);
        methodParameterNode3.addChoice(choiceNode31);

		// assignment with value condition

		StatementArray statementArray  = new StatementArray(StatementArrayOperator.ASSIGN, null);

		AbstractStatement assignmentWithValueCondition =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode2, "5");

		statementArray.addStatement(assignmentWithValueCondition);

        AbstractStatement assignmentWithValueCondition2 =
                AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode3, "6");

        statementArray.addStatement(assignmentWithValueCondition2);

		ChoiceNode testCaseChoiceNode1 = new ChoiceNode("result1", "0", null);
		ChoiceNode testCaseChoiceNode2 = new ChoiceNode("result2", "0", null);
        ChoiceNode testCaseChoiceNode3 = new ChoiceNode("result3", "0", null);

		List<ChoiceNode> testCaseChoiceNodes = new ArrayList<>();
		testCaseChoiceNodes.add(testCaseChoiceNode1);
		testCaseChoiceNodes.add(testCaseChoiceNode2);
        testCaseChoiceNodes.add(testCaseChoiceNode3);

		statementArray.setExpectedValues(testCaseChoiceNodes);

		ChoiceNode resultChoiceNode = testCaseChoiceNodes.get(0);
		String resultValue = resultChoiceNode.getValueString();
		assertEquals("0", resultValue);

		resultChoiceNode = testCaseChoiceNodes.get(1);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("5", resultValue);

        resultChoiceNode = testCaseChoiceNodes.get(2);
        resultValue = resultChoiceNode.getValueString();
        assertEquals("6", resultValue);

		// assignment with parameter condition

        statementArray  = new StatementArray(StatementArrayOperator.ASSIGN, null);

		AbstractStatement assignmentWithParameterCondition =
				AssignmentStatement.createAssignmentWithParameterCondition(methodParameterNode2, methodParameterNode1);
		statementArray.addStatement(assignmentWithParameterCondition);

        AbstractStatement assignmentWithParameterCondition2 =
                AssignmentStatement.createAssignmentWithParameterCondition(methodParameterNode3, methodParameterNode1);
        statementArray.addStatement(assignmentWithParameterCondition2);

		testCaseChoiceNode1 = new ChoiceNode("result1", "33", null);
		testCaseChoiceNode2 = new ChoiceNode("result2", "0", null);
        testCaseChoiceNode3 = new ChoiceNode("result3", "0", null);

		testCaseChoiceNodes = new ArrayList<>();
		testCaseChoiceNodes.add(testCaseChoiceNode1);
		testCaseChoiceNodes.add(testCaseChoiceNode2);
        testCaseChoiceNodes.add(testCaseChoiceNode3);

		statementArray.setExpectedValues(testCaseChoiceNodes);

		resultChoiceNode = testCaseChoiceNodes.get(0);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("33", resultValue);

		resultChoiceNode = testCaseChoiceNodes.get(1);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("33", resultValue);

        resultChoiceNode = testCaseChoiceNodes.get(2);
        resultValue = resultChoiceNode.getValueString();
        assertEquals("33", resultValue);

		// assignment with choice condition

        statementArray  = new StatementArray(StatementArrayOperator.ASSIGN, null);

		ChoiceNode choiceNodeForStatement1 = new ChoiceNode("choice", "123", null);

		AbstractStatement assignmentWithChoiceCondition1 =
				AssignmentStatement.createAssignmentWithChoiceCondition(methodParameterNode2, choiceNodeForStatement1);
		statementArray.addStatement((assignmentWithChoiceCondition1));

        ChoiceNode choiceNodeForStatement2 = new ChoiceNode("choice", "567", null);

        AbstractStatement assignmentWithChoiceCondition2 =
                AssignmentStatement.createAssignmentWithChoiceCondition(methodParameterNode3, choiceNodeForStatement2);
        statementArray.addStatement((assignmentWithChoiceCondition2));

		testCaseChoiceNode1 = new ChoiceNode("result1", "1", null);
		testCaseChoiceNode2 = new ChoiceNode("result2", "2", null);
        testCaseChoiceNode3 = new ChoiceNode("result3", "3", null);

		testCaseChoiceNodes = new ArrayList<>();
		testCaseChoiceNodes.add(testCaseChoiceNode1);
		testCaseChoiceNodes.add(testCaseChoiceNode2);
        testCaseChoiceNodes.add(testCaseChoiceNode3);

		statementArray.setExpectedValues(testCaseChoiceNodes);

		resultChoiceNode = testCaseChoiceNodes.get(0);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("1", resultValue);

		resultChoiceNode = testCaseChoiceNodes.get(1);
		resultValue = resultChoiceNode.getValueString();
		assertEquals("123", resultValue);

        resultChoiceNode = testCaseChoiceNodes.get(2);
        resultValue = resultChoiceNode.getValueString();
        assertEquals("567", resultValue);
	}

}
