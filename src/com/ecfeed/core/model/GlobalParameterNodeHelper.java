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

public class GlobalParameterNodeHelper {

	public enum SignatureType {
		WITH_TYPE,
		WITHOUT_TYPE
	}

//	public static List<MethodNode> getLinkedMethods(AbstractParameterNode globalParameterNode) {
//
//		if (globalParameterNode instanceof CompositeParameterNode) {
//			
//			List<CompositeParameterNode> compositeLocalParameters = 
//					getLocalCompositeParametersLinkedToGlobal(globalParameterNode);
//			
//			List<MethodNode> methodNodes = getMethodsForCompositeParameters(compositeLocalParameters);
//			return methodNodes;
//		}
//		
//		ExceptionHelper.reportRuntimeException("TODO"); // TODO MO-RE 
//		return null;
//	}
	
	public static List<AbstractParameterNode> getLinkedParameters(AbstractParameterNode globalParameterNode) {

		List<AbstractParameterNode> result = new ArrayList<>();

		IParametersParentNode parametersParentNode = globalParameterNode.getParametersParent();

		getParametersLinkedToGlobalParameterRecursive(globalParameterNode, parametersParentNode, result);

		return result;
	}
	
	public static List<BasicParameterNode> getLinkedBasicParameters(BasicParameterNode globalParameterNode) {
		
		List<AbstractParameterNode> abstractParameterNodes = getLinkedParameters(globalParameterNode);
		
		List<BasicParameterNode> basicParameterNodes = getBasicParameters(abstractParameterNodes);
		
		return basicParameterNodes;
	}
	
	public static List<BasicParameterNode> getBasicParameters(List<AbstractParameterNode> abstractParameterNodes) {
		
		List<BasicParameterNode> result = new ArrayList<>();
		
		for (AbstractParameterNode abstractParameterNode : abstractParameterNodes) {
			
			if (abstractParameterNode instanceof BasicParameterNode) {
				result.add((BasicParameterNode) abstractParameterNode);
			}
		}
		
		return result;
	}

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

	private static void getParametersLinkedToGlobalParameterRecursive(
			AbstractParameterNode globBasicParameterNode,
			IAbstractNode currentNode,
			List<AbstractParameterNode> inOutLinkedParameters) {

		if (isParameterLinkedToGlobal(currentNode, globBasicParameterNode)) {
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

	private static boolean isParameterLinkedToGlobal(
			IAbstractNode currentNode,
			AbstractParameterNode globalBasicParameterNode) {

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

		List<AbstractParameterNode> linkedMethodMethodParameters = getLinkedParameters(globalParameterNode);

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
