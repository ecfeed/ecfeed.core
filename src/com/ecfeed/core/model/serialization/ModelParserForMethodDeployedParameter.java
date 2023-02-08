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
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForMethodDeployedParameter implements IModelParserForMethodDeployedParameter {

	public Optional<BasicParameterNode> parseMethodDeployedParameter(Element element, MethodNode method, ListOfStrings errors) {
		Optional<BasicParameterNode> parameter = parseMethodBasicParameter(element, method, errors);


		try {
		
			if (!parameter.isPresent()) {
				ExceptionHelper.reportRuntimeException("The deployed parameter is non-existent.");
			}
	
			if (parameter.get().isLinked() && parameter.get().getLinkToGlobalParameter() != null) {
				
				AbstractParameterNode candidate = parameter.get().getLinkToGlobalParameter();
				parameter.get().setDeploymentParameter((BasicParameterNode) candidate);
			} else {
		
				String candidateName = parameter.get().getQualifiedName();
				
				Optional<BasicParameterNode> candidate = method.getNestedBasicParameters(true).stream()
					.filter(e -> e.getQualifiedName().equals(candidateName))
					.findAny();
				
				if (candidate.isPresent()) {
					parameter.get().setDeploymentParameter(candidate.get());
				} else {
					System.out.println("The deployed parameter is corrupted. The main node could not be found - [" + candidateName + "].");
					return Optional.empty();
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return parameter;
	}

	public Optional<BasicParameterNode> parseMethodBasicParameter(Element element, MethodNode method, ListOfStrings errors) {
		String defaultValue = null;
		String name, type;
		boolean expected = false;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationHelperVersion1.getBasicParameterNodeName(), errors);
			name = ModelParserHelper.getElementName(element, errors);
			type = ModelParserHelper.getAttributeValue(element, TYPE_NAME_ATTRIBUTE, errors);

			if (element.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = Boolean.parseBoolean(ModelParserHelper.getAttributeValue(element, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, errors));
				defaultValue = ModelParserHelper.getAttributeValue(element, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, errors);
			}

		} catch (ParserException e) {
			return Optional.empty();
		}

		BasicParameterNode parameter = new BasicParameterNode("tmp", type, defaultValue, expected, method.getModelChangeRegistrator());
		parameter.setNameUnsafe(name);

		ModelParserHelper.parseParameterProperties(element, parameter);

		if (element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null) {

			try {
				String linkPath = ModelParserHelper.getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME, errors);
				
				method.getNestedBasicParameters(true).stream()
					.filter(e -> e.getQualifiedName().equals(linkPath))
					.findAny()
					.ifPresent(parameter::setLinkToGlobalParameter);
				
			} catch (ParserException e) {
				return Optional.empty();
			}
		}

		if (!parameter.isLinked()) {
			parameter.setTypeComments(ModelParserHelper.parseTypeComments(element));
		}

		return Optional.of(parameter);
	}
}
