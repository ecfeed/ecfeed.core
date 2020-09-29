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

import static org.junit.Assert.*;

public class MethodParameterNodeHelperTest {


	@Test
	public void createParameterTest() { // TODO SIMPLE-VIEW move

		MethodParameterNode methodParameterNode;

		try {
			new MethodParameterNode("par%1", null, "int", "0", false);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodParameterNode("!", null, "int", "0", false);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodParameterNode("a b", null, "int", "0", false);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void getNameTest() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode("parameter_1", null, "type1", "0", false);

		String name = MethodParameterNodeHelper.getName(methodParameterNode, ExtLanguage.JAVA);
		assertEquals("parameter_1", name);

		name = MethodParameterNodeHelper.getName(methodParameterNode, ExtLanguage.SIMPLE);
		assertEquals("parameter 1", name);

	}

	@Test
	public void createSignatureTest(){

		MethodParameterNode methodParameterNode;

		methodParameterNode =
				new MethodParameterNode("par_1", null, "int", "0", false);

		String signature = MethodParameterNodeHelper.createSignature(methodParameterNode, ExtLanguage.JAVA);
		assertEquals("int par_1", signature);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, ExtLanguage.SIMPLE);
		assertEquals("Number par 1", signature);


		methodParameterNode =
				new MethodParameterNode("par_1", null, "String", "0", true);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, ExtLanguage.JAVA);
		assertEquals("[e]String par_1", signature);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, ExtLanguage.SIMPLE);
		assertEquals("[e]Text par 1", signature);

		GlobalParameterNode globalParameterNode = new GlobalParameterNode("global_1", null, "String");
		methodParameterNode.setLink(globalParameterNode);

		methodParameterNode.setLinked(true);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, ExtLanguage.JAVA);
		assertEquals("[e]String par_1[LINKED]->global_1", signature);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, ExtLanguage.SIMPLE);
		assertEquals("[e]Text par 1[LINKED]->global 1", signature);

	}

}
