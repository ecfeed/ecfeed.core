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
import com.ecfeed.core.model.utils.BasicParameterWithString;
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

		// root
		//   class
		//     method
		//       str1
		//         par1
		//           choice1
		//         constraint (child of str1)

		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						compositeParameterNode);

		assertEquals(1, parametersWithChoices.size());

		assertEquals(choiceNode1, parametersWithChoices.get(0).getChoiceNode());
		assertEquals(basicParameterNode1, parametersWithChoices.get(0).getBasicParameterNode());
	}

	@Test
	public void getParametersWithChoicesForCompositeParameterTest2() {

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

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		//   class
		//     method
		//       str1
		//         par1
		//           choice1
		//       constraint (child of method)

		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						compositeParameterNode);

		assertEquals(1, parametersWithChoices.size());

		assertEquals(choiceNode1, parametersWithChoices.get(0).getChoiceNode());
		assertEquals(basicParameterNode1, parametersWithChoices.get(0).getBasicParameterNode());
	}

	@Test
	public void getParametersWithChoicesForCompositeParameterTest3() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "method", true, null);

		CompositeParameterNode compositeParameterNode1 = 
				MethodNodeHelper.addNewCompositeParameter(methodNode, "str1", true, null);

		CompositeParameterNode compositeParameterNode2 = 
				CompositeParameterNodeHelper.addNewCompositeParameter(compositeParameterNode1, "str2", true, null);

		BasicParameterNode basicParameterNode1 =
				CompositeParameterNodeHelper.addNewBasicParameter(
						compositeParameterNode2, "par1", "String", "", true, null);

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

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		//   class
		//     method
		//       str1
		//         str2
		//           par1
		//             choice1
		//       constraint (child of method)

		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						compositeParameterNode1);

		assertEquals(1, parametersWithChoices.size());

		assertEquals(choiceNode1, parametersWithChoices.get(0).getChoiceNode());
		assertEquals(basicParameterNode1, parametersWithChoices.get(0).getBasicParameterNode());
	}

	@Test
	public void getParametersWithChoicesForCompositeParameterTest4() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "method", true, null);

		CompositeParameterNode compositeParameterNode1 = 
				MethodNodeHelper.addNewCompositeParameter(methodNode, "str1", true, null);

		CompositeParameterNode compositeParameterNode2 = 
				CompositeParameterNodeHelper.addNewCompositeParameter(compositeParameterNode1, "str2", true, null);

		BasicParameterNode basicParameterNode1 =
				CompositeParameterNodeHelper.addNewBasicParameter(
						compositeParameterNode2, "par1", "String", "", true, null);

		BasicParameterNodeHelper.addNewChoice(basicParameterNode1, "choice1", "c1", false, true, null);

		ChoiceNode choiceNode2 =
				BasicParameterNodeHelper.addNewChoice(basicParameterNode1, "choice2", "c2", false, true, null);

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				precondition, 
				postcondition, 
				null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		//   class
		//     method
		//       str1
		//         str2
		//           par1
		//             choice1
		//             choice2
		//       constraint with choice2 (child of method)

		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						compositeParameterNode1);

		assertEquals(1, parametersWithChoices.size());

		assertEquals(choiceNode2, parametersWithChoices.get(0).getChoiceNode());
		assertEquals(basicParameterNode1, parametersWithChoices.get(0).getBasicParameterNode());
	}

	@Test
	public void getParametersWithChoicesForCompositeParameterTest5() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "method", true, null);

		CompositeParameterNode compositeParameterNode1 = 
				MethodNodeHelper.addNewCompositeParameter(methodNode, "str1", true, null);

		CompositeParameterNode compositeParameterNode2 = 
				CompositeParameterNodeHelper.addNewCompositeParameter(compositeParameterNode1, "str2", true, null);

		BasicParameterNode basicParameterNode1 =
				CompositeParameterNodeHelper.addNewBasicParameter(
						compositeParameterNode2, "par1", "String", "", true, null);

		ChoiceNode choiceNode1 =
				BasicParameterNodeHelper.addNewChoice(basicParameterNode1, "choice1", "c1", false, true, null);

		BasicParameterNode basicParameterNode2 =
				CompositeParameterNodeHelper.addNewBasicParameter(
						compositeParameterNode2, "par2", "String", "", true, null);

		ChoiceNode choiceNode2 =
				BasicParameterNodeHelper.addNewChoice(basicParameterNode2, "choice2", "c2", false, true, null);

		RelationStatement precondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode1, null, EMathRelation.EQUAL, choiceNode1);

		RelationStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode2, null, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				precondition, 
				postcondition, 
				null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		//   class
		//     method
		//       str1
		//         str2
		//           par1
		//             choice1
		//           par2
		//             choice2
		//       constraint with choice1 and choice2 (child of method)

		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						compositeParameterNode1);

		assertEquals(2, parametersWithChoices.size());

		BasicParameterWithChoice basicParameterWithChoice1 = parametersWithChoices.get(0);
		assertEquals(choiceNode1, basicParameterWithChoice1.getChoiceNode());
		assertEquals(basicParameterNode1, basicParameterWithChoice1.getBasicParameterNode());

		BasicParameterWithChoice basicParameterWithChoice2 = parametersWithChoices.get(1);
		assertEquals(choiceNode2, basicParameterWithChoice2.getChoiceNode());
		assertEquals(basicParameterNode2, basicParameterWithChoice2.getBasicParameterNode());
	}

	@Test
	public void getParametersWithLabelsForBasicParameterTest1() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "method", true, null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "String", "", true, null);

		ChoiceNode choice1 = 
				BasicParameterNodeHelper.addNewChoice(basicParameterNode, "choice1", "c1", false, true, null);

		String label1 = "label1";
		choice1.addLabel(label1);

		String label2 = "label2";
		choice1.addLabel(label2);


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
				RelationStatement.createRelationStatementWithLabelCondition(
						basicParameterNode, null, EMathRelation.EQUAL, label1);

		ConstraintsParentNodeHelper.addNewConstraintNode(
				methodNode,
				"constraint",
				ConstraintType.EXTENDED_FILTER,
				precondition2, postcondition2,
				true, null); 		

		// root
		//   class
		//     method
		//       par1
		//         choice1 with label1 and label2

		List<BasicParameterWithString> parametersWithLabels =
				ParametersAndConstraintsParentNodeHelper.getParametersWithLabelsUsedInConstraintsForLocalTopParameter(
						basicParameterNode);

		assertEquals(1, parametersWithLabels.size());
		BasicParameterWithString parameterWithLabel = parametersWithLabels.get(0);
		assertEquals(label1, parameterWithLabel.getStr());
	}

}
