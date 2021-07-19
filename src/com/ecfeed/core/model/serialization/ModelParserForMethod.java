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

import java.util.Optional;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.StringHelper;

import nu.xom.Element;

public class ModelParserForMethod implements IModelParserForMethod {

	IModelParserForMethodParameter fModelParserForMethodParameter;
	IModelParserForTestCase fModelParserForTestCase;
	IModelParserForConstraint fModelParserForConstraint;
	
	public  ModelParserForMethod(
			IModelParserForMethodParameter modelParserForMethodParameter,
			IModelParserForTestCase modelParserForTestCase,
			IModelParserForConstraint modelParserForConstraint) {
		
		fModelParserForMethodParameter = modelParserForMethodParameter;
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

		for (Element child : ModelParserHelper.getIterableChildren(methodElement, SerializationHelperVersion1.getParameterNodeName())) {

			Optional<MethodParameterNode> node = 
					fModelParserForMethodParameter.parseMethodParameter(child, targetMethodNode, errorList);
			if (node.isPresent()) {
				targetMethodNode.addParameter(node.get());
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
