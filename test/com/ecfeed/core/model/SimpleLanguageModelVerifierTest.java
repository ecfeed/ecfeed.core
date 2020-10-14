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

import com.ecfeed.core.testutils.RandomModelGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleLanguageModelVerifierTest {

	@Test
	public void classDuplicatedTest() {

		RootNode rootNode = new RootNode("rootNode", null);
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		ClassNode classNode1 = new ClassNode("com.Class_1", null);
		rootNode.addClass(classNode1);
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		ClassNode classNode2 = new ClassNode("com.xx.Class_1", null);
		rootNode.addClass(classNode2);
		assertNotNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		classNode2.setName("Class_1");
		assertNotNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		classNode2.setName("com.xx.Class_2");
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));
	}

	@Test
	public void methodDuplicatedTest() {

		RootNode rootNode = new RootNode("rootNode", null);
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		ClassNode classNode = new ClassNode("com.Class_1", null);
		rootNode.addClass(classNode);
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		MethodNode methodNode1 = new MethodNode("Method_1", null);
		classNode.addMethod(methodNode1);
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		MethodParameterNode methodParameterNode11 =
				new MethodParameterNode("Param1", null, "int", "0", false);
		methodNode1.addParameter(methodParameterNode11);

		MethodNode methodNode2 = new MethodNode("Method_1", null);
		classNode.addMethod(methodNode2);
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		MethodParameterNode methodParameterNode21 =
				new MethodParameterNode("Param1", null, "int", "0", false);
		methodNode2.addParameter(methodParameterNode21);
		assertNotNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		methodParameterNode21.setType("byte");
		assertNotNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		methodParameterNode21.setType("String");
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		methodParameterNode21.setType("char");
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		methodParameterNode21.setType("boolean");
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		methodParameterNode21.setType("long");
		assertNotNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		methodParameterNode21.setType("double");
		assertNotNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));
	}

}
