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
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;


public class MethodParameterOperationSetTypeTest {

	@Test
	public void setTypeInSimpleModeForOneParam() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("com.Class1", null);
		rootNode.addClass(classNode);

		// add method1 with int parameter

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);

		methodNode1.addParameter(methodParameterNode1);

		// add method2 with String parameter

		MethodNode methodNode2 = new MethodNode("method2", null);
		classNode.addMethod(methodNode2);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par1", "String", "x", false, null);

		methodNode2.addParameter(methodParameterNode2);

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();

		// set param of method2 to Number

		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		try {
			BasicParameterOperationSetType methodParameterOperationSetType =
					new BasicParameterOperationSetType(
							methodParameterNode2, 
							"byte",
							null, 
							typeAdapterProvider,
							extLanguageManagerForSimple);
			
			methodParameterOperationSetType.execute();
		} catch (Exception e) {
			fail();
		}

		// set param of method1 to Text

		try {
			BasicParameterOperationSetType methodParameterOperationSetType =
					new BasicParameterOperationSetType(
							methodParameterNode1, 
							"String", 
							null, 
							typeAdapterProvider,
							extLanguageManagerForSimple);
			
			methodParameterOperationSetType.execute();
		} catch (Exception e) {
			fail();
		}

		// set param of method1 to Logical

		try {
			BasicParameterOperationSetType methodParameterOperationSetType =
					new BasicParameterOperationSetType(
							methodParameterNode1, 
							"boolean", 
							null, 
							typeAdapterProvider,
							extLanguageManagerForSimple);
			
			methodParameterOperationSetType.execute();
		} catch (Exception e) {
			fail();
		}

		// set param of method2 to logical

		try {
			BasicParameterOperationSetType methodParameterOperationSetType =
					new BasicParameterOperationSetType(
							methodParameterNode2, 
							"boolean", 
							null, 
							typeAdapterProvider,
							extLanguageManagerForSimple);
			
			methodParameterOperationSetType.execute();
			
		} catch (Exception e) {
			fail();
		}
	}

//	private ParameterConversionDefinition createParameterConversionDefinition(String srcValue, String dstValue) {
//		
//		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();
//
//		IParameterConversionItemPart srcPart = 
//				new ParameterConversionItemPartForRaw(
//						IParameterConversionItemPart.ItemPartType.VALUE.getCode(), 
//						srcValue);
//		
//		IParameterConversionItemPart dstPart = 
//				new ParameterConversionItemPartForRaw(
//						IParameterConversionItemPart.ItemPartType.VALUE.getCode(), 
//						dstValue);
//
//		ParameterConversionItem parameterConversionItem = new ParameterConversionItem(srcPart, dstPart, "");
//		
//		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItem);
//		
//		return parameterConversionDefinition;
//	}

//	@Test
//	public void setTextToNumberInSimpleMode() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		ClassNode classNode = new ClassNode("com.Class1", null);
//		rootNode.addClass(classNode);
//
//		// add method1 with char parameter
//
//		MethodNode methodNode1 = new MethodNode("method", null);
//		classNode.addMethod(methodNode1);
//
//		MethodParameterNode methodParameterNode1 =
//				new MethodParameterNode("par1", "char", "A", false, null);
//
//		methodNode1.addParameter(methodParameterNode1);
//
//		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();
//		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();
//
//		try {
//			MethodParameterOperationSetType methodParameterOperationSetType =
//					new MethodParameterOperationSetType(
//							methodParameterNode1, "Number", null, extLanguageManagerForSimple, typeAdapterProvider);
//			methodParameterOperationSetType.execute();
//		} catch (Exception e) {
//			fail();
//		}
//
//		assertEquals("byte", methodParameterNode1.getType());
//	}

//	@Test
//	public void setTypeInSimpleModeForTwoParams() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		ClassNode classNode = new ClassNode("com.Class1", null);
//		rootNode.addClass(classNode);
//
//		// add method1 with int,int parameters
//
//		MethodNode methodNode1 = new MethodNode("method", null);
//		classNode.addMethod(methodNode1);
//
//		MethodParameterNode methodParameterNode11 =
//				new MethodParameterNode("par1", "int", "0", false, null);
//
//		methodNode1.addParameter(methodParameterNode11);
//
//		MethodParameterNode methodParameterNode12 =
//				new MethodParameterNode("par2", "int", "0", false, null);
//
//		methodNode1.addParameter(methodParameterNode12);
//
//
//		// add method2 with int,String parameters
//
//		MethodNode methodNode2 = new MethodNode("method", null);
//		classNode.addMethod(methodNode2);
//
//		MethodParameterNode methodParameterNode21 =
//				new MethodParameterNode("par1", "int", "0", false, null);
//
//		methodNode2.addParameter(methodParameterNode21);
//
//		MethodParameterNode methodParameterNode22 =
//				new MethodParameterNode("par2", "String", "x", false, null);
//
//		methodNode2.addParameter(methodParameterNode22);
//
//
//
//		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();
//		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();
//
//		// set param22 to Logical
//
//		try {
//			MethodParameterOperationSetType methodParameterOperationSetType =
//					new MethodParameterOperationSetType(
//							methodParameterNode22, "Logical", null, extLanguageManagerForSimple, typeAdapterProvider);
//			methodParameterOperationSetType.execute();
//		} catch (Exception e) {
//			fail();
//		}
//
//		// set param22 to Number
//
//		try {
//			MethodParameterOperationSetType methodParameterOperationSetType =
//					new MethodParameterOperationSetType(
//							methodParameterNode22, "Number", null, extLanguageManagerForSimple, typeAdapterProvider);
//			methodParameterOperationSetType.execute();
//			fail();
//		} catch (Exception e) {
//			TestHelper.checkExceptionMessage(
//					e,
//					"Class1",
//					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE);
//		}
//
//		// set param12 to Logical
//
//		try {
//			MethodParameterOperationSetType methodParameterOperationSetType =
//					new MethodParameterOperationSetType(
//							methodParameterNode12, "Logical", null, extLanguageManagerForSimple, typeAdapterProvider);
//			methodParameterOperationSetType.execute();
//			fail();
//		} catch (Exception e) {
//			TestHelper.checkExceptionMessage(
//					e,
//					"Class1",
//					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE);
//		}
//	}

	@Test
	public void setTypeInJavaModeForOneParam() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("com.Class1", null);
		rootNode.addClass(classNode);

		// add method1 with int parameter

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);

		methodNode1.addParameter(methodParameterNode1);

		// add method2 with String parameter

		MethodNode methodNode2 = new MethodNode("method2", null);
		classNode.addMethod(methodNode2);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par1", "String", "x", false, null);

		methodNode2.addParameter(methodParameterNode2);

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();
		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		// set param of method2 to int

		try {
			BasicParameterOperationSetType methodParameterOperationSetType =
					new BasicParameterOperationSetType(
							methodParameterNode2, "int", null, typeAdapterProvider, extLanguageManagerForJava);
			methodParameterOperationSetType.execute();
			
		} catch (Exception e) {
			fail();
		}

		// set param of method1 to String

		try {
			BasicParameterOperationSetType methodParameterOperationSetType =
					new BasicParameterOperationSetType(
							methodParameterNode1, "String", null, typeAdapterProvider, extLanguageManagerForJava);
			methodParameterOperationSetType.execute();
		} catch (Exception e) {
			fail();
		}

		// set param of method1 to boolean

		try {
			BasicParameterOperationSetType methodParameterOperationSetType =
					new BasicParameterOperationSetType(
							methodParameterNode1, "boolean", null, typeAdapterProvider, extLanguageManagerForJava);
			methodParameterOperationSetType.execute();
		} catch (Exception e) {
			fail();
		}

		// set param of method2 to boolean

		try {
			BasicParameterOperationSetType methodParameterOperationSetType =
					new BasicParameterOperationSetType(
							methodParameterNode2, "boolean", null, typeAdapterProvider, extLanguageManagerForJava);
			methodParameterOperationSetType.execute();
			
		} catch (Exception e) {
			fail();
		}
	}


}