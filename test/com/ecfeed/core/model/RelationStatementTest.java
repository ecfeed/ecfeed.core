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
	public void isConsistentChoiceConditionWithLocalParameter() {

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

	@Test
	public void isConsistentChoiceConditionWithGlobalParameter() {

		RootNode rootNode = new RootNode("root", null);

		BasicParameterNode globalBasicParameter = 
				RootNodeHelper.addNewGlobalBasicParameterToRoot(rootNode, "gp", "String", null);

		ChoiceNode globalChoice1 = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameter, "choice", "1", false, true, null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", null);

		MethodNode methodNode1 = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		BasicParameterNode basicParameterNode1 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par", "int", "0", true, null);

		basicParameterNode1.setLinkToGlobalParameter(globalBasicParameter);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, globalChoice1);

		assertTrue(statement1.isConsistent(methodNode1));	

		// hanging choice

		ChoiceNode hangingChoice = new ChoiceNode("X", "1");

		RelationStatement statement3 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, hangingChoice);

		assertFalse(statement3.isConsistent(methodNode1));
	}

	@Test
	public void isConsistentChoiceConditionWithGlobalStructure() {

		RootNode rootNode = new RootNode("root", null);

		CompositeParameterNode globalCompositeParameter = 
				RootNodeHelper.addGlobalCompositeParameterToRoot(rootNode, "gs", true, null);

		BasicParameterNode globalBasicParameter = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameter,"gp", "String", "", false, null);

		ChoiceNode globalChoice1 = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameter, "choice", "1", false, true, null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", null);

		MethodNode methodNode1 = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		CompositeParameterNode localCompositeParameter = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode1, "ls", true, null);

		localCompositeParameter.setLinkToGlobalParameter(globalCompositeParameter);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						globalBasicParameter, localCompositeParameter, EMathRelation.EQUAL, globalChoice1);

		assertTrue(statement1.isConsistent(methodNode1));	

		// hanging choice

		ChoiceNode hangingChoiceNode = new ChoiceNode("hc", "0");

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						globalBasicParameter, localCompositeParameter, EMathRelation.EQUAL, hangingChoiceNode);

		assertFalse(statement2.isConsistent(methodNode1));

		// choice form other parameter

		BasicParameterNode localBasicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par", "String", "X", true, null);

		ChoiceNode localChoice = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				localBasicParameterNode, "lchoice", "1", false, true, null);

		RelationStatement statement3 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						globalBasicParameter, localCompositeParameter, EMathRelation.EQUAL, localChoice);

		assertFalse(statement3.isConsistent(methodNode1));

		// other parameter

		RelationStatement statement4 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						localBasicParameterNode, localCompositeParameter, EMathRelation.EQUAL, globalChoice1);

		assertFalse(statement4.isConsistent(methodNode1));
	}

	@Test
	public void isConsistentChoiceConditionWithLocalStructure() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", null);

		MethodNode methodNode1 = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		CompositeParameterNode localCompositeParameter = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode1, "ls", true, null);

		BasicParameterNode localBasicParameter1 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						localCompositeParameter,"lp1", "String", "", false, null);

		ChoiceNode localChoice1 = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				localBasicParameter1, "choice", "1", false, true, null);


		BasicParameterNode localBasicParameter2 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						localCompositeParameter,"lp2", "String", "", false, null);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						localBasicParameter1, localCompositeParameter, EMathRelation.EQUAL, localChoice1);

		assertTrue(statement1.isConsistent(methodNode1));

		// hanging choice

		ChoiceNode hangingChoiceNode = new ChoiceNode("h", "A");

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						localBasicParameter1, localCompositeParameter, EMathRelation.EQUAL, hangingChoiceNode);

		assertFalse(statement2.isConsistent(methodNode1));

		// other parameter

		RelationStatement statement3 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						localBasicParameter2, localCompositeParameter, EMathRelation.EQUAL, localChoice1);

		assertFalse(statement3.isConsistent(methodNode1));		
	}

	@Test
	public void isConsistentParameterConditionWithLocalParameter() {

		MethodNode methodNode1 = new MethodNode("method1");

		BasicParameterNode basicParameterNode11 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par11", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode11, "choice11", "1", false, true, null);

		BasicParameterNode basicParameterNode12 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par12", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode12, "choice12", "1", false, true, null);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithParameterCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, basicParameterNode12);

		assertTrue(statement1.isConsistent(methodNode1));

		// the second method with parameter

		MethodNode methodNode2 = new MethodNode("method2");

		BasicParameterNode basicParameterNode21 = 
				MethodNodeHelper.addNewBasicParameter(methodNode2, "par21", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode21, "choice21", "1", false, true, null);

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithParameterCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, basicParameterNode21);

		assertFalse(statement2.isConsistent(methodNode1));		
	}

	// XYX TODO parameter condition, label condition, value condition 
}
