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

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ObjectHelper;
import com.ecfeed.core.utils.SignatureHelper;

public class GlobalParameterNodeHelper { // TODO MO-RE remove this class - move functions to helpers accordint to the real type 

	public static List<CompositeParameterNode> getLocalCompositeParametersLinkedToGlobal(
			CompositeParameterNode globParameterNode) {

		List<CompositeParameterNode> localCompositeParameterNodes =	new ArrayList<>();

		IAbstractNode startNode = findStartNode(globParameterNode);

		getLocalCompositeParametersLinkedToGlobalRecursive(startNode, globParameterNode, localCompositeParameterNodes);

		return localCompositeParameterNodes;
	}

	private static IAbstractNode findStartNode(AbstractParameterNode globParameterNode) {

		ClassNode classNode = AbstractNodeHelper.findClassNode(globParameterNode);

		if (classNode != null) {
			return classNode;
		}

		RootNode rootNode = AbstractNodeHelper.findRootNode(globParameterNode);

		return rootNode;
	}

	private static void getLocalCompositeParametersLinkedToGlobalRecursive(
			IAbstractNode currentNode,
			CompositeParameterNode globalCompositeParameterNode,
			List<CompositeParameterNode> inOutLocalCompositeParameterNodes) {

		if (currentNode instanceof CompositeParameterNode) {

			CompositeParameterNode compositeParameterNode = (CompositeParameterNode) currentNode;

			CompositeParameterNode linkToGlobalParameter = 
					(CompositeParameterNode) compositeParameterNode.getLinkToGlobalParameter();

			if (ObjectHelper.isEqual(linkToGlobalParameter, globalCompositeParameterNode)) {
				inOutLocalCompositeParameterNodes.add(compositeParameterNode);
			}

		}

		List<IAbstractNode> children = currentNode.getChildren();

		for (IAbstractNode child : children) {

			if (isNodeIgnoredForSearchOfComposites(child)) {
				continue;
			}

			getLocalCompositeParametersLinkedToGlobalRecursive(
					child,
					globalCompositeParameterNode,
					inOutLocalCompositeParameterNodes);
		}

	}

	private static boolean isNodeIgnoredForSearchOfComposites(IAbstractNode node) {

		if (node instanceof BasicParameterNode) {
			return true;
		}

		if (node instanceof ChoiceNode) {
			return true;
		}

		if (node instanceof ConstraintNode) {
			return true;
		}

		if (node instanceof TestSuiteNode) {
			return true;
		}

		if (node instanceof TestCaseNode) {
			return true;
		}

		return false;
	}

	public static List<MethodNode> getMethodsForCompositeParameters(
			List<CompositeParameterNode> compositeLocalParameters) {

		List<MethodNode> methodNodes = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeLocalParameters) {

			MethodNode methodNode = MethodNodeHelper.findMethodNode(compositeParameterNode);

			methodNodes.add(methodNode);
		}

		return methodNodes;
	}

	public static String checkLinkedParameters(BasicParameterNode globalParameterNode) {

		List<AbstractParameterNode> linkedMethodMethodParameters = 
				AbstractParameterNodeHelper.getLinkedParameters(globalParameterNode);

		if (linkedMethodMethodParameters == null) {
			return null;
		}

		if (linkedMethodMethodParameters.size() <= 0) {
			return null;
		}

		AbstractParameterNode firstMethodParameterNode = linkedMethodMethodParameters.get(0);

		String errorMessage = 
				"Parameter " + firstMethodParameterNode.getName() + 
				" of method " + firstMethodParameterNode.getParent().toString() + 
				" is linked to current global parameter " + globalParameterNode.getName() + ". " + 
				"Change of parameter type is not possible.";

		return errorMessage;
	}

	public static ChoiceNode addNewChoiceToGlobalParameter(
			BasicParameterNode globalParameterNode, 
			String choiceNodeName, 
			String valueString,
			boolean isRandomizedValue,
			IModelChangeRegistrator modelChangeRegistrator) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, modelChangeRegistrator);
		choiceNode.setRandomizedValue(isRandomizedValue);

		globalParameterNode.addChoice(choiceNode);

		return choiceNode;
	}

	public static ChoiceNode addNewChoiceToGlobalParameter(
			BasicParameterNode globalParameterNode, 
			String choiceNodeName, 
			String valueString,
			IModelChangeRegistrator modelChangeRegistrator) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, modelChangeRegistrator);
		choiceNode.setRandomizedValue(false);

		globalParameterNode.addChoice(choiceNode);

		return choiceNode;
	}

	public static String getName(BasicParameterNode methodParameterNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(methodParameterNode, extLanguageManager);
	}

	public static String createSignature(
			AbstractParameterNode globalParameterNode, 
			SignatureHelper.SignatureType signatureType,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = getQualifiedName(globalParameterNode, extLanguageManager);

		if (signatureType == SignatureHelper.SignatureType.WITHOUT_TYPE) {
			return qualifiedName;
		}

		String type = "";

		if (globalParameterNode instanceof BasicParameterNode) {

			BasicParameterNode basicParameterNode = (BasicParameterNode)globalParameterNode;
			type = getType(basicParameterNode, extLanguageManager);
		}

		return (type + " " + qualifiedName).trim();
	}

	public static String getQualifiedName(
			AbstractParameterNode globalParameterNode,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = globalParameterNode.getQualifiedName();
		qualifiedName = extLanguageManager.convertTextFromIntrToExtLanguage(qualifiedName);

		return qualifiedName;
	}

	public static String getType(BasicParameterNode globalParameterNode, IExtLanguageManager extLanguageManager) {

		String type = globalParameterNode.getType();
		type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);

		return type;
	}

}
