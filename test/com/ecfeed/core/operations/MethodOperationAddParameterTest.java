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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.operations.nodes.OnParameterOperationAddToParent;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodOperationAddParameterTest {

	@Test
	public void addParameterForSingle() {

		//		IExtLanguageManager  extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		ClassNode classNode = new ClassNode("com.class1", null);

		// add method with int parameter

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		//		BasicParameterNode methodParameterNode1 =
		//				new BasicParameterNode("arg0", "int", "0", false, null);
		//
		//		OnParameterOperationAddToParent methodOperationAddParameter1 =
		//				new OnParameterOperationAddToParent(
		//					methodNode1,
		//					methodParameterNode1,
		//					extLanguageManagerForSimple);
		//
		//		try {
		//			methodOperationAddParameter1.execute();
		//		} catch (Exception e) {
		//			fail();
		//		}

		MethodNode methodNode2 = new MethodNode("method2", null);
		classNode.addMethod(methodNode2);

		//		BasicParameterNode methodParameterNode2 =
		//				new BasicParameterNode("arg0", "long", "0", false, null);

		//		OnParameterOperationAddToParent methodOperationAddParameter2 =
		//				new OnParameterOperationAddToParent(
		//						methodNode2,
		//						methodParameterNode2,
		//						extLanguageManagerForSimple);
		//
		//		try {
		//			methodOperationAddParameter2.execute();
		//		} catch (Exception e) {
		//			fail();
		//		}
	}

	@Test
	public void addParameterForJava() {

		IExtLanguageManager  extLanguageManagerForJava = new ExtLanguageManagerForJava();

		ClassNode classNode = new ClassNode("com.class1", null);

		// add method with int parameter

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("arg0", "int", "0", false, null);

		OnParameterOperationAddToParent methodOperationAddParameter1 =
				new OnParameterOperationAddToParent(
						methodNode1,
						methodParameterNode1,
						extLanguageManagerForJava);

		try {
			methodOperationAddParameter1.execute();
		} catch (Exception e) {
			fail();
		}

		// add method with long parameter

		MethodNode methodNode2 = new MethodNode("method2", null);
		classNode.addMethod(methodNode2);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("arg0", "long", "0", false, null);

		OnParameterOperationAddToParent methodOperationAddParameter2 =
				new OnParameterOperationAddToParent(
						methodNode2,
						methodParameterNode2,
						extLanguageManagerForJava);

		try {
			methodOperationAddParameter2.execute();
		} catch (Exception e) {
			fail();
		}
	}

}
