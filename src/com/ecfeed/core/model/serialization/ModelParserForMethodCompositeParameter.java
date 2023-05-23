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
			IAbstractNode parent,
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
				fModelParserForMethodParameter.parseMethodParameter(child, method, targetCompositeParameterNode, errorList)
				.ifPresent(targetCompositeParameterNode::addParameter); // XYX add error checking
			} else if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {
				parseMethodCompositeParameter(child, method, targetCompositeParameterNode, errorList)
				.ifPresent(targetCompositeParameterNode::addParameter); // XYX add error checking
			}
		}

		for (Element child : children) {

			if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getConstraintName())) {

				try {
					fModelParserForConstraint.parseConstraint(child, targetCompositeParameterNode, errorList)
					.ifPresent(targetCompositeParameterNode::addConstraint); // XYX add error checking
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

			IAbstractNode linkValue = method.getRoot();

			for (String segment : linkPath.split(":")) {
				linkValue = linkValue.getChild(segment);

				if (linkValue == null) {
					if (method.getParent().getName().equals(segment)) {
						linkValue = method.getParent();
					}
				}
			}

			if (linkValue != null) {
				targetCompositeParameterNode.setLinkToGlobalParameter((AbstractParameterNode) linkValue);
			}
		}

		return Optional.ofNullable(targetCompositeParameterNode);
	}
}
