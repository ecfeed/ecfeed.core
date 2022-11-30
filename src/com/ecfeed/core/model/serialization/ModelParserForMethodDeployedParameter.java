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

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.SignatureHelper;
import nu.xom.Element;

import java.util.Optional;

import static com.ecfeed.core.model.serialization.SerializationConstants.*;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;

public class ModelParserForMethodDeployedParameter implements IModelParserForMethodDeployedParameter {

	public Optional<BasicParameterNode> parseMethodDeployedParameter(Element element, MethodNode method, ListOfStrings errors) {
		Optional<BasicParameterNode> parameter = parseMethodBasicParameter(element, method, errors);

		if (!parameter.isPresent()) {
			ExceptionHelper.reportRuntimeException("The deployed parameter is non-existent.");
		}

		AbstractParameterNode parameterCandidate;
		String[] parameterCandidateSegments = parameter.get().getName().split(SignatureHelper.SIGNATURE_NAME_SEPARATOR);

		parameterCandidate = method.getParameter(method.getParameterIndex(parameterCandidateSegments[0]));
		parameterCandidate = MethodDeployer.getNestedBasicParameter(parameterCandidate, parameterCandidateSegments, 1);

		parameter.get().setDeploymentParameter((BasicParameterNode) parameterCandidate);

		return parameter;
	}

	public Optional<BasicParameterNode> parseMethodBasicParameter(Element element, MethodNode method, ListOfStrings errors) {
		String defaultValue = null;
		String name, type;
		boolean expected = false;

		try {
			ModelParserHelper.assertNodeTag(element.getQualifiedName(), SerializationHelperVersion1.getBasicParameterNodeName(), errors);
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

		if (element.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null && method.getClassNode() != null) {
			String linkPath;

			try {
				linkPath = ModelParserHelper.getAttributeValue(element, PARAMETER_LINK_ATTRIBUTE_NAME, errors);
			} catch (ParserException e) {
				return Optional.empty();
			}

			BasicParameterNode link = method.getClassNode().findGlobalParameter(linkPath);

			if (link != null) {
				parameter.setLinkToGlobalParameter(link);
			}
		}

		if (!parameter.isLinked()) {
			parameter.setTypeComments(ModelParserHelper.parseTypeComments(element));
		}

		return Optional.of(parameter);
	}
}
