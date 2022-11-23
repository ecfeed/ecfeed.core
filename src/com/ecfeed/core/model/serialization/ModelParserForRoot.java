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

	public RootNode parseRoot(
			Element element, ListOfStrings outErrorList) throws ParserException {

		ModelParserHelper.assertNodeTag(element.getQualifiedName(), ROOT_NODE_NAME, outErrorList);
		String name = ModelParserHelper.getElementName(element, outErrorList);

		RootNode targetRootNode = new RootNode(name, fModelChangeRegistrator, fModelVersion);

		targetRootNode.setDescription(ModelParserHelper.parseComments(element));

		for (Element child : ModelParserHelper.getIterableChildren(element, new String[]
				{SerializationHelperVersion1.getBasicParameterNodeName(), SerializationHelperVersion1.getCompositeParameterNodeName()}
		)) {

			Optional<BasicParameterNode> parameterBasic = fModelParserForGlobalParameter.parseGlobalParameter(child, targetRootNode.getModelChangeRegistrator(), outErrorList);
			if (parameterBasic.isPresent()) {
				targetRootNode.addParameter(parameterBasic.get());
				continue;
			}

			Optional<CompositeParameterNode> parameterComposite = fModelParserForGlobalCompositeParameter.parseGlobalCompositeParameter(child, targetRootNode.getModelChangeRegistrator(), outErrorList);
			if (parameterComposite.isPresent()) {
				targetRootNode.addParameter(parameterComposite.get());
			}
		}

		for (Element child : ModelParserHelper.getIterableChildren(element, SerializationConstants.CLASS_NODE_NAME)) {
			Optional<ClassNode> node = fModelParserForClass.parseClass(child, targetRootNode, outErrorList);
			if (node.isPresent()) {
				targetRootNode.addClass(node.get());
			}
		}

		return targetRootNode;
	}

}
