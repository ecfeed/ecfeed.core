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

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.SignatureHelper;

import nu.xom.Element;

public class ModelParserForCompositeParameter {

	public static Optional<CompositeParameterNode> parseParameter(
			Element element,
			IParametersParentNode parametersParentNode,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

		Optional<CompositeParameterNode> compositeParameterNode = 
				parseParameterWithoutConstraints(element, parametersParentNode, modelChangeRegistrator, errorList);

		if (!compositeParameterNode.isPresent()) {
			return compositeParameterNode;
		}

		parseConstraints(element, compositeParameterNode.get(), errorList);

		return compositeParameterNode;
	}

	private static Optional<CompositeParameterNode> parseParameterWithoutConstraints(
			Element element,
			IParametersParentNode parametersParentNode,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

		CompositeParameterNode targetCompositeParameterNode = 
				ModelParserForParameterHelper.createCompositeParameter(
						element, parametersParentNode, modelChangeRegistrator, errorList);

		String[] parametersAndConstraintsElementNames = 
				SerializationHelperVersion1.getParametersAndConstraintsElementNames();

		List<Element> children = 
				ModelParserHelper.getIterableChildren(element, parametersAndConstraintsElementNames);

		parseParameters(children, targetCompositeParameterNode, parametersParentNode, errorList);

		ModelParserForParameterHelper.parseConstraints(children, targetCompositeParameterNode, errorList);

		if (element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {

			setLinkToGlobalParameter(element, parametersParentNode, targetCompositeParameterNode, errorList);
		}

		return Optional.ofNullable(targetCompositeParameterNode);
	}

	private static void parseConstraints(
			Element element,
			CompositeParameterNode targetCompositeParameterNode,
			ListOfStrings errorList) {

		String[] parametersAndConstraintsElementNames = 
				SerializationHelperVersion1.getParametersAndConstraintsElementNames();

		List<Element> children = 
				ModelParserHelper.getIterableChildren(element, parametersAndConstraintsElementNames);

		ModelParserForParameterHelper.parseConstraints(children, targetCompositeParameterNode, errorList);
	}

	private static void setLinkToGlobalParameter(
			Element element, 
			IParametersParentNode parametersParentNode,
			CompositeParameterNode targetCompositeParameterNode, 
			ListOfStrings errorList) {

		String linkPath;

		try {
			linkPath = ModelParserHelper.getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME, errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return;
		}

		AbstractParameterNode link = findLink(linkPath, parametersParentNode);

		if (link != null) {
			targetCompositeParameterNode.setLinkToGlobalParameter((AbstractParameterNode) link);
		} else {
			errorList.add("Cannot find link for parameter: " + targetCompositeParameterNode.getName());
		}
	}

	private static void parseParameters(
			List<Element> elementsOfParametersAndConstraints, 
			CompositeParameterNode targetCompositeParameterNode,
			IParametersParentNode parametersParentNode, 
			ListOfStrings errorList) {

		for (Element child : elementsOfParametersAndConstraints) {

			parseConditionallyParameterElement(child, parametersParentNode, targetCompositeParameterNode, errorList);
		}
	}

	private static void parseConditionallyParameterElement(
			Element child, 
			IParametersParentNode parametersParentNode,
			CompositeParameterNode targetCompositeParameterNode, 
			ListOfStrings errorList) {

		if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getBasicParameterNodeName())) {

			Optional<BasicParameterNode> methodParameter = 
					new ModelParserBasicForParameter().parseParameter(
							child, parametersParentNode, parametersParentNode.getModelChangeRegistrator(), errorList);

			if (methodParameter.isPresent()) {
				targetCompositeParameterNode.addParameter(methodParameter.get());
			} else {
				errorList.add("Cannot parse parameter of parent structure: " + targetCompositeParameterNode.getName() + ".");
			}

			return;
		} 

		if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {

			Optional<CompositeParameterNode> compositeParameter = 
					parseParameter(child, parametersParentNode, parametersParentNode.getModelChangeRegistrator(), errorList);

			if (compositeParameter.isPresent()) {
				targetCompositeParameterNode.addParameter(compositeParameter.get());
			} else {
				errorList.add("Cannot parse structure of parent structure: " + targetCompositeParameterNode.getName() + ".");
			}
		}
	}

	private static AbstractParameterNode findLink(String linkPath, IParametersParentNode parametersParentNode) {

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
