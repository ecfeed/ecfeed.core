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

		String errorMessage;

		errorMessage = ClassNodeHelper.validateClassName("c1", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("c1", ExtLanguage.SIMPLE);
		assertNull(errorMessage);


		// valid with separator

		errorMessage = ClassNodeHelper.validateClassName("c_1", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("c 1", ExtLanguage.SIMPLE);
		assertNull(errorMessage);


		// all allowed characters

		errorMessage =
				ClassNodeHelper.validateClassName(
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_$",
						ExtLanguage.JAVA);

		assertNull(errorMessage);


		errorMessage =
				ClassNodeHelper.validateClassName(
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 $",
						ExtLanguage.SIMPLE);

		assertNull(errorMessage);


		// just dolar

		errorMessage = ClassNodeHelper.validateClassName("$", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("$", ExtLanguage.SIMPLE);
		assertNull(errorMessage);


		// invalid separator

		errorMessage = ClassNodeHelper.validateClassName("c 1", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("c_1", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);


		// invalid char

		errorMessage = ClassNodeHelper.validateClassName("#", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("#", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);


		// number at the front

		errorMessage = ClassNodeHelper.validateClassName("1a", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("1a", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);


		// just separator

		errorMessage = ClassNodeHelper.validateClassName("_a", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName(" a", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);
	}

}
