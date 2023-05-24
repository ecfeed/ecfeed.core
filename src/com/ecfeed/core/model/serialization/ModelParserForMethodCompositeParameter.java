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

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.LogHelperCore;

import nu.xom.Element;

import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;

public class ModelParserForMethodCompositeParameter implements IModelParserForMethodCompositeParameter {

	private IModelParserForMethodParameter fModelParserForMethodParameter;
	private IModelParserForConstraint fModelParserForConstraint;

	public ModelParserForMethodCompositeParameter(IModelParserForMethodParameter modelParserForMethodParameter,
			IModelParserForConstraint modelParserForConstraint) {
		fModelParserForMethodParameter = modelParserForMethodParameter;
		fModelParserForConstraint = modelParserForConstraint;
	}

	public Optional<CompositeParameterNode> parseMethodCompositeParameter(
			Element element,
			MethodNode method,
			IParametersParentNode parent,
			ListOfStrings errorList) {

		String name;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationHelperVersion1.getCompositeParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(element, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		CompositeParameterNode targetCompositeParameterNode = new CompositeParameterNode(name, method.getModelChangeRegistrator());
		targetCompositeParameterNode.setParent(parent);

		List<Element> children = ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getParametersAndConstraintsElementNames());

		for (Element child : children) {

			if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getBasicParameterNodeName())) {

				Optional<BasicParameterNode> methodParameter = 
						fModelParserForMethodParameter.parseMethodParameter(
								child, method, targetCompositeParameterNode, errorList);

				if (methodParameter.isPresent()) {
					targetCompositeParameterNode.addParameter(methodParameter.get());
				} else {
					errorList.add("Cannot parse parameter of parent structure: " + targetCompositeParameterNode.getName() + ".");
				}

			} else if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {

				Optional<CompositeParameterNode> compositeParameter = parseMethodCompositeParameter(child, method, targetCompositeParameterNode, errorList);

				if (compositeParameter.isPresent()) {
					targetCompositeParameterNode.addParameter(compositeParameter.get());
				} else {
					errorList.add("Cannot parse structure of parent structure: " + targetCompositeParameterNode.getName() + ".");
				}

			}
		}

		for (Element child : children) {

			if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getConstraintName())) {

				try {
					Optional<ConstraintNode> constraint = 
							fModelParserForConstraint.parseConstraint(child, targetCompositeParameterNode, errorList);

					if (constraint.isPresent()) {
						targetCompositeParameterNode.addConstraint(constraint.get());
					} else {
						errorList.add("Cannot parse constraint of parent structure: " + targetCompositeParameterNode.getName() + ".");
					}

				} catch (Exception e) {
					LogHelperCore.logError("A composite parameter could not be parsed: " + targetCompositeParameterNode.getName());
				}
			}
		}

		if (element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {

			String linkPath;

			try {
				linkPath = ModelParserHelper.getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME, errorList);
			} catch (ParserException e) {
				return Optional.empty();
			}

			AbstractParameterNode link = AbstractParameterNodeHelper.findParameter(linkPath, parent);

			if (link != null) {
				targetCompositeParameterNode.setLinkToGlobalParameter((AbstractParameterNode) link);
			}
		}

		return Optional.ofNullable(targetCompositeParameterNode);
	}
}
