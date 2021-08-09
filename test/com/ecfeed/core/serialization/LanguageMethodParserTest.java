/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization;

import org.junit.Test;

import com.ecfeed.core.model.serialization.LanguageMethodParser;

public class LanguageMethodParserTest {

	@Test
	public  void test1() {

		String methodXml = ""; // TODO
		parseSignature("void test();",  methodXml);
	}

	private void parseSignature(String signature, String methodXml) {
		//		MethodNode methodNode = 
		LanguageMethodParser.parseJavaMethodSignature(signature);

		// TODO compare with method node created from methodXml

	}

}
