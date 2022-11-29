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

public class ModelParserForMethodDeployedParameter implements IModelParserForMethodDeployedParameter {

	private IModelParserForMethodParameter fModelParserForMethodParameter;

	public ModelParserForMethodDeployedParameter(IModelParserForMethodParameter modelParserForMethodParameter) {
		fModelParserForMethodParameter = modelParserForMethodParameter;
	}

	public Optional<BasicParameterNode> parseMethodDeployedParameter(Element parameterElement, MethodNode method, ListOfStrings errorList) {
		Optional<BasicParameterNode> parameter = fModelParserForMethodParameter.parseMethodParameter(parameterElement, method, errorList);

		if (!parameter.isPresent()) {
			ExceptionHelper.reportRuntimeException("The deployed parameter is non-existent.");
		}

		AbstractParameterNode parameterCandidate;
		String[] parameterCandidateSegments = parameter.get().getName().split(SignatureHelper.SIGNATURE_NAME_SEPARATOR);

		parameterCandidate = method.getParameter(method.getParameterIndex(parameterCandidateSegments[0]));
		parameterCandidate = getNestedParameter(parameterCandidate, parameterCandidateSegments, 1);

		parameter.get().setDeploymentParameter((BasicParameterNode) parameterCandidate);

		getChoices(parameter.get(), (BasicParameterNode) parameterCandidate);

		return Optional.of(parameter.get());
	}

	private BasicParameterNode getNestedParameter(AbstractParameterNode parameter, String[] path, int index) {

		if (parameter instanceof BasicParameterNode) {
			return (BasicParameterNode) parameter;
		}

		CompositeParameterNode element = (CompositeParameterNode) parameter;
		AbstractParameterNode elementNested = element.getParameter(element.getParameterIndex(path[index]));

		return getNestedParameter(elementNested, path, index + 1);
	}

	private void getChoices(BasicParameterNode parameterReference, BasicParameterNode parameterTarget) {

		for (ChoiceNode choiceReference : parameterReference.getChoices()) {
			ChoiceNode choiceTarget = parameterTarget.getChoice(choiceReference.getQualifiedName());

			if (choiceTarget == null) {
				continue;
			}

			getAbstractChoices(choiceReference, choiceTarget);
		}
	}

	private void getAbstractChoices(ChoiceNode choiceReference, ChoiceNode choiceTarget) {

		if (choiceReference.isAbstract()) {
			for (ChoiceNode choiceAbstractReference : choiceReference.getChoices()) {
				ChoiceNode choiceAbstractTarget = choiceTarget.getChoice(choiceAbstractReference.getQualifiedName());

				if (choiceAbstractTarget == null) {
					continue;
				}

				getAbstractChoices(choiceAbstractReference, choiceAbstractTarget);
			}
		} else {
			choiceReference.setDeploymentChoiceNode(choiceTarget);
		}
	}
}
