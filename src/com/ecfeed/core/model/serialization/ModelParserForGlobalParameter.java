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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

import static com.ecfeed.core.model.serialization.SerializationConstants.*;

public class ModelParserForGlobalParameter implements IModelParserForGlobalParameter {

	private IModelParserForChoice fModelParserForChoice;
	
	public ModelParserForGlobalParameter(IModelParserForChoice modelParserForChoice) {
		fModelParserForChoice = modelParserForChoice;
	}
	
	public Optional<BasicParameterNode> parseGlobalParameter(
			Element element, 
			IModelChangeRegistrator modelChangeRegistrator, 
			
			ListOfStrings errorList) {

		String name, type;
		String defaultValue = null;
		String expected = String.valueOf(false);

		try {
			ModelParserHelper.assertNodeTag(element.getQualifiedName(), SerializationHelperVersion1.getBasicParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(element, errorList);
			type = ModelParserHelper.getAttributeValue(element, TYPE_NAME_ATTRIBUTE, errorList);

			if (element.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = ModelParserHelper.getAttributeValue(element, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, errorList);
				defaultValue = ModelParserHelper.getAttributeValue(element, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, errorList);
			}
		} catch (ParserException e) {
			return Optional.empty();
		}

		BasicParameterNode targetGlobalParameterNode = new BasicParameterNode(name, type, defaultValue, Boolean.parseBoolean(expected), modelChangeRegistrator);

		ModelParserHelper.parseParameterProperties(element, targetGlobalParameterNode);

		List<Element> children = 
				ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getChoiceNodeName());

		for (Element child : children) {

			Optional<ChoiceNode> node = fModelParserForChoice.parseChoice(child, errorList);
			if (node.isPresent()) {
				targetGlobalParameterNode.addChoice(node.get());
			}
		}

		targetGlobalParameterNode.setDescription(ModelParserHelper.parseComments(element));
		targetGlobalParameterNode.setTypeComments(ModelParserHelper.parseTypeComments(element));

		return Optional.ofNullable(targetGlobalParameterNode);
	}

}
