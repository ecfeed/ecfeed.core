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

import static com.ecfeed.core.model.serialization.SerializationConstants.CLASS_NODE_NAME;

import java.util.Optional;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.BooleanHolder;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.StringHolder;

import nu.xom.Element;

public class ModelParserForClass implements IModelParserForClass {

	private IModelParserForGlobalParameter fModelParserForGlobalParameter;
	private IModelParserForGlobalCompositeParameter fModelParserForGlobalCompositeParameter;
	private IModelParserForMethod fModelParserForMethod;

	public ModelParserForClass(
			IModelParserForGlobalParameter modelParserForGlobalParameter,
			IModelParserForGlobalCompositeParameter modelParserForGlobalCompositeParameter,
			IModelParserForMethod modelParserForMethod) {
		
		fModelParserForGlobalParameter = modelParserForGlobalParameter;
		fModelParserForGlobalCompositeParameter = modelParserForGlobalCompositeParameter;
		fModelParserForMethod = modelParserForMethod;
	}
	
	public Optional<ClassNode> parseClass(
			Element classElement, RootNode parent, ListOfStrings errorList) throws ParserException {

		String name;
		
		try {
			ModelParserHelper.assertNodeTag(classElement.getQualifiedName(), CLASS_NODE_NAME, errorList);
			name = ModelParserHelper.getElementName(classElement, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		BooleanHolder runOnAndroidHolder = new BooleanHolder(false);
		StringHolder androidBaseRunnerHolder = new StringHolder(); 

		ClassNode targetClassNode = 
				new ClassNode(
						name, 
						parent.getModelChangeRegistrator(), 
						runOnAndroidHolder.get(), 
						androidBaseRunnerHolder.get());

		targetClassNode.setDescription(ModelParserHelper.parseComments(classElement));
		//we need to do it here, so the backward search for global parameters will work
		targetClassNode.setParent(parent);

		//parameters must be parsed before classes
		for (Element child : ModelParserHelper.getIterableChildren(classElement, new String[]
			{SerializationHelperVersion1.getBasicParameterNodeName(), SerializationHelperVersion1.getCompositeParameterNodeName()}
		)) {
			if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getBasicParameterNodeName())) {
				fModelParserForGlobalParameter.parseGlobalParameter(child, targetClassNode.getModelChangeRegistrator(), errorList)
						.ifPresent(targetClassNode::addParameter);
			} else if (ModelParserHelper.verifyNodeTag(child, SerializationHelperVersion1.getCompositeParameterNodeName())) {
				fModelParserForGlobalCompositeParameter.parseGlobalCompositeParameter(child, targetClassNode.getModelChangeRegistrator(), errorList)
						.ifPresent(targetClassNode::addParameter);
			}
		}

		for (Element child : ModelParserHelper.getIterableChildren(classElement, SerializationConstants.METHOD_NODE_NAME)) {
			Optional<MethodNode> node = 
					fModelParserForMethod.parseMethod(child, targetClassNode, errorList);
			
			if (node.isPresent()) {
				targetClassNode.addMethod(node.get());
			}
		}

		return Optional.ofNullable(targetClassNode);
	}

}
