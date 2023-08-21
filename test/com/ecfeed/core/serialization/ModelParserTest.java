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
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IStatementCondition;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.ValueCondition;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.StringHelper;

public class ModelParserTest {

	RandomModelGenerator fGenerator = new RandomModelGenerator();

	@Test
	public void parseModelWithValueAssignmentConstraint() {

		String modelXml = getModelXmlWithAssingments();

		ByteArrayInputStream istream = new ByteArrayInputStream(modelXml.getBytes());
		ModelParser parser = new ModelParser();
		try {
			final ListOfStrings outErrorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, outErrorList);

			// ModelLogger.printModel("after parsing", parsedModel);

			if (!outErrorList.isEmpty()) {
				fail(outErrorList.getCollectionOfStrings().get(0));
			}

			checkAssignmentsInModel(parsedModel);

		} catch (Exception e) {
			fail();
		}
	}

	private void checkAssignmentsInModel(RootNode rootNode) {

		List<ClassNode> classNodes = rootNode.getClasses();
		ClassNode classNode = classNodes.get(0);

		List<MethodNode> methodNodes = classNode.getMethods();
		MethodNode methodNode = methodNodes.get(0);

		List<ConstraintNode> constraintNodes = methodNode.getConstraintNodes();
		ConstraintNode constraintNode = constraintNodes.get(0);

		Constraint constraint = constraintNode.getConstraint();

		AbstractStatement postcondition = constraint.getPostcondition();

		if (!(postcondition instanceof StatementArray)) {
			fail();
		}

		StatementArray statementArray = (StatementArray)postcondition;

		List<AbstractStatement> statements = statementArray.getChildren();

		if (statements.size() != 1) {
			fail();
		}

		AbstractStatement abstractStatement = statements.get(0);

		if (!(abstractStatement instanceof AssignmentStatement)) {
			fail();
		}

		AssignmentStatement assignmentStatement = (AssignmentStatement)abstractStatement;

		if (assignmentStatement.getRelation() !=  EMathRelation.ASSIGN) {
			fail();
		}

		IStatementCondition statementCondition = assignmentStatement.getCondition();

		if (!(statementCondition instanceof ValueCondition)) {
			fail();
		}

		ValueCondition valueCondition  = (ValueCondition)statementCondition;

		if (!StringHelper.isEqual(valueCondition.getRightValue(), "5")) {
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
