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
import java.util.Set;

import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.NameHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.TypeHelper;

public class ModelComparator { // XYX improve method comparator to check parents (if parent matches in model1 then it should match also in model2) 

	// XYX compare constraints of composite parameter

	public static void compareRootNodes(RootNode rootNode1, RootNode rootNode2) {

		NameHelper.compareNames(rootNode1.getName(), rootNode2.getName());
		
		AbstractNodeHelper.compareSizes(rootNode1.getClasses(), rootNode2.getClasses(), "Number of classes differs.");

		for (int i = 0; i < rootNode1.getClasses().size(); ++i) {

			ClassNode classNode1 = rootNode1.getClasses().get(i);
			ClassNode classNode2 = rootNode2.getClasses().get(i);

			AbstractNodeHelper.compareParents(classNode1, rootNode1, classNode2, rootNode2);
			compareClasses(classNode1, classNode2);
		}

		compareSerializedModelsAsLastResort(rootNode1, rootNode2);
	}

	public static void compareClasses(ClassNode classNode1, ClassNode classNode2) {

		NameHelper.compareNames(classNode1.getName(), classNode2.getName());
		AbstractNodeHelper.compareSizes(classNode1.getMethods(), classNode2.getMethods(), "Number of methods differs.");

		for (int i = 0; i < classNode1.getMethods().size(); ++i) {

			MethodNode methodNode1 = classNode1.getMethods().get(i);
			MethodNode methodNode2 = classNode2.getMethods().get(i);

			AbstractNodeHelper.compareParents(methodNode1, classNode1, methodNode2, classNode2);
			compareMethods(methodNode1, methodNode2);
		}
	}

	public static void compareMethods(MethodNode method1, MethodNode method2) {

		if (method1 == null) {
			ExceptionHelper.reportRuntimeException("Empty method 1.");
		}

		if (method2 == null) {
			ExceptionHelper.reportRuntimeException("Empty method 2.");
		}

		NameHelper.compareNames(method1.getName(), method2.getName());
		AbstractNodeHelper.compareSizes(method1.getParameters(), method2.getParameters(), "Number of parameters differs.");
		AbstractNodeHelper.compareSizes(method1.getConstraintNodes(), method2.getConstraintNodes(), "Number of constraints differs.");
		AbstractNodeHelper.compareSizes(method1.getTestCases(), method2.getTestCases(), "Number of test cases differs.");

		compareMethodParameters(method1, method2);
		MethodNodeHelper.compareDeployedParameters(method1, method2);
		compareMethodConstraints(method1, method2);
		compareTestCases(method1, method2);
	}

	private static void compareTestCases(MethodNode methodNode1, MethodNode methodNode2) {

		for (int i =0; i < methodNode1.getTestCases().size(); ++i) {

			TestCaseNode testCaseNode1 = methodNode1.getTestCases().get(i);
			TestCaseNode testCaseNode2 = methodNode2.getTestCases().get(i);

			AbstractNodeHelper.compareParents(testCaseNode1, methodNode1, testCaseNode2, methodNode2);
			compareTestCases(testCaseNode1, testCaseNode2);
		}
	}

	private static void compareMethodConstraints(MethodNode methodNode1, MethodNode methodNode2) {

		// XYX check sizes here and not outside of function

		for (int i =0; i < methodNode1.getConstraintNodes().size(); ++i) {

			ConstraintNode constraintNode1 = methodNode1.getConstraintNodes().get(i);
			ConstraintNode constraintNode2 = methodNode2.getConstraintNodes().get(i);

			AbstractNodeHelper.compareParents(constraintNode1, methodNode1, constraintNode2, methodNode2);
			ConstraintNodeHelper.compareConstraintNodes(constraintNode1, constraintNode2);
		}
	}

	private static void compareMethodParameters(MethodNode methodNode1, MethodNode methodNode2) {

		for (int i =0; i < methodNode1.getParameters().size(); ++i) {


			AbstractParameterNode abstractParameterNode1 = methodNode1.getParameters().get(i);
			AbstractParameterNode abstractParameterNode2 = methodNode2.getParameters().get(i);

			AbstractNodeHelper.compareParents(abstractParameterNode1, methodNode1, abstractParameterNode2, methodNode2);
			AbstractParameterNodeHelper.compareParameters(abstractParameterNode1, abstractParameterNode2);
		}
	}

	public static void compareParametersWithLinkingContexts(
			ParameterWithLinkingContext parameterWithContext1, 
			ParameterWithLinkingContext parameterWithContext2) {

		AbstractParameterNodeHelper.compareParameters(
				parameterWithContext1.getParameter(), parameterWithContext2.getParameter());
		
		AbstractParameterNodeHelper.compareParameters(
				parameterWithContext1.getLinkingContext(), parameterWithContext2.getLinkingContext());
	}

	public static void compareMethodParameters(
			BasicParameterNode methodParameterNode1, 
			BasicParameterNode methodParameterNode2) {

		NameHelper.compareNames(methodParameterNode1.getName(), methodParameterNode2.getName());

		TypeHelper.compareIntegers(methodParameterNode1.getChoices().size(), methodParameterNode2.getChoices().size(), "Length of choices list differs.");

		for(int i = 0; i < methodParameterNode1.getChoices().size(); i++){
			compareChoices(methodParameterNode1.getChoices().get(i), methodParameterNode2.getChoices().get(i));
		}
	}

	public static void compareTestCases(TestCaseNode testCase1, TestCaseNode testCase2) {

		NameHelper.compareNames(testCase1.getName(), testCase2.getName());
		
		AbstractNodeHelper.compareSizes(testCase1.getTestData(), testCase2.getTestData(), "Number of choices differs.");
		
		for(int i = 0; i < testCase1.getTestData().size(); i++){
			
			ChoiceNode choiceNode1 = testCase1.getTestData().get(i);
			ChoiceNode choiceNode2 = testCase2.getTestData().get(i);

			if(choiceNode1.getParameter() instanceof BasicParameterNode){
				StringHelper.compareStrings(choiceNode1.getValueString(), choiceNode2.getValueString(), "Choice values differ.");
			}
			else{
				compareChoices(testCase1.getTestData().get(i),testCase2.getTestData().get(i));
			}
		}
	}

	public static void compareChoices(ChoiceNode choice1, ChoiceNode choice2) {

		NameHelper.compareNames(choice1.getName(), choice2.getName());
		StringHelper.compareStrings(choice1.getValueString(),choice2.getValueString(), "Choice values differ.");
		compareLabels(choice1.getLabels(), choice2.getLabels());
		TypeHelper.compareIntegers(choice1.getChoices().size(), choice2.getChoices().size(), "Length of choices list differs.");
		for(int i = 0; i < choice1.getChoices().size(); i++){
			compareChoices(choice1.getChoices().get(i), choice2.getChoices().get(i));
		}
	}

	public static void compareLabels(Set<String> labels, Set<String> labels2) {

		BooleanHelper.assertIsTrue(labels.size() == labels2.size(), "Sizes of labels should be equal.");

		for(String label : labels){
			BooleanHelper.assertIsTrue(labels2.contains(label), "Label2 should contain label1");
		}
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

}
