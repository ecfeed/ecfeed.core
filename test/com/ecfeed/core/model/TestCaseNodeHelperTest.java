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

import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestCaseNodeHelperTest {

	@Test
	public void getSignatureAndDataStringTest() {

		MethodNode methodNode = new MethodNode("method_1", null);

		ChoiceNode choiceNode1 = new ChoiceNode("choice_1", null, "value");
		ChoiceNode choiceNode2 = new ChoiceNode("choice_2", null, "value");

		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();
		choiceNodes.add(choiceNode1);
		choiceNodes.add(choiceNode2);

		TestCaseNode testCaseNode = new TestCaseNode("test_case_1", null, choiceNodes);
		testCaseNode.setParent(methodNode);

		String signature = TestCaseNodeHelper.createSignature(testCaseNode, new ExtLanguageManagerForJava());
		assertEquals("[test_case_1] method_1(choice_1, choice_2)", signature);

		signature = TestCaseNodeHelper.createSignature(testCaseNode, new ExtLanguageManagerForSimple());
		assertEquals("[test case 1] method 1(choice 1, choice 2)", signature);

		String testDataString = TestCaseNodeHelper.getTestDataString(testCaseNode, new ExtLanguageManagerForJava());
		assertEquals("choice_1, choice_2", testDataString);

		testDataString = TestCaseNodeHelper.getTestDataString(testCaseNode, new ExtLanguageManagerForSimple());
		assertEquals("choice 1, choice 2", testDataString);
	}

}