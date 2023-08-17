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

import static com.ecfeed.core.model.ConstraintNodeHelper.createSignature;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;

public class ConstraintNodeHelperTest {

	@Test
	public void createSignatureTest1(){

		ConstraintNode c1 = new ConstraintNode("c", new Constraint("c", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(true, null), null), null);

		c1.setName("c_1");

		c1.getConstraint().setPrecondition(new StaticStatement(false, null));

		c1.getConstraint().setPostcondition(new StaticStatement(false, null));

		String signature = createSignature(c1,  new ExtLanguageManagerForJava());
		assertEquals("c_1 : false => false", signature);

		signature = createSignature(c1,  new ExtLanguageManagerForSimple());
		assertEquals("c_1 : false => false", signature);
	}

	@Test
	public void createSignatureTest2() {

		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();
		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		BasicParameterNode parameter1 = new BasicParameterNode("par_1", "int", "0", false, null);

		AbstractStatement precondition1 =
				RelationStatement.createRelationStatementWithValueCondition(
						parameter1, null, EMathRelation.EQUAL, "A");

		AbstractStatement precondition = precondition1;

		BasicParameterNode parameter2 = new BasicParameterNode("par_2", "int", "0", false, null);

		AbstractStatement postcondition1 =
				RelationStatement.createRelationStatementWithValueCondition(
						parameter2, null, EMathRelation.EQUAL, "C");

		AbstractStatement postcondition = postcondition1;

		Constraint constraint = new Constraint("co_1", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintNode c1 = new ConstraintNode("cn", constraint, null);

		String signature = ConstraintNodeHelper.createSignature(c1, extLanguageManagerForSimple);
		assertEquals("co_1 : par 1=A => par 2=C", signature);

		signature = ConstraintNodeHelper.createSignature(c1, extLanguageManagerForJava);
		assertEquals("co_1 : par_1=A => par_2=C", signature);
	}

	//	@Test
	//	public void createSignatureTest3() {
	//
	//		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();
	//		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();
	//
	//		ChoiceNode choice1 = new ChoiceNode("choice_1", "value1", null);
	//		ChoiceNode choice2 = new ChoiceNode("choice 2", "value2", null);
	//
	//		BasicParameterNode parameter1 = new BasicParameterNode("par_1", "int", "0", false, null);
	//
	//		AbstractStatement precondition =
	//				RelationStatement.createRelationStatementWithChoiceCondition(
	//						parameter1, null, EMathRelation.EQUAL, choice1);
	//
	//		BasicParameterNode parameter2 = new BasicParameterNode("par_2", "int", "0", false, null);
	//
	//		AbstractStatement postcondition =
	//				RelationStatement.createRelationStatementWithChoiceCondition(
	//						parameter2, null, EMathRelation.EQUAL, choice2);
	//
	//		Constraint constraint = new Constraint("co", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);
	//
	//		ConstraintNode constraintNode = new ConstraintNode("cn", constraint, null);
	//
	//		String signature = ConstraintNodeHelper.createSignature(constraintNode, extLanguageManagerForJava);
	//		assertEquals("co : par_1=choice_1[choice] => par_2=choice 2[choice]", signature);
	//
	//		signature = ConstraintNodeHelper.createSignature(constraintNode, extLanguageManagerForSimple);
	//		assertEquals("co : par 1=choice_1[choice] => par 2=choice 2[choice]", signature);
	//	}

	@Test
	public void createSignatureForCompositeParameterTest(){

		// add method and composite parameter

		MethodNode methodNode = new MethodNode("method");
		CompositeParameterNode compositeParameterNode = new CompositeParameterNode("Composite", null);
		methodNode.addParameter(compositeParameterNode);

		// create and add constraint

		Constraint constraint = new Constraint(
				"c", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(true, null), null);

		ConstraintNode c1 =	new ConstraintNode("c", constraint, null);

		c1.setName("c_1");

		c1.getConstraint().setPrecondition(new StaticStatement(false, null));

		c1.getConstraint().setPostcondition(new StaticStatement(false, null));

		compositeParameterNode.addConstraint(c1);

		// check signatures

		String signature = createSignature(c1,  new ExtLanguageManagerForJava());
		assertEquals("Composite:c_1 : false => false", signature);

		signature = createSignature(c1,  new ExtLanguageManagerForSimple());
		assertEquals("Composite:c_1 : false => false", signature);
	}

	@Test
	public void getMentioningConstraintNodesForBasicParameter() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "Class1", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "Method1", true, null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "String", "", true, null);

		ChoiceNode choiceNode = 
				BasicParameterNodeHelper.addNewChoice(
						basicParameterNode, "choice1", "1", false, true, null);

		// constraint 1 with choice condition

		RelationStatement precondition1 =
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, 
						EMathRelation.EQUAL, 
						choiceNode);

		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint1 = new Constraint(
				"constraint1", ConstraintType.EXTENDED_FILTER, precondition1, postcondition, null);

		ConstraintNode constraintNode1 = 
				ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint1, true, null);

		// constraint 2 - static statements
		
		Constraint constraint2 = new Constraint(
				"constraint1", 
				ConstraintType.EXTENDED_FILTER, 
				new StaticStatement(EvaluationResult.TRUE), 
				new StaticStatement(EvaluationResult.TRUE), 
				null);
		
		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint2, true, null);
		
		List<ConstraintNode> mentioningConstraintNodes = 
				ConstraintNodeHelper.getMentioningConstraintNodes(basicParameterNode);

		assertEquals(1, mentioningConstraintNodes.size());

		ConstraintNode resultConstraintNode = mentioningConstraintNodes.get(0);

		assertEquals(constraintNode1, resultConstraintNode);
	}

}
