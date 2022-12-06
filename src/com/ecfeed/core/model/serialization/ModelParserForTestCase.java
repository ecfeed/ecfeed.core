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
import java.util.stream.Collectors;

import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForTestCase implements IModelParserForTestCase {

	public Optional<TestCaseNode> parseTestCase(
			Element element, MethodNode method, ListOfStrings errorList) {

		String name;

		try {
			ModelParserHelper.assertNodeTag(element.getQualifiedName(), TEST_CASE_NODE_NAME, errorList);
			name = ModelParserHelper.getAttributeValue(element, TEST_SUITE_NAME_ATTRIBUTE, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		String[] elementTypes = new String[] { TEST_PARAMETER_NODE_NAME, EXPECTED_PARAMETER_NODE_NAME };
		List<Element> parameterElements = ModelParserHelper.getIterableChildren(element, elementTypes);
		
		List<BasicParameterNode> parameters;

		if (method.isDeployed()) {
			parameters = method.getDeployedMethodParameters().stream().map(BasicParameterNode::getDeploymentParameter).collect(Collectors.toList());
		} else {
			parameters = method.getParametersAsBasic();
		}

		List<ChoiceNode> testData = new ArrayList<>();

		if (parameters.size() != parameterElements.size()) {
			errorList.add(Messages.WRONG_NUMBER_OF_TEST_PAREMETERS(name));
			return Optional.empty();
		}

		for (int i = 0; i < parameterElements.size(); i++) {
			Element testParameterElement = parameterElements.get(i);
			BasicParameterNode parameter = parameters.get(i);
			ChoiceNode testValue = null;

			if (testParameterElement.getLocalName().equals(SerializationConstants.TEST_PARAMETER_NODE_NAME)) {
				String choiceName;

				try {
					choiceName = ModelParserHelper.getAttributeValue(
							testParameterElement, SerializationHelperVersion1.getChoiceAttributeName(), 
							errorList);
				} catch (ParserException e) {
					return Optional.empty();
				}

				testValue = parameter.getChoice(choiceName);
				if (testValue == null) {
					errorList.add(Messages.PARTITION_DOES_NOT_EXIST(parameter.getName(), choiceName));
					return Optional.empty();
				}

			} else if (testParameterElement.getLocalName().equals(SerializationConstants.EXPECTED_PARAMETER_NODE_NAME)) {
				String valueString;

				try {
					valueString = 
							ModelParserHelper.getAttributeValue(
									testParameterElement, SerializationConstants.VALUE_ATTRIBUTE_NAME, errorList);
				} catch (ParserException e) {
					return Optional.empty();
				}

				if (valueString == null) {
					errorList.add(Messages.MISSING_VALUE_ATTRIBUTE_IN_TEST_CASE_ELEMENT);
					return Optional.empty();
				}

				testValue = new ChoiceNode(AssignmentStatement.ASSIGNMENT_CHOICE_NAME, valueString, parameter.getModelChangeRegistrator());
				testValue.setParent(parameter);
			}

			testData.add(testValue);
		}

		TestCaseNode targetTestCaseNode = new TestCaseNode(name, method.getModelChangeRegistrator(), testData);
		targetTestCaseNode.setDescription(ModelParserHelper.parseComments(element));

		return Optional.ofNullable(targetTestCaseNode);
	}

}
