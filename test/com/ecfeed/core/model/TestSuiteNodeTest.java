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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class TestSuiteNodeTest {

	
	@Test
	public void addAndDeleteTestCases() {
		
		TestSuiteNode testSuiteNode = new TestSuiteNode("TestSuite1", null);
		assertEquals(0, testSuiteNode.getTestCaseNodes().size());
		
		// add test case 1
		
		TestCaseNode testCaseNode1 = new TestCaseNode(null);
		testSuiteNode.addTestCase(testCaseNode1);
		assertEquals(1, testSuiteNode.getTestCaseNodes().size());
		
		// add the same test case

		testSuiteNode.addTestCase(testCaseNode1);
		assertEquals(1, testSuiteNode.getTestCaseNodes().size());
		
		// add test case 2
		
		TestCaseNode testCaseNode2 = new TestCaseNode(null);
		testSuiteNode.addTestCase(testCaseNode2);
		assertEquals(2, testSuiteNode.getTestCaseNodes().size());

		// add test case 2 again
		
		testSuiteNode.addTestCase(testCaseNode2);
		assertEquals(2, testSuiteNode.getTestCaseNodes().size());
		
		// delete test case 1
		
		testSuiteNode.removeTestCase(testCaseNode1);
		assertEquals(1, testSuiteNode.getTestCaseNodes().size());
		
		// try delete test case 1 again
		
		testSuiteNode.removeTestCase(testCaseNode1);
		assertEquals(1, testSuiteNode.getTestCaseNodes().size());
		
		// delete test case 2

		testSuiteNode.removeTestCase(testCaseNode2);
		assertEquals(0, testSuiteNode.getTestCaseNodes().size());
	}
}
