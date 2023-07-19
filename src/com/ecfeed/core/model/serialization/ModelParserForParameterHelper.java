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
import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_NAME_ATTRIBUTE;

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForParameterHelper {

	public static BasicParameterNode createBasicParameter(
			Element element, 
			String parameterNodeName,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

		String name, type;
		String defaultValue = null;
		String expected = String.valueOf(false);

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), parameterNodeName, errorList);
			name = ModelParserHelper.getElementName(element, errorList);
			type = ModelParserHelper.getAttributeValue(element, TYPE_NAME_ATTRIBUTE, errorList);

			if (element.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = ModelParserHelper.getAttributeValue(element, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, errorList);
			}

			if (element.getAttribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME) != null) {
				defaultValue = ModelParserHelper.getAttributeValue(element, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, errorList);
			}
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return null;
		}

		BasicParameterNode targetGlobalParameterNode = 
				new BasicParameterNode(name, type, defaultValue, Boolean.parseBoolean(expected), modelChangeRegistrator);

		return targetGlobalParameterNode;
	}

	public static void parseChoices(
			Element element, 
			BasicParameterNode targetGlobalParameterNode, 
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

		ModelParserForChoice modelParserForChoice = 
				new ModelParserForChoice(modelChangeRegistrator);

		List<Element> children = 
				ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getChoiceNodeName());

		for (Element child : children) {

			Optional<ChoiceNode> node = modelParserForChoice.parseChoice(child, errorList);

			if (node.isPresent()) {
				targetGlobalParameterNode.addChoice(node.get());
			}
		}
	}

	public static void parseChoices2(
			Element parameterElement, 
			BasicParameterNode targetMethodParameterNode,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings outErrorList) {

		ModelParserForChoice modelParserForChoice = 
				new ModelParserForChoice(modelChangeRegistrator);

		List<Element> children = 
				ModelParserHelper.getIterableChildren(
						parameterElement, SerializationHelperVersion1.getChoiceNodeName());

		for (Element child : children) {
			Optional<ChoiceNode> node = modelParserForChoice.parseChoice(child, outErrorList);
			if (node.isPresent()) {
				targetMethodParameterNode.addChoice(node.get());
			}
		}
	}


}
