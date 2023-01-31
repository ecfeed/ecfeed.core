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

import org.junit.Test;

public class TestSuiteNodeTest {

	@Test
	public void addAndDeleteEmptyTestSuites() {
		
		MethodNode methodNode = new MethodNode("Method");
		assertEquals(0, methodNode.getTestSuites().size());
		
		TestSuiteNode testSuiteNode1 = new TestSuiteNode("TestSuite1", null);
		methodNode.addTestSuite(testSuiteNode1);
		assertEquals(1, methodNode.getTestSuites().size());
		
		TestSuiteNode testSuiteNode2 = new TestSuiteNode("TestSuite2", null);
		methodNode.addTestSuite(testSuiteNode2);
		assertEquals(2, methodNode.getTestSuites().size());
		
		
		methodNode.removeTestSuite(testSuiteNode1);
		assertEquals(1, methodNode.getTestSuites().size());
		
		methodNode.removeTestSuite(testSuiteNode2);
		assertEquals(0, methodNode.getTestSuites().size());
	}

}
