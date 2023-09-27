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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForChoice {

	public static ChoiceNode parseChoice(
			Element element,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

		String name, value;
		boolean isRandomized;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationHelperVersion1.getChoiceNodeName(), errorList);
			name = ModelParserHelper.getElementName(element, errorList);
			value = ModelParserHelper.getAttributeValue(element, VALUE_ATTRIBUTE, errorList);
			isRandomized = ModelParserHelper.getIsRandomizedValue(element, NODE_IS_RADOMIZED_ATTRIBUTE);
		} catch (Exception e) {
			errorList.addIfUnique(e.getMessage());
			return null;
		}

		ChoiceNode choice = new ChoiceNode(name, value, modelChangeRegistrator);
		choice.setRandomizedValue(isRandomized);
		choice.setDescription(ModelParserHelper.parseComments(element));

		for (Element child : ModelParserHelper.getIterableChildren(element)) {

			if (child.getLocalName() == SerializationHelperVersion1.getChoiceNodeName()) {
				ChoiceNode node = parseChoice(child, modelChangeRegistrator, errorList);
				if (node != null) {
					choice.addChoice(node);
				} else {
					errorList.addIfUnique("Cannot parse choice.");
					return null;
				}
			}

			if (child.getLocalName() == SerializationConstants.LABEL_NODE_NAME) {
				choice.addLabel(WhiteCharConverter.getInstance().decode(child.getAttributeValue(SerializationConstants.LABEL_ATTRIBUTE_NAME)));
			}
		}

		return choice;
	}

}
