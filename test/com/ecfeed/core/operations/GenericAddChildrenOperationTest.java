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
import java.util.Optional;

import org.junit.Test;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class GenericAddChildrenOperationTest {

	@Test
	public void addClass() {

		RootNode rootNode = new RootNode("Root", null);

		// add class node 1

		ClassNode classNode1 = new ClassNode("Class1", null);

		NodeMapper nodeMapper = new NodeMapper();
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(rootNode, classNode1, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation1.execute();

		List<ClassNode> classNodes = rootNode.getClasses();
		assertEquals(1, classNodes.size());

		// add class node 2

		ClassNode classNode2 = new ClassNode("Class2", null);

		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(rootNode, classNode2, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation2.execute();

		classNodes = rootNode.getClasses();
		assertEquals(2, classNodes.size());

		// add class node 3

		ClassNode classNode3 = new ClassNode("Class3", null);

		GenericAddChildrenOperation genericAddChildrenOperation3 = 
				createAddingNodeOperation(rootNode, classNode3, 2, Optional.of(nodeMapper));
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
		
		NodeMapper nodeMapper = new NodeMapper();
		
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(classNode, methodNode1, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation1.execute();

		List<MethodNode> methods = classNode.getMethods();
		assertEquals(1, methods.size());

		// add method node 2

		MethodNode methodNode2 = new MethodNode("Method2");
		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(classNode, methodNode2, 0, Optional.of(nodeMapper));
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
		
		NodeMapper nodeMapper = new NodeMapper();
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(methodNode, basicParameterNode1, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation1.execute();

		List<AbstractParameterNode> parameterNodes = methodNode.getParameters();
		assertEquals(1, parameterNodes.size());

		// add basic parameters node 2 

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode("BasicParam2", "String", "", false, null);
		
		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(methodNode, basicParameterNode2, 0, Optional.of(nodeMapper));
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

		NodeMapper nodeMapper = new NodeMapper();
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(basicParameterNode1, choiceNode1, 0, Optional.of(nodeMapper));
		
		genericAddChildrenOperation1.execute();

		List<ChoiceNode> choiceNodes = basicParameterNode1.getChoices();
		assertEquals(1, choiceNodes.size());

		// add choice node 2 

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");

		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(basicParameterNode1, choiceNode2, 0, Optional.of(nodeMapper));
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

		NodeMapper nodeMapper = new NodeMapper();
		
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(basicParameterNode1, choiceNode1, 0, Optional.of(nodeMapper));
		
		genericAddChildrenOperation1.execute();

		assertEquals(1, basicParameterNode1.getChoices().size());

		// add choice node 2 

		ChoiceNode choiceNode2 = new ChoiceNode("Choice2", "2");

		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(choiceNode1, choiceNode2, 0, Optional.of(nodeMapper));
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

		NodeMapper nodeMapper = new NodeMapper();
		GenericAddChildrenOperation genericAddChildrenOperation = 
				createAddingNodeOperation(methodNode, constraintNode, 0, Optional.of(nodeMapper));
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
		String testSuiteName = "TestSuite";
		
		TestCaseNode testCaseNode1 = new TestCaseNode(testSuiteName, null, choicesOfTestCase);

		NodeMapper nodeMapper = new NodeMapper();
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(methodNode, testCaseNode1, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation1.execute();

		assertEquals(1, methodNode.getTestCases().size());
		assertEquals(1, methodNode.getTestSuites().size());
		TestSuiteNode testSuiteNode = methodNode.findTestSuite(testSuiteName);
		assertEquals(1, testSuiteNode.getTestCaseNodes().size());

		// add test case 2

		TestCaseNode testCaseNode = new TestCaseNode(testSuiteName, null, choicesOfTestCase);

		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(methodNode, testCaseNode, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation2.execute();
		
		assertEquals(2, methodNode.getTestCases().size());
		assertEquals(2,testSuiteNode.getTestCaseNodes().size());

		// reverse operation2

		genericAddChildrenOperation2.getReverseOperation().execute();
		assertEquals(1, methodNode.getTestCases().size());

		// reverse operation 1

		genericAddChildrenOperation1.getReverseOperation().execute();
		assertEquals(0, methodNode.getTestCases().size());
	}

	@Test
	public void addCompositeParameters() {

		RootNode rootNode = new RootNode("Root", null);

		// class

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method 

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// basic parameter

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode("BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		// choice

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		// test case 
		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// add composite parameter 1

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);

		NodeMapper nodeMapper = new NodeMapper();
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(methodNode, compositeParameterNode1, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation1.execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(0, compositeParameterNode1.getParameters().size());
		assertEquals(0, methodNode.getTestCases().size());

		// add composite parameter 2

		CompositeParameterNode compositeParameterNode2 = new CompositeParameterNode("S2", null);
		
		GenericAddChildrenOperation genericAddChildrenOperation2 = 
				createAddingNodeOperation(compositeParameterNode1, compositeParameterNode2, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation2.execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(0, methodNode.getTestCases().size());

		// reverse operation 2

		genericAddChildrenOperation2.getReverseOperation().execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(0, compositeParameterNode1.getParameters().size());
		assertEquals(0, methodNode.getTestCases().size());

		genericAddChildrenOperation1.getReverseOperation().execute();

		assertEquals(1, methodNode.getParameters().size());
		assertEquals(1, methodNode.getTestCases().size());
	}

	@Test
	public void addBasicParameterToCompositeParameter() {

		RootNode rootNode = new RootNode("Root", null);

		// class

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// method 

		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);

		// basic parameter

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode("BasicParam1", "String", "", false, null);
		methodNode.addParameter(basicParameterNode1);

		// choice

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");
		basicParameterNode1.addChoice(choiceNode1);

		// test case 
		List<ChoiceNode> choicesOfTestCase = Arrays.asList(new ChoiceNode[] {choiceNode1});
		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		// composite parameter 1

		CompositeParameterNode compositeParameterNode1 = new CompositeParameterNode("S1", null);
		methodNode.addParameter(compositeParameterNode1);

		// basic parameter 2

		BasicParameterNode basicParameterNode2 = 
				new BasicParameterNode("BasicParam2", "String", "", false, null);

		// add parameter 2 by operation

		NodeMapper nodeMapper = new NodeMapper();
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(compositeParameterNode1, basicParameterNode2, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation1.execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(1, compositeParameterNode1.getParameters().size());
		assertEquals(0, methodNode.getTestCases().size());

		// revert

		genericAddChildrenOperation1.getReverseOperation().execute();

		assertEquals(2, methodNode.getParameters().size());
		assertEquals(0, compositeParameterNode1.getParameters().size());
		assertEquals(1, methodNode.getTestCases().size());
	}

	@Test
	public void addGlobalBasicParameter() {

		RootNode rootNode = new RootNode("Root", null);

		// global parameter

		BasicParameterNode globalBasicParameterNode1 = 
				new BasicParameterNode(
						"GlobalBasicParam1", "String", "", false, null);

		NodeMapper nodeMapper = new NodeMapper();
		
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(rootNode, globalBasicParameterNode1, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation1.execute();

		assertEquals(1, rootNode.getParameters().size());

		genericAddChildrenOperation1.getReverseOperation().execute();
		assertEquals(0, rootNode.getParameters().size());
	}

	@Test
	public void addChoiceToGlobalBasicParameter() {

		RootNode rootNode = new RootNode("Root", null);

		// global parameter

		BasicParameterNode globalBasicParameterNode1 = 
				new BasicParameterNode(
						"GlobalBasicParam1", "String", "", false, null);
		rootNode.addParameter(globalBasicParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("Choice1", "1");

		NodeMapper nodeMapper = new NodeMapper();
		
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(globalBasicParameterNode1, choiceNode1, 0, Optional.of(nodeMapper));
		
		genericAddChildrenOperation1.execute();

		assertEquals(1, globalBasicParameterNode1.getChoices().size());

		genericAddChildrenOperation1.getReverseOperation().execute();
		assertEquals(0, globalBasicParameterNode1.getChoices().size());
	}


	@Test
	public void addChoiceNodeOfCompositeParameter() {

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

		// basic parameters of composite parameter 

		BasicParameterNode basicParameterNode1 = 
				new BasicParameterNode(
						"BasicParam1", "String", "", false, null);
		compositeParameterNode1.addParameter(basicParameterNode1);

		// choice 11

		ChoiceNode choiceNode11 = new ChoiceNode("Choice11", "11");
		basicParameterNode1.addChoice(choiceNode11);

		// choice 12 added by operation

		ChoiceNode choiceNode12 = new ChoiceNode("Choice12", "12");

		NodeMapper nodeMapper = new NodeMapper();
		
		GenericAddChildrenOperation genericAddChildrenOperation1 = 
				createAddingNodeOperation(basicParameterNode1, choiceNode12, 0, Optional.of(nodeMapper));
		genericAddChildrenOperation1.execute();


		assertEquals(2, basicParameterNode1.getChoices().size());

		// reverse operation

		genericAddChildrenOperation1.getReverseOperation().execute();
		assertEquals(1, basicParameterNode1.getChoices().size());
	}

	private ConstraintNode createConstraintNodeWithValueCondition(
			BasicParameterNode basicParameterNode, String value) {

		StaticStatement staticStatement = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithValueCondition(
						basicParameterNode, null, EMathRelation.EQUAL, value);  // TODO MO-RE leftParameterLinkingContext

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
			int index,
			Optional<NodeMapper> nodeMapper) {

		List<IAbstractNode> children = Arrays.asList(child);

		GenericAddChildrenOperation genericAddChildrenOperation =
				new GenericAddChildrenOperation(
						parent, 
						children, 
						index, 
						true,
						nodeMapper,
						new ExtLanguageManagerForJava());

		return genericAddChildrenOperation;
	}

}
