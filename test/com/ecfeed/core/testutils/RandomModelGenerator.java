/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.testutils;

import static com.ecfeed.core.testutils.TestUtilConstants.SUPPORTED_TYPES;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ecfeed.core.model.*;
import org.junit.Test;

import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;

public class RandomModelGenerator {

	private static int id = 0;
	private Random rand = new Random();
	private ModelStringifier fStringifier = new ModelStringifier();

	public int MAX_CLASSES = 3;
	public int MAX_METHODS = 3;
	public int MAX_PARAMETERS = 3;
	public int MAX_CONSTRAINTS = 3;
	public int MAX_TEST_CASES = 10;
	public int MAX_PARTITIONS = 5;
	public int MAX_PARTITION_LEVELS = 3;
	public int MAX_PARTITION_LABELS = 3;
	public int MAX_STATEMENTS = 5;
	public int MAX_STATEMENTS_DEPTH = 3;

	public IAbstractNode generateNode(ENodeType type){
		switch(type){
		case CHOICE:
			return generateChoice(MAX_PARTITION_LEVELS, MAX_PARTITIONS, MAX_PARTITION_LABELS, randomType(true));
		case CLASS:
			return generateClass(MAX_METHODS);
		case CONSTRAINT:
			return generateMethod(MAX_PARAMETERS, MAX_CONSTRAINTS, 0).getConstraintNodes().get(rand.nextInt(MAX_CONSTRAINTS));
		case METHOD:
			return generateMethod(MAX_PARAMETERS, MAX_CONSTRAINTS, MAX_TEST_CASES);
		case PARAMETER:
			return generateParameter(randomType(true), rand.nextBoolean(), MAX_PARTITION_LEVELS, MAX_PARTITIONS, MAX_PARTITION_LABELS);
		case METHOD_PARAMETER:
			return generateParameter(randomType(true), rand.nextBoolean(), MAX_PARTITION_LEVELS, MAX_PARTITIONS, MAX_PARTITION_LABELS);
		case GLOBAL_PARAMETER:
			return generateParameter(randomType(true), false, MAX_PARTITION_LEVELS, MAX_PARTITIONS, MAX_PARTITION_LABELS);
		case PROJECT:
			return generateModel(MAX_CLASSES);
		case TEST_CASE:
			return generateMethod(MAX_PARAMETERS, 0, MAX_TEST_CASES).getTestCases().get(rand.nextInt(MAX_TEST_CASES));
		}
		return null;
	}

	public RootNode generateModel(int classes){
		String name = generateString(RegexHelper.REGEX_ROOT_NODE_NAME);

		RootNode root = new RootNode(name, null);

		for(int i = 0; i < classes; i++){
			root.addClass(generateClass(rand.nextInt(MAX_METHODS)));
		}

		return root;
	}



	public ClassNode generateClass(int methods) {
		String name = generateString(RegexHelper.REGEX_CLASS_NODE_NAME);

		ClassNode theClass = new ClassNode(name, null);

//		theClass.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID, "true");
//		theClass.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER, "runner");

		for(int i = 0; i < methods; i++){
			int parameters = rand.nextInt(MAX_PARAMETERS);
			int constraints = rand.nextInt(MAX_CONSTRAINTS);
			int testCases = rand.nextInt(MAX_TEST_CASES);

			theClass.addMethod(generateMethod(parameters, constraints, testCases));
		}

		return theClass;
	}

	public MethodNode generateMethod(int parameters, int constraints, int testCases){
		String name = generateString(RegexHelper.REGEX_METHOD_NODE_NAME);

		MethodNode method = new MethodNode(name, null);
		method.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER, "runner");
		method.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM, "false");
		method.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER, "Chrome");
		method.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER_PATH, "driver");
		method.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM, "false");
		method.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_START_URL, "startUrl");

		for (int i = 0; i < parameters; i++) {
			boolean expected = rand.nextInt(4) < 3 ? false : true;
			String type = randomType(true);

			method.addParameter(generateParameter(type, expected,
					rand.nextInt(MAX_PARTITION_LEVELS), rand.nextInt(MAX_PARTITIONS) + 1,
					rand.nextInt(MAX_PARTITION_LABELS)));
		}

		for(int i = 0; i < constraints; i++){
			method.addConstraint(generateConstraint(method));
		}

		for(int i = 0; i < testCases; i++){
			method.addTestCase(generateTestCase(method));
		}

		return method;
	}

	public BasicParameterNode generateParameter(String type, boolean expected, int choiceLevels, int choices, int labels){
		String name = generateString(RegexHelper.REGEX_CATEGORY_NODE_NAME);

		BasicParameterNode parameter = new BasicParameterNode(name, type, randomChoiceValue(type), expected, null);

		parameter.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE, "X");
		parameter.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT, "Y");
		parameter.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT, "Z");
		parameter.setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ACTION, "V");

		if(choices > 0){
			for(int i = 0; i < rand.nextInt(choices) + 1; i++){
				parameter.addChoice(generateChoice(choiceLevels, choices, labels, type));
			}
		}

		return parameter;
	}

	public TestCaseNode generateTestCase(MethodNode method){
		String name = generateString(RegexHelper.REGEX_TEST_CASE_NODE_NAME);
		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();

		for(AbstractParameterNode abstractParameterNode : method.getParameters()){
			
			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				continue;
			}
			
			BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;
			
			if(basicParameterNode.isExpected()){
				ChoiceNode expectedValue = new ChoiceNode(ChoiceNode.ASSIGNMENT_NAME, randomChoiceValue(basicParameterNode.getType()), null);
				expectedValue.setParent(abstractParameterNode);
				testData.add(expectedValue);
			}
			else{
				List<ChoiceNode> choices = basicParameterNode.getChoices();
				if(choices.size() == 0){
					System.out.println("Empty parameter!");
				}
				ChoiceNode p = basicParameterNode.getChoices().get(rand.nextInt(choices.size()));
				while(p.getChoices().size() > 0){
					List<ChoiceNode> pchoices = p.getChoices();
					p = pchoices.get(rand.nextInt(pchoices.size()));
				}
				testData.add(p);
			}
		}

		TestCaseNode targetTestCaseNode = new TestCaseNode(name, null, testData);

		method.addTestCase(targetTestCaseNode);
		return targetTestCaseNode;
	}

	public ConstraintNode generateConstraint(MethodNode method){
		String name = generateString(RegexHelper.REGEX_CONSTRAINT_NODE_NAME);

		Constraint constraint = 
				new Constraint(
						"constraint",
						ConstraintType.EXTENDED_FILTER,
						generatePrecondition(method), generatePostcondition(method), method.getModelChangeRegistrator()
                );

		return new ConstraintNode(name, constraint, null);
	}

	public AbstractStatement generatePrecondition(MethodNode method) {
		return generateStatement(method, MAX_STATEMENTS_DEPTH);
	}

	public AbstractStatement generateStatement(MethodNode method, int maxDepth) {
		switch(rand.nextInt(5)){
		case 0:
			return generateStaticStatement();
		case 1:
		case 2:
			return generateChoicesParentStatement(method);
		case 3:
		case 4:
			if(maxDepth > 0){
				return generateStatementArray(method, maxDepth);
			}
		}
		return generateStaticStatement();
	}

	public StaticStatement generateStaticStatement(){
		return new StaticStatement(rand.nextBoolean(), null);
	}

	public RelationStatement generateChoicesParentStatement(MethodNode method) {
		List<BasicParameterNode> parameters = new ArrayList<BasicParameterNode>();

		for(AbstractParameterNode abstractParameterNode : method.getParameters()){
			
			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				continue;
			}
			
			BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;
			
			if(basicParameterNode.isExpected() == false && basicParameterNode.getChoices().size() > 0){
				parameters.add(basicParameterNode);
			}
		}

		if(parameters.size() == 0){
			BasicParameterNode parameter = generateParameter(JavaLanguageHelper.TYPE_NAME_INT, false, 0, 1, 1);
			method.addParameter(parameter);
			parameters.add(parameter);
		}

		BasicParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		EMathRelation relation = rand.nextBoolean() ? EMathRelation.EQUAL : EMathRelation.NOT_EQUAL;
		if(parameter.getChoices().size() == 0){
			ChoiceNode choice = generateChoice(0, 0, 1, parameter.getType());
			parameter.addChoice(choice);
		}

		if(rand.nextBoolean()){
			List<String> choiceNames = new ArrayList<String>(parameter.getAllChoiceNames());
			String luckyChoiceName = choiceNames.get(rand.nextInt(choiceNames.size()));
			ChoiceNode condition = parameter.getChoice(luckyChoiceName);
			return RelationStatement.createRelationStatementWithChoiceCondition(parameter, relation, condition);
		}
		else{
			if(parameter.getLeafLabels().size() == 0){
				parameter.getChoices().get(0).addLabel(generateString(RegexHelper.REGEX_PARTITION_LABEL));
			}

			Set<String>labels = parameter.getLeafLabels();

			String label = labels.toArray(new String[]{})[rand.nextInt(labels.size())];
			return RelationStatement.createRelationStatementWithLabelCondition(parameter, relation, label);
		}
	}

	public ExpectedValueStatement generateExpectedValueStatement(MethodNode method) {
		List<BasicParameterNode> parameters = new ArrayList<BasicParameterNode>();

		for(AbstractParameterNode abstractParameterNode : method.getParameters()){
			
			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				continue;
			}
			
			BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;
			
			if(basicParameterNode.isExpected() == true){
				parameters.add(basicParameterNode);
			}
		}

		if(parameters.size() == 0){
			BasicParameterNode parameter = generateParameter(SUPPORTED_TYPES[rand.nextInt(SUPPORTED_TYPES.length)], true, 0, 1, 1);
			method.addParameter(parameter);
			parameters.add(parameter);
		}

		BasicParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));


		String value = randomChoiceValue(parameter.getType());
		String name = generateString(RegexHelper.REGEX_PARTITION_NODE_NAME);
		ChoiceNode choice = new ChoiceNode(name, value, null);
		parameter.addChoice(choice);
		return new ExpectedValueStatement(parameter, choice, new JavaPrimitiveTypePredicate());
	}

	public StatementArray generateStatementArray(MethodNode method, int depth) {

		StatementArray statement = 
				new StatementArray(
						rand.nextBoolean()?StatementArrayOperator.AND:StatementArrayOperator.OR,
								method.getModelChangeRegistrator());

		for(int i = 0; i < MAX_STATEMENTS; i++){
			statement.addStatement(generateStatement(method, depth - 1));
		}
		return statement;
	}

	public AbstractStatement generatePostcondition(MethodNode method) {
		if(method.getParameters().size() == 0){
			method.addParameter(generateParameter(JavaLanguageHelper.TYPE_NAME_INT, false, 0, 1, 1));
		}

		List<AbstractParameterNode> parameters = method.getParameters();
		
		AbstractParameterNode parameter = parameters.get(rand.nextInt(parameters.size()));
		
		if (!(parameter instanceof BasicParameterNode)) {
			return null;
		}
		
		BasicParameterNode basicParameterNode = (BasicParameterNode) parameter;
		
		if(basicParameterNode.isExpected()){
			return generateExpectedValueStatement(method);
		}
		return generateStatement(method, MAX_STATEMENTS_DEPTH);
	}

	public ChoiceNode generateChoice(int levels, int choices, int labels, String type) {
		String name = generateString(RegexHelper.REGEX_PARTITION_NODE_NAME);
		name = name.replaceAll(":", "_");
		String value = randomChoiceValue(type);

		ChoiceNode choice = new ChoiceNode(name, value, null);
		for(int i = 0; i < labels; i++){
			String label = generateString(RegexHelper.REGEX_PARTITION_LABEL);
			choice.addLabel(label);
		}

		if(levels > 0){
			for(int i = 0; i < choices; i++){
				choice.addChoice(generateChoice(levels - 1, choices, labels, type));
			}
		}

		return choice;
	}

	public String randomType(boolean includeUserType){

		int typeIdx = rand.nextInt(SUPPORTED_TYPES.length + (includeUserType ? 1 : 0));
		if(typeIdx < SUPPORTED_TYPES.length){
			return SUPPORTED_TYPES[typeIdx];
		}

		return generateString(RegexHelper.REGEX_CATEGORY_TYPE_NAME);
	}

	private String randomChoiceValue(String type){
		switch(type){
		case JavaLanguageHelper.TYPE_NAME_BOOLEAN:
			return randomBooleanValue();
		case JavaLanguageHelper.TYPE_NAME_BYTE:
			return randomByteValue();
		case JavaLanguageHelper.TYPE_NAME_CHAR:
			return randomCharValue();
		case JavaLanguageHelper.TYPE_NAME_DOUBLE:
			return randomDoubleValue();
		case JavaLanguageHelper.TYPE_NAME_FLOAT:
			return randomFloatValue();
		case JavaLanguageHelper.TYPE_NAME_INT:
			return randomIntValue();
		case JavaLanguageHelper.TYPE_NAME_LONG:
			return randomLongValue();
		case JavaLanguageHelper.TYPE_NAME_SHORT:
			return randomShortValue();
		case JavaLanguageHelper.TYPE_NAME_STRING:
			return randomStringValue();
		default:
			return randomUserTypeValue();
		}
	}

	private String randomBooleanValue() {
		return String.valueOf(rand.nextBoolean());
	}

	private String randomByteValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE"};

		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf((byte)rand.nextInt());
	}

	private String randomCharValue() {
		return generateString(RegexHelper.REGEX_CHAR_TYPE_VALUE);
	}

	private String randomDoubleValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE", "POSITIVE_INFINITY", "NEGATIVE_INFINITY"};

		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf(rand.nextDouble());
	}



	private String randomFloatValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE", "POSITIVE_INFINITY", "NEGATIVE_INFINITY"};

		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf(rand.nextLong());
	}

	private String randomIntValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE"};

		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf(rand.nextInt());
	}

	private String randomLongValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE"};

		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}

		return String.valueOf(rand.nextLong());
	}

	private String randomShortValue() {
		String[] specialValues = {"MIN_VALUE", "MAX_VALUE"};

		if(rand.nextInt(5) == 0){
			return specialValues[rand.nextInt(specialValues.length)];
		}
		return String.valueOf((short)rand.nextInt());
	}

	private String randomStringValue() {
		return generateString(RegexHelper.REGEX_STRING_TYPE_VALUE);
	}

	private String randomUserTypeValue() {
		return generateString(RegexHelper.REGEX_USER_TYPE_VALUE);
	}

	private String generateString(String regex){
		return "name" + id++;

		//		Xeger generator = new Xeger(regex);
		//		return generator.generate();
	}

	@Test
	public void testGenerateClass(){
		ClassNode _class = generateClass(5);
		System.out.println(fStringifier.stringify(_class, 0));
	}

	//	@Test
	public void testChoiceGeneration(){
		System.out.println("Childless choices:");
		for(String type : new String[]{"String"}){
			ChoiceNode p0 = generateChoice(0, 0, 0, type);
			System.out.println(type + " choice:" + p0);
		}

		System.out.println("Hierarchic choices:");
		for(String type : SUPPORTED_TYPES){
			System.out.println("Type: " + type);
			ChoiceNode p1 = generateChoice(MAX_PARTITION_LEVELS, MAX_PARTITIONS, MAX_PARTITION_LABELS, type);
			System.out.println(fStringifier.stringify(p1, 0));
		}
	}

	//	@Test
	public void testParameterGenerator(){
		for(String type : SUPPORTED_TYPES){
			for(boolean expected : new Boolean[]{true, false}){
				System.out.println("Type: " + type);
				int choices = rand.nextInt(MAX_PARTITIONS);
				int labels = rand.nextInt(MAX_PARTITION_LABELS);
				int levels = rand.nextInt(MAX_PARTITION_LEVELS);
				BasicParameterNode c = generateParameter(type, expected, levels, choices, labels);
				System.out.println(fStringifier.stringify(c, 0));
			}
		}
	}

	//	@Test
	public void testMethodGenerator(){
		MethodNode m = generateMethod(5, 5, 5);
		System.out.println(fStringifier.stringify(m, 0));
	}

	//	@Test
	public void testTestCaseGenerator(){
		MethodNode m = generateMethod(5, 0, 0);
		TestCaseNode tc = generateTestCase(m);
		System.out.println(fStringifier.stringify(m, 0));
		System.out.println(fStringifier.stringify(tc, 0));
	}

	//	@Test
	public void testGenerateConstraint(){
		MethodNode m = generateMethod(10, 0, 0);
		ConstraintNode c = generateConstraint(m);
		System.out.println(fStringifier.stringify(c, 2));
	}

	//	@Test
	public void testGenerateStaticStatement(){
		for(int i = 0; i < 10; i++){
			StaticStatement statement = generateStaticStatement();
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}

	//	@Test
	public void testGenerateChoicesParentStatement(){
		for(int i = 0; i < 10; i++){
			MethodNode m = generateMethod(10, 0, 0);
			RelationStatement statement = generateChoicesParentStatement(m);
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}

	//	@Test
	public void testGenerateExpectedValueStatement(){
		for(int i = 0; i < 10; i++){
			MethodNode m = generateMethod(10, 0, 0);
			ExpectedValueStatement statement = generateExpectedValueStatement(m);
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}

	//	@Test
	public void testGenerateStatementArray(){
		for(int i = 0; i < 10; i++){
			MethodNode m = generateMethod(10, 0, 0);
			StatementArray statement = generateStatementArray(m, 3);
			System.out.println(fStringifier.stringify(statement, 0));
		}
	}
}
