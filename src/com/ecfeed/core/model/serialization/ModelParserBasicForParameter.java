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
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserBasicForParameter {

	public static Optional<BasicParameterNode> parseParameter(
			Element parameterElement, 
			IParametersParentNode parametersParentNodeUsedForLocalParameters,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings outErrorList) {

		BasicParameterNode targetMethodParameterNode = 
				ModelParserForParameterHelper.createBasicParameter(
						parameterElement, 
						SerializationHelperVersion1.getBasicParameterNodeName(), 
						modelChangeRegistrator, outErrorList);

		ModelParserHelper.parseParameterProperties(parameterElement, targetMethodParameterNode);

		if (parameterElement.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {
			ModelParserForParameterHelper.setLink(
					parameterElement, parametersParentNodeUsedForLocalParameters, targetMethodParameterNode, outErrorList);
		} 

		ModelParserForParameterHelper.parseChoices(
				parameterElement, 
				targetMethodParameterNode, 
				targetMethodParameterNode.getModelChangeRegistrator(), 
				outErrorList);

		targetMethodParameterNode.setDescription(ModelParserHelper.parseComments(parameterElement));

		if (!targetMethodParameterNode.isLinked()) {
			targetMethodParameterNode.setTypeComments(ModelParserHelper.parseTypeComments(parameterElement));
		}

		return Optional.ofNullable(targetMethodParameterNode);
	}

}
