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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForMethodParameter implements IModelParserForMethodParameter {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	public Optional<MethodParameterNode> parseMethodParameter(
			Element parameterElement, MethodNode method, ListOfStrings errorList) {

		String name, type;
		String defaultValue = null;
		String expected = String.valueOf(false);

		try {
			ModelParserHelper.assertNodeTag(parameterElement.getQualifiedName(), getParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(parameterElement, fWhiteCharConverter, errorList);
			type = ModelParserHelper.getAttributeValue(parameterElement, TYPE_NAME_ATTRIBUTE, fWhiteCharConverter, errorList);

			if (parameterElement.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = 
						ModelParserHelper.getAttributeValue(
								parameterElement, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, fWhiteCharConverter, errorList);
				defaultValue = 
						ModelParserHelper.getAttributeValue(
								parameterElement, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, fWhiteCharConverter, errorList);
			}

		} catch (ParserException e) {
			return Optional.empty();
		}

		MethodParameterNode targetMethodParameterNode = 
				new MethodParameterNode(
						name, type, defaultValue, Boolean.parseBoolean(expected), method.getModelChangeRegistrator()
						);

		ModelParserHelper.parseParameterProperties(parameterElement, targetMethodParameterNode);

		if (parameterElement.getAttribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME) != null) {
			boolean linked ;

			try {
				linked = 
						Boolean.parseBoolean(ModelParserHelper.getAttributeValue(
								parameterElement, PARAMETER_IS_LINKED_ATTRIBUTE_NAME, fWhiteCharConverter, errorList));

			} catch (ParserException e) {
				return Optional.empty();
			}

			targetMethodParameterNode.setLinked(linked);
		}

		if (parameterElement.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null && method != null && method.getClassNode() != null) {
			String linkPath;

			try {
				linkPath = 
						ModelParserHelper.getAttributeValue(
								parameterElement, PARAMETER_LINK_ATTRIBUTE_NAME, fWhiteCharConverter, errorList);
			} catch (ParserException e) {
				return Optional.empty();
			}

			GlobalParameterNode link = method.getClassNode().findGlobalParameter(linkPath);

			if (link != null) {
				targetMethodParameterNode.setLink(link);
			} else {
				targetMethodParameterNode.setLinked(false);
			}
		} else {
			targetMethodParameterNode.setLinked(false);
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

		targetMethodParameterNode.setDescription(ModelParserHelper.parseComments(parameterElement, fWhiteCharConverter));

		if (targetMethodParameterNode.isLinked() == false) {
			targetMethodParameterNode.setTypeComments(ModelParserHelper.parseTypeComments(parameterElement, fWhiteCharConverter));
		}

		return Optional.ofNullable(targetMethodParameterNode);
	}

	private String getParameterNodeName() {
		return SerializationHelperVersion1.getParameterNodeName();
	}


}
