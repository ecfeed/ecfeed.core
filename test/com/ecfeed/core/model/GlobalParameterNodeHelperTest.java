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

import static org.junit.Assert.assertEquals;

public class GlobalParameterNodeHelperTest {

	@Test
	public void getQualifiedNameTest(){

		RootNode rootNode = new RootNode("root", null);

		GlobalParameterNode globalParameterNode = new GlobalParameterNode("global_1", null, "String");
		globalParameterNode.setParent(rootNode);

		String qualifiedName = GlobalParameterNodeHelper.getQualifiedName(globalParameterNode, ExtLanguage.JAVA);
		assertEquals("global_1", qualifiedName);

		qualifiedName = GlobalParameterNodeHelper.getQualifiedName(globalParameterNode, ExtLanguage.SIMPLE);
		assertEquals("global 1", qualifiedName);


		String type = GlobalParameterNodeHelper.getType(globalParameterNode, ExtLanguage.JAVA);
		assertEquals("String", type);

		qualifiedName = GlobalParameterNodeHelper.getType(globalParameterNode, ExtLanguage.SIMPLE);
		assertEquals("Text", qualifiedName);
	}

	@Test
	public void createSignatureTest(){

		RootNode rootNode = new RootNode("root", null);

		GlobalParameterNode globalParameterNode = new GlobalParameterNode("global_1", null, "String");
		globalParameterNode.setParent(rootNode);

		String signature = GlobalParameterNodeHelper.createSignature(globalParameterNode, ExtLanguage.JAVA);
		assertEquals("String global_1", signature);

		signature = GlobalParameterNodeHelper.createSignature(globalParameterNode, ExtLanguage.SIMPLE);
		assertEquals("Text global 1", signature);
	}

}
