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
				RelationStatement.createRelationStatementWithParameterCondition(
						methodParameterNode1, null, EMathRelation.EQUAL, methodParameterNode2, null);  // TODO MO-RE leftParameterLinkingContext
			} catch (Exception e) {
				fail();
			}
		} else {
			try {
				RelationStatement.createRelationStatementWithParameterCondition(
						methodParameterNode1, null, EMathRelation.EQUAL, methodParameterNode2, null);  // TODO MO-RE leftParameterLinkingContext
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
				RootNodeHelper.addNewGlobalBasicParameterToRoot(rootNode, "gp", "String", true, null);

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
				RootNodeHelper.addNewGlobalCompositeParameterToRoot(rootNode, "gs", true, null);

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
						localBasicParameter1, null, EMathRelation.EQUAL, localChoice1);

		assertTrue(statement1.isConsistent(methodNode1));

		// hanging choice

		ChoiceNode hangingChoiceNode = new ChoiceNode("h", "A");

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						localBasicParameter1, null, EMathRelation.EQUAL, hangingChoiceNode);

		assertFalse(statement2.isConsistent(methodNode1));

		// other parameter

		RelationStatement statement3 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						localBasicParameter2, null, EMathRelation.EQUAL, localChoice1);

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
						basicParameterNode11, null, EMathRelation.EQUAL, basicParameterNode12, null);

		assertTrue(statement1.isConsistent(methodNode1));

		// the second method with parameter

		MethodNode methodNode2 = new MethodNode("method2");

		BasicParameterNode basicParameterNode21 = 
				MethodNodeHelper.addNewBasicParameter(methodNode2, "par21", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode21, "choice21", "1", false, true, null);

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithParameterCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, basicParameterNode21, null);

		assertFalse(statement2.isConsistent(methodNode1));		
	}

	@Test
	public void isConsistentParameterConditionWithGlobalParameter() {

		RootNode rootNode = new RootNode("root", null);

		BasicParameterNode globalBasicParameter1 = 
				RootNodeHelper.addNewGlobalBasicParameterToRoot(rootNode, "gp1", "String", true, null);

		BasicParameterNode globalBasicParameter2 = 
				RootNodeHelper.addNewGlobalBasicParameterToRoot(rootNode, "gp2", "String", true, null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", null);

		MethodNode methodNode1 = ClassNodeHelper.addNewMethodToClass(classNode, "method1", true, null);

		BasicParameterNode basicParameterNode11 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par11", "int", "0", true, null);

		basicParameterNode11.setLinkToGlobalParameter(globalBasicParameter1);

		BasicParameterNode basicParameterNode12 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par12", "int", "0", true, null);

		basicParameterNode12.setLinkToGlobalParameter(globalBasicParameter2);

		// consistent statement 

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithParameterCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, basicParameterNode12, null);

		assertTrue(statement1.isConsistent(methodNode1));

		// parameter from other method

		MethodNode methodNode2 = ClassNodeHelper.addNewMethodToClass(classNode, "method2", true, null);

		BasicParameterNode basicParameterNode21 = 
				MethodNodeHelper.addNewBasicParameter(methodNode2, "par21", "int", "0", true, null);

		basicParameterNode21.setLinkToGlobalParameter(globalBasicParameter1);

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithParameterCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, basicParameterNode21, null);

		assertFalse(statement2.isConsistent(methodNode1));
		assertFalse(statement2.isConsistent(methodNode2));
	}

	@Test
	public void isConsistentParameterConditionWithGlobalStructure() {

		RootNode rootNode = new RootNode("root", null);

		// the first global structure with parameter

		CompositeParameterNode globalCompositeParameter1 = 
				RootNodeHelper.addNewGlobalCompositeParameterToRoot(rootNode, "gs", true, null);

		BasicParameterNode globalBasicParameter11 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameter1,"gp1", "String", "", false, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameter11, "choice", "1", false, true, null);

		// the second global structure with parameter

		CompositeParameterNode globalCompositeParameter2 = 
				RootNodeHelper.addNewGlobalCompositeParameterToRoot(rootNode, "gs", true, null);

		BasicParameterNode globalBasicParameter21 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameter2,"gp2", "String", "", false, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameter21, "choice", "1", false, true, null);

		// class and method

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", null);

		MethodNode methodNode1 = ClassNodeHelper.addNewMethodToClass(classNode, "method1", true, null);

		// linked parameter 1

		CompositeParameterNode localCompositeParameter11 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode1, "ls11", true, null);

		localCompositeParameter11.setLinkToGlobalParameter(globalCompositeParameter1);

		// linked parameter 2

		CompositeParameterNode localCompositeParameter12 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode1, "ls12", true, null);

		localCompositeParameter12.setLinkToGlobalParameter(globalCompositeParameter2);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithParameterCondition(
						globalBasicParameter11, localCompositeParameter11, 
						EMathRelation.EQUAL, 
						globalBasicParameter21, localCompositeParameter12);

		assertTrue(statement1.isConsistent(methodNode1));	

		// the second method

		MethodNode methodNode2 = ClassNodeHelper.addNewMethodToClass(classNode, "method2", true, null);

		CompositeParameterNode localCompositeParameter21 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode2, "ls", true, null);

		localCompositeParameter21.setLinkToGlobalParameter(globalCompositeParameter1);

		// right parameter from another method

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithParameterCondition(
						globalBasicParameter11, localCompositeParameter11, 
						EMathRelation.EQUAL, 
						globalBasicParameter21, localCompositeParameter21);

		assertFalse(statement2.isConsistent(methodNode1));		
	}

	@Test
	public void isConsistentLabelConditionWithLocalParameter() {

		MethodNode methodNode1 = new MethodNode("method1");

		BasicParameterNode basicParameterNode11 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par11", "int", "0", true, null);

		ChoiceNode choiceNode11 = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode11, "choice11", "1", false, true, null);

		choiceNode11.addLabel("label1");

		BasicParameterNode basicParameterNode12 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par12", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode12, "choice12", "1", false, true, null);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithLabelCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, "label1");

		assertTrue(statement1.isConsistent(methodNode1));

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithLabelCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, "label2");

		assertFalse(statement2.isConsistent(methodNode1));
	}

	@Test
	public void isConsistentValueConditionWithLocalParameter() {

		MethodNode methodNode1 = new MethodNode("method1");

		BasicParameterNode basicParameterNode11 = 
				MethodNodeHelper.addNewBasicParameter(methodNode1, "par11", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode11, "choice11", "11", false, true, null);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithValueCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, "11");

		assertTrue(statement1.isConsistent(methodNode1));

		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithValueCondition(
						basicParameterNode11, null, EMathRelation.EQUAL, "AB");

		assertTrue(statement2.isConsistent(methodNode1));
	}

	@Test
	public void isConsistentChoiceConditionWithGlobalStructureAndConstraintUnderStructure() {

		RootNode rootNode = new RootNode("root", null);
		
		CompositeParameterNode globalCompositeParameter = 
				RootNodeHelper.addNewGlobalCompositeParameterToRoot(rootNode, "gs", true, null);
		
		BasicParameterNode globalBasicParameter1 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameter,"gp1", "String", "", false, null);

		ChoiceNode globalChoice = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameter1, "gc1", "1", false, true, null);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						globalBasicParameter1, null, EMathRelation.EQUAL, globalChoice);
		
		// valid parameter

		assertTrue(statement1.isConsistent(globalCompositeParameter));

		// invalid parameter
		
		BasicParameterNode globalBasicParameter2 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameter,"lp2", "String", "", false, null);
		
		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						globalBasicParameter2, null, EMathRelation.EQUAL, globalChoice);

		assertFalse(statement2.isConsistent(globalCompositeParameter));
	}
	
	@Test
	public void isConsistentChoiceConditionWithLocalStructureAndConstraintUnderStructure() {

		RootNode rootNode = new RootNode("root", null);
		
		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", null);
		
		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);
		
		CompositeParameterNode localCompositeParameter = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "ls", true, null);
		
		BasicParameterNode localBasicParameter1 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						localCompositeParameter,"gp1", "String", "", false, null);

		ChoiceNode localChoice = BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				localBasicParameter1, "gc1", "1", false, true, null);

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						localBasicParameter1, null, EMathRelation.EQUAL, localChoice);
		
		// valid parameter

		assertTrue(statement1.isConsistent(localCompositeParameter));

		// invalid parameter
		
		BasicParameterNode localBasicParameter2 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						localCompositeParameter,"lp2", "String", "", false, null);
		
		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						localBasicParameter2, null, EMathRelation.EQUAL, localChoice);

		assertFalse(statement2.isConsistent(localCompositeParameter));
	}
	
}
