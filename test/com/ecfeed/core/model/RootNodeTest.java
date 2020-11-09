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

import org.junit.Test;

import com.ecfeed.core.testutils.RandomModelGenerator;

public class RootNodeTest{

	@Test
	public void addClassTest(){
		RootNode root = new RootNode("name", null);
		ClassNode c1 = new ClassNode("c1", null);
		ClassNode c2 = new ClassNode("c2", null);
		ClassNode c3 = new ClassNode("c3", null);
		assertEquals(0,  root.getClasses().size());
		root.addClass(c1);
		assertEquals(1,  root.getClasses().size());
		assertEquals(c1, root.getClasses().get(0));
		root.addClass(c2);
		assertEquals(2,  root.getClasses().size());
		assertEquals(c1, root.getClasses().get(0));
		assertEquals(c2, root.getClasses().get(1));
		root.addClass(c3,0);
		assertEquals(3,  root.getClasses().size());
		assertEquals(c3, root.getClasses().get(0));
		assertEquals(c1, root.getClasses().get(1));
		assertEquals(c2, root.getClasses().get(2));
	}


	@Test
	public void testGetClass(){
		RootNode root = new RootNode("name", null); 
		ClassNode classNode1 = new ClassNode("name", null);
		ClassNode classNode2 = new ClassNode("name", null);
		assertEquals(0,  root.getClasses().size());
		assertEquals(0,  root.getChildren().size());
		root.addClass(classNode1);
		root.addClass(classNode2);
		assertEquals(2,  root.getClasses().size());
		assertEquals(2,  root.getChildren().size());
		assertTrue(root.getClasses().contains(classNode1));
		assertTrue(root.getChildren().contains(classNode1));
		assertTrue(root.getClasses().contains(classNode2));
		assertTrue(root.getChildren().contains(classNode2));

		root.removeClass(classNode1);
		assertEquals(1,  root.getClasses().size());
		assertEquals(1,  root.getChildren().size());
		assertFalse(root.getClasses().contains(classNode1));
		assertFalse(root.getChildren().contains(classNode1));
		assertTrue(root.getClasses().contains(classNode2));
		assertTrue(root.getChildren().contains(classNode2));
	}


	@Test
	public void testGetClassModel(){
		RootNode root = new RootNode("name", null);
		ClassNode class1 = new ClassNode("com.example.class1", null);
		ClassNode class2 = new ClassNode("com.example.class2", null);
		ClassNode class3 = new ClassNode("class1", null);

		root.addClass(class1);
		root.addClass(class2);
		root.addClass(class3);

		assertEquals(class1, root.getClass("com.example.class1"));
	}

	@Test
	public void compareTest(){
		RootNode r1 = new RootNode("r1", null);
		RootNode r2 = new RootNode("r2", null);

		assertFalse(r1.isMatch(r2));

		r2.setName("r1");
		assertTrue(r1.isMatch(r2));

		ClassNode class1 = new ClassNode("name", null);
		ClassNode class2 = new ClassNode("name", null);

		r1.addClass(class1);
		assertFalse(r1.isMatch(r2));

		r2.addClass(class2);
		assertTrue(r1.isMatch(r2));

		class2.setName("new_name");
		assertFalse(r1.isMatch(r2));

		class2.setName("name");
		assertTrue(r1.isMatch(r2));

		GlobalParameterNode parameter1 = new GlobalParameterNode("parameter1", "int", null);
		GlobalParameterNode parameter2 = new GlobalParameterNode("parameter1", "int", null);

		r1.addParameter(parameter1);
		assertFalse(r1.isMatch(r2));
		r2.addParameter(parameter2);
		assertTrue(r1.isMatch(r2));
		parameter1.setName("newName");
		assertFalse(r1.isMatch(r2));
		parameter2.setName("newName");
		assertTrue(r1.isMatch(r2));

		parameter1.setType("float");
		assertFalse(r1.isMatch(r2));
		parameter2.setType("float");
		assertTrue(r1.isMatch(r2));
	}

	@Test
	public void getCopyTest(){
		RandomModelGenerator generator = new RandomModelGenerator();
		for(int i = 0; i < 100; i++){
			RootNode root = generator.generateModel(3);
			RootNode copy = root.makeClone();
			assertTrue(copy.isMatch(root));
		}
	}

}
