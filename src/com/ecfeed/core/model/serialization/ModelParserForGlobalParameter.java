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

import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_NAME_ATTRIBUTE;

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForGlobalParameter implements IModelParserForGlobalParameter {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	public Optional<GlobalParameterNode> parseGlobalParameter(
			Element element, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings errorList) {

		String name, type;

		try {
			ModelParserHelper.assertNodeTag(element.getQualifiedName(), SerializationHelperVersion1.getParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(element, fWhiteCharConverter, errorList);
			type = ModelParserHelper.getAttributeValue(element, TYPE_NAME_ATTRIBUTE, fWhiteCharConverter, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		GlobalParameterNode targetGlobalParameterNode = new GlobalParameterNode(name, type, modelChangeRegistrator);

		ModelParserHelper.parseParameterProperties(element, targetGlobalParameterNode);

		List<Element> children = 
				ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getChoiceNodeName());

		ModelParserForChoice modelParserForChoice = new ModelParserForChoice(modelChangeRegistrator);

		for (Element child : children) {

			Optional<ChoiceNode> node = modelParserForChoice.parseChoice(child, errorList);
			if (node.isPresent()) {
				targetGlobalParameterNode.addChoice(node.get());
			}
		}

		targetGlobalParameterNode.setDescription(ModelParserHelper.parseComments(element, fWhiteCharConverter));
		targetGlobalParameterNode.setTypeComments(ModelParserHelper.parseTypeComments(element, fWhiteCharConverter));

		return Optional.ofNullable(targetGlobalParameterNode);
	}

}
