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

public class ModelParserForRoot implements IModelParserForRoot {

	IModelChangeRegistrator fModelChangeRegistrator;
	private int fModelVersion;
	private IModelParserForGlobalParameter fModelParserForGlobalParameter;
	private IModelParserForGlobalCompositeParameter fModelParserForGlobalCompositeParameter;
	private IModelParserForClass fModelParserForClass;

	public ModelParserForRoot(
			int modelVersion, 
			IModelParserForGlobalParameter modelParserForGlobalParameter,
			IModelParserForGlobalCompositeParameter modelParserForGlobalCompositeParameter,
			IModelParserForClass modelParserForClass,
			IModelChangeRegistrator modelChangeRegistrator) {

		fModelVersion = modelVersion;
		fModelParserForGlobalParameter = modelParserForGlobalParameter;
		fModelParserForGlobalCompositeParameter = modelParserForGlobalCompositeParameter;
		fModelParserForClass = modelParserForClass;
		fModelChangeRegistrator = modelChangeRegistrator;
	}

	public RootNode parseRoot(Element element, ListOfStrings outErrorList) throws ParserException {

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
				ModelParserHelper.getIterableChildren(element, SerializationHelperVersion1.getParameterNodeNames());
		
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
					fModelParserForGlobalParameter.parseGlobalBasicParameter(
							parameterElement, targetRootNode.getModelChangeRegistrator(), outErrorList);
			
			globalBasicParameter.ifPresent(targetRootNode::addParameter);
			return;
		} 
		
		boolean isCompositeParameterElement = 
				ModelParserHelper.verifyElementName(
						parameterElement, SerializationHelperVersion1.getCompositeParameterNodeName());
		
		if (isCompositeParameterElement) {
			Optional<CompositeParameterNode> globalCompositeParameter = 
					fModelParserForGlobalCompositeParameter.parseGlobalCompositeParameter(
							parameterElement, targetRootNode.getModelChangeRegistrator(), outErrorList);
			
			globalCompositeParameter.ifPresent(targetRootNode::addParameter);
			return;
		}
	}

	private void parseClasses(
			Element element, RootNode targetRootNode, ListOfStrings outErrorList)
			throws ParserException {
		
		List<Element> childClassElements = 
				ModelParserHelper.getIterableChildren(element, SerializationConstants.CLASS_NODE_NAME);
		
		for (Element classElement : childClassElements) {
			
			Optional<ClassNode> node = fModelParserForClass.parseClass(classElement, targetRootNode, outErrorList);
			
			if (node.isPresent()) {
				targetRootNode.addClass(node.get());
			}
		}
	}

}
