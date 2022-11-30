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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ListOfStrings;
import nu.xom.Element;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.ecfeed.core.model.serialization.SerializationConstants.*;

public class ModelParserForMethodCompositeParameter implements IModelParserForMethodCompositeParameter {

	private IModelParserForMethodParameter fModelParserForMethodParameter;

	public ModelParserForMethodCompositeParameter(IModelParserForMethodParameter modelParserForMethodParameter) {
		fModelParserForMethodParameter = modelParserForMethodParameter;
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

		List<Element> children = ModelParserHelper.getIterableChildren(element, new String[]
				{SerializationHelperVersion1.getBasicParameterNodeName(), SerializationHelperVersion1.getCompositeParameterNodeName()}
		);

		for (Element child : children) {

			if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getBasicParameterNodeName())) {
				fModelParserForMethodParameter.parseMethodParameter(child, method, errorList)
						.ifPresent(targetCompositeParameterNode::addParameter);
			} else if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {
				parseMethodCompositeParameter(child, method, errorList)
						.ifPresent(targetCompositeParameterNode::addParameter);
			}
		}

		return Optional.ofNullable(targetCompositeParameterNode);
	}
}
