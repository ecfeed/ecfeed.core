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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

public class BasicParameterNodeHelper {

	public static void compareParameters(
			BasicParameterNode basicParameterNode1, 
			BasicParameterNode basicParameterNode2) {

		if (basicParameterNode1.isExpected() != basicParameterNode2.isExpected()) {
			ExceptionHelper.reportRuntimeException("Expected property does not match.");
		}

		ModelCompareHelper.compareTypes(basicParameterNode1.getType(), basicParameterNode1.getType());
		ModelCompareHelper.compareSizes(basicParameterNode1.getChoices(), basicParameterNode2.getChoices());
		for(int i = 0; i < basicParameterNode1.getChoices().size(); ++i){
			ModelCompareHelper.compareChoices(basicParameterNode1.getChoices().get(i), basicParameterNode2.getChoices().get(i));
		}

	}

	public static boolean propertiesOfBasicParametrsMatch(
			List<BasicParameterNode> parameters1, List<BasicParameterNode> parameters2) {

		if (parameters1.size() != parameters2.size()) {
			return false;
		}

		ListIterator<BasicParameterNode> iterator1 = parameters1.listIterator();
		ListIterator<BasicParameterNode> iterator2 = parameters2.listIterator();

		for(;;) {

			boolean hasNext1 = iterator1.hasNext();
			boolean hasNext2 = iterator2.hasNext();

			if (!hasNext1) {
				return true;
			}

			if (hasNext1 != hasNext2) {
				return false;
			}

			BasicParameterNode basicParameterNode1 = iterator1.next();
			BasicParameterNode basicParameterNode2 = iterator2.next();

			if (!basicParameterNode1.propertiesMatch(basicParameterNode2)) {
				return false;
			}
		}
	}

	public static List<BasicParameterNode> convertAbstractListToBasicList(List<AbstractParameterNode> abstractParameterNodes) {

		List<BasicParameterNode> result = new ArrayList<>(); 

		for(AbstractParameterNode abstractParameterNode : abstractParameterNodes) {

			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				ExceptionHelper.reportRuntimeException("Cannot convert abstract parameters to basic parameters.");
			}

			result.add((BasicParameterNode) abstractParameterNode);
		}

		return result;
	}

	public static List<BasicParameterNode> getBasicParametersForParentNodeSubtree(
			IParametersParentNode parametersParentNode) {

		List<BasicParameterNode> resultParameterNodes = new ArrayList<BasicParameterNode>();

		accumulateBasicParametersRecursively(parametersParentNode, resultParameterNodes);

		return resultParameterNodes;
	}

	private static void accumulateBasicParametersRecursively(
			IParametersParentNode parametersParentNode,
			List<BasicParameterNode> inOutResult) {

		List<AbstractParameterNode> abstractParameters = parametersParentNode.getParameters();

		for (AbstractParameterNode abstractParameterNode : abstractParameters) {

			if (abstractParameterNode instanceof CompositeParameterNode) {

				if (abstractParameterNode.isLinked() && (abstractParameterNode.getLinkToGlobalParameter() != null)) {
					accumulateBasicParametersRecursively((IParametersParentNode)abstractParameterNode.getLinkToGlobalParameter(), inOutResult);
				} else {
					accumulateBasicParametersRecursively((IParametersParentNode)abstractParameterNode, inOutResult);
				}
			} else {
				inOutResult.add((BasicParameterNode) abstractParameterNode);
			}
		}
	}

	public static BasicParameterNode findBasicParameterByQualifiedName(
			String parameterNameToFindInExtLanguage, 
			IParametersParentNode parametersParentNode,
			IExtLanguageManager extLanguageManager) {

		String parameterNameToFindInIntrLanguage = 
				extLanguageManager.convertTextFromExtToIntrLanguage(parameterNameToFindInExtLanguage);

		return findParameterByQualifiedNameRecursive(parameterNameToFindInIntrLanguage, parametersParentNode);
	}

	public static BasicParameterNode findBasicParameterByQualifiedIntrName(
			String parameterNameToFindInIntrLanguage, 
			IParametersParentNode parametersParentNode) {

		return findParameterByQualifiedNameRecursive(parameterNameToFindInIntrLanguage, parametersParentNode);
	}

	private static BasicParameterNode findParameterByQualifiedNameRecursive(String parameterName, IAbstractNode parameterParent) {

		String[] segments = parameterName.split(SignatureHelper.SIGNATURE_NAME_SEPARATOR);

		while (parameterParent.getChild(segments[0]) == null || !(parameterParent instanceof MethodNode || parameterParent instanceof ClassNode || parameterParent instanceof RootNode)) {
			parameterParent = parameterParent.getParent();

			if (parameterParent == null) {
				return null;
			}
		}

		for (int i = 0 ; i < segments.length ; i++) {
			parameterParent = parameterParent.getChild(segments[i]);
		}

		return (BasicParameterNode) parameterParent;
	}

	public static String calculateNewParameterType(BasicParameterNode fTarget, String linkedParameterSignature) {

		if (linkedParameterSignature == null) {
			return fTarget.getRealType();
		}

		return extractTypeFromSignature(linkedParameterSignature);
	}

	public static List<MethodNode> getMentioningMethodNodes(BasicParameterNode basicParameterNode) {

		MethodNode methodNode = MethodNodeHelper.findMethodNode(basicParameterNode);

		if (methodNode != null) {
			return Arrays.asList(methodNode);
		}
		
		IAbstractNode parentAbstractNode = basicParameterNode.getParent();
		
		if (parentAbstractNode instanceof CompositeParameterNode) {
			return getMentioningMethodsForChildOfGlobalStructure(basicParameterNode);
		}

		return getMentioningMethodsForGlobalParameter(basicParameterNode);
	}

	private static List<MethodNode> getMentioningMethodsForChildOfGlobalStructure(
			BasicParameterNode basicParameterNode) {
		
		CompositeParameterNode compositeParameterNode = 
				AbstractParameterNodeHelper.getTopComposite(basicParameterNode);
		
		List<MethodNode> resultMethodNodes = new ArrayList<>();

		List<AbstractParameterNode> linkedParameters = 
				AbstractParameterNodeHelper.getLinkedParameters(compositeParameterNode);

		for (AbstractParameterNode linkedParameterNode : linkedParameters) {

			MethodNode methodNode = MethodNodeHelper.findMethodNode(linkedParameterNode);

			if (methodNode != null) {
				resultMethodNodes.add(methodNode);
			}
		}

		return resultMethodNodes;
	}

	private static List<MethodNode> getMentioningMethodsForGlobalParameter(BasicParameterNode basicParameterNode) {
		
		List<MethodNode> resultMethodNodes = new ArrayList<>();

		List<AbstractParameterNode> linkedParameters = 
				AbstractParameterNodeHelper.getLinkedParameters(basicParameterNode);

		for (AbstractParameterNode linkedParameterNode : linkedParameters) {

			MethodNode methodNode = MethodNodeHelper.findMethodNode(linkedParameterNode);

			if (methodNode != null) {
				resultMethodNodes.add(methodNode);
			}
		}

		return resultMethodNodes;
	}

	//	private static void getMentioningMethodNodesRecursive(
	//			BasicParameterNode basicParameterNode, 
	//			IAbstractNode currentNode,
	//			List<MethodNode> inOutResultMethodNodes) {
	//		
	//		if ((currentNode instanceof CompositeParameterNode) || (currentNode instanceof BasicParameterNode)) {
	//			return;
	//		}
	//		
	//		if (currentNode instanceof MethodNode) {
	//			
	//			MethodNode methodNode = (MethodNode) currentNode;
	//			
	//			if (MethodNodeHelper.methodNodeMentionsBasicParameter(methodNode, basicParameterNode)) {
	//				inOutResultMethodNodes.add(methodNode);
	//			}
	//			
	//			return;
	//		}
	//		
	//		
	//		List<IAbstractNode> children = currentNode.getChildren();
	//		
	//		for (IAbstractNode abstractNode : children) {
	//			getMentioningMethodNodesRecursive(basicParameterNode, abstractNode, inOutResultMethodNodes);
	//		}
	//		
	//	}

	public static Set<ConstraintNode> getMentioningConstraints(Collection<BasicParameterNode> parameters) {

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for(BasicParameterNode parameter : parameters){
			result.addAll(getMentioningConstraints(parameter));
		}

		return result;
	}

	public static List<ConstraintNode> getMentioningConstraints(List<BasicParameterNode> basicParameterNodesToDelete) {

		Set<ConstraintNode> resultConstraintNodesToDelete = new HashSet<>();

		for (BasicParameterNode basicParameterNode : basicParameterNodesToDelete) {

			List<ConstraintNode> currentConstraintNodes =
					getMentioningConstraints(basicParameterNode);

			resultConstraintNodesToDelete.addAll(currentConstraintNodes);
		}

		ArrayList<ConstraintNode> resultList = new ArrayList<>(resultConstraintNodesToDelete);
		return resultList;
	}

	public static List<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter) { 

		Set<ConstraintNode> setOfMentioningConstraints = getMentioningConstraints(parameter, 0);

		ArrayList<ConstraintNode> mentioningConstraints = new ArrayList<>(setOfMentioningConstraints);

		return mentioningConstraints;
	}

	private static Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter, int dummy) {

		Set<ConstraintNode> result = new HashSet<>();

		RootNode rootNode = AbstractNodeHelper.findRootNode(parameter);

		getMentioningConstraintsRecursive(rootNode, parameter, result);

		return result;
	}

	public static void getMentioningConstraintsRecursive(
			IAbstractNode currentNode, 
			BasicParameterNode parameter, 
			Set<ConstraintNode> inOutConstraints) {

		if (currentNode == null) {
			return;
		}

		if ((currentNode instanceof BasicParameterNode) || (currentNode instanceof ChoiceNode)) {
			return;
		}

		if (currentNode instanceof IConstraintsParentNode) {

			IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) currentNode;

			Set<ConstraintNode> constraintsOfMethod = 
					constraintsParentNode.getMentioningConstraints(parameter);

			inOutConstraints.addAll(constraintsOfMethod);
		}

		List<IAbstractNode> children = currentNode.getChildren();

		for (IAbstractNode child : children) {
			getMentioningConstraintsRecursive(child, parameter, inOutConstraints);
		}
	}

	private static String extractTypeFromSignature(String linkedParameterSignature) {

		String type = StringHelper.getFirstToken(linkedParameterSignature, " ");
		return type;
	}

	public static List<BasicParameterNode> getLinkedBasicParameters(
			BasicParameterNode basicParameterNode) {

		if (!basicParameterNode.isGlobalParameter()) {
			return new ArrayList<>();
		}

		List<AbstractParameterNode> abstractParameterNodes = AbstractParameterNodeHelper.getLinkedParameters(basicParameterNode);

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

	public static ChoiceNode addNewChoiceToBasicParameter(
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

	public static ChoiceNode addNewChoiceToBasicParameter(
			BasicParameterNode globalParameterNode, 
			String choiceNodeName, 
			String valueString,
			IModelChangeRegistrator modelChangeRegistrator) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, modelChangeRegistrator);
		choiceNode.setRandomizedValue(false);

		globalParameterNode.addChoice(choiceNode);

		return choiceNode;
	}

	public static boolean parameterMentionsBasicParameter(
			BasicParameterNode basicParameterNode,
			BasicParameterNode checkedBasicParameterNode) {

		if (basicParameterNode.equals(checkedBasicParameterNode)) {
			return true;
		}

		AbstractParameterNode link = basicParameterNode.getLinkToGlobalParameter();

		if (link == null) {
			return false;
		}

		if (link.equals(basicParameterNode)) {
			return true;
		}

		return false;
	}

}
