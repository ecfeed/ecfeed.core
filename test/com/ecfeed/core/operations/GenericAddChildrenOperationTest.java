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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
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

	@Test
	public void addMethods() {

		RootNode rootNode = new RootNode("Root", null);
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node 1

		MethodNode methodNode1 = new MethodNode("Method1");
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(classNode, methodNode1, 0 );
		genericAddChildrenOperation1.execute();

		List<MethodNode> methods = classNode.getMethods();
		assertEquals(1, methods.size());

		// add method node 2

		MethodNode methodNode2 = new MethodNode("Method2");
		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(classNode, methodNode2, 0 );
		genericAddChildrenOperation2.execute();

		methods = classNode.getMethods();
		assertEquals(2, methods.size());

		assertEquals("Method2", methods.get(0).getName());
		assertEquals("Method1", methods.get(1).getName());

		// reverse operation 2

		genericAddChildrenOperation2.getReverseOperation().execute();
		methods = classNode.getMethods();
		assertEquals(1, methods.size());
		assertEquals("Method1", methods.get(0).getName());

		// reverse operation 1

		genericAddChildrenOperation1.getReverseOperation().execute();
		methods = classNode.getMethods();
		assertEquals(0, methods.size());
	}

	@Test
	public void addBasicParameterToMethod() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// add basic parameters node 1 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(methodNode, basicParameterNode1, 0 );
		genericAddChildrenOperation1.execute();

		List<AbstractParameterNode> parameterNodes = methodNode.getParameters();
		assertEquals(1, parameterNodes.size());

		// add basic parameters node 2 

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode(
						"BasicParam2", "String", "", false, null);
		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(methodNode, basicParameterNode2, 0 );
		genericAddChildrenOperation2.execute();

		parameterNodes = methodNode.getParameters();
		assertEquals(2, parameterNodes.size());

		// reverse operation 2

		genericAddChildrenOperation2.getReverseOperation().execute();
		parameterNodes = methodNode.getParameters();
		assertEquals(1, parameterNodes.size());


		// reverse operation 1

		genericAddChildrenOperation1.getReverseOperation().execute();
		parameterNodes = methodNode.getParameters();
		assertEquals(0, parameterNodes.size());
	}

	@Test
	public void addChoiceToBasicParameterOfMethod() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		// add choice node 1 

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");

		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(basicParameterNode1, choiceNode1, 0 );
		genericAddChildrenOperation1.execute();

		List<ChoiceNode> choiceNodes = basicParameterNode1.getChoices();
		assertEquals(1, choiceNodes.size());

		// add choice node 2 

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");

		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(basicParameterNode1, choiceNode2, 0 );
		genericAddChildrenOperation2.execute();

		choiceNodes = basicParameterNode1.getChoices();
		assertEquals(2, choiceNodes.size());

		// reverse operation 2

		genericAddChildrenOperation2.getReverseOperation().execute();
		assertEquals(1, basicParameterNode1.getChoices().size());

		// reverse operation 1

		genericAddChildrenOperation1.getReverseOperation().execute();
		assertEquals(0, basicParameterNode1.getChoices().size());
	}

	@Test
	public void addChoiceToChoice() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		// add choice node 1 

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");

		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(basicParameterNode1, choiceNode1, 0 );
		genericAddChildrenOperation1.execute();

		assertEquals(1, basicParameterNode1.getChoices().size());

		// add choice node 2 

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");

		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(choiceNode1, choiceNode2, 0 );
		genericAddChildrenOperation2.execute();

		assertEquals(1, basicParameterNode1.getChoices().size());
		assertEquals(1, choiceNode1.getChoices().size());


		// reverse operation 2

		genericAddChildrenOperation2.getReverseOperation().execute();
		assertEquals(1, basicParameterNode1.getChoices().size());
		assertEquals(0, choiceNode1.getChoices().size());

		// reverse operation 1

		genericAddChildrenOperation1.getReverseOperation().execute();
		assertEquals(0, basicParameterNode1.getChoices().size());
	}

	@Test
	public void addConstraint() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// basic parameters and choices 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		// constraints

		ConstraintNode constraintNode = createConstraintNodeWithValueCondition(basicParameterNode1,"1");

		// add constraint

		GenericAddChildrenOperation genericAddChildrenOperation = 
				createAddingNodeOperation(methodNode, constraintNode, 0 );
		genericAddChildrenOperation.execute();

		assertEquals(1, methodNode.getConstraintNodes().size());

		// reverse operation

		genericAddChildrenOperation.getReverseOperation().execute();
		assertEquals(0, methodNode.getConstraintNodes().size());
	}

	@Test
	public void addTestCases() {

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

		// add test case 1

		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1, choiceNode2});
		TestCaseNode testCaseNode1 = new TestCaseNode(choicesOfTestCase);

		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(methodNode, testCaseNode1, 0 );
		genericAddChildrenOperation1.execute();

		assertEquals(1, methodNode.getTestCases().size());

		// add test case 2

		TestCaseNode testCaseNode = new TestCaseNode(choicesOfTestCase);

		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(methodNode, testCaseNode, 0 );
		genericAddChildrenOperation2.execute();

		assertEquals(2, methodNode.getTestCases().size());

		// reverse operation2

		genericAddChildrenOperation2.getReverseOperation().execute();
		assertEquals(1, methodNode.getTestCases().size());

		// reverse operation 1

		genericAddChildrenOperation1.getReverseOperation().execute();
		assertEquals(0, methodNode.getTestCases().size());
	}

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
