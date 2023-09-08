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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.TypeHelper;

public class BasicParameterNodeHelper {

	public static void compareParameters(
			BasicParameterNode basicParameterNode1, 
			BasicParameterNode basicParameterNode2) {

		if (basicParameterNode1.isExpected() != basicParameterNode2.isExpected()) {
			ExceptionHelper.reportRuntimeException("Expected property does not match.");
		}

		TypeHelper.compareTypes(basicParameterNode1.getType(), basicParameterNode1.getType());

		List<ChoiceNode> choices1 = basicParameterNode1.getChoices();
		List<ChoiceNode> choices2 = basicParameterNode2.getChoices();

		AbstractNodeHelper.compareSizes(choices1, choices2, "Number of choices differ.");

		for(int i = 0; i < choices1.size(); ++i){
			ChoiceNodeHelper.compareChoices(choices1.get(i), choices2.get(i));
		}

	}

	public static boolean propertiesOfBasicParametrsMatch(
			List<ParameterWithLinkingContext> parameters1, 
			List<ParameterWithLinkingContext> parameters2) {

		if (parameters1.size() != parameters2.size()) {
			return false;
		}

		ListIterator<ParameterWithLinkingContext> iterator1 = parameters1.listIterator();
		ListIterator<ParameterWithLinkingContext> iterator2 = parameters2.listIterator();

		for(;;) {

			boolean hasNext1 = iterator1.hasNext();
			boolean hasNext2 = iterator2.hasNext();

			if (!hasNext1) {
				return true;
			}

			if (hasNext1 != hasNext2) {
				return false;
			}

			ParameterWithLinkingContext item1 = iterator1.next();
			ParameterWithLinkingContext item2 = iterator2.next();

			if (!item1.getParameterAsBasic().isMatch(item2.getParameterAsBasic())) {
				return false;
			}

			AbstractParameterNode linkingContext1 = item1.getLinkingContext();
			AbstractParameterNode linkingContext2 = item2.getLinkingContext();

			if (linkingContext1 == null && linkingContext2 == null) {
				return true;
			}

			if (linkingContext1 != null && linkingContext2 == null) {
				return false;
			}

			if (linkingContext1 == null && linkingContext2 != null) {
				return false;
			}

			if (!linkingContext1.isMatch(linkingContext2)) {
				return false;
			}
		}
	}

	//	public static List<BasicParameterNode> convertAbstractListToBasicList(
	//			List<AbstractParameterNode> abstractParameterNodes) {
	//
	//		List<BasicParameterNode> result = new ArrayList<>(); 
	//
	//		for(AbstractParameterNode abstractParameterNode : abstractParameterNodes) {
	//
	//			if (!(abstractParameterNode instanceof BasicParameterNode)) {
	//				ExceptionHelper.reportRuntimeException("Cannot convert abstract parameters to basic parameters.");
	//			}
	//
	//			result.add((BasicParameterNode) abstractParameterNode);
	//		}
	//
	//		return result;
	//	}

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

	private static BasicParameterNode findParameterByQualifiedNameRecursive(
			String parameterName, 
			IAbstractNode parameterParent) {

		MethodNode parent = MethodNodeHelper.findMethodNode(parameterParent);

		if (parent == null) {
			return null;
		}

		List<BasicParameterNode> parameters = parent.getNestedBasicParameters(true);

		for (BasicParameterNode parameter : parameters) {

			String qualifiedName = 
					AbstractParameterSignatureHelper.createPathToTopContainerNewStandard(
							parameter, new ExtLanguageManagerForJava());

			if (qualifiedName.equals(parameterName)) {
				return parameter;
			}
		}

		return null;
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
				CompositeParameterNodeHelper.findTopComposite(basicParameterNode);

		return CompositeParameterNodeHelper.getMentioningMethodNodes(compositeParameterNode);
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

		RootNode rootNode = RootNodeHelper.findRootNode(parameter);

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

		List<AbstractParameterNode> abstractParameterNodes = 
				AbstractParameterNodeHelper.getLinkedParameters(basicParameterNode);

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

	public static ChoiceNode addNewChoice(
			BasicParameterNode basicParameterNode,
			String choiceNodeName,
			String valueString,
			boolean isRandomizedValue,
			boolean setParent,
			IModelChangeRegistrator modelChangeRegistrator) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, modelChangeRegistrator);
		choiceNode.setRandomizedValue(isRandomizedValue);

		if (setParent) {
			choiceNode.setParent(basicParameterNode);
		}

		basicParameterNode.addChoice(choiceNode);

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

	public static BasicParameterNode findBasicParameter(ChoiceNode globalChoiceNode) {

		IAbstractNode parent = globalChoiceNode;

		while (parent != null) {

			if (parent instanceof BasicParameterNode) {
				return (BasicParameterNode) parent;
			}

			parent = parent.getParent();
		}

		return null;
	}

	public static boolean choiceNodeExists(
			BasicParameterNode basicParameterNode, 
			ChoiceNode choiceNodeToFind) {

		Set<ChoiceNode> choiceNodes = basicParameterNode.getAllChoices();

		Iterator<ChoiceNode> it = choiceNodes.iterator();

		while(it.hasNext()) {
			ChoiceNode choiceNode = it.next();

			if (choiceNode.equals(choiceNodeToFind)) {
				return true;
			}
		}

		return false;
	}

	public static boolean valueOfChoiceNodeExists(BasicParameterNode basicParameterNode, String value) {

		Set<ChoiceNode> choiceNodes = basicParameterNode.getAllChoices();

		Iterator<ChoiceNode> it = choiceNodes.iterator();

		while(it.hasNext()) {
			ChoiceNode choiceNode = it.next();

			String valueString = choiceNode.getValueString();

			if (valueString.equals(value)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isParameterOfConstraintConsistent(
			BasicParameterNode basicParameterNode, 
			AbstractParameterNode parameterLinkingContext,
			IParametersAndConstraintsParentNode parentOfConstraint) {

		if (basicParameterNode.isGlobalParameter()) {

			if (isGlobalParameterConsistent(basicParameterNode, parameterLinkingContext, parentOfConstraint)) {
				return true;
			}

			return false;

		} else {

			if (isLocalParameterConsistent(basicParameterNode, parameterLinkingContext, parentOfConstraint)) {
				return true;
			}

			return false;
		}
	}

	private static boolean isGlobalParameterConsistent(
			BasicParameterNode globalBasicParameterNode, 
			AbstractParameterNode linkingContext,
			IParametersAndConstraintsParentNode parentMethodNodeOfConstraint) {

		CompositeParameterNode globalTopCompositeParameterNode = 
				CompositeParameterNodeHelper.findTopComposite(globalBasicParameterNode);

		if (globalTopCompositeParameterNode == null) {
			return false;
		}

		if (!isGlobalTopCompositeConsistent(globalTopCompositeParameterNode, parentMethodNodeOfConstraint)) {
			return false;
		}

		if (!isLinkingContextConsistent(linkingContext, parentMethodNodeOfConstraint)) {
			return false;
		}

		return true;
	}

	private static boolean isLinkingContextConsistent(
			AbstractParameterNode linkingContext,
			IParametersAndConstraintsParentNode topParentNode) {

		if (linkingContext == null) {
			return true;
		}

		List<AbstractParameterNode> parameters = topParentNode.getParameters();

		for (AbstractParameterNode parameter : parameters) {

			if (parameter.equals(linkingContext)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isGlobalTopCompositeConsistent(
			CompositeParameterNode globalTopCompositeParameterNode,
			IParametersAndConstraintsParentNode parentOfConstraint) {

		if (parentOfConstraint.equals(globalTopCompositeParameterNode)) {
			return true;
		}

		List<AbstractParameterNode> childParameters = parentOfConstraint.getParameters();

		for (AbstractParameterNode childParameter : childParameters) {

			if (childParameter instanceof BasicParameterNode) {
				continue;
			}

			AbstractParameterNode link = childParameter.getLinkToGlobalParameter();

			if (link.equals(globalTopCompositeParameterNode)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isLocalParameterConsistent(
			BasicParameterNode basicParameterNode,
			AbstractParameterNode parameterLinkingContext,
			IParametersAndConstraintsParentNode topParentNode) {

		CompositeParameterNode topComposite = 
				CompositeParameterNodeHelper.findTopComposite(basicParameterNode);

		if (topComposite == null) {

			return isLocalParameterConsistenWhenNoTopComposite(
					basicParameterNode, parameterLinkingContext,topParentNode);
		}

		List<AbstractParameterNode> childParameters = topParentNode.getParameters();

		for (AbstractParameterNode childParameter : childParameters) {

			if (childParameter instanceof BasicParameterNode) {
				if (childParameter.equals(basicParameterNode)) {
					return true;
				}
			}

			if (childParameter instanceof CompositeParameterNode) {
				if (childParameter.equals(topComposite)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean isLocalParameterConsistenWhenNoTopComposite(
			BasicParameterNode basicParameterNode,
			AbstractParameterNode parameterLinkingContext, 
			IParametersAndConstraintsParentNode topParentNode) {

		IParametersParentNode parentOfParameter = basicParameterNode.getParent();

		if (parentOfParameter == null) {
			return false;
		}

		if (topParentNode instanceof MethodNode) {

			MethodNode methodNode = (MethodNode) topParentNode;

			if (!parentOfParameter.equals(methodNode)) {
				return false;
			}
		}

		if (parameterLinkingContext != null) {

			CompositeParameterNode topComposite2 = 
					CompositeParameterNodeHelper.findTopComposite(parameterLinkingContext);

			if (topComposite2 != null && parentOfParameter.equals(topComposite2.getParent())) {
				return false;
			}
		}

		return true;
	}

	public static BasicParameterNode findParameterWithChoices(
			BasicParameterNode basicParameterNode, AbstractParameterNode linkingContext) {

		if (basicParameterNode.isGlobalParameter()) {
			return basicParameterNode;
		}

		if (linkingContext == null) {
			return basicParameterNode;
		}

		BasicParameterNode link = (BasicParameterNode) basicParameterNode.getLinkToGlobalParameter();

		if (link != null) {
			return link;
		}

		if (linkingContext instanceof BasicParameterNode) {
			return (BasicParameterNode) linkingContext;
		}

		return null;
	}

	public static List<BasicParameterNode> findBasicParameters(List<IAbstractNode> selectedNodes) {

		List<BasicParameterNode> parameters = selectedNodes.stream()
				.filter(e -> e instanceof BasicParameterNode)
				.map(e -> (BasicParameterNode)e)
				.collect(Collectors.toList());

		return parameters;
	}

	public static String getExtendedParameterName(String linkedParameterSignature) {

		String lastToken = StringHelper.getLastToken(linkedParameterSignature, " ");

		if (lastToken != null) {
			return lastToken;
		}

		return linkedParameterSignature;
	}

	public static List<BasicParameterNode> getMentioningBasicParameterNodes(
			List<CompositeParameterNode> compositeParametesNodes) {

		List<BasicParameterNode> basicParameterNodesToReturn = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParametesNodes) {

			List<BasicParameterNode> currentBasicParameterNodes = 
					CompositeParameterNodeHelper.getAllChildBasicParameters(compositeParameterNode);

			basicParameterNodesToReturn.addAll(currentBasicParameterNodes);
		}

		return basicParameterNodesToReturn;
	}

	public static BasicParameterNode findGlobalBasicParameter(
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

	public static BasicParameterNode getBasicParameter( // XYX move to basic parameter node helper
			int parameterNumber, IParametersParentNode parametersParentNode) {

		AbstractParameterNode abstractParameterNode = parametersParentNode.getParameter(parameterNumber);

		if (!(abstractParameterNode instanceof BasicParameterNode)) {
			ExceptionHelper.reportRuntimeException("Basic parameter expected.");
		}

		BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;

		return basicParameterNode;
	}

}
