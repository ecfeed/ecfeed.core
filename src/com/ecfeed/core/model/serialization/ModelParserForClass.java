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

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForClass {

	public static ClassNode parseAndAddClass(
			Element classElement, 
			RootNode rootNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings errorList) {

		ClassNode classNode = createClassNode(classElement, rootNode, errorList);

		if (classNode == null) {
			return null;
		}

		rootNode.addClass(classNode);

		classNode.setDescription(ModelParserHelper.parseComments(classElement));

		ModelParserForParameterHelper.parseLocalAndChildParametersWithoutConstraints(
				classElement, classNode, elementToNodeMapper, errorList);

		parseConstraintsOfGlobalCompositeParameters(classElement, elementToNodeMapper, errorList);

		parseMethods(classElement, classNode, elementToNodeMapper, errorList);

		return classNode;
	}

	private static void parseConstraintsOfGlobalCompositeParameters(
			Element classElement,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings errorList) {

		List<Element> iterableChildren = 
				ModelParserHelper.getIterableChildren(
						classElement, SerializationHelperVersion1.getCompositeParameterNodeName());

		for (Element compositeElement : iterableChildren) {

			CompositeParameterNode compositeParameterNode = 
					(CompositeParameterNode) elementToNodeMapper.getNode(compositeElement);

			ModelParserForParameterHelper.parseLocalAndChildConstraints(
					compositeElement, 
					compositeParameterNode, 
					elementToNodeMapper, 
					errorList);
		}
	}

	private static ClassNode createClassNode(Element classElement, RootNode rootNode, ListOfStrings errorList) {
		String name;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(classElement.getQualifiedName(), CLASS_NODE_NAME, errorList);
			name = ModelParserHelper.getElementName(classElement, errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return null;
		}

		ClassNode classNode = new ClassNode(name, rootNode.getModelChangeRegistrator());
		classNode.setParent(rootNode);

		return classNode;
	}

	private static void parseMethods(
			Element classElement, 
			ClassNode targetClassNode,
			ElementToNodeMapper elementToNodeMapper,
			ListOfStrings errorList) {

		List<Element> childrenMethodElements = 
				ModelParserHelper.getIterableChildren(classElement, SerializationConstants.METHOD_NODE_NAME);

		for (Element methodElement : childrenMethodElements) {

			MethodNode node = 
					ModelParserForMethod.parseMethod(
							methodElement, targetClassNode, elementToNodeMapper, errorList);

			if (node==null) {
				continue;
			}

			addMethodWithUniqueName(targetClassNode, node);
		}
	}

	private static void addMethodWithUniqueName(ClassNode targetClassNode, MethodNode methodNode) {

		MethodNode existingMethodNode = targetClassNode.findMethodWithTheSameName(methodNode.getName());

		if (existingMethodNode != null) {

			String newMethodName = 
					ClassNodeHelper.generateUniqueMethodName(
							targetClassNode, existingMethodNode.getName(), null, new ExtLanguageManagerForJava());

			methodNode.setName(newMethodName);
		}

		targetClassNode.addMethod(methodNode);
	}

}
