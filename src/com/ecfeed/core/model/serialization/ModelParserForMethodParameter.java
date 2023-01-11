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

import static com.ecfeed.core.model.serialization.SerializationConstants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_IS_LINKED_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_NAME_ATTRIBUTE;

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForMethodParameter implements IModelParserForMethodParameter {

	public Optional<BasicParameterNode> parseMethodParameter(
			Element parameterElement, MethodNode method, ListOfStrings errorList) {

		String name, type;
		String defaultValue = null;
		String expected = String.valueOf(false);

		try {
			ModelParserHelper.assertNodeTag(parameterElement.getQualifiedName(), getParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(parameterElement, errorList);
			type = ModelParserHelper.getAttributeValue(parameterElement, TYPE_NAME_ATTRIBUTE, errorList);

			if (parameterElement.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = 
						ModelParserHelper.getAttributeValue(
								parameterElement, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, errorList);
				defaultValue = 
						ModelParserHelper.getAttributeValue(
								parameterElement, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, errorList);
			}

		} catch (ParserException e) {
			return Optional.empty();
		}

		BasicParameterNode targetMethodParameterNode = 
				new BasicParameterNode(
						name, type, defaultValue, Boolean.parseBoolean(expected), method.getModelChangeRegistrator()
						);

		ModelParserHelper.parseParameterProperties(parameterElement, targetMethodParameterNode);

		if (parameterElement.getAttribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME) != null) {
			//			boolean linked ;
			//
			//			try {
			//				linked = 
			//						Boolean.parseBoolean(ModelParserHelper.getAttributeValue(
			//								parameterElement, PARAMETER_IS_LINKED_ATTRIBUTE_NAME, errorList));
			//
			//			} catch (ParserException e) {
			//				return Optional.empty();
			//			}
			//
			//targetMethodParameterNode.setLinked(linked);
		}

		if (parameterElement.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null && method.getClassNode() != null) {
			String linkPath;

			try {
				linkPath = 
						ModelParserHelper.getAttributeValue(
								parameterElement, PARAMETER_LINK_ATTRIBUTE_NAME, errorList);
			} catch (ParserException e) {
				return Optional.empty();
			}

			AbstractParameterNode link = method.getClassNode().findGlobalParameter(linkPath);

			if (link != null) {
				targetMethodParameterNode.setLinkToGlobalParameter(link);
			} else {
				// targetMethodParameterNode.setLinked(false);
			}
		} else {
			// targetMethodParameterNode.setLinked(false);
		}

		ModelParserForChoice modelParserForChoice = 
				new ModelParserForChoice(targetMethodParameterNode.getModelChangeRegistrator());

		List<Element> children = 
				ModelParserHelper.getIterableChildren(
						parameterElement, SerializationHelperVersion1.getChoiceNodeName());

		for (Element child : children) {
			Optional<ChoiceNode> node = modelParserForChoice.parseChoice(child, errorList);
			if (node.isPresent()) {
				targetMethodParameterNode.addChoice(node.get());
			}
		}

		targetMethodParameterNode.setDescription(ModelParserHelper.parseComments(parameterElement));

		if (!targetMethodParameterNode.isLinked()) {
			targetMethodParameterNode.setTypeComments(ModelParserHelper.parseTypeComments(parameterElement));
		}

		return Optional.ofNullable(targetMethodParameterNode);
	}

	private String getParameterNodeName() {
		return SerializationHelperVersion1.getBasicParameterNodeName();
	}


}
