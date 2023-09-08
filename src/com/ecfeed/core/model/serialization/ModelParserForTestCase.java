/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import static com.ecfeed.core.model.serialization.SerializationConstants.EXPECTED_PARAMETER_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_CASE_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_PARAMETER_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_SUITE_NAME_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.model.utils.ParameterWithLinkingContextHelper;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForTestCase {

	public static TestCaseNode parseTestCase(
			Element element, MethodNode method, ListOfStrings errorList) {

		TestCaseNode targetTestCaseNode = createAndInitializeTestCase(element, method, errorList);

		if (targetTestCaseNode == null) {
			return null;
		}

		List<Element> choiceElements = getChoiceElements(element);
		List<ParameterWithLinkingContext> deployedParameters = method.getDeployedParametersWithLinkingContexts();

		if (deployedParameters.size() != choiceElements.size()) {
			errorList.add(Messages.WRONG_NUMBER_OF_TEST_PAREMETERS(targetTestCaseNode.getName()));
			return null;
		}

		Optional<List<ChoiceNode>> testData = parseTestData(choiceElements, deployedParameters, errorList);

		if (!testData.isPresent()) {
			return null;
		}

		targetTestCaseNode.setTestData(testData.get());
		targetTestCaseNode.setDescription(ModelParserHelper.parseComments(element));

		return targetTestCaseNode;
	}

	private static TestCaseNode createAndInitializeTestCase (Element testCaseElement, MethodNode method, ListOfStrings errorList) {

		String name = parseTestCaseName(testCaseElement, errorList);

		if (name == null) {
			return null;
		}

		TestCaseNode targetTestCaseNode = new TestCaseNode(name, method.getModelChangeRegistrator(), null);
		return targetTestCaseNode;
	}

	private static Optional<List<ChoiceNode>> parseTestData(
			List<Element> parameterElements, 
			List<ParameterWithLinkingContext> parameters,
			ListOfStrings errorList) {

		List<ChoiceNode> testData = new ArrayList<>();

		for (int index = 0; index < parameterElements.size(); index++) {

			Element choiceElement = parameterElements.get(index);
			ParameterWithLinkingContext parameter = parameters.get(index);

			Optional<ChoiceNode> choiceNode = parseChoiceElement(choiceElement, parameter, errorList);

			if (!choiceNode.isPresent()) {
				return Optional.empty();
			}

			testData.add(choiceNode.get());
		}

		return Optional.of(testData);
	}

	private static Optional<ChoiceNode> parseChoiceElement(
			Element choiceElement, 
			ParameterWithLinkingContext parameterWithLinkingContext,
			ListOfStrings errorList) {

		BasicParameterNode choicesParentParameter = 
				ParameterWithLinkingContextHelper.findChoicesParentParameter(parameterWithLinkingContext);

		String elementName = choiceElement.getLocalName();

		if (elementName.equals(SerializationConstants.TEST_PARAMETER_NODE_NAME)) {

			Optional<ChoiceNode> choiceNode = 
					parseExistingChoiceNode(choiceElement, choicesParentParameter, errorList);

			return choiceNode;
		} 

		if (elementName.equals(SerializationConstants.EXPECTED_PARAMETER_NODE_NAME)) {

			Optional<ChoiceNode> choiceNode = 
					parseNewExpectedChoiceNode(choiceElement, errorList, choicesParentParameter);

			return choiceNode;
		}

		errorList.add("Invalid name of test data element.");
		return Optional.empty();

	}

	private static Optional<ChoiceNode>  parseNewExpectedChoiceNode(
			Element choiceElement, 
			ListOfStrings errorList,
			BasicParameterNode choicesParentParameter) {
		String valueString;

		try {
			valueString = 
					ModelParserHelper.getAttributeValue(
							choiceElement, SerializationConstants.VALUE_ATTRIBUTE_NAME, errorList);

		} catch (Exception e) {
			errorList.add(e.getMessage());
			return Optional.empty();
		}

		if (valueString == null) {
			errorList.add(Messages.MISSING_VALUE_ATTRIBUTE_IN_TEST_CASE_ELEMENT);
			return Optional.empty();
		}

		ChoiceNode testValue = 
				new ChoiceNode(
						AssignmentStatement.ASSIGNMENT_CHOICE_NAME, 
						valueString, 
						choicesParentParameter.getModelChangeRegistrator());

		testValue.setParent(choicesParentParameter);

		return Optional.of(testValue);
	}

	private static Optional<ChoiceNode> parseExistingChoiceNode(
			Element choiceElement,
			BasicParameterNode choicesParentParameter, 
			ListOfStrings errorList) {

		String choiceQualifiedName;

		try {
			choiceQualifiedName = ModelParserHelper.getAttributeValue(
					choiceElement, SerializationHelperVersion1.getChoiceAttributeName(), 
					errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return Optional.empty();
		}

		ChoiceNode choiceNode = ChoiceNodeHelper.findChoice(choicesParentParameter, choiceQualifiedName);

		if (choiceNode == null) {
			return Optional.empty();
		}

		return Optional.of(choiceNode);
	}

	private static List<Element> getChoiceElements(Element element) {
		String[] elementTypes = new String[] { TEST_PARAMETER_NODE_NAME, EXPECTED_PARAMETER_NODE_NAME };
		List<Element> parameterElements = ModelParserHelper.getIterableChildren(element, elementTypes);
		return parameterElements;
	}

	private static String parseTestCaseName(Element element, ListOfStrings errorList) {

		try {
			ModelParserHelper.assertNameEqualsExpectedName(
					element.getQualifiedName(), TEST_CASE_NODE_NAME, errorList);

			String name = ModelParserHelper.getAttributeValue(element, TEST_SUITE_NAME_ATTRIBUTE, errorList);
			return name;

		} catch (Exception e) {
			errorList.add(e.getMessage());
			return null;
		}
	}

}
