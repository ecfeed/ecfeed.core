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
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ListOfStrings;

import com.ecfeed.core.utils.LogHelperCore;
import nu.xom.Element;

public class ModelParserForGlobalCompositeParameter implements IModelParserForGlobalCompositeParameter {

	private IModelParserForGlobalParameter fModelParserForGlobalParameter;
	private IModelParserForConstraint fModelParserForConstraint;

	public ModelParserForGlobalCompositeParameter(IModelParserForGlobalParameter modelParserForGlobalParameter,
												  IModelParserForConstraint modelParserForConstraint) {
		fModelParserForGlobalParameter = modelParserForGlobalParameter;
		fModelParserForConstraint = modelParserForConstraint;
	}
	
	public Optional<CompositeParameterNode> parseGlobalCompositeParameter(
			Element element, 
			IModelChangeRegistrator modelChangeRegistrar,
			ListOfStrings errorList) {

		String name;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationHelperVersion1.getCompositeParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(element, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		CompositeParameterNode targetCompositeParameterNode = new CompositeParameterNode(name, modelChangeRegistrar);

		List<Element> children = ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getParametersAndConstraintsElementNames());

		for (Element child : children) {

			if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getBasicParameterNodeName())) {
				fModelParserForGlobalParameter.parseGlobalBasicParameter(child, targetCompositeParameterNode.getModelChangeRegistrator(), errorList)
						.ifPresent(targetCompositeParameterNode::addParameter); // XYX add error checking
			} else if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {
				parseGlobalCompositeParameter(child, targetCompositeParameterNode.getModelChangeRegistrator(), errorList)
						.ifPresent(targetCompositeParameterNode::addParameter); // XYX add error checking
			} else if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getConstraintName())) {

				try {
					fModelParserForConstraint.parseConstraint(child, targetCompositeParameterNode, errorList)
							.ifPresent(targetCompositeParameterNode::addConstraint); // XYX add error checking
				} catch (Exception e) {
					LogHelperCore.logError("A composite parameter could not be parsed: " + targetCompositeParameterNode.getName());
				}
			}
		}

		return Optional.of(targetCompositeParameterNode);
	}

}
