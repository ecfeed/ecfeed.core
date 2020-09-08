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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ClassNodeHelperTest {

	@Test
	public void getNameAndPackageTest(){

		ClassNode classNode = new ClassNode("pack.class1", null);
		String simpleName = ClassNodeHelper.getSimpleName(classNode);
		assertEquals("class1", simpleName);

		String packageName = ClassNodeHelper.getPackageName(classNode);
		assertEquals("pack", packageName);

		String qualifiedName = ClassNodeHelper.getQualifiedName(classNode);
		assertEquals("pack.class1", qualifiedName);
	}

	@Test
	public void verifyNameTest(){

		ClassNode classNode = new ClassNode("pack.class1", null);
		String simpleName = ClassNodeHelper.getSimpleName(classNode);
		assertEquals("class1", simpleName);

		String packageName = ClassNodeHelper.getPackageName(classNode);
		assertEquals("pack", packageName);

		String qualifiedName = ClassNodeHelper.getQualifiedName(classNode);
		assertEquals("pack.class1", qualifiedName);
	}

}
