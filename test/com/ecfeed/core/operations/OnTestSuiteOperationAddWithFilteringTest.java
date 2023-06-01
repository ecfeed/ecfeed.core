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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.nodes.OnTestSuiteOperationAddWithFiltering;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IntegerHolder;

public class OnTestSuiteOperationAddWithFilteringTest {

	@Test
	public void shouldFilterNotRandomizedTestCasesForPositiveAndNegativeFilters() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "int", "0", true, null);

		ChoiceNode choiceNode1 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "choice1", "1", false, true, null);

		ChoiceNode choiceNode2 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "choice2", "2", false, true, null);

		// constraint

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, EMathRelation.EQUAL, choiceNode1);

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.BASIC_FILTER, precondition, postcondition, null);

		ConstraintNode constraintNode = ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		List<ConstraintNode> constraintNodes = new ArrayList<>();
		constraintNodes.add(constraintNode);

		// list of test caseNodes needed for operation 

		// test case 1

		List<ChoiceNode> choiceNodes1 = new ArrayList<>();
		choiceNodes1.add(choiceNode1);

		MethodNodeHelper.addNewTestCase(methodNode, "suite1", choiceNodes1, true);

		// test case 2

		List<ChoiceNode> choiceNodes2 = new ArrayList<>();
		choiceNodes2.add(choiceNode2);

		MethodNodeHelper.addNewTestCase(methodNode, "suite1", choiceNodes2, true);

		// operation for positive filter

		List<TestCaseNode> testCaseNodes = methodNode.getTestCases();

		IntegerHolder countOfAddedTestCases = new IntegerHolder(0);

		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		OnTestSuiteOperationAddWithFiltering operation =
				new OnTestSuiteOperationAddWithFiltering(
						methodNode, 
						testCaseNodes, 
						"suite2", 
						constraintNodes, 
						OnTestSuiteOperationAddWithFiltering.TestCasesFilteringDirection.POSITIVE, 
						true, 
						countOfAddedTestCases, 
						extLanguageManagerForJava);

		// execute and check 

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}

		assertEquals(1, countOfAddedTestCases.get());

		List<TestCaseNode> sourceTestCaseNodesAfterOperation = methodNode.getTestCases("suite1");
		assertEquals(2, sourceTestCaseNodesAfterOperation.size());

		List<TestCaseNode> dstTestCaseNodesAfterOperation = methodNode.getTestCases("suite2");
		assertEquals(1, dstTestCaseNodesAfterOperation.size());

		TestCaseNode dstTestCaseNode = dstTestCaseNodesAfterOperation.get(0);
		ChoiceNode dstChoiceNode = dstTestCaseNode.getChoices().get(0);
		assertEquals(choiceNode1.getName(), dstChoiceNode.getName());

		// undo and check

		try {
			operation.getReverseOperation().execute();
		} catch (Exception e) {
			fail();
		}

		List<TestCaseNode> sourceTestCaseNodesAfterUndo = methodNode.getTestCases("suite1");
		assertEquals(2, sourceTestCaseNodesAfterUndo.size());

		List<TestCaseNode> dstTestCaseNodesAfterUndo = methodNode.getTestCases("suite2");
		assertEquals(0, dstTestCaseNodesAfterUndo.size());

		// operation for negative filter

		countOfAddedTestCases = new IntegerHolder(0);

		operation =
				new OnTestSuiteOperationAddWithFiltering(
						methodNode, 
						testCaseNodes, 
						"suite2", 
						constraintNodes, 
						OnTestSuiteOperationAddWithFiltering.TestCasesFilteringDirection.NEGATIVE, 
						true, 
						countOfAddedTestCases, 
						extLanguageManagerForJava);

		// execute and check 

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}

		assertEquals(1, countOfAddedTestCases.get());

		dstTestCaseNodesAfterOperation = methodNode.getTestCases("suite2");
		assertEquals(1, dstTestCaseNodesAfterOperation.size());

		dstTestCaseNode = dstTestCaseNodesAfterOperation.get(0);
		dstChoiceNode = dstTestCaseNode.getChoices().get(0);
		assertEquals(choiceNode2.getName(), dstChoiceNode.getName());
	}

	@Test
	public void shouldFilterRandomizedTestCases() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "int", "0", true, null);

		ChoiceNode choiceNode1 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "choice1", "1:2", true, true, null);

		ChoiceNode choiceNode2 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "choice2", "2:3", true, true, null);

		// constraint

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, EMathRelation.EQUAL, choiceNode1);

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.BASIC_FILTER, precondition, postcondition, null);

		ConstraintNode constraintNode = ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		List<ConstraintNode> constraintNodes = new ArrayList<>();
		constraintNodes.add(constraintNode);

		// list of test caseNodes needed for operation 

		// test case 1

		List<ChoiceNode> choiceNodes1 = new ArrayList<>();
		choiceNodes1.add(choiceNode1);

		MethodNodeHelper.addNewTestCase(methodNode, "suite1", choiceNodes1, true);

		// test case 2

		List<ChoiceNode> choiceNodes2 = new ArrayList<>();
		choiceNodes2.add(choiceNode2);

		MethodNodeHelper.addNewTestCase(methodNode, "suite1", choiceNodes2, true);

		// variables operation 

		List<TestCaseNode> testCaseNodes = methodNode.getTestCases();

		IntegerHolder countOfAddedTestCases = new IntegerHolder(0);

		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();
		
		// filter with ambiguous test cases

		OnTestSuiteOperationAddWithFiltering operation =
				new OnTestSuiteOperationAddWithFiltering(
						methodNode, 
						testCaseNodes, 
						"suite2", 
						constraintNodes, 
						OnTestSuiteOperationAddWithFiltering.TestCasesFilteringDirection.POSITIVE, 
						true, 
						countOfAddedTestCases, 
						extLanguageManagerForJava);

		// execute and check 

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}

		assertEquals(2, countOfAddedTestCases.get());

		List<TestCaseNode> sourceTestCaseNodesAfterOperation = methodNode.getTestCases("suite1");
		assertEquals(2, sourceTestCaseNodesAfterOperation.size());

		List<TestCaseNode> dstTestCaseNodesAfterOperation = methodNode.getTestCases("suite2");
		assertEquals(2, dstTestCaseNodesAfterOperation.size());

		// undo

		try {
			operation.getReverseOperation().execute();
		} catch (Exception e) {
			fail();
		}
		
		// filter without ambiguous test cases
		
		countOfAddedTestCases.set(0);
		
		operation =
				new OnTestSuiteOperationAddWithFiltering(
						methodNode, 
						testCaseNodes, 
						"suite2", 
						constraintNodes, 
						OnTestSuiteOperationAddWithFiltering.TestCasesFilteringDirection.POSITIVE, 
						false, 
						countOfAddedTestCases, 
						extLanguageManagerForJava);

		// execute and check 

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(1, countOfAddedTestCases.get());
		
		dstTestCaseNodesAfterOperation = methodNode.getTestCases("suite2");
		assertEquals(1, dstTestCaseNodesAfterOperation.size());

		TestCaseNode dstTestCaseNode = dstTestCaseNodesAfterOperation.get(0);
		ChoiceNode dstChoiceNode = dstTestCaseNode.getChoices().get(0);
		assertEquals(choiceNode1.getName(), dstChoiceNode.getName());
	}
	
}
