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

import static com.ecfeed.core.model.serialization.SerializationConstants.NODE_IS_RADOMIZED_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.VALUE_ATTRIBUTE;

import java.util.Optional;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForChoice implements IModelParserForChoice {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();
	private IModelChangeRegistrator fModelChangeRegistrator;

	public ModelParserForChoice(IModelChangeRegistrator modelChangeRegistrator) {
		fModelChangeRegistrator = modelChangeRegistrator;
	}

	public Optional<ChoiceNode> parseChoice(
			Element element, ListOfStrings errorList) {

		String name, value;
		boolean isRandomized;

		try {
			ModelParserHelper.assertNodeTag(element.getQualifiedName(), SerializationHelperVersion1.getChoiceNodeName(), errorList);
			name = ModelParserHelper.getElementName(element, fWhiteCharConverter, errorList);
			value = ModelParserHelper.getAttributeValue(element, VALUE_ATTRIBUTE, fWhiteCharConverter, errorList);
			isRandomized = ModelParserHelper.getIsRandomizedValue(element, NODE_IS_RADOMIZED_ATTRIBUTE, fWhiteCharConverter);
		} catch (ParserException e) {
			return Optional.empty();
		}

		ChoiceNode choice = new ChoiceNode(name, value, fModelChangeRegistrator);
		choice.setRandomizedValue(isRandomized);
		choice.setDescription(ModelParserHelper.parseComments(element, fWhiteCharConverter));

		for (Element child : ModelParserHelper.getIterableChildren(element)) {

			if (child.getLocalName() == SerializationHelperVersion1.getChoiceNodeName()) {
				Optional<ChoiceNode> node = parseChoice(child, errorList);
				if (node.isPresent()) {
					choice.addChoice(node.get());
				} else {
					return Optional.empty();
				}
			}

			if (child.getLocalName() == SerializationConstants.LABEL_NODE_NAME) {
				choice.addLabel(fWhiteCharConverter.decode(child.getAttributeValue(SerializationConstants.LABEL_ATTRIBUTE_NAME)));
			}
		}

		return Optional.ofNullable(choice);
	}

}
