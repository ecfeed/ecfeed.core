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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.testutils.RandomModelGenerator;

public class TestCaseNodeTest {

	// TODO MO-RE 
	//	@Test
	//	public void testRemoveTestCase(){
	//		MethodNode method = new MethodNode("name", null);
	//		TestCaseNode testCase1 = new TestCaseNode("name1", null, new ArrayList<ChoiceNode>());
	//		TestCaseNode testCase2 = new TestCaseNode("name1", null, new ArrayList<ChoiceNode>());
	//		TestCaseNode testCase3 = new TestCaseNode("name2", null, new ArrayList<ChoiceNode>());
	//		TestCaseNode testCase4 = new TestCaseNode("name2", null, new ArrayList<ChoiceNode>());
	//
	//		method.addTestCase(testCase1);
	//		method.addTestCase(testCase2);
	//		method.addTestCase(testCase3);
	//		method.addTestCase(testCase4);
	//
	//		assertEquals(4, method.getTestCases().size());
	//
	//		method.removeTestCase(testCase1);
	//
	//		assertEquals(3, method.getTestCases().size());
	//		assertFalse(method.getTestCases().contains(testCase1));
	//
	//		TestSuiteNode testSuiteNode = new TestSuiteNode();
	//		testSuiteNode.setName("name2");
	//		method.removeTestSuite(testSuiteNode);
	//		assertEquals(1, method.getTestCases().size());
	//		assertFalse(method.getTestCases().contains(testCase3));
	//		assertFalse(method.getTestCases().contains(testCase4));
	//		assertTrue(method.getTestCases().contains(testCase2));
	//	}

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
}
