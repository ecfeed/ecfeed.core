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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.SignatureHelper;

import nu.xom.Element;

public class ModelParserForMethodParameter {

	public Optional<BasicParameterNode> parseMethodParameter(
			Element parameterElement, MethodNode method, IAbstractNode parent, ListOfStrings outErrorList) {

		BasicParameterNode targetMethodParameterNode = 
				ModelParserForParameterHelper.createBasicParameter(
						parameterElement, getParameterNodeName(), method.getModelChangeRegistrator(), outErrorList);

		ModelParserHelper.parseParameterProperties(parameterElement, targetMethodParameterNode);


		if (parameterElement.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null && method.getClassNode() != null) {
			String linkPath;

			try {
				linkPath = 
						ModelParserHelper.getAttributeValue(
								parameterElement, PARAMETER_LINK_ATTRIBUTE_NAME, outErrorList);
			} catch (Exception e) {
				outErrorList.add(e.getMessage());
				return Optional.empty();
			}

			//AbstractParameterNode link = method.getClassNode().findGlobalParameter(linkPath);
			AbstractParameterNode link = findLink(linkPath, method);

			if (link != null) {
				targetMethodParameterNode.setLinkToGlobalParameter(link);
			} else {
				// targetMethodParameterNode.setLinked(false);
			}
		} else {
			// targetMethodParameterNode.setLinked(false);
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

	private AbstractParameterNode findLink(String linkPath, MethodNode method) {

		if (linkPath.startsWith(SignatureHelper.SIGNATURE_ROOT_MARKER)) {
			return AbstractParameterNodeHelper.findParameter(linkPath, method);
		}

		// old convention - to be removed in next release when all models would be converted to new convention
		return method.getClassNode().findGlobalParameter(linkPath); 
	}

	private String getParameterNodeName() {
		return SerializationHelperVersion1.getBasicParameterNodeName();
	}

}
