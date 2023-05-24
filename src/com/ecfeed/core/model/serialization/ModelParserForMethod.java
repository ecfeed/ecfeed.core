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
import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.StringHelper;

import nu.xom.Element;

public class ModelParserForMethod implements IModelParserForMethod {

	IModelParserForMethodParameter fModelParserForMethodParameter;
	IModelParserForMethodCompositeParameter fModelParserForMethodCompositeParameter;
	IModelParserForMethodDeployedParameter fModelParserForMethodDeployedParameter;
	IModelParserForTestCase fModelParserForTestCase;
	IModelParserForConstraint fModelParserForConstraint;

	public  ModelParserForMethod(
			IModelParserForMethodParameter modelParserForMethodParameter,
			IModelParserForMethodCompositeParameter modelParserForMethodCompositeParameter,
			IModelParserForMethodDeployedParameter modelParserForMethodDeployedParameter,
			IModelParserForTestCase modelParserForTestCase,
			IModelParserForConstraint modelParserForConstraint) {

		fModelParserForMethodParameter = modelParserForMethodParameter;
		fModelParserForMethodCompositeParameter = modelParserForMethodCompositeParameter;
		fModelParserForMethodDeployedParameter = modelParserForMethodDeployedParameter;
		fModelParserForTestCase = modelParserForTestCase;
		fModelParserForConstraint = modelParserForConstraint;
	}

	public Optional<MethodNode> parseMethod(
			Element methodElement, ClassNode classNode, ListOfStrings inOutErrorList) {

		MethodNode targetMethodNode = parseAndInitializeMethod(methodElement, classNode, inOutErrorList);

		if (targetMethodNode == null) {
			return Optional.empty();
		}

		parseMethodProperties(methodElement, targetMethodNode);

		parseParameters(methodElement, targetMethodNode, inOutErrorList);

		parseConstraints(methodElement, targetMethodNode, inOutErrorList);

		parseDeployedParameters(methodElement, targetMethodNode, inOutErrorList);

		parseTestCases(methodElement, targetMethodNode, inOutErrorList);

		parseComments(methodElement, targetMethodNode);

		return Optional.of(targetMethodNode);
	}

	private void parseComments(Element methodElement, MethodNode targetMethodNode) {

		targetMethodNode.setDescription(ModelParserHelper.parseComments(methodElement));
	}

	private MethodNode parseAndInitializeMethod(
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

	private void parseParameters(Element methodElement, MethodNode targetMethodNode, ListOfStrings inOutErrorList) {

		List<Element> parameterElements = 
				ModelParserHelper.getIterableChildren(
						methodElement, SerializationHelperVersion1.getParametersElementNames());

		for (Element parameterElement : parameterElements) {

			parseParameterElement(parameterElement, targetMethodNode, inOutErrorList);
		}

	}

	private void parseParameterElement(
			Element parameterElement, MethodNode targetMethodNode, ListOfStrings inOutErrorList) {

		String basicParameterElementName = SerializationHelperVersion1.getBasicParameterNodeName(); // TODO MO-RE looks like a bug - version 1 ??

		if (ModelParserHelper.verifyElementName(parameterElement, basicParameterElementName)) {

			Optional<BasicParameterNode> basicParameterNode = 
					fModelParserForMethodParameter.parseMethodParameter(
							parameterElement, targetMethodNode, targetMethodNode, inOutErrorList);

			if (basicParameterNode.isPresent()) {
				targetMethodNode.addParameter(basicParameterNode.get());
			} else {
				inOutErrorList.add("Cannot parse parameter for method: " + targetMethodNode.getName() + ".");
			}
			
			return;
		} 

		String compositeParameterElementName = SerializationHelperVersion1.getCompositeParameterNodeName();

		if (ModelParserHelper.verifyElementName(parameterElement, compositeParameterElementName)) {

			Optional<CompositeParameterNode> compositeParameter = 
					fModelParserForMethodCompositeParameter.parseMethodCompositeParameter(
							parameterElement, targetMethodNode, targetMethodNode, inOutErrorList);
			
			if (compositeParameter.isPresent()) {
				targetMethodNode.addParameter(compositeParameter.get());
			} else {
				inOutErrorList.add("Cannot parse structure for method: " + targetMethodNode.getName() + ".");
			}

			return;
		}

		inOutErrorList.add("Invalid type of parameter element.");
	}

	private void parseConstraints(
			Element methodElement, MethodNode targetMethodNode, ListOfStrings inOutErrorList) {

		List<Element> constraintElements = 
				ModelParserHelper.getIterableChildren(methodElement, SerializationConstants.CONSTRAINT_NODE_NAME);

		for (Element constraintElement : constraintElements) {

			Optional<ConstraintNode> constraintNode = 
					fModelParserForConstraint.parseConstraint(constraintElement, targetMethodNode, inOutErrorList);

			if (constraintNode.isPresent()) {
				targetMethodNode.addConstraint(constraintNode.get());
			} else {
				inOutErrorList.add("Cannot parse constraint for method: " + targetMethodNode.getName() + ".");
			}
		}
	}

	private void parseTestCases(Element methodElement, MethodNode targetMethodNode, ListOfStrings inOutErrorList) {

		try {
			List<Element> testCaseElements = 
					ModelParserHelper.getIterableChildren(
							methodElement, SerializationConstants.TEST_CASE_NODE_NAME);

			for (Element testCaseElement : testCaseElements) {

				Optional<TestCaseNode> testCase = 
						fModelParserForTestCase.parseTestCase(testCaseElement, targetMethodNode, inOutErrorList);
				
				if (testCase.isPresent()) {
					targetMethodNode.addTestCase(testCase.get());
				} else {
					inOutErrorList.add("Cannot parse test case for method: " + targetMethodNode.getName() + ".");
				}

			}
		} catch (Exception e) {
			inOutErrorList.add("Failed to parse test cases.");
		}
	}

	private void parseDeployedParameters(
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

	private void parseDeploymentElement(
			Element deploymentElement, 
			MethodNode targetMethodNode,
			List<ParameterWithLinkingContext> inOutParametersWithContexts,
			ListOfStrings inOutErrorList) {

		List<Element> iterableChildren = 
				ModelParserHelper.getIterableChildren(
						deploymentElement, SerializationHelperVersion1.getBasicParameterNodeName());

		for (Element childNested : iterableChildren) {

			Optional<ParameterWithLinkingContext> parameterWithLinkingContext = 
					fModelParserForMethodDeployedParameter.parseMethodDeployedParameter(
							childNested, targetMethodNode, inOutErrorList);

			if (parameterWithLinkingContext.isPresent()) {
				inOutParametersWithContexts.add(parameterWithLinkingContext.get());
			} else {
				inOutErrorList.add("Cannot parse deployed element for method: " + targetMethodNode.getName() + ".");
			}

		}
	}

	private void parseMethodProperties(Element methodElement, MethodNode targetMethodNode) {
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER, methodElement, targetMethodNode); // TODO MO-RE obsolete property ?
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM, methodElement, targetMethodNode);; // TODO MO-RE obsolete property ?
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER, methodElement, targetMethodNode);; // TODO MO-RE obsolete property ?
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER_PATH, methodElement, targetMethodNode); ; // TODO MO-RE obsolete property ?
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM, methodElement, targetMethodNode); ; // TODO MO-RE obsolete property ?
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_START_URL, methodElement, targetMethodNode); ; // TODO MO-RE obsolete property ?
	}

	private void parseMethodProperty(
			NodePropertyDefs.PropertyId propertyId, 
			Element methodElement, 
			MethodNode targetMethodNode) {

		String value = ModelParserHelper.getPropertyValue(propertyId, methodElement);
		if (StringHelper.isNullOrEmpty(value)) {
			return;
		}
		targetMethodNode.setPropertyValue(propertyId, value);		
	}

}
