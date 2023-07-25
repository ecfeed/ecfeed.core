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

import static com.ecfeed.core.model.serialization.SerializationConstants.ROOT_NODE_NAME;

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForRoot {

	IModelChangeRegistrator fModelChangeRegistrator;
	private int fModelVersion;

	public ModelParserForRoot(
			int modelVersion, 
			IModelChangeRegistrator modelChangeRegistrator) {

		fModelVersion = modelVersion;
		fModelChangeRegistrator = modelChangeRegistrator;
	}

	public RootNode parseRoot(
			Element element,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings outErrorList) {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), ROOT_NODE_NAME, outErrorList);
		String name = ModelParserHelper.getElementName(element, outErrorList);

		RootNode targetRootNode = new RootNode(name, fModelChangeRegistrator, fModelVersion);

		targetRootNode.setDescription(ModelParserHelper.parseComments(element));

		parseGlobalParametersOfRoot(element, targetRootNode, elementToNodeMapper, outErrorList);

		parseClasses(element, targetRootNode, elementToNodeMapper, outErrorList);

		return targetRootNode;
	}

	private void parseGlobalParametersOfRoot(
			Element element, 
			RootNode targetRootNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings outErrorList) {

		List<Element> parameterElements = 
				ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getParametersAndConstraintsElementNames());

		for (Element parameterElement : parameterElements) {

			parseOneGlobalParameter(parameterElement, targetRootNode, elementToNodeMapper, outErrorList);
		}
	}

	private void parseOneGlobalParameter(
			Element parameterElement, 
			RootNode targetRootNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings inOutErrorList) {

		boolean isBasicParameterElement = 
				ModelParserHelper.verifyElementName(
						parameterElement, SerializationHelperVersion1.getBasicParameterNodeName());

		if (isBasicParameterElement) {
			
			Optional<BasicParameterNode> globalBasicParameter = 
					ModelParserBasicForParameter.parseParameter(
							parameterElement, targetRootNode, targetRootNode.getModelChangeRegistrator(), inOutErrorList);

			if (globalBasicParameter.isPresent()) {
				targetRootNode.addParameter(globalBasicParameter.get());
			} else {
				inOutErrorList.add("Cannot parse parameter of root: " + targetRootNode.getName() + ".");
			}
			return;
		} 

		boolean isCompositeParameterElement = 
				ModelParserHelper.verifyElementName(
						parameterElement, SerializationHelperVersion1.getCompositeParameterNodeName());

		if (isCompositeParameterElement) {
			
			Optional<CompositeParameterNode> globalCompositeParameter = 
					ModelParserForCompositeParameter.parseParameterWithoutConstraints(
							parameterElement, targetRootNode, 
							targetRootNode.getModelChangeRegistrator(), elementToNodeMapper, inOutErrorList);

			if (globalCompositeParameter.isPresent()) {
				targetRootNode.addParameter(globalCompositeParameter.get());
			} else {
				inOutErrorList.add("Cannot parse structure of root: " + targetRootNode.getName() + ".");
			}
			
			ModelParserForParameterHelper.parseLocalAndChildConstraints(
					parameterElement, globalCompositeParameter.get(), elementToNodeMapper, inOutErrorList);

			return;
		}
	}

	private void parseClasses(
			Element element, 
			RootNode targetRootNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings outErrorList) {

		List<Element> childClassElements = 
				ModelParserHelper.getIterableChildren(element, SerializationConstants.CLASS_NODE_NAME);

		for (Element classElement : childClassElements) {

			ModelParserForClass.parseAndAddClass(classElement, targetRootNode, elementToNodeMapper, outErrorList);
		}
	}

}
