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

import static com.ecfeed.core.model.serialization.SerializationConstants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_NAME_ATTRIBUTE;

import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.IParametersAndConstraintsParentNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.SignatureHelper;

import nu.xom.Element;

public class ModelParserForParameterHelper {

	public static BasicParameterNode createBasicParameter(
			Element element, 
			String parameterNodeName,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

		String name, type;
		String defaultValue = null;
		String expected = String.valueOf(false);

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), parameterNodeName, errorList);
			name = ModelParserHelper.getElementName(element, errorList);
			type = ModelParserHelper.getAttributeValue(element, TYPE_NAME_ATTRIBUTE, errorList);

			if (element.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = ModelParserHelper.getAttributeValue(element, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, errorList);
			}

			if (element.getAttribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME) != null) {
				defaultValue = ModelParserHelper.getAttributeValue(element, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, errorList);
			}
		} catch (Exception e) {
			errorList.addIfUnique(e.getMessage());
			return null;
		}

		BasicParameterNode targetGlobalParameterNode = 
				new BasicParameterNode(name, type, defaultValue, Boolean.parseBoolean(expected), modelChangeRegistrator);

		return targetGlobalParameterNode;
	}

	public static CompositeParameterNode createCompositeParameter(
			Element element, 
			IParametersParentNode parent,
			IModelChangeRegistrator modelChangeRegistrator, 
			ListOfStrings errorList) {

		String nameOfCompositeParameter;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationHelperVersion1.getCompositeParameterNodeName(), errorList);
			nameOfCompositeParameter = ModelParserHelper.getElementName(element, errorList);
		} catch (Exception e) {
			errorList.addIfUnique(e.getMessage());
			return null;
		}

		CompositeParameterNode targetCompositeParameterNode = 
				new CompositeParameterNode(nameOfCompositeParameter, modelChangeRegistrator);

		targetCompositeParameterNode.setParent(parent);

		return targetCompositeParameterNode;
	}

	public static void parseChoices(
			Element element, 
			BasicParameterNode targetGlobalParameterNode, 
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

		List<Element> children = 
				ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getChoiceNodeName());

		for (Element child : children) {

			ChoiceNode node = ModelParserForChoice.parseChoice(child, modelChangeRegistrator, errorList);

			if (node != null) {
				targetGlobalParameterNode.addChoice(node);
			}
		}
	}

	public static void setLink(
			Element parameterElement, 
			IParametersParentNode parametersParentNode, 
			BasicParameterNode targetMethodParameterNode, 
			ListOfStrings outErrorList) {

		String linkPath;

		try {
			linkPath = 
					ModelParserHelper.getAttributeValue(
							parameterElement, PARAMETER_LINK_ATTRIBUTE_NAME, outErrorList);

		} catch (Exception e) {
			outErrorList.addIfUnique(e.getMessage());
			return;
		}

		AbstractParameterNode link = findLink(linkPath, parametersParentNode);

		if (link != null) {
			targetMethodParameterNode.setLinkToGlobalParameter(link);
		}
	}

	public static AbstractParameterNode findLink(String linkPath, IParametersParentNode parametersParentNode) {

		if (linkPath.startsWith(SignatureHelper.SIGNATURE_ROOT_MARKER)) {
			return AbstractParameterNodeHelper.findParameter(linkPath, parametersParentNode);
		}

		// old convention - to be removed in next release when all models would be converted to new convention
		return ((MethodNode)parametersParentNode).getClassNode().findGlobalParameter(linkPath); 
	}

	public static AbstractParameterNode findLink2(String linkPath, IParametersParentNode parametersParentNode) { // TODO REMOVE ? 

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


	public static void parseLocalConstraints(
			Element element, 
			IParametersAndConstraintsParentNode targetNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) {

		List<Element> constraintElements = 
				ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_NODE_NAME);

		ModelParserForParameterHelper.parseConstraints(constraintElements, targetNode, inOutErrorList);
	}

	private static void parseConstraints(
			List<Element> children, 
			IParametersAndConstraintsParentNode constraintsParentNode,
			ListOfStrings errorList) {

		for (Element child : children) {

			if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getConstraintName())) {

				try {
					ConstraintNode constraint = 
							ModelParserForConstraint.parseConstraint(child, constraintsParentNode, errorList);

					if (constraint != null) {
						constraintsParentNode.addConstraint(constraint);
					} else {
						errorList.addIfUnique("Cannot parse constraint of parent structure: " + constraintsParentNode.getName() + ".");
					}

				} catch (Exception e) {
					LogHelperCore.logError("A composite parameter could not be parsed: " + constraintsParentNode.getName());
				}
			}
		}
	}

	public static void parseLocalAndChildConstraints(
			Element methodElement, 
			IParametersAndConstraintsParentNode targetMethodNode, 
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) {

		ModelParserForParameterHelper.parseLocalConstraints(
				methodElement, targetMethodNode, elementToNodeMapper, inOutErrorList);

		parseConstraintsOfChildComposites(methodElement, targetMethodNode, elementToNodeMapper, inOutErrorList);
	}

	private static void parseConstraintsOfChildComposites(
			Element element,
			IParametersAndConstraintsParentNode parentNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) { 

		List<Element> childCompositeParameterElements =
				ModelParserHelper.getIterableChildren(
						element, SerializationHelperVersion1.getCompositeParameterNodeName());

		for (Element childCompositeParameterElement : childCompositeParameterElements) {

			IParametersAndConstraintsParentNode childAbstractNode = 
					(IParametersAndConstraintsParentNode) elementToNodeMapper.getNode(childCompositeParameterElement);

			ModelParserForParameterHelper.parseLocalConstraints(
					childCompositeParameterElement, childAbstractNode, elementToNodeMapper, inOutErrorList);

			CompositeParameterNode childCompositeParameterNode = 
					(CompositeParameterNode) elementToNodeMapper.getNode(childCompositeParameterElement); 

			parseConstraintsOfChildComposites(
					childCompositeParameterElement, childCompositeParameterNode, elementToNodeMapper, inOutErrorList);
		}
	}

	public static void parseLocalAndChildParametersWithoutConstraints(
			Element methodElement, 
			IParametersParentNode parametersParentNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) {

		List<Element> parameterElements =
				ModelParserHelper.getIterableChildren(
						methodElement, SerializationHelperVersion1.getParametersElementNames());

		for (Element parameterElement : parameterElements) {

			parseConditionallyParameterElementWithChildParameters(
					parameterElement, parametersParentNode, elementToNodeMapper, inOutErrorList);
		}
	}

	private static void parseConditionallyParameterElementWithChildParameters(
			Element parameterElement, 
			IParametersParentNode parametersParentNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) {

		String basicParameterElementName = SerializationHelperVersion1.getBasicParameterNodeName();

		if (ModelParserHelper.verifyElementName(parameterElement, basicParameterElementName)) {

			BasicParameterNode basicParameterNode = 
					ModelParserBasicForParameter.parseParameter(
							parameterElement, parametersParentNode, parametersParentNode.getModelChangeRegistrator(), inOutErrorList);

			if (basicParameterNode != null) {
				elementToNodeMapper.addMappings(parameterElement, basicParameterNode);
				parametersParentNode.addParameter(basicParameterNode);
			} else {
				inOutErrorList.addIfUnique("Cannot parse parameter for method: " + parametersParentNode.getName() + ".");
			}

			return;
		} 

		String compositeParameterElementName = SerializationHelperVersion1.getCompositeParameterNodeName();

		if (ModelParserHelper.verifyElementName(parameterElement, compositeParameterElementName)) {

			CompositeParameterNode compositeParameterNode = 
					ModelParserForCompositeParameter.parseParameterWithoutConstraints(
							parameterElement, parametersParentNode, 
							parametersParentNode.getModelChangeRegistrator(), elementToNodeMapper, inOutErrorList);

			if (compositeParameterNode != null) {
				elementToNodeMapper.addMappings(parameterElement, compositeParameterNode);
				parametersParentNode.addParameter(compositeParameterNode);
			} else {
				inOutErrorList.addIfUnique("Cannot parse structure for method: " + parametersParentNode.getName() + ".");
			}

			return;
		}

		inOutErrorList.addIfUnique("Invalid type of parameter element.");
	}

}
