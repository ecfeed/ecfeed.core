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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.ChoiceConversionList;
import com.ecfeed.core.utils.ChoiceConversionOperation;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodParameterNodeTestForDetached {

	@Test
	public void linkMethodParameterToClassParameterBasicUseCase() {

		RootNode rootNode = new RootNode("Root", null);

		// names of global parameters
		// the same name for root global parameter and class global parameter

		final String globalParameterName = "GP1";
		final String globalChoiceName1 = "GC1";

		// add global parameter and choice for root

		GlobalParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, "String", null);

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfRoot, globalChoiceName1, "0", null);

		// add class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class1", null);

		// add global parameter and choice for class

		GlobalParameterNode globalParameterNodeOfClass = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNodeForClass = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass, globalChoiceName1, "0", null);

		// add methodNode 

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method", null);

		// add parameter and choice to method

		final String methodParameterName = "P1";
		final String methodChoiceName1 = "C1";

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");

		ChoiceNode methodChoiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");

		// add constraint

		addNewSimpleConstraintToMethod(methodNode, "c1", methodParameterNode, methodChoiceNode1, methodChoiceNode1);

		// creating choice conversion list

		ChoiceConversionList choiceConversionList = new ChoiceConversionList();

		choiceConversionList.addItem(
				methodChoiceName1, 
				ChoiceConversionOperation.MERGE, 
				globalChoiceName1);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterLinker.linkMethodParameteToGlobalParameter(
				methodParameterNode, 
				globalParameterNodeOfClass, 
				choiceConversionList, 
				reverseOperations, 
				extLanguageManager);

		// check global parameter of class

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		ChoiceNode choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfClass, methodParameterNode2.getLink());

		// check choices from constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromPostcondition);

		// reverse operation

		assertEquals(6, reverseOperations.getSize());
		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(false, methodParameterNode2.isLinked());
		assertNull(methodParameterNode2.getLink());

		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);

		// check choices from constraints

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPrecondition);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPostcondition);
	}

	@Test
	public void linkMethodParameterToRootParameterBasicUseCase() {

		RootNode rootNode = new RootNode("Root", null);

		// names of global parameters
		// the same name for root global parameter and class global parameter

		final String globalParameterName = "GP1";
		final String globalChoiceName1 = "GC1";

		// add global parameter and choice for root

		GlobalParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNodeOfRoot = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfRoot, globalChoiceName1, "0", null);

		// add class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class1", null);

		// add global parameter and choice for class

		GlobalParameterNode globalParameterNodeOfClass = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String", null);

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfClass, globalChoiceName1, "0", null);

		// add methodNode 

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method", null);

		// add parameter and choice to method

		final String methodParameterName = "P1";
		final String methodChoiceName1 = "C1";

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");

		ChoiceNode methodChoiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");

		// add constraint

		addNewSimpleConstraintToMethod(methodNode, "c1", methodParameterNode, methodChoiceNode1, methodChoiceNode1);

		// creating choice conversion list

		ChoiceConversionList choiceConversionList = new ChoiceConversionList();

		choiceConversionList.addItem(
				methodChoiceName1, 
				ChoiceConversionOperation.MERGE, 
				globalChoiceName1);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterLinker.linkMethodParameteToGlobalParameter(
				methodParameterNode, 
				globalParameterNodeOfRoot, 
				choiceConversionList, 
				reverseOperations, 
				extLanguageManager);

		// check global parameter of root

		assertEquals(1, rootNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfRoot.getChoiceCount());
		ChoiceNode choiceNodeFromGlobalParam = globalParameterNodeOfRoot.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeOfRoot, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfRoot, methodParameterNode2.getLink());

		// check choices from constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalChoiceNodeOfRoot, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalChoiceNodeOfRoot, choiceNodeFromPostcondition);

		// reverse operation

		assertEquals(6, reverseOperations.getSize());
		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, rootNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfRoot.getChoiceCount());
		choiceNodeFromGlobalParam = globalParameterNodeOfRoot.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeOfRoot, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(false, methodParameterNode2.isLinked());
		assertNull(methodParameterNode2.getLink());

		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);

		// check choices from constraints

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPrecondition);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPostcondition);
	}

	@Test
	public void filterChoicesNotUsedInConstraints() {

		RootNode rootNode = new RootNode("Root", null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add global parameter of class and choice node

		final String parameterType = "String";
		final String choiceValueString = "1";

		GlobalParameterNode globalParameterNodeOfClass1 = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, "CP1", parameterType, null);

		ChoiceNode globalChoiceNodeOfMethod11 = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass1, "CC11", choiceValueString, null);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter and choice to method

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", parameterType);

		ChoiceNode choiceNodeOfMethod11 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC11", choiceValueString);

		//		ChoiceNode choiceNodeOfMethod12 = 
		MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC12", choiceValueString);

		addNewSimpleConstraintToMethod(methodNode, "C1" , methodParameterNode1, choiceNodeOfMethod11, choiceNodeOfMethod11);


		// creating choice conversion list

		ChoiceConversionList choiceConversionList = new ChoiceConversionList();

		choiceConversionList.addItem(
				choiceNodeOfMethod11.getName(), 
				ChoiceConversionOperation.MERGE, 
				globalChoiceNodeOfMethod11.getName());

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterLinker.linkMethodParameteToGlobalParameter(
				methodParameterNode1, 
				globalParameterNodeOfClass1, 
				choiceConversionList, 
				reverseOperations, 
				extLanguageManager);

		int globalParamChoiceCount = globalParameterNodeOfClass1.getChoiceCount();
		assertEquals(1, globalParamChoiceCount);

		ChoiceNode resultChoiceNode = globalParameterNodeOfClass1.getChoices().get(0);
		assertEquals(globalChoiceNodeOfMethod11.getName(), resultChoiceNode.getName());
	}

	@Test
	public void moveChildChoices() {

		RootNode rootNode = new RootNode("Root", null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add global parameter of class and choice node

		final String parameterType = "String";
		final String choiceValueString = "1";

		GlobalParameterNode globalParameterNodeOfClass1 = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, "CP1", parameterType, null);

		ChoiceNode globalChoiceOfClass11 = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass1, "CC11", choiceValueString, null);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter and choice to method

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", parameterType);

		ChoiceNode choiceNodeOfMethod11 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC11", choiceValueString);

		ChoiceNode choiceNodeOfMethod111 = 
				ChoiceNodeHelper.addChoiceToChoice(choiceNodeOfMethod11, "MC111", choiceValueString);

		addNewSimpleConstraintToMethod(
				methodNode, "constraint1", methodParameterNode1, choiceNodeOfMethod11, choiceNodeOfMethod11);

		// creating choice conversion list

		ChoiceConversionList choiceConversionList = new ChoiceConversionList();

		choiceConversionList.addItem(
				choiceNodeOfMethod11.getName(), 
				ChoiceConversionOperation.MERGE, 
				globalChoiceOfClass11.getName());

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterLinker.linkMethodParameteToGlobalParameter(
				methodParameterNode1, 
				globalParameterNodeOfClass1, 
				choiceConversionList, 
				reverseOperations, 
				extLanguageManager);


		// check 

		assertEquals(1, globalChoiceOfClass11.getChoiceCount()); 
		ChoiceNode resultChoiceNode = globalChoiceOfClass11.getChoices().get(0);

		assertEquals(choiceNodeOfMethod111.getName(), resultChoiceNode.getName());

		// reverse operation

		assertEquals(6, reverseOperations.getSize());
		reverseOperations.executeFromTail();

		// check

		assertEquals(0, globalChoiceOfClass11.getChoiceCount()); 

		assertEquals(1, choiceNodeOfMethod11.getChoiceCount());
		ChoiceNode resultChoiceNode2 = choiceNodeOfMethod11.getChoices().get(0);
		assertEquals(choiceNodeOfMethod111.getName(), resultChoiceNode2.getName());
	}


	@Test
	public void deletingTestCases() {

		RootNode rootNode = new RootNode("Root", null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add global parameter of class and choice node

		final String parameterType = "String";
		final String choiceValueString = "1";

		GlobalParameterNode globalParameterNodeOfClass1 = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, "CP1", parameterType, null);

		ChoiceNode globalChoiceOfClass11 =
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass1, "CC11", choiceValueString, null);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter and choice to method

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", parameterType);

		ChoiceNode choiceNodeOfMethod11 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC11", choiceValueString);

		addNewSimpleConstraintToMethod(
				methodNode, "constraint1", methodParameterNode1, choiceNodeOfMethod11, choiceNodeOfMethod11);

		// add test case

		List<ChoiceNode> choices = new ArrayList<>();
		choices.add(choiceNodeOfMethod11);

		TestCaseNode testCaseNode = new TestCaseNode(choices);
		List<TestCaseNode> testCaseNodes = new ArrayList<TestCaseNode>();
		testCaseNodes.add(testCaseNode);

		methodNode.addTestCase(testCaseNode);


		// creating choice conversion list

		ChoiceConversionList choiceConversionList = new ChoiceConversionList();

		choiceConversionList.addItem(
				choiceNodeOfMethod11.getName(), 
				ChoiceConversionOperation.MERGE, 
				globalChoiceOfClass11.getName());

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterLinker.linkMethodParameteToGlobalParameter(
				methodParameterNode1, 
				globalParameterNodeOfClass1, 
				choiceConversionList, 
				reverseOperations, 
				extLanguageManager);

		// checking test cases - should be deleted

		List<TestCaseNode> resultTestCases = methodNode.getTestCases();

		assertEquals(0, resultTestCases.size());

		// reverse operation

		assertEquals(6, reverseOperations.getSize());
		reverseOperations.executeFromTail();

		assertEquals(1, resultTestCases.size());
	}

	//	@Test
	//	public void linkMethodParameterToClassParameter() {
	//
	//		RootNode rootNode = new RootNode("Root", null);
	//
	//		ClassNode classNode = new ClassNode("class1", null);
	//		rootNode.addClass(classNode);
	//
	//		final String globalParameterName = "GP1";
	//		final String globalChoiceName1 = "GC1";
	//
	//		// add global parameter and choice
	//
	//		GlobalParameterNode globalParameterNode = ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String");
	//		
	//		ChoiceNode globalChoiceNode1 = 
	//				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
	//						globalParameterNode, globalChoiceName1, "0");
	//
	//		// add methodNode 
	//		
	//		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method");
	//
	//		// add parameter and choice to method
	//
	//		final String methodParameterName = "P1";
	//		final String methodChoiceName1 = "C1";
	//
	//		MethodParameterNode methodParameterNode = 
	//				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");
	//
	//		ChoiceNode methodChoiceNode1 = 
	//				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");
	//
	//		// add choice not used in constraint
	//		
	//		final String methodChoiceName2 = "C2";
	//		ChoiceNode methodChoiceNode2 = 
	//				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName2, "0");
	//
	//		// add constraint
	//		
	//		addNewSimpleConstraintToMethod(methodNode, "c1", methodParameterNode, methodChoiceNode1, methodChoiceNode1);
	//
	//		// creating choice conversion list
	//
	//		ChoiceConversionList choiceConversionList = new ChoiceConversionList();
	//
	//		choiceConversionList.addItem(
	//				methodChoiceName1, 
	//				ChoiceConversionOperation.MERGE, 
	//				globalChoiceName1);
	//
	//		// linking
	//
	//		ListOfModelOperations reverseOperations = new ListOfModelOperations();
	//		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();
	//
	//		ParameterLinker.linkMethodParameteToGlobalParameter(
	//				methodParameterNode, globalParameterNode, 
	//				choiceConversionList, reverseOperations, extLanguageManager);
	//
	//		// check global parameter
	//
	//		assertEquals(1, classNode.getParametersCount());
	//		assertEquals(1, globalParameterNode.getChoiceCount());
	//		ChoiceNode choiceNodeFromGlobalParam = globalParameterNode.getChoice(globalChoiceName1);
	//		assertEquals(globalChoiceNode1, choiceNodeFromGlobalParam);
	//
	//		// check local parameter 
	//
	//		assertEquals(1, methodNode.getParametersCount());
	//		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked
	//
	//		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
	//		assertEquals(true, methodParameterNode2.isLinked());
	//		assertEquals(globalParameterNode, methodParameterNode2.getLink());
	//
	//		// check choices from constraints
	//
	//		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
	//		assertEquals(globalChoiceNode1, choiceNodeFromPrecondition);
	//
	//		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
	//		assertEquals(globalChoiceNode1, choiceNodeFromPostcondition);
	//
	//		// reverse operation
	//
	//		assertEquals(5, reverseOperations.getSize());
	//		reverseOperations.executeFromTail();
	//
	//		// check global parameter
	//
	//		assertEquals(1, classNode.getParametersCount());
	//		assertEquals(1, globalParameterNode.getChoiceCount());
	//		choiceNodeFromGlobalParam = globalParameterNode.getChoice(globalChoiceName1);
	//		assertEquals(globalChoiceNode1, choiceNodeFromGlobalParam);
	//
	//		// check local parameter 
	//
	//		assertEquals(1, methodNode.getParametersCount());
	//		assertEquals(2, methodParameterNode.getChoiceCount());
	//
	//		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
	//		assertEquals(false, methodParameterNode2.isLinked());
	//		assertNull(methodParameterNode2.getLink());
	//
	//		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
	//		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);
	//
	//		// check choices from constraints
	//
	//		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
	//		assertEquals(methodChoiceNode1, choiceNodeFromPrecondition);
	//
	//		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
	//		assertEquals(methodChoiceNode1, choiceNodeFromPostcondition);
	//	}
	//
	// TODO - add test to check if choice conversion list is complete
	// TODO - choice conversion list which includes top choices (children of parameter)

	//	@Test
	//	public void attachDetachParameterNodeTest() {
	//
	//		MethodNode methodNode = new MethodNode("method", null);
	//
	//		// create and add parameter, choice1, and child choice2
	//
	//		final String par1Name = "par1";
	//		final String oldChoiceName1 = "oldChoice1";
	//		final String choiceNodeName2 = "oldChoice2";		
	//
	//		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
	//		ChoiceNode oldChoiceNode1 = addNewChoiceToMethodParameter(oldMethodParameterNode, oldChoiceName1, "0");
	//		ChoiceNode oldChoiceNode2 = addNewChoiceToChoice(oldChoiceNode1, choiceNodeName2, "0");
	//
	//		addNewTestCaseToMethod(methodNode, oldChoiceNode1);
	//		addNewSimpleConstraintToMethod(methodNode, "c1", oldMethodParameterNode, oldChoiceNode1, oldChoiceNode2);
	//
	//		// detach parameter 
	//
	//		methodNode.detachParameterNode(par1Name);
	//		assertTrue(oldMethodParameterNode.isDetached());
	//		assertTrue(oldChoiceNode1.isDetached());
	//
	//		assertEquals(0, methodNode.getParametersCount());
	//		assertEquals(1, methodNode.getDetachedParametersCount());
	//
	//		// check choice node 1 from test case - should not be changed
	//
	//		TestCaseNode testCaseNode = methodNode.getTestCases().get(0);
	//		List<ChoiceNode> testData = testCaseNode.getTestData();
	//		ChoiceNode choiceFromTestCase1 = testData.get(0);
	//		assertTrue(choiceFromTestCase1.isDetached());
	//		assertEquals(oldChoiceNode1, choiceFromTestCase1);
	//
	//		// check choice node 2 from test case - should not be changed
	//
	//		ChoiceNode choiceFromTestCase2 = choiceFromTestCase1.getChoices().get(0);
	//		assertTrue(choiceFromTestCase2.isDetached());
	//		assertEquals(oldChoiceNode2, choiceFromTestCase2);
	//
	//		// check choice nodes from constraint - should not be changed
	//
	//		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
	//		assertEquals(oldChoiceNode1, choiceNodeFromPrecondition);
	//
	//		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
	//		assertEquals(oldChoiceNode2, choiceNodeFromPostcondition);
	//
	//		// add new parameter and two choices to method
	//
	//		String newPar1Name = "newPar1";
	//		MethodParameterNode newMethodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");
	//
	//		String newChoiceName1 = "newChoice1";
	//		ChoiceNode newChoiceNode1 = addNewChoiceToMethodParameter(newMethodParameterNode, newChoiceName1, "0");
	//
	//		String newChoiceName2 = "newChoice2";
	//		ChoiceNode newChoiceNode2 = addNewChoiceToChoice(newChoiceNode1, newChoiceName2, "0");
	//
	//		// prepare choice conversion list for attachment
	//
	//		ChoiceConversionList choiceConversionList = new ChoiceConversionList();
	//		
	//		choiceConversionList.addItem(
	//				oldChoiceNode1.getQualifiedName(), 
	//				ChoiceConversionOperation.MERGE, 
	//				newChoiceNode1.getQualifiedName());
	//		
	//		choiceConversionList.addItem(
	//				oldChoiceNode2.getQualifiedName(),
	//				ChoiceConversionOperation.MERGE,
	//				newChoiceNode2.getQualifiedName());
	//
	//		// attach - should replace old choice with new choice and oldParameter with new parameter
	//		ParameterAttacher.attach(oldMethodParameterNode, newMethodParameterNode, choiceConversionList);
	//
	//		assertEquals(1, methodNode.getParametersCount());
	//		assertEquals(0, methodNode.getDetachedParametersCount());
	//
	//		// check parameter from constraint - should be new 
	//
	//		MethodParameterNode methodParameterNodeFromConstraint = 
	//				getMethodParameterNodeFromConstraintPrecondition(methodNode, 0);
	//		assertEquals(methodParameterNodeFromConstraint, newMethodParameterNode);
	//
	//		methodParameterNodeFromConstraint = 
	//				getMethodParameterNodeFromConstraintPostcondition(methodNode, 0);
	//		assertEquals(methodParameterNodeFromConstraint, newMethodParameterNode);
	//
	//		// check choices nodes from constraint - should be new
	//
	//		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
	//		assertEquals(choiceNodeFromPrecondition, newChoiceNode1);
	//
	//		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
	//		assertEquals(choiceNodeFromPostcondition, newChoiceNode2);
	//
	//		// check choice node from test case - should be new 
	//
	//		testCaseNode = methodNode.getTestCases().get(0);
	//		testData = testCaseNode.getTestData();
	//
	//		choiceFromTestCase1 = testData.get(0);
	//		assertEquals(choiceFromTestCase1, newChoiceNode1);
	//
	//		choiceFromTestCase2 = choiceFromTestCase1.getChoices().get(0);
	//		assertEquals(choiceFromTestCase2, newChoiceNode2);
	//	}

	//	@Test
	//	public void attachWithoutChoicesTest() {
	//
	//		MethodNode methodNode = new MethodNode("method", null);
	//
	//		// create and add parameter, choice1, and child choice2
	//
	//		final String par1Name = "par1";
	//		final String oldChoiceName1 = "choice1";
	//		final String oldChoiceName2 = "choice2";		
	//
	//		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
	//		ChoiceNode oldChoiceNode1 = addNewChoiceToMethodParameter(oldMethodParameterNode, oldChoiceName1, "0");
	//		ChoiceNode oldChoiceNode2 = addNewChoiceToChoice(oldChoiceNode1, oldChoiceName2, "0");
	//
	//		// detach parameter 
	//
	//		methodNode.detachParameterNode(par1Name);
	//
	//		// add new parameter without choices
	//
	//		String newPar1Name = "newPar1";
	//		MethodParameterNode newMethodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");
	//
	//		// attach - should create child choices in destination parameter
	//
	//		ParameterAttacher.attach(oldMethodParameterNode, newMethodParameterNode, null);
	//
	//		assertEquals(0, methodNode.getDetachedParametersCount());
	//		assertEquals(1, methodNode.getParametersCount());
	//
	//		// abstract choices with child choices should be transferred to destination parameter
	//
	//		List<ChoiceNode> choiceNodes1 = newMethodParameterNode.getChoices();
	//		assertEquals(1, choiceNodes1.size());
	//
	//		ChoiceNode newChoiceNode1 = choiceNodes1.get(0);
	//		assertEquals(oldChoiceNode1.getQualifiedName(), newChoiceNode1.getQualifiedName());
	//
	//		List<ChoiceNode> choiceNodes2  = newChoiceNode1.getChoices();
	//		assertEquals(1, choiceNodes2.size());
	//
	//		ChoiceNode newChoiceNode2 = choiceNodes2.get(0);
	//		assertEquals(oldChoiceNode2.getQualifiedName(), newChoiceNode2.getQualifiedName());
	//	}

	//	@Test
	//	public void attachWithTheSameChoiceNameTest() {
	//
	//		MethodNode methodNode = new MethodNode("method", null);
	//
	//		// create and add parameter and choice1
	//
	//		final String par1Name = "par1";
	//		final String choiceName1 = "choice1";
	//
	//		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
	//		addNewChoiceToMethodParameter(oldMethodParameterNode, choiceName1, "0");
	//
	//		// detach parameter 
	//
	//		methodNode.detachParameterNode(par1Name);
	//
	//		// add new parameter without choices
	//
	//		String newPar1Name = "newPar1";
	//		MethodParameterNode newMethodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");
	//
	//		//  add choice with the same name to new parameter
	//
	//		addNewChoiceToMethodParameter(newMethodParameterNode, choiceName1, "0");
	//
	//		// attach - should create choice with name choice1-1
	//
	//		// TODO DE-NO USE ParameterAttacher
	////		methodNode.attachParameterNode(par1Name, newPar1Name, null);
	//		ParameterAttacher.attach(oldMethodParameterNode, newMethodParameterNode, null);
	//
	//		assertEquals(0, methodNode.getDetachedParametersCount());
	//		assertEquals(1, methodNode.getParametersCount());
	//
	//		// check old choice1 and new choice1-1
	//
	//		List<ChoiceNode> choiceNodes1 = newMethodParameterNode.getChoices();
	//
	//		ChoiceNode newChoiceNode1 = choiceNodes1.get(0);
	//		String newName1 = newChoiceNode1.getQualifiedName();
	//		assertEquals(newName1, choiceName1);
	//
	//		ChoiceNode newChoiceNode2 = choiceNodes1.get(1);
	//		String newName2 = newChoiceNode2.getName();
	//		assertEquals(newName2, choiceName1 + "-1");
	//	}

	//	@Test
	//	public void complexAttachTest() {
	//
	//		// Before:
	//
	//		// detached par1 with 3 cascading choices: choice1, choice2, choice3
	//		// 2 constraints containing par1 and these choices
	//
	//		// new parameter parN1 with one choiceN1
	//
	//		// Attach parameters:
	//
	//		// attaching par1 to parN1
	//		// with choiceConversion list: 'choice1:choice2' -> 'choiceN1'
	//
	//		// Result:
	//
	//		// parN1
	//		//    choiceN1
	//		//       choice3
	//		//    choice1
	//
	//		// constraints should contain parN1 and choices: choice1, choiceN1, choice3
	//
	//
	//		MethodNode methodNode = new MethodNode("method", null);
	//
	//		// create and add parameter, choice1, and child choice2
	//
	//		final String par1Name = "par1";
	//		final String oldChoiceName1 = "choice1";
	//		final String oldChoiceName2 = "choice2";		
	//		final String oldChoiceName3 = "choice3";
	//
	//		MethodParameterNode oldMethodParameterNode = addParameterToMethod(methodNode, par1Name, "String");
	//		ChoiceNode oldChoiceNode1 = addNewChoiceToMethodParameter(oldMethodParameterNode, oldChoiceName1, "0");
	//		ChoiceNode oldChoiceNode2 = addNewChoiceToChoice(oldChoiceNode1, oldChoiceName2, "0");
	//		ChoiceNode oldChoiceNode3 = addNewChoiceToChoice(oldChoiceNode2, oldChoiceName3, "0");
	//
	//		addNewSimpleConstraintToMethod(methodNode, "c1", oldMethodParameterNode, oldChoiceNode1, oldChoiceNode2);
	//		addNewSimpleConstraintToMethod(methodNode, "c2", oldMethodParameterNode, oldChoiceNode1, oldChoiceNode3);
	//
	//		// detach parameter 
	//
	//		methodNode.detachParameterNode(par1Name);
	//
	//		// add new parameter and choice
	//
	//		String newPar1Name = "parN1";
	//		MethodParameterNode newMethodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");
	//
	//		String newChoiceName1 = "choiceN1";
	//		ChoiceNode newChoiceNode1 = addNewChoiceToMethodParameter(newMethodParameterNode, newChoiceName1, "0");
	//
	//		// prepare choice conversion list for attachment
	//
	//		ChoiceConversionList choiceConversionList = new ChoiceConversionList();
	//		
	//		choiceConversionList.addItem(
	//				oldChoiceNode2.getQualifiedName(),
	//				ChoiceConversionOperation.MERGE,
	//				newChoiceNode1.getQualifiedName());
	//		
	//		// attach
	//
	//		ParameterAttacher.attach(oldMethodParameterNode, newMethodParameterNode, choiceConversionList);
	//
	//		// checking choices - children of parameter
	//
	//		List<ChoiceNode> newChoices1 = newMethodParameterNode.getChoices();
	//		assertEquals(2, newChoices1.size());
	//
	//		ChoiceNode attachedChoiceNode1 = newChoices1.get(0);
	//		assertEquals(newChoiceName1, attachedChoiceNode1.getName());
	//
	//		ChoiceNode attachedChoiceNode2 = newChoices1.get(1);
	//		assertEquals(oldChoiceName1, attachedChoiceNode2.getName());
	//
	//		// checking child of attached choice 1
	//
	//		List<ChoiceNode> newChoices11 = attachedChoiceNode1.getChoices();
	//		assertEquals(1, newChoices11.size());
	//
	//		ChoiceNode attachedChoiceNode11 = newChoices11.get(0);
	//		assertEquals(oldChoiceName3, attachedChoiceNode11.getName());
	//
	//		// check parameter from constraint - should be new 
	//
	//		checkParametersFromConstraints(methodNode, 0, newMethodParameterNode);
	//		checkParametersFromConstraints(methodNode, 1, newMethodParameterNode);
	//
	//		// check choices nodes from constraints
	//
	//		checkChoicesInConstraint(methodNode, 0, oldChoiceNode1, newChoiceNode1);
	//		checkChoicesInConstraint(methodNode, 1, oldChoiceNode1, oldChoiceNode3);
	//	}

	//	private void checkParametersFromConstraints(
	//			MethodNode methodNode, 
	//			int constraintIndex,
	//			MethodParameterNode expectedParameterFromConstraint) {
	//
	//		MethodParameterNode methodParameterNodeFromConstraint = 
	//				getMethodParameterNodeFromConstraintPrecondition(methodNode, constraintIndex);
	//		assertEquals(expectedParameterFromConstraint, methodParameterNodeFromConstraint);
	//
	//		methodParameterNodeFromConstraint = 
	//				getMethodParameterNodeFromConstraintPostcondition(methodNode, constraintIndex);
	//		assertEquals(expectedParameterFromConstraint, methodParameterNodeFromConstraint);
	//	}

	//	private void checkChoicesInConstraint(
	//			MethodNode methodNode, 
	//			int constraintIndex, 
	//			ChoiceNode expectedChoiceNodeFromPrecondition,
	//			ChoiceNode expectedChoiceNodeFromPostcondition) {
	//
	//		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, constraintIndex);
	//		assertEquals(expectedChoiceNodeFromPrecondition.getName(), choiceNodeFromPrecondition.getName());
	//
	//		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, constraintIndex);
	//		String name1 = expectedChoiceNodeFromPostcondition.getName();
	//		String name2 = choiceNodeFromPostcondition.getName();
	//		assertEquals(name1, name2);
	//	}


	// TODO DE-NO - move funcitons to helpers

	private void addNewSimpleConstraintToMethod(
			MethodNode methodNode,
			String constraintName,
			MethodParameterNode methodParameterNode,
			ChoiceNode choiceNode1,
			ChoiceNode choiceNode2) {

		RelationStatement relationStatement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode, EMathRelation.EQUAL, choiceNode1);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode, EMathRelation.LESS_THAN, choiceNode2);

		Constraint constraint = new Constraint(
				constraintName, 
				ConstraintType.EXTENDED_FILTER, 
				relationStatement1, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode(constraintName, constraint, null);

		methodNode.addConstraint(constraintNode);
	}

	private ChoiceNode getChoiceNodeFromConstraintPostcondition(
			MethodNode methodNode, int constraintIndex) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();

		ChoiceNode choiceNode = getChoiceNodeFromChoiceCondition(postcondition);

		return choiceNode;
	}

	private ChoiceNode getChoiceNodeFromConstraintPrecondition(
			MethodNode methodNode, int constraintIndex) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();

		ChoiceNode choiceNode = getChoiceNodeFromChoiceCondition(precondition);

		return choiceNode;
	}

	private ChoiceNode getChoiceNodeFromChoiceCondition(AbstractStatement abstractStatement) {

		RelationStatement relationStatement = (RelationStatement)abstractStatement; 

		IStatementCondition statementCondition = relationStatement.getCondition();

		ChoiceCondition choiceCondition = (ChoiceCondition)statementCondition;

		ChoiceNode choiceNode = choiceCondition.getRightChoice();

		return choiceNode;
	}

	//	private MethodParameterNode getMethodParameterNodeFromConstraintPrecondition(
	//			MethodNode methodNode, int constraintIndex) {
	//
	//		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);
	//
	//		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();
	//
	//		RelationStatement relationStatement = (RelationStatement)precondition; 
	//
	//		MethodParameterNode methodParameterNode = relationStatement.getLeftParameter();
	//
	//		return methodParameterNode;
	//	}

	//	private MethodParameterNode getMethodParameterNodeFromConstraintPostcondition(
	//			MethodNode methodNode, int constraintIndex) {
	//
	//		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);
	//
	//		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();
	//
	//		RelationStatement relationStatement = (RelationStatement)postcondition; 
	//
	//		MethodParameterNode methodParameterNode = relationStatement.getLeftParameter();
	//
	//		return methodParameterNode;
	//	}

}
