/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.operations.nodes.OnClassOperationAddToRoot;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.TestHelper;

public class RootOperationAddClassTest {

	@Test
	public void test1() {

		RootNode rootNode = new RootNode("Root", null);

		// add class to empty root

		ClassNode classNode1 = new ClassNode("com.Class1", null);

		OnClassOperationAddToRoot operation1Simple = 
				new OnClassOperationAddToRoot(rootNode, classNode1, false, new ExtLanguageManagerForSimple());

		try {
			operation1Simple.execute();
		} catch (Exception e) {
			fail();
		}

		// add class with colliding name in simple mode only

		ClassNode classNode2 = new ClassNode("com.xx.Class1", null);

		OnClassOperationAddToRoot operation2Simple = 
				new OnClassOperationAddToRoot(rootNode, classNode2, false, new ExtLanguageManagerForSimple());

		try {
			operation2Simple.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					RootNodeHelper.CLASS_WITH_NAME,
					"Class1",
					RootNodeHelper.ALREADY_EXISTS);
		}

		// adding the same class in Java mode should be ok

		OnClassOperationAddToRoot operation2Java = 
				new OnClassOperationAddToRoot(rootNode, classNode2, false, new ExtLanguageManagerForJava());

		try {
			operation2Java.execute();
		} catch (Exception e) {
			fail();
		}

		// adding class with the same name should fail in Java mode

		try {
			operation2Java.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					RootNodeHelper.CLASS_WITH_NAME,
					"com.xx.Class1",
					RootNodeHelper.ALREADY_EXISTS);
		}
	}

}
