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

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.GeneratorValue;
import com.ecfeed.core.generators.RandomGenerator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.MethodDeployer;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelComparator;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StatementArrayOperator;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.model.serialization.SerializationConstants;
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.SimpleProgressMonitor;

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
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void parseChoiceTestVersion1() {
		parseChoiceTest(1);
	}	

	@Test
	public void parseConditionStatementTest(){
		try{
			int version = ModelVersionDistributor.getCurrentSoftwareVersion();
			RootNode root = new RootNode("root", null, version);
			ClassNode classNode = new ClassNode("classNode", null);
			MethodNode methodNode = new MethodNode("method", null);
			BasicParameterNode choicesParentParameter =
					new BasicParameterNode("choicesParentParameter", JavaLanguageHelper.TYPE_NAME_STRING, "0", false, null);
			BasicParameterNode expectedParameter =
					new BasicParameterNode("expectedParameter", JavaLanguageHelper.TYPE_NAME_CHAR, "0", true, null);
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
					RelationStatement.createRelationStatementWithChoiceCondition(choicesParentParameter, null, EMathRelation.EQUAL, choice1), // TODO MO-RE leftParameterLinkingContext
					null);

			Constraint constraintBasicFilter = new Constraint(
					"constraint",
					ConstraintType.BASIC_FILTER,
					new StaticStatement(true, null),
					RelationStatement.createRelationStatementWithChoiceCondition(choicesParentParameter, null, EMathRelation.EQUAL, choice1),  // TODO MO-RE leftParameterLinkingContext
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
							RelationStatement.createRelationStatementWithLabelCondition(choicesParentParameter, null, EMathRelation.EQUAL, "label"), // TODO MO-RE leftParameterLinkingContext
							null);

			Constraint expectedConstraint =
					new Constraint(
							"constraint",
							ConstraintType.ASSIGNMENT,
							new StaticStatement(true, null),
							new ExpectedValueStatement(
									expectedParameter,
									null, // TODO MO-RE
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
			classNode.addMethod(methodNode);
			methodNode.addParameter(choicesParentParameter);
			methodNode.addParameter(expectedParameter);
			choicesParentParameter.addChoice(choice1);
			
			methodNode.addConstraint(labelConstraintNode);
			methodNode.addConstraint(choiceConstraintNodeExtendedFilter);
			methodNode.addConstraint(choiceConstraintNodeBasicFilter);
			methodNode.addConstraint(constraintNodeAssignment);
			methodNode.addConstraint(expectedConstraintNode);
			
			NodeMapper nodeMapper = new NodeMapper();
			MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, nodeMapper);
			MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, methodNode, nodeMapper);
			
			methodNode.addTestCase(testCase);

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

		BasicParameterNode methodParameterNode1 = new BasicParameterNode(
				"par1",
				"int",
				"1",
				false,
				null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// method parameter 2 node with choice

		BasicParameterNode methodParameterNode2 = new BasicParameterNode(
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
				AssignmentStatement.createAssignmentWithParameterCondition(
						methodParameterNode2, methodParameterNode1, null);

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
		} catch (Exception e) {
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
			BasicParameterNode parameter = new BasicParameterNode("parameter", JavaLanguageHelper.TYPE_NAME_STRING, "0", false, null);
			ChoiceNode choice = new ChoiceNode("choice", "A                 B", null);
			List<ChoiceNode> testData = new ArrayList<ChoiceNode>();
			testData.add(choice);
			TestCaseNode testCase = new TestCaseNode("test", null, testData);

			root.addClass(classNode);
			classNode.addMethod(method);
			method.addParameter(parameter);
			parameter.addChoice(choice);
			
			NodeMapper nodeMapper = new NodeMapper();
			MethodNode deployedMethodNode = MethodDeployer.deploy(method, nodeMapper);
			MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, method, nodeMapper);
			
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
		MethodNode methodNode = new MethodNode(randomName(), null);
		List<BasicParameterNode> choicesParentParameters = createChoicesParentParameters(numOfParameters);
		List<BasicParameterNode> expectedParameters = createExpectedParameters(numOfExpParameters);

		for(int i = 0, j = 0; i < choicesParentParameters.size() || j < expectedParameters.size();){
			if(rand.nextBoolean() && i < choicesParentParameters.size()){
				methodNode.addParameter(choicesParentParameters.get(i));
				++i;
			}
			else if (j < expectedParameters.size()){
				methodNode.addParameter(expectedParameters.get(j));
				++j;
			}
		}

		List<ConstraintNode> constraints = createConstraints(choicesParentParameters, expectedParameters, numOfConstraints);
		List<TestCaseNode> testCases = createTestCases(getMethodParameters(methodNode), numOfTestCases);

		for(ConstraintNode constraint : constraints){
			methodNode.addConstraint(constraint);
		}

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, nodeMapper);
		MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, methodNode, nodeMapper);

		for(TestCaseNode testCase : testCases){
			methodNode.addTestCase(testCase);
		}

		return methodNode;
	}

	private List<BasicParameterNode> getMethodParameters(MethodNode method) {

		List<BasicParameterNode> result = new ArrayList<BasicParameterNode>();

		List<AbstractParameterNode> methodParameters = method.getParameters();

		for (AbstractParameterNode abstractParameterNode : methodParameters) {
			result.add((BasicParameterNode) abstractParameterNode);
		}

		return result;
	}

	private List<BasicParameterNode> createChoicesParentParameters(int numOfParameters) {
		List<BasicParameterNode> parameters = new ArrayList<BasicParameterNode>();
		for(int i = 0; i < numOfParameters; i++){
			parameters.add(createChoicesParentParameter(CATEGORY_TYPES[rand.nextInt(CATEGORY_TYPES.length)], rand.nextInt(MAX_PARTITIONS) + 1));
		}
		return parameters;
	}

	private BasicParameterNode createChoicesParentParameter(String type, int numOfChoices) {
		BasicParameterNode parameter = new BasicParameterNode(randomName(), type, "0", false, null);
		for(int i = 0; i < numOfChoices; i++){
			parameter.addChoice(createChoice(type, 1));
		}
		return parameter;
	}

	private List<BasicParameterNode> createExpectedParameters(int numOfExpParameters) {
		List<BasicParameterNode> parameters = new ArrayList<BasicParameterNode>();
		for(int i = 0; i < numOfExpParameters; i++){
			parameters.add(createExpectedValueParameter(CATEGORY_TYPES[rand.nextInt(CATEGORY_TYPES.length)]));
		}
		return parameters;
	}

	private BasicParameterNode createExpectedValueParameter(String type) {
		String defaultValue = createRandomValue(type);
		BasicParameterNode parameter = new BasicParameterNode(randomName(), type, "0", true, null);
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


	private List<ConstraintNode> createConstraints(List<BasicParameterNode> choicesParentParameters,
			List<BasicParameterNode> expectedParameters, int numOfConstraints) {
		List<ConstraintNode> constraints = new ArrayList<ConstraintNode>();
		for(int i = 0; i < numOfConstraints; ++i){
			constraints.add(new ConstraintNode(randomName(), createConstraint(choicesParentParameters, expectedParameters), null));
		}
		return constraints;
	}

	private Constraint createConstraint(List<BasicParameterNode> choicesParentParameters,
			List<BasicParameterNode> expectedParameters) {

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

	private AbstractStatement createChoicesParentStatement(List<BasicParameterNode> parameters) {
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

	private AbstractStatement createLabelStatement(List<BasicParameterNode> parameters) {
		BasicParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
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
		return RelationStatement.createRelationStatementWithLabelCondition(parameter, null, relation, label); // TODO MO-RE leftParameterLinkingContext
	}

	private AbstractStatement createChoiceStatement(List<BasicParameterNode> parameters) {
		BasicParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		ChoiceNode choiceNode = 
				new ArrayList<ChoiceNode>(parameter.getLeafChoices()).get(rand.nextInt(parameter.getChoices().size()));

		EMathRelation relation = pickRelation();
		return RelationStatement.createRelationStatementWithChoiceCondition(parameter, null, relation, choiceNode);  // TODO MO-RE leftParameterLinkingContext
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

	private AbstractStatement createExpectedStatement(List<BasicParameterNode> parameters) {
		if(parameters.size() == 0) return null;
		BasicParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		return new ExpectedValueStatement(
				parameter, null, new ChoiceNode("default", createRandomValue(parameter.getType()), null), new JavaPrimitiveTypePredicate());
	}

	private List<BasicParameterNode> getChoicesParentParameters(List<? extends BasicParameterNode> parameters) {
		List<BasicParameterNode> result = new ArrayList<BasicParameterNode>();
		for(BasicParameterNode parameter : parameters){
			if(parameter instanceof BasicParameterNode == false){
				result.add(parameter);
			}
		}
		return result;
	}

	private AbstractStatement createStatementArray(int levels, List<BasicParameterNode> parameters) {

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
			List<BasicParameterNode> parameters, int numOfTestCases) {

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
		} catch (Exception e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
		return result;
	}

	private List<List<ChoiceNode>> getGeneratorInput(
			List<BasicParameterNode> parameters) {
		List<List<ChoiceNode>> result = new ArrayList<List<ChoiceNode>>();
		for(BasicParameterNode parameter : parameters){
			result.add(parameter.getLeafChoices());
		}
		return result;
	}

	protected String randomName(){
		return "name" + nextInt++;
	}

	private void compareModels(RootNode model1, RootNode model2) {

		try {
			ModelComparator.compareRootNodes(model1, model2);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}