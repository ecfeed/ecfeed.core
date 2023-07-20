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

	private ModelParserForParameter fModelParserForMethodParameter;
	private ModelParserForConstraint fModelParserForConstraint;

	public ModelParserForMethodCompositeParameter(
			ModelParserForParameter modelParserForMethodParameter,
			ModelParserForConstraint modelParserForConstraint) {
		
		fModelParserForMethodParameter = modelParserForMethodParameter;
		fModelParserForConstraint = modelParserForConstraint;
	}

	public Optional<CompositeParameterNode> parseMethodCompositeParameter(
			Element element,
			MethodNode method,
			IParametersParentNode parent,
			ListOfStrings errorList) {

		CompositeParameterNode targetCompositeParameterNode = 
				createCompositeParameter(element, method, parent, errorList);

		List<Element> children = 
				ModelParserHelper.getIterableChildren(
						element, SerializationHelperVersion1.getParametersAndConstraintsElementNames());

		parseParameters(children, targetCompositeParameterNode, method, errorList);

		parseConstraints(children, targetCompositeParameterNode, errorList);

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

	private CompositeParameterNode createCompositeParameter(
			Element element, MethodNode method,
			IParametersParentNode parent, ListOfStrings errorList) {
		
		String nameOfCompositeParameter;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationHelperVersion1.getCompositeParameterNodeName(), errorList);
			nameOfCompositeParameter = ModelParserHelper.getElementName(element, errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return null;
		}

		CompositeParameterNode targetCompositeParameterNode = 
				new CompositeParameterNode(nameOfCompositeParameter, method.getModelChangeRegistrator());
		
		targetCompositeParameterNode.setParent(parent);
		
		return targetCompositeParameterNode;
	}

	private void parseConstraints(List<Element> children, CompositeParameterNode targetCompositeParameterNode,
			ListOfStrings errorList) {
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
