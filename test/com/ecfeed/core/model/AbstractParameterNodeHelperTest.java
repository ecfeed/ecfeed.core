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

public class AbstractParameterNodeHelperTest {

	// TODO MO-RE
	//	@Test
	//	public void createSignatureByIntrLanguageTest() {
	//
	//		String signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int","par1",false,	new ExtLanguageManagerForJava());
	//		assertEquals("int par1", signature);
	//
	//		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int","par1",false,	new ExtLanguageManagerForSimple());
	//		assertEquals("Number: par1", signature);
	//
	//		// with expected flag
	//
	//		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int","par1",true,	new ExtLanguageManagerForJava());
	//		assertEquals("[e]int par1", signature);
	//
	//		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int","par1",true,	new ExtLanguageManagerForSimple());
	//		assertEquals("[e]Number: par1", signature);
	//
	//		// without parameter name
	//
	//		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int",null,false,	new ExtLanguageManagerForJava());
	//		assertEquals("int", signature);
	//
	//		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int",null,false,	new ExtLanguageManagerForSimple());
	//		assertEquals("Number", signature);
	//
	//		// expected without parameter
	//
	//		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int",null,true,	new ExtLanguageManagerForJava());
	//		assertEquals("[e]int", signature);
	//
	//		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int",null,true,	new ExtLanguageManagerForSimple());
	//		assertEquals("[e]Number", signature);
	//
	//	}

	// TODO MO-RE
	//	@Test
	//	public void createSignatureByExtLanguageTest() {
	//
	//		String signature = AbstractParameterNodeHelper.createSignature(
	//				"Number","par1",false, new ExtLanguageManagerForSimple());
	//		assertEquals("Number: par1", signature);
	//
	//		signature = AbstractParameterNodeHelper.createSignature(
	//				"Number","par1",true, new ExtLanguageManagerForSimple());
	//		assertEquals("[e]Number: par1", signature);
	//	}

	@Test
	public void createParameterLabelsTest() {

		BasicParameterNode methodParameterNode =
				new BasicParameterNode(
						"name", "int", "0", true, null);

		String label = MethodParameterNodeHelper.createSignature(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("[e]int name", label);

		label = MethodParameterNodeHelper.createSignature(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("[e]Number: name", label);
	}

	// TODO MO-RE
	//	@Test
	//	public void createTypeLabelsTest() {
	//
	//		BasicParameterNode methodParameterNode =
	//				new BasicParameterNode(
	//						"name", "int", "0", true, null);
	//
	//		String label = AbstractParameterNodeHelper.getType(methodParameterNode, new ExtLanguageManagerForJava());
	//		assertEquals("int", label);
	//
	//		label = AbstractParameterNodeHelper.getType(methodParameterNode, new ExtLanguageManagerForSimple());
	//		assertEquals("Number", label);
	//	}

}
