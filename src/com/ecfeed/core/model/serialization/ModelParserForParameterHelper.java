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
import java.util.Optional;

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
			errorList.add(e.getMessage());
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
			errorList.add(e.getMessage());
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

		ModelParserForChoice modelParserForChoice = 
				new ModelParserForChoice(modelChangeRegistrator);

		List<Element> children = 
				ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getChoiceNodeName());

		for (Element child : children) {

			Optional<ChoiceNode> node = modelParserForChoice.parseChoice(child, errorList);

			if (node.isPresent()) {
				targetGlobalParameterNode.addChoice(node.get());
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
			outErrorList.add(e.getMessage());
			return;
		}

		AbstractParameterNode link = findLink(linkPath, parametersParentNode);

		if (link != null) {
			targetMethodParameterNode.setLinkToGlobalParameter(link);
		}
	}

	private static AbstractParameterNode findLink(String linkPath, IParametersParentNode parametersParentNode) {

		if (linkPath.startsWith(SignatureHelper.SIGNATURE_ROOT_MARKER)) {
			return AbstractParameterNodeHelper.findParameter(linkPath, parametersParentNode);
		}

		// old convention - to be removed in next release when all models would be converted to new convention
		return ((MethodNode)parametersParentNode).getClassNode().findGlobalParameter(linkPath); 
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
					Optional<ConstraintNode> constraint = 
							new ModelParserForConstraint().parseConstraint(child, constraintsParentNode, errorList);

					if (constraint.isPresent()) {
						constraintsParentNode.addConstraint(constraint.get());
					} else {
						errorList.add("Cannot parse constraint of parent structure: " + constraintsParentNode.getName() + ".");
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
			IParametersParentNode targetMethodNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) {

		List<Element> parameterElements =
				ModelParserHelper.getIterableChildren(
						methodElement, SerializationHelperVersion1.getParametersElementNames());

		for (Element parameterElement : parameterElements) {

			parseConditionallyParameterElementWithChildParameters(
					parameterElement, targetMethodNode, elementToNodeMapper, inOutErrorList);
		}
	}

	private static void parseConditionallyParameterElementWithChildParameters(
			Element parameterElement, 
			IParametersParentNode targetMethodNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) {

		String basicParameterElementName = SerializationHelperVersion1.getBasicParameterNodeName();

		if (ModelParserHelper.verifyElementName(parameterElement, basicParameterElementName)) {

			Optional<BasicParameterNode> basicParameterNode = 
					new ModelParserBasicForParameter().parseParameter(
							parameterElement, targetMethodNode, targetMethodNode.getModelChangeRegistrator(), inOutErrorList);

			elementToNodeMapper.addMappings(parameterElement, basicParameterNode.get());

			if (basicParameterNode.isPresent()) {
				targetMethodNode.addParameter(basicParameterNode.get());
			} else {
				inOutErrorList.add("Cannot parse parameter for method: " + targetMethodNode.getName() + ".");
			}

			return;
		} 

		String compositeParameterElementName = SerializationHelperVersion1.getCompositeParameterNodeName();

		if (ModelParserHelper.verifyElementName(parameterElement, compositeParameterElementName)) {

			Optional<CompositeParameterNode> compositeParameterNode = 
					ModelParserForCompositeParameter.parseParameterWithoutConstraints(
							parameterElement, targetMethodNode, 
							targetMethodNode.getModelChangeRegistrator(), elementToNodeMapper, inOutErrorList);

			elementToNodeMapper.addMappings(parameterElement, compositeParameterNode.get());

			if (compositeParameterNode.isPresent()) {
				targetMethodNode.addParameter(compositeParameterNode.get());
			} else {
				inOutErrorList.add("Cannot parse structure for method: " + targetMethodNode.getName() + ".");
			}

			return;
		}

		inOutErrorList.add("Invalid type of parameter element.");
	}

}
