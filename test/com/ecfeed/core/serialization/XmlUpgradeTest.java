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
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.StringHelper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.junit.Assert.*;

public class XmlUpgradeTest {

	@Test
	public void upgradeStandardConstraintFromVersion3To4Test() {

		String sourceInVersion3 = createSourceXmlWithStandardConstraintInVersion3();
		String expectedResultInVersion4 = createResultXmlWithStandardConstraintInVersion4();

		upgradeFromVersion3To4(sourceInVersion3, expectedResultInVersion4);
	}

	private void upgradeFromVersion3To4(String sourceInVersion3, String expectedResultInVersion4) {

		// read model from xml in version 3

		ByteArrayInputStream istream = new ByteArrayInputStream(sourceInVersion3.getBytes());

		ModelParser parser = new ModelParser();

		RootNode parsedModel = null;
		ArrayList<String> errorList = new ArrayList<>();
		try {
			parsedModel = parser.parseModel(istream, null, errorList);
		} catch (ParserException e) {
			fail();
		}

		if (errorList.size() > 0) {
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

		// compare
		assertEqualsByLines(expectedResultInVersion4, result);
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
				fail("Line: " + (lineIndex+1) + " differs.");
			}
		}
	}

	private String createSourceXmlWithStandardConstraintInVersion3() {

		String xml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
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


		xml = xml.replace("'", "\"");
		return xml;
	}

	private String createResultXmlWithStandardConstraintInVersion4() {

		String xml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
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

		xml = xml.replace("'", "\"");
		return xml;
	}

}
