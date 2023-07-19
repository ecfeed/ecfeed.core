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

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForClass {

	private ModelParserForGlobalParameter fModelParserForGlobalParameter;
	private IModelParserForGlobalCompositeParameter fModelParserForGlobalCompositeParameter;
	private IModelParserForMethod fModelParserForMethod;

	public ModelParserForClass(
			ModelParserForGlobalParameter modelParserForGlobalParameter,
			IModelParserForGlobalCompositeParameter modelParserForGlobalCompositeParameter,
			IModelParserForMethod modelParserForMethod) {

		fModelParserForGlobalParameter = modelParserForGlobalParameter;
		fModelParserForGlobalCompositeParameter = modelParserForGlobalCompositeParameter;
		fModelParserForMethod = modelParserForMethod;
	}

	public Optional<ClassNode> parseAndAddClass(
			Element classElement, RootNode rootNode, ListOfStrings errorList) {

		String name;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(classElement.getQualifiedName(), CLASS_NODE_NAME, errorList);
			name = ModelParserHelper.getElementName(classElement, errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return Optional.empty();
		}

		ClassNode classNode = new ClassNode(name, rootNode.getModelChangeRegistrator());
		classNode.setParent(rootNode);
		rootNode.addClass(classNode);

		classNode.setDescription(ModelParserHelper.parseComments(classElement));
		//we need to do it here, so the backward search for global parameters will work
		classNode.setParent(rootNode);

		parseClassParameters(classElement, classNode, errorList);

		parseMethods(classElement, classNode, errorList);

		return Optional.ofNullable(classNode);
	}

	private void parseClassParameters(Element classElement, ClassNode targetClassNode, ListOfStrings errorList) {

		List<Element> iterableChildren = 
				ModelParserHelper.getIterableChildren(
						classElement, SerializationHelperVersion1.getParametersAndConstraintsElementNames());

		for (Element child : iterableChildren) {

			parseClassParameter(targetClassNode, errorList, child);
		}
	}

	private void parseClassParameter(ClassNode targetClassNode, ListOfStrings errorList, Element child) {

		boolean isBasicParameter = 
				ModelParserHelper.verifyElementName(
						child, SerializationHelperVersion1.getBasicParameterNodeName());

		if (isBasicParameter) {

			Optional<BasicParameterNode> globalBasicParameter = 
					fModelParserForGlobalParameter.parseGlobalBasicParameter(
							child, targetClassNode.getModelChangeRegistrator(), errorList);

			if (globalBasicParameter.isPresent()) {
				targetClassNode.addParameter(globalBasicParameter.get());
			} else {
				errorList.add("Cannot parse parameter for class: " + targetClassNode.getName() + ".");
			}

			return;
		} 

		boolean isCompositeParameter = 
				ModelParserHelper.verifyElementName(
						child, SerializationHelperVersion1.getCompositeParameterNodeName());

		if (isCompositeParameter) {

			Optional<CompositeParameterNode> globalCompositeParameter = 
					fModelParserForGlobalCompositeParameter.parseGlobalCompositeParameter(
							child, targetClassNode.getModelChangeRegistrator(), errorList);

			if (globalCompositeParameter.isPresent()) {
				targetClassNode.addParameter(globalCompositeParameter.get());
			} else {
				errorList.add("Cannot parse structure for class: " + targetClassNode.getName() + ".");
			}

		}
	}

	private void parseMethods(
			Element classElement, ClassNode targetClassNode, ListOfStrings errorList) {

		List<Element> childrenMethodElements = 
				ModelParserHelper.getIterableChildren(classElement, SerializationConstants.METHOD_NODE_NAME);

		for (Element methodElement : childrenMethodElements) {

			Optional<MethodNode> node = fModelParserForMethod.parseMethod(methodElement, targetClassNode, errorList);

			if (!node.isPresent()) {
				continue;
			}

			MethodNode methodNode = node.get();

			addMethodWithUniqueName(targetClassNode, methodNode);
		}
	}

	private void addMethodWithUniqueName(ClassNode targetClassNode, MethodNode methodNode) {

		MethodNode existingMethodNode = targetClassNode.findMethodWithTheSameName(methodNode.getName());

		if (existingMethodNode != null) {

			String newMethodName = 
					ClassNodeHelper.generateNewMethodName(
							targetClassNode, existingMethodNode.getName(), new ExtLanguageManagerForJava());

			methodNode.setName(newMethodName);
		}

		targetClassNode.addMethod(methodNode);
	}

}
