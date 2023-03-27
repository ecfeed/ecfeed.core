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


import java.io.ByteArrayOutputStream;

import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class ModelComparator {

	public static void assertModelsEqual(RootNode model1, RootNode model2) {

		ModelCompareHelper.compareNames(model1.getName(), model2.getName());
		ModelCompareHelper.compareSizes(model1.getClasses(), model2.getClasses());

		for(int i = 0; i < model1.getClasses().size(); ++i){
			compareClasses(model1.getClasses().get(i), model2.getClasses().get(i));
		}

		compareSerializedModelsAsLastResort(model1, model2);
	}

	private static void compareSerializedModelsAsLastResort(RootNode model1, RootNode model2) {

		String xml1 = serializeModel(model1);
		String xml2 = serializeModel(model2);
		
		String[] lines1 = xml1.split("\n");
		String[] lines2 = xml2.split("\n");
		
		if (xml1.equals(xml2)) {
			return;
		}

		String errorMessage = StringHelper.isEqualByLines(lines1, lines2);
		
		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException("Model comparison failed with message: " + errorMessage);
		}
		
		ExceptionHelper.reportRuntimeException("Comparison of serialized models failed.");
	}

	private static String serializeModel(RootNode model1) {

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

		try {
			serializer.serialize(model1);
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Failed to serialize model.", e);
		}

		return ostream.toString();
	}

	private static void compareClasses(ClassNode classNode1, ClassNode classNode2) {

		ModelCompareHelper.compareNames(classNode1.getName(), classNode2.getName());
		ModelCompareHelper.compareSizes(classNode1.getMethods(), classNode2.getMethods());

		for(int i = 0; i < classNode1.getMethods().size(); ++i){
			compareMethods(classNode1.getMethods().get(i), classNode2.getMethods().get(i));
		}
	}

	public static void compareMethods(MethodNode method1, MethodNode method2) {

		if (method1 == null) {
			ExceptionHelper.reportRuntimeException("Empty method 1.");
		}

		if (method2 == null) {
			ExceptionHelper.reportRuntimeException("Empty method 2.");
		}

		ModelCompareHelper.compareNames(method1.getName(), method2.getName());
		ModelCompareHelper.compareSizes(method1.getParameters(), method2.getParameters());
		ModelCompareHelper.compareSizes(method1.getConstraintNodes(), method2.getConstraintNodes());
		ModelCompareHelper.compareSizes(method1.getTestCases(), method2.getTestCases());

		for(int i =0; i < method1.getParameters().size(); ++i){
			compareParameters(method1.getParameters().get(i), method2.getParameters().get(i));
		}
		for(int i =0; i < method1.getConstraintNodes().size(); ++i){
			compareConstraintNodes(method1.getConstraintNodes().get(i), method2.getConstraintNodes().get(i));
		}
		for(int i =0; i < method1.getTestCases().size(); ++i){
			compareTestCases(method1.getTestCases().get(i), method2.getTestCases().get(i));
		}
	}

	public static void compareParameters(
			AbstractParameterNode abstractParameter1, 
			AbstractParameterNode abstractParameter2) {

		AbstractParameterNodeHelper.compareParameterTypes(abstractParameter1, abstractParameter2);

		ModelCompareHelper.compareNames(abstractParameter1.getName(), abstractParameter2.getName());

		if ((abstractParameter1 instanceof BasicParameterNode) && (abstractParameter2 instanceof BasicParameterNode)) {

			BasicParameterNode basicParameterNode1 = (BasicParameterNode) abstractParameter1;
			BasicParameterNode basicParameterNode2 = (BasicParameterNode) abstractParameter2;

			BasicParameterNodeHelper.compareParameters(basicParameterNode1, basicParameterNode2);
			return;
		}

		if ((abstractParameter1 instanceof CompositeParameterNode) && (abstractParameter2 instanceof CompositeParameterNode)) {

			CompositeParameterNode basicParameterNode1 = (CompositeParameterNode) abstractParameter1;
			CompositeParameterNode basicParameterNode2 = (CompositeParameterNode) abstractParameter2;

			CompositeParameterNodeHelper.compareParameters(basicParameterNode1, basicParameterNode2);
			return;
		}

		ExceptionHelper.reportRuntimeException("Unhandled combination of parameter types.");
	}

	public static void compareMethodParameters(BasicParameterNode methodParameterNode1, BasicParameterNode methodParameterNode2) {

		ModelCompareHelper.compareNames(methodParameterNode1.getName(), methodParameterNode2.getName());

		ModelCompareHelper.assertIntegersEqual(methodParameterNode1.getChoices().size(), methodParameterNode2.getChoices().size(), "Length of choices list differs.");

		for(int i = 0; i < methodParameterNode1.getChoices().size(); i++){
			ModelCompareHelper.compareChoices(methodParameterNode1.getChoices().get(i), methodParameterNode2.getChoices().get(i));
		}
	}

	private static void compareStrings(String str1, String str2) {

		if (StringHelper.isEqual(str1, str2)) {
			return;
		}

		ExceptionHelper.reportRuntimeException("String values differ");
	}

	private static void compareConstraintNodes(ConstraintNode constraint1, ConstraintNode constraint2) {

		ModelCompareHelper.compareNames(constraint1.getName(), constraint2.getName());
		compareConstraints(constraint1.getConstraint(), constraint2.getConstraint());
	}

	private static void compareConstraints(Constraint constraint1, Constraint constraint2) {

		if (constraint1.getType() != constraint2.getType()) {
			ExceptionHelper.reportRuntimeException("Constraint types different.");
		}

		compareStatements(constraint1.getPrecondition(), constraint2.getPrecondition());
		compareStatements(constraint1.getPostcondition(), constraint2.getPostcondition());
	}

	public static void compareStatements(AbstractStatement statement1, AbstractStatement statement2) {

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

		ExceptionHelper.reportRuntimeException("Unknown type of statement or compared statements are of didderent types");
	}

	private static void compareExpectedValueStatements(
			ExpectedValueStatement statement1,
			ExpectedValueStatement statement2) {

		compareParameters(statement1.getLeftMethodParameterNode(), statement2.getLeftMethodParameterNode());
		assertStringsEqual(statement1.getChoice().getValueString(), statement2.getChoice().getValueString(), "Conditions differ.");
	}

	private static void compareAssignmentStatements(
			AssignmentStatement statement1, AssignmentStatement statement2) {

		if (statement1.isEqualTo(statement1)) {
			ExceptionHelper.reportRuntimeException("Assignment statements do not match");
		}
	}

	private static void compareRelationStatements(RelationStatement statement1, RelationStatement statement2) {
		compareParameters(statement1.getLeftParameter(), statement2.getLeftParameter());
		if((statement1.getRelation() != statement2.getRelation())){
			ExceptionHelper.reportRuntimeException("Compared statements have different relations: " +
					statement1.getRelation() + " and " + statement2.getRelation());
		}
		compareConditions(statement1.getConditionValue(), statement2.getConditionValue());
	}

	private static void compareConditions(Object condition1, Object condition2) {

		if (condition1 instanceof String && condition2 instanceof String) {
			if(condition1.equals(condition2) == false){
				ExceptionHelper.reportRuntimeException("Compared labels are different: " + condition1 + "!=" + condition2);
				return;
			}
		}

		if (condition1 instanceof ChoiceNode && condition2 instanceof ChoiceNode) {
			ModelCompareHelper.compareChoices((ChoiceNode)condition1, (ChoiceNode)condition2);
			return;
		}

		if (condition1 instanceof BasicParameterNode && condition2 instanceof BasicParameterNode) {
			compareMethodParameters((BasicParameterNode)condition1, (BasicParameterNode)condition2);
			return;

		}

		if (condition1 instanceof java.lang.String && condition2 instanceof java.lang.String) {
			compareStrings((String)condition1, (String) condition2);
			return;
		}

		String type1 = condition1.getClass().getTypeName();
		String type2 = condition2.getClass().getTypeName();

		ExceptionHelper.reportRuntimeException("Unknown or not same types of compared conditions of types: " + type1 + ", " + type2 + ".");
	}

	private static void compareStatementArrays(StatementArray array1, StatementArray array2) {
		if(array1.getOperator() != array2.getOperator()){
			ExceptionHelper.reportRuntimeException("Operator of compared statement arrays differ");
		}
		ModelCompareHelper.compareSizes(array1.getChildren(), array2.getChildren());
		for(int i = 0; i < array1.getChildren().size(); ++i){
			compareStatements(array1.getChildren().get(i), array2.getChildren().get(i));
		}
	}

	private static void compareStaticStatements(StaticStatement statement1, StaticStatement statement2) {
		if(statement1.getValue() != statement2.getValue()){
			ExceptionHelper.reportRuntimeException("Static statements different");
		}
	}

	private static void compareTestCases(TestCaseNode testCase1, TestCaseNode testCase2) {
		ModelCompareHelper.compareNames(testCase1.getName(), testCase2.getName());
		ModelCompareHelper.compareSizes(testCase1.getTestData(), testCase2.getTestData());
		for(int i = 0; i < testCase1.getTestData().size(); i++){
			ChoiceNode testValue1 = testCase1.getTestData().get(i);
			ChoiceNode testValue2 = testCase2.getTestData().get(i);

			if(testValue1.getParameter() instanceof BasicParameterNode){
				ModelCompareHelper.compareValues(testValue1.getValueString(), testValue2.getValueString());
			}
			else{
				ModelCompareHelper.compareChoices(testCase1.getTestData().get(i),testCase2.getTestData().get(i));
			}
		}
	}

	private static void assertStringsEqual(String valueString, String valueString2, String message) {

		if (valueString.equals(valueString2))  {
			return;
		}

		ExceptionHelper.reportRuntimeException("String values do not match." + " " + message);
	}

}
