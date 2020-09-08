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
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MethodNodeHelperTest {

	@Test
	public void test1(){

		ClassNode classNode = new ClassNode("class1", null);

		MethodNode methodNode = new MethodNode("method_1", null);
		classNode.addMethod(methodNode);

		String signature = MethodNodeHelper.createSignature(methodNode, ExtLanguage.JAVA);
		assertEquals("method_1()", signature);

		signature = MethodNodeHelper.createSignature(methodNode, ExtLanguage.SIMPLE);
		assertEquals("method 1()", signature);


		MethodParameterNode param1 = new MethodParameterNode("param1", null, "int", "0", false);
		methodNode.addParameter(param1);

		signature = MethodNodeHelper.createSignature(methodNode, ExtLanguage.JAVA);
		assertEquals("method_1(int param1)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, ExtLanguage.SIMPLE);
		assertEquals("method 1(Number param1)", signature);


		MethodParameterNode param2 = new MethodParameterNode("param2", null, "double", "0.0", true);
		methodNode.addParameter(param2);

		signature = MethodNodeHelper.createSignature(methodNode, false, ExtLanguage.JAVA);
		assertEquals("method_1(int param1, double param2)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, ExtLanguage.JAVA);
		assertEquals("method_1(int param1, [e]double param2)", signature);

		
		signature = MethodNodeHelper.createSignature(methodNode, false, ExtLanguage.SIMPLE);
		assertEquals("method 1(Number param1, Number param2)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, ExtLanguage.SIMPLE);
		assertEquals("method 1(Number param1, [e]Number param2)", signature);


		signature = MethodNodeHelper.createLongSignature(methodNode, ExtLanguage.JAVA);
		assertEquals("class1.method_1(int param1, double param2)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, ExtLanguage.SIMPLE);
		assertEquals("class1.method 1(Number param1, Number param2)", signature);
	}

}
