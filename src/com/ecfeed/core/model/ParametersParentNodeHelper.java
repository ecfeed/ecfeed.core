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

	public static String generateUniqueParameterName(
			String nameInIntrLanguage, 
			String availableNameInIntrLanguage,
			IParametersParentNode parametersParent) {

		String nameCore = StringHelper.removeFromNumericPostfix(nameInIntrLanguage);

		String newName = generateUniqueParameterNameFromNameCore(nameCore, availableNameInIntrLanguage, parametersParent);

		return newName;
	}

	private static String generateUniqueParameterNameFromNameCore(
			String startNameCore,
			String availableName,
			IParametersParentNode parametersParent) {

		for (int i = 1;   ; i++) {

			String newName = startNameCore + String.valueOf(i);

			if (availableName != null && StringHelper.isEqual(newName, availableName)) {
				return availableName;
			}

			if (null == AbstractParameterNodeHelper.findParameterByName(newName, parametersParent)) {
				return newName;
			}
		}
	}

	//----------------------------------------------------------------------------

	public static List<AbstractParameterNode> getNestedAbstractParameters(IParametersParentNode parent, boolean follow) {
		List<AbstractParameterNode> nodes = new ArrayList<>();

		if (parent instanceof AbstractParameterNode) {
			getNestedAbstractParameterSourceParameter(parent, nodes, follow);
		} else {
			getNestedAbstractParameterSourceNode(parent, nodes, follow);
		}

		return nodes;
	}

	private static void getNestedAbstractParameterSourceParameter(IParametersParentNode parent, List<AbstractParameterNode> nodes, boolean follow) {
		AbstractParameterNode parsedNode = (AbstractParameterNode) parent;

		if (follow) {
			parsedNode = parsedNode.getLinkDestination();
		}

		for (AbstractParameterNode node : ((IParametersParentNode) parsedNode).getParameters()) {

			nodes.add(node);

			if (node instanceof CompositeParameterNode) {
				nodes.addAll(getNestedAbstractParameters((IParametersParentNode) node, follow));
			}
		}
	}

	private static void getNestedAbstractParameterSourceNode(IParametersParentNode parent, List<AbstractParameterNode> nodes, boolean follow) {

		for (IAbstractNode node : parent.getChildren()) {

			if (node instanceof BasicParameterNode) {
				nodes.add((AbstractParameterNode) node);
			} else if (node instanceof IParametersParentNode) {
				nodes.addAll(getNestedAbstractParameters((IParametersParentNode) node, follow));
			}
		}
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

	//	public static IParametersParentNode findParametersParentNode(IAbstractNode abstractNode) {
	//
	//		IAbstractNode parent = abstractNode;
	//
	//		while (parent != null) {
	//
	//			if (parent instanceof IParametersParentNode) {
	//				return (IParametersParentNode) parent;
	//			}
	//
	//			parent = parent.getParent();
	//		}
	//
	//		return null;
	//	}

}
