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

import static com.ecfeed.core.testutils.TestUtilConstants.SUPPORTED_TYPES;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Random;

import org.junit.Test;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.serialization.IModelParserForChoice;
import com.ecfeed.core.model.serialization.IModelParserForConstraint;
import com.ecfeed.core.model.serialization.IModelParserForGlobalParameter;
import com.ecfeed.core.model.serialization.IModelParserForMethod;
import com.ecfeed.core.model.serialization.IModelParserForMethodParameter;
import com.ecfeed.core.model.serialization.IModelParserForTestCase;
import com.ecfeed.core.model.serialization.ModelParserForChoice;
import com.ecfeed.core.model.serialization.ModelParserForClass;
import com.ecfeed.core.model.serialization.ModelParserForConstraint;
import com.ecfeed.core.model.serialization.ModelParserForGlobalParameter;
import com.ecfeed.core.model.serialization.ModelParserForMethod;
import com.ecfeed.core.model.serialization.ModelParserForMethodParameter;
import com.ecfeed.core.model.serialization.ModelParserForTestCase;
import com.ecfeed.core.model.serialization.SerializationConstants;
import com.ecfeed.core.model.serialization.SerializationHelperVersion1;
import com.ecfeed.core.model.serialization.XomAnalyser;
import com.ecfeed.core.model.serialization.XomAnalyserFactory;
import com.ecfeed.core.model.serialization.XomBuilder;
import com.ecfeed.core.model.serialization.XomBuilderFactory;
import com.ecfeed.core.model.serialization.XomStatementBuilder;
import com.ecfeed.core.testutils.ModelStringifier;
import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

public class XomParserTest {

	private final boolean DEBUG = true;

	RandomModelGenerator fModelGenerator = new RandomModelGenerator();
	ModelStringifier fStringifier = new ModelStringifier();
	Random fRandom = new Random();

	@Test
	public void parseRootTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseRootTest(version);
		}
	}

	private void parseRootTest(int version) {
		try {
			RootNode rootNode = fModelGenerator.generateModel(3);

			XomBuilder builder = XomBuilderFactory.createXomBuilder(version, null);
			Element rootElement = (Element)rootNode.accept(builder);
			TRACE(rootElement);

			XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
			RootNode parsedRootNode = analyser.parseRoot(rootElement, null, new ListOfStrings());
			assertElementsEqual(rootNode, parsedRootNode);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseClassTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseClassTest(version);
		}
	}

	private void parseClassTest(int version){
		try {
			ClassNode classNode = fModelGenerator.generateClass(3);

			XomBuilder builder = XomBuilderFactory.createXomBuilder(version, null);
			Element element = (Element)classNode.accept(builder);
			TRACE(element);

			RootNode tmpRoot = new RootNode("tmp", null);
			
			// TODO PARSER
			
			IModelParserForChoice modelParserForChoice = new ModelParserForChoice(null);
			
			IModelParserForMethodParameter modelParserForMethodParameter = new ModelParserForMethodParameter();
			
			IModelParserForTestCase modelParserForTestCase = new ModelParserForTestCase();
			
			IModelParserForConstraint modelParserForConstraint = new ModelParserForConstraint();
			
			IModelParserForMethod modelParserForMethod = 
					new ModelParserForMethod(modelParserForMethodParameter, modelParserForTestCase, modelParserForConstraint);
			
			IModelParserForGlobalParameter modelParserForGlobalParameter = 
					new ModelParserForGlobalParameter(modelParserForChoice);
			
			ModelParserForClass modelParserForClass = 
					new ModelParserForClass(
							modelParserForGlobalParameter, modelParserForMethod);
			
			Optional<ClassNode> parsedClass = 
					modelParserForClass.parseClass(element, tmpRoot, new ListOfStrings());
			
			assertElementsEqual(classNode, parsedClass.get());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseMethodTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseMethodTest(version);
		}
	}

	private void parseMethodTest(int version){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode methodNode = fModelGenerator.generateMethod(5, 5, 5);

				XomBuilder builder = XomBuilderFactory.createXomBuilder(version, null);
				Element element = (Element)methodNode.accept(builder);
				TRACE(element);

				ClassNode tmpClassNode = new ClassNode("tmp", null);
				
				// TODO PARSER
				
				IModelParserForMethodParameter modelParserForMethodParameter = new ModelParserForMethodParameter();
				
				IModelParserForTestCase modelParserForTestCase = new ModelParserForTestCase();
				
				IModelParserForConstraint modelParserForConstraint = new ModelParserForConstraint();
				
				IModelParserForMethod modelParserForMethod = 
						new ModelParserForMethod(modelParserForMethodParameter, modelParserForTestCase, modelParserForConstraint);
				
				Optional<MethodNode> parsedMethodNode = modelParserForMethod.parseMethod(element, tmpClassNode, new ListOfStrings());
				assertElementsEqual(methodNode, parsedMethodNode.get());
			}
			catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseParameterTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseParameterTest(version);
		}
	}

	private void parseParameterTest(int version){
		for(String type : SUPPORTED_TYPES){
			try{
				for(boolean expected : new Boolean[]{true, false}){
					MethodNode methodNode = new MethodNode("method", null);
					MethodParameterNode methodParameterNode = fModelGenerator.generateParameter(type, expected, 3, 3, 3);
					methodNode.addParameter(methodParameterNode);

					XomBuilder builder = XomBuilderFactory.createXomBuilder(version, null);
					Element element = (Element)methodParameterNode.accept(builder);
					TRACE(element);

					Optional<MethodParameterNode> parsedMethodParameterNode = 
							new ModelParserForMethodParameter().parseMethodParameter(element, methodNode, new ListOfStrings());
					assertElementsEqual(methodParameterNode, parsedMethodParameterNode.get());
				}
			}
			catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseTestCaseTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseTestCaseTest(version);
		}
	}

	private void parseTestCaseTest(int version){
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
			for(int j = 0; j < 100; j++){
				try {
					TestCaseNode testCaseNode = fModelGenerator.generateTestCase(m);
					XomBuilder builder = XomBuilderFactory.createXomBuilder(version, null);
					Element element = (Element)testCaseNode.accept(builder);
					TRACE(element);

					ModelParserForTestCase modelParserForTestCase = new ModelParserForTestCase();
					Optional<TestCaseNode> tc1 = modelParserForTestCase.parseTestCase(element, m, new ListOfStrings());
					assertElementsEqual(testCaseNode, tc1.get());
				} catch (Exception e) {
					fail("Unexpected exception: " + e.getMessage());
				}
			}
		}
	}

	@Test
	public void parseConstraintTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseConstraintTest(version);
		}
	}

	private void parseConstraintTest(int version) {
		for(int i = 0; i < 10; i++){
			MethodNode m = fModelGenerator.generateMethod(3, 0, 0);
			for(int j = 0; j < 10; j++){
				try {
					ConstraintNode c = fModelGenerator.generateConstraint(m);

					XomBuilder builder = XomBuilderFactory.createXomBuilder(version, null);
					Element element = (Element)c.accept(builder);
					TRACE(element);

					Optional<ConstraintNode> c1 = new ModelParserForConstraint().parseConstraint(element, m, new ListOfStrings());
					assertElementsEqual(c, c1.get());
				} catch (Exception e) {
					fail("Unexpected exception: " + e.getMessage() + "\nMethod\n" + new ModelStringifier().stringify(m, 0));
				}
			}
		}
	}


	@Test
	public void parseChoiceTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseChoiceTest(version);
		}
	}

	private void parseChoiceTest(int version){
		for(String type: SUPPORTED_TYPES){
			try {
				ChoiceNode p = fModelGenerator.generateChoice(3, 3, 3, type);

				XomBuilder builder = XomBuilderFactory.createXomBuilder(version, null);
				Element element = (Element)p.accept(builder);
				TRACE(element);

				Optional<ChoiceNode> p1 = new ModelParserForChoice(null).parseChoice(element, new ListOfStrings());
				assertElementsEqual(p, p1.get());
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}


	@Test
	public void parseStaticStatementTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseStaticStatementTest(version);
		}
	}

	private void parseStaticStatementTest(int version) {
		StaticStatement trueStatement = new StaticStatement(true, null);
		StaticStatement falseStatement = new StaticStatement(false, null);
		try{
			XomStatementBuilder builder = 
					new XomStatementBuilder(
							getStatementParameterAttributeName(version), 
							getStatementChoiceAttributeName(version));

			Element trueElement = (Element)trueStatement.accept(builder);
			Element falseElement = (Element)falseStatement.accept(builder);
			TRACE(trueElement);
			TRACE(falseElement);

			StaticStatement parsedTrue = new ModelParserForConstraint().parseStaticStatement(trueElement, null, new ListOfStrings());
			StaticStatement parsedFalse = new ModelParserForConstraint().parseStaticStatement(falseElement, null, new ListOfStrings());

			assertStatementsEqual(trueStatement, parsedTrue);
			assertStatementsEqual(falseStatement, parsedFalse);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}


	@Test
	public void parseChoiceStatementTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseChoiceStatementTest(version);
		}
	}

	private void parseChoiceStatementTest(int version){
		for(int i = 0; i < 10; i++){
			try{
				MethodNode m = fModelGenerator.generateMethod(5, 0, 0);
				RelationStatement s = fModelGenerator.generateChoicesParentStatement(m);

				XomStatementBuilder builder = 
						new XomStatementBuilder(
								getStatementParameterAttributeName(version), 
								getStatementChoiceAttributeName(version));

				Element element = (Element)s.accept(builder);
				TRACE(element);

				AbstractStatement parsedS = null;
				switch(element.getLocalName()){
				case SerializationConstants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME:
					parsedS = new ModelParserForConstraint().parseLabelStatement(element, m, new ListOfStrings());
					break;
				case SerializationConstants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME:
					parsedS = new ModelParserForConstraint().parseChoiceStatement(element, m, new ListOfStrings());
					break;
				}

				assertStatementsEqual(s, parsedS);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseExpectedValueStatementTest() {

		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseExpectedValueStatementTest(version);
		}
	}	

	private String getStatementParameterAttributeName(int version) {

		return SerializationHelperVersion1.getStatementParameterAttributeName();
	}

	private String getStatementChoiceAttributeName(int version) {

		return SerializationHelperVersion1.getStatementChoiceAttributeName();
	}

	private void parseExpectedValueStatementTest(int version){

		for(int i = 0; i < 10; i++){
			try{
				MethodNode m = fModelGenerator.generateMethod(10, 0, 0);
				ExpectedValueStatement s = fModelGenerator.generateExpectedValueStatement(m);

				XomStatementBuilder builder = 
						new XomStatementBuilder(
								getStatementParameterAttributeName(version), 
								getStatementChoiceAttributeName(version));

				Element element = (Element)s.accept(builder);
				TRACE(element);

				ExpectedValueStatement parsedS = new ModelParserForConstraint().parseExpectedValueStatement(element, m, new ListOfStrings());
				assertStatementsEqual(s, parsedS);
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void parseStatementArrayTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			parseStatementArrayTest(version);
		}
	}	

	private void parseStatementArrayTest(int version) {
		try{
			MethodNode m = fModelGenerator.generateMethod(10, 0, 0);
			StatementArray s = fModelGenerator.generateStatementArray(m, 4);

			XomStatementBuilder builder = 
					new XomStatementBuilder(
							getStatementParameterAttributeName(version), 
							getStatementChoiceAttributeName(version));

			Element element = (Element)s.accept(builder);
			TRACE(element);

			StatementArray parsedS = new ModelParserForConstraint().parseStatementArray(element, m, new ListOfStrings());
			assertStatementsEqual(s, parsedS);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}

	}

	@Test
	public void assertTypeTest() {

		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			assertTypeTest(version);
		}
	}

	private void assertTypeTest(int version){
		try{

			RootNode root = fModelGenerator.generateModel(3);
			ClassNode _class = fModelGenerator.generateClass(3);

			XomBuilder builder = XomBuilderFactory.createXomBuilder(version, null);
			Element rootElement = (Element)root.accept(builder);
			Element classElement = (Element)_class.accept(builder);

			XomAnalyser analyser = XomAnalyserFactory.createXomAnalyser(version);
			RootNode rootNode = analyser.parseRoot(rootElement, null, new ListOfStrings());

			// TODO PARSER
			
			IModelParserForChoice modelParserForChoice = new ModelParserForChoice(null);
			
			IModelParserForMethodParameter modelParserForMethodParameter = new ModelParserForMethodParameter();
			IModelParserForTestCase modelParserForTestCase = new ModelParserForTestCase();
			
			IModelParserForConstraint modelParserForConstraint = new ModelParserForConstraint();
			
			IModelParserForMethod modelParserForMethod = 
					new ModelParserForMethod(modelParserForMethodParameter, modelParserForTestCase, modelParserForConstraint);
			
			IModelParserForGlobalParameter modelParserForGlobalParameter = 
					new ModelParserForGlobalParameter(modelParserForChoice);
			
			ModelParserForClass modelParserForClass = 
					new ModelParserForClass(
							modelParserForGlobalParameter, modelParserForMethod);
			try {
				modelParserForClass.parseClass(classElement, rootNode, new ListOfStrings());
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}

			try {
				ListOfStrings errorList = new ListOfStrings();
				modelParserForClass.parseClass(rootElement, rootNode, errorList);
				
				assertFalse(errorList.isEmpty());
			} catch (Exception e) {
			}

			try {
				ListOfStrings errorList = new ListOfStrings();
				analyser.parseRoot(classElement, null, errorList);
				assertFalse(errorList.isEmpty());
			} catch (Exception e) {
			}
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}

	}

	private void assertStatementsEqual(AbstractStatement s1, AbstractStatement s2) {
		if(s1.isEqualTo(s2) == false){
			fail("Parsed statement\n" + fStringifier.stringify(s1, 0) + "\ndiffers from original\n" + fStringifier.stringify(s2, 0));
		}

	}

	private void assertElementsEqual(AbstractNode n, AbstractNode n1) {
		if(n.isMatch(n1) == false){
			fail("Parsed element differs from original\n" + fStringifier.stringify(n, 0) + "\n" + fStringifier.stringify(n1, 0));
		}
	}

	private void TRACE(Element element){
		if (!DEBUG) {
			return;
		}

		Document document = new Document(element);
		OutputStream ostream = new ByteArrayOutputStream();
		Serializer serializer = new Serializer(ostream);
		// Uncomment for pretty formatting. This however will affect
		// whitespaces in the document's ... infoset
		serializer.setIndent(4);
		try {
			serializer.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(ostream);
	}

}
