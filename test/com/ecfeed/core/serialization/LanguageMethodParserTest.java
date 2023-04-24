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
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.serialization.IModelParserForMethod;
import com.ecfeed.core.model.serialization.LanguageMethodParser;
import com.ecfeed.core.model.serialization.ModelParserHelper;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.TestHelper;

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
			parseSignature("void test();",  LanguageMethodParser.Language.JAVA, methodXml);
		} catch (Exception e) {
			fail(e.getMessage()); 
		}
		
		try {
			parseSignature("void test();",  LanguageMethodParser.Language.CPP, methodXml);
		} catch (Exception e) {
			fail(e.getMessage()); 
		}
		
	}
	
	@Test
	public void shouldParseMethodWithoutReturnType() {

		String methodXml = 
				"<Method name='test'>\n" + 
				"</Method>";
		
		try {
			parseSignature("test();",  LanguageMethodParser.Language.CPP, methodXml);
		} catch (Exception e) {
			fail();
		}
	}	

	@Test
	public void shouldFailWhenNoStartBrace() {

		String methodXml = 
				"<Method name='test'>\n" + 
				"</Method>";
		
		try {
			parseSignature("void test);",  LanguageMethodParser.Language.JAVA, methodXml);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, LanguageMethodParser.STARTING_BRACKET_NOT_FOUND);
		}
	}	

	@Test
	public void shouldFailWhenNoEndBrace() {

		String methodXml = 
				"<Method name='test'>\n" + 
				"</Method>";
		
		try {
			parseSignature("void test(;",  LanguageMethodParser.Language.JAVA, methodXml);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, LanguageMethodParser.ENDING_BRACKET_NOT_FOUND);
		}
	}	

	@Test
	public void shouldFailWhenInvalidCharsInName() {

		String methodXml = 
				"<Method name='test'>\n" + 
				"</Method>";
		
		try {
			parseSignature("void te%st();",  LanguageMethodParser.Language.JAVA, methodXml);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, LanguageMethodParser.NOT_A_VALID_JAVA_IDENTIFIER);
		}
	}	
	
	@Test
	public void AAshouldParseMethodWithOneParameter() {
		
		String methodXml = 
				"<Method name='test'>\n" + 
				"	<Properties>\n" + 
				"		<Property name='methodRunner' type='String' value='Java Runner'/>\n" + 
				"		<Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" + 
				"		<Property name='wbBrowser' type='String' value='Chrome'/>\n" + 
				"		<Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" + 
				"	</Properties>\n" + 
				"	<Parameter name='par0' type='int' isExpected='false' expected='0' linked='false'>\n" + 
				"		<Properties>\n" + 
				"			<Property name='wbIsOptional' type='boolean' value='false'/>\n" + 
				"		</Properties>\n" + 
				"		<Comments>\n" + 
				"			<TypeComments/>\n" + 
				"		</Comments>\n" + 
				"	</Parameter>\n" + 
				"</Method>";
		
		methodXml.replace("\"", "'");
		
		try {
			parseSignature("void test(int par0);",  LanguageMethodParser.Language.JAVA, methodXml);
		} catch (Exception e) {
			fail(e.getMessage()); 
		}
		
		try {
			parseSignature("void test(unsigned int* par0);",  LanguageMethodParser.Language.CPP, methodXml);
		} catch (Exception e) {
			fail(e.getMessage()); 
		}

		try {
			parseSignature("int test(uint par0);",  LanguageMethodParser.Language.CSHARP, methodXml);
		} catch (Exception e) {
			fail(e.getMessage()); 
		}
	}

	@Test
	public void shouldParseMethodWithOneParameterInPython() {
		
		String methodXml = 
				"<Method name='test'>\n" + 
				"	<Parameter name='par0' type='String' isExpected='false' expected='0' linked='false' />\n" + 
				"</Method>";
		
		methodXml.replace("\"", "'");
		
		try {
			parseSignature("def test(par0);",  LanguageMethodParser.Language.PYTHON, methodXml);
		} catch (Exception e) {
			fail(e.getMessage()); 
		}		
	}
	
	@Test
	public void shouldParseMethodWithThreeParameters() {
		
		String methodXml = createXmlForMethodWithThreeParameters();
		
		methodXml.replace("\"", "'");
		
		try {
			parseSignature(
					"void test(int par0, float par1, String par2);",  
					LanguageMethodParser.Language.JAVA, 
					methodXml);
		} catch (Exception e) {
			fail(e.getMessage()); 
		}
	}

	@Test
	public void shouldFailWhenMissingParameter() {
		
		String methodXml = createXmlForMethodWithThreeParameters();
		
		methodXml.replace("\"", "'");
		
		try {
			parseSignature(
					"void test(int par0,, float par1, String par2);",  
					LanguageMethodParser.Language.JAVA, 
					methodXml);
			fail();			
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, LanguageMethodParser.MISSING_PARAMETER);
		}
	}

	@Test
	public void shoulIgnoreTwoOpeningBrackets() {
		
		String methodXml = createXmlForMethodWithThreeParameters();
		
		methodXml.replace("\"", "'");
		
		try {
			parseSignature("void test((int par0, float par1, String par2);",  LanguageMethodParser.Language.JAVA, methodXml);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void shoulIgnoreTwoClosingBrackets() {
		
		String methodXml = createXmlForMethodWithThreeParameters();
		
		methodXml.replace("\"", "'");
		
		try {
			parseSignature("void test(int par0, float par1, String par2));",  LanguageMethodParser.Language.JAVA, methodXml);
		} catch (Exception e) {
			fail();
		}
	}
	
	private void parseSignature(String signature, LanguageMethodParser.Language language, String methodXml) {
		
		MethodNode methodNodeFromSignature = LanguageMethodParser.parseJavaMethodSignature(signature, language);

		MethodNode methodNodeFromXml = parseXml(methodXml);
		
		MethodNodeHelper.compareMethods(methodNodeFromSignature, methodNodeFromXml);
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

	private String createXmlForMethodWithThreeParameters() {
		String methodXml = 
				"<Method name='test'>\n" + 
				"	<Parameter name='par0' type='int' isExpected='false' expected='0' linked='false' />\n" + 
				"	<Parameter name='par1' type='float' isExpected='false' expected='0' linked='false' />\n" + 
				"	<Parameter name='par2' type='String' isExpected='false' expected='0' linked='false' />\n" + 
				"</Method>";
		return methodXml;
	}

}
