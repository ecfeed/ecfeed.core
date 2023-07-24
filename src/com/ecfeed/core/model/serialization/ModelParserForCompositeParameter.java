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
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.SignatureHelper;

import nu.xom.Element;

public class ModelParserForCompositeParameter {

	public static Optional<CompositeParameterNode> parseParameterWithoutConstraints(
			Element element,
			IParametersParentNode parametersParentNode,
			IModelChangeRegistrator modelChangeRegistrator,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings errorList) {

		Optional<CompositeParameterNode> compositeParameterNode = 
				parseParameterWithoutConstraintsIntr(
						element, parametersParentNode, modelChangeRegistrator, elementToNodeMapper, errorList);

		if (!compositeParameterNode.isPresent()) {
			return compositeParameterNode;
		}

		return compositeParameterNode;
	}

	private static Optional<CompositeParameterNode> parseParameterWithoutConstraintsIntr(
			Element element,
			IParametersParentNode parametersParentNode,
			IModelChangeRegistrator modelChangeRegistrator,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings errorList) {

		CompositeParameterNode targetCompositeParameterNode = 
				ModelParserForParameterHelper.createCompositeParameter(
						element, parametersParentNode, modelChangeRegistrator, errorList);

		ModelParserForParameterHelper.parseLocalAndChildParametersWithoutConstraints(
				element, targetCompositeParameterNode, elementToNodeMapper, errorList);


		if (element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {

			setLinkToGlobalParameter(element, 
					//parametersParentNode, 
					targetCompositeParameterNode, errorList);
		}

		return Optional.ofNullable(targetCompositeParameterNode);
	}

	private static void setLinkToGlobalParameter(
			Element element, 
			CompositeParameterNode targetCompositeParameterNode, 
			ListOfStrings errorList) {

		String linkPath;

		try {
			linkPath = ModelParserHelper.getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME, errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return;
		}

		AbstractParameterNode link = findLink(linkPath, targetCompositeParameterNode);

		if (link != null) {
			targetCompositeParameterNode.setLinkToGlobalParameter((AbstractParameterNode) link);
		} else {
			errorList.add("Cannot find link for parameter: " + targetCompositeParameterNode.getName());
		}
	}

	private static AbstractParameterNode findLink(String linkPath, IParametersParentNode parametersParentNode) { // XYX combine with method find link from ModelParserForParameterHelper

		AbstractParameterNode link = AbstractParameterNodeHelper.findParameter(linkPath, parametersParentNode);

		if (link!=null) {
			return link;
		}

		RootNode rootNode = RootNodeHelper.findRootNode(parametersParentNode);

		if (rootNode == null) {
			return null;
		}

		String newLinkPath = 
				SignatureHelper.SIGNATURE_ROOT_MARKER + rootNode.getName() + 
				SignatureHelper.SIGNATURE_NAME_SEPARATOR + linkPath;

		link = AbstractParameterNodeHelper.findParameter(newLinkPath, parametersParentNode);

		if (link == null) {
			return null;
		}

		return link;
	}
}
