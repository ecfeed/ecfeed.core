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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.utils.ListOfStrings;

import com.ecfeed.core.utils.LogHelperCore;
import nu.xom.Element;

public class ModelParserForGlobalCompositeParameter { // XYX

	private ModelParserBasicForParameter fModelParserForParameter;
	private ModelParserForConstraint fModelParserForConstraint;

	public ModelParserForGlobalCompositeParameter(
			ModelParserBasicForParameter ModelParserForParameter,
			ModelParserForConstraint modelParserForConstraint) {

		fModelParserForParameter = ModelParserForParameter;
		fModelParserForConstraint = modelParserForConstraint;
	}

	public Optional<CompositeParameterNode> parseGlobalCompositeParameter(
			Element element,
			IParametersParentNode parent,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

		CompositeParameterNode targetCompositeParameterNode = 
				ModelParserForParameterHelper.createCompositeParameter(
						element, parent, modelChangeRegistrator, errorList);

		String[] parametersAndConstraintsElementNames = 
				SerializationHelperVersion1.getParametersAndConstraintsElementNames();

		List<Element> children = ModelParserHelper.getIterableChildren(element, parametersAndConstraintsElementNames);

		for (Element child : children) {

			if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getBasicParameterNodeName())) {

				Optional<BasicParameterNode> globalBasicParameter = 
						fModelParserForParameter.parseParameter(
								child, null, targetCompositeParameterNode.getModelChangeRegistrator(), errorList);

				if (globalBasicParameter.isPresent()) {
					targetCompositeParameterNode.addParameter(globalBasicParameter.get());
				} else {
					errorList.add("Cannot parse for parent structure: " + targetCompositeParameterNode.getName() + ".");
				}

			} else if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {

				Optional<CompositeParameterNode> globalCompositeParameter = 
						parseGlobalCompositeParameter(
								child, parent, modelChangeRegistrator, errorList);

				if (globalCompositeParameter.isPresent()) {
					targetCompositeParameterNode.addParameter(globalCompositeParameter.get());
				} else {
					errorList.add("Cannot parse structure for parent structure: " + targetCompositeParameterNode.getName() + ".");
				}

			} else if (ModelParserHelper.verifyElementName(child, SerializationHelperVersion1.getConstraintName())) {

				try {
					Optional<ConstraintNode> constraint = 
							fModelParserForConstraint.parseConstraint(child, targetCompositeParameterNode, errorList);

					if (constraint.isPresent()) {
						targetCompositeParameterNode.addConstraint(constraint.get());
					} else {
						errorList.add("Cannot parse constraint for parent structure: " + targetCompositeParameterNode.getName() + ".");
					}

				} catch (Exception e) {
					LogHelperCore.logError("A composite parameter could not be parsed: " + targetCompositeParameterNode.getName());
				}
			}
		}

		return Optional.of(targetCompositeParameterNode);
	}

}
