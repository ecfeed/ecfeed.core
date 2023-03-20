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

	//	@Test
	//	public void createSignatureByIntrLanguageTest() {
	//
	//		String signature = AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int","par1",false,	new ExtLanguageManagerForJava());
	//		assertEquals("int par1", signature);
	//
	//		signature = AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int","par1",false,	new ExtLanguageManagerForSimple());
	//		assertEquals("Number: par1", signature);
	//
	//		// with expected flag
	//
	//		signature = AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int","par1",true,	new ExtLanguageManagerForJava());
	//		assertEquals("[e]int par1", signature);
	//
	//		signature = AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int","par1",true,	new ExtLanguageManagerForSimple());
	//		assertEquals("[e]Number: par1", signature);
	//
	//		// without parameter name
	//
	//		signature = AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int",null,false,	new ExtLanguageManagerForJava());
	//		assertEquals("int", signature);
	//
	//		signature = AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int",null,false,	new ExtLanguageManagerForSimple());
	//		assertEquals("Number", signature);
	//
	//		// expected without parameter
	//
	//		signature = AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int",null,true,	new ExtLanguageManagerForJava());
	//		assertEquals("[e]int", signature);
	//
	//		signature = AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
	//				"int",null,true,	new ExtLanguageManagerForSimple());
	//		assertEquals("[e]Number", signature);
	//
	//	}

	//	@Test
	//	public void createSignatureByExtLanguageTest() {
	//
	//		String signature = AbstractParameterSignatureHelper.createSignature(
	//				"Number","par1",false, new ExtLanguageManagerForSimple());
	//		assertEquals("Number: par1", signature);
	//
	//		signature = AbstractParameterSignatureHelper.createSignature(
	//				"Number","par1",true, new ExtLanguageManagerForSimple());
	//		assertEquals("[e]Number: par1", signature);
	//	}

	@Test
	public void createTypeLabelsTest() {

		BasicParameterNode methodParameterNode =
				new BasicParameterNode(
						"name", "int", "0", true, null);

		String label = AbstractParameterSignatureHelper.getType(methodParameterNode, new ExtLanguageManagerForJava());
		assertEquals("int", label);

		label = AbstractParameterSignatureHelper.getType(methodParameterNode, new ExtLanguageManagerForSimple());
		assertEquals("Number", label);
	}

}
