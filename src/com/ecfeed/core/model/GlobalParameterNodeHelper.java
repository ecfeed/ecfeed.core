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

public class GlobalParameterNodeHelper {

	public enum SignatureType {
		WITH_TYPE,
		WITHOUT_TYPE
	}

	public static List<BasicParameterNode> getLinkedParameters(BasicParameterNode globalParameterNode) {

		List<BasicParameterNode> result = new ArrayList<>();

		IParametersParentNode parametersParentNode = globalParameterNode.getParametersParent();

		getParametersLinkedToGlobalParameterRecursive(globalParameterNode, parametersParentNode, result);

		return result;
	}

	private static void getParametersLinkedToGlobalParameterRecursive(
			BasicParameterNode globBasicParameterNode,
			IAbstractNode currentNode,
			List<BasicParameterNode> inOutLinkedParameters) {

		if (isBasicParameterLinkedToGlobal(currentNode, globBasicParameterNode)) {
			inOutLinkedParameters.add((BasicParameterNode) currentNode);
			return;
		}

		if ((currentNode instanceof ChoiceNode)) {
			return;
		}

		List<IAbstractNode> children = currentNode.getChildren();

		for (IAbstractNode childNode : children) {
			getParametersLinkedToGlobalParameterRecursive(globBasicParameterNode, childNode, inOutLinkedParameters);
		}
	}

	private static boolean isBasicParameterLinkedToGlobal(
			IAbstractNode currentNode,
			BasicParameterNode globalBasicParameterNode) {

		if (!(currentNode instanceof BasicParameterNode)) {
			return false;
		}

		BasicParameterNode basicParameterNode = (BasicParameterNode) currentNode;

		if (basicParameterNode.getLinkToGlobalParameter() == globalBasicParameterNode) {
			return true;
		}

		return false;
	}

	public static String checkLinkedParameters(BasicParameterNode globalParameterNode) {

		List<BasicParameterNode> linkedMethodMethodParameters = getLinkedParameters(globalParameterNode);

		if (linkedMethodMethodParameters == null) {
			return null;
		}

		if (linkedMethodMethodParameters.size() <= 0) {
			return null;
		}

		BasicParameterNode firstMethodParameterNode = linkedMethodMethodParameters.get(0);

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
			SignatureType signatureType,
			IExtLanguageManager extLanguageManager) {

		String qualifiedName = getQualifiedName(globalParameterNode, extLanguageManager);

		if (signatureType == SignatureType.WITHOUT_TYPE) {
			return qualifiedName;
		}

		String type = "";

		if (globalParameterNode instanceof BasicParameterNode) {

			BasicParameterNode basicParameterNode = (BasicParameterNode)globalParameterNode;
			type = getType(basicParameterNode, extLanguageManager);
		}

		return type + " " + qualifiedName;
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
