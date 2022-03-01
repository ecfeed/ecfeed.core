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

import org.junit.Test;

import static org.junit.Assert.*;

public class MethodParameterNodeTest {

	@Test
	public void createParameterTest() {

		try {
			new MethodParameterNode("par%1", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodParameterNode("!", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodParameterNode("a b", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void basicAttachDetachChoiceTest() {
		
		// create method and parameter
		
		MethodNode methodNode = new MethodNode("method", null);
		
		MethodParameterNode methodParameterNode = new MethodParameterNode("name", "type", "0", false, null);
		methodParameterNode.setParent(methodNode);
		
		String oldChoiceNodeName = "old";
		ChoiceNode oldChoiceNode = new ChoiceNode(oldChoiceNodeName, "0", null);
		methodParameterNode.addChoice(oldChoiceNode);
		
		assertFalse(oldChoiceNode.isDetached());
		
		// detach and delete choice node
		
		ChoiceNode choiceNode = methodParameterNode.getChoices().get(0);
		
		methodParameterNode.detachAndDeleteChoiceNode(choiceNode.getName());
		
		assertEquals(0, methodParameterNode.getChoiceCount());
		
		// check detached choice nodes
		
		assertEquals(1, methodParameterNode.getDetachedChoiceCount());
		
		ChoiceNode detachedChoiceNode = methodParameterNode.getDetachedChoices().get(0);
		assertTrue(detachedChoiceNode.isDetached());
		
		// add new choice node to parameter
		
		ChoiceNode newChoiceNode = new ChoiceNode("new", "0", null);
		methodParameterNode.addChoice(newChoiceNode);
		assertEquals(1, methodParameterNode.getChoiceCount());

		// attach detached choice node
		
		methodParameterNode.attachAndDeleteChoiceNode(detachedChoiceNode.getName(), newChoiceNode.getName());
		
		assertEquals(0, methodParameterNode.getDetachedChoiceCount());
		assertEquals(1, methodParameterNode.getChoiceCount());
		
		ChoiceNode choiceNode1 = methodParameterNode.getChoices().get(0);
		assertEquals(choiceNode1, newChoiceNode);
		
		assertFalse(choiceNode1.isDetached());
	}

}
