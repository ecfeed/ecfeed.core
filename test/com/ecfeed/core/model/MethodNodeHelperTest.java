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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IntegerHolder;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.TestCasesFilteringDirection;

public class MethodNodeHelperTest {

	@Test
	public void getMethodNameTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		String methodName = AbstractNodeHelper.getName(methodNode, new ExtLanguageManagerForJava());
		assertEquals("method_1", methodName);

		methodName = AbstractNodeHelper.getName(methodNode, new ExtLanguageManagerForSimple());
		assertEquals("method 1", methodName);
	}

	@Test
	public void findMethodParameterByNameTest() {

		MethodNode methodNode = new MethodNode("method", null);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("arg_1", "String", "", false, null);
		methodNode.addParameter(methodParameterNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("arg_2", "String", "", false, null);
		methodNode.addParameter(methodParameterNode2);

		// checks for simple language

		ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		BasicParameterNode foundMethodParameterNode =
				BasicParameterNodeHelper.findBasicParameterByQualifiedName("arg 1", methodNode, extLanguageManagerForSimple);
		assertEquals(foundMethodParameterNode, methodParameterNode1);

		foundMethodParameterNode =
				BasicParameterNodeHelper.findBasicParameterByQualifiedName("arg 2", methodNode, extLanguageManagerForSimple);
		assertEquals(foundMethodParameterNode, methodParameterNode2);

		foundMethodParameterNode =
				BasicParameterNodeHelper.findBasicParameterByQualifiedName("arg 3", methodNode, extLanguageManagerForSimple);
		assertNull(foundMethodParameterNode);

		// checks for Java language

		ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		foundMethodParameterNode =
				BasicParameterNodeHelper.findBasicParameterByQualifiedName("arg_1", methodNode, extLanguageManagerForJava);
		assertEquals(foundMethodParameterNode, methodParameterNode1);

		foundMethodParameterNode =
				BasicParameterNodeHelper.findBasicParameterByQualifiedName("arg_2", methodNode, extLanguageManagerForJava);
		assertEquals(foundMethodParameterNode, methodParameterNode2);

		foundMethodParameterNode =
				BasicParameterNodeHelper.findBasicParameterByQualifiedName("arg_3", methodNode, extLanguageManagerForJava);
		assertNull(foundMethodParameterNode);
	}

	@Test
	public void getParameterNamesTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		BasicParameterNode param1 = new BasicParameterNode("param1", "int", "0", false, null);
		methodNode.addParameter(param1);

		BasicParameterNode param2 = new BasicParameterNode("param2", "int", "0", false, null);
		methodNode.addParameter(param2);

		List<String> methodParameterNames = ParametersParentNodeHelper.getParameterNames(methodNode, new ExtLanguageManagerForJava());

		assertEquals(2,  methodParameterNames.size());
		assertEquals("param1", methodParameterNames.get(0));
		assertEquals("param2", methodParameterNames.get(1));
	}

	@Test
	public void getParameterTypesTest(){

		MethodNode methodNode = new MethodNode("method_1", null);

		BasicParameterNode param1 = new BasicParameterNode("param1", "int", "0", false, null);
		methodNode.addParameter(param1);

		BasicParameterNode param2 = new BasicParameterNode("param2", "double", "0", false, null);
		methodNode.addParameter(param2);

		// java types

		List<String> methodParameterTypes = ParametersParentNodeHelper.getParameterTypes(methodNode, new ExtLanguageManagerForJava());

		assertEquals(2,  methodParameterTypes.size());
		assertEquals("int", methodParameterTypes.get(0));
		assertEquals("double", methodParameterTypes.get(1));

		// simple types

		methodParameterTypes = ParametersParentNodeHelper.getParameterTypes(methodNode, new ExtLanguageManagerForSimple());

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


		BasicParameterNode param1 = new BasicParameterNode("param1", "int", "0", false, null);
		methodNode.addParameter(param1);

		signature = MethodNodeHelper.createSignature(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("method_1(param1 : int)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, false, new ExtLanguageManagerForJava());
		assertEquals("method_1(int)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, true, new ExtLanguageManagerForSimple());
		assertEquals("method 1(param1 : Number)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, false, new ExtLanguageManagerForSimple());
		assertEquals("method 1(Number)", signature);

		BasicParameterNode param2 = new BasicParameterNode("param2", "double", "0.0", true, null);
		methodNode.addParameter(param2);

		signature = MethodNodeHelper.createSignature(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("method_1(param1 : int, param2 : double)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, false, new ExtLanguageManagerForJava());
		assertEquals("method_1(int, double)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("method_1(param1 : int, param2[EXP] : double)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, false, new ExtLanguageManagerForJava());
		assertEquals("method_1(int, [EXP]double)", signature);

		signature = MethodNodeHelper.createSignature(methodNode, false, new ExtLanguageManagerForSimple());
		assertEquals("method 1(Number, Number)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, true, new ExtLanguageManagerForSimple());
		assertEquals("method 1(param1 : Number, param2[EXP] : Number)", signature);

		signature = MethodNodeHelper.createSignatureWithExpectedDecorations(methodNode, false, new ExtLanguageManagerForSimple());
		assertEquals("method 1(Number, [EXP]Number)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, true, new ExtLanguageManagerForJava());
		assertEquals("class1.method_1(param1 : int, param2 : double)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, false, new ExtLanguageManagerForJava());
		assertEquals("class1.method_1(int, double)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, true, new ExtLanguageManagerForSimple());
		assertEquals("class1.method 1(param1 : Number, param2 : Number)", signature);

		signature = MethodNodeHelper.createLongSignature(methodNode, false, new ExtLanguageManagerForSimple());
		assertEquals("class1.method 1(Number, Number)", signature);

		//		// by external language
		//
		//		String[] params = {"Number", "Text", "Logical"};
		//		signature = MethodNodeHelper.createSignature(
		//				"f 1",
		//				Arrays.asList(params),
		//				null,
		//				null,
		//				new ExtLanguageManagerForSimple());
		//
		//		assertEquals("f 1(Number, Text, Logical)", signature);
		//
		//		// with parameter names
		//
		//		String[] paramNames = {"num", "txt", "log"};
		//		signature = MethodNodeHelper.createSignature(
		//				"f 1",
		//				Arrays.asList(params),
		//				Arrays.asList(paramNames),
		//				null,
		//				new ExtLanguageManagerForJava());
		//
		//		assertEquals("f 1(Number num, Text txt, Logical log)", signature);
		//
		//		// with expected decorations
		//
		//		Boolean[] expDecorations = {true, false, true};
		//		signature = MethodNodeHelper.createSignature(
		//				"f 1",
		//				Arrays.asList(params),
		//				Arrays.asList(paramNames),
		//				Arrays.asList(expDecorations),
		//				new ExtLanguageManagerForSimple());
		//
		//		assertEquals("f 1([e]Number: num, Text: txt, [e]Logical: log)", signature);
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

		BasicParameterNode param1 = new BasicParameterNode("param1", "int", "0", false, null);
		methodNode.addParameter(param1);

		BasicParameterNode param2 = new BasicParameterNode("param2", "double", "0.0", true, null);
		methodNode.addParameter(param2);

		List<String> paramNames = ParametersParentNodeHelper.getParameterNames(methodNode, new ExtLanguageManagerForJava());
		assertEquals(2, paramNames.size());
		assertEquals("param1", paramNames.get(0));
		assertEquals("param2", paramNames.get(1));

		List<String> paramTypes = ParametersParentNodeHelper.getParameterTypes(methodNode, new ExtLanguageManagerForJava());
		assertEquals(2, paramTypes.size());
		assertEquals("int", paramTypes.get(0));
		assertEquals("double", paramTypes.get(1));

		paramTypes = ParametersParentNodeHelper.getParameterTypes(methodNode, new ExtLanguageManagerForSimple());
		assertEquals(2, paramTypes.size());
		assertEquals("Number", paramTypes.get(0));
		assertEquals("Number", paramTypes.get(1));
	}

	@Test
	public void createSignatureOfParametersTest() {

		ClassNode classNode = new ClassNode("class1", null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method_1", true, null); 

		MethodNodeHelper.addNewBasicParameter(methodNode, "param_1", "int", "0", true, null);

		MethodNodeHelper.addNewBasicParameter(methodNode, "param_2", "double", "0", true, null);

		String signature =  MethodNodeHelper.createSignaturesOfParameters(methodNode, new ExtLanguageManagerForJava());
		assertEquals("param_1 : int, param_2 : double", signature);

		signature =  MethodNodeHelper.createSignaturesOfParameters(methodNode, new ExtLanguageManagerForSimple());
		assertEquals("param_1 : Number, param_2 : Number", signature);
	}

	@Test
	public void createNewParameterForSimpleTest() {

		IExtLanguageManager  extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		ClassNode classNode = new ClassNode("class1", null);

		// add method 1 - char

		MethodNode methodNode1a = new MethodNode("method_1", null);
		classNode.addMethod(methodNode1a);

		BasicParameterNode methodParameterNode1a =
				ParametersAndConstraintsParentNodeHelper.createBasicParameter(methodNode1a, extLanguageManagerForSimple);

		assertEquals(JavaLanguageHelper.TYPE_NAME_STRING,  methodParameterNode1a.getType());

		methodNode1a.addParameter(methodParameterNode1a);

		// add method 2

		MethodNode methodNode1b = new MethodNode("method_2", null);
		classNode.addMethod(methodNode1b);

		BasicParameterNode methodParameterNode1b =
				ParametersAndConstraintsParentNodeHelper.createBasicParameter(methodNode1b, extLanguageManagerForSimple);

		assertEquals(JavaLanguageHelper.TYPE_NAME_STRING,  methodParameterNode1b.getType());

		methodNode1b.addParameter(methodParameterNode1b);

		// add method 3 - boolean

		MethodNode methodNode1c = new MethodNode("method_3", null);
		classNode.addMethod(methodNode1c);

		BasicParameterNode methodParameterNode1c =
				ParametersAndConstraintsParentNodeHelper.createBasicParameter(methodNode1c, extLanguageManagerForSimple);

		assertEquals(JavaLanguageHelper.TYPE_NAME_STRING,  methodParameterNode1c.getType());

		methodNode1c.addParameter(methodParameterNode1c);

		// add method 4 - user type

		MethodNode methodNode1d = new MethodNode("method_4", null);
		classNode.addMethod(methodNode1d);

		BasicParameterNode methodParameterNode1d =
				ParametersAndConstraintsParentNodeHelper.createBasicParameter(methodNode1d, extLanguageManagerForSimple);

		assertEquals(JavaLanguageHelper.TYPE_NAME_STRING,  methodParameterNode1d.getType());

		methodNode1d.addParameter(methodParameterNode1d);

		// add method 4 - user type 2

		MethodNode methodNode1e = new MethodNode("method_5", null);
		classNode.addMethod(methodNode1e);

		BasicParameterNode methodParameterNode1e =
				ParametersAndConstraintsParentNodeHelper.createBasicParameter(methodNode1e, extLanguageManagerForSimple);

		assertEquals(JavaLanguageHelper.TYPE_NAME_STRING,  methodParameterNode1e.getType());

		methodNode1e.addParameter(methodParameterNode1e);
	}

	@Test
	public void getConstraintNamesTest() {

		IParametersAndConstraintsParentNode methodNode = new MethodNode("method", null);

		Constraint constraint = new Constraint("c 1", ConstraintType.EXTENDED_FILTER, null, null, null);

		ConstraintNode constraintNode = new ConstraintNode("cn 1", constraint,null);
		methodNode.addConstraint(constraintNode);

		Set<String> names = methodNode.getNamesOfConstraints();

		assertEquals(1, names.size());

		List<String> namesArray = new ArrayList<>(names);

		assertEquals("cn 1", namesArray.get(0));
	}

	//	@Test
	//	public void groupTestCasesTest() {
	//
	//		RootNode rootNode = new RootNode("root", null);
	//
	//		ClassNode classNode = new ClassNode("class", null);
	//		rootNode.addClass(classNode);
	//
	//		MethodNode methodNode = new MethodNode("method", null);
	//		classNode.addMethod(methodNode);
	//
	//		BasicParameterNode methodParameterNode =
	//				new BasicParameterNode(
	//						"par","int", "0",false,null);
	//
	//		methodNode.addParameter(methodParameterNode);
	//
	//		ChoiceNode choiceNode = new ChoiceNode("c", "1", null);
	//		methodParameterNode.addChoice(choiceNode);
	//
	//		List<ChoiceNode> choices = new ArrayList<>();
	//		choices.add(choiceNode);
	//
	//		TestCaseNode testCase1 = new TestCaseNode("t1", null, choices);
	//		methodNode.addTestCase(testCase1);
	//
	//		TestCaseNode testCase2 = new TestCaseNode("t1", null, choices);
	//		methodNode.addTestCase(testCase2);
	//
	//		List<TestSuiteNode> testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
	//		checkTestSuites1(testSuiteNodes, testCase1, testCase2);
	//
	//		// the second time - result should be the same
	//
	//		testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
	//		checkTestSuites1(testSuiteNodes, testCase1, testCase2);
	//
	//		// the third, different test case
	//
	//		TestCaseNode testCase3 = new TestCaseNode("txx", null, choices);
	//		methodNode.addTestCase(testCase3);
	//
	//		testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
	//		checkTestSuites2(testSuiteNodes, testCase1, testCase2, testCase3);
	//
	//		testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
	//		checkTestSuites2(testSuiteNodes, testCase1, testCase2, testCase3);
	//
	//		// removing the third test case
	//		methodNode.removeTestCase(testCase3);
	//
	//		testSuiteNodes = MethodNodeHelper.createGroupingTestSuites(methodNode);
	//		checkTestSuites1(testSuiteNodes, testCase1, testCase2);
	//	}

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

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
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

		BasicParameterNode resultMethodParameterNode =
				ParametersAndConstraintsParentNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);

		assertNull(resultMethodParameterNode);

		// check with expected parameter

		methodParameterNode1.setExpected(true);

		resultMethodParameterNode =
				ParametersAndConstraintsParentNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);

		assertEquals(methodParameterNode1, resultMethodParameterNode);

		// add assignment to constraint

		AssignmentStatement assignmentStatement1 =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode1, "6");
		statementArray1.addStatement(assignmentStatement1);

		resultMethodParameterNode =
				ParametersAndConstraintsParentNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);
		assertNull(resultMethodParameterNode);

		// add the second parameter - not expected

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		resultMethodParameterNode =
				ParametersAndConstraintsParentNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);

		assertNull(resultMethodParameterNode);

		// change the second parameter to expected

		methodParameterNode2.setExpected(true);

		resultMethodParameterNode =
				ParametersAndConstraintsParentNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);
		assertEquals(methodParameterNode2, resultMethodParameterNode);

		//  use the second parameter in assignment

		AssignmentStatement assignmentStatement2 =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode2, "3");
		statementArray1.addStatement(assignmentStatement2);

		resultMethodParameterNode =
				ParametersAndConstraintsParentNodeHelper.findExpectedParameterNotUsedInAssignment(methodNode, constraint);

		assertNull(resultMethodParameterNode);
	}

	@Test
	public void shouldFilterNotRandomizedTestCasesForPositiveFilters() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "int", "0", true, null);

		ChoiceNode choiceNode1 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "choice1", "1", false, true, null);

		ChoiceNode choiceNode2 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "choice2", "2", false, true, null);

		// constraint

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, EMathRelation.EQUAL, choiceNode1);

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.BASIC_FILTER, precondition, postcondition, null);

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(constraint);

		// list of test cases

		List<TestCase> srcTestCases = new ArrayList<TestCase>();

		// test case 1

		List<ChoiceNode> choiceNodes1 = new ArrayList<>();
		choiceNodes1.add(choiceNode1);
		TestCase testCase1 = new TestCase(choiceNodes1);
		srcTestCases.add(testCase1);

		// test case 2

		List<ChoiceNode> choiceNodes2 = new ArrayList<>();
		choiceNodes2.add(choiceNode2);
		TestCase testCase2 = new TestCase(choiceNodes2);
		srcTestCases.add(testCase2);

		IntegerHolder countOfAddedTestCases = new IntegerHolder(0);

		// filter 

		List<TestCase> filteredTestCases = 
				MethodNodeHelper.filterTestCases(
						methodNode, 
						srcTestCases,
						"suite2",
						constraints, 
						TestCasesFilteringDirection.POSITIVE,
						true, 
						countOfAddedTestCases);

		// check
		
		assertEquals(1, (int)countOfAddedTestCases.get());
		assertEquals(1, filteredTestCases.size());

		TestCase dstTestCase = filteredTestCases.get(0);
		ChoiceNode dstChoiceNode = dstTestCase.getListOfChoiceNodes().get(0);
		assertEquals(choiceNode1.getName(), dstChoiceNode.getName());
	}

	@Test
	public void shouldFilterNotRandomizedTestCasesForNegativeFilters() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "int", "0", true, null);

		ChoiceNode choiceNode1 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "choice1", "1", false, true, null);

		ChoiceNode choiceNode2 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "choice2", "2", false, true, null);

		// constraint

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, EMathRelation.EQUAL, choiceNode1);

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.BASIC_FILTER, precondition, postcondition, null);

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(constraint);

		// list of test cases

		List<TestCase> srcTestCases = new ArrayList<TestCase>();

		// test case 1

		List<ChoiceNode> choiceNodes1 = new ArrayList<>();
		choiceNodes1.add(choiceNode1);
		TestCase testCase1 = new TestCase(choiceNodes1);
		srcTestCases.add(testCase1);

		// test case 2

		List<ChoiceNode> choiceNodes2 = new ArrayList<>();
		choiceNodes2.add(choiceNode2);
		TestCase testCase2 = new TestCase(choiceNodes2);
		srcTestCases.add(testCase2);

		IntegerHolder countOfAddedTestCases = new IntegerHolder(0);

		// filter 

		List<TestCase> filteredTestCases = 
				MethodNodeHelper.filterTestCases(
						methodNode, 
						srcTestCases,
						"suite2",
						constraints, 
						TestCasesFilteringDirection.NEGATIVE,
						true, 
						countOfAddedTestCases);

		// check
		
		assertEquals(1, (int)countOfAddedTestCases.get());
		assertEquals(1, filteredTestCases.size());

		TestCase dstTestCase = filteredTestCases.get(0);
		ChoiceNode dstChoiceNode = dstTestCase.getListOfChoiceNodes().get(0);
		assertEquals(choiceNode2.getName(), dstChoiceNode.getName());
	}
	
}
