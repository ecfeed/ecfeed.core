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

public class AbstractParameterNodeHelperTest {

	@Test
	public void createSignatureByIntrLanguageTest() {

		String signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
				"int","par1",false,	ExtLanguage.JAVA);
		assertEquals("int par1", signature);

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
				"int","par1",false,	ExtLanguage.SIMPLE);
		assertEquals("Number par1", signature);

		// with expected flag

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
				"int","par1",true,	ExtLanguage.JAVA);
		assertEquals("[e]int par1", signature);

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
				"int","par1",true,	ExtLanguage.SIMPLE);
		assertEquals("[e]Number par1", signature);

		// without parameter name

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
				"int",null,false,	ExtLanguage.JAVA);
		assertEquals("int", signature);

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
				"int",null,false,	ExtLanguage.SIMPLE);
		assertEquals("Number", signature);

		// expected without parameter

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
				"int",null,true,	ExtLanguage.JAVA);
		assertEquals("[e]int", signature);

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
				"int",null,true,	ExtLanguage.SIMPLE);
		assertEquals("[e]Number", signature);

	}

	@Test
	public void createSignatureByExtLanguageTest() {

		String signature = AbstractParameterNodeHelper.createSignature(
				"Number","par1",false);
		assertEquals("Number par1", signature);

		signature = AbstractParameterNodeHelper.createSignature(
				"Number","par1",true);
		assertEquals("[e]Number par1", signature);
	}

	@Test
	public void createParameterLabelsTest() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode(
						"name", null,	"int", "0", true);

		String label = MethodParameterNodeHelper.createSignature(methodParameterNode, ExtLanguage.JAVA);
		assertEquals("[e]int name", label);

		label = MethodParameterNodeHelper.createSignature(methodParameterNode, ExtLanguage.SIMPLE);
		assertEquals("[e]Number name", label);
	}

	@Test
	public void createTypeLabelsTest() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode(
						"name", null,	"int", "0", true);

		String label = AbstractParameterNodeHelper.createTypeSignature(methodParameterNode, ExtLanguage.JAVA);
		assertEquals("int", label);

		label = AbstractParameterNodeHelper.createTypeSignature(methodParameterNode, ExtLanguage.SIMPLE);
		assertEquals("Number", label);
	}

}
