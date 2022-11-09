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

import org.junit.Test;

import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.utils.EMathRelation;

import static org.junit.Assert.*;

public class MethodNodeTest {

	@Test
	public void newMethodNodeTest(){

		try {
			new MethodNode("%a", null);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodNode("a b", null);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodNode("0-1", null);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testAddParameter(){
		MethodNode method = new MethodNode("name", null);
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type1", "0", false, null);
		BasicParameterNode expCat = new BasicParameterNode("expCat", "type2", "0", true, null);
		assertEquals(0, method.getParameters().size());
		method.addParameter(parameter);
		assertEquals(1, method.getParameters().size());
		assertTrue(method.getParameters().contains(parameter));
		method.addParameter(expCat);
		assertEquals(2, method.getParameters().size());
		assertTrue(method.getParameters().contains(expCat));

		assertEquals(1, method.getParametersNames(false).size());
		assertTrue(method.getParametersNames(false).contains("parameter"));

		assertEquals(1, method.getParametersNames(true).size());
		assertTrue(method.getParametersNames(true).contains("expCat"));

		assertEquals(parameter, method.findParameter("parameter"));
		assertEquals(expCat, method.findParameter("expCat"));

		assertEquals(2, method.getParameterTypes().size());
		assertTrue(method.getParameterTypes().contains("type1"));
		assertTrue(method.getParameterTypes().contains("type2"));
	}

	@Test
	public void testAddConstraint(){
		MethodNode method = new MethodNode("name", null);
		Constraint constraint1 = new Constraint("name1", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(false, null), null);
		Constraint constraint2 = new Constraint("name2", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(false, null), null);
		ConstraintNode constraintNode1 = new ConstraintNode("name1", constraint1, null);
		ConstraintNode constraintNode2 = new ConstraintNode("name2", constraint2, null);
		assertEquals(0, method.getConstraintNodes().size());
		assertEquals(0, method.getConstraints("name1").size());
		assertEquals(0, method.getConstraints("name2").size());

		method.addConstraint(constraintNode1);
		method.addConstraint(constraintNode2);

		assertEquals(method, constraintNode1.getParent());
		assertEquals(method, constraintNode2.getParent());

		assertEquals(2, method.getConstraints().size());
		assertTrue(method.getConstraints().contains(constraint1));
		assertTrue(method.getConstraints().contains(constraint2));

		assertEquals(2, method.getConstraintNodes().size());
		assertEquals(1, method.getConstraints("name1").size());
		assertTrue(method.getConstraints("name1").contains(constraint1));
		assertFalse(method.getConstraints("name1").contains(constraint2));
		assertEquals(1, method.getConstraints("name2").size());
		assertFalse(method.getConstraints("name2").contains(constraint1));
		assertTrue(method.getConstraints("name2").contains(constraint2));

		assertEquals(2, method.getConstraintsNames().size());
		assertTrue(method.getConstraintsNames().contains("name1"));
		assertTrue(method.getConstraintsNames().contains("name2"));

		method.removeConstraint(constraintNode1);
		assertFalse(method.getConstraintsNames().contains("name1"));
		assertTrue(method.getConstraintsNames().contains("name2"));

	}

	@Test
	public void testAddTestCase(){
		MethodNode method = new MethodNode("name", null);
		TestCaseNode testCase1 = new TestCaseNode("suite_1", null, new ArrayList<ChoiceNode>());
		TestCaseNode testCase2 = new TestCaseNode("suite_2", null, new ArrayList<ChoiceNode>());
		assertEquals(0, method.getTestCases().size());
		assertEquals(0, method.getTestCases("suite_1").size());
		assertEquals(0, method.getTestCases("suite_2").size());

		method.addTestCase(testCase1);
		method.addTestCase(testCase2);

		assertEquals(method, testCase1.getParent());
		assertEquals(method, testCase2.getParent());

		assertEquals(2, method.getTestCases().size());
		assertTrue(method.getTestCases().contains(testCase1));
		assertTrue(method.getTestCases().contains(testCase2));

		assertEquals(1, method.getTestCases("suite_1").size());
		assertTrue(method.getTestCases("suite_1").contains(testCase1));
		assertFalse(method.getTestCases("suite_1").contains(testCase2));

		assertEquals(1, method.getTestCases("suite_2").size());
		assertTrue(method.getTestCases("suite_2").contains(testCase2));
		assertFalse(method.getTestCases("suite_2").contains(testCase1));

		assertEquals(2, method.getTestCaseNames().size());
		assertTrue(method.getTestCaseNames().contains("suite_1"));
		assertTrue(method.getTestCaseNames().contains("suite_2"));
	}

	@Test
	public void testGetChildren(){
		MethodNode method = new MethodNode("name", null);
		TestCaseNode testCase = new TestCaseNode("test_case", null, new ArrayList<ChoiceNode>());
		ConstraintNode constraint = new ConstraintNode("constraint",
				 new Constraint("constraint", ConstraintType.EXTENDED_FILTER, new StaticStatement(false, null), new StaticStatement(false, null), null), null);
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		BasicParameterNode expCat = new BasicParameterNode("expCat", "type", "0", true, null);

		assertEquals(0, method.getChildren().size());
		assertFalse(method.hasChildren());
		method.addParameter(parameter);
		method.addParameter(expCat);
		method.addConstraint(constraint);
		method.addTestCase(testCase);

		assertEquals(4, method.getChildren().size());
		assertTrue(method.hasChildren());
		assertTrue(method.getChildren().contains(parameter));
		assertTrue(method.getChildren().contains(expCat));
		assertTrue(method.getChildren().contains(constraint));
		assertTrue(method.getChildren().contains(testCase));
		assertEquals(parameter, method.getChild("parameter"));
		assertEquals(expCat, method.getChild("expCat"));
		assertEquals(testCase, method.getChild("test_case"));
		assertEquals(constraint, method.getChild("constraint"));
	}

	@Test
	public void moveChildTest(){
		//		MethodNode method = new MethodNode("name");
		//
		//		ParameterNode parameter1 = new ParameterNode("name", "type", "0", false);
		//		ParameterNode parameter2 = new ParameterNode("name", "type", "0", false);
		//		ParameterNode parameter3 = new ParameterNode("name", "type", "0", false);
		//
		//		TestCaseNode testCase1 = new TestCaseNode("test case", new ArrayList<ChoiceNode>());
		//		TestCaseNode testCase2 = new TestCaseNode("test case", new ArrayList<ChoiceNode>());
		//		TestCaseNode testCase3 = new TestCaseNode("test case", new ArrayList<ChoiceNode>());
		//
		//		ConstraintNode constraint1 = new ConstraintNode("constraint",
		//				new Constraint(new StaticStatement(false), new StaticStatement(false)));
		//		ConstraintNode constraint2 = new ConstraintNode("constraint",
		//				new Constraint(new StaticStatement(false), new StaticStatement(false)));
		//		ConstraintNode constraint3 = new ConstraintNode("constraint",
		//				new Constraint(new StaticStatement(false), new StaticStatement(false)));
		//
		//		method.addParameter(parameter1);
		//		method.addParameter(parameter2);
		//		method.addParameter(parameter3);
		//
		//		method.addTestCase(testCase1);
		//		method.addTestCase(testCase2);
		//		method.addTestCase(testCase3);
		//
		//		method.addConstraint(constraint1);
		//		method.addConstraint(constraint2);
		//		method.addConstraint(constraint3);
		//
		//		int parameter2Index = method.getChildren().indexOf(parameter2);
		//		int testCase2Index = method.getChildren().indexOf(testCase2);
		//		int constraint2Index = method.getChildren().indexOf(constraint2);
		//
		//		method.moveChild(parameter2, true);
		//		assertEquals(parameter2Index - 1, method.getChildren().indexOf(parameter2));
		//		method.moveChild(parameter2, true); //parameter should not be moved further
		//		assertEquals(parameter2Index - 1, method.getChildren().indexOf(parameter2));
		//		method.moveChild(parameter2, false);
		//		assertEquals(parameter2Index, method.getChildren().indexOf(parameter2));
		//		method.moveChild(parameter2, false);
		//		assertEquals(parameter2Index + 1, method.getChildren().indexOf(parameter2));
		//		method.moveChild(parameter2, false); //parameter should not be moved further
		//		assertEquals(parameter2Index + 1, method.getChildren().indexOf(parameter2));
		//
		//		method.moveChild(testCase2, true);
		//		assertEquals(testCase2Index - 1, method.getChildren().indexOf(testCase2));
		//		method.moveChild(testCase2, true); //test case should not be moved further
		//		assertEquals(testCase2Index - 1, method.getChildren().indexOf(testCase2));
		//		method.moveChild(testCase2, false);
		//		assertEquals(testCase2Index, method.getChildren().indexOf(testCase2));
		//		method.moveChild(testCase2, false);
		//		assertEquals(testCase2Index + 1, method.getChildren().indexOf(testCase2));
		//		method.moveChild(testCase2, false); //test case should not be moved further
		//		assertEquals(testCase2Index + 1, method.getChildren().indexOf(testCase2));
		//
		//		method.moveChild(constraint2, true);
		//		assertEquals(constraint2Index - 1, method.getChildren().indexOf(constraint2));
		//		method.moveChild(constraint2, true); //test case should not be moved further
		//		assertEquals(constraint2Index - 1, method.getChildren().indexOf(constraint2));
		//		method.moveChild(constraint2, false);
		//		assertEquals(constraint2Index, method.getChildren().indexOf(constraint2));
		//		method.moveChild(constraint2, false);
		//		assertEquals(constraint2Index + 1, method.getChildren().indexOf(constraint2));
		//		method.moveChild(constraint2, false); //test case should not be moved further
		//		assertEquals(constraint2Index + 1, method.getChildren().indexOf(constraint2));
		//
	}

	@Test
	public void testRemoveTestCase(){
		MethodNode method = new MethodNode("name", null);
		TestCaseNode testCase1 = new TestCaseNode("name1", null, new ArrayList<ChoiceNode>());
		TestCaseNode testCase2 = new TestCaseNode("name1", null, new ArrayList<ChoiceNode>());
		TestCaseNode testCase3 = new TestCaseNode("name2", null, new ArrayList<ChoiceNode>());
		TestCaseNode testCase4 = new TestCaseNode("name2", null, new ArrayList<ChoiceNode>());

		method.addTestCase(testCase1);
		method.addTestCase(testCase2);
		method.addTestCase(testCase3);
		method.addTestCase(testCase4);

		assertEquals(4, method.getTestCases().size());

		method.removeTestCase(testCase1);

		assertEquals(3, method.getTestCases().size());
		assertFalse(method.getTestCases().contains(testCase1));

		TestSuiteNode testSuiteNode = new TestSuiteNode();
		testSuiteNode.setName("name2");
		method.removeTestSuite(testSuiteNode);
		assertEquals(1, method.getTestCases().size());
		assertFalse(method.getTestCases().contains(testCase3));
		assertFalse(method.getTestCases().contains(testCase4));
		assertTrue(method.getTestCases().contains(testCase2));
	}

	@Test
	public void removeChoicesParentParameterTest(){
		//		MethodNode method = new MethodNode("method");
		//		ParameterNode parameter = new ParameterNode("parameter", "type", "0", false);
		//		ChoiceNode choice = new ChoiceNode("choice", "0");
		//		Constraint mentioningConstraint = new Constraint(new DecomposedParameterStatement(parameter, Relation.EQUAL, choice), new StaticStatement(false));
		//		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		//		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		//		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		//		TestCaseNode testCaseNode = new TestCaseNode("name", new ArrayList<ChoiceNode>());
		//
		//		parameter.addChoice(choice);
		//		method.addParameter(parameter);
		//		method.addConstraint(notMentioningConstraintNode);
		//		method.addConstraint(mentioningConstraintNode);
		//		method.addTestCase(testCaseNode);
		//
		//		assertTrue(method.getParameters().contains(parameter));
		//		assertTrue(method.getParameters(false).contains(parameter));
		//		assertTrue(method.getConstraintNodes().contains(mentioningConstraintNode));
		//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		//		assertTrue(method.getTestCases().contains(testCaseNode));
		//
		//		assertTrue(method.removeParameter(parameter));
		//		assertFalse(method.getParameters().contains(parameter));
		//		assertFalse(method.getParameters(false).contains(parameter));
		//		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
	}

	@Test
	public void removeExpectedParameterTest(){
		//		MethodNode method = new MethodNode("method");
		//		ParameterNode parameter = new ParameterNode("parameter", "type", "0", true);
		//		parameter.setDefaultValueString("value");
		//		ChoiceNode choice = new ChoiceNode("choice", "value2");
		//		Constraint mentioningConstraint = new Constraint(new ExpectedValueStatement(parameter, choice), new StaticStatement(false));
		//		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		//		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		//		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		//		TestCaseNode testCaseNode = new TestCaseNode("name", new ArrayList<ChoiceNode>());
		//
		//		parameter.addChoice(choice);
		//		method.addParameter(parameter);
		//		method.addConstraint(notMentioningConstraintNode);
		//		method.addConstraint(mentioningConstraintNode);
		//		method.addTestCase(testCaseNode);
		//
		//		assertTrue(method.getParameters().contains(parameter));
		//		assertTrue(method.getParameters(true).contains(parameter));
		//		assertTrue(method.getConstraintNodes().contains(mentioningConstraintNode));
		//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		//		assertTrue(method.getTestCases().contains(testCaseNode));
		//
		//		assertTrue(method.removeParameter(parameter));
		//		assertFalse(method.getParameters().contains(parameter));
		//		assertFalse(method.getParameters(true).contains(parameter));
		//		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
	}

	@Test
	public void testGetExpectedParametersNames(){
		MethodNode method = new MethodNode("name", null);
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		BasicParameterNode expCat1 = new BasicParameterNode("expCat1", "type", "0", true, null);
		BasicParameterNode expCat2 = new BasicParameterNode("expCat2", "type", "0", true, null);

		method.addParameter(parameter);
		method.addParameter(expCat1);
		method.addParameter(expCat2);

		assertEquals(3, method.getParameters().size());
		assertTrue(method.getParameters().contains(parameter));
		assertTrue(method.getParameters().contains(expCat1));
		assertTrue(method.getParameters().contains(expCat2));

		assertEquals(2,  method.getParametersNames(true).size());
		assertTrue(method.getParametersNames(true).contains("expCat1"));
		assertTrue(method.getParametersNames(true).contains("expCat2"));

		method.removeParameter(expCat1);
		assertEquals(1,  method.getParametersNames(true).size());
		assertFalse(method.getParametersNames(true).contains("expCat1"));
		assertTrue(method.getParametersNames(true).contains("expCat2"));
	}

	@Test
	public void testReplaceParameterWithExpected(){
		//		MethodNode method = new MethodNode("method");
		//		ParameterNode parameter = new ParameterNode("parameter", "type","0",  false);
		//		ChoiceNode choice = new ChoiceNode("choice", "value");
		//		Constraint mentioningConstraint = new Constraint(new DecomposedParameterStatement(parameter, Relation.EQUAL, choice), new StaticStatement(false));
		//		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		//		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		//		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		//		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
		//		testData.add(choice);
		//		TestCaseNode testCaseNode = new TestCaseNode("name", testData);
		//
		//		ParameterNode newExpCat = new ParameterNode("expCat", "type", "0", true);
		//		newExpCat.setDefaultValueString("expected value");
		//		parameter.addChoice(choice);
		//		method.addParameter(parameter);
		//		method.addConstraint(notMentioningConstraintNode);
		//		method.addConstraint(mentioningConstraintNode);
		//		method.addTestCase(testCaseNode);
		//
		//		method.replaceParameter(0, newExpCat);
		//
		//		assertFalse(method.getParameters().contains(parameter));
		//		assertFalse(method.getParameters(false).contains(parameter));
		//		assertTrue(method.getParameters().contains(newExpCat));
		//		assertTrue(method.getParameters(true).contains(newExpCat));
		//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		//		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		//		assertTrue(method.getTestCases().contains(testCaseNode));
		//		assertEquals("expected value", testCaseNode.getTestData().get(0).getValueString());
	}

	@Test
	public void testReplaceParameterWithChoicesParent(){
		//		MethodNode method = new MethodNode("method");
		//		ParameterNode parameter = new ParameterNode("parameter", "type", "0", true);
		//		ChoiceNode choice = new ChoiceNode("choice", "value");
		//		Constraint mentioningConstraint = new Constraint(new ExpectedValueStatement(parameter, choice), new StaticStatement(false));
		//		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
		//		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
		//		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
		//		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
		//		testData.add(choice);
		//		TestCaseNode testCaseNode = new TestCaseNode("name", testData);
		//
		//		ParameterNode newCat = new ParameterNode("newCat", "type", "0", false);
		//		parameter.addChoice(choice);
		//		method.addParameter(parameter);
		//		method.addConstraint(notMentioningConstraintNode);
		//		method.addConstraint(mentioningConstraintNode);
		//		method.addTestCase(testCaseNode);
		//
		//		method.replaceParameter(0, newCat);
		//
		//		assertFalse(method.getParameters().contains(parameter));
		//		assertFalse(method.getParameters(true).contains(parameter));
		//		assertTrue(method.getParameters().contains(newCat));
		//		assertTrue(method.getParameters(false).contains(newCat));
		//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		//		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		//		assertEquals(0, method.getTestCases().size());
	}

	//	@Test
	//	public void testChangeParameterTypeToExpected(){
	//		MethodNode method = new MethodNode("method");
	//		ParameterNode parameter = new ParameterNode("parameter", "type", false);
	//		ChoiceNode choice = new ChoiceNode("choice", "value");
	//		Constraint mentioningConstraint = new Constraint(new DecomposedParameterStatement(parameter, Relation.EQUAL, choice), new StaticStatement(false));
	//		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
	//		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
	//		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
	//		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
	//		testData.add(choice);
	//		TestCaseNode testCaseNode = new TestCaseNode("name", testData);
	//
	//		parameter.addChoice(choice);
	//		method.addParameter(parameter);
	//		method.addConstraint(notMentioningConstraintNode);
	//		method.addConstraint(mentioningConstraintNode);
	//		method.addTestCase(testCaseNode);
	//
	//		parameter.getMethod().changeParameterExpectedStatus(parameter,true);
	//
	//		assertTrue(method.getParameters().contains(parameter));
	//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
	//		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
	//		assertTrue(method.getTestCases().contains(testCaseNode));
	//		assertEquals("value", testCaseNode.getTestData().get(0).getValueString());
	//	}

	//	@Test
	//	public void testChangeParameterTypeToDecomposed(){
	//		MethodNode method = new MethodNode("method");
	//		ParameterNode parameter = new ParameterNode("parameter", "type", true);
	//		ChoiceNode choice = new ChoiceNode("choice", "value");
	//		Constraint mentioningConstraint = new Constraint(new ExpectedValueStatement(parameter, choice), new StaticStatement(false));
	//		Constraint notMentioningConstraint = new Constraint(new StaticStatement(false), new StaticStatement(false));
	//		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint);
	//		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint);
	//		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
	//		testData.add(choice);
	//		TestCaseNode testCaseNode = new TestCaseNode("name", testData);
	//
	//		parameter.addChoice(choice);
	//		method.addParameter(parameter);
	//		method.addConstraint(notMentioningConstraintNode);
	//		method.addConstraint(mentioningConstraintNode);
	//		method.addTestCase(testCaseNode);
	//
	//		parameter.getMethod().changeParameterExpectedStatus(parameter,false);
	//
	//		assertTrue(method.getParameters().contains(parameter));
	//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
	//		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
	//		assertEquals(0, method.getTestCases().size());
	//	}

	@Test
	public void testChoiceRemoved(){
		MethodNode method = new MethodNode("method", null);
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		ChoiceNode choice = new ChoiceNode("choice", "value", null);
		Constraint mentioningConstraint = 
				new Constraint(
						"constraint",
						ConstraintType.EXTENDED_FILTER,
						RelationStatement.createRelationStatementWithChoiceCondition(
                                parameter, EMathRelation.EQUAL, choice), new StaticStatement(false, null), null);

		Constraint notMentioningConstraint =
				new Constraint("constraint", ConstraintType.EXTENDED_FILTER, new StaticStatement(false, null), new StaticStatement(false, null), null);
		ConstraintNode mentioningConstraintNode = new ConstraintNode("constraint", mentioningConstraint, null);
		ConstraintNode notMentioningConstraintNode = new ConstraintNode("constraint", notMentioningConstraint, null);
		List<ChoiceNode> mentioningTestData = new ArrayList<ChoiceNode>();
		mentioningTestData.add(choice);
		TestCaseNode mentioningTestCaseNode = new TestCaseNode("name", null, mentioningTestData);
		List<ChoiceNode> notMentioningTestData = new ArrayList<ChoiceNode>();
		mentioningTestData.add(new ChoiceNode("dummy", "0", null));
		TestCaseNode notMentioningTestCaseNode = new TestCaseNode("name", null, notMentioningTestData);

		parameter.addChoice(choice);
		method.addParameter(parameter);
		method.addConstraint(notMentioningConstraintNode);
		method.addConstraint(mentioningConstraintNode);
		method.addTestCase(mentioningTestCaseNode);
		method.addTestCase(notMentioningTestCaseNode);

		assertTrue(method.getConstraintNodes().contains(mentioningConstraintNode));
		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		assertTrue(method.getTestCases().contains(notMentioningTestCaseNode));
		assertTrue(method.getTestCases().contains(mentioningTestCaseNode));

		parameter.removeChoice(choice);
		//		assertFalse(method.getConstraintNodes().contains(mentioningConstraintNode));
		//		assertTrue(method.getConstraintNodes().contains(notMentioningConstraintNode));
		//		assertTrue(method.getTestCases().contains(notMentioningTestCaseNode));
		//		assertFalse(method.getTestCases().contains(mentioningTestCaseNode));

	}

	/************compare()*************************/
	@Test
	public void compareNameTest(){
		MethodNode m1 = new MethodNode("m", null);
		MethodNode m2 = new MethodNode("m", null);

		assertTrue(m1.isMatch(m2));
		m1.setName("m1");
		assertFalse(m1.isMatch(m2));
		m2.setName("m1");
		assertTrue(m1.isMatch(m2));
	}

	@Test
	public void compareParametersTest(){
		MethodNode m1 = new MethodNode("m", null);
		MethodNode m2 = new MethodNode("m", null);

		assertTrue(m1.isMatch(m2));
		m1.setName("m1");
		assertFalse(m1.isMatch(m2));
		m2.setName("m1");
		assertTrue(m1.isMatch(m2));

		BasicParameterNode c1 = new BasicParameterNode("c", "type", "0", true, null);
		BasicParameterNode c2 = new BasicParameterNode("c", "type", "0", true, null);

		m1.addParameter(c1);
		assertFalse(m1.isMatch(m2));
		m2.addParameter(c2);
		assertTrue(m1.isMatch(m2));

		c1.setName("c1");
		assertFalse(m1.isMatch(m2));
		c2.setName("c1");
		assertTrue(m1.isMatch(m2));
	}

	@Test
	public void compareConstraintsTest(){
		MethodNode m1 = new MethodNode("m", null);
		MethodNode m2 = new MethodNode("m", null);

		ConstraintNode c1 = new ConstraintNode("c", new Constraint("c", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(true, null), null), null);
		ConstraintNode c2 = new ConstraintNode("c", new Constraint("c", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(true, null), null), null);

		m1.addConstraint(c1);
		assertFalse(m1.isMatch(m2));
		m2.addConstraint(c2);
		assertTrue(m1.isMatch(m2));

		c1.setName("c1");
		assertFalse(m1.isMatch(m2));
		c2.setName("c1");
		assertTrue(m1.isMatch(m2));
	}

	@Test
	public void compareTestCasesTest(){
		MethodNode m1 = new MethodNode("m", null);
		MethodNode m2 = new MethodNode("m", null);

		TestCaseNode tc1 = new TestCaseNode("tc", null, new ArrayList<ChoiceNode>());
		TestCaseNode tc2 = new TestCaseNode("tc", null, new ArrayList<ChoiceNode>());

		m1.addTestCase(tc1);
		assertFalse(m1.isMatch(m2));
		m2.addTestCase(tc2);
		assertTrue(m1.isMatch(m2));

		tc1.setName("tc1");
		assertFalse(m1.isMatch(m2));
		tc2.setName("tc1");
		assertTrue(m1.isMatch(m2));
	}

	@Test
	public void compareSmokeTest(){
		for(int i = 0; i < 5; i++){
			RandomModelGenerator gen = new RandomModelGenerator();
			MethodNode m = gen.generateMethod(3, 3, 10);
			assertTrue(m.isMatch(m));
		}
	}

}
