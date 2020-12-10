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

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AssignmenttStatementTest {

	@Test
	public void setExpectedValueTest() {

		// method node

		MethodNode methodNode = new MethodNode("method", null);

		// method parameter 1 node with choice

		MethodParameterNode methodParameterNode1 = new MethodParameterNode(
				"par1",
				"int",
				"1",
				false,
				null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// method parameter 2 node with choice

		MethodParameterNode methodParameterNode2 = new MethodParameterNode(
				"par2",
				"int",
				"2",
				true,
				null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode21 = new ChoiceNode("choice21", "21",  null);
		methodParameterNode2.addChoice(choiceNode21);

		// assignment with value condition

		AbstractStatement assignmentStatement =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode2, "5");

		ChoiceNode choiceNode1 = new ChoiceNode("result1", "0", null);
		ChoiceNode choiceNode2 = new ChoiceNode("result2", "0", null);

		List<ChoiceNode> choiceNodes = new ArrayList<>();
		choiceNodes.add(choiceNode1);
		choiceNodes.add(choiceNode2);

		assignmentStatement.setExpectedValue(choiceNodes);

		ChoiceNode resultChoiceNode = choiceNodes.get(1);
		String resultValue = resultChoiceNode.getValueString();

		assertEquals("5", resultValue);
	}

}
