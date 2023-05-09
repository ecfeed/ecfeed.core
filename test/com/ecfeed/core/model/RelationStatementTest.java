/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import com.ecfeed.core.utils.EMathRelation;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RelationStatementTest {

	@Test
	public void testStatementWithParameterCondition() {

		testCreateStatementWithParameterCondition("int", "int", true);
		testCreateStatementWithParameterCondition("int", "boolean", false);
		testCreateStatementWithParameterCondition("boolean", "int", false);
		testCreateStatementWithParameterCondition("boolean", "boolean", true);

		testCreateStatementWithParameterCondition("byte", "int", true);
		testCreateStatementWithParameterCondition("int", "byte", true);

		testCreateStatementWithParameterCondition("int", "short", true);
		testCreateStatementWithParameterCondition("short", "int", true);

		testCreateStatementWithParameterCondition("long", "int", true);
		testCreateStatementWithParameterCondition("int", "long", true);

		testCreateStatementWithParameterCondition("int", "float", true);
		testCreateStatementWithParameterCondition("float", "int", true);

		testCreateStatementWithParameterCondition("float", "double", true);
		testCreateStatementWithParameterCondition("double", "float", true);

		testCreateStatementWithParameterCondition("String", "String", true);
		testCreateStatementWithParameterCondition("String", "boolean", false);
		testCreateStatementWithParameterCondition("boolean", "String", false);

		testCreateStatementWithParameterCondition("char", "char", true);
		testCreateStatementWithParameterCondition("String", "char", true);
		testCreateStatementWithParameterCondition("char", "String", true);

		testCreateStatementWithParameterCondition("String", "double", false);
		testCreateStatementWithParameterCondition("double", "String", false);
	}

	private void testCreateStatementWithParameterCondition(String parameter1Type, String parameter2Type, boolean okExpected) {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", parameter1Type, null, false, null);
		methodNode.addParameter(methodParameterNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", parameter2Type, null, false, null);
		methodNode.addParameter(methodParameterNode2);

		if (okExpected) {
			try {
				RelationStatement.createRelationStatementWithParameterCondition(methodParameterNode1, null, EMathRelation.EQUAL, methodParameterNode2);  // TODO MO-RE leftParameterLinkingContext
			} catch (Exception e) {
				fail();
			}
		} else {
			try {
				RelationStatement.createRelationStatementWithParameterCondition(methodParameterNode1, null, EMathRelation.EQUAL, methodParameterNode2);  // TODO MO-RE leftParameterLinkingContext
				fail();
			} catch (Exception e) {
			}
		}
	}

	@Test
	public void choiceStatementTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", "65", false, null);
		ChoiceNode choice = new ChoiceNode("choice", "876", null);
		parameter.addChoice(choice);
		choice.addLabel("label");

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						parameter, null, EMathRelation.EQUAL, choice);
		
		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithLabelCondition(
						parameter, null, EMathRelation.EQUAL, "label");

		RelationStatement copy1 = statement1.makeClone();
		RelationStatement copy2 = statement2.makeClone();

		assertTrue(statement1.isEqualTo(copy1));
		assertTrue(statement2.isEqualTo(copy2));
	}
	
	@Test
	public void isConsistent1() {

		MethodNode methodNode1 = new MethodNode("method1");
		
		BasicParameterNode basicParameterNode1 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par", "int", "0", true, null);
		
		ChoiceNode choice1 = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode1, "choice", "1", false, true, null);
		
		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, choice1);
		
		MethodNode methodNode2 = new MethodNode("method2");
		
		BasicParameterNode basicParameterNode2 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par", "int", "0", true, null);
		
		ChoiceNode choice2 = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode2, "choice", "1", false, true, null);
		
		assertTrue(statement1.isConsistent(methodNode1));
		
		// method mismatched with parameter
		
		assertFalse(statement1.isConsistent(methodNode2));

		// choice mismatched
		
		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, choice2);
		
		assertFalse(statement2.isConsistent(methodNode1));
		
		// hanging choice

		ChoiceNode hangingChoice = new ChoiceNode("X", "1");
		
		RelationStatement statement3 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, hangingChoice);
		
		assertFalse(statement3.isConsistent(methodNode1));
		
		// hanging parameter
		
		BasicParameterNode hangingParameter =
				new BasicParameterNode("p", "int", "0", false, null);

		RelationStatement statement4 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						hangingParameter, null, EMathRelation.EQUAL, choice1);
		
		assertFalse(statement4.isConsistent(methodNode1));		
	}
}
