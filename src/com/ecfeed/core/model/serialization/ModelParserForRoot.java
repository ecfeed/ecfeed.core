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
	private ModelParserBasicForParameter fModelParserForParameter;
	private ModelParserForGlobalCompositeParameter fModelParserForGlobalCompositeParameter;
	private ModelParserForClass fModelParserForClass;

	public ModelParserForRoot(
			int modelVersion, 
			ModelParserBasicForParameter ModelParserForParameter,
			ModelParserForGlobalCompositeParameter modelParserForGlobalCompositeParameter,
			ModelParserForClass modelParserForClass,
			IModelChangeRegistrator modelChangeRegistrator) {

		fModelVersion = modelVersion;
		fModelParserForParameter = ModelParserForParameter;
		fModelParserForGlobalCompositeParameter = modelParserForGlobalCompositeParameter;
		fModelParserForClass = modelParserForClass;
		fModelChangeRegistrator = modelChangeRegistrator;
	}

	public RootNode parseRoot(Element element, ListOfStrings outErrorList) {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), ROOT_NODE_NAME, outErrorList);
		String name = ModelParserHelper.getElementName(element, outErrorList);

		RootNode targetRootNode = new RootNode(name, fModelChangeRegistrator, fModelVersion);

		targetRootNode.setDescription(ModelParserHelper.parseComments(element));

		parseGlobalParametersOfRoot(element, targetRootNode, outErrorList);

		parseClasses(element, targetRootNode, outErrorList);

		return targetRootNode;
	}

	private void parseGlobalParametersOfRoot(Element element, RootNode targetRootNode, ListOfStrings outErrorList) {

		List<Element> parameterElements = 
				ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getParametersAndConstraintsElementNames());

		for (Element parameterElement : parameterElements) {

			parseOneGlobalParameter(parameterElement, targetRootNode, outErrorList);
		}
	}

	private void parseOneGlobalParameter(
			Element parameterElement, 
			RootNode targetRootNode,
			ListOfStrings outErrorList) {

		boolean isBasicParameterElement = 
				ModelParserHelper.verifyElementName(
						parameterElement, SerializationHelperVersion1.getBasicParameterNodeName());

		if (isBasicParameterElement) {
			Optional<BasicParameterNode> globalBasicParameter = 
					fModelParserForParameter.parseParameter(
							parameterElement, targetRootNode, targetRootNode.getModelChangeRegistrator(), outErrorList);

			if (globalBasicParameter.isPresent()) {
				targetRootNode.addParameter(globalBasicParameter.get());
			} else {
				outErrorList.add("Cannot parse parameter of root: " + targetRootNode.getName() + ".");
			}
			return;
		} 

		boolean isCompositeParameterElement = 
				ModelParserHelper.verifyElementName(
						parameterElement, SerializationHelperVersion1.getCompositeParameterNodeName());

		if (isCompositeParameterElement) {
			Optional<CompositeParameterNode> globalCompositeParameter = 
					fModelParserForGlobalCompositeParameter.parseGlobalCompositeParameter(
							parameterElement, targetRootNode.getModelChangeRegistrator(), outErrorList);

			if (globalCompositeParameter.isPresent()) {
				targetRootNode.addParameter(globalCompositeParameter.get());
			} else {
				outErrorList.add("Cannot parse structure of root: " + targetRootNode.getName() + ".");
			}
			return;
		}
	}

	private void parseClasses(Element element, RootNode targetRootNode, ListOfStrings outErrorList) {

		List<Element> childClassElements = 
				ModelParserHelper.getIterableChildren(element, SerializationConstants.CLASS_NODE_NAME);

		for (Element classElement : childClassElements) {

			fModelParserForClass.parseAndAddClass(classElement, targetRootNode, outErrorList);
		}
	}

}
