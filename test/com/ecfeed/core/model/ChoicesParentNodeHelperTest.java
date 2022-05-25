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

import java.util.List;

import org.junit.Test;

public class ChoicesParentNodeHelperTest {

	@Test
	public void copyOneLevelOfChoices(){

		// metod node 
		
		MethodNode methodNode = new MethodNode("M", null);
		
		// source parameter with two choices
		
		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "P1", "String");
		
		ChoiceNode choiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(
						methodParameterNode1, "C1", "1");

		ChoiceNode choiceNode2 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(
						methodParameterNode1, "C2", "1");
		
		// destination parameter 
		
		MethodParameterNode methodParameterNode2 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "P2", "String");
		

		// creating copy
		
		ChoicesParentNodeHelper.createCopyOfChoicesTree(methodParameterNode1, methodParameterNode2);
		
		// checks
		
		assertEquals(2, methodParameterNode1.getChoices().size());
		
		List<ChoiceNode> copiedChoices = methodParameterNode2.getChoices();
		assertEquals(2, copiedChoices.size());

		ChoiceNode resultChoiceNode1 = copiedChoices.get(0);
		assertEquals(choiceNode1.getName(), resultChoiceNode1.getName());

		ChoiceNode resultChoiceNode2 = copiedChoices.get(1);
		assertEquals(choiceNode2.getName(), resultChoiceNode2.getName());
	}


}
