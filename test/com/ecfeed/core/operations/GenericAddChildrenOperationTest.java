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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class GenericAddChildrenOperationTest {

	@Test
	public void addClass() {

		RootNode rootNode = new RootNode("Root", null);
		
		// add class node 1
		
		ClassNode classNode1 = new ClassNode("Class1", null);

		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(rootNode, classNode1, 0 );
		genericAddChildrenOperation1.execute();

		List<ClassNode> classNodes = rootNode.getClasses();
		assertEquals(1, classNodes.size());
		
		// add class node 2
		
		ClassNode classNode2 = new ClassNode("Class2", null);

		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(rootNode, classNode2, 0 );
		genericAddChildrenOperation2.execute();
		
		classNodes = rootNode.getClasses();
		assertEquals(2, classNodes.size());
		
		// add class node 3

		ClassNode classNode3 = new ClassNode("Class3", null);

		GenericAddChildrenOperation genericAddChildrenOperation3 = 
				createAddingNodeOperation(rootNode, classNode3, 2 );
		genericAddChildrenOperation3.execute();
		
		classNodes = rootNode.getClasses();
		assertEquals(3, classNodes.size());
		
		// reverse operation 3
		
		genericAddChildrenOperation3.getReverseOperation().execute();
		classNodes = rootNode.getClasses();
		assertEquals(2, classNodes.size());
		
		// reverse operation 2
		
		genericAddChildrenOperation2.getReverseOperation().execute();
		classNodes = rootNode.getClasses();
		assertEquals(1, classNodes.size());

		// reverse operation 1
		
		genericAddChildrenOperation1.getReverseOperation().execute();
		classNodes = rootNode.getClasses();
		assertEquals(0, classNodes.size());
	}

//	// TODO MO-RE remove class with method and parameter
//
//	@Test
//	public void removeMethods() {
//
//		RootNode rootNode = new RootNode("Root", null);
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		MethodNode methodNode1 = new MethodNode("Method1");
//		classNode.addMethod(methodNode1);
//		MethodNode methodNode2 = new MethodNode("Method2");
//		classNode.addMethod(methodNode2);
//
//		// removing the first method
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(methodNode1);
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation1 = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation1.execute();
//
//		assertEquals(1, classNode.getMethods().size());
//
//		nodesToDelete.clear();
//		nodesToDelete.add(methodNode2);
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation2 = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation2.execute();
//
//		assertEquals(0, classNode.getMethods().size());
//
//		genericRemoveNodesOperation2.getReverseOperation().execute();
//		assertEquals(1, classNode.getMethods().size());
//
//		genericRemoveNodesOperation1.getReverseOperation().execute();
//		assertEquals(2, classNode.getMethods().size());
//	}
//
//	@Test
//	public void removeBasicParameterOfMethod() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		// class node 
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		// method node
//
//		MethodNode methodNode = new MethodNode("Method");
//		classNode.addMethod(methodNode);
//
//		// basic parameters and choices 
//
//		BasicParameterNode basicParameterNode1 = 
//				new BasicParameterNode(
//						"BasicParam1", "String", "", false, null);
//		methodNode.addParameter(basicParameterNode1);
//
//		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
//		basicParameterNode1.addChoice(choiceNode1);
//
//		BasicParameterNode basicParameterNode2 = 
//				new BasicParameterNode(
//						"BasicParam2", "String", "", false, null);
//		methodNode.addParameter(basicParameterNode2);
//
//		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
//		basicParameterNode2.addChoice(choiceNode2);
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
//		TestCaseNode testCaseNode = new TestCaseNode(choicesOfTestCase);
//		methodNode.addTestCase(testCaseNode);
//
//		// copy parameters to deployed parameters
//
//		List<BasicParameterNode> deployedParameters = new ArrayList<>();
//		deployedParameters.add(basicParameterNode1);
//		deployedParameters.add(basicParameterNode2);
//		methodNode.setDeployedParameters(deployedParameters);
//
//		// list of nodes to delete
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(basicParameterNode1);
//
//		// remove
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation.execute();
//
//		assertEquals(1, methodNode.getParameters().size());
//		assertEquals(1, methodNode.getConstraintNodes().size());
//
//		assertEquals(0, methodNode.getTestCases().size());
//		assertEquals(0, methodNode.getDeployedMethodParameters().size());
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
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//	}
//
//	@Test
//	public void removeBasicParameterOfNestedStructure() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		// class node 
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		// method node
//
//		MethodNode methodNode = new MethodNode("Method");
//		classNode.addMethod(methodNode);
//
//		// structures
//
//		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
//		methodNode.addParameter(compositeParameterNode1);
//
//		CompositeParameterNode compositeParameterNode2 = new CompositeParameterNode("S2", null);
//		compositeParameterNode1.addParameter(compositeParameterNode2);
//
//		// basic parameters and choices 
//
//		BasicParameterNode basicParameterNode1 = 
//				new BasicParameterNode(
//						"BasicParam1", "String", "", false, null);
//		compositeParameterNode2.addParameter(basicParameterNode1);
//
//		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
//		basicParameterNode1.addChoice(choiceNode1);
//
//		BasicParameterNode basicParameterNode2 = 
//				new BasicParameterNode(
//						"BasicParam2", "String", "", false, null);
//		compositeParameterNode2.addParameter(basicParameterNode2);
//
//		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
//		basicParameterNode2.addChoice(choiceNode2);
//
//		// constraints
//
//		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
//		compositeParameterNode2.addConstraint(constraintNode1);
//
//		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
//		compositeParameterNode2.addConstraint(constraintNode2);
//
//		// test case
//
//		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
//		TestCaseNode testCaseNode = new TestCaseNode(choicesOfTestCase);
//		methodNode.addTestCase(testCaseNode);
//
//		// copy parameters to deployed parameters
//
//		List<BasicParameterNode> deployedParameters = new ArrayList<>();
//		deployedParameters.add(basicParameterNode1);
//		deployedParameters.add(basicParameterNode2);
//		methodNode.setDeployedParameters(deployedParameters);
//
//		// list of nodes to delete
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(basicParameterNode1);
//
//		// remove
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation.execute();
//
//		assertEquals(1, compositeParameterNode2.getParameters().size());
//		assertEquals(1, compositeParameterNode2.getConstraintNodes().size());
//
//		assertEquals(0, methodNode.getTestCases().size());
//		assertEquals(0, methodNode.getDeployedMethodParameters().size());
//
//		// reverse
//
//		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
//		reverseOperation.execute();
//
//		assertEquals(2, compositeParameterNode2.getParameters().size());
//
//		List<ConstraintNode> resultConstraintNodes = compositeParameterNode2.getConstraintNodes();
//		assertEquals(2, resultConstraintNodes.size());
//
//		assertEquals(1, methodNode.getTestCases().size());
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//	}
//
//	@Test
//	public void removeCompositeParameter() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		// class node 
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		// method node
//
//		MethodNode methodNode = new MethodNode("Method");
//		classNode.addMethod(methodNode);
//
//		// nested structures with parameters and choices
//
//		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
//		methodNode.addParameter(compositeParameterNode1);
//
//		CompositeParameterNode compositeParameterNode2 = new CompositeParameterNode("S2", null);
//		compositeParameterNode1.addParameter(compositeParameterNode2);
//
//		// basic parameters and choices 
//
//		BasicParameterNode basicParameterNode1 = new BasicParameterNode("P1", "String", "", false, null);
//		compositeParameterNode2.addParameter(basicParameterNode1);
//
//		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
//		basicParameterNode1.addChoice(choiceNode1);
//
//		BasicParameterNode basicParameterNode2 = new BasicParameterNode("P2", "String", "", false, null);
//		compositeParameterNode2.addParameter(basicParameterNode2);
//
//		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
//		basicParameterNode2.addChoice(choiceNode2);
//
//		// additional structure on method level
//
//		CompositeParameterNode compositeParameterNode3 = new CompositeParameterNode("S3", null);
//		methodNode.addParameter(compositeParameterNode3);
//
//
//		// constraint of method
//
//		ConstraintNode constraintNode1 = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
//		methodNode.addConstraint(constraintNode1);
//
//		// constraint of composite parameter node
//
//		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
//		compositeParameterNode2.addConstraint(constraintNode2);
//
//		// test case 
//
//		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
//		TestCaseNode testCaseNode = new TestCaseNode(choicesOfTestCase);
//		methodNode.addTestCase(testCaseNode);
//
//		// copy parameters to deployed parameters
//
//		List<BasicParameterNode> deployedParameters = new ArrayList<>();
//		deployedParameters.add(basicParameterNode1);
//		deployedParameters.add(basicParameterNode2);
//		methodNode.setDeployedParameters(deployedParameters);
//
//		// list of nodes to delete
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(compositeParameterNode1);
//
//		// remove
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation.execute();
//
//		assertEquals(1, methodNode.getParameters().size());
//		assertEquals(0, methodNode.getConstraintNodes().size());
//
//		assertEquals(0, methodNode.getTestCases().size());
//		assertEquals(0, methodNode.getDeployedMethodParameters().size());
//
//		// reverse
//
//		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
//		reverseOperation.execute();
//
//		assertEquals(2, methodNode.getParameters().size());
//		assertEquals(1, methodNode.getConstraintNodes().size());
//
//		assertEquals(1, methodNode.getTestCases().size());
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//
//		assertEquals(2, compositeParameterNode2.getParameters().size());
//
//		assertEquals(1, compositeParameterNode2.getConstraintNodes().size());
//	}
//
//	@Test
//	public void removeGlobalBasicParameter() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		// global parameter
//
//		BasicParameterNode globalBasicParameterNode1 = 
//				new BasicParameterNode(
//						"GlobalBasicParam1", "String", "", false, null);
//		rootNode.addParameter(globalBasicParameterNode1);
//
//		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
//		globalBasicParameterNode1.addChoice(choiceNode1);
//
//		// class node
//
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		// method node
//
//		MethodNode methodNode = new MethodNode("Method");
//		classNode.addMethod(methodNode);
//
//		// linked parameter
//
//		BasicParameterNode basicParameterNode1 =
//				new BasicParameterNode(
//						"BasicParam1",
//						"String",
//						"",
//						false,
//						globalBasicParameterNode1,
//						null);
//
//		methodNode.addParameter(basicParameterNode1);
//
//		BasicParameterNode basicParameterNode2 = 
//				new BasicParameterNode(
//						"BasicParam2", "String", "", false, null);
//		methodNode.addParameter(basicParameterNode2);
//
//		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
//		basicParameterNode2.addChoice(choiceNode2);
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
//		TestCaseNode testCaseNode = new TestCaseNode(choicesOfTestCase);
//		methodNode.addTestCase(testCaseNode);
//
//		// copy parameters to deployed parameters
//
//		List<BasicParameterNode> deployedParameters = new ArrayList<>();
//		deployedParameters.add(basicParameterNode1);
//		deployedParameters.add(basicParameterNode2);
//		methodNode.setDeployedParameters(deployedParameters);
//
//		// list of nodes to delete
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(globalBasicParameterNode1);
//
//		// remove operation
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation.execute();
//
//		assertEquals(1, methodNode.getParameters().size());
//		assertEquals(1, methodNode.getConstraintNodes().size());
//
//		assertEquals(0, methodNode.getTestCases().size());
//		assertEquals(0, methodNode.getDeployedMethodParameters().size());
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
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//	}
//
//	@Test
//	public void removeChoiceNodeOfMethodBasicParameter() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		// class node 
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		// method node
//
//		MethodNode methodNode = new MethodNode("Method");
//		classNode.addMethod(methodNode);
//
//		// basic parameters and choices 
//
//		BasicParameterNode basicParameterNode1 = 
//				new BasicParameterNode(
//						"BasicParam1", "String", "", false, null);
//		methodNode.addParameter(basicParameterNode1);
//
//		ChoiceNode choiceNode11 = new ChoiceNode("Choice11", "11");
//		basicParameterNode1.addChoice(choiceNode11);
//
//		ChoiceNode choiceNode12 = new ChoiceNode("Choice12", "12");
//		basicParameterNode1.addChoice(choiceNode12);
//
//		BasicParameterNode basicParameterNode2 = 
//				new BasicParameterNode(
//						"BasicParam2", "String", "", false, null);
//		methodNode.addParameter(basicParameterNode2);
//
//		ChoiceNode choiceNode21 = new ChoiceNode("Choice21", "21");
//		basicParameterNode2.addChoice(choiceNode21);
//
//		// constraints
//
//		ConstraintNode constraintNode1 = createConstraintNodeWithChoiceCondition(basicParameterNode1, choiceNode11);
//		methodNode.addConstraint(constraintNode1);
//
//		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
//		methodNode.addConstraint(constraintNode2);
//
//		// test case
//
//		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode11, choiceNode21});
//		TestCaseNode testCaseNode = new TestCaseNode(choicesOfTestCase);
//		methodNode.addTestCase(testCaseNode);
//
//		// copy parameters to deployed parameters
//
//		List<BasicParameterNode> deployedParameters = new ArrayList<>();
//		deployedParameters.add(basicParameterNode1);
//		deployedParameters.add(basicParameterNode2);
//		methodNode.setDeployedParameters(deployedParameters);
//
//		// list of nodes to delete
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(choiceNode11);
//
//		// remove
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation.execute();
//
//		assertEquals(2, methodNode.getParameters().size());
//		assertEquals(1, basicParameterNode1.getChoiceCount());
//
//		assertEquals(1, methodNode.getConstraintNodes().size());
//		assertEquals(0, methodNode.getTestCases().size());
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//
//		// reverse operation
//
//		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
//		reverseOperation.execute();
//
//		assertEquals(2, methodNode.getParameters().size());
//		assertEquals(2, basicParameterNode1.getChoiceCount());
//
//		assertEquals(2, methodNode.getConstraintNodes().size());
//
//		assertEquals(1, methodNode.getTestCases().size());
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//	}
//
//	@Test
//	public void removeChoiceNodeOfCompositeParameter() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		// class node 
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		// method node
//
//		MethodNode methodNode = new MethodNode("Method");
//		classNode.addMethod(methodNode);
//
//		// composite parameter node
//
//		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
//		methodNode.addParameter(compositeParameterNode1);
//
//		// basic parameters of composite parameter and choices 
//
//		BasicParameterNode basicParameterNode1 = 
//				new BasicParameterNode(
//						"BasicParam1", "String", "", false, null);
//		compositeParameterNode1.addParameter(basicParameterNode1);
//
//		ChoiceNode choiceNode11 = new ChoiceNode("Choice11", "11");
//		basicParameterNode1.addChoice(choiceNode11);
//
//		ChoiceNode choiceNode12 = new ChoiceNode("Choice12", "12");
//		basicParameterNode1.addChoice(choiceNode12);
//
//		BasicParameterNode basicParameterNode2 = 
//				new BasicParameterNode(
//						"BasicParam2", "String", "", false, null);
//		compositeParameterNode1.addParameter(basicParameterNode2);
//
//		ChoiceNode choiceNode21 = new ChoiceNode("Choice21", "21");
//		basicParameterNode2.addChoice(choiceNode21);
//
//		// constraints
//
//		ConstraintNode constraintNode1 = createConstraintNodeWithChoiceCondition(basicParameterNode1, choiceNode11);
//		methodNode.addConstraint(constraintNode1);
//
//		ConstraintNode constraintNode2 = createConstraintNodeWithValueCondition(basicParameterNode2,"2");
//		methodNode.addConstraint(constraintNode2);
//
//		// test case
//
//		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode11, choiceNode21});
//		TestCaseNode testCaseNode = new TestCaseNode(choicesOfTestCase);
//		methodNode.addTestCase(testCaseNode);
//
//		// copy parameters to deployed parameters
//
//		List<BasicParameterNode> deployedParameters = new ArrayList<>();
//		deployedParameters.add(basicParameterNode1);
//		deployedParameters.add(basicParameterNode2);
//		methodNode.setDeployedParameters(deployedParameters);
//
//		// list of nodes to delete
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(choiceNode11);
//
//		// remove
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation.execute();
//
//		assertEquals(1, methodNode.getParameters().size());
//		assertEquals(2, compositeParameterNode1.getParameters().size());
//
//		assertEquals(1, basicParameterNode1.getChoiceCount());
//
//		assertEquals(1, methodNode.getConstraintNodes().size());
//		assertEquals(0, methodNode.getTestCases().size());
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//
//		// reverse operation
//
//		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
//		reverseOperation.execute();
//
//		assertEquals(1, methodNode.getParameters().size());
//		assertEquals(2, basicParameterNode1.getChoiceCount());
//
//		assertEquals(2, methodNode.getConstraintNodes().size());
//
//		assertEquals(1, methodNode.getTestCases().size());
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//	}
//
//	@Test
//	public void removeConstraint() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		// class node 
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		// method node
//
//		MethodNode methodNode = new MethodNode("Method");
//		classNode.addMethod(methodNode);
//
//		// basic parameters and choices 
//
//		BasicParameterNode basicParameterNode1 = 
//				new BasicParameterNode(
//						"BasicParam1", "String", "", false, null);
//		methodNode.addParameter(basicParameterNode1);
//
//		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
//		basicParameterNode1.addChoice(choiceNode1);
//
//		// constraints
//
//		ConstraintNode constraintNode = createConstraintNodeWithValueCondition(basicParameterNode1,"1");
//		methodNode.addConstraint(constraintNode);
//
//		// list of nodes to delete
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(constraintNode);
//
//		// remove
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation.execute();
//
//		assertEquals(0, methodNode.getConstraintNodes().size());
//
//		// reverse
//		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
//		reverseOperation.execute();
//
//		assertEquals(1, methodNode.getConstraintNodes().size());
//	}
//
//	@Test
//	public void removeTestCases() {
//
//		RootNode rootNode = new RootNode("Root", null);
//
//		// class node 
//		ClassNode classNode = new ClassNode("Class", null);
//		rootNode.addClass(classNode);
//
//		// method node
//
//		MethodNode methodNode = new MethodNode("Method");
//		classNode.addMethod(methodNode);
//
//		// basic parameters and choices 
//
//		BasicParameterNode basicParameterNode1 = 
//				new BasicParameterNode(
//						"BasicParam1", "String", "", false, null);
//		methodNode.addParameter(basicParameterNode1);
//
//		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
//		basicParameterNode1.addChoice(choiceNode1);
//
//		BasicParameterNode basicParameterNode2 = 
//				new BasicParameterNode(
//						"BasicParam2", "String", "", false, null);
//		methodNode.addParameter(basicParameterNode2);
//
//		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");
//		basicParameterNode2.addChoice(choiceNode2);
//
//		// test case
//
//		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
//		TestCaseNode testCaseNode = new TestCaseNode(choicesOfTestCase);
//		methodNode.addTestCase(testCaseNode);
//
//		// copy parameters to deployed parameters
//
//		List<BasicParameterNode> deployedParameters = new ArrayList<>();
//		deployedParameters.add(basicParameterNode1);
//		deployedParameters.add(basicParameterNode2);
//		methodNode.setDeployedParameters(deployedParameters);
//
//		// list of nodes to delete
//
//		List<IAbstractNode> nodesToDelete = new ArrayList<>();
//		nodesToDelete.add(testCaseNode);
//
//		// remove
//
//		GenericRemoveNodesOperation genericRemoveNodesOperation = 
//				createRemovingNodesOperation(nodesToDelete, rootNode);
//		genericRemoveNodesOperation.execute();
//
//		assertEquals(0, methodNode.getTestCases().size());
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//
//		// reverse
//		IModelOperation reverseOperation = genericRemoveNodesOperation.getReverseOperation();
//		reverseOperation.execute();
//
//		assertEquals(1, methodNode.getTestCases().size());
//		assertEquals(2, methodNode.getDeployedMethodParameters().size());
//	}
//
//
//	private ConstraintNode createConstraintNodeWithValueCondition(
//			BasicParameterNode basicParameterNode, String value) {
//
//		StaticStatement staticStatement = new StaticStatement(EvaluationResult.TRUE);
//
//		RelationStatement relationStatement2 = 
//				RelationStatement.createRelationStatementWithValueCondition(
//						basicParameterNode, EMathRelation.EQUAL, value);
//
//		Constraint constraint = new Constraint(
//				"constraint", 
//				ConstraintType.EXTENDED_FILTER, 
//				staticStatement, 
//				relationStatement2, 
//				null);
//
//		ConstraintNode constraintNode = new ConstraintNode("constraintNode", constraint, null);
//		return constraintNode;
//	}
//
//	private ConstraintNode createConstraintNodeWithChoiceCondition(
//			BasicParameterNode basicParameterNode, ChoiceNode choiceNode) {
//
//		StaticStatement staticStatement = new StaticStatement(EvaluationResult.TRUE);
//
//		RelationStatement relationStatement2 = 
//				RelationStatement.createRelationStatementWithChoiceCondition(
//						basicParameterNode, EMathRelation.EQUAL, choiceNode);
//
//		Constraint constraint = new Constraint(
//				"constraint", 
//				ConstraintType.EXTENDED_FILTER, 
//				staticStatement, 
//				relationStatement2, 
//				null);
//
//		ConstraintNode constraintNode = new ConstraintNode("constraintNode", constraint, null);
//		return constraintNode;
//	}

	private GenericAddChildrenOperation createAddingNodeOperation(
			IAbstractNode parent,
			IAbstractNode child,
			int index) {

		List<IAbstractNode> children = Arrays.asList(child);
		
		GenericAddChildrenOperation genericAddChildrenOperation =
				new GenericAddChildrenOperation(
						parent, 
						children, 
						index, 
						new TypeAdapterProviderForJava(), 
						true,
						new ExtLanguageManagerForJava());

		return genericAddChildrenOperation;
	}

}
