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

import com.ecfeed.core.testutils.RandomModelGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

public class RootNodeHelperTest {

	@Test
	public void generateNewClassNameTest() {

		RootNode rootNode = new RootNode("name", null);
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

}
