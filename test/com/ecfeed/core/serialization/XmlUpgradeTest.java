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

		RootNode convertedModel = ModelConverter.convertFrom3To4(parsedModel);

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
		assertEqualsByLines(expectedResultInVersion4FromText, result);

		// compare with model created in version4

		if (expectedResultInVersion4FromModel != null) {
			assertEqualsByLines(expectedResultInVersion4FromModel, result);
		}
	}

	private void assertEqualsByLines(String expectedResult, String result) {

		final String lineSeparator = System.getProperty("line.separator");

		String[] expectedResultLines = expectedResult.split(lineSeparator);
		String[] resultLines = result.split(lineSeparator);

		if (expectedResultLines.length != resultLines.length) {
			fail("Content does not match");
			return;
		}

		for (int lineIndex = 0; lineIndex < resultLines.length; lineIndex++) {

			String expectedLine = expectedResultLines[lineIndex];
			expectedLine = expectedLine.replace("\r", "");

			String resultLine = resultLines[lineIndex];
			resultLine = resultLine.replace("\r", "");

			if (!StringHelper.isEqual(expectedLine, resultLine)) {
				fail("Line: " + (lineIndex + 1) + " differs.");
			}
		}
	}

	private Pair<String, String> createXmlsWithStandardConstraintInVersion3And4() {

		String sourceTxtInVersion3 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<Model name='root' version='3'>\n" +
				"    <Class name='classNode'>\n" +
				"        <Properties>\n" +
				"            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
				"        </Properties>\n" +
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
				"        <Properties>\n" +
				"            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
				"        </Properties>\n" +
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
				"        <Properties>\n" +
				"            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
				"        </Properties>\n" +
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
				"        <Properties>\n" +
				"            <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
				"        </Properties>\n" +
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

	private String generateAssignmentConstraintExampleInVersion4() {

		RootNode root = new RootNode("root", null, 4);

		ClassNode classNode = new ClassNode("classNode", null);
		root.addClass(classNode);

		MethodNode methodNode = new MethodNode("method", null);
		classNode.addMethod(methodNode);

		// method parameter 1 node with choice

		MethodParameterNode methodParameterNode1 = new MethodParameterNode(
				"par1",
				"int",
				"1",
				false,
				null);

		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// method parameter 2 node with choice

		MethodParameterNode methodParameterNode2 = new MethodParameterNode(
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
				RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode11);

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
