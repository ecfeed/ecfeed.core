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
import java.util.Optional;
import java.util.stream.Collectors;

import com.ecfeed.core.operations.GenericOperationAddParameter;
import com.ecfeed.core.operations.nodes.OnMethodParametersOperationReplaceWithGlobal;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.OperationExecuter;

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
	
	public static List<MethodNode> getMentioningMethodNodes(IParametersParentNode parametersParentNode) {
		
		if (parametersParentNode instanceof MethodNode) {
			
			List<MethodNode> methodNodes = new ArrayList<>();
			
			methodNodes.add((MethodNode) parametersParentNode);
			
			return methodNodes;
		}
		
		if (parametersParentNode instanceof RootNode) {
			return new ArrayList<>();
		}
		
		CompositeParameterNode compositeParameterNode =
				AbstractParameterNodeHelper.findTopComposite(parametersParentNode);

		if (compositeParameterNode == null) {
			return new ArrayList<>();
		}
		
		List<MethodNode> mentioningNodes = 
				CompositeParameterNodeHelper.getMentioningMethodNodes(compositeParameterNode);
		
		return mentioningNodes;
	}
	
	public static BasicParameterNode addNewBasicParameter(
			IParametersParentNode parametersParentNode,
			OperationExecuter operationExecuter,
			IExtLanguageManager extLanguageManager) {

		String parameterName = ParametersAndConstraintsParentNodeHelper.generateNewParameterName(parametersParentNode);

		String parameterType = JavaLanguageHelper.TYPE_NAME_STRING;

		String defaultValue = JavaLanguageHelper.getDefaultValue(parameterType);

		BasicParameterNode parameter = 
				new BasicParameterNode(
						parameterName, parameterType, defaultValue, false, parametersParentNode.getModelChangeRegistrator());

		int index = parametersParentNode.getParameters().size();

		if (addBasicParameterByOperation(parameter, index, parametersParentNode, operationExecuter, extLanguageManager)) {
			return parameter;
		}

		return null;
	}

	public static CompositeParameterNode addNewCompositeParameter(
			IParametersParentNode parametersParentNode, 
			OperationExecuter operationExecuter,
			IExtLanguageManager extLanguageManager) {

		String parameterName = 
				ParametersAndConstraintsParentNodeHelper.generateNewCompositeParameterName(parametersParentNode);

		CompositeParameterNode parameter = 
				new CompositeParameterNode(
						parameterName, parametersParentNode.getModelChangeRegistrator());

		int index = parametersParentNode.getParameters().size();

		if (addCompositeParameterByOperation(
				parameter, index, parametersParentNode, operationExecuter, extLanguageManager)) {
			return parameter;
		}

		return null;
	}

	public static boolean addBasicParameterByOperation(
			BasicParameterNode parameter, 
			int index,
			IParametersParentNode ownNode,
			OperationExecuter operationExecuter,
			IExtLanguageManager extLanguageManager) {

		return operationExecuter.execute(
				new GenericOperationAddParameter(ownNode, parameter, index, true, extLanguageManager), 
				"Cannot add parameter.");
	}

	public static boolean addCompositeParameterByOperation(
			CompositeParameterNode parameter, 
			int index,
			IParametersParentNode ownNode,
			OperationExecuter operationExecuter,
			IExtLanguageManager extLanguageManager) {

		return operationExecuter.execute(
				new GenericOperationAddParameter(ownNode, parameter, index, true, extLanguageManager), 
				"Cannot add parameter.");
	}
	
	public static boolean convertMethodParametersToGlobalByOperation(
			IParametersParentNode parametersParentNode, 
			List<BasicParameterNode> originalParameters,
			IModelUpdateContext modelUpdateContext,
			IExtLanguageManager extLanguageManager) {
		
		OnMethodParametersOperationReplaceWithGlobal operation = 
				new OnMethodParametersOperationReplaceWithGlobal(
						parametersParentNode, 
						originalParameters, 
						Optional.empty(), 
						extLanguageManager);

		OperationExecuter operationExecuter = new OperationExecuter(modelUpdateContext);
		
		return operationExecuter.execute(
				operation, Messages.DIALOG_REPLACE_PARAMETERS_WITH_LINKS_TITLE);
	}
	
}
