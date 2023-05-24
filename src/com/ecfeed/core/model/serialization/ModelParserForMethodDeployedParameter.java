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

import java.util.Optional;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.SignatureHelper;

import nu.xom.Element;

public class ModelParserForMethodDeployedParameter implements IModelParserForMethodDeployedParameter { // TODO MO-RE do we need interface ?

	@Override
	public Optional<ParameterWithLinkingContext> parseMethodDeployedParameter(
			Element parameterElement, 
			MethodNode methodNode,
			ListOfStrings errorList) {

		AbstractParameterNode parameter = parseDeployedNode(
				parameterElement, SerializationConstants.METHOD_DEPLOYED_PATH_OF_PARAMETER, methodNode, errorList);

		AbstractParameterNode linkingContext = parseDeployedNode(
				parameterElement, SerializationConstants.METHOD_DEPLOYED_PATH_OF_CONTEXT, methodNode, errorList);

		if (parameter == null && linkingContext == null) {
			return Optional.empty();
		}

		ParameterWithLinkingContext parameterWithLinkingContext = new ParameterWithLinkingContext(parameter, linkingContext);

		return Optional.of(parameterWithLinkingContext);
	}

	private AbstractParameterNode parseDeployedNode(
			Element parameterElement,
			String attributeName,
			MethodNode methodNode,
			ListOfStrings errorList) {

		String path = "";

		path = parameterElement.getAttributeValue(attributeName);

		if (path == null) {
			return null;
		}

		AbstractParameterNode foundParameter = AbstractParameterNodeHelper.findParameter(path, methodNode);

		if (foundParameter == null) {
			errorList.add("Original parameter not found by path: " + path);
			return null;
		}

		return foundParameter;
	}

	//	public Optional<BasicParameterNode> parseMethodDeployedParameter(
	//			Element element, MethodNode method, ListOfStrings errors) {
	//		
	//		Optional<BasicParameterNode> parameter = 
	//				parseMethodBasicParameter(element, method, errors);
	//
	//		try {
	//			if (!parameter.isPresent()) {
	//
	//				ExceptionHelper.reportRuntimeException("The deployed parameter is non-existent.");
	//			}
	//
	//			if (parameter.get().isLinked() && parameter.get().getLinkToGlobalParameter() != null) {
	//
	//				AbstractParameterNode candidate = parameter.get().getLinkToGlobalParameter();
	//				parameter.get().setDeploymentParameter((BasicParameterNode) candidate);
	//			} else {
	//
	//				String candidateName = AbstractParameterSignatureHelper.getQualifiedName(parameter.get());
	//
	//				Optional<BasicParameterNode> candidate = method.getNestedBasicParameters(true).stream()
	//						.filter(e -> AbstractParameterSignatureHelper.getQualifiedName(e).equals(candidateName))
	//						.findAny();
	//
	//				if (candidate.isPresent()) {
	//
	//					parameter.get().setDeploymentParameter(candidate.get()); //XXY
	//				} else {
	//
	//					System.out.println("The deployed parameter is corrupted. The main node could not be found - [" + candidateName + "].");
	//					return Optional.empty();
	//				}
	//			}
	//
	//		} catch(Exception e) {
	//
	//			ExceptionHelper.reportRuntimeException("The deployed parameter could not be parsed.");
	//			return Optional.empty();
	//		}
	//
	//		return parameter;
	//	}
	//
	public Optional<BasicParameterNode> parseMethodBasicParameter(
			Element element, MethodNode method, ListOfStrings outErrorList) {

		String defaultValue = null;
		String name, type;
		boolean expected = false;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(
					element.getQualifiedName(), 
					SerializationHelperVersion1.getBasicParameterNodeName(), outErrorList);

			name = ModelParserHelper.getElementName(element, outErrorList);
			type = ModelParserHelper.getAttributeValue(element, TYPE_NAME_ATTRIBUTE, outErrorList);

			if (element.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = 
						Boolean.parseBoolean(ModelParserHelper.getAttributeValue(
								element, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, outErrorList));
			}

			if (element.getAttribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME) != null) {
				defaultValue = 
						ModelParserHelper.getAttributeValue(element, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, outErrorList);
			}

		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return Optional.empty();
		}

		String lastSegment = getLastSegment(name);

		BasicParameterNode parameter = 
				new BasicParameterNode(
						lastSegment, type, defaultValue, expected, method.getModelChangeRegistrator());

		ModelParserHelper.parseParameterProperties(element, parameter);

		if (element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {

			try {
				String linkPath = ModelParserHelper.getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME, outErrorList);

//				Optional<BasicParameterNode> basicParameterNode = 
//						method.getNestedBasicParameters(true).stream()
//						.filter(e -> AbstractParameterSignatureHelper.getQualifiedName(e).equals(linkPath))
//						.findAny(); //use function from helper 
				
				AbstractParameterNode basicParameterNode = AbstractParameterNodeHelper.findParameter(linkPath, method);

				if (basicParameterNode != null) {
					parameter.setLinkToGlobalParameter(basicParameterNode);
				} else {
					outErrorList.add("Cannot parse link of parameter: " + parameter.getName() + ".");
				}

			} catch (Exception e) {
				outErrorList.add(e.getMessage());
				return Optional.empty();
			}
		}

		if (!parameter.isLinked()) {
			parameter.setTypeComments(ModelParserHelper.parseTypeComments(element));
		}

		return Optional.of(parameter);
	}

	private String getLastSegment(String name) { // TODO MO-RE move to 

		int index = name.lastIndexOf(SignatureHelper.SIGNATURE_NAME_SEPARATOR);

		if (index == -1) {
			return name;
		}

		String lastSegment = name.substring(index+1);
		return lastSegment;
	}

}
