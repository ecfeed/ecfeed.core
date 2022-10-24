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

import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.IExtLanguageManager;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class MethodNodeHelperTest {

	@Test
	public void getMethodNameTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		String methodName = MethodNodeHelper.getName(methodNode, new ExtLanguageManagerForJava());
		assertEquals("method_1", methodName);

		methodName = MethodNodeHelper.getName(methodNode, new ExtLanguageManagerForSimple());
		assertEquals("method 1", methodName);
	}

	@Test
	public void findMethodParameterByNameTest() {

		MethodNode methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("arg_1", "String", "", false, null);
		methodNode.addParameter(methodParameterNode1);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("arg_2", "String", "", false, null);
		methodNode.addParameter(methodParameterNode2);

		// checks for simple language

		ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		MethodParameterNode foundMethodParameterNode =
				MethodNodeHelper.findMethodParameterByName("arg 1", methodNode, extLanguageManagerForSimple);
		assertEquals(foundMethodParameterNode, methodParameterNode1);

		foundMethodParameterNode =
				MethodNodeHelper.findMethodParameterByName("arg 2", methodNode, extLanguageManagerForSimple);
		assertEquals(foundMethodParameterNode, methodParameterNode2);

		foundMethodParameterNode =
				MethodNodeHelper.findMethodParameterByName("arg 3", methodNode, extLanguageManagerForSimple);
		assertNull(foundMethodParameterNode);

		// checks for Java language

		ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		foundMethodParameterNode =
				MethodNodeHelper.findMethodParameterByName("arg_1", methodNode, extLanguageManagerForJava);
		assertEquals(foundMethodParameterNode, methodParameterNode1);

		foundMethodParameterNode =
				MethodNodeHelper.findMethodParameterByName("arg_2", methodNode, extLanguageManagerForJava);
		assertEquals(foundMethodParameterNode, methodParameterNode2);

		foundMethodParameterNode =
				MethodNodeHelper.findMethodParameterByName("arg_3", methodNode, extLanguageManagerForJava);
		assertNull(foundMethodParameterNode);
	}

	@Test
	public void getParameterNamesTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		MethodParameterNode param1 = new MethodParameterNode("param1", "int", "0", false, null);
		methodNode.addParameter(param1);

		MethodParameterNode param2 = new MethodParameterNode("param2", "int", "0", false, null);
		methodNode.addParameter(param2);

		List<String> methodParameterNames = MethodNodeHelper.getParameterNames(methodNode, new ExtLanguageManagerForJava());

		assertEquals(2,  methodParameterNames.size());
		assertEquals("param1", methodParameterNames.get(0));
		assertEquals("param2", methodParameterNames.get(1));
	}

	@Test
	public void getParameterTypesTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		MethodParameterNode param1 = new MethodParameterNode("param1", "int", "0", false, null);
		methodNode.addParameter(param1);

		MethodParameterNode param2 = new MethodParameterNode("param2", "double", "0", false, null);
		methodNode.addParameter(param2);

		// java types

		List<String> methodParameterTypes = MethodNodeHelper.getParameterTypes(methodNode, new ExtLanguageManagerForJava());

		assertEquals(2,  methodParameterTypes.size());
		assertEquals("int", methodParameterTypes.get(0));
		assertEquals("double", methodParameterTypes.get(1));

		// simple types

		methodParameterTypes = MethodNodeHelper.getParameterTypes(methodNode, new ExtLanguageManagerForSimple());

		assertEquals(2,  methodParameterTypes.size());
		assertEquals("Number", methodParameterTypes.get(0));
		assertEquals("Number", methodParameterTypes.get(1));
	}


	@Test
	public void signatureCreateTest(){

		ClassNode classNode = new ClassNode("class1", null);

		MethodNode methodNode = new MethodNode("method_1", null);
		classNode.addMethod(methodNode);

		String signature = MethodNodeHelper.createSignature(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("method_1()", signature);

		signature = MethodNodeHelper.createSignature(methodNode, true, new ExtLanguageManagerForSimple());
		assertEquals("method 1()", signature);


		MethodParameterNode param1 = new MethodParameterNode("param1", "int", "0", false, null);
		methodNode.addParameter(param1);

		signature = MethodNodeHelper.createSignature(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("method_1(int param1)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, false, new ExtLanguageManagerForJava());
		assertEquals("method_1(int)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, true, new ExtLanguageManagerForSimple());
		assertEquals("method 1(Number: param1)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, false, new ExtLanguageManagerForSimple());
		assertEquals("method 1(Number)", signature);

		MethodParameterNode param2 = new MethodParameterNode("param2", "double", "0.0", true, null);
		methodNode.addParameter(param2);

		signature = MethodNodeHelper.createSignature(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("method_1(int param1, double param2)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, false, new ExtLanguageManagerForJava());
		assertEquals("method_1(int, double)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("method_1(int param1, [e]double param2)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, false, new ExtLanguageManagerForJava());
		assertEquals("method_1(int, [e]double)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, false, new ExtLanguageManagerForSimple());
		assertEquals("method 1(Number, Number)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, true, new ExtLanguageManagerForSimple());
		assertEquals("method 1(Number: param1, [e]Number: param2)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, false, new ExtLanguageManagerForSimple());
		assertEquals("method 1(Number, [e]Number)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("class1.method_1(int param1, double param2)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, false, new ExtLanguageManagerForJava());
		assertEquals("class1.method_1(int, double)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, true, new ExtLanguageManagerForSimple());
		assertEquals("class1.method 1(Number: param1, Number: param2)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, false, new ExtLanguageManagerForSimple());
		assertEquals("class1.method 1(Number, Number)", signature);

		// by external language

		String[] params = {"Number", "Text", "Logical"};
		signature = MethodNodeHelper.createSignature(
				"f 1",
				Arrays.asList(params),
				null,
				null,
				new ExtLanguageManagerForSimple());

		assertEquals("f 1(Number, Text, Logical)", signature);

		// with parameter names

		String[] paramNames = {"num", "txt", "log"};
		signature = MethodNodeHelper.createSignature(
				"f 1",
				Arrays.asList(params),
				Arrays.asList(paramNames),
				null,
				new ExtLanguageManagerForJava());

		assertEquals("f 1(Number num, Text txt, Logical log)", signature);

		// with expected decorations

		Boolean[] expDecorations = {true, false, true};
		signature = MethodNodeHelper.createSignature(
				"f 1",
				Arrays.asList(params),
				Arrays.asList(paramNames),
				Arrays.asList(expDecorations),
				new ExtLanguageManagerForSimple());

		assertEquals("f 1([e]Number: num, Text: txt, [e]Logical: log)", signature);

	}

	@Test
	public void validateMethodNameTest() {

		// valid without separator

		String errorMessage;

		errorMessage = MethodNodeHelper.validateMethodName("f1", new ExtLanguageManagerForJava());
		assertNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("f1", new ExtLanguageManagerForSimple());
		assertNull(errorMessage);


		// valid with separator

		errorMessage = MethodNodeHelper.validateMethodName("f_1", new ExtLanguageManagerForJava());
		assertNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("f 1", new ExtLanguageManagerForSimple());
		assertNull(errorMessage);


		// all allowed characters

		errorMessage =
				MethodNodeHelper.validateMethodName(
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_$",
						new ExtLanguageManagerForJava());

		assertNull(errorMessage);


		errorMessage =
				MethodNodeHelper.validateMethodName(
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 $",
						new ExtLanguageManagerForSimple());

		assertNull(errorMessage);


		// just dolar

		errorMessage = MethodNodeHelper.validateMethodName("$", new ExtLanguageManagerForJava());
		assertNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("$", new ExtLanguageManagerForSimple());
		assertNull(errorMessage);


		// invalid separator

		errorMessage = MethodNodeHelper.validateMethodName("f 1", new ExtLanguageManagerForJava());
		assertNotNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("f_1", new ExtLanguageManagerForSimple());
		assertNotNull(errorMessage);


		// invalid char

		errorMessage = MethodNodeHelper.validateMethodName("#", new ExtLanguageManagerForJava());
		assertNotNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("#", new ExtLanguageManagerForSimple());
		assertNotNull(errorMessage);


		// number at the front

		errorMessage = MethodNodeHelper.validateMethodName("1a", new ExtLanguageManagerForJava());
		assertNotNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName("1a", new ExtLanguageManagerForSimple());
		assertNotNull(errorMessage);


		// just separator

		errorMessage = MethodNodeHelper.validateMethodName("_a", new ExtLanguageManagerForJava());
		assertNull(errorMessage);

		errorMessage = MethodNodeHelper.validateMethodName(" a", new ExtLanguageManagerForSimple());
		assertNull(errorMessage);
	}

	@Test
	public void getParameterNamesAndTypesTest(){

		ClassNode classNode = new ClassNode("class1", null);

		MethodNode methodNode = new MethodNode("method_1", null);
		classNode.addMethod(methodNode);

		MethodParameterNode param1 = new MethodParameterNode("param1", "int", "0", false, null);
		methodNode.addParameter(param1);

		MethodParameterNode param2 = new MethodParameterNode("param2", "double", "0.0", true, null);
		methodNode.addParameter(param2);

		List<String> paramNames = MethodNodeHelper.getParameterNames(methodNode, new ExtLanguageManagerForJava());
		assertEquals(2, paramNames.size());
		assertEquals("param1", paramNames.get(0));
		assertEquals("param2", paramNames.get(1));

		List<String> paramTypes = MethodNodeHelper.getParameterTypes(methodNode, new ExtLanguageManagerForJava());
		assertEquals(2, paramTypes.size());
		assertEquals("int", paramTypes.get(0));
		assertEquals("double", paramTypes.get(1));

		paramTypes = MethodNodeHelper.getParameterTypes(methodNode, new ExtLanguageManagerForSimple());
		assertEquals(2, paramTypes.size());
		assertEquals("Number", paramTypes.get(0));
		assertEquals("Number", paramTypes.get(1));
	}

	@Test
	public void createSignatureOfParametersTest() {

		ClassNode classNode = new ClassNode("class1", null);

		MethodNode methodNode = new MethodNode("method_1", null);
		classNode.addMethod(methodNode);

		MethodParameterNode param1 = new MethodParameterNode("param_1", "int", "0", false, null);
		methodNode.addParameter(param1);

		MethodParameterNode param2 = new MethodParameterNode("param_2", "double", "0.0", true, null);
		methodNode.addParameter(param2);

		String signature =  MethodNodeHelper.createSignaturesOfParameters(methodNode, new ExtLanguageManagerForJava());
		assertEquals("int param_1, [e]double param_2", signature);

		signature =  MethodNodeHelper.createSignaturesOfParameters(methodNode, new ExtLanguageManagerForSimple());
		assertEquals("Number: param 1, [e]Number: param 2", signature);
	}

	@Test
	public void createNewParameterForSimpleTest() {

		IExtLanguageManager  extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		ClassNode classNode = new ClassNode("class1", null);

		// add method 1 - char

		MethodNode methodNode1a = new MethodNode("method_1", null);
		classNode.addMethod(methodNode1a);

		MethodParameterNode methodParameterNode1a =
				MethodNodeHelper.createNewParameter(methodNode1a, extLanguageManagerForSimple);

		assertEquals("char",  methodParameterNode1a.getType());

		methodNode1a.addParameter(methodParameterNode1a);

		// add method 2 - byte

		MethodNode methodNode1b = new MethodNode("method_1", null);
		classNode.addMethod(methodNode1b);

		MethodParameterNode methodParameterNode1b =
				MethodNodeHelper.createNewParameter(methodNode1b, extLanguageManagerForSimple);

		assertEquals("byte",  methodParameterNode1b.getType());

		methodNode1b.addParameter(methodParameterNode1b);

		// add method 3 - boolean

		MethodNode methodNode1c = new MethodNode("method_1", null);
		classNode.addMethod(methodNode1c);

		MethodParameterNode methodParameterNode1c =
				MethodNodeHelper.createNewParameter(methodNode1c, extLanguageManagerForSimple);

		assertEquals("boolean",  methodParameterNode1c.getType());

		methodNode1c.addParameter(methodParameterNode1c);

		// add method 4 - user type

		MethodNode methodNode1d = new MethodNode("method_1", null);
		classNode.addMethod(methodNode1d);

		MethodParameterNode methodParameterNode1d =
				MethodNodeHelper.createNewParameter(methodNode1d, extLanguageManagerForSimple);

		assertEquals("UserType",  methodParameterNode1d.getType());

		methodNode1d.addParameter(methodParameterNode1d);

		// add method 4 - user type 2

		MethodNode methodNode1e = new MethodNode("method_1", null);
		classNode.addMethod(methodNode1e);

		MethodParameterNode methodParameterNode1e =
				MethodNodeHelper.createNewParameter(methodNode1e, extLanguageManagerForSimple);

		assertEquals("UserType0",  methodParameterNode1e.getType());

		methodNode1e.addParameter(methodParameterNode1e);
	}

	@Test
	public void createNewParameterForJavaTest() {

		IExtLanguageManager  extLanguageManagerForJava = new ExtLanguageManagerForJava();

		ClassNode classNode = new ClassNode("class1", null);

		// add method 1 - int

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		MethodParameterNode methodParameterNode1 =
				MethodNodeHelper.createNewParameter(methodNode1, extLanguageManagerForJava);

		assertEquals("int",  methodParameterNode1.getType());

		methodNode1.addParameter(methodParameterNode1);

		// add method 2 - byte

		MethodNode methodNode2 = new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		MethodParameterNode methodParameterNode2 =
				MethodNodeHelper.createNewParameter(methodNode2, extLanguageManagerForJava);

		assertEquals("byte",  methodParameterNode2.getType());

		methodNode2.addParameter(methodParameterNode2);

		// add method 3 - short

		MethodNode methodNode3 = new MethodNode("method", null);
		classNode.addMethod(methodNode3);

		MethodParameterNode methodParameterNode3 =
				MethodNodeHelper.createNewParameter(methodNode3, extLanguageManagerForJava);

		assertEquals("short",  methodParameterNode3.getType());

		methodNode3.addParameter(methodParameterNode3);

		// add method 4 - long

		MethodNode methodNode4 = new MethodNode("method", null);
		classNode.addMethod(methodNode4);

		MethodParameterNode methodParameterNode4 =
				MethodNodeHelper.createNewParameter(methodNode4, extLanguageManagerForJava);

		assertEquals("long",  methodParameterNode4.getType());

		methodNode4.addParameter(methodParameterNode4);

		// add method 5 - float

		MethodNode methodNode5 = new MethodNode("method", null);
		classNode.addMethod(methodNode5);

		MethodParameterNode methodParameterNode5 =
				MethodNodeHelper.createNewParameter(methodNode5, extLanguageManagerForJava);

		assertEquals("float",  methodParameterNode5.getType());

		methodNode5.addParameter(methodParameterNode5);

		// add method 6

		MethodNode methodNode6 = new MethodNode("method", null);
		classNode.addMethod(methodNode6);

		MethodParameterNode methodParameterNode6 =
				MethodNodeHelper.createNewParameter(methodNode6, extLanguageManagerForJava);

		assertEquals("double",  methodParameterNode6.getType());

		methodNode6.addParameter(methodParameterNode6);

		// add method 7

		MethodNode methodNode7 = new MethodNode("method", null);
		classNode.addMethod(methodNode7);

		MethodParameterNode methodParameterNode7 =
				MethodNodeHelper.createNewParameter(methodNode7, extLanguageManagerForJava);

		assertEquals("String",  methodParameterNode7.getType());

		methodNode7.addParameter(methodParameterNode7);


		// add method 8

		MethodNode methodNode8 = new MethodNode("method", null);
		classNode.addMethod(methodNode8);

		MethodParameterNode methodParameterNode8 =
				MethodNodeHelper.createNewParameter(methodNode8, extLanguageManagerForJava);

		assertEquals("char",  methodParameterNode8.getType());

		methodNode8.addParameter(methodParameterNode8);

		// add method 9

		MethodNode methodNode9 = new MethodNode("method", null);
		classNode.addMethod(methodNode9);

		MethodParameterNode methodParameterNode9 =
				MethodNodeHelper.createNewParameter(methodNode9, extLanguageManagerForJava);

		assertEquals("boolean",  methodParameterNode9.getType());

		methodNode9.addParameter(methodParameterNode9);

		// add method 10

		MethodNode methodNode10 = new MethodNode("method", null);
		classNode.addMethod(methodNode10);

		MethodParameterNode methodParameterNode10 =
				MethodNodeHelper.createNewParameter(methodNode10, extLanguageManagerForJava);

		assertEquals("default.UserType",  methodParameterNode10.getType());

		methodNode10.addParameter(methodParameterNode10);
	}

	@Test
	public void createTwoParametersForJavaTest() {

		IExtLanguageManager  extLanguageManagerForJava = new ExtLanguageManagerForJava();

		ClassNode classNode = new ClassNode("class1", null);

		// add method 1 - parameters int, int

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		MethodParameterNode methodParameterNode11 =
				MethodNodeHelper.createNewParameter(methodNode1, extLanguageManagerForJava);

		assertEquals("int",  methodParameterNode11.getType());
		methodNode1.addParameter(methodParameterNode11);

		MethodParameterNode methodParameterNode12 =
				MethodNodeHelper.createNewParameter(methodNode1, extLanguageManagerForJava);

		assertEquals("int",  methodParameterNode12.getType());
		methodNode1.addParameter(methodParameterNode12);

		// add method 2 - parameters int, byte

		MethodNode methodNode2 = new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		MethodParameterNode methodParameterNode21 =
				MethodNodeHelper.createNewParameter(methodNode2, extLanguageManagerForJava);

		assertEquals("int",  methodParameterNode21.getType());
		methodNode2.addParameter(methodParameterNode21);

		MethodParameterNode methodParameterNode22 =
				MethodNodeHelper.createNewParameter(methodNode2, extLanguageManagerForJava);

		assertEquals("byte",  methodParameterNode22.getType());
		methodNode2.addParameter(methodParameterNode22);
	}

	@Test
	public void getConstraintNamesTest() {

		MethodNode methodNode = new MethodNode("method", null);

		Constraint constraint = new Constraint("c 1", ConstraintType.EXTENDED_FILTER, null, null, null);

		ConstraintNode constraintNode = new ConstraintNode("cn 1", constraint,null);
		methodNode.addConstraint(constraintNode);

		Set<String> names = MethodNodeHelper.getConstraintNames(methodNode, new ExtLanguageManagerForJava());

		assertEquals(1, names.size());

		List<String> namesArray = new ArrayList<>(names);

		assertEquals("cn 1", namesArray.get(0));
	}

	@Test
	public void groupTestCasesTest() {

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = new ClassNode("class", null);
		rootNode.addClass(classNode);

		MethodNode methodNode = new MethodNode("method", null);
		classNode.addMethod(methodNode);

		MethodParameterNode methodParameterNode =
				new MethodParameterNode(
						"par","int", "0",false,null);

		methodNode.addParameter(methodParameterNode);

		ChoiceNode choiceNode = new ChoiceNode("c", "1", null);
		methodParameterNode.addChoice(choiceNode);

		List<ChoiceNode> choices = new ArrayList<>();
		choices.add(choiceNode);

		TestCaseNode testCase1 = new TestCaseNode("t1", null, choices);
		methodNode.addTestCase(testCase1);

		TestCaseNode testCase2 = new TestCaseNode("t1", null, choices);
		methodNode.addTestCase(testCase2);

		List<TestSuiteNode> testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
		checkTestSuites1(testSuiteNodes, testCase1, testCase2);

		// the second time - result should be the same

		testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
		checkTestSuites1(testSuiteNodes, testCase1, testCase2);

		// the third, different test case

		TestCaseNode testCase3 = new TestCaseNode("txx", null, choices);
		methodNode.addTestCase(testCase3);

		testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
		checkTestSuites2(testSuiteNodes, testCase1, testCase2, testCase3);

		testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
		checkTestSuites2(testSuiteNodes, testCase1, testCase2, testCase3);

		// removing the third test case
		methodNode.removeTestCase(testCase3);

		testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
		checkTestSuites1(testSuiteNodes, testCase1, testCase2);
	}

	public void checkTestSuites1(List<TestSuiteNode> testSuiteNodes, TestCaseNode testCase1, TestCaseNode testCase2) {

		assertEquals(1, testSuiteNodes.size());

		TestSuiteNode testSuiteNode = testSuiteNodes.get(0);
		List<IAbstractNode> testCaseNodes = testSuiteNode.getChildren();

		assertEquals(testCase1, testCaseNodes.get(0));
		assertEquals(testCase2, testCaseNodes.get(1));

		String testSuiteName = testSuiteNode.getName();

		assertEquals(testSuiteName, testCase1.getName());
		assertEquals(testSuiteName, testCase2.getName());
	}

	public void checkTestSuites2(
			List<TestSuiteNode> testSuiteNodes,
			TestCaseNode testCase1, TestCaseNode testCase2, TestCaseNode testCase3) {

		assertEquals(2, testSuiteNodes.size());

		TestSuiteNode testSuiteNode1 = testSuiteNodes.get(0);
		List<IAbstractNode> testCaseNodes1 = testSuiteNode1.getChildren();

		assertEquals(2, testCaseNodes1.size());

		assertEquals(testCase1, testCaseNodes1.get(0));
		assertEquals(testCase2, testCaseNodes1.get(1));

		TestSuiteNode testSuiteNode2 = testSuiteNodes.get(1);
		List<IAbstractNode> testCaseNodes2 = testSuiteNode2.getChildren();

		assertEquals(1, testCaseNodes2.size());
		assertEquals(testCase3, testCaseNodes2.get(0));
	}

	@Test
	public void findExpectedParameterNotUsedInAssignmentTest() {

		// prepare method with one parameter

		MethodNode methodNode = new MethodNode("fun",  null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		// prepare constraint with empty list of assignments

		StaticStatement trueStatement = new StaticStatement(true, null);

		StatementArray statementArray1 = new StatementArray(StatementArrayOperator.AND, null);

		Constraint constraint =
				new Constraint(
						"c",
						ConstraintType.ASSIGNMENT,
						trueStatement,
						statementArray1,
						null);

		ConstraintNode constraintNode = new ConstraintNode("cn", constraint, null);

		methodNode.addConstraint(constraintNode);

		// check with not expected parameter

		MethodParameterNode resultMethodParameterNode =
				MethodNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);

		assertNull(resultMethodParameterNode);

		// check with expected parameter

		methodParameterNode1.setExpected(true);

		resultMethodParameterNode =
				MethodNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);

		assertEquals(methodParameterNode1, resultMethodParameterNode);

		// add assignment to constraint

		AssignmentStatement assignmentStatement1 =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode1, "6");
		statementArray1.addStatement(assignmentStatement1);

		resultMethodParameterNode =
				MethodNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);
		assertNull(resultMethodParameterNode);

		// add the second parameter - not expected

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		resultMethodParameterNode =
				MethodNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);

		assertNull(resultMethodParameterNode);

		// change the second parameter to expected

		methodParameterNode2.setExpected(true);

		resultMethodParameterNode =
				MethodNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);
		assertEquals(methodParameterNode2, resultMethodParameterNode);

		//  use the second parameter in assignment

		AssignmentStatement assignmentStatement2 =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode2, "3");
		statementArray1.addStatement(assignmentStatement2);

		resultMethodParameterNode =
				MethodNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);

		assertNull(resultMethodParameterNode);
	}
}
