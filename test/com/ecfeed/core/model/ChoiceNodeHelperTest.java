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


import com.ecfeed.core.utils.ExtLanguage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChoiceNodeHelperTest {

	@Test
	public void getNameTest() {

		ChoiceNode choice = new ChoiceNode("choice_1", null, "MAX_VALUE");
		assertEquals("choice_1", ChoiceNodeHelper.getName(choice, ExtLanguage.JAVA));
		assertEquals("choice 1", ChoiceNodeHelper.getName(choice, ExtLanguage.SIMPLE));
	}

	@Test
	public void getQualifiedNameTest() {

		ChoiceNode choice1 = new ChoiceNode("choice_1", null, "MAX_VALUE");
		assertEquals("choice_1", ChoiceNodeHelper.getQualifiedName(choice1, ExtLanguage.JAVA));
		assertEquals("choice 1", ChoiceNodeHelper.getQualifiedName(choice1, ExtLanguage.SIMPLE));

		ChoiceNode choice2 = new ChoiceNode("choice_2", null, "MAX_VALUE");
		choice2.setParent(choice1);
		assertEquals("choice_1:choice_2", ChoiceNodeHelper.getQualifiedName(choice2, ExtLanguage.JAVA));
		assertEquals("choice 1:choice 2", ChoiceNodeHelper.getQualifiedName(choice2, ExtLanguage.SIMPLE));

		ChoiceNode choice3 = new ChoiceNode("choice_3", null, "MAX_VALUE");
		choice3.setParent(choice2);
		assertEquals("choice_1:choice_2:choice_3", ChoiceNodeHelper.getQualifiedName(choice3, ExtLanguage.JAVA));
		assertEquals("choice 1:choice 2:choice 3", ChoiceNodeHelper.getQualifiedName(choice3, ExtLanguage.SIMPLE));
	}

	@Test
	public void getParentChoiceTest() {

		ChoiceNode choice1 = new ChoiceNode("choice_1", null, "MAX_VALUE");
		assertNull(ChoiceNodeHelper.getParentChoice(choice1));

		ChoiceNode choice2 = new ChoiceNode("choice_2", null, "MAX_VALUE");
		assertNull(ChoiceNodeHelper.getParentChoice(choice2));
		choice2.setParent(choice1);
		assertEquals(choice1, ChoiceNodeHelper.getParentChoice(choice2));

		ChoiceNode choice3 = new ChoiceNode("choice_3", null, "MAX_VALUE");
		choice3.setParent(choice2);
		ChoiceNode parentChoice = ChoiceNodeHelper.getParentChoice(choice3);
		assertEquals(choice2, parentChoice);
	}

	@Test
	public void createSignatureTest() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode(
						"par1", null,	"int", "0", true);

		ChoiceNode choice = new ChoiceNode("choice_1", null, "MAX_VALUE");
		choice.setParent(methodParameterNode);

		String label = ChoiceNodeHelper.createSignature(choice, ExtLanguage.JAVA);
		assertEquals("choice_1 [MAX_VALUE]", label);

		label = ChoiceNodeHelper.createSignature(choice, ExtLanguage.SIMPLE);
		assertEquals("choice 1 [2147483647]", label);

		choice = new ChoiceNode("choice_1", null, "5");
		choice.setParent(methodParameterNode);

		label = ChoiceNodeHelper.createSignature(choice, ExtLanguage.JAVA);
		assertEquals("choice_1 [5]", label);
	}

	@Test
	public void createTestDataLabelTest() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode(
						"par1", null,	"int", "0", true);

		ChoiceNode choice = new ChoiceNode("choice_1", null, "MAX_VALUE");
		choice.setParent(methodParameterNode);

		String label = ChoiceNodeHelper.createTestDataLabel(choice, ExtLanguage.SIMPLE);
		assertEquals("[e]2147483647", label);

		methodParameterNode.setExpected(false);

		label = ChoiceNodeHelper.createTestDataLabel(choice, ExtLanguage.JAVA);
		assertEquals("choice_1", label);

		label = ChoiceNodeHelper.createTestDataLabel(choice, ExtLanguage.SIMPLE);
		assertEquals("choice 1", label);
	}

	@Test
	public void getChoiceNamesTest() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode(
						"par1", null,	"int", "0", true);

		ChoiceNode choice1 = new ChoiceNode("choice_1", null, "0");
		choice1.setParent(methodParameterNode);

		ChoiceNode choice2 = new ChoiceNode("choice_2", null, "0");
		choice2.setParent(methodParameterNode);

		List<ChoiceNode> choiceNodes = new ArrayList<>();
		choiceNodes.add(choice1);
		choiceNodes.add(choice2);

		List<String> choiceNames = ChoiceNodeHelper.getChoiceNames(choiceNodes, ExtLanguage.JAVA);
		assertEquals(2, choiceNames.size());
		assertEquals("choice_1", choiceNames.get(0));
		assertEquals("choice_2", choiceNames.get(1));

		choiceNames = ChoiceNodeHelper.getChoiceNames(choiceNodes, ExtLanguage.SIMPLE);
		assertEquals(2, choiceNames.size());
		assertEquals("choice 1", choiceNames.get(0));
		assertEquals("choice 2", choiceNames.get(1));

		choice2.setParent(choice1);

		choiceNames = ChoiceNodeHelper.getChoiceNames(choiceNodes, ExtLanguage.JAVA);
		assertEquals(2, choiceNames.size());
		assertEquals("choice_1", choiceNames.get(0));
		assertEquals("choice_1:choice_2", choiceNames.get(1));

		choiceNames = ChoiceNodeHelper.getChoiceNames(choiceNodes, ExtLanguage.SIMPLE);
		assertEquals(2, choiceNames.size());
		assertEquals("choice 1", choiceNames.get(0));
		assertEquals("choice 1:choice 2", choiceNames.get(1));
	}
}
