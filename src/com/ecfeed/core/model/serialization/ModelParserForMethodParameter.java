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
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_IS_LINKED_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_NAME_ATTRIBUTE;

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForMethodParameter implements IModelParserForMethodParameter {

	public Optional<BasicParameterNode> parseMethodParameter(
			Element parameterElement, MethodNode method, IAbstractNode parent, ListOfStrings errorList) {

		String name, type;
		String defaultValue = null;
		String expected = String.valueOf(false);

		try {
			ModelParserHelper.assertNameEqualsExpectedName(parameterElement.getQualifiedName(), getParameterNodeName(), errorList);
			name = ModelParserHelper.getElementName(parameterElement, errorList);
			type = ModelParserHelper.getAttributeValue(parameterElement, TYPE_NAME_ATTRIBUTE, errorList);

			if (parameterElement.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = 
						ModelParserHelper.getAttributeValue(
								parameterElement, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, errorList);
			}

			if (parameterElement.getAttribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME) != null) {
				defaultValue =
						ModelParserHelper.getAttributeValue(
								parameterElement, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, errorList);
			}

		} catch (ParserException e) {
			return Optional.empty();
		}

		BasicParameterNode targetMethodParameterNode = 
				new BasicParameterNode(
						name, type, defaultValue, Boolean.parseBoolean(expected), method.getModelChangeRegistrator()
						);

		ModelParserHelper.parseParameterProperties(parameterElement, targetMethodParameterNode);

		if (parameterElement.getAttribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME) != null) {
			//			boolean linked ;
			//
			//			try {
			//				linked = 
			//						Boolean.parseBoolean(ModelParserHelper.getAttributeValue(
			//								parameterElement, PARAMETER_IS_LINKED_ATTRIBUTE_NAME, errorList));
			//
			//			} catch (ParserException e) {
			//				return Optional.empty();
			//			}
			//
			//targetMethodParameterNode.setLinked(linked);
		}

		ClassNode classNode = method.getClassNode();
		
		if (parameterElement.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null && classNode != null) {
			String linkPath;

			try {
				linkPath = 
						ModelParserHelper.getAttributeValue(
								parameterElement, PARAMETER_LINK_ATTRIBUTE_NAME, errorList);
				
			} catch (ParserException e) {
				return Optional.empty();
			}
			
			AbstractParameterNode link = findGlobalParameter(classNode, linkPath);

			if (link != null) {
				targetMethodParameterNode.setLinkToGlobalParameter(link);
			} else {
				// targetMethodParameterNode.setLinked(false);
			}
		} else {
			// targetMethodParameterNode.setLinked(false);
		}

		ModelParserForChoice modelParserForChoice = 
				new ModelParserForChoice(targetMethodParameterNode.getModelChangeRegistrator());

		String choiceNodeName = SerializationHelperVersion1.getChoiceNodeName();
		
		List<Element> children = 
				ModelParserHelper.getIterableChildren(
						parameterElement, choiceNodeName);

		for (Element child : children) {
			Optional<ChoiceNode> node = modelParserForChoice.parseChoice(child, errorList);
			if (node.isPresent()) {
				targetMethodParameterNode.addChoice(node.get());
			}
		}

		targetMethodParameterNode.setDescription(ModelParserHelper.parseComments(parameterElement));

		if (!targetMethodParameterNode.isLinked()) {
			targetMethodParameterNode.setTypeComments(ModelParserHelper.parseTypeComments(parameterElement));
		}

		return Optional.ofNullable(targetMethodParameterNode);
	}

	private String getParameterNodeName() {
		return SerializationHelperVersion1.getBasicParameterNodeName();
	}

	private static BasicParameterNode findGlobalParameter(ClassNode classNode, String qualifiedName) {
		
		String qualifiedNameNewStandard = createQualifiedNameNewStandard(qualifiedName);

		List<BasicParameterNode> globalParameters = classNode.getAllGlobalParametersAvailableForLinking();

		for (BasicParameterNode parameter : globalParameters) {

			String currentQualifiedNameNew = 
					AbstractParameterSignatureHelper.createSignatureToTopContainerNewStandard(
							parameter, new ExtLanguageManagerForJava());

			if (currentQualifiedNameNew.equals(qualifiedNameNewStandard)) {
				return parameter;
			}
		}

		return null;
	}

	private static String createQualifiedNameNewStandard(String currentQualifiedInOldStandard) {
		
		String qualifiedNameNewStandard = currentQualifiedInOldStandard.replace(":", ".");
		
		return qualifiedNameNewStandard;
	}
 

}
