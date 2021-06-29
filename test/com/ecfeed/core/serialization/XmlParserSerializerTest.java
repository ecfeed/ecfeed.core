/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.*;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.*;
import org.junit.Test;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.model.serialization.SerializationConstants;
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;

// TODO check all tests for java.lang.NullPointerException

public class XmlParserSerializerTest {

	private final int TEST_RUNS = 10;

	private final int MAX_CLASSES = 5;
	private final int MAX_METHODS = 5;
	private final int MAX_PARAMETERS = 5;
	private final int MAX_PARTITIONS = 10;
	private final int MAX_PARTITION_LEVELS = 5;
	private final int MAX_PARTITION_LABELS = 5;
	private final int MAX_CONSTRAINTS = 5;
	private final int MAX_TEST_CASES = 50;

	Random rand = new Random();
	static int nextInt = 0;

	private final String[] CATEGORY_TYPES = new String[]{
			SerializationConstants.TYPE_NAME_BOOLEAN, SerializationConstants.TYPE_NAME_BYTE, SerializationConstants.TYPE_NAME_CHAR,
			SerializationConstants.TYPE_NAME_DOUBLE, SerializationConstants.TYPE_NAME_FLOAT, SerializationConstants.TYPE_NAME_INT,
			SerializationConstants.TYPE_NAME_LONG, SerializationConstants.TYPE_NAME_SHORT, SerializationConstants.TYPE_NAME_STRING
	};

	@Test
	public void randomTest() {
		try {
			for(int i = 0; i < TEST_RUNS; ++i){
				RootNode model = createRootNode(rand.nextInt(MAX_CLASSES) + 1);
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				ModelSerializer serializer = 
						new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());
				ModelParser parser = new ModelParser();
				serializer.serialize(model);
				ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
				RootNode parsedModel = parser.parseModel(istream, null, new ListOfStrings());
				compareModels(model, parsedModel);

			}
		} catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseChoiceTestVersion3() {
		parseChoiceTest(3);
	}	

	@Test
	public void parseConditionStatementTest(){
		try{
			int version = ModelVersionDistributor.getCurrentSoftwareVersion();
			RootNode root = new RootNode("root", null, version);
			ClassNode classNode = new ClassNode("classNode", null);
			MethodNode method = new MethodNode("method", null);
			MethodParameterNode choicesParentParameter =
					new MethodParameterNode("choicesParentParameter", JavaLanguageHelper.TYPE_NAME_STRING, "0", false, null);
			MethodParameterNode expectedParameter =
					new MethodParameterNode("expectedParameter", JavaLanguageHelper.TYPE_NAME_CHAR, "0", true, null);
			expectedParameter.setDefaultValueString("d");
			ChoiceNode choice1 = new ChoiceNode("choice", "p", null);
			choice1.setParent(choicesParentParameter);
			ChoiceNode choice2 = new ChoiceNode("expected", "s", null);
			choice2.setParent(expectedParameter);

			List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
			testData.add(choice1);
			testData.add(choice2);
			TestCaseNode testCase = new TestCaseNode("test", null, testData);

			Constraint constraintExtendedFilter = new Constraint(
					"constraint",
					ConstraintType.EXTENDED_FILTER,
					new StaticStatement(true, null),
					RelationStatement.createRelationStatementWithChoiceCondition(choicesParentParameter, EMathRelation.EQUAL, choice1),
					null);

			Constraint constraintBasicFilter = new Constraint(
					"constraint",
					ConstraintType.BASIC_FILTER,
					new StaticStatement(true, null),
					RelationStatement.createRelationStatementWithChoiceCondition(choicesParentParameter, EMathRelation.EQUAL, choice1),
					null);

			Constraint constraintAssignment = new Constraint(
					"constraint",
					ConstraintType.ASSIGNMENT,
					new StaticStatement(true, null),
					new StatementArray(StatementArrayOperator.AND, null),
					null);

			Constraint labelConstraint = 
					new Constraint(
							"constraint",
							ConstraintType.EXTENDED_FILTER,
							new StaticStatement(true, null),
							RelationStatement.createRelationStatementWithLabelCondition(choicesParentParameter, EMathRelation.EQUAL, "label"),
							null);

			Constraint expectedConstraint =
					new Constraint(
							"constraint",
							ConstraintType.ASSIGNMENT,
							new StaticStatement(true, null),
							new ExpectedValueStatement(
									expectedParameter,
									new ChoiceNode("expected", "n", null),
									new JavaPrimitiveTypePredicate()),
							null
                    );

			ConstraintNode choiceConstraintNodeExtendedFilter =
					new ConstraintNode("choice constraint extended filter", constraintExtendedFilter, null);

			ConstraintNode choiceConstraintNodeBasicFilter =
					new ConstraintNode("choice constraint basic filter", constraintBasicFilter, null);

			ConstraintNode constraintNodeAssignment =
					new ConstraintNode("assignment constraint ", constraintAssignment, null);

			ConstraintNode labelConstraintNode =
					new ConstraintNode("label constraint", labelConstraint, null);

			ConstraintNode expectedConstraintNode =
					new ConstraintNode("expected constraint", expectedConstraint, null);

			root.addClass(classNode);
			classNode.addMethod(method);
			method.addParameter(choicesParentParameter);
			method.addParameter(expectedParameter);
			choicesParentParameter.addChoice(choice1);
			method.addTestCase(testCase);
			method.addConstraint(labelConstraintNode);
			method.addConstraint(choiceConstraintNodeExtendedFilter);
			method.addConstraint(choiceConstraintNodeBasicFilter);
			method.addConstraint(constraintNodeAssignment);
			method.addConstraint(expectedConstraintNode);

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, version);
			serializer.serialize(root);

			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			RootNode parsedModel = parser.parseModel(istream, null, new ListOfStrings());
			compareModels(root, parsedModel);
		}
		catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void serializeAndParseAssignmentConstraintTest() {

		int modelVersion = 4;

		RootNode root = new RootNode("root", null, modelVersion);

		ClassNode classNode = new ClassNode("classNode", null);
		root.addClass(classNode);

		MethodNode methodNode = new MethodNode("method", null);
		classNode.addMethod(methodNode);

		// method parameter 1 node with choice

		MethodParameterNode methodParameterNode1 = new MethodParameterNode(
				"par1",
				"int",
				"1",
				false,
				null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// method parameter 2 node with choice

		MethodParameterNode methodParameterNode2 = new MethodParameterNode(
				"par2",
				"int",
				"2",
				true,
				null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode21 = new ChoiceNode("choice21", "21",  null);
		methodParameterNode2.addChoice(choiceNode21);

		// assignment with value condition

		StatementArray statementArray  = new StatementArray(StatementArrayOperator.ASSIGN, null);

		AbstractStatement assignmentWithValueCondition =
				AssignmentStatement.createAssignmentWithValueCondition(methodParameterNode2, "5");
		statementArray.addStatement(assignmentWithValueCondition);

		// assignment with parameter condition

		AbstractStatement assignmentWithParameterCondition =
				AssignmentStatement.createAssignmentWithParameterCondition(methodParameterNode2, methodParameterNode1);
		statementArray.addStatement(assignmentWithParameterCondition);

		// assignment with choice condition

		AbstractStatement assignmentWithChoiceCondition =
				AssignmentStatement.createAssignmentWithChoiceCondition(methodParameterNode2, choiceNode21);
		statementArray.addStatement(assignmentWithChoiceCondition);

		StaticStatement precondition = new StaticStatement(true, null);
		AbstractStatement postcondition = statementArray;

		// constraint

		Constraint constraint = new Constraint(
				"constraint",
				ConstraintType.ASSIGNMENT,
				precondition,
				postcondition,
				null);

		ConstraintNode constraintNode = new ConstraintNode("cn", constraint, null);
		methodNode.addConstraint(constraintNode);

		// serializing to stream

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, modelVersion);

		try {
			serializer.serialize(root);
		} catch (Exception e) {
			fail();
		}

		// System.out.println(ostream.toString());

		// parsing from stream

		ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());

		ModelParser parser = new ModelParser();

		RootNode parsedModel = null;
		ListOfStrings errorList = new ListOfStrings();

		try {
			parsedModel = parser.parseModel(istream, null, errorList);
		} catch (ParserException e) {
			fail();
		}

		if (!errorList.isEmpty()) {
			fail();
		}

		compareModels(root, parsedModel);
	}

	public void parseChoiceTest(int version) {
		try{
			RootNode root = new RootNode("root", null, version);
			ClassNode classNode = new ClassNode("classNode", null);
			MethodNode method = new MethodNode("method", null);
			MethodParameterNode parameter = new MethodParameterNode("parameter", JavaLanguageHelper.TYPE_NAME_STRING, "0", false, null);
			ChoiceNode choice = new ChoiceNode("choice", "A                 B", null);
			List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
			testData.add(choice);
			TestCaseNode testCase = new TestCaseNode("test", null, testData);

			root.addClass(classNode);
			classNode.addMethod(method);
			method.addParameter(parameter);
			parameter.addChoice(choice);
			method.addTestCase(testCase);

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, version);
			ModelParser parser = new ModelParser();
			serializer.serialize(root);

			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			RootNode parsedModel = parser.parseModel(istream, null, new ListOfStrings());
			compareModels(root, parsedModel);
		}
		catch (IOException e) {
			fail("Unexpected exception");
		} catch (ParserException e) {
			fail("Unexpected exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	protected RootNode createRootNode(int classes) {
		RootNode root = new RootNode(randomName(), null);
		for(int i = 0; i < classes; ++i){
			root.addClass(createClassNode(rand.nextInt(MAX_METHODS) + 1));
		}
		return root;
	}

	protected ClassNode createClassNode(int methods) {
		ClassNode classNode = new ClassNode("com.example." + randomName(), null);
		for(int i = 0; i < methods; ++i){
			int numOfParameters = rand.nextInt(MAX_PARAMETERS) + 1;
			int numOfConstraints = rand.nextInt(MAX_CONSTRAINTS) + 1;
			int numOfTestCases = rand.nextInt(MAX_TEST_CASES);
			classNode.addMethod(createMethodNode(numOfParameters, 0, numOfConstraints, numOfTestCases));
		}
		return classNode;
	}

	protected MethodNode createMethodNode(int numOfParameters,
			int numOfExpParameters, int numOfConstraints, int numOfTestCases) {
		MethodNode method = new MethodNode(randomName(), null);
		List<MethodParameterNode> choicesParentParameters = createChoicesParentParameters(numOfParameters);
		List<MethodParameterNode> expectedParameters = createExpectedParameters(numOfExpParameters);

		for(int i = 0, j = 0; i < choicesParentParameters.size() || j < expectedParameters.size();){
			if(rand.nextBoolean() && i < choicesParentParameters.size()){
				method.addParameter(choicesParentParameters.get(i));
				++i;
			}
			else if (j < expectedParameters.size()){
				method.addParameter(expectedParameters.get(j));
				++j;
			}
		}

		List<ConstraintNode> constraints = createConstraints(choicesParentParameters, expectedParameters, numOfConstraints);
		List<TestCaseNode> testCases = createTestCases(method.getMethodParameters(), numOfTestCases);

		for(ConstraintNode constraint : constraints){
			method.addConstraint(constraint);
		}
		for(TestCaseNode testCase : testCases){
			method.addTestCase(testCase);
		}

		return method;
	}

	private List<MethodParameterNode> createChoicesParentParameters(int numOfParameters) {
		List<MethodParameterNode> parameters = new ArrayList<MethodParameterNode>();
		for(int i = 0; i < numOfParameters; i++){
			parameters.add(createChoicesParentParameter(CATEGORY_TYPES[rand.nextInt(CATEGORY_TYPES.length)], rand.nextInt(MAX_PARTITIONS) + 1));
		}
		return parameters;
	}

	private MethodParameterNode createChoicesParentParameter(String type, int numOfChoices) {
		MethodParameterNode parameter = new MethodParameterNode(randomName(), type, "0", false, null);
		for(int i = 0; i < numOfChoices; i++){
			parameter.addChoice(createChoice(type, 1));
		}
		return parameter;
	}

	private List<MethodParameterNode> createExpectedParameters(int numOfExpParameters) {
		List<MethodParameterNode> parameters = new ArrayList<MethodParameterNode>();
		for(int i = 0; i < numOfExpParameters; i++){
			parameters.add(createExpectedValueParameter(CATEGORY_TYPES[rand.nextInt(CATEGORY_TYPES.length)]));
		}
		return parameters;
	}

	private MethodParameterNode createExpectedValueParameter(String type) {
		String defaultValue = createRandomValue(type);
		MethodParameterNode parameter = new MethodParameterNode(randomName(), type, "0", true, null);
		parameter.setDefaultValueString(defaultValue);
		return parameter;
	}

	private String createRandomValue(String type) {
		switch(type){
		case SerializationConstants.TYPE_NAME_BOOLEAN:
			return Boolean.toString(rand.nextBoolean());
		case SerializationConstants.TYPE_NAME_BYTE:
			return Byte.toString((byte)rand.nextInt());
		case SerializationConstants.TYPE_NAME_CHAR:
			int random = rand.nextInt(255);
			if (random >= 32) {
				return new String ("\\" + String.valueOf(random));
			}
			return new String ("\\");
		case SerializationConstants.TYPE_NAME_DOUBLE:
			return Double.toString(rand.nextDouble());
		case SerializationConstants.TYPE_NAME_FLOAT:
			return Float.toString(rand.nextFloat());
		case SerializationConstants.TYPE_NAME_INT:
			return Integer.toString(rand.nextInt());
		case SerializationConstants.TYPE_NAME_LONG:
			return Long.toString(rand.nextLong());
		case SerializationConstants.TYPE_NAME_SHORT:
			return Short.toString((short)rand.nextInt());
		case SerializationConstants.TYPE_NAME_STRING:
			if(rand.nextInt(5) == 0){
				return JavaLanguageHelper.SPECIAL_VALUE_NULL;
			}
			else{
				return generateRandomString(rand.nextInt(10));
			}
		default:
			fail("Unexpected parameter type");
			return null;
		}
	}

	String generateRandomString(int length) {
		String allowedChars = " 1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		String result = new String();
		for(int i = 0; i < length; i++){
			int index = rand.nextInt(allowedChars.length());
			result += allowedChars.substring(index, index + 1);
		}
		return result;
	}

	private ChoiceNode createChoice(String type, int level) {
		String value = createRandomValue(type);
		ChoiceNode choice = new ChoiceNode(randomName(), value, null);
		for(int i = 0; i < rand.nextInt(MAX_PARTITION_LABELS); i++){
			choice.addLabel(generateRandomString(10));
		}
		boolean createChildren = rand.nextBoolean();
		int numOfChildren = rand.nextInt(MAX_PARTITIONS);
		if(createChildren && level <= MAX_PARTITION_LEVELS){
			for(int i = 0; i < numOfChildren; i++){
				choice.addChoice(createChoice(type, level + 1));
			}
		}
		return choice;
	}


	private List<ConstraintNode> createConstraints(List<MethodParameterNode> choicesParentParameters,
			List<MethodParameterNode> expectedParameters, int numOfConstraints) {
		List<ConstraintNode> constraints = new ArrayList<ConstraintNode>();
		for(int i = 0; i < numOfConstraints; ++i){
			constraints.add(new ConstraintNode(randomName(), createConstraint(choicesParentParameters, expectedParameters), null));
		}
		return constraints;
	}

	private Constraint createConstraint(List<MethodParameterNode> choicesParentParameters,
			List<MethodParameterNode> expectedParameters) {

		AbstractStatement precondition = createChoicesParentStatement(choicesParentParameters);
		AbstractStatement postcondition = null;
		while(postcondition == null){
			if(rand.nextBoolean()){
				postcondition = createChoicesParentStatement(choicesParentParameters);
			} else {
				postcondition = createExpectedStatement(expectedParameters);
			}
		}

		return new Constraint("constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);
	}

	private AbstractStatement createChoicesParentStatement(List<MethodParameterNode> parameters) {
		AbstractStatement statement = null;
		while(statement == null){
			switch(rand.nextInt(3)){
			case 0: statement = new StaticStatement(rand.nextBoolean(), null);
			case 1: if(getChoicesParentParameters(parameters).size() > 0){
				switch(rand.nextInt(2)){
				case 0:
					statement = createChoiceStatement(parameters);
				case 1:
					statement = createLabelStatement(parameters);
				}
			}
			case 2: statement = createStatementArray(rand.nextInt(3), parameters);
			}
		}
		return statement;
	}

	private AbstractStatement createLabelStatement(List<MethodParameterNode> parameters) {
		MethodParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		Set<String> labels = parameter.getLeafLabels();
		String label;
		if(labels.size() > 0){
			label = new ArrayList<String>(labels).get(rand.nextInt(labels.size()));
		}
		else{
			label = "SomeLabel";
			parameter.getChoices().get(0).addLabel(label);
		}
		EMathRelation relation = pickRelation();
		return RelationStatement.createRelationStatementWithLabelCondition(parameter, relation, label);
	}

	private AbstractStatement createChoiceStatement(List<MethodParameterNode> parameters) {
		MethodParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		ChoiceNode choiceNode = 
				new ArrayList<ChoiceNode>(parameter.getLeafChoices()).get(rand.nextInt(parameter.getChoices().size()));

		EMathRelation relation = pickRelation();
		return RelationStatement.createRelationStatementWithChoiceCondition(parameter, relation, choiceNode);
	}

	private EMathRelation pickRelation() {
		EMathRelation relation;
		switch(rand.nextInt(2)){
		case 0: relation = EMathRelation.EQUAL;
		case 1: relation = EMathRelation.NOT_EQUAL;
		default: relation = EMathRelation.EQUAL;
		}
		return relation;
	}

	private AbstractStatement createExpectedStatement(List<MethodParameterNode> parameters) {
		if(parameters.size() == 0) return null;
		MethodParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		return new ExpectedValueStatement(parameter, new ChoiceNode("default", createRandomValue(parameter.getType()), null), new JavaPrimitiveTypePredicate());
	}

	private List<MethodParameterNode> getChoicesParentParameters(List<? extends MethodParameterNode> parameters) {
		List<MethodParameterNode> result = new ArrayList<MethodParameterNode>();
		for(MethodParameterNode parameter : parameters){
			if(parameter instanceof MethodParameterNode == false){
				result.add(parameter);
			}
		}
		return result;
	}

	private AbstractStatement createStatementArray(int levels, List<MethodParameterNode> parameters) {

		StatementArray array = 
				new StatementArray(rand.nextBoolean()?StatementArrayOperator.AND:StatementArrayOperator.OR, null);

		for(int i = 0; i < rand.nextInt(3) + 1; ++i){
			if(levels > 0){
				array.addStatement(createStatementArray(levels - 1, parameters));
			}
			else{
				if(rand.nextBoolean() && getChoicesParentParameters(parameters).size() > 0){
					array.addStatement(createChoiceStatement(parameters));
				}
				else{
					array.addStatement(new StaticStatement(rand.nextBoolean(), null));
				}
			}
		}
		return array;
	}

	private List<TestCaseNode> createTestCases(
			List<MethodParameterNode> parameters, int numOfTestCases) {
		List<TestCaseNode> result = new ArrayList<TestCaseNode>();
		try {
			RandomGenerator<ChoiceNode> generator = new RandomGenerator<ChoiceNode>();
			List<List<ChoiceNode>> input = getGeneratorInput(parameters);
			List<IGeneratorValue> genArguments = new ArrayList<>();

			GeneratorValue generatorArgumentLength = new GeneratorValue(generator.getDefinitionLength(), String.valueOf(numOfTestCases));
			genArguments.add(generatorArgumentLength);

			GeneratorValue generatorArgumentDuplicates = new GeneratorValue(generator.getDefinitionDuplicates(), "true");
			genArguments.add(generatorArgumentDuplicates);

			generator.initialize(input, new DummyEvaluator<>(), genArguments, new SimpleProgressMonitor());
			List<ChoiceNode> next;
			while((next = generator.next()) != null){
				result.add(new TestCaseNode(randomName(), null, next));
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
		return result;
	}

	private List<List<ChoiceNode>> getGeneratorInput(
			List<MethodParameterNode> parameters) {
		List<List<ChoiceNode>> result = new ArrayList<List<ChoiceNode>>();
		for(MethodParameterNode parameter : parameters){
			result.add(parameter.getLeafChoices());
		}
		return result;
	}

	protected String randomName(){
		return "name" + nextInt++;
	}

	private void compareModels(RootNode model1, RootNode model2) {

		compareNames(model1.getName(), model2.getName());
		compareSizes(model1.getClasses(), model2.getClasses());

		for(int i = 0; i < model1.getClasses().size(); ++i){
			compareClasses(model1.getClasses().get(i), model2.getClasses().get(i));
		}
	}

	private void compareClasses(ClassNode classNode1, ClassNode classNode2) {

		compareNames(classNode1.getName(), classNode2.getName());
		compareSizes(classNode1.getMethods(), classNode2.getMethods());

		for(int i = 0; i < classNode1.getMethods().size(); ++i){
			compareMethods(classNode1.getMethods().get(i), classNode2.getMethods().get(i));
		}
	}

	private void compareMethods(MethodNode method1, MethodNode method2) {

		compareNames(method1.getName(), method2.getName());
		compareSizes(method1.getParameters(), method2.getParameters());
		compareSizes(method1.getConstraintNodes(), method2.getConstraintNodes());
		compareSizes(method1.getTestCases(), method2.getTestCases());

		for(int i =0; i < method1.getParameters().size(); ++i){
			compareParameters(method1.getMethodParameters().get(i), method2.getMethodParameters().get(i));
		}
		for(int i =0; i < method1.getConstraintNodes().size(); ++i){
			compareConstraintNodes(method1.getConstraintNodes().get(i), method2.getConstraintNodes().get(i));
		}
		for(int i =0; i < method1.getTestCases().size(); ++i){
			compareTestCases(method1.getTestCases().get(i), method2.getTestCases().get(i));
		}
	}

	private void compareParameters(MethodParameterNode parameter1, MethodParameterNode parameter2) {
		compareNames(parameter1.getName(), parameter2.getName());
		compareNames(parameter1.getType(), parameter2.getType());
		compareSizes(parameter1.getChoices(), parameter2.getChoices());
		if(parameter1 instanceof MethodParameterNode || parameter2 instanceof MethodParameterNode){
			if((parameter1 instanceof MethodParameterNode && parameter2 instanceof MethodParameterNode) == false){
				fail("Either both parameters must be expected value or none");
			}
		}
		for(int i = 0; i < parameter1.getChoices().size(); ++i){
			compareChoices(parameter1.getChoices().get(i), parameter2.getChoices().get(i));
		}
	}

	private void compareChoices(ChoiceNode choice1, ChoiceNode choice2) {

		compareNames(choice1.getName(), choice2.getName());
		compareValues(choice1.getValueString(),choice2.getValueString());
		compareLabels(choice1.getLabels(), choice2.getLabels());
		assertEquals(choice1.getChoices().size(), choice2.getChoices().size());
		for(int i = 0; i < choice1.getChoices().size(); i++){
			compareChoices(choice1.getChoices().get(i), choice2.getChoices().get(i));
		}
	}

	private void compareMethodParameters(MethodParameterNode methodParameterNode1, MethodParameterNode methodParameterNode2) {

		compareNames(methodParameterNode1.getName(), methodParameterNode2.getName());

		assertEquals(methodParameterNode1.getChoices().size(), methodParameterNode2.getChoices().size());

		for(int i = 0; i < methodParameterNode1.getChoices().size(); i++){
			compareChoices(methodParameterNode1.getChoices().get(i), methodParameterNode2.getChoices().get(i));
		}
	}

	private void compareStrings(String str1, String str2) {

		if (StringHelper.isEqual(str1, str2)) {
			return;
		}

		fail("String values differ");
	}

	private void compareLabels(Set<String> labels, Set<String> labels2) {
		assertTrue(labels.size() == labels2.size());
		for(String label : labels){
			assertTrue(labels2.contains(label));
		}
	}

	private void compareValues(Object value1, Object value2) {
		boolean result = true;
		if(value1 == null){
			result = (value2 == null);
		}
		else{
			result = value1.equals(value2);
		}
		if(!result){
			fail("Value " + value1 + " differ from " + value2);
		}
	}

	private void compareConstraintNodes(ConstraintNode constraint1, ConstraintNode constraint2) {

		compareNames(constraint1.getName(), constraint2.getName());
		compareConstraints(constraint1.getConstraint(), constraint2.getConstraint());
	}

	private void compareConstraints(Constraint constraint1, Constraint constraint2) {

		if (constraint1.getType() != constraint2.getType()) {
			fail("Constraint types different.");
		}

		compareStatements(constraint1.getPrecondition(), constraint2.getPrecondition());
		compareStatements(constraint1.getPostcondition(), constraint2.getPostcondition());
	}

	private void compareStatements(AbstractStatement statement1, AbstractStatement statement2) {

		if (statement1 instanceof StaticStatement && statement2 instanceof StaticStatement) {
			compareStaticStatements((StaticStatement)statement1, (StaticStatement)statement2);
			return;
		}

		if (statement1 instanceof RelationStatement && statement2 instanceof RelationStatement) {
			compareRelationStatements((RelationStatement)statement1, (RelationStatement)statement2);
			return;
		}

		if (statement1 instanceof StatementArray && statement2 instanceof StatementArray) {
			compareStatementArrays((StatementArray)statement1, (StatementArray)statement2);
			return;
		}

		if (statement1 instanceof ExpectedValueStatement && statement2 instanceof ExpectedValueStatement) {
			compareExpectedValueStatements((ExpectedValueStatement)statement1, (ExpectedValueStatement)statement2);
			return;
		}

		if (statement1 instanceof AssignmentStatement && statement2 instanceof AssignmentStatement) {
			compareAssignmentStatements((AssignmentStatement)statement1, (AssignmentStatement)statement2);
			return;
		}

		fail("Unknown type of statement or compared statements are of didderent types");
	}

	private void compareExpectedValueStatements(
			ExpectedValueStatement statement1,
			ExpectedValueStatement statement2) {

		compareParameters(statement1.getParameter(), statement2.getParameter());
		assertEquals(statement1.getCondition().getValueString(), statement2.getCondition().getValueString());
	}

	private void compareAssignmentStatements(
			AssignmentStatement statement1, AssignmentStatement statement2) {

		if (statement1.isEqualTo(statement1)) {
			fail("Assignment statements do not match");
		}
	}

	private void compareRelationStatements(RelationStatement statement1, RelationStatement statement2) {
		compareParameters(statement1.getLeftParameter(), statement2.getLeftParameter());
		if((statement1.getRelation() != statement2.getRelation())){
			fail("Compared statements have different relations: " +
					statement1.getRelation() + " and " + statement2.getRelation());
		}
		compareConditions(statement1.getConditionValue(), statement2.getConditionValue());
	}

	private void compareConditions(Object condition1, Object condition2) {

		if (condition1 instanceof String && condition2 instanceof String) {
			if(condition1.equals(condition2) == false){
				fail("Compared labels are different: " + condition1 + "!=" + condition2);
				return;
			}
		}

		if (condition1 instanceof ChoiceNode && condition2 instanceof ChoiceNode) {
			compareChoices((ChoiceNode)condition1, (ChoiceNode)condition2);
			return;
		}

		if (condition1 instanceof MethodParameterNode && condition2 instanceof MethodParameterNode) {
			compareMethodParameters((MethodParameterNode)condition1, (MethodParameterNode)condition2);
			return;

		}

		if (condition1 instanceof java.lang.String && condition2 instanceof java.lang.String) {
			compareStrings((String)condition1, (String) condition2);
			return;
		}

		String type1 = condition1.getClass().getTypeName();
		String type2 = condition2.getClass().getTypeName();

		fail("Unknown or not same types of compared conditions of types: " + type1 + ", " + type2 + ".");
	}

	private void compareStatementArrays(StatementArray array1, StatementArray array2) {
		if(array1.getOperator() != array2.getOperator()){
			fail("Operator of compared statement arrays differ");
		}
		compareSizes(array1.getChildren(), array2.getChildren());
		for(int i = 0; i < array1.getChildren().size(); ++i){
			compareStatements(array1.getChildren().get(i), array2.getChildren().get(i));
		}
	}

	private void compareStaticStatements(StaticStatement statement1, StaticStatement statement2) {
		if(statement1.getValue() != statement2.getValue()){
			fail("Static statements different");
		}
	}

	private void compareTestCases(TestCaseNode testCase1, TestCaseNode testCase2) {
		compareNames(testCase1.getName(), testCase2.getName());
		compareSizes(testCase1.getTestData(), testCase2.getTestData());
		for(int i = 0; i < testCase1.getTestData().size(); i++){
			ChoiceNode testValue1 = testCase1.getTestData().get(i);
			ChoiceNode testValue2 = testCase2.getTestData().get(i);

			if(testValue1.getParameter() instanceof MethodParameterNode){
				compareValues(testValue1.getValueString(), testValue2.getValueString());
			}
			else{
				compareChoices(testCase1.getTestData().get(i),testCase2.getTestData().get(i));
			}
		}
	}

	private void compareSizes(Collection<? extends Object> collection1, Collection<? extends Object> collection2) {
		if(collection1.size() != collection2.size()){
			fail("Different sizes of collections");
		}
	}

	private void compareNames(String name, String name2) {
		if(name.equals(name2) == false){
			fail("Different names: " + name + ", " + name2);
		}
	}
}

// TODO create model in version before assignments, parse, serialize and all expected output constraints should be converted