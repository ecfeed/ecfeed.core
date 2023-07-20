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
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForGlobalCompositeParameter { // XYX

	private ModelParserBasicForParameter fModelParserForParameter;

	public ModelParserForGlobalCompositeParameter(
			ModelParserBasicForParameter ModelParserForParameter) {

		fModelParserForParameter = ModelParserForParameter;
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

		// parse basic and composite parameters 

		for (Element child : children) {

			// 1
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

				// 2
				Optional<CompositeParameterNode> globalCompositeParameter = 
						parseGlobalCompositeParameter(
								child, parent, modelChangeRegistrator, errorList);

				if (globalCompositeParameter.isPresent()) {
					targetCompositeParameterNode.addParameter(globalCompositeParameter.get());
				} else {
					errorList.add("Cannot parse structure for parent structure: " + targetCompositeParameterNode.getName() + ".");
				}

			} 
		}

		ModelParserForParameterHelper.parseConstraints(children, targetCompositeParameterNode, errorList);
		
		return Optional.of(targetCompositeParameterNode);
	}

}
