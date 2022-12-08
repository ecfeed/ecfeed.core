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

import nu.xom.Element;

public class ModelParserForGlobalCompositeParameter implements IModelParserForGlobalCompositeParameter {

	private IModelParserForGlobalParameter fModelParserForGlobalParameter;

	public ModelParserForGlobalCompositeParameter(IModelParserForGlobalParameter modelParserForGlobalParameter) {
		fModelParserForGlobalParameter = modelParserForGlobalParameter;
	}
	
	public Optional<CompositeParameterNode> parseGlobalCompositeParameter(
			Element element, 
			IModelChangeRegistrator modelChangeRegistrar,
			ListOfStrings errorList) {

		String name;

		try {
			ModelParserHelper.assertNodeTag(element.getQualifiedName(), SerializationHelperVersion1.getCompositeParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(element, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		CompositeParameterNode targetCompositeParameterNode = new CompositeParameterNode(name, modelChangeRegistrar);

		List<Element> children = ModelParserHelper.getIterableChildren(element, new String[]
				{SerializationHelperVersion1.getBasicParameterNodeName(), SerializationHelperVersion1.getCompositeParameterNodeName()}
		);

		for (Element child : children) {

			if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getBasicParameterNodeName())) {
				fModelParserForGlobalParameter.parseGlobalParameter(child, targetCompositeParameterNode.getModelChangeRegistrator(), errorList)
						.ifPresent(targetCompositeParameterNode::addParameter);
			} else if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {
				parseGlobalCompositeParameter(child, targetCompositeParameterNode.getModelChangeRegistrator(), errorList)
						.ifPresent(targetCompositeParameterNode::addParameter);
			}
		}

		return Optional.of(targetCompositeParameterNode);
	}

}
