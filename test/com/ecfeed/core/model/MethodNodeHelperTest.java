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

import static org.junit.Assert.*;

public class MethodNodeHelperTest {

	@Test
	public void signatureCreateTest(){

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

	@Test
	public void validateMethodNameTest() {

		List<String> problems;


		// valid without separator

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("f1", problems, ExtLanguage.JAVA);
		assertEquals(0, problems.size());

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("f1", problems, ExtLanguage.SIMPLE);
		assertEquals(0, problems.size());


		// valid with separator

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("f_1", problems, ExtLanguage.JAVA);
		assertEquals(0, problems.size());

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("f 1", problems, ExtLanguage.SIMPLE);
		assertEquals(0, problems.size());


		// all allowed characters

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_$", problems, ExtLanguage.JAVA);
		assertEquals(0, problems.size());

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 $", problems, ExtLanguage.SIMPLE);
		assertEquals(0, problems.size());


		// just dolar

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("$", problems, ExtLanguage.JAVA);
		assertEquals(0, problems.size());

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("$", problems, ExtLanguage.SIMPLE);
		assertEquals(0, problems.size());


		// invalid separator

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("f 1", problems, ExtLanguage.JAVA);
		assertEquals(1, problems.size());

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("f_1", problems, ExtLanguage.SIMPLE);
		assertEquals(1, problems.size());


		// invalid char

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("#", problems, ExtLanguage.JAVA);
		assertEquals(1, problems.size());

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("#", problems, ExtLanguage.SIMPLE);
		assertEquals(1, problems.size());


		// number at the front

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("1a", problems, ExtLanguage.JAVA);
		assertEquals(1, problems.size());

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("1a", problems, ExtLanguage.SIMPLE);
		assertEquals(1, problems.size());


		// just separator

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName("_a", problems, ExtLanguage.JAVA);
		assertEquals(1, problems.size());

		problems = new ArrayList<>();
		MethodNodeHelper.validateMethodName(" a", problems, ExtLanguage.SIMPLE);
		assertEquals(1, problems.size());
	}

	@Test
	public void getParameterNamesAndTypesTest(){

		ClassNode classNode = new ClassNode("class1", null);

		MethodNode methodNode = new MethodNode("method_1", null);
		classNode.addMethod(methodNode);

		MethodParameterNode param1 = new MethodParameterNode("param1", null, "int", "0", false);
		methodNode.addParameter(param1);

		MethodParameterNode param2 = new MethodParameterNode("param2", null, "double", "0.0", true);
		methodNode.addParameter(param2);

		List<String> paramNames = MethodNodeHelper.getParameterNames(methodNode);
		assertEquals(2, paramNames.size());
		assertEquals("param1", paramNames.get(0));
		assertEquals("param2", paramNames.get(1));

		List<String> paramTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, ExtLanguage.JAVA);
		assertEquals(2, paramTypes.size());
		assertEquals("int", paramTypes.get(0));
		assertEquals("double", paramTypes.get(1));

		paramTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, ExtLanguage.SIMPLE);
		assertEquals(2, paramTypes.size());
		assertEquals("Number", paramTypes.get(0));
		assertEquals("Number", paramTypes.get(1));
	}

}
