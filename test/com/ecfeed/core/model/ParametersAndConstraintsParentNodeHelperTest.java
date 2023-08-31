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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.utils.BasicParameterWithChoice;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;

public class ParametersAndConstraintsParentNodeHelperTest {

	@Test
	public void getParametersWithChoicesForBasicParameterTest1() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "method", true, null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "String", "", true, null);

		BasicParameterNodeHelper.addNewChoice(basicParameterNode, "choice", "c1", false, true, null);

		StaticStatement precondition1 = new StaticStatement(EvaluationResult.TRUE);
		StaticStatement postcondition1 = new StaticStatement(EvaluationResult.TRUE);

		Constraint constraint1 = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				precondition1, 
				postcondition1, 
				null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint1, true, null);

		StaticStatement precondition2 = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition2 = 
				RelationStatement.createRelationStatementWithParameterCondition(
						basicParameterNode, null, EMathRelation.EQUAL, basicParameterNode, null);

		Constraint constraint2 = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				precondition2, 
				postcondition2, 
				null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint2, true, null);

		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						basicParameterNode);

		assertEquals(0, parametersWithChoices.size());
	}

	@Test
	public void getParametersWithChoicesForBasicParameterTest2() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "method", true, null);

		BasicParameterNode basicParameterNode1 = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "String", "", true, null);

		ChoiceNode choiceNode1 =
				BasicParameterNodeHelper.addNewChoice(basicParameterNode1, "choice1", "c1", false, true, null);

		BasicParameterNode basicParameterNode2 = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par2", "String", "", true, null);

		BasicParameterNodeHelper.addNewChoice(basicParameterNode2, "choice1", "c1", false, true, null);

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, choiceNode1);

		Constraint constraint = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				precondition, 
				postcondition, 
				null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						basicParameterNode1);

		assertEquals(1, parametersWithChoices.size());

		assertEquals(choiceNode1, parametersWithChoices.get(0).getChoiceNode());
		assertEquals(basicParameterNode1, parametersWithChoices.get(0).getBasicParameterNode());
	}

	@Test
	public void getParametersWithChoicesForCompositeParameterTest1() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "method", true, null);

		CompositeParameterNode compositeParameterNode = 
				MethodNodeHelper.addNewCompositeParameter(methodNode, "str1", true, null);

		BasicParameterNode basicParameterNode1 =
				CompositeParameterNodeHelper.addNewBasicParameter(compositeParameterNode, "par1", "String", "", true, null);

		ChoiceNode choiceNode1 =
				BasicParameterNodeHelper.addNewChoice(basicParameterNode1, "choice1", "c1", false, true, null);

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, choiceNode1);

		Constraint constraint = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				precondition, 
				postcondition, 
				null);

		ConstraintsParentNodeHelper.addNewConstraintNode(compositeParameterNode, constraint, true, null);

		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						compositeParameterNode);

		assertEquals(1, parametersWithChoices.size());

		assertEquals(choiceNode1, parametersWithChoices.get(0).getChoiceNode());
		assertEquals(basicParameterNode1, parametersWithChoices.get(0).getBasicParameterNode());
	}

}
