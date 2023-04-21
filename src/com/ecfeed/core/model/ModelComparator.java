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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class ModelComparator { // XYX improve method comparator to check parents (if parent matches in model1 then it should match also in model2) 

	public static void compareRootNodes(RootNode model1, RootNode model2) {

		compareNames(model1.getName(), model2.getName());
		compareSizes(model1.getClasses(), model2.getClasses(), "Number of classes differ.");

		for(int i = 0; i < model1.getClasses().size(); ++i){
			compareClasses(model1.getClasses().get(i), model2.getClasses().get(i));
		}

		compareSerializedModelsAsLastResort(model1, model2);
	}

	public static void compareClasses(ClassNode classNode1, ClassNode classNode2) {

		compareNames(classNode1.getName(), classNode2.getName());
		compareSizes(classNode1.getMethods(), classNode2.getMethods(), "Number of methods differ.");

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

		compareNames(method1.getName(), method2.getName());
		compareSizes(method1.getParameters(), method2.getParameters(), "Number of parameters differ.");
		compareSizes(method1.getConstraintNodes(), method2.getConstraintNodes(), "Number of constraints differ.");
		compareSizes(method1.getTestCases(), method2.getTestCases(), "Number of test cases differ.");

		for(int i =0; i < method1.getParameters().size(); ++i){
			compareParameters(method1.getParameters().get(i), method2.getParameters().get(i));
		}

		compareDeployedParameters(method1, method2);

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

		if (abstractParameter1 == null && abstractParameter2 == null) {
			return;
		}

		AbstractParameterNodeHelper.compareParameterTypes(abstractParameter1, abstractParameter2);

		compareNames(abstractParameter1.getName(), abstractParameter2.getName());

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

	public static void compareParametersWithLinkingContexts(
			ParameterWithLinkingContext parameterWithContext1, 
			ParameterWithLinkingContext parameterWithContext2) {

		compareParameters(parameterWithContext1.getParameter(), parameterWithContext2.getParameter());
		compareParameters(parameterWithContext1.getLinkingContext(), parameterWithContext2.getLinkingContext());
	}

	public static void compareMethodParameters(BasicParameterNode methodParameterNode1, BasicParameterNode methodParameterNode2) {

		compareNames(methodParameterNode1.getName(), methodParameterNode2.getName());

		compareIntegers(methodParameterNode1.getChoices().size(), methodParameterNode2.getChoices().size(), "Length of choices list differs.");

		for(int i = 0; i < methodParameterNode1.getChoices().size(); i++){
			compareChoices(methodParameterNode1.getChoices().get(i), methodParameterNode2.getChoices().get(i));
		}
	}

	public static void compareConstraintNodes(ConstraintNode constraint1, ConstraintNode constraint2) {

		compareNames(constraint1.getName(), constraint2.getName());
		compareConstraints(constraint1.getConstraint(), constraint2.getConstraint());
	}

	public static void compareConstraints(Constraint constraint1, Constraint constraint2) {

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

	public static void compareTestCases(TestCaseNode testCase1, TestCaseNode testCase2) {

		compareNames(testCase1.getName(), testCase2.getName());
		compareSizes(testCase1.getTestData(), testCase2.getTestData(), "Number of choices differ.");
		for(int i = 0; i < testCase1.getTestData().size(); i++){
			ChoiceNode testValue1 = testCase1.getTestData().get(i);
			ChoiceNode testValue2 = testCase2.getTestData().get(i);

			if(testValue1.getParameter() instanceof BasicParameterNode){
				compareValues(testValue1.getValueString(), testValue2.getValueString());
			}
			else{
				compareChoices(testCase1.getTestData().get(i),testCase2.getTestData().get(i));
			}
		}
	}

	public static void compareChoices(ChoiceNode choice1, ChoiceNode choice2) {

		compareNames(choice1.getName(), choice2.getName());
		compareValues(choice1.getValueString(),choice2.getValueString());
		compareLabels(choice1.getLabels(), choice2.getLabels());
		compareIntegers(choice1.getChoices().size(), choice2.getChoices().size(), "Length of choices list differs.");
		for(int i = 0; i < choice1.getChoices().size(); i++){
			compareChoices(choice1.getChoices().get(i), choice2.getChoices().get(i));
		}
	}

	public static void compareSizes(
			Collection<? extends Object> collection1, 
			Collection<? extends Object> collection2, 
			String errorMessage) {

		int size1 = collection1.size();

		int size2 = collection2.size();

		if (size1 != size2) {
			ExceptionHelper.reportRuntimeException(errorMessage + " " + collection1.size() + " vs " + collection2.size() + ".");
		}
	}

	public static void compareNames(String name, String name2) {
		if(name.equals(name2) == false){
			ExceptionHelper.reportRuntimeException("Different names: " + name + ", " + name2);
		}
	}

	public static void compareTypes(String type1, String type2) {

		if(type1.equals(type2) == false){
			ExceptionHelper.reportRuntimeException("Different types: " + type1 + ", " + type2);
		}
	}

	public static void compareValues(Object value1, Object value2) {

		boolean result = true;
		if(value1 == null){
			result = (value2 == null);
		}
		else{
			result = value1.equals(value2);
		}
		if(!result){
			ExceptionHelper.reportRuntimeException("Value " + value1 + " differ from " + value2);
		}
	}

	public static void compareLabels(Set<String> labels, Set<String> labels2) {

		assertIsTrue(labels.size() == labels2.size(), "Sizes of labels should be equal.");

		for(String label : labels){
			assertIsTrue(labels2.contains(label), "Label2 should contain label1");
		}
	}

	private static void assertIsTrue(boolean b, String message) {

		if (b == true) {
			return;
		}

		ExceptionHelper.reportRuntimeException("True boolean value expected." + " " + message);
	}


	public static void compareIntegers(int size, int size2, String message) {

		if (size == size2) {
			return;
		}

		ExceptionHelper.reportRuntimeException("Integers do not match." + " " + message);
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

	private static void compareDeployedParameters(MethodNode method1, MethodNode method2) {

		List<ParameterWithLinkingContext> deployedParametersWithContexts1 = method1.getDeployedParametersWithLinkingContexts();
		List<ParameterWithLinkingContext> deployedParametersWithContexts2 = method2.getDeployedParametersWithLinkingContexts();

		if (deployedParametersWithContexts1.size() != deployedParametersWithContexts2.size()) {
			ExceptionHelper.reportRuntimeException("Length of deployed parameters in two method differs.");
		}

		int size = deployedParametersWithContexts1.size();

		for (int i = 0; i < size; ++i) {

			ParameterWithLinkingContext parameterWithContext1 = deployedParametersWithContexts1.get(i);
			ParameterWithLinkingContext parameterWithContext2 = deployedParametersWithContexts2.get(i);

			compareParametersWithLinkingContexts(parameterWithContext1,parameterWithContext2);
		}
	}

	private static void compareStrings(String str1, String str2) {

		if (StringHelper.isEqual(str1, str2)) {
			return;
		}

		ExceptionHelper.reportRuntimeException("String values differ");
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
			compareChoices((ChoiceNode)condition1, (ChoiceNode)condition2);
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
		compareSizes(array1.getChildren(), array2.getChildren(), "Number of statements differ.");
		for(int i = 0; i < array1.getChildren().size(); ++i){
			compareStatements(array1.getChildren().get(i), array2.getChildren().get(i));
		}
	}

	private static void compareStaticStatements(StaticStatement statement1, StaticStatement statement2) {
		if(statement1.getValue() != statement2.getValue()){
			ExceptionHelper.reportRuntimeException("Static statements different");
		}
	}

	private static void assertStringsEqual(String valueString, String valueString2, String message) {

		if (valueString.equals(valueString2))  {
			return;
		}

		ExceptionHelper.reportRuntimeException("String values do not match." + " " + message);
	}


}
