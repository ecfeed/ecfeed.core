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

import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ListOfStrings;

import com.ecfeed.core.utils.LogHelperCore;
import nu.xom.Element;

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
			ListOfStrings errorList) {

		String name;

		try {
			ModelParserHelper.assertNodeTag(element.getQualifiedName(), SerializationHelperVersion1.getCompositeParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(element, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		CompositeParameterNode targetCompositeParameterNode = new CompositeParameterNode(name, method.getModelChangeRegistrator());

		List<Element> children = ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getParameterNodeNames());

		for (Element child : children) {

			if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getBasicParameterNodeName())) {
				fModelParserForMethodParameter.parseMethodParameter(child, method, errorList)
						.ifPresent(targetCompositeParameterNode::addParameter);
			} else if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {
				parseMethodCompositeParameter(child, method, errorList)
						.ifPresent(targetCompositeParameterNode::addParameter);
			} else if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getConstraintName())) {

				try {
					fModelParserForConstraint.parseConstraint(child, targetCompositeParameterNode, errorList)
							.ifPresent(targetCompositeParameterNode::addConstraint);
				} catch (Exception e) {
					LogHelperCore.logError("A composite parameter could not be parsed: " + targetCompositeParameterNode.getName());
				}
			}
		}

		return Optional.ofNullable(targetCompositeParameterNode);
	}
}
