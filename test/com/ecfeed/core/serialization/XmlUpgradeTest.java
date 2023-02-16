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

import com.ecfeed.core.model.*;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.utils.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class XmlUpgradeTest {

	@Test
	public void upgradeStandardConstraintFromVersion3To4Test() {

		Pair<String, String> pairOfXmls = createXmlsWithStandardConstraintInVersion3And4();

		upgradeFromVersion3To4(pairOfXmls.getFirst(), pairOfXmls.getSecond(), null);
	}

	@Test
	public void upgradeAssignmentConstraintFromVersion3To4Test() {

		Pair<String, String> pairOfXmls = createXmlsWithAssignmentConstraintInVersion3And4();

		String serializedModelInVersion4 = generateAssignmentConstraintExampleInVersion4();

		upgradeFromVersion3To4(pairOfXmls.getFirst(), pairOfXmls.getSecond(), serializedModelInVersion4);
	}

	@Test
	public void upgradeNonUniqueMethodsFromVersion4To5Test() {

		Pair<String, String> pairOfXmls = createXmlsnWithNonUniqueMethodsInVersion4AndUniqueInVersion5();
		
		String expectedXml = pairOfXmls.getSecond();
		String[] expectedResultLines = expectedXml.split("\n");
		
		String sourceXmlInVersion4 = pairOfXmls.getFirst();
		String resultInVersion5 = convertXmlFromVersion4ToVersion5(sourceXmlInVersion4);
		String[] resultLines = resultInVersion5.split(System.getProperty("line.separator"));
		
		assertNull(TestHelper.isEqualByLines(expectedResultLines, resultLines));
	}

	private void upgradeFromVersion3To4(
			String sourceInVersion3,
			String expectedResultInVersion4FromText,
			String expectedResultInVersion4FromModel) {

		// read model from xml in version 3

		ByteArrayInputStream istream = new ByteArrayInputStream(sourceInVersion3.getBytes());

		ModelParser parser = new ModelParser();

		RootNode parsedModel = null;
		ListOfStrings errorList = new ListOfStrings();
		try {
			parsedModel = parser.parseModel(istream, null, errorList);
		} catch (ParserException e) {
			fail();
		}

		if (!errorList.isEmpty()) {
			fail();
		}

		// convert model to version 4

		ModelConverter.convertFrom3To4(parsedModel);

		// write model to xml

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();

		ModelSerializer serializer = new ModelSerializer(ostream, 4);

		try {
			serializer.serialize(parsedModel);
		} catch (Exception e) {
			fail();
		}

		String result = ostream.toString();
		// System.out.println(result);

		// compare with model from text
		String[] expectedResultLines1 = expectedResultInVersion4FromText.split("\n");
		String[] resultLines1 = result.split(System.getProperty("line.separator"));

		assertNull(TestHelper.isEqualByLines(expectedResultLines1, resultLines1));

		// compare with model created in version4

		if (expectedResultInVersion4FromModel != null) {
			String[] expectedResultLines2 = expectedResultInVersion4FromModel.split("\n");
			String[] resultLines2 = result.split(System.getProperty("line.separator"));

			assertNull(TestHelper.isEqualByLines(expectedResultLines2, resultLines2));
		}
	}
	
//	private void upgradeFromVersion4To5WithCheck(
//			String sourceInVersion4,
//			String expectedResultInVersion5) {
//
//		String resultInVersion5 = convertXmlFromVersion4ToVersion5(sourceInVersion4);
//
//		// compare with model from text
//		String[] expectedResultLines1 = expectedResultInVersion5.split("\n");
//		String[] resultLines1 = resultInVersion5.split(System.getProperty("line.separator"));
//
//		assertNull(TestHelper.isEqualByLines(expectedResultLines1, resultLines1));
//	}

	private String convertXmlFromVersion4ToVersion5(String sourceXmlInVersion4) {
		
		RootNode parsedModel = parseXmlInVersion4ToModellInVersion5(sourceXmlInVersion4);
		String resultXmlInVersion5 = serializeModelInVersion5ToXml(parsedModel);
		
		return resultXmlInVersion5;
	}

	private String serializeModelInVersion5ToXml(RootNode parsedModel) {
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();

		ModelSerializer serializer = new ModelSerializer(ostream, 5);

		try {
			serializer.serialize(parsedModel);
		} catch (Exception e) {
			fail();
		}

		String result = ostream.toString();
		return result;
	}

	private RootNode parseXmlInVersion4ToModellInVersion5(String sourceInVersion4) {
		
		ByteArrayInputStream istream = new ByteArrayInputStream(sourceInVersion4.getBytes());

		ModelParser parser = new ModelParser();

		RootNode parsedModel = null;
		ListOfStrings errorList = new ListOfStrings();
		try {
			// Note. Parsing already has to convert non unique method names 
			// because method with non unique name cannot be added to class.
			
			parsedModel = parser.parseModel(istream, null, errorList);
		} catch (ParserException e) {
			fail();
		}

		if (!errorList.isEmpty()) {
			fail();
		}
		
		ModelConverter.convertFrom4To5(parsedModel);
		return parsedModel;
	}
	
	private Pair<String, String> createXmlsWithStandardConstraintInVersion3And4() {

		String sourceTxtInVersion3 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<Model name='root' version='3'>\n" +
				"    <Class name='classNode'>\n" +
				"        <Method name='method'>\n" +
				"            <Properties>\n" +
				"                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"            <Parameter name='par1' type='int' isExpected='false' expected='1' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice11' value='11' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"            <Parameter name='par2' type='int' isExpected='false' expected='2' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice21' value='21' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"            <Constraint name='cn'>\n" +
				"                <Premise>\n" +
				"                    <Statement choice='choice11' parameter='par1' relation='equal'/>\n" +
				"                </Premise>\n" +
				"                <Consequence>\n" +
				"                    <Statement choice='choice21' parameter='par2' relation='equal'/>\n" +
				"                </Consequence>\n" +
				"            </Constraint>\n" +
				"        </Method>\n" +
				"    </Class>\n" +
				"</Model>";


		sourceTxtInVersion3 = sourceTxtInVersion3.replace("'", "\"");


		String expectedResultTextInVersion4 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<Model name='root' version='4'>\n" +
				"    <Class name='classNode'>\n" +
				"        <Method name='method'>\n" +
				"            <Properties>\n" +
				"                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"            <Parameter name='par1' type='int' isExpected='false' expected='1' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice11' value='11' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"            <Parameter name='par2' type='int' isExpected='false' expected='2' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice21' value='21' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"            <Constraint name='cn' type='EF'>\n" +
				"                <Premise>\n" +
				"                    <Statement choice='choice11' parameter='par1' relation='equal'/>\n" +
				"                </Premise>\n" +
				"                <Consequence>\n" +
				"                    <Statement choice='choice21' parameter='par2' relation='equal'/>\n" +
				"                </Consequence>\n" +
				"            </Constraint>\n" +
				"        </Method>\n" +
				"    </Class>\n" +
				"</Model>";

		expectedResultTextInVersion4 = expectedResultTextInVersion4.replace("'", "\"");

		return new Pair<>(sourceTxtInVersion3, expectedResultTextInVersion4);
	}

	private Pair<String, String> createXmlsWithAssignmentConstraintInVersion3And4() {

		String sourceTxtInVersion3 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<Model name='root' version='3'>\n" +
				"    <Class name='classNode'>\n" +
				"        <Method name='method'>\n" +
				"            <Properties>\n" +
				"                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"            <Parameter name='par1' type='int' isExpected='false' expected='1' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice11' value='11' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"            <Parameter name='par2' type='int' isExpected='true' expected='2' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice21' value='21' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"            <Constraint name='cn'>\n" +
				"                <Premise>\n" +
				"                    <Statement choice='choice11' parameter='par1' relation='equal'/>\n" +
				"                </Premise>\n" +
				"                <Consequence>\n" +
				"                    <ExpectedValueStatement parameter='par2' value='5'/>\n" +
				"                </Consequence>\n" +
				"            </Constraint>\n" +
				"        </Method>\n" +
				"    </Class>\n" +
				"</Model>";


		sourceTxtInVersion3 = sourceTxtInVersion3.replace("'", "\"");


		String expectedResultTextInVersion4 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<Model name='root' version='4'>\n" +
				"    <Class name='classNode'>\n" +
				"        <Method name='method'>\n" +
				"            <Properties>\n" +
				"                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"            <Parameter name='par1' type='int' isExpected='false' expected='1' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice11' value='11' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"            <Parameter name='par2' type='int' isExpected='true' expected='2' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice21' value='21' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"            <Constraint name='cn' type='AS'>\n" +
				"                <Premise>\n" +
				"                    <Statement choice='choice11' parameter='par1' relation='equal'/>\n" +
				"                </Premise>\n" +
				"                <Consequence>\n" +
				"                    <ValueStatement rightValue='5' parameter='par2' relation='assign'/>\n" +
				"                </Consequence>\n" +
				"            </Constraint>\n" +
				"        </Method>\n" +
				"    </Class>\n" +
				"</Model>";

		expectedResultTextInVersion4 = expectedResultTextInVersion4.replace("'", "\"");

		return new Pair<>(sourceTxtInVersion3, expectedResultTextInVersion4);
	}

	private Pair<String, String> createXmlsnWithNonUniqueMethodsInVersion4AndUniqueInVersion5() {

		String sourceTxtInVersion3 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<Model name='root' version='4'>\n" +
				"    <Class name='classNode'>\n" +
				
				"        <Method name='method'>\n" +
				"            <Properties>\n" +
				"                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"            <Parameter name='par1' type='int' isExpected='false' expected='1' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice11' value='11' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"        </Method>\n" +
				
				"        <Method name='method'>\n" +
				"            <Properties>\n" +
				"                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"        </Method>\n" +
				
				"    </Class>\n" +
				"</Model>";


		sourceTxtInVersion3 = sourceTxtInVersion3.replace("'", "\"");


		String expectedResultTextInVersion4 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<Model name='root' version='5'>\n" +
				"    <Class name='classNode'>\n" +
				
				"        <Method name='method'>\n" +
				"            <Properties>\n" +
				"                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"            <Parameter name='par1' type='int' isExpected='false' expected='1' linked='false'>\n" +
				"                <Properties>\n" +
				"                    <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"                </Properties>\n" +
				"                <Comments>\n" +
				"                    <TypeComments/>\n" +
				"                </Comments>\n" +
				"                <Choice name='choice11' value='11' isRandomized='false'/>\n" +
				"            </Parameter>\n" +
				"        </Method>\n" +

				"        <Method name='method1'>\n" +
				"            <Properties>\n" +
				"                <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"                <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"                <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"                <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"        </Method>\n" +
				
				"    </Class>\n" +
				"</Model>";

		expectedResultTextInVersion4 = expectedResultTextInVersion4.replace("'", "\"");

		return new Pair<>(sourceTxtInVersion3, expectedResultTextInVersion4);
	}
	
	private String generateAssignmentConstraintExampleInVersion4() {

		RootNode root = new RootNode("root", null, 4);

		ClassNode classNode = new ClassNode("classNode", null);
		root.addClass(classNode);

		MethodNode methodNode = new MethodNode("method", null);
		classNode.addMethod(methodNode);

		// method parameter 1 node with choice

		BasicParameterNode methodParameterNode1 = new BasicParameterNode(
				"par1",
				"int",
				"1",
				false,
				null);

		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// method parameter 2 node with choice

		BasicParameterNode methodParameterNode2 = new BasicParameterNode(
				"par2",
				"int",
				"2",
				true,
				null);

		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode21 = new ChoiceNode("choice21", "21",  null);
		methodParameterNode2.addChoice(choiceNode21);

		// constraint

		AbstractStatement precondition =
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode1, null, EMathRelation.EQUAL, choiceNode11);  // TODO MO-RE leftParameterLinkingContext

		AbstractStatement postcondition =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode2, "5");

		Constraint constraint = new Constraint(
				"constraint",
				ConstraintType.ASSIGNMENT,
				precondition,
				postcondition,
				null);

		ConstraintNode constraintNode = new ConstraintNode("cn", constraint, null);
		methodNode.addConstraint(constraintNode);

		// serializing to stream

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, 3);

		try {
			serializer.serialize(root);
		} catch (Exception e) {
			fail();
		}

		return ostream.toString();
	}
	
}
