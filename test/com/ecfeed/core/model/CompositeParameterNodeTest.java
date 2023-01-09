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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CompositeParameterNodeTest {

	@Test
	public void basicTest() {
		
		String name = "parameterName";
		
		CompositeParameterNode compositeParameterNode = new CompositeParameterNode(name, null);
		
		assertEquals(name, compositeParameterNode.getName());
	}

	@Test
	public void childParametersTest() {
		
		String parameterName1 = "name1";
		String parameterName2 = "name2";
		
		// create composite parameter
		
		CompositeParameterNode compositeParameterNode = new CompositeParameterNode(parameterName1, null);
		
		assertEquals(0, compositeParameterNode.getChildrenCount());
		List<IAbstractNode> children = compositeParameterNode.getChildren();
		assertEquals(0, children.size());
		
		List<AbstractParameterNode> parameters = compositeParameterNode.getParameters();
		assertEquals(0, parameters.size());
		
		assertFalse(compositeParameterNode.parameterExists(parameterName2));
		
		// add two parameters

		BasicParameterNode basicParameterNode1 = new BasicParameterNode(parameterName1, "int", null);
		BasicParameterNode basicParameterNode2 = new BasicParameterNode(parameterName2, "int", null);
		
		compositeParameterNode.addParameter(basicParameterNode2);
		compositeParameterNode.addParameter(basicParameterNode1, 0);
		
		assertEquals(2, compositeParameterNode.getChildrenCount());
		children = compositeParameterNode.getChildren();
		assertEquals(2, children.size());
		
		parameters = compositeParameterNode.getParameters();
		assertEquals(2, parameters.size());
		
		assertTrue(compositeParameterNode.parameterExists(parameterName1));
		assertTrue(compositeParameterNode.parameterExists(parameterName2));
		
		BasicParameterNode resultNode1 = (BasicParameterNode) parameters.get(0);
		assertEquals(basicParameterNode1, resultNode1);
		
		BasicParameterNode resultNode2 = (BasicParameterNode) compositeParameterNode.getParameter(1);
		assertEquals(basicParameterNode2, resultNode2);
		
		BasicParameterNode resultNode2b = (BasicParameterNode) compositeParameterNode.findParameter(parameterName2);
		assertEquals(basicParameterNode2, resultNode2b);
		
		// remove parameter
		
		compositeParameterNode.removeParameter(basicParameterNode2);
		
		assertEquals(1, compositeParameterNode.getChildrenCount());
		children = compositeParameterNode.getChildren();
		assertEquals(1, children.size());
		
		parameters = compositeParameterNode.getParameters();

		BasicParameterNode resultNode1b = (BasicParameterNode) parameters.get(0);
		assertEquals(basicParameterNode1, resultNode1b);
		
		assertTrue(compositeParameterNode.parameterExists(parameterName1));
		assertFalse(compositeParameterNode.parameterExists(parameterName2));
		
		// replace parameters
		
		List<AbstractParameterNode> parametersToReplace = new ArrayList<>();
		parametersToReplace.add(basicParameterNode2);
		
		compositeParameterNode.replaceParameters(parametersToReplace);
		
		assertEquals(1, compositeParameterNode.getChildrenCount());
		children = compositeParameterNode.getChildren();
		assertEquals(1, children.size());
		
		parameters = compositeParameterNode.getParameters();

		BasicParameterNode resultNode2c = (BasicParameterNode) parameters.get(0);
		assertEquals(basicParameterNode2, resultNode2c);
		
		assertFalse(compositeParameterNode.parameterExists(parameterName1));
		assertTrue(compositeParameterNode.parameterExists(parameterName2));
	}
	
	@Test
	public void isGlobalParameterTest() {
		
		String name = "parameterName";
		
		CompositeParameterNode compositeParameterNode = new CompositeParameterNode(name, null);
		assertEquals(false, compositeParameterNode.isGlobalParameter());
		
		RootNode rootNode = new RootNode("root", null);
		rootNode.addParameter(compositeParameterNode);
		
		assertEquals(true, compositeParameterNode.isGlobalParameter());
	}
	
	@Test
	public void isMatchTest() {
		
		String parameterName1 = "name";
		
		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode(parameterName1, null);
		CompositeParameterNode compositeParameterNode2 = new CompositeParameterNode(parameterName1, null);
		assertTrue(compositeParameterNode1.isMatch(compositeParameterNode2));
		
		BasicParameterNode basicParameterNode1 = new BasicParameterNode(parameterName1, "int", null);
		compositeParameterNode1.addParameter(basicParameterNode1);
		assertFalse(compositeParameterNode1.isMatch(compositeParameterNode2));
		
		BasicParameterNode basicParameterNode2 = new BasicParameterNode(parameterName1, "int", null);
		compositeParameterNode2.addParameter(basicParameterNode2);
		assertTrue(compositeParameterNode1.isMatch(compositeParameterNode2));
		
		ChoiceNode choiceNode1 = new ChoiceNode("choice", "1");
		basicParameterNode1.addChoice(choiceNode1);
		assertFalse(compositeParameterNode1.isMatch(compositeParameterNode2));
		
		ChoiceNode choiceNode2 = new ChoiceNode("choice", "1");
		basicParameterNode2.addChoice(choiceNode2);
		assertTrue(compositeParameterNode1.isMatch(compositeParameterNode2));
	}
	
	@Test
	public void addAndDeleteChildCompositeTest() {
		
		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("name1", null);
		CompositeParameterNode compositeParameterNode2 = new CompositeParameterNode("name2", null);
		
		assertEquals(0, compositeParameterNode1.getParametersCount());
		compositeParameterNode1.addParameter(compositeParameterNode2);
		assertEquals(1, compositeParameterNode1.getParametersCount());
		
		AbstractParameterNode resultParameter = compositeParameterNode1.getParameter(0);
		assertEquals("name2", resultParameter.getName());
		
		compositeParameterNode1.removeParameter(compositeParameterNode2);
		assertEquals(0, compositeParameterNode1.getParametersCount());
	}
	
}
