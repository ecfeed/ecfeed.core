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

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.SignatureHelper;

import nu.xom.Element;

import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;

public class ModelParserForMethodCompositeParameter {

	private ModelParserForMethodParameter fModelParserForMethodParameter;
	private ModelParserForConstraint fModelParserForConstraint;

	public ModelParserForMethodCompositeParameter(
			ModelParserForMethodParameter modelParserForMethodParameter,
			ModelParserForConstraint modelParserForConstraint) {
		
		fModelParserForMethodParameter = modelParserForMethodParameter;
		fModelParserForConstraint = modelParserForConstraint;
	}

	public Optional<CompositeParameterNode> parseMethodCompositeParameter(
			Element element,
			MethodNode method,
			IParametersParentNode parent,
			ListOfStrings errorList) {

		String nameOfCompositeParameter;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationHelperVersion1.getCompositeParameterNodeName(), errorList);
			nameOfCompositeParameter = ModelParserHelper.getElementName(element, errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return Optional.empty();
		}

		CompositeParameterNode targetCompositeParameterNode = new CompositeParameterNode(nameOfCompositeParameter, method.getModelChangeRegistrator());
		targetCompositeParameterNode.setParent(parent);

		List<Element> children = ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getParametersAndConstraintsElementNames());

		for (Element child : children) {

			if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getBasicParameterNodeName())) {

				Optional<BasicParameterNode> methodParameter = 
						fModelParserForMethodParameter.parseMethodParameter(
								child, method, targetCompositeParameterNode, errorList);

				if (methodParameter.isPresent()) {
					targetCompositeParameterNode.addParameter(methodParameter.get());
				} else {
					errorList.add("Cannot parse parameter of parent structure: " + targetCompositeParameterNode.getName() + ".");
				}

			} else if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {

				Optional<CompositeParameterNode> compositeParameter = parseMethodCompositeParameter(child, method, targetCompositeParameterNode, errorList);

				if (compositeParameter.isPresent()) {
					targetCompositeParameterNode.addParameter(compositeParameter.get());
				} else {
					errorList.add("Cannot parse structure of parent structure: " + targetCompositeParameterNode.getName() + ".");
				}

			}
		}

		for (Element child : children) {

			if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getConstraintName())) {

				try {
					Optional<ConstraintNode> constraint = 
							fModelParserForConstraint.parseConstraint(child, targetCompositeParameterNode, errorList);

					if (constraint.isPresent()) {
						targetCompositeParameterNode.addConstraint(constraint.get());
					} else {
						errorList.add("Cannot parse constraint of parent structure: " + targetCompositeParameterNode.getName() + ".");
					}

				} catch (Exception e) {
					LogHelperCore.logError("A composite parameter could not be parsed: " + targetCompositeParameterNode.getName());
				}
			}
		}

		if (element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {

			String linkPath;

			try {
				linkPath = ModelParserHelper.getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME, errorList);
			} catch (Exception e) {
				errorList.add(e.getMessage());
				return Optional.empty();
			}

			AbstractParameterNode link = findLink(linkPath, parent);

			if (link != null) {
				targetCompositeParameterNode.setLinkToGlobalParameter((AbstractParameterNode) link);
			} else {
				errorList.add("Cannot find link for parameter: " + nameOfCompositeParameter);
			}
		}

		return Optional.ofNullable(targetCompositeParameterNode);
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
