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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;


public class TestCaseNodeHelperTest {

	@Test
	public void getSignatureAndDataStringTest() {

		MethodNode methodNode = new MethodNode("method_1", null);

		ChoiceNode choiceNode1 = new ChoiceNode("choice_1", "value", null);
		ChoiceNode choiceNode2 = new ChoiceNode("choice 2", "value", null);

		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();
		choiceNodes.add(choiceNode1);
		choiceNodes.add(choiceNode2);

		TestCaseNode testCaseNode = new TestCaseNode("test case 1", null, choiceNodes);
		testCaseNode.setParent(methodNode);

		String signature = TestCaseNodeHelper.createSignature(testCaseNode, true, new ExtLanguageManagerForJava());
		assertEquals("[test case 1] method_1(choice_1, choice 2)", signature);

		signature = TestCaseNodeHelper.createSignature(testCaseNode, true, new ExtLanguageManagerForSimple());
		assertEquals("[test case 1] method 1(choice_1, choice 2)", signature);

		String testDataString = TestCaseNodeHelper.getTestDataString(testCaseNode, new ExtLanguageManagerForJava());
		assertEquals("choice_1, choice 2", testDataString);

		testDataString = TestCaseNodeHelper.getTestDataString(testCaseNode, new ExtLanguageManagerForSimple());
		assertEquals("choice_1, choice 2", testDataString);
	}

}