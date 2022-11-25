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

import static com.ecfeed.core.model.serialization.SerializationConstants.METHOD_DEPLOYED_PARAMETERS_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.METHOD_NODE_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.StringHelper;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
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
			Element methodElement, ClassNode classNode, ListOfStrings errorList) throws ParserException {

		String name;

		try {
			ModelParserHelper.assertNodeTag(methodElement.getQualifiedName(), METHOD_NODE_NAME, errorList);
			name = ModelParserHelper.getElementName(methodElement, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		MethodNode targetMethodNode = new MethodNode(name, classNode.getModelChangeRegistrator());
		targetMethodNode.setParent(classNode);

		parseMethodProperties(methodElement, targetMethodNode);

		for (Element child : ModelParserHelper.getIterableChildren(methodElement, new String[]
				{SerializationHelperVersion1.getBasicParameterNodeName(), SerializationHelperVersion1.getCompositeParameterNodeName()}
		)) {

			Optional<BasicParameterNode> nodeParameter = fModelParserForMethodParameter.parseMethodParameter(child, targetMethodNode, errorList);
			if (nodeParameter.isPresent()) {
				targetMethodNode.addParameter(nodeParameter.get());
				continue;
			}

			Optional<CompositeParameterNode> nodeComposite = fModelParserForMethodCompositeParameter.parseMethodCompositeParameter(child, targetMethodNode, errorList);
			if (nodeComposite.isPresent()) {
				targetMethodNode.addParameter(nodeComposite.get());
			}
		}

		for (Element child : ModelParserHelper.getIterableChildren(methodElement, SerializationConstants.TEST_CASE_NODE_NAME)) {
			Optional<TestCaseNode> node = fModelParserForTestCase.parseTestCase(child, targetMethodNode, errorList);
			if (node.isPresent()) {
				targetMethodNode.addTestCase(node.get());
			}
		}

		for (Element child : ModelParserHelper.getIterableChildren(methodElement, SerializationConstants.CONSTRAINT_NODE_NAME)) {
			Optional<ConstraintNode> node = fModelParserForConstraint.parseConstraint(child, targetMethodNode, errorList);
			if (node.isPresent()) {
				targetMethodNode.addConstraint(node.get());
			}
		}

		List<BasicParameterNode> parametersDeployed = new ArrayList<>();
		for (Element child : ModelParserHelper.getIterableChildren(methodElement, SerializationConstants.METHOD_DEPLOYED_PARAMETERS_NAME)) {
			for (Element childNested : ModelParserHelper.getIterableChildren(child, SerializationHelperVersion1.getBasicParameterNodeName())) {
				Optional<BasicParameterNode> parameter = fModelParserForMethodDeployedParameter.parseMethodDeployedParameter(childNested, targetMethodNode, errorList);

				if (parameter.isPresent()) {
					parametersDeployed.add(parameter.get());
				}
			}
		}
		targetMethodNode.setDeployedParameters(parametersDeployed);

		targetMethodNode.setDescription(ModelParserHelper.parseComments(methodElement));

		return Optional.ofNullable(targetMethodNode);
	}

	private void parseMethodProperties(Element methodElement, MethodNode targetMethodNode) {
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER_PATH, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_START_URL, methodElement, targetMethodNode);
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
