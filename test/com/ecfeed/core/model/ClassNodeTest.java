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

import com.ecfeed.core.utils.TestHelper;
import org.junit.Test;

public class ClassNodeTest extends ClassNode {
	public ClassNodeTest(){
		super("com.ecfeed.model.ClassNodeTest", null);
	}

	@Test
	public void getChildrenTest(){
		ClassNode classNode = new ClassNode("com.example.ClassName", null);
		MethodNode method1 = new MethodNode("method1", null);
		MethodNode method2 = new MethodNode("method2", null);

		classNode.addMethod(method1);
		classNode.addMethod(method2);

		List<IAbstractNode> children = classNode.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(method1));
		assertTrue(children.contains(method2));
	}

	@Test
	public void getMethodTest() {
		ClassNode classNode = new ClassNode("com.example.ClassName", null);
		MethodNode method1 = new MethodNode("method", null);
		MethodNode method2 = new MethodNode("method", null);

		List<String> method1Types = new ArrayList<String>();
		method1Types.add("int");
		method1Types.add("double");

		List<String> method2Types = new ArrayList<String>();
		method2Types.add("int");
		method2Types.add("int");

		int inx = 0;
		for(String type : method1Types){
			method1.addParameter(new BasicParameterNode("parameter" +  inx++, type, "0", false, null));
		}

		for(String type : method2Types){
			method2.addParameter(new BasicParameterNode("parameter" + inx++, type, "0", false, null));
		}

		classNode.addMethod(method1);

		try {
			classNode.addMethod(method2);
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, "Cannot add method.", "Method with the same name already exists.");
		}

		assertEquals(method1, classNode.findMethodWithTheSameName("method"));
	}

	@Test
	public void getMethodsTest() {
		ClassNode classNode = new ClassNode("com.example.ClassName", null);
		MethodNode method1 = new MethodNode("method1", null);
		MethodNode method2 = new MethodNode("method2", null);
		classNode.addMethod(method1);
		classNode.addMethod(method2);

		assertTrue(classNode.getMethods().contains(method1));
		assertTrue(classNode.getMethods().contains(method2));
	}

	@Test
	public void getTestSuitesTest(){
		ClassNode classNode = new ClassNode("com.example.ClassName", null);
		MethodNode method1 = new MethodNode("method1", null);
		MethodNode method2 = new MethodNode("method2", null);

		method1.addTestCase(new TestCaseNode("suite 1", null, null));
		method1.addTestCase(new TestCaseNode("suite 2", null, null));
		method1.addTestCase(new TestCaseNode("suite 2", null, null));
		method1.addTestCase(new TestCaseNode("suite 3", null, null));

		method2.addTestCase(new TestCaseNode("suite 1", null, null));
		method2.addTestCase(new TestCaseNode("suite 4", null, null));
		method2.addTestCase(new TestCaseNode("suite 2", null, null));
		method2.addTestCase(new TestCaseNode("suite 3", null, null));

		classNode.addMethod(method1);
		classNode.addMethod(method2);

		assertEquals(4, classNode.getTestCaseNames().size());
		assertTrue(classNode.getTestCaseNames().contains("suite 1"));
		assertTrue(classNode.getTestCaseNames().contains("suite 2"));
		assertTrue(classNode.getTestCaseNames().contains("suite 3"));
		assertTrue(classNode.getTestCaseNames().contains("suite 4"));
		assertFalse(classNode.getTestCaseNames().contains("unused test suite"));
	}

	@Test
	public void compareTest(){
		ClassNode c1 = new ClassNode("c1", null);
		ClassNode c2 = new ClassNode("c2", null);

		assertFalse(c1.isMatch(c2));

		c2.setName("c1");
		assertTrue(c1.isMatch(c2));

		MethodNode m1 = new MethodNode("m1", null);
		MethodNode m2 = new MethodNode("m2", null);

		c1.addMethod(m1);
		assertFalse(c1.isMatch(c2));

		c2.addMethod(m2);
		assertFalse(c1.isMatch(c2));

		m2.setName("m1");
		assertTrue(c1.isMatch(c2));

		BasicParameterNode parameter1 = new BasicParameterNode("parameter1", "int", "0", false, null);
		c1.addParameter(parameter1);
		assertFalse(c1.isMatch(c2));
		BasicParameterNode parameter2 = new BasicParameterNode("parameter1", "int", "0", false, null);
		c2.addParameter(parameter2);
		assertTrue(c1.isMatch(c2));
		parameter1.setName("newName");
		assertFalse(c1.isMatch(c2));
		parameter2.setName("newName");
		assertTrue(c1.isMatch(c2));
		parameter1.setType("float");
		assertFalse(c1.isMatch(c2));
		parameter2.setType("float");
		assertTrue(c1.isMatch(c2));
	}
}
