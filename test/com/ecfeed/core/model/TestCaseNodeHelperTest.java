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

		BasicParameterNode basicParameterNode1 = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "String", "", true, null);
		
		BasicParameterNode basicParameterNode2 = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par2", "String", "", true, null);

		ChoiceNode choiceNode1 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode1, "choice_1", "value", false, true, null);

		ChoiceNode choiceNode2 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode2, "choice_2", "value", false, true, null);

		List<ChoiceNode> choiceNodesOfTestCase = new ArrayList<ChoiceNode>();
		choiceNodesOfTestCase.add(choiceNode1);
		choiceNodesOfTestCase.add(choiceNode2);

		TestCaseNode testCaseNode = MethodNodeHelper.addNewTestCase(methodNode, "test case 1", choiceNodesOfTestCase, true);

		String signature = TestCaseNodeHelper.createSignature(testCaseNode, true, new ExtLanguageManagerForJava());
		assertEquals("[test case 1] method_1(choice_1, choice_2)", signature);

		signature = TestCaseNodeHelper.createSignature(testCaseNode, true, new ExtLanguageManagerForSimple());
		assertEquals("[test case 1] method 1(choice_1, choice_2)", signature);

		String testDataString = TestCaseNodeHelper.getTestDataString(testCaseNode, new ExtLanguageManagerForJava());
		assertEquals("choice_1, choice_2", testDataString);

		testDataString = TestCaseNodeHelper.getTestDataString(testCaseNode, new ExtLanguageManagerForSimple());
		assertEquals("choice_1, choice_2", testDataString);
	}

}