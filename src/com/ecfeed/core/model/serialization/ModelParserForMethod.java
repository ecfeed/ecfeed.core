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

import static com.ecfeed.core.model.serialization.SerializationConstants.METHOD_NODE_NAME;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForMethod {

	public static MethodNode parseMethod(
			Element methodElement, 
			ClassNode classNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) {

		MethodNode targetMethodNode = parseAndInitializeMethod(methodElement, classNode, inOutErrorList);

		if (targetMethodNode == null) {
			return null;
		}

		parseMethodProperties(methodElement, targetMethodNode);

		ModelParserForParameterHelper.parseLocalAndChildParametersWithoutConstraints(
				methodElement, targetMethodNode, elementToNodeMapper, inOutErrorList);

		ModelParserForParameterHelper.parseLocalAndChildConstraints(
				methodElement, targetMethodNode, elementToNodeMapper, inOutErrorList);

		parseDeployedParameters(methodElement, targetMethodNode, inOutErrorList);

		parseTestCases(methodElement, targetMethodNode, inOutErrorList);

		parseComments(methodElement, targetMethodNode);

		return targetMethodNode;
	}

	private static void parseComments(Element methodElement, MethodNode targetMethodNode) {

		targetMethodNode.setDescription(ModelParserHelper.parseComments(methodElement));
	}

	private static MethodNode parseAndInitializeMethod(
			Element methodElement, ClassNode classNode, ListOfStrings inOutErrorList) {

		String name;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(
					methodElement.getQualifiedName(), METHOD_NODE_NAME, inOutErrorList);

			name = ModelParserHelper.getElementName(methodElement, inOutErrorList);
		} catch (Exception e) {
			inOutErrorList.add("Cannot parse name of method.");
			return null;
		}

		MethodNode targetMethodNode = new MethodNode(name, classNode.getModelChangeRegistrator());
		targetMethodNode.setParent(classNode);

		return targetMethodNode;
	}

	private static void parseTestCases(Element methodElement, MethodNode targetMethodNode, ListOfStrings inOutErrorList) {

		try {
			List<Element> testCaseElements = 
					ModelParserHelper.getIterableChildren(
							methodElement, SerializationConstants.TEST_CASE_NODE_NAME);

			for (Element testCaseElement : testCaseElements) {

				TestCaseNode testCase = 
						ModelParserForTestCase.parseTestCase(testCaseElement, targetMethodNode, inOutErrorList);

				if (testCase != null) {
					targetMethodNode.addTestCase(testCase);
				} else {
					inOutErrorList.add("Cannot parse test case for method: " + targetMethodNode.getName() + ".");
				}

			}
		} catch (Exception e) {
			inOutErrorList.add("Failed to parse test cases.");
		}
	}

	private static void parseDeployedParameters(
			Element methodElement, MethodNode targetMethodNode, ListOfStrings inOutErrorList) {

		List<ParameterWithLinkingContext> parametersWithContexts = new ArrayList<>();

		List<Element> deploymentElements = 
				ModelParserHelper.getIterableChildren(
						methodElement, SerializationConstants.METHOD_DEPLOYED_PARAMETERS_TAG);

		for (Element deploymentElement : deploymentElements) {

			parseDeploymentElement(deploymentElement, targetMethodNode, parametersWithContexts, inOutErrorList);
		}

		targetMethodNode.setDeployedParametersWithContexts(parametersWithContexts);
	}

	private static void parseDeploymentElement(
			Element deploymentElement, 
			MethodNode targetMethodNode,
			List<ParameterWithLinkingContext> inOutParametersWithContexts,
			ListOfStrings inOutErrorList) {

		List<Element> childElements = 
				ModelParserHelper.getIterableChildren(
						deploymentElement, SerializationHelperVersion1.getBasicParameterNodeName());

		for (Element childElement : childElements) {
			
			ParameterWithLinkingContext parameterWithLinkingContext = 
					ModelParserForMethodDeployedParameter.parseMethodDeployedParameter(
							childElement, targetMethodNode, inOutErrorList);

			if (parameterWithLinkingContext != null) {
				inOutParametersWithContexts.add(parameterWithLinkingContext);
			} else {
				inOutErrorList.add("Cannot parse deployed element for method: " + targetMethodNode.getName() + ".");
			}

		}
	}

	private static void parseMethodProperties(Element methodElement, MethodNode targetMethodNode) {
		//		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_ METHOD_RUNNER, methodElement, targetMethodNode);
		//		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM, methodElement, targetMethodNode);
		//		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER, methodElement, targetMethodNode);
		//		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER_PATH, methodElement, targetMethodNode);
		//		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM, methodElement, targetMethodNode);
		//		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_START_URL, methodElement, targetMethodNode);
	}

	//	private static void parseMethodProperty(
	//			NodePropertyDefs.PropertyId propertyId, 
	//			Element methodElement, 
	//			MethodNode targetMethodNode) {
	//
	//		String value = ModelParserHelper.getPropertyValue(propertyId, methodElement);
	//		if (StringHelper.isNullOrEmpty(value)) {
	//			return;
	//		}
	//		targetMethodNode.setPropertyValue(propertyId, value);		
	//	}

}
