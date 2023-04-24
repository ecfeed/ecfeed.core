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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;

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
	public void addParameter(){
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
	public void addConstraint(){
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

		assertEquals(2, method.getNamesOfConstraints().size());
		assertTrue(method.getNamesOfConstraints().contains("name1"));
		assertTrue(method.getNamesOfConstraints().contains("name2"));

		method.removeConstraint(constraintNode1);
		assertFalse(method.getNamesOfConstraints().contains("name1"));
		assertTrue(method.getNamesOfConstraints().contains("name2"));

	}

	@Test
	public void getChildrenTest(){
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

		assertEquals(5, method.getChildren().size());
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
	public void getExpectedParametersNames(){
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
	public void replaceParameterWithExpected(){
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
	public void replaceParameterWithChoicesParent(){
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
	public void choiceRemoved(){
		MethodNode method = new MethodNode("method", null);
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		ChoiceNode choice = new ChoiceNode("choice", "value", null);
		Constraint mentioningConstraint = 
				new Constraint(
						"constraint",
						ConstraintType.EXTENDED_FILTER,
						RelationStatement.createRelationStatementWithChoiceCondition(
								parameter, null, EMathRelation.EQUAL, choice), new StaticStatement(false, null), null);

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

	//	@Test
	//	public void addAndDeleteEmptyTestSuites() {
	//
	//		MethodNode methodNode = new MethodNode("Method");
	//		assertEquals(0, methodNode.getTestSuites().size());
	//
	//		TestSuiteNode testSuiteNode1 = new TestSuiteNode("TestSuite1", null);
	//		methodNode.addTestSuite(testSuiteNode1);
	//		assertEquals(1, methodNode.getTestSuites().size());
	//
	//		TestSuiteNode testSuiteNode2 = new TestSuiteNode("TestSuite2", null);
	//		methodNode.addTestSuite(testSuiteNode2);
	//		assertEquals(2, methodNode.getTestSuites().size());
	//
	//
	//		methodNode.removeTestSuite(testSuiteNode1);
	//		assertEquals(1, methodNode.getTestSuites().size());
	//
	//		methodNode.removeTestSuite(testSuiteNode2);
	//		assertEquals(0, methodNode.getTestSuites().size());
	//	}

	@Test
	public void addTestCase() {

		MethodNode method = new MethodNode("name", null);

		assertEquals(0, method.getTestCases().size());
		assertEquals(0, method.getTestSuites().size());

		// add test case 11

		String testSuiteName1 = "Suite1";
		TestCaseNode testCase11 = new TestCaseNode(testSuiteName1, null, new ArrayList<ChoiceNode>());

		method.addTestCase(testCase11);

		assertEquals(1, method.getTestCases().size());
		assertEquals(testSuiteName1, method.getTestCases().get(0).getName());

		assertEquals(1, method.getTestSuites().size());

		TestSuiteNode addedTestSuite1 = method.getTestSuites().get(0);
		assertEquals(testSuiteName1, addedTestSuite1.getName());
		assertEquals(1, addedTestSuite1.getTestCaseNodes().size());

		// add test case 12

		TestCaseNode testCase12 = new TestCaseNode(testSuiteName1, null, new ArrayList<ChoiceNode>());

		method.addTestCase(testCase12);

		assertEquals(2, method.getTestCases().size());
		assertEquals(testSuiteName1, method.getTestCases().get(1).getName());

		assertEquals(1, method.getTestSuites().size());
		assertEquals(2, method.getTestSuites().get(0).getTestCaseNodes().size());

		// add test case 21 to new testSuite

		String testSuiteName2 = "Suite2";
		TestCaseNode testCase21 = new TestCaseNode(testSuiteName2, null, new ArrayList<ChoiceNode>());

		method.addTestCase(testCase21);

		assertEquals(3, method.getTestCases().size());
		assertEquals(testSuiteName1, method.getTestCases().get(1).getName());

		assertEquals(2, method.getTestSuites().size());

		TestSuiteNode foundTestSuite1 = method.findTestSuite(testSuiteName1);
		assertEquals(2, foundTestSuite1.getTestCaseNodes().size());

		TestSuiteNode foundTestSuite2 = method.findTestSuite(testSuiteName2);
		assertEquals(1, foundTestSuite2.getTestCaseNodes().size());

		// remove test case 11

		method.removeTestCase(testCase11);

		assertEquals(2, method.getTestCases().size());
		assertEquals(2, method.getTestSuites().size());

		foundTestSuite1 = method.findTestSuite(testSuiteName1);
		assertEquals(1, foundTestSuite1.getTestCaseNodes().size());

		foundTestSuite2 = method.findTestSuite(testSuiteName2);
		assertEquals(1, foundTestSuite2.getTestCaseNodes().size());

		// remove test case 21

		method.removeTestCase(testCase21);

		assertEquals(1, method.getTestCases().size());
		assertEquals(1, method.getTestSuites().size());

		foundTestSuite1 = method.findTestSuite(testSuiteName1);
		assertEquals(1, foundTestSuite1.getTestCaseNodes().size());

		foundTestSuite2 = method.findTestSuite(testSuiteName2);
		assertNull(foundTestSuite2);

		// remove test case 12

		method.removeTestCase(testCase12);

		assertEquals(0, method.getTestCases().size());
		assertEquals(0, method.getTestSuites().size());

		foundTestSuite1 = method.findTestSuite(testSuiteName1);
		assertNull(foundTestSuite1);
	}

	@Test
	public void copyMethodTest() {

		MethodNode method = new MethodNode("method", null);
		BasicParameterNode par1 = new BasicParameterNode("par1", "int", "0", false, null);
		BasicParameterNode par2 = new BasicParameterNode("par2", "int", "0", true, null);
		ConstraintNode constraint1 = new ConstraintNode("constraint1", new Constraint("constraint1", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(true, null), null), null);
		ConstraintNode constraint2 = new ConstraintNode("constraint2", new Constraint("constraint2", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(true, null), null), null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		par1.addChoice(choice1);
		ChoiceNode expectedChoice1 = new ChoiceNode("expected", "0", null);
		expectedChoice1.setParent(par2);
		ChoiceNode expectedChoice2 = new ChoiceNode("expected", "2", null);
		expectedChoice2.setParent(par2);
		TestCaseNode testCase1 = new TestCaseNode("test case 1", null, Arrays.asList(choice1, expectedChoice1));
		TestCaseNode testCase2 = new TestCaseNode("test case 1", null, Arrays.asList(choice1, expectedChoice2));

		method.addParameter(par1);
		method.addParameter(par2);
		method.addConstraint(constraint1);
		method.addConstraint(constraint2);
		method.addTestCase(testCase1);
		method.addTestCase(testCase2);

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode copy = method.makeClone(Optional.of(nodeMapper));
		MethodNodeHelper.compareMethods(method, copy);
	}

	@Test
	public void cloneMethodWithChoiceConditionConstraintTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "int", "0", true, null);

		ChoiceNode choiceNode = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode, "choice1", "0", false, true, null);

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = RelationStatement.createRelationStatementWithChoiceCondition(
				basicParameterNode, null, EMathRelation.EQUAL, choiceNode);

		Constraint constraint = 
				new Constraint("Constraint", ConstraintType.BASIC_FILTER, precondition, postcondition ,null);

		ConstraintNode constraintNode = new ConstraintNode("Constraint", constraint, null);

		methodNode.addConstraint(constraintNode);

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode clonedMethodNode = methodNode.makeClone(Optional.of(nodeMapper));

		BasicParameterNode clonedBasicParameter = (BasicParameterNode) clonedMethodNode.getParameter(0);
		assertNotEquals(clonedBasicParameter, basicParameterNode);
		assertEquals(clonedBasicParameter.getParent(), clonedMethodNode);

		ChoiceNode clonedChoiceNode = clonedBasicParameter.getChoices().get(0);
		assertNotEquals(clonedChoiceNode, choiceNode);
		assertEquals(clonedChoiceNode.getParent(), clonedBasicParameter);

		ConstraintNode clonedConstraintNode = clonedMethodNode.getConstraintNodes().get(0);
		assertNotEquals(clonedConstraintNode, constraintNode);
		assertEquals(clonedConstraintNode.getParent(), clonedMethodNode);

		Constraint clonedConstraint = clonedConstraintNode.getConstraint();
		assertNotEquals(clonedConstraint, constraint);

		RelationStatement clonedPostcondition = (RelationStatement) clonedConstraint.getPostcondition();
		assertNotEquals(clonedPostcondition, postcondition);

		BasicParameterNode clonedLeftParameterNodeFromConstraint = clonedPostcondition.getLeftParameter();
		assertEquals(clonedLeftParameterNodeFromConstraint, clonedBasicParameter);

		ChoiceCondition clonedChoiceCondition = (ChoiceCondition) clonedPostcondition.getCondition();
		ChoiceNode clonedChoiceNodeFromConstraint = clonedChoiceCondition.getRightChoice();
		assertEquals(clonedChoiceNodeFromConstraint, clonedChoiceNode);

		MethodNodeHelper.compareMethods(methodNode, clonedMethodNode);
	}

	@Test
	public void cloneMethodWithParameterConditionConstraintTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode basicParameterNode1 = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode1, "choice1", "0", false, true, null);

		BasicParameterNode basicParameterNode2 = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode2, "choice2", "0", false, true, null);

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = RelationStatement.createRelationStatementWithParameterCondition(
				basicParameterNode1, null, EMathRelation.EQUAL, basicParameterNode2);

		Constraint constraint = 
				new Constraint("Constraint", ConstraintType.BASIC_FILTER, precondition, postcondition ,null);

		ConstraintNode constraintNode = new ConstraintNode("Constraint", constraint, null);

		methodNode.addConstraint(constraintNode);

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode clonedMethodNode = methodNode.makeClone(Optional.of(nodeMapper));

		BasicParameterNode clonedBasicParameter1 = (BasicParameterNode) clonedMethodNode.getParameter(0);
		assertNotEquals(clonedBasicParameter1, basicParameterNode1);
		assertEquals(clonedBasicParameter1.getParent(), clonedMethodNode);

		BasicParameterNode clonedBasicParameter2 = (BasicParameterNode) clonedMethodNode.getParameter(1);
		assertNotEquals(clonedBasicParameter2, basicParameterNode2);
		assertEquals(clonedBasicParameter2.getParent(), clonedMethodNode);

		ConstraintNode clonedConstraintNode = clonedMethodNode.getConstraintNodes().get(0);
		assertNotEquals(clonedConstraintNode, constraintNode);
		assertEquals(clonedConstraintNode.getParent(), clonedMethodNode);

		Constraint clonedConstraint = clonedConstraintNode.getConstraint();
		assertNotEquals(clonedConstraint, constraint);

		RelationStatement clonedPostcondition = (RelationStatement) clonedConstraint.getPostcondition();
		assertNotEquals(clonedPostcondition, postcondition);

		BasicParameterNode clonedLeftParameterNodeFromConstraint = clonedPostcondition.getLeftParameter();
		assertEquals(clonedLeftParameterNodeFromConstraint, clonedBasicParameter1);

		ParameterCondition clonedParameterCondition = (ParameterCondition) clonedPostcondition.getCondition();
		BasicParameterNode clonedParameter2NodeFromConstraint = clonedParameterCondition.getRightParameterNode();
		assertEquals(clonedParameter2NodeFromConstraint, clonedBasicParameter2);

		MethodNodeHelper.compareMethods(methodNode, clonedMethodNode);
	}

	@Test
	public void copyMethodWithDeployedParameters() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode, "choice1", "0", false, true, null);

		NodeMapper nodeMapper1 = new NodeMapper();
		MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, nodeMapper1);
		MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, methodNode, nodeMapper1);

		BasicParameterNode deployedParameter = 
				(BasicParameterNode) methodNode.getDeployedParametersWithLinkingContexts().get(0).getParameter();
		assertEquals(deployedParameter.getParent(), methodNode);

		NodeMapper nodeMapper2 = new NodeMapper();
		MethodNode clonedMethodNode = methodNode.makeClone(Optional.of(nodeMapper2));

		BasicParameterNode clonedDeployedParameter = 
				(BasicParameterNode) clonedMethodNode.getDeployedParametersWithLinkingContexts().get(0).getParameter();

		assertEquals(clonedDeployedParameter.getParent(), clonedMethodNode);

		ChoiceNode clonedDeployedChoice = clonedDeployedParameter.getChoices().get(0);
		assertEquals(clonedDeployedChoice.getParent(), clonedDeployedParameter);

		MethodNodeHelper.compareMethods(methodNode, clonedMethodNode);
	}

	@Test
	public void copyMethodWithDeployedParametersAndLinkingContext() {

		RootNode rootNode = new RootNode("root", null);

		CompositeParameterNode globalComposite = 
				RootNodeHelper.addGlobalCompositeParameterToRoot(rootNode, "GS1", null); // XYX set parent

		BasicParameterNode globalBasicParameterNode = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalComposite, "GP1", "int", "0", null); // XYX set parent

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameterNode, "GC1", "1", false, true, null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "CLL1", null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "M1", true, null);

		CompositeParameterNode localCompositeParameterNode = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS1", true, null);

		localCompositeParameterNode.setLinkToGlobalParameter(globalComposite);

		// deployment

		NodeMapper nodeMapper1 = new NodeMapper();
		MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, nodeMapper1);

		MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, methodNode, nodeMapper1);

		List<ParameterWithLinkingContext> deployedParameterWithLinkingContexts = 
				methodNode.getDeployedParametersWithLinkingContexts();

		ParameterWithLinkingContext firstDeployedParameterWithLinkingContext = 
				deployedParameterWithLinkingContexts.get(0);

		assertEquals(firstDeployedParameterWithLinkingContext.getParameter(), globalBasicParameterNode);
		assertEquals(firstDeployedParameterWithLinkingContext.getLinkingContext(), localCompositeParameterNode);

		// clone

		NodeMapper nodeMapper2 = new NodeMapper();
		RootNode clonedRootNode = rootNode.makeClone(Optional.of(nodeMapper2));

		RootNodeHelper.compareRootNodes(rootNode, clonedRootNode);
	}

}
