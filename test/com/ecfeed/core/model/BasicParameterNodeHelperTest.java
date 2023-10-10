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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class BasicParameterNodeHelperTest {

	@Test
	public void getQualifiedNameTest(){

		RootNode rootNode = new RootNode("root", null);

		BasicParameterNode globalParameterNode = new BasicParameterNode("global_1", "String", "0", false, null);
		globalParameterNode.setParent(rootNode);

		String qualifiedName = AbstractParameterSignatureHelper.createPathToTopContainerNewStandard(
				globalParameterNode, new ExtLanguageManagerForJava());

		assertEquals("global_1", qualifiedName);

		//		qualifiedName = AbstractParameterSignatureHelper.createPathToTopContainerNewStandard(
		//				globalParameterNode, new ExtLanguageManagerForSimple());
		//
		//		assertEquals("global 1", qualifiedName);


		String type = 
				AbstractParameterSignatureHelper.createSignatureOfParameterTypeNewStandard(
						globalParameterNode, new ExtLanguageManagerForJava());

		assertEquals("String", type);

		//		qualifiedName = 
		//				AbstractParameterSignatureHelper.createSignatureOfParameterTypeNewStandard(
		//						globalParameterNode, new ExtLanguageManagerForSimple());
		//
		//		assertEquals("Text", qualifiedName);
	}

}
