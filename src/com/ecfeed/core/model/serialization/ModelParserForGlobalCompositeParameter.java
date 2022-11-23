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
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ListOfStrings;
import nu.xom.Element;

import java.util.List;
import java.util.Optional;

import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_NAME_ATTRIBUTE;

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

		CompositeParameterNode targetGlobalCompositeParameterNode = new CompositeParameterNode(name, modelChangeRegistrar);

		List<Element> children = ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getChoiceNodeName());

		for (Element child : children) {

			Optional<BasicParameterNode> parameterBasic = fModelParserForGlobalParameter.parseGlobalParameter(child, targetGlobalCompositeParameterNode.getModelChangeRegistrator(), errorList);
			if (parameterBasic.isPresent()) {
				targetGlobalCompositeParameterNode.addParameter(parameterBasic.get());
				continue;
			}

			Optional<CompositeParameterNode> parameterComposite = parseGlobalCompositeParameter(child, targetGlobalCompositeParameterNode.getModelChangeRegistrator(), errorList);
			if (parameterComposite.isPresent()) {
				targetGlobalCompositeParameterNode.addParameter(parameterComposite.get());
			}
		}

		return Optional.ofNullable(targetGlobalCompositeParameterNode);
	}

}
