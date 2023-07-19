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

import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForGlobalParameter {

	public Optional<BasicParameterNode> parseGlobalBasicParameter(
			Element element, 
			IModelChangeRegistrator modelChangeRegistrator, 
			ListOfStrings errorList) {

		BasicParameterNode targetGlobalParameterNode = 
				ModelParserForParameterHelper.createBasicParameter(
						element, SerializationHelperVersion1.getBasicParameterNodeName(), modelChangeRegistrator, errorList);

		ModelParserHelper.parseParameterProperties(element, targetGlobalParameterNode);

		ModelParserForParameterHelper.parseChoices(element, targetGlobalParameterNode, modelChangeRegistrator, errorList);

		targetGlobalParameterNode.setDescription(ModelParserHelper.parseComments(element));
		targetGlobalParameterNode.setTypeComments(ModelParserHelper.parseTypeComments(element));

		return Optional.ofNullable(targetGlobalParameterNode);
	}

}
