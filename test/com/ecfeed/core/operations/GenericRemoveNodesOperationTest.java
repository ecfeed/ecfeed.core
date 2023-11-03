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
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.NodesByType;

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

		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		methodNode.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition("constraint", basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParametersWithContexts = new ArrayList<>();
		
		deployedParametersWithContexts.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParametersWithContexts.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParametersWithContexts);

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
		assertEquals(0, methodNode.getDeployedParameters().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(2, methodNode.getParameters().size());

		List<ConstraintNode> resultConstraintNodes = methodNode.getConstraintNodes();
		assertEquals(2, resultConstraintNodes.size());

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());
	}

	//	@Test
	//	public void basicLinkedParameterRemoveFromMethod() {
	//
	//		RootNode rootNode = new RootNode("Root", null);
	//
	//		final String parameterType = "int";
	//
	//		BasicParameterNode globalParameterNodeOfRoot1 = 
	//				RootNodeHelper.addGlobalBasicParameterToRoot(rootNode, "RP1", parameterType, null);
	//
	//		final String choiceValueString = "AB";
	//
	//		BasicParameterNodeHelper.addNewChoice(
	//				globalParameterNodeOfRoot1, "RC11", choiceValueString, false, null);
	//
	//
	//		ClassNode classNode = new ClassNode("Class", null);
	//		rootNode.addClass(classNode);
	//
	//		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);
	//
	//		BasicParameterNode localParameterNode = 
	//				ParametersAndConstraintsParentNodeHelper.addLinkedParameter(methodNode, "MP1", parameterType, globalParameterNodeOfRoot1);
	//
	//		List<IAbstractNode> listOfNodesToDelete = new ArrayList<>();
	//		listOfNodesToDelete.add(localParameterNode);
	//		
	//		GenericRemoveNodesProcessorOfNodes genericRemoveNodesProcessorOfNodes =
	//				new GenericRemoveNodesProcessorOfNodes(
	//						listOfNodesToDelete, new TypeAdapterProviderForJava(), true, new ExtLanguageManagerForJava());
	//
	//		NodesByType processedNodesToDelete = genericRemoveNodesProcessorOfNodes.getProcessedNodes();
	//		
	//		System.out.println(processedNodesToDelete);
	//		
	//		RootNode rootNode = new RootNode("Root", null);
	//		
	//		// global basic parameter with choice
	//
	//		BasicParameterNode globalBasicParameterNode = RootNodeHelper.addGlobalBasicParameterToRoot(rootNode, "GParam1", "String", null);
	//
	//		BasicParameterNodeHelper.addNewChoice(globalBasicParameterNode, "Choice1", "1", null);
	//		
	//		// class node 
	//
	//		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class", null); 
	//		
	//		// method node
	//
	//		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);
	//		
	//		// linked basic parameter 
	//
	//		BasicParameterNode localBasicParameterNode = 
	//				MethodNodeHelper.addNewBasicParameter(methodNode, "LocalBasicParam1", "String", "", null);
	//		localBasicParameterNode.setLinkToGlobalParameter(globalBasicParameterNode);
	//		
	//		// constraints
	//
	//		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
	//		methodNode.addConstraint(constraintNode1);
	//
	//		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
	//		methodNode.addConstraint(constraintNode2);
	//
	//		// test case
	//
	//		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
	//		MethodNodeHelper.addNewTestCase(methodNode, choicesOfTestCase);
	//
	//		// copy parameters to deployed parameters
	//
	//		List<BasicParameterNode> deployedParameters = new ArrayList<>();
	//		deployedParameters.add(basicParameterNode1);
	//		deployedParameters.add(basicParameterNode2);
	//		methodNode.setDeployedParameters(deployedParameters);
	//
	//		// initial checks 
	//		
	//		// list of nodes to delete
	//
	//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
	//		nodesToDelete.add(globalParameterNodeOfRoot1);
	//
	//		// remove
	//
	//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
	//				createRemovingNodesOperation(nodesToDelete, rootNode);
	//		genericRemoveNodesOperation.execute();
	//
	//		assertEquals(1, globalParameterNodeOfRoot1.getChoices().size());
	//		
	//		assertEquals(1, methodNode.getConstraintNodes().size());
	//
	//		assertEquals(0, methodNode.getTestCases().size());
	//		assertEquals(0, methodNode.getDeployedParameters().size());
	//
	//		// reverse
	//		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
	//		reverseOperation.execute();
	//
	//		assertEquals(2, methodNode.getParameters().size());
	//
	//		List<ConstraintNode> resultConstraintNodes = methodNode.getConstraintNodes();
	//		assertEquals(2, resultConstraintNodes.size());
	//
	//		assertEquals(1, methodNode.getTestCases().size());
	//		assertEquals(2, methodNode.getDeployedParameters().size());
	//	}

	@Test
	public void basicParameterRemoveFromLocalComposite() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method node

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// local composite
		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		methodNode.addParameter(compositeParameterNode1);

		// basic parameter 1 added to composite

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		compositeParameterNode1.addParameter(basicParameterNode1);

		// choice of basic parameter 1

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		// basic parameter 2

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		compositeParameterNode1.addParameter(basicParameterNode2);

		// choice of basic parameter 1

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
		basicParameterNode2.addChoice(choiceNode2);

		// constraints of composite

		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		compositeParameterNode1.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition("constraint", basicParameterNode2,"2");
		compositeParameterNode1.addConstraint(constraintNode2);

		// composite parameter 2 of method

		CompositeParameterNode compositeParameterNode2 = new CompositeParameterNode("S2", null);
		methodNode.addParameter(compositeParameterNode2);

		// constraints of method node use basic parameters form composite 1

		ConstraintNode constraintNode1m = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		methodNode.addConstraint(constraintNode1m);

		ConstraintNode constraintNode2m = createConstraintNodeWithValueCondition("constraint", basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2m);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

		// check created structure

		assertEquals(2, compositeParameterNode1.getParameters().size());
		assertEquals(2, compositeParameterNode1.getConstraintNodes().size());

		assertEquals(2, methodNode.getConstraintNodes().size());
		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());

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
		assertEquals(0, methodNode.getDeployedParameters().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		// the same as before remove 

		assertEquals(2, compositeParameterNode1.getParameters().size());
		assertEquals(2, compositeParameterNode1.getConstraintNodes().size());

		assertEquals(2, methodNode.getConstraintNodes().size());
		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());
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
				createConstraintNodeWithValueCondition("constraint", basicParameterNode1OfGlobalComposite,"GS11");

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

		ConstraintNode constraintNodeOnMethod = 
				createConstraintNodeWithValueCondition("constraint", basicParameterNode1OfGlobalComposite,"M1");
		methodNode.addConstraint(constraintNodeOnMethod);

		// constraint of composite parameter node 1

		ConstraintNode constraintNodeOnS1 = 
				createConstraintNodeWithValueCondition("constraint", basicParameterNode1OfGlobalComposite,"S1");
		compositeParameterNode1.addConstraint(constraintNodeOnS1);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1OfGlobalComposite, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2OfGlobalComposite, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

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
	public void choiceRemoveFromGlobalComposite() {

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
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1OfGlobalComposite, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2OfGlobalComposite, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

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
	public void choiceRemoveFromGlobalLinkedParameter() {

		RootNode rootNode = new RootNode("Root", null);

		// class node 

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// global basic parameter

		BasicParameterNode globalBasicParameterNode = 
				ClassNodeHelper.addNewBasicParameter(classNode, "GP1", "String", "", true, null);

		// global choice

		ChoiceNode globalChoiceNode = 
				BasicParameterNodeHelper.addNewChoice(
						globalBasicParameterNode, "GC1", "A", false, false, null);

		// method node

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "Method", true, null);

		// local parameter linked to global

		BasicParameterNode localBasicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "P1", "String", "0", true, null);
		localBasicParameterNode.setLinkToGlobalParameter(globalBasicParameterNode);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {globalChoiceNode});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// initial checks

		assertEquals(1, globalBasicParameterNode.getChoices().size());
		assertEquals(1, methodNode.getParameters().size());
		assertEquals(1, methodNode.getTestCases().size());

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(globalChoiceNode);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(0, globalBasicParameterNode.getChoices().size());
		assertEquals(1, methodNode.getParameters().size());
		assertEquals(0, methodNode.getTestCases().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, globalBasicParameterNode.getChoices().size());
		assertEquals(1, methodNode.getParameters().size());
		assertEquals(1, methodNode.getTestCases().size());
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

		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		compositeParameterNode2.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition("constraint", basicParameterNode2,"2");
		compositeParameterNode2.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

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
		assertEquals(0, methodNode.getDeployedParameters().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(2, compositeParameterNode2.getParameters().size());

		List<ConstraintNode> resultConstraintNodes = compositeParameterNode2.getConstraintNodes();
		assertEquals(2, resultConstraintNodes.size());

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());
	}

	@Test
	public void basicParameterGlobalRemove2() {

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

		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		methodNode.addConstraint(constraintNode1);

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition("constraint", basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

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
		assertEquals(2, methodNode.getDeployedParameters().size());
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
		assertEquals(0, methodNode.getDeployedParameters().size());

		// reverse

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		// checks for initial state

		assertEquals(1, rootNode.getParameters().size());
		assertEquals(1, rootNode.getClasses().size());

		assertEquals(1, classNode.getMethods().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, methodNode.getDeployedParameters().size());
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

		// constraint 1 of composite parameter node 11

		ConstraintNode constraint1NodeOnGlobalS11 = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		globalCompositeParameterNode11.addConstraint(constraint1NodeOnGlobalS11);

		// constraint 2 of composite parameter node 11

		ConstraintNode constraint2NodeOnGlobalS11 = createConstraintNodeWithValueCondition("constraint", basicParameterNode2,"1");
		globalCompositeParameterNode11.addConstraint(constraint2NodeOnGlobalS11);

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

		// constraint 1 of method

		ConstraintNode constraint1NodeOnMethod = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		methodNode.addConstraint(constraint1NodeOnMethod);

		// constraint 2 of method

		ConstraintNode constraint2NodeOnMethod = createConstraintNodeWithValueCondition("constraint", basicParameterNode2,"1");
		methodNode.addConstraint(constraint2NodeOnMethod);

		// constraint 1of composite parameter node 1

		ConstraintNode constraint1NodeOnS1 = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		compositeParameterNode1.addConstraint(constraint1NodeOnS1);

		// constraint of composite parameter node 1

		ConstraintNode constraint2NodeOnS1 = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
		compositeParameterNode1.addConstraint(constraint2NodeOnS1);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		//		Root
		//		GS11
		//			P1
		//				Choice1
		//			P2
		//				Choice2
		//			constraint(P1=value)
		//			constraint(P2=value)
		//		Class
		//			Method
		//				S1
		//					S11 -> GS11
		//					constraint(P1=value)
		//					constraint(P2=value)
		//				S3
		//				constraint(P1=value)
		//				constraint(P2=value)
		//				testCase

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

		// initial checks

		assertEquals(1, rootNode.getParameters().size());

		assertEquals(2, globalCompositeParameterNode11.getParameters().size());
		assertEquals(2, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, methodNode.getConstraints().size());
		assertEquals(1, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(2, compositeParameterNode1.getConstraints().size());

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(globalCompositeParameterNode11);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation =
				createRemovingNodesOperation(nodesToDelete, rootNode);

		// check generated operations

		List<IModelOperation> operations = genericRemoveNodesOperation.getOperations();
		assertEquals(9, operations.size()); // 2 constraints, 1 test case, 2 parameters, 2 composites

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
		assertEquals(2, globalCompositeParameterNode11.getConstraints().size());

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, methodNode.getConstraints().size());
		assertEquals(1, methodNode.getTestCases().size());

		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(2, compositeParameterNode1.getConstraints().size());
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

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition("constraint2", basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode11, choiceNode21});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(choiceNode11);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, basicParameterNode1.getChoiceCount());

		List<ConstraintNode> constraintNodes = methodNode.getConstraintNodes(); 
		assertEquals(1, constraintNodes.size());
		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());

		// reverse operation

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(2, basicParameterNode1.getChoiceCount());

		assertEquals(2, methodNode.getConstraintNodes().size());

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());
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

		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition("constraint", basicParameterNode2,"2");
		methodNode.addConstraint(constraintNode2);

		// test case

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode11, choiceNode21});
		MethodNodeHelper.addNewTestCase(methodNode, "ts", choicesOfTestCase, true);

		// copy parameters to deployed parameters

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

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
		assertEquals(2, methodNode.getDeployedParameters().size());

		// reverse operation

		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, methodNode.getParameters().size());
		assertEquals(2, basicParameterNode1.getChoiceCount());

		assertEquals(2, methodNode.getConstraintNodes().size());

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());
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

		ConstraintNode constraintNode = createConstraintNodeWithValueCondition("constraint", basicParameterNode1,"1");
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

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

		// list of nodes to delete

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(testCaseNode);

		// remove

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		assertEquals(0, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(2, methodNode.getDeployedParameters().size());
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

		List<ParameterWithLinkingContext> deployedParameters = new ArrayList<>();
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode1, null));
		deployedParameters.add(new ParameterWithLinkingContext(basicParameterNode2, null));
		methodNode.setDeployedParametersWithContexts(deployedParameters);

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
		assertEquals(2, methodNode.getDeployedParameters().size());

		// reverse
		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
		reverseOperation.execute();

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(1, methodNode.getTestSuites().size());
		assertEquals(2, methodNode.getDeployedParameters().size());
	}

	private ConstraintNode 
	createConstraintNodeWithValueCondition(
			String constraintName,
			BasicParameterNode basicParameterNode, 
			String value) {

		StaticStatement staticStatement = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithValueCondition(
						basicParameterNode, null, EMathRelation.EQUAL, value);

		Constraint constraint = new Constraint(
				constraintName, 
				ConstraintType.EXTENDED_FILTER, 
				staticStatement, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode(constraintName, constraint, null);
		return constraintNode;
	}

	private ConstraintNode createConstraintNodeWithChoiceCondition(
			BasicParameterNode basicParameterNode, ChoiceNode choiceNode) {

		StaticStatement staticStatement = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, EMathRelation.EQUAL, choiceNode);

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

		GenericRemoveNodesProcessorOfNodes genericRemoveNodesProcessorOfNodes =
				new GenericRemoveNodesProcessorOfNodes(
						nodesToDelete, true, new ExtLanguageManagerForJava());

		NodesByType processedNodesToDelete = genericRemoveNodesProcessorOfNodes.getProcessedNodes();

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				new GenericRemoveNodesOperation(
						processedNodesToDelete,
						true, 
						nodeToBeSelectedAfterOperation, 
						nodeToBeSelectedAfterOperation, 
						new ExtLanguageManagerForJava());

		return genericRemoveNodesOperation;
	}

}
