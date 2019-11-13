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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelTestHelper;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.serialization.ModelPartialSerializer;
import com.ecfeed.core.utils.XmlComparator;


public class ModelPartialSerializerTest {

	@Test
	public void shouldSerializePartially1() throws Exception {

		String xml = prepareSourceXml1();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);
		MethodParameterNode methodParameterNode = methodNode.getMethodParameter(0);
		ChoiceNode choiceNode = methodParameterNode.getChoices().get(0);

		List<ChoiceNode> allowedChoices = new ArrayList<ChoiceNode>();
		allowedChoices.add(choiceNode);
		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(methodNode, domain, null, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml1();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml1() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='localChoice1' value='1' isRandomized='false'/>");
		sb.append("                <Choice name='localChoice2' value='2' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <TestCase testSuite='default suite'>");
		sb.append("                <TestParameter choice='localChoice1'/>");
		sb.append("            </TestCase>");
		sb.append("        </Method>");
		sb.append("        <Method name='testMethod2'>");
		sb.append("            <Parameter name='arg1' type='byte' isExpected='false' expected='0' linked='false'>");
		sb.append("            </Parameter>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("    <Class name='com.example.test.TestClass2'>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml1() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='localChoice1' value='1' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	@Test
	public void shouldSerializePartially2() throws Exception {

		String xml = prepareSourceXml2();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(1);
		MethodNode methodNode = classNode.getMethods().get(1);
		MethodParameterNode methodParameterNode1 = methodNode.getMethodParameter(0);
		ChoiceNode choiceNode1 = methodParameterNode1.getChoices().get(1);
		MethodParameterNode methodParameterNode2 = methodNode.getMethodParameter(1);
		ChoiceNode choiceNode2 = methodParameterNode2.getChoices().get(1);

		List<ChoiceNode> allowedChoices = new ArrayList<ChoiceNode>();
		allowedChoices.add(choiceNode1);
		allowedChoices.add(choiceNode2);
		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(methodNode, domain, null, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml2();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml2() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice1' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("    <Class name='com.example.test.TestClass2'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='byte' isExpected='false' expected='0' linked='false'>");
		sb.append("            </Parameter>");
		sb.append("        </Method>");
		sb.append("        <Method name='testMethod2'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice1' value='1' isRandomized='false'/>");		
		sb.append("                <Choice name='choice2' value='2' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='localChoice1' value='1' isRandomized='false'/>");		
		sb.append("                <Choice name='localChoice2' value='2' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml2() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass2'>");
		sb.append("        <Method name='testMethod2'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice2' value='2' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='localChoice2' value='2' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	@Test
	public void shouldSeritalizePartially3() throws Exception {

		String xml = prepareSourceXml3();

		RootNode rootNode = ModelTestHelper.createModel(xml);

		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);
		MethodParameterNode methodParameterNode1 = methodNode.getMethodParameter(0);

		List<ChoiceNode> choicesFromSource = methodParameterNode1.getChoices();
		ChoiceNode choiceNode2 = choicesFromSource.get(1); 
		ChoiceNode choiceNode3 = choicesFromSource.get(2);

		List<ChoiceNode> allowedChoices = new ArrayList<ChoiceNode>();
		allowedChoices.add(choiceNode2);
		allowedChoices.add(choiceNode3);
		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(methodNode, domain, null, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml3();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml3() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg1'/>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("    <Parameter name='arg1' type='int'>");
		sb.append("        <Choice name='choice1' value='1' isRandomized='false'/>");
		sb.append("        <Choice name='choice2' value='2' isRandomized='false'/>");
		sb.append("        <Choice name='choice3' value='3' isRandomized='false'/>");
		sb.append("    </Parameter>");
		sb.append("    <Parameter name='arg2' type='int'>");
		sb.append("        <Choice name='choice21' value='0' isRandomized='false'/>");
		sb.append("    </Parameter>");
		sb.append("</Model>");		

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml3() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg1'/>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("    <Parameter name='arg1' type='int'>");
		sb.append("        <Choice name='choice2' value='2' isRandomized='false'/>");
		sb.append("        <Choice name='choice3' value='3' isRandomized='false'/>");
		sb.append("    </Parameter>");
		sb.append("</Model>");		

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	@Test
	public void shouldSeritalizePartially4() throws Exception {

		String xml = prepareSourceXml4();

		RootNode rootNode = ModelTestHelper.createModel(xml);

		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		MethodParameterNode methodParameterNode1 = methodNode.getMethodParameter(0);
		ChoiceNode choiceNode1 = methodParameterNode1.getChoices().get(0);

		MethodParameterNode methodParameterNode2 = methodNode.getMethodParameter(1);
		ChoiceNode choiceNode2 = methodParameterNode2.getChoices().get(0);

		List<ChoiceNode> allowedChoices = new ArrayList<ChoiceNode>();
		allowedChoices.add(choiceNode1);
		allowedChoices.add(choiceNode2);
		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(methodNode, domain, null, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml4();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml4() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg01'/>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='com.example.test.TestClass1:arg11'/>");
		sb.append("        </Method>");
		sb.append("        <Parameter name='arg11' type='int'>");
		sb.append("            <Choice name='choice111' value='0' isRandomized='false'/>");
		sb.append("            <Choice name='choice112' value='0' isRandomized='false'/>");
		sb.append("        </Parameter>");
		sb.append("    </Class>");
		sb.append("    <Class name='com.example.test.TestClass2'>");
		sb.append("        <Parameter name='arg21' type='int'>");
		sb.append("            <Choice name='choice211' value='0' isRandomized='false'/>");
		sb.append("            <Choice name='choice212' value='0' isRandomized='false'/>");
		sb.append("        </Parameter>");
		sb.append("    </Class>");
		sb.append("    <Parameter name='arg01' type='int'>");
		sb.append("        <Choice name='choice1' value='0' isRandomized='false'/>");
		sb.append("        <Choice name='choice2' value='0' isRandomized='false'/>");
		sb.append("    </Parameter>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml4() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='arg01'/>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='true' link='com.example.test.TestClass1:arg11'/>");
		sb.append("        </Method>");
		sb.append("        <Parameter name='arg11' type='int'>");
		sb.append("            <Choice name='choice111' value='0' isRandomized='false'/>");
		sb.append("        </Parameter>");
		sb.append("    </Class>");
		sb.append("    <Parameter name='arg01' type='int'>");
		sb.append("        <Choice name='choice1' value='0' isRandomized='false'/>");
		sb.append("    </Parameter>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}


	@Test
	public void shouldSeritalizePartially5() throws Exception {

		String xml = prepareSourceXml5();

		RootNode rootNode = ModelTestHelper.createModel(xml);

		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		List<ChoiceNode> allowedChoices = new ArrayList<ChoiceNode>();
		List<IConstraint<ChoiceNode>> allowedConstraints = new ArrayList<IConstraint<ChoiceNode>>();
		List<ConstraintNode> constraints = methodNode.getConstraintNodes();

		for (ConstraintNode constraintNode : constraints) {
			allowedConstraints.add(constraintNode.getConstraint());
			allowedChoices.addAll(constraintNode.getListOfChoices());
		}

		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(
				methodNode, domain, allowedConstraints, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml5();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml5() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice1' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <ImplicationConstraint name='constraint'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='choice1' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml5() {

		return prepareSourceXml5();
	}

	@Test
	public void shouldSeritalizePartially6() throws Exception {

		String xml = prepareSourceXml6();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(0);
		List<ChoiceNode> allowedChoices = constraintNode.getListOfChoices();
		allowedChoices.add(methodNode.getParameter(1).getChoices().get(0));

		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		List<IConstraint<ChoiceNode>> allowedConstraints = new ArrayList<IConstraint<ChoiceNode>>();
		allowedConstraints.add(constraintNode.getConstraint());

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(
				methodNode, domain, allowedConstraints, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml6();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml6() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice11' value='0' isRandomized='false'/>");
		sb.append("                <Choice name='choice12' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice21' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <ImplicationConstraint name='constraint1'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='choice11' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("            <ImplicationConstraint name='constraint2'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='choice12' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml6() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice11' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice21' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <ImplicationConstraint name='constraint1'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='choice11' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;

	}

	@Test
	public void shouldSeritalizePartially7() throws Exception {

		String xml = prepareSourceXml7();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(1);
		List<ChoiceNode> allowedChoices = constraintNode.getListOfChoices();
		allowedChoices.add(methodNode.getParameter(1).getChoices().get(0));

		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		List<IConstraint<ChoiceNode>> allowedConstraints = new ArrayList<IConstraint<ChoiceNode>>();
		allowedConstraints.add(constraintNode.getConstraint());

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(
				methodNode, domain, allowedConstraints, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml7();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml7() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice11' value='0' isRandomized='false'/>");
		sb.append("                <Choice name='choice12' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice21' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <ImplicationConstraint name='constraint1'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='choice11' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("            <ImplicationConstraint name='constraint2'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='choice12' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml7() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice12' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice21' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <ImplicationConstraint name='constraint2'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='choice12' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}


	@Test
	public void shouldSeritalizePartially8() throws Exception {

		String xml = prepareSourceXml8();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(0);
		assertEquals(constraintNode.getListOfChoices().size(), 0);		

		List<IConstraint<ChoiceNode>> allowedConstraints = new ArrayList<IConstraint<ChoiceNode>>();
		allowedConstraints.add(constraintNode.getConstraint());

		List<ChoiceNode> allowedChoices = new ArrayList<ChoiceNode>();
		allowedChoices.add(methodNode.getParameter(0).getChoices().get(0));
		allowedChoices.add(methodNode.getParameter(1).getChoices().get(0));
		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(
				methodNode, domain, allowedConstraints, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml8();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml8() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice11' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <Parameter name='arg2' type='int' isExpected='false' expected='0' linked='false'>");
		sb.append("                <Choice name='choice21' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <ImplicationConstraint name='constraint1'>");
		sb.append("                <Premise>");
		sb.append("                    <ParameterStatement rightParameter='arg2' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml8() {

		return prepareSourceXml8();
	}

	@Test
	public void shouldSeritalizePartially9() throws Exception {

		String xml = prepareSourceXml9();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);

		List<ChoiceNode> allowedChoices = new ArrayList<ChoiceNode>();
		ChoiceNode globalChoice1 = methodNode.getParameter(0).getChoices().get(0);
		ChoiceNode choice1 = globalChoice1.getChoices().get(0);
		allowedChoices.add(choice1);

		ChoiceNode globalChoice2 = methodNode.getParameter(0).getChoices().get(1);
		ChoiceNode choice2 = globalChoice2.getChoices().get(1);
		allowedChoices.add(choice2);

		List<List<ChoiceNode>> domain = new ArrayList<List<ChoiceNode>>();
		domain.add(allowedChoices);

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(
				methodNode, domain, null, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml9();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml9() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='globalArg1'>");
		sb.append("                <Choice name='choice11' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("    <Parameter name='globalArg1' type='int'>");
		sb.append("        <Choice name='global1' value='0' isRandomized='false'>");
		sb.append("            <Choice name='global11' value='0' isRandomized='false'/>");
		sb.append("            <Choice name='global12' value='0' isRandomized='false'/>");
		sb.append("        </Choice>");
		sb.append("        <Choice name='global2' value='0' isRandomized='false'>");
		sb.append("            <Choice name='global21' value='0' isRandomized='false'/>");
		sb.append("            <Choice name='global22' value='0' isRandomized='false'/>");
		sb.append("        </Choice>");
		sb.append("    </Parameter>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml9() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='globalArg1'/>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("    <Parameter name='globalArg1' type='int'>");
		sb.append("        <Choice name='global1' value='0' isRandomized='false'>");
		sb.append("            <Choice name='global11' value='0' isRandomized='false'/>");
		sb.append("        </Choice>");
		sb.append("        <Choice name='global2' value='0' isRandomized='false'>");
		sb.append("            <Choice name='global22' value='0' isRandomized='false'/>");
		sb.append("        </Choice>");
		sb.append("    </Parameter>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	@Test
	public void shouldSeritalizePartially10() throws Exception {

		String xml = prepareSourceXml10();

		RootNode rootNode = ModelTestHelper.createModel(xml);
		ClassNode classNode = rootNode.getClasses().get(0);
		MethodNode methodNode = classNode.getMethods().get(0);
		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(0);

		List<IConstraint<ChoiceNode>> allowedConstraints = new ArrayList<IConstraint<ChoiceNode>>();
		allowedConstraints.add(constraintNode.getConstraint());

		OutputStream outputStream = new ByteArrayOutputStream();

		ModelPartialSerializer modelPartialSerializer = 
				new ModelPartialSerializer(
						outputStream, 
						ModelVersionDistributor.getCurrentSoftwareVersion());

		modelPartialSerializer.serializeModelPartForGenerator(
				methodNode, null, allowedConstraints, false, false);

		String resultXml = outputStream.toString();
		String expectedResultXml = prepareResultXml10();

		assertTrue(XmlComparator.areXmlsEqual(expectedResultXml, resultXml));
	}

	private String prepareSourceXml10() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='globalArg1'>");
		sb.append("                <Choice name='choice11' value='0' isRandomized='false'/>");
		sb.append("            </Parameter>");
		sb.append("            <ImplicationConstraint name='constraint'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='globalc1:global11' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("    <Parameter name='globalArg1' type='int'>");
		sb.append("        <Choice name='globalc1' value='0' isRandomized='false'>");
		sb.append("            <Choice name='global11' value='0' isRandomized='false'/>");
		sb.append("        </Choice>");
		sb.append("    </Parameter>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;
	}

	private String prepareResultXml10() {

		StringBuilder sb = new StringBuilder(); 

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<Model name='PartialSerializerTest' version='3'>");
		sb.append("    <Class name='com.example.test.TestClass1'>");
		sb.append("        <Method name='testMethod1'>");
		sb.append("            <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='true' link='globalArg1'/>");
		sb.append("            <ImplicationConstraint name='constraint'>");
		sb.append("                <Premise>");
		sb.append("                    <Statement choice='globalc1:global11' parameter='arg1' relation='equal'/>");
		sb.append("                </Premise>");
		sb.append("                <Consequence>");
		sb.append("                    <StaticStatement value='true'/>");
		sb.append("                </Consequence>");
		sb.append("            </ImplicationConstraint>");
		sb.append("        </Method>");
		sb.append("    </Class>");
		sb.append("    <Parameter name='globalArg1' type='int'>");
		sb.append("        <Choice name='globalc1' value='0' isRandomized='false'>");
		sb.append("            <Choice name='global11' value='0' isRandomized='false'/>");
		sb.append("        </Choice>");
		sb.append("    </Parameter>");
		sb.append("</Model>");

		String xml = sb.toString();
		xml = xml.replace("'", "\"");
		return xml;

	}

}
