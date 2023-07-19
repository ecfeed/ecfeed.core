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

import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;

import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForMethodParameter {

	public Optional<BasicParameterNode> parseMethodParameter(
			Element parameterElement, MethodNode method, IAbstractNode parent, ListOfStrings outErrorList) {

		BasicParameterNode targetMethodParameterNode = 
				ModelParserForParameterHelper.createBasicParameter(
						parameterElement, getParameterNodeName(), method.getModelChangeRegistrator(), outErrorList);

		ModelParserHelper.parseParameterProperties(parameterElement, targetMethodParameterNode);

		if (parameterElement.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {
			ModelParserForParameterHelper.setLink(parameterElement, method, targetMethodParameterNode, outErrorList);
			targetMethodParameterNode.setTypeComments(ModelParserHelper.parseTypeComments(parameterElement));
		} 

		ModelParserForParameterHelper.parseChoices(
				parameterElement, 
				targetMethodParameterNode, 
				targetMethodParameterNode.getModelChangeRegistrator(), 
				outErrorList);

		targetMethodParameterNode.setDescription(ModelParserHelper.parseComments(parameterElement));

		return Optional.ofNullable(targetMethodParameterNode);
	}

	private String getParameterNodeName() {
		return SerializationHelperVersion1.getBasicParameterNodeName();
	}

}
