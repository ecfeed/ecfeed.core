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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.ecfeed.core.testutils.RandomModelGenerator;

public class TestCaseNodeTest {

	@Test
	public void setNameForSingleTestCaseAndTestSuite() {

		MethodNode methodNode = new MethodNode("Method");

		ChoiceNode p1 = new ChoiceNode("name", "value", null);

		List<ChoiceNode> td1 = new ArrayList<ChoiceNode>();
		td1.add(p1);

		String testSuiteName = "TestSuite";
		TestCaseNode tc1 = new TestCaseNode(testSuiteName, null, td1);

		methodNode.addTestCase(tc1);

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(1, methodNode.getTestSuites().size());

		String newTestSuiteName = "NewTestSuite";

		tc1.setName(newTestSuiteName);

		List<TestCaseNode> resultTestCaseNodes = methodNode.getTestCases();
		assertEquals(1, resultTestCaseNodes.size());

		TestCaseNode resultTestCaseNode1 = resultTestCaseNodes.get(0);
		assertEquals(newTestSuiteName, resultTestCaseNode1.getName());

		List<TestSuiteNode> resultTestSuiteNodes = methodNode.getTestSuites();
		assertEquals(1, resultTestSuiteNodes.size());

		TestSuiteNode resultTestSuiteNode = resultTestSuiteNodes.get(0);
		assertEquals(newTestSuiteName, resultTestSuiteNode.getName());

		TestCaseNode resultTestCaseNode2 = resultTestSuiteNode.getTestCaseNodes().get(0);
		assertEquals(newTestSuiteName, resultTestCaseNode2.getName());
	}		

	@Test
	public void compare(){
		ChoiceNode p1 = new ChoiceNode("name", "value", null);
		ChoiceNode p2 = new ChoiceNode("name", "value", null);

		List<ChoiceNode> td1 = new ArrayList<ChoiceNode>();
		td1.add(p1);
		List<ChoiceNode> td2 = new ArrayList<ChoiceNode>();
		td2.add(p2);
		List<ChoiceNode> td3 = new ArrayList<ChoiceNode>();

		TestCaseNode tc1 = new TestCaseNode("name", null, td1);
		TestCaseNode tc2 = new TestCaseNode("name", null, td2);
		TestCaseNode tc3 = new TestCaseNode("name", null, td3);

		assertTrue(tc1.isMatch(tc2));
		assertFalse(tc1.isMatch(tc3));

		tc1.setName("tc1");
		assertFalse(tc1.isMatch(tc2));
		tc2.setName("tc1");
		assertTrue(tc1.isMatch(tc2));

		p1.setName("p1");
		assertFalse(tc1.isMatch(tc2));
		p2.setName("p1");
		assertTrue(tc1.isMatch(tc2));
	}

	//	@Test
	public void compareSmokeTest(){
		for(int i = 0; i < 5; i++){
			RandomModelGenerator gen = new RandomModelGenerator();
			MethodNode m = gen.generateMethod(5, 0, 0);
			TestCaseNode t = gen.generateTestCase(m);

			assertTrue(t.isMatch(t));
		}
	}
	
	@Test
	public void copyTestCaseTest(){
		MethodNode method = new MethodNode("method", null);
		BasicParameterNode par1 = new BasicParameterNode("par1", "int", "0", false, null);
		BasicParameterNode par2 = new BasicParameterNode("par2", "int", "0", true, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		par1.addChoice(choice1);
		ChoiceNode expectedChoice1 = new ChoiceNode("expected", "0", null);
		expectedChoice1.setParent(par2);
		ChoiceNode expectedChoice2 = new ChoiceNode("expected", "2", null);
		expectedChoice2.setParent(par2);
		TestCaseNode testCase = new TestCaseNode("test case 1", null, Arrays.asList(choice1, expectedChoice1));

		method.addParameter(par1);
		method.addParameter(par2);
		method.addTestCase(testCase);

		NodeMapper nodeMapper = new NodeMapper();
		TestCaseNode copy = testCase.makeClone(Optional.of(nodeMapper));
		assertTrue(testCase.isMatch(copy));
	}

}
