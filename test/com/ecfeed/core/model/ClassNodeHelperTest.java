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

import com.ecfeed.core.utils.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ClassNodeHelperTest {

	@Test
	public void getNameAndPackageTest(){

		ClassNode classNode = new ClassNode("pack.class_1", null);

		String simpleName = ClassNodeHelper.getNonQualifiedName(classNode, new ExtLanguageManagerForJava());
		assertEquals("class_1", simpleName);

		simpleName = ClassNodeHelper.getNonQualifiedName(classNode, new ExtLanguageManagerForSimple());
		assertEquals("class 1", simpleName);


		String packageName = ClassNodeHelper.getPackageName(classNode, new ExtLanguageManagerForJava());
		assertEquals("pack", packageName);

		packageName = ClassNodeHelper.getPackageName(classNode, new ExtLanguageManagerForSimple());
		assertEquals("", packageName);


		String qualifiedName = ClassNodeHelper.getQualifiedName(classNode, new ExtLanguageManagerForJava());
		assertEquals("pack.class_1", qualifiedName);

		qualifiedName = ClassNodeHelper.getQualifiedName(classNode, new ExtLanguageManagerForSimple());
		assertEquals("class 1", qualifiedName);
	}

	@Test
	public void verifyNameTest(){

		String errorMessage;

		errorMessage = ClassNodeHelper.validateClassName("c1", new ExtLanguageManagerForJava());
		assertNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("c1", new ExtLanguageManagerForSimple());
		assertNull(errorMessage);


		// valid with separator

		errorMessage = ClassNodeHelper.validateClassName("c_1", new ExtLanguageManagerForJava());
		assertNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("c 1", new ExtLanguageManagerForSimple());
		assertNull(errorMessage);


		// all allowed characters

		errorMessage =
				ClassNodeHelper.validateClassName(
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_$",
						new ExtLanguageManagerForJava());

		assertNull(errorMessage);


		errorMessage =
				ClassNodeHelper.validateClassName(
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 $",
						new ExtLanguageManagerForSimple());

		assertNull(errorMessage);


		// just dolar

		errorMessage = ClassNodeHelper.validateClassName("$", new ExtLanguageManagerForJava());
		assertNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("$", new ExtLanguageManagerForSimple());
		assertNull(errorMessage);


		// invalid separator

		errorMessage = ClassNodeHelper.validateClassName("c 1", new ExtLanguageManagerForJava());
		assertNotNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("c_1", new ExtLanguageManagerForSimple());
		assertNotNull(errorMessage);


		// invalid char

		errorMessage = ClassNodeHelper.validateClassName("#", new ExtLanguageManagerForJava());
		assertNotNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("#", new ExtLanguageManagerForSimple());
		assertNotNull(errorMessage);


		// number at the front

		errorMessage = ClassNodeHelper.validateClassName("1a", new ExtLanguageManagerForJava());
		assertNotNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName("1a", new ExtLanguageManagerForSimple());
		assertNotNull(errorMessage);


		// just separator

		errorMessage = ClassNodeHelper.validateClassName("_a", new ExtLanguageManagerForJava());
		assertNotNull(errorMessage);

		errorMessage = ClassNodeHelper.validateClassName(" a", new ExtLanguageManagerForSimple());
		assertNotNull(errorMessage);
	}

	@Test
	public void verifyMethodSignatureTest(){

		ClassNode classNode = new ClassNode("class1", null);

		String methodNameInJavaLanguage = "method_1";

		String[] invalidParameterTypesInJavaLanguage = { "bool", "Int" };
		String[] invalidParameterTypesInSimpleLanguage = { "Num", "Tex" };

		List<String> paramTypesInJavaLanguage = new ArrayList<>();
		paramTypesInJavaLanguage.add("int");
		paramTypesInJavaLanguage.add("double");


		String methodNameInSimpleLanguage = "method 1";

		List<String> paramTypesInSimpleLanguage = new ArrayList<>();
		paramTypesInSimpleLanguage.add("Number");
		paramTypesInSimpleLanguage.add("Number");

		// invalid parameter types in java language

		String errorMessage =
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInJavaLanguage, Arrays.asList(invalidParameterTypesInJavaLanguage), new ExtLanguageManagerForJava());

		assertNotNull(errorMessage);

		errorMessage =
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInSimpleLanguage, Arrays.asList(invalidParameterTypesInSimpleLanguage), new ExtLanguageManagerForSimple());

		assertNotNull(errorMessage);

		// empty class

		errorMessage =
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInJavaLanguage, paramTypesInJavaLanguage, new ExtLanguageManagerForJava());

		assertNull(errorMessage);

		errorMessage =
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInSimpleLanguage, paramTypesInSimpleLanguage, new ExtLanguageManagerForSimple());

		assertNull(errorMessage);


		// class with one method without parameters

		MethodNode methodNode = new MethodNode("method_1", null);
		classNode.addMethod(methodNode);

		errorMessage =
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInJavaLanguage, paramTypesInJavaLanguage, new ExtLanguageManagerForJava());

		assertNull(errorMessage);

		errorMessage =
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInSimpleLanguage, paramTypesInSimpleLanguage, new ExtLanguageManagerForSimple());

		assertNull(errorMessage);


		// class with conflicting method

		MethodParameterNode param1 = new MethodParameterNode("param1", null, "int", "0", false);
		methodNode.addParameter(param1);

		MethodParameterNode param2 = new MethodParameterNode("param2", null, "double", "0.0", true);
		methodNode.addParameter(param2);


		errorMessage =
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInJavaLanguage, paramTypesInJavaLanguage, new ExtLanguageManagerForJava());

		assertNotNull(errorMessage);

		errorMessage =
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInSimpleLanguage, paramTypesInSimpleLanguage, new ExtLanguageManagerForSimple());

		assertNotNull(errorMessage);
	}

	@Test
	public void generateNewMethodNameTest() {

		// class node without methods

		ClassNode classNode = new ClassNode("class1", null);

		// invalid parameter type in java language

		String[] paramTypes1 = {"int", "x"};
		try {
			String result =
					ClassNodeHelper.generateNewMethodName(
							classNode, "method", Arrays.asList(paramTypes1), new ExtLanguageManagerForJava());
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, JavaLanguageHelper.INVALID_JAVA_TYPE);
		}

		// simple language

		String[] paramTypes2 = {"Num", "Tex"};
		try {
			String result =
					ClassNodeHelper.generateNewMethodName(
							classNode, "method", Arrays.asList(paramTypes2), new ExtLanguageManagerForSimple());
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, SimpleLanguageHelper.INVALID_SIMPLE_TYPE);
		}

		// java language

		String methodName;

		String[] paramTypesInJavaLanguage = {"int", "String"};
		methodName =
				ClassNodeHelper.generateNewMethodName(
					classNode, "method_1", Arrays.asList(paramTypesInJavaLanguage), new ExtLanguageManagerForJava());
		assertEquals("method_1", methodName);

		// simple language

		String[] paramTypesInSimpleLanguage = {"Number", "Text"};
		methodName =
				ClassNodeHelper.generateNewMethodName(
						classNode, "method 1", Arrays.asList(paramTypesInSimpleLanguage), new ExtLanguageManagerForSimple());
		assertEquals("method 1", methodName);

		// add method with the same name but only one parameter

		MethodNode methodNode1 = new MethodNode("method_1", null);
		classNode.addMethod(methodNode1);

		MethodParameterNode param1 = new MethodParameterNode("param1", null, "int", "0", false);
		methodNode1.addParameter(param1);

		// check in java language

		methodName =
				ClassNodeHelper.generateNewMethodName(
						classNode, "method_1", Arrays.asList(paramTypesInJavaLanguage), new ExtLanguageManagerForJava());
		assertEquals("method_1", methodName);

		// check in simple language

		methodName =
				ClassNodeHelper.generateNewMethodName(
						classNode, "method 1", Arrays.asList(paramTypesInSimpleLanguage), new ExtLanguageManagerForSimple());
		assertEquals("method 1", methodName);

		// adding the second parameter

		MethodParameterNode param2 = new MethodParameterNode("param2", null, "String", "0", false);
		methodNode1.addParameter(param2);

		// check in Java and Simple language

		methodName =
				ClassNodeHelper.generateNewMethodName(
						classNode, "method_1", Arrays.asList(paramTypesInJavaLanguage), new ExtLanguageManagerForJava());
		assertEquals("method_2", methodName);

		methodName =
				ClassNodeHelper.generateNewMethodName(
						classNode, "method 1", Arrays.asList(paramTypesInSimpleLanguage), new ExtLanguageManagerForSimple());
		assertEquals("method 2", methodName);
	}

	@Test
	public void createSignatureTest() {

		// class node without methods

		ClassNode classNode = new ClassNode("com.class_1", null);

		String signature = ClassNodeHelper.createSignature(classNode, new ExtLanguageManagerForJava());
		assertEquals("com.class_1", signature);

		signature = ClassNodeHelper.createSignature(classNode, new ExtLanguageManagerForSimple());
		assertEquals("class 1", signature);
	}

	@Test
	public void convertParameterTypesInJavaExtLanguageTest() {

		String[] parameterTypes = {"byte", "short", "int", "long", "float", "double", "char", "String", "boolean"};

		List<String> convertedTypes =
				AbstractParameterNodeHelper.convertParameterTypesToExtLanguage(
					Arrays.asList(parameterTypes),
					new ExtLanguageManagerForJava());

		assertEquals(9, convertedTypes.size());
		assertEquals("byte", convertedTypes.get(0));
		assertEquals("short", convertedTypes.get(1));
		assertEquals("int", convertedTypes.get(2));
		assertEquals("long", convertedTypes.get(3));
		assertEquals("float", convertedTypes.get(4));
		assertEquals("double", convertedTypes.get(5));
		assertEquals("char", convertedTypes.get(6));
		assertEquals("String", convertedTypes.get(7));
		assertEquals("boolean", convertedTypes.get(8));
	}

	@Test
	public void convertParameterTypesInSimpleExtLanguageTest() {

		String[] parameterTypes = {"byte", "short", "int", "long", "float", "double", "char", "String", "boolean"};

		List<String> convertedTypes =
				AbstractParameterNodeHelper.convertParameterTypesToExtLanguage(
						Arrays.asList(parameterTypes),
						new ExtLanguageManagerForSimple());

		assertEquals(9, convertedTypes.size());
		assertEquals("Number", convertedTypes.get(0));
		assertEquals("Number", convertedTypes.get(1));
		assertEquals("Number", convertedTypes.get(2));
		assertEquals("Number", convertedTypes.get(3));
		assertEquals("Number", convertedTypes.get(4));
		assertEquals("Number", convertedTypes.get(5));
		assertEquals("Text", convertedTypes.get(6));
		assertEquals("Text", convertedTypes.get(7));
		assertEquals("Logical", convertedTypes.get(8));
	}

}
