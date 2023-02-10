/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class GenericRemoveNodesOperationTest {

	@Test
	public void classWithoutChildrenRemove() {

		RootNode rootNode = new RootNode("Root", null);
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(classNode);

		GenericRemoveNodesOperation genericRemoveNodesOperation = createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		List<ClassNode> classNodes = rootNode.getClasses();

		assertTrue(classNodes.isEmpty());

		genericRemoveNodesOperation.getReverseOperation().execute();
		classNodes = rootNode.getClasses();

		assertEquals(1, classNodes.size());
		assertEquals(classNode, classNodes.get(0));
	}

	// TODO MO-RE remove class with method and parameter

	@Test
	public void methodsRemove() {

		RootNode rootNode = new RootNode("Root", null);
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		MethodNode methodNode1 = new MethodNode("Method1");
		classNode.addMethod(methodNode1);
		MethodNode methodNode2 = new MethodNode("Method2");
		classNode.addMethod(methodNode2);

		// removing the first method

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(methodNode1);

		GenericRemoveNodesOperation genericRemoveNodesOperation1 = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation1.execute();

		assertEquals(1, classNode.getMethods().size());

		nodesToDelete.clear();
		nodesToDelete.add(methodNode2);

		GenericRemoveNodesOperation genericRemoveNodesOperation2 = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation2.execute();

		assertEquals(0, classNode.getMethods().size());

		genericRemoveNodesOperation2.getReverseOperation().execute();
		assertEquals(1, classNode.getMethods().size());

		genericRemoveNodesOperation1.getReverseOperation().execute();
		assertEquals(2, classNode.getMethods().size());
	}

	@Test
	public void basicParameterRemoveFromMethod() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// basic parameters and choices 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		methodNode.addParameter(basicParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2.addChoice(choiceNode2);

		// constraints

		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		methodNode.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(basicParameterNode1);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(1, methodNode.getParameters().size());
		assertEquals(1, methodNode.getConstraintNodes().size());

		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(0, methodNode.getDeployedMethodParameters().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(2, methodNode.getParameters().size());

		List<ConstraintNode> resultConstraintNodes = methodNode.getConstraintNodes();
		assertEquals(2, resultConstraintNodes.size());

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
	}

	@Test
	public void basicParameterRemoveFromLocalComposite() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		methodNode.addParameter(compositeParameterNode1);

		// basic parameter 1 added to composite

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		compositeParameterNode1.addParameter(basicParameterNode1);

		// choices of basic parameter 1

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		compositeParameterNode1.addParameter(basicParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2.addChoice(choiceNode2);

		// constraints of composite

		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		compositeParameterNode1.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
		compositeParameterNode1.addConstraint(constraintNode2);

		// basic parameter 2 of method

		CompositeParameterNode compositeParameterNode2 = new CompositeParameterNode("S2", null);
		methodNode.addParameter(compositeParameterNode2);

		// constraints of method node use basic parameters form composite 1

		ConstraintNode constraintNode1m = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		methodNode.addConstraint(constraintNode1m);

		ConstraintNode constraintNode2m = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2m);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// check created structure

		assertEquals(2, compositeParameterNode1.getParameters().size());
		assertEquals(2, compositeParameterNode1.getConstraintNodes().size());

		assertEquals(2, methodNode.getConstraintNodes().size());
		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(basicParameterNode1);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(1, compositeParameterNode1.getConstraintNodes().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getConstraintNodes().size());
		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(0, methodNode.getDeployedMethodParameters().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		// the same as before remove 

		assertEquals(2, compositeParameterNode1.getParameters().size());
		assertEquals(2, compositeParameterNode1.getConstraintNodes().size());

		assertEquals(2, methodNode.getConstraintNodes().size());
		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
	}

	@Test
	public void basicParameterRemoveFromGlobalComposite() {

		RootNode rootNode = new RootNode("Root", null);

		// global composite

		CompositeParameterNode globalCompositeParameterNode11 = new CompositeParameterNode("GS11", null);
		rootNode.addParameter(globalCompositeParameterNode11);

		// basic parameters and choices of global composite

		BasicParameterNode basicParameterNode1OfGlobalComposite = 
				new BasicParameterNode("P1", "String", "", false, null);
		globalCompositeParameterNode11.addParameter(basicParameterNode1OfGlobalComposite);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1OfGlobalComposite.addChoice(choiceNode1);

		BasicParameterNode basicParameterNode2OfGlobalComposite = 
				new BasicParameterNode("P2", "String", "", false, null);
		globalCompositeParameterNode11.addParameter(basicParameterNode2OfGlobalComposite);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2OfGlobalComposite.addChoice(choiceNode2);

		// constraint of composite parameter node 11

		ConstraintNode constraintNodeOnGlobalS11 = 
				createConstraintNodeWithValueCondition(basicParameterNode1OfGlobalComposite,"GS11");

		globalCompositeParameterNode11.addConstraint(constraintNodeOnGlobalS11);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// composite of method

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		methodNode.addParameter(compositeParameterNode1);

		// linked composite 11 of composite 1 

		CompositeParameterNode compositeParameterNode11 = new CompositeParameterNode("S11", null);
		compositeParameterNode11.setLinkToGlobalParameter(globalCompositeParameterNode11);
		compositeParameterNode1.addParameter(compositeParameterNode11);

		// additional structure on method level

		CompositeParameterNode compositeParameterNode3 = new CompositeParameterNode("S3", null);
		methodNode.addParameter(compositeParameterNode3);

		// constraint of method

		ConstraintNode constraintNodeOnMethod = createConstraintNodeWithValueCondition(basicParameterNode1OfGlobalComposite,"M1");
		methodNode.addConstraint(constraintNodeOnMethod);

		// constraint of composite parameter node 1

		ConstraintNode constraintNodeOnS1 = createConstraintNodeWithValueCondition(basicParameterNode1OfGlobalComposite,"S1");
		compositeParameterNode1.addConstraint(constraintNodeOnS1);

		// test case 

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1OfGlobalComposite);
		deployedParameters.add(basicParameterNode2OfGlobalComposite);
		methodNode.setDeployedParameters(deployedParameters);

		// initial checks

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, rootNode.getClasses().size());

		assertEquals(2, globalCompositeParameterNode11.getParameters().size());
		assertEquals(1, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getConstraints().size());
		assertEquals(1, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(1, compositeParameterNode1.getConstraints().size());

		//		Root
		//		GS11
		//			P1
		//				Choice1
		//			P2
		//				Choice2
		//			constraintGS11(P1)
		//		Class
		//			Method
		//				S1
		//					S11 -> GS11
		//					constraintS1(P1)
		//				S2
		//				constraintM1(P1)
		//				testCase

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(basicParameterNode1OfGlobalComposite);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);

		// check generated operations

		List<IModelOperation> operations = genericRemoveNodesOperation.getOperations();
		assertEquals(5, operations.size()); // 3 constraints, 1 test case, 1 parameter

		genericRemoveNodesOperation.execute();

		// checks after remove

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, globalCompositeParameterNode11.getParameters().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(0, methodNode.getConstraints().size());
		assertEquals(0, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(0, compositeParameterNode1.getConstraints().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		// checks after reverse

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, rootNode.getClasses().size());

		assertEquals(2, globalCompositeParameterNode11.getParameters().size());
		assertEquals(1, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getConstraints().size());
		assertEquals(1, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(1, compositeParameterNode1.getConstraints().size());
	}

	@Test
	public void AAchoiceRemoveFromGlobalComposite() {

		RootNode rootNode = new RootNode("Root", null);

		// global composite

		CompositeParameterNode globalCompositeParameterNode11 = new CompositeParameterNode("GS11", null);
		rootNode.addParameter(globalCompositeParameterNode11);

		// basic parameters and choices of global composite

		BasicParameterNode basicParameterNode1OfGlobalComposite = 
				new BasicParameterNode("P1", "String", "", false, null);
		globalCompositeParameterNode11.addParameter(basicParameterNode1OfGlobalComposite);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1OfGlobalComposite.addChoice(choiceNode1);

		BasicParameterNode basicParameterNode2OfGlobalComposite = 
				new BasicParameterNode("P2", "String", "", false, null);
		globalCompositeParameterNode11.addParameter(basicParameterNode2OfGlobalComposite);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2OfGlobalComposite.addChoice(choiceNode2);

		// constraints of composite parameter node 11

		ConstraintNode constraintNode1OnGlobalS11 = 
				createConstraintNodeWithChoiceCondition(basicParameterNode1OfGlobalComposite, choiceNode1);

		globalCompositeParameterNode11.addConstraint(constraintNode1OnGlobalS11);

		ConstraintNode constraintNode2OnGlobalS11 = 
				createConstraintNodeWithChoiceCondition(basicParameterNode2OfGlobalComposite,choiceNode2);

		globalCompositeParameterNode11.addConstraint(constraintNode2OnGlobalS11);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// composite of method

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		methodNode.addParameter(compositeParameterNode1);

		// linked composite 11 of composite 1 

		CompositeParameterNode compositeParameterNode11 = new CompositeParameterNode("S11", null);
		compositeParameterNode11.setLinkToGlobalParameter(globalCompositeParameterNode11);
		compositeParameterNode1.addParameter(compositeParameterNode11);

		// additional structure on method level

		CompositeParameterNode compositeParameterNode3 = new CompositeParameterNode("S3", null);
		methodNode.addParameter(compositeParameterNode3);

		// constraint of method

		ConstraintNode constraintNode1OnMethod = 
				createConstraintNodeWithChoiceCondition(basicParameterNode1OfGlobalComposite, choiceNode1);

		methodNode.addConstraint(constraintNode1OnMethod);

		ConstraintNode constraintNode2OnMethod = 
				createConstraintNodeWithChoiceCondition(basicParameterNode2OfGlobalComposite, choiceNode2);

		methodNode.addConstraint(constraintNode2OnMethod);

		// constraint of composite parameter node 1

		ConstraintNode constraintNode1OnS1 = 
				createConstraintNodeWithChoiceCondition(basicParameterNode1OfGlobalComposite, choiceNode1);

		compositeParameterNode1.addConstraint(constraintNode1OnS1);

		ConstraintNode constraintNode2OnS1 = 
				createConstraintNodeWithChoiceCondition(basicParameterNode2OfGlobalComposite, choiceNode2);

		compositeParameterNode1.addConstraint(constraintNode2OnS1);

		// test case 

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1OfGlobalComposite);
		deployedParameters.add(basicParameterNode2OfGlobalComposite);
		methodNode.setDeployedParameters(deployedParameters);

		// initial checks

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, rootNode.getClasses().size());

		assertEquals(2, globalCompositeParameterNode11.getParameters().size());
		assertEquals(2, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(1, basicParameterNode1OfGlobalComposite.getChoices().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, methodNode.getConstraints().size());
		assertEquals(1, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(2, compositeParameterNode1.getConstraints().size());

		//		Root
		//		GS11
		//			P1
		//				Choice1
		//			P2
		//				Choice2
		//			constraintGS11(P1=Choice1)
		//			constraintGS11(P2=Choice2)
		//		Class
		//			Method
		//				S1
		//					S11 -> GS11
		//					constraintS11(P1=Choice1)
		//					constraintS11(P2=Choice2)
		//				S2
		//				constraintM1(P1=Choice1)
		//				constraintM1(P2=Choice2)
		//				testCase

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(choiceNode1);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);

		// check generated operations

		List<IModelOperation> operations = genericRemoveNodesOperation.getOperations();
		assertEquals(5, operations.size()); // 3 constraints, 1 test case, 1 choice

		genericRemoveNodesOperation.execute();

		// checks after remove

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, rootNode.getClasses().size());

		assertEquals(2, globalCompositeParameterNode11.getParameters().size());
		assertEquals(1, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(0, basicParameterNode1OfGlobalComposite.getChoices().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getConstraints().size());
		assertEquals(0, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(1, compositeParameterNode1.getConstraints().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		// checks after reverse

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, rootNode.getClasses().size());

		assertEquals(2, globalCompositeParameterNode11.getParameters().size());
		assertEquals(2, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(1, basicParameterNode1OfGlobalComposite.getChoices().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, methodNode.getConstraints().size());
		assertEquals(1, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(2, compositeParameterNode1.getConstraints().size());
	}

	@Test
	public void basicParameterRemoveFromNestedStructure() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// structures

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		methodNode.addParameter(compositeParameterNode1);

		CompositeParameterNode compositeParameterNode2 = new CompositeParameterNode("S2", null);
		compositeParameterNode1.addParameter(compositeParameterNode2);

		// basic parameters and choices 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		compositeParameterNode2.addParameter(basicParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		compositeParameterNode2.addParameter(basicParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2.addChoice(choiceNode2);

		// constraints

		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		compositeParameterNode2.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
		compositeParameterNode2.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(basicParameterNode1);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(1, compositeParameterNode2.getParameters().size());
		assertEquals(1, compositeParameterNode2.getConstraintNodes().size());

		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(0, methodNode.getDeployedMethodParameters().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(2, compositeParameterNode2.getParameters().size());

		List<ConstraintNode> resultConstraintNodes = compositeParameterNode2.getConstraintNodes();
		assertEquals(2, resultConstraintNodes.size());

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
	}

	@Test
	public void basicParameterGlobalRemove() {

		RootNode rootNode = new RootNode("Root", null);

		// global parameter

		BasicParameterNode globalBasicParameterNode1 = 
				new BasicParameterNode(
						"GlobalBasicParam1", "String", "", false, null);
		rootNode.addParameter(globalBasicParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		globalBasicParameterNode1.addChoice(choiceNode1);

		// class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// linked parameter

		BasicParameterNode basicParameterNode1 =
				new BasicParameterNode(
						"BasicParam1",
						"String",
						"",
						false,
						globalBasicParameterNode1,
						null);

		methodNode.addParameter(basicParameterNode1);

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		methodNode.addParameter(basicParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2.addChoice(choiceNode2);

		// constraints

		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		methodNode.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// 	Root
		// 		GlobalBasicParam1
		// 			Choice1
		//		Class
		//			Method
		//				BasicParam1 -> GlobalBasicParam1
		//				BasicParam2
		//					Choice2
		//			constraint( BasicParam1 = 1)
		//			constraint( BasicParam2 = 2)
		//			testCase

		// initial checks

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, rootNode.getClasses().size());

		assertEquals(1, classNode.getMethods().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
		assertEquals(2, methodNode.getConstraintNodes().size());
		assertEquals(1, methodNode.getTestCases().size());

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(globalBasicParameterNode1);

		// remove operation

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);

		assertEquals(4, genericRemoveNodesOperation.getOperations().size());

		genericRemoveNodesOperation.execute();

		assertEquals(1, methodNode.getParameters().size());
		assertEquals(1, methodNode.getConstraintNodes().size());

		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(0, methodNode.getDeployedMethodParameters().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		// checks for initial state

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, rootNode.getClasses().size());

		assertEquals(1, classNode.getMethods().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
		assertEquals(2, methodNode.getConstraintNodes().size());
		assertEquals(1, methodNode.getTestCases().size());
	}

	@Test
	public void compositeParameterGlobalRemoveTrivialCase() {

		RootNode rootNode = new RootNode("Root", null);

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		rootNode.addParameter(compositeParameterNode1);

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(compositeParameterNode1);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(0, rootNode.getGlobalCompositeParameters().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, rootNode.getGlobalCompositeParameters().size());

	}

	@Test
	public void compositeParameterGlobalRemove() {

		RootNode rootNode = new RootNode("Root", null);

		// global composite

		CompositeParameterNode globalCompositeParameterNode11 = new CompositeParameterNode("GS11", null);
		rootNode.addParameter(globalCompositeParameterNode11);

		// basic parameters and choices of global composite

		BasicParameterNode basicParameterNode1 = new BasicParameterNode("P1", "String", "", false, null);
		globalCompositeParameterNode11.addParameter(basicParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		BasicParameterNode basicParameterNode2 = new BasicParameterNode("P2", "String", "", false, null);
		globalCompositeParameterNode11.addParameter(basicParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2.addChoice(choiceNode2);

		// constraint of composite parameter node 11

		ConstraintNode constraintNodeOnGlobalS11 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		globalCompositeParameterNode11.addConstraint(constraintNodeOnGlobalS11);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// composite of method

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		methodNode.addParameter(compositeParameterNode1);

		// linked composite 11 of composite 1 

		CompositeParameterNode compositeParameterNode11 = new CompositeParameterNode("S11", null);
		compositeParameterNode11.setLinkToGlobalParameter(globalCompositeParameterNode11);
		compositeParameterNode1.addParameter(compositeParameterNode11);

		// additional structure on method level

		CompositeParameterNode compositeParameterNode3 = new CompositeParameterNode("S3", null);
		methodNode.addParameter(compositeParameterNode3);

		// constraint of method

		ConstraintNode constraintNodeOnMethod = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		methodNode.addConstraint(constraintNodeOnMethod);

		// constraint of composite parameter node 1

		ConstraintNode constraintNodeOnS1 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		compositeParameterNode1.addConstraint(constraintNodeOnS1);

		// constraint of composite parameter node 11

		ConstraintNode constraintNodeOnS11 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		compositeParameterNode11.addConstraint(constraintNodeOnS11);

		// test case 

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// initial checks

		assertEquals(1, rootNode.getParameters().size());

		assertEquals(2, globalCompositeParameterNode11.getParameters().size());
		assertEquals(1, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getConstraints().size());
		assertEquals(1, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(1, compositeParameterNode1.getConstraints().size());

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(globalCompositeParameterNode11);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);

		// check generated operations

		List<IModelOperation> operations = genericRemoveNodesOperation.getOperations();
		assertEquals(7, operations.size()); // 2 constraints, 1 test case, 2 parameters, 2 composites

		genericRemoveNodesOperation.execute();

		// checks after remove

		assertEquals(0, rootNode.getParameters().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(0, methodNode.getConstraints().size());
		assertEquals(0, methodNode.getTestCases().size());

		assertEquals(0, compositeParameterNode1.getParameters().size());
		assertEquals(0, compositeParameterNode1.getConstraints().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, rootNode.getParameters().size());

		assertEquals(2, globalCompositeParameterNode11.getParameters().size());
		assertEquals(1, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getConstraints().size());
		assertEquals(1, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(1, compositeParameterNode1.getConstraints().size());
	}

	@Test
	public void choiceNodeRemoveFromMethodBasicParameter() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// basic parameters and choices 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("Choice11", "11");
		basicParameterNode1.addChoice(choiceNode11);

		ChoiceNode choiceNode12 = new ChoiceNode("Choice12", "12");
		basicParameterNode1.addChoice(choiceNode12);

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		methodNode.addParameter(basicParameterNode2);

		ChoiceNode choiceNode21 = new ChoiceNode("Choice21", "21");
		basicParameterNode2.addChoice(choiceNode21);

		// constraints

		ConstraintNode constraintNode1 = createConstraintNodeWithChoiceCondition(basicParameterNode1, choiceNode11);
		methodNode.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode11, choiceNode21});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(choiceNode11);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, basicParameterNode1.getChoiceCount());

		assertEquals(1, methodNode.getConstraintNodes().size());
		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());

		// reverse operation

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, basicParameterNode1.getChoiceCount());

		assertEquals(2, methodNode.getConstraintNodes().size());

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
	}

	@Test
	public void choiceNodeRemoveFromCompositeParameter() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// composite parameter node

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		methodNode.addParameter(compositeParameterNode1);

		// basic parameters of composite parameter and choices 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		compositeParameterNode1.addParameter(basicParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("Choice11", "11");
		basicParameterNode1.addChoice(choiceNode11);

		ChoiceNode choiceNode12 = new ChoiceNode("Choice12", "12");
		basicParameterNode1.addChoice(choiceNode12);

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		compositeParameterNode1.addParameter(basicParameterNode2);

		ChoiceNode choiceNode21 = new ChoiceNode("Choice21", "21");
		basicParameterNode2.addChoice(choiceNode21);

		// constraints

		ConstraintNode constraintNode1 = createConstraintNodeWithChoiceCondition(basicParameterNode1, choiceNode11);
		methodNode.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode11, choiceNode21});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(choiceNode11);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(1, methodNode.getParameters().size());
		assertEquals(2, compositeParameterNode1.getParameters().size());

		assertEquals(1, basicParameterNode1.getChoiceCount());

		assertEquals(1, methodNode.getConstraintNodes().size());
		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());

		// reverse operation

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, methodNode.getParameters().size());
		assertEquals(2, basicParameterNode1.getChoiceCount());

		assertEquals(2, methodNode.getConstraintNodes().size());

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
	}

	@Test
	public void constraintRemoveFromMethod() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// basic parameters and choices 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		// constraints

		ConstraintNode constraintNode = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
		methodNode.addConstraint(constraintNode);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(constraintNode);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(0, methodNode.getConstraintNodes().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, methodNode.getConstraintNodes().size());
	}

	@Test
	public void testCasesRemove() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// basic parameters and choices 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		methodNode.addParameter(basicParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2.addChoice(choiceNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(testCaseNode);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
	}

	@Test
	public void testSuitesRemove() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// basic parameters and choices 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		methodNode.addParameter(basicParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2.addChoice(choiceNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		String testSuiteName = "TestSuite";
		TestCaseNode testCaseNode = new TestCaseNode(testSuiteName, null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// copy parameters to deployed parameters

		List<BasicParameterNode> deployedParameters = new ArrayList<>();
		deployedParameters.add(basicParameterNode1);
		deployedParameters.add(basicParameterNode2);
		methodNode.setDeployedParameters(deployedParameters);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		TestSuiteNode testSuiteNode = methodNode.findTestSuite(testSuiteName);
		nodesToDelete.add(testSuiteNode);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(0, methodNode.getTestSuites().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(1, methodNode.getTestSuites().size());
		assertEquals(2, methodNode.getDeployedMethodParameters().size());
	}

	private ConstraintNode createConstraintNodeWithValueCondition(
			BasicParameterNode basicParameterNode, String value) {

		StaticStatement staticStatement = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithValueCondition(
						basicParameterNode, EMathRelation.EQUAL, value);

		Constraint constraint = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				staticStatement, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode("constraintNode", constraint, null);
		return constraintNode;
	}

	private ConstraintNode createConstraintNodeWithChoiceCondition(
			BasicParameterNode basicParameterNode, ChoiceNode choiceNode) {

		StaticStatement staticStatement = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, EMathRelation.EQUAL, choiceNode);

		Constraint constraint = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				staticStatement, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode("constraintNode", constraint, null);
		return constraintNode;
	}

	private GenericRemoveNodesOperation createRemovingNodesOperation(
			List<IAbstractNode> nodesToDelete, 
			IAbstractNode nodeToBeSelectedAfterOperation) {

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				new GenericRemoveNodesOperation(
						nodesToDelete, 
						new TypeAdapterProviderForJava(), 
						true, 
						nodeToBeSelectedAfterOperation, 
						nodeToBeSelectedAfterOperation, 
						new ExtLanguageManagerForJava());

		return genericRemoveNodesOperation;
	}

}
