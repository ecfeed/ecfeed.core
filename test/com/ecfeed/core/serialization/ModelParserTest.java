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
import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.ecfeed.core.testutils.ModelTestUtils.assertElementsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ModelParserTest {

	RandomModelGenerator fGenerator = new RandomModelGenerator();

	@Test
	public void parseModelWithAssignmentConstraint() {

		String modelXml = getModelXmlWithAssingments();

		ByteArrayInputStream istream = new ByteArrayInputStream(modelXml.getBytes());
		ModelParser parser = new ModelParser();
		try {
			RootNode parsedModel = parser.parseModel(istream, null, new ListOfStrings());
			ModelLogger.printModel("after parsing", parsedModel);
		} catch (ParserException e) {
			fail();
		}
	}

	private String getModelXmlWithAssingments() {

		String xml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<Model name='TestModel11' version='4'>\n" +
				"    <Class name='test.Class1'>\n" +
				"    <Properties>\n" +
				"        <Property name='runOnAndroid' type='boolean' value='false'/>\n" +
				"    </Properties>\n" +
				"    <Method name='testMethod'>\n" +
				"        <Properties>\n" +
				"            <Property name='methodRunner' type='String' value='Java Runner'/>\n" +
				"            <Property name='wbMapBrowserToParam' type='boolean' value='false'/>\n" +
				"            <Property name='wbBrowser' type='String' value='Chrome'/>\n" +
				"            <Property name='wbMapStartUrlToParam' type='boolean' value='false'/>\n" +
				"        </Properties>\n" +
				"        <Parameter name='arg1' type='int' isExpected='false' expected='0' linked='false'>\n" +
				"            <Properties>\n" +
				"                <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"            </Properties>\n" +
				"            <Comments>\n" +
				"                <TypeComments/>\n" +
				"            </Comments>\n" +
				"            <Choice name='choice11' value='1' isRandomized='false'/>\n" +
				"            <Choice name='choice12' value='2' isRandomized='false'/>\n" +
				"        </Parameter>\n" +
				"        <Parameter name='arg2' type='char' isExpected='true' expected='5' linked='false'>\n" +
				"        <Properties>\n" +
				"            <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"        </Properties>\n" +
				"        <Comments>\n" +
				"            <TypeComments/>\n" +
				"        </Comments>\n" +
				"        </Parameter>\n" +
				"        <Constraint name='constraint' type='AS'>\n" +
				"            <Premise>\n" +
				"                <StaticStatement value='true'/>\n" +
				"            </Premise>\n" +
				"            <Consequence>\n" +
				"                <StatementArray operator='assign'>\n" +
				"                    <ValueStatement rightValue='5' parameter='arg2' relation='assign'/>\n" +
				"                </StatementArray>\n" +
				"            </Consequence>\n" +
				"            </Constraint>\n" +
				"    </Method>\n" +
				"    </Class>\n" +
				"    <Parameter name='arg1' type='int'>\n" +
				"        <Properties>\n" +
				"            <Property name='wbIsOptional' type='boolean' value='false'/>\n" +
				"        </Properties>\n" +
				"        <Comments>\n" +
				"            <TypeComments/>\n" +
				"        </Comments>\n" +
				"        <Choice name='choice1' value='0' isRandomized='false'/>\n" +
				"        <Choice name='choice2' value='0' isRandomized='false'/>\n" +
				"    </Parameter>\n" +
				"</Model>\n";

		xml = xml.replace("'", "\"");
		return xml;

	}

}
