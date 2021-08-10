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

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.junit.Test;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelComparator;
import com.ecfeed.core.model.serialization.IModelParserForMethod;
import com.ecfeed.core.model.serialization.LanguageMethodParser;
import com.ecfeed.core.model.serialization.ModelParserHelper;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

public class LanguageMethodParserTest {

	@Test
	public void shouldParseMethodWithoutParameters() {

		String methodXml = 
				"<Method name='test'>\n" + 
				"</Method>";
		
		try {
			parseSignature("void test();",  methodXml);
		} catch (Exception e) {
			fail(e.getMessage()); 
		}
	}
	
	@Test
	public void shouldFailWhenNoStartBrace() {

		String methodXml = 
				"<Method name='test'>\n" + 
				"</Method>";
		
		try {
			parseSignature("void test);",  methodXml);
			fail();
		} catch (Exception e) {
		}
	}	

	@Test
	public void shouldFailWhenNoEndBrace() {

		String methodXml = 
				"<Method name='test'>\n" + 
				"</Method>";
		
		try {
			parseSignature("void test(;",  methodXml);
			fail();
		} catch (Exception e) {
		}
	}	

	private void parseSignature(String signature, String methodXml) {
		
		MethodNode methodNodeFromSignature = LanguageMethodParser.parseJavaMethodSignature(signature);

		MethodNode methodNodeFromXml = parseXml(methodXml);
		
		ModelComparator.compareMethods(methodNodeFromSignature, methodNodeFromXml);
	}

	private MethodNode parseXml(String methodXml) {
		
		InputStream istream = new ByteArrayInputStream(methodXml.getBytes());
		Builder builder = new Builder();

		Document document = null;

		try {
			document = builder.build(istream);
		} catch (ParsingException | IOException e1) {
			ExceptionHelper.reportRuntimeException(e1.getMessage());;
		}

		Element element = document.getRootElement();

		IModelParserForMethod modelParserForMethod = 
				ModelParserHelper.createStandardModelParserForMethod();

		ListOfStrings errorList = new ListOfStrings();

		ClassNode classNode = new ClassNode("Class1", null);
		Optional<MethodNode> optMethodNodeFromXml = Optional.empty();

		try {
			optMethodNodeFromXml = 
					modelParserForMethod.parseMethod(
							element, classNode, errorList);
		} catch (ParserException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}

		if (!optMethodNodeFromXml.isPresent()) {
			ExceptionHelper.reportRuntimeException("Failed to convert method from xml.");
		}
		return optMethodNodeFromXml.get();
	}

}
