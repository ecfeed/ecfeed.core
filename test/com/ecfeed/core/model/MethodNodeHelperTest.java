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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MethodNodeHelperTest {

	@Test
	public void getMethodNameTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		String methodName = MethodNodeHelper.getMethodName(methodNode, ExtLanguage.JAVA);
		assertEquals("method_1", methodName);

		methodName = MethodNodeHelper.getMethodName(methodNode, ExtLanguage.SIMPLE);
		assertEquals("method 1", methodName);
	}

	@Test
	public void getParameterNamesTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		MethodParameterNode param1 = new MethodParameterNode("param1", null, "int", "0", false);
		methodNode.addParameter(param1);

		MethodParameterNode param2 = new MethodParameterNode("param2", null, "int", "0", false);
		methodNode.addParameter(param2);

		List<String> methodParameterNames = MethodNodeHelper.getParameterNames(methodNode);

		assertEquals(2,  methodParameterNames.size());
		assertEquals("param1", methodParameterNames.get(0));
		assertEquals("param2", methodParameterNames.get(1));
	}

	@Test
	public void getParameterTypesTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		MethodParameterNode param1 = new MethodParameterNode("param1", null, "int", "0", false);
		methodNode.addParameter(param1);

		MethodParameterNode param2 = new MethodParameterNode("param2", null, "double", "0", false);
		methodNode.addParameter(param2);

		// java types

		List<String> methodParameterTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, ExtLanguage.JAVA);

		assertEquals(2,  methodParameterTypes.size());
		assertEquals("int", methodParameterTypes.get(0));
		assertEquals("double", methodParameterTypes.get(1));

		// simple types

		methodParameterTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, ExtLanguage.SIMPLE);

		assertEquals(2,  methodParameterTypes.size());
		assertEquals("Number", methodParameterTypes.get(0));
		assertEquals("Number", methodParameterTypes.get(1));
	}


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

		// by external language

		String[] params = {"Number", "Text", "Logical"};
		signature = MethodNodeHelper.createSignature(
				"f 1",
				Arrays.asList(params),
				null,
				null);

		assertEquals("f 1(Number, Text, Logical)", signature);

		// with parameter names

		String[] paramNames = {"num", "txt", "log"};
		signature = MethodNodeHelper.createSignature(
				"f 1",
				Arrays.asList(params),
				Arrays.asList(paramNames),
				null);

		assertEquals("f 1(Number num, Text txt, Logical log)", signature);

		// with expected decorations

		Boolean[] expDecorations = {true, false, true};
		signature = MethodNodeHelper.createSignature(
				"f 1",
				Arrays.asList(params),
				Arrays.asList(paramNames),
				Arrays.asList(expDecorations));

		assertEquals("f 1([e]Number num, Text txt, [e]Logical log)", signature);

	}

	@Test
	public void validateMethodNameTest() {

		// valid without separator

		String errorMessage;

		errorMessage = MethodNodeHelper.validateMethodName("f1", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("f1", ExtLanguage.SIMPLE);
		assertNull(errorMessage);


		// valid with separator

		errorMessage = MethodNodeHelper.validateMethodName("f_1", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("f 1", ExtLanguage.SIMPLE);
		assertNull(errorMessage);


		// all allowed characters

		errorMessage =
				MethodNodeHelper.validateMethodName(
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_$",
						ExtLanguage.JAVA);

		assertNull(errorMessage);


		errorMessage =
				MethodNodeHelper.validateMethodName(
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 $",
						ExtLanguage.SIMPLE);

		assertNull(errorMessage);


		// just dolar

		errorMessage = MethodNodeHelper.validateMethodName("$", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("$", ExtLanguage.SIMPLE);
		assertNull(errorMessage);


		// invalid separator

		errorMessage = MethodNodeHelper.validateMethodName("f 1", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("f_1", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);


		// invalid char

		errorMessage = MethodNodeHelper.validateMethodName("#", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("#", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);


		// number at the front

		errorMessage = MethodNodeHelper.validateMethodName("1a", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("1a", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);


		// just separator

		errorMessage = MethodNodeHelper.validateMethodName("_a", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName(" a", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);
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
