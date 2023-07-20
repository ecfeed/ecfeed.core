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
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.SignatureHelper;

import nu.xom.Element;

public class ModelParserForMethodCompositeParameter {

	private ModelParserBasicForParameter fModelParserForMethodParameter;

	public ModelParserForMethodCompositeParameter(
			ModelParserBasicForParameter modelParserForMethodParameter) {

		fModelParserForMethodParameter = modelParserForMethodParameter;
	}

	public Optional<CompositeParameterNode> parseMethodCompositeParameter(
			Element element,
			MethodNode method,
			IParametersParentNode parent,
			ListOfStrings errorList) {

		CompositeParameterNode targetCompositeParameterNode = 
				ModelParserForParameterHelper.createCompositeParameter(
						element, parent, method.getModelChangeRegistrator(), errorList);

		String[] parametersAndConstraintsElementNames = 
				SerializationHelperVersion1.getParametersAndConstraintsElementNames();

		List<Element> children = 
				ModelParserHelper.getIterableChildren(element, parametersAndConstraintsElementNames);

		parseParameters(children, targetCompositeParameterNode, method, errorList);

		ModelParserForParameterHelper.parseConstraints(children, targetCompositeParameterNode, errorList);

		if (element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {

			setLinkToGlobalParameter(element, parent, targetCompositeParameterNode, errorList);
		}

		return Optional.ofNullable(targetCompositeParameterNode);
	}

	private void setLinkToGlobalParameter(
			Element element, 
			IParametersParentNode parent,
			CompositeParameterNode targetCompositeParameterNode, 
			ListOfStrings errorList) {

		String linkPath;

		try {
			linkPath = ModelParserHelper.getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME, errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return;
		}

		AbstractParameterNode link = findLink(linkPath, parent);

		if (link != null) {
			targetCompositeParameterNode.setLinkToGlobalParameter((AbstractParameterNode) link);
		} else {
			errorList.add("Cannot find link for parameter: " + targetCompositeParameterNode.getName());
		}
	}

	private void parseParameters(
			List<Element> elementsOfParametersAndConstraints, 
			CompositeParameterNode targetCompositeParameterNode,
			MethodNode method, 
			ListOfStrings errorList) {

		for (Element child : elementsOfParametersAndConstraints) {

			parseConditionallyParameterElement(child, method, targetCompositeParameterNode, errorList);
		}
	}

	private void parseConditionallyParameterElement(
			Element child, 
			MethodNode method,
			CompositeParameterNode targetCompositeParameterNode, 
			ListOfStrings errorList) {

		if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getBasicParameterNodeName())) {

			Optional<BasicParameterNode> methodParameter = 
					fModelParserForMethodParameter.parseParameter(
							child, method, method.getModelChangeRegistrator(), errorList);

			if (methodParameter.isPresent()) {
				targetCompositeParameterNode.addParameter(methodParameter.get());
			} else {
				errorList.add("Cannot parse parameter of parent structure: " + targetCompositeParameterNode.getName() + ".");
			}

			return;
		} 

		if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {

			Optional<CompositeParameterNode> compositeParameter = parseMethodCompositeParameter(child, method, targetCompositeParameterNode, errorList);

			if (compositeParameter.isPresent()) {
				targetCompositeParameterNode.addParameter(compositeParameter.get());
			} else {
				errorList.add("Cannot parse structure of parent structure: " + targetCompositeParameterNode.getName() + ".");
			}
		}
	}

	private AbstractParameterNode findLink(String linkPath, IParametersParentNode parent) {

		AbstractParameterNode link = AbstractParameterNodeHelper.findParameter(linkPath, parent);

		if (link!=null) {
			return link;
		}

		RootNode rootNode = RootNodeHelper.findRootNode(parent);

		if (rootNode == null) {
			return null;
		}

		String newLinkPath = 
				SignatureHelper.SIGNATURE_ROOT_MARKER + rootNode.getName() + 
				SignatureHelper.SIGNATURE_NAME_SEPARATOR + linkPath;

		link = AbstractParameterNodeHelper.findParameter(newLinkPath, parent);

		if (link == null) {
			return null;
		}

		return link;
	}
}
