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

import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import org.junit.Test;

import static org.junit.Assert.*;

public class MethodParameterNodeHelperTest {

	@Test
	public void getNameTest() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode("parameter_1", "type1", "0", false, null);

		String name = MethodParameterNodeHelper.getName(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("parameter_1", name);

		name = MethodParameterNodeHelper.getName(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("parameter 1", name);

	}

	@Test
	public void createSignatureTest(){

		MethodParameterNode methodParameterNode;

		methodParameterNode =
				new MethodParameterNode("par_1", "int", "0", false, null);

		String signature = MethodParameterNodeHelper.createSignature(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("int par_1", signature);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("Number par 1", signature);


		methodParameterNode =
				new MethodParameterNode("par_1", "String", "0", true, null);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("[e]String par_1", signature);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("[e]Text par 1", signature);

		GlobalParameterNode globalParameterNode = new GlobalParameterNode("global_1", null, "String");
		methodParameterNode.setLink(globalParameterNode);

		methodParameterNode.setLinked(true);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("[e]String par_1[LINKED]->global_1", signature);

		signature = MethodParameterNodeHelper.createSignature(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("[e]Text par 1[LINKED]->global 1", signature);
	}

	@Test
	public void createReverseSignatureTest(){

		MethodParameterNode methodParameterNode;

		methodParameterNode =
				new MethodParameterNode("par_1", "int", "0", false, null);

		String signature = MethodParameterNodeHelper.createReverseSignature(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("par_1 : int", signature);

		signature = MethodParameterNodeHelper.createReverseSignature(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("par 1 : Number", signature);


		methodParameterNode =
				new MethodParameterNode("par_1", "String", "0", true, null);

		signature = MethodParameterNodeHelper.createReverseSignature(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("[e]par_1 : String", signature);

		signature = MethodParameterNodeHelper.createReverseSignature(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("[e]par 1 : Text", signature);

		GlobalParameterNode globalParameterNode = new GlobalParameterNode("global_1", null, "String");
		methodParameterNode.setLink(globalParameterNode);

		methodParameterNode.setLinked(true);

		signature = MethodParameterNodeHelper.createReverseSignature(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("[e]par_1 : String[LINKED]->global_1", signature);

		signature = MethodParameterNodeHelper.createReverseSignature(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("[e]par 1 : Text[LINKED]->global 1", signature);
	}

}
