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
import java.util.Set;

import static org.junit.Assert.*;

public class AbstractParameterNodeHelperTest {

	@Test
	public void createSignatureTest() {

		String signature = AbstractParameterNodeHelper.createSignatureOfOneParameter(
				"int","par1",false,	ExtLanguage.JAVA);
		assertEquals("int par1", signature);

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameter(
				"int","par1",false,	ExtLanguage.SIMPLE);
		assertEquals("Number par1", signature);

		// with expected flag

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameter(
				"int","par1",true,	ExtLanguage.JAVA);
		assertEquals("[e]int par1", signature);

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameter(
				"int","par1",true,	ExtLanguage.SIMPLE);
		assertEquals("[e]Number par1", signature);

		// without parameter name

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameter(
				"int",null,false,	ExtLanguage.JAVA);
		assertEquals("int", signature);

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameter(
				"int",null,false,	ExtLanguage.SIMPLE);
		assertEquals("Number", signature);

		// expected without parameter

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameter(
				"int",null,true,	ExtLanguage.JAVA);
		assertEquals("[e]int", signature);

		signature = AbstractParameterNodeHelper.createSignatureOfOneParameter(
				"int",null,true,	ExtLanguage.SIMPLE);
		assertEquals("[e]Number", signature);

	}

}
