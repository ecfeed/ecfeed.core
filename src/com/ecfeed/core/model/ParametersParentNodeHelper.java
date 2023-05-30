/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public class ParametersParentNodeHelper {

	public static List<String> getParameterTypes(IParametersParentNode parameterParentNode, IExtLanguageManager extLanguageManager) {

		List<String> result = new ArrayList<String>();

		for (AbstractParameterNode abstractParameterNode : parameterParentNode.getParameters()) {

			if (abstractParameterNode instanceof BasicParameterNode) {

				BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;

				String type = basicParameterNode.getType();

				type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);

				result.add(type);
				continue;
			}

			if (abstractParameterNode instanceof CompositeParameterNode) {

				result.add(CompositeParameterNode.COMPOSITE_PARAMETER_TYPE);
				continue;
			}
		}

		return result;
	}

	public static List<String> getParameterNames(IParametersParentNode method, IExtLanguageManager extLanguageManager) {

		List<String> result = new ArrayList<String>();

		for (AbstractParameterNode parameter : method.getParameters()) {

			BasicParameterNode methodParameterNode = (BasicParameterNode)parameter;

			String name = MethodParameterNodeHelper.getName(methodParameterNode, extLanguageManager);

			result.add(name);
		}

		return result;
	}

	public static BasicParameterNode findGlobalParameter(
			IParametersParentNode parametersParentNode, String globalParameterExtendedName) {

		if (StringHelper.isNullOrEmpty(globalParameterExtendedName)) {
			return null;
		}

		String parentName = AbstractNodeHelper.getParentName(globalParameterExtendedName);
		String parameterName = ParametersAndConstraintsParentNodeHelper.getParameterName(globalParameterExtendedName);

		MethodNode methodNode = MethodNodeHelper.findMethodNode(parametersParentNode);

		ClassNode classNode = methodNode.getClassNode();
		String className = classNode.getName();

		if (StringHelper.isEqual(className, parentName)) {
			AbstractParameterNode abstractParameterNode = classNode.findParameter(parameterName);
			return (BasicParameterNode)abstractParameterNode;
		}

		RootNode rootNode = classNode.getRoot();
		String rootName = rootNode.getName();

		if (parentName == null || rootName.equals(parentName)) {
			AbstractParameterNode abstractParameterNode = rootNode.findParameter(parameterName);
			return (BasicParameterNode)abstractParameterNode;
		}			

		ExceptionHelper.reportRuntimeException("Invalid dst parameter extended name.");
		return null;
	}

	public static BasicParameterNode getBasicParameter(int parameterNumber, IParametersParentNode parametersParentNode) {

		AbstractParameterNode abstractParameterNode = parametersParentNode.getParameter(parameterNumber);

		if (!(abstractParameterNode instanceof BasicParameterNode)) {
			ExceptionHelper.reportRuntimeException("Basic parameter expected.");
		}

		BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;

		return basicParameterNode;
	}

	public static List<String> getParameterTypes(List<AbstractParameterNode> parameters) {

		List<String> parameterTypes = new ArrayList<String>();

		for (AbstractParameterNode abstractParameterNode : parameters) {

			if (abstractParameterNode instanceof BasicParameterNode) {

				BasicParameterNode methodParameterNode = (BasicParameterNode)abstractParameterNode;

				String parameterType = methodParameterNode.getType();
				parameterTypes.add(parameterType);

			} else {

				parameterTypes.add(CompositeParameterNode.COMPOSITE_PARAMETER_TYPE);
			}
		}

		return parameterTypes;
	}

//----------------------------------------------------------------------------

	public static List<AbstractParameterNode> getNestedAbstractParameters(IParametersParentNode parent, boolean follow) {
		List<AbstractParameterNode> nodes = new ArrayList<>();

		for (AbstractParameterNode node : parent.getParameters()) {

			AbstractParameterNode parsedNode = node;

			if (follow) {
				 parsedNode = node.getLinkDestination();
			}

			nodes.add(parsedNode);

			if (parsedNode instanceof IParametersParentNode) {
				getNestedCompositeParameters((IParametersParentNode) parsedNode, follow);
			}
		}

		return nodes;
	}

	public static List<BasicParameterNode> getNestedBasicParameters(IParametersParentNode parent, boolean follow) {

		return getNestedAbstractParameters(parent, follow).stream()
				.filter(e -> e instanceof BasicParameterNode)
				.map(e -> (BasicParameterNode) e)
				.collect(Collectors.toList());
	}

	public static List<CompositeParameterNode> getNestedCompositeParameters(IParametersParentNode parent, boolean follow) {

		return getNestedAbstractParameters(parent, follow).stream()
				.filter(e -> e instanceof CompositeParameterNode)
				.map(e -> (CompositeParameterNode) e)
				.collect(Collectors.toList());
	}
}
