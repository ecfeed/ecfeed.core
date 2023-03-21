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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class RootNodeHelperTest {

	@Test
	public void generateNewClassNameTest() {

		RootNode rootNode = new RootNode("Root", null);
		final String classNameCore = "Class";
		String newClassName = RootNodeHelper.generateNewClassName(rootNode, classNameCore);
		assertEquals("Class1", newClassName);

		ClassNode class1 = new ClassNode(newClassName, null);
		rootNode.addClass(class1);

		newClassName = RootNodeHelper.generateNewClassName(rootNode, classNameCore);
		assertEquals("Class2", newClassName);

		ClassNode class2 = new ClassNode(newClassName, null);
		rootNode.addClass(class2);

		newClassName = RootNodeHelper.generateNewClassName(rootNode, classNameCore);
		assertEquals("Class3", newClassName);
	}

	@Test
	public void classWithNameExistsTest() {

		RootNode rootNode = new RootNode("Root", null);
		assertNull(RootNodeHelper.classWithNameExists("Class1",  rootNode, new ExtLanguageManagerForJava()));

		ClassNode class1 = new ClassNode("Class1", null);
		rootNode.addClass(class1);

		assertNotNull(RootNodeHelper.classWithNameExists("Class1",  rootNode, new ExtLanguageManagerForJava()));
	}

}
