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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NameHelper;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

public class ConstraintNodeHelper {

	public static void convertConstraint(
			ConstraintNode constraintNode,
			ParameterConversionItem parameterConversionItem) {

		Constraint constraint = constraintNode.getConstraint();

		if (constraint == null) {
			ExceptionHelper.reportRuntimeException("Cannot update choice references. Constraint is empty.");
		}

		constraint.convert(parameterConversionItem);
	}

	//	public static void updateParameterReferences(
	//			ConstraintNode constraintNode,
	//			MethodParameterNode oldMethodParameterNode,
	//			ChoicesParentNode dstParameterForChoices) {
	//
	//		Constraint constraint = constraintNode.getConstraint();
	//
	//		if (constraint == null) {
	//			ExceptionHelper.reportRuntimeException("Cannot update choice references. Constraint is empty.");
	//		}
	//
	//		//		constraint.updateParameterReferences(oldMethodParameterNode, dstParameterForChoices);
	//	}

	public static List<ChoiceNode> getChoicesUsedInConstraint(
			ConstraintNode constraintNode,
			BasicParameterNode methodParameterNode) {

		List<ChoiceNode> result = 
				ConstraintHelper.getChoicesUsedInConstraints(
						constraintNode.getConstraint(),
						methodParameterNode);

		return result;
	}

	public static List<String> getLabelsUsedInConstraint(
			ConstraintNode constraintNode,
			BasicParameterNode methodParameterNode) {

		List<String> result = 
				ConstraintHelper.getLabelsUsedInConstraints(
						constraintNode.getConstraint(),
						methodParameterNode);

		return result;
	}

	public static String createSignature(ConstraintNode constraintNode, IExtLanguageManager extLanguageManager) {

		String qualifiedNameInExtLanguage = createQualifiedName(constraintNode, extLanguageManager); 

		String signatureOfConditionsInExtLanguage = 
				ConstraintHelper.createSignatureOfConditions(constraintNode.getConstraint(), extLanguageManager);

		String signatureInExtLanguage = 
				qualifiedNameInExtLanguage + SignatureHelper.SIGNATURE_CONTENT_SEPARATOR + signatureOfConditionsInExtLanguage;

		return signatureInExtLanguage; 
	}

	private static String createQualifiedName(ConstraintNode constraintNode, IExtLanguageManager extLanguageManager) {

		String prefixInIntrLanguage = createQualifiedPrefix(constraintNode);

		String prefixInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(prefixInIntrLanguage);

		String constraintName = constraintNode.getConstraint().getName();

		if (StringHelper.isNullOrEmpty(prefixInExtLanguage)) {
			return constraintName;
		}

		String qualifiedName = 
				prefixInExtLanguage + SignatureHelper.SIGNATURE_NAME_SEPARATOR + constraintName;

		return qualifiedName;
	}

	public static String getName(ConstraintNode ownNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(ownNode, extLanguageManager);
	}

	private static String createQualifiedPrefix(ConstraintNode constraintNode) {

		String prefix = createQualifiedPrefixIntr(constraintNode);

		prefix = prefix.trim();

		prefix = StringHelper.removeFromPostfix(":", prefix);

		prefix = prefix.trim();

		return prefix;
	}

	private static String createQualifiedPrefixIntr(ConstraintNode constraintNode) {

		String prefix = "";
		IAbstractNode currentNode = constraintNode;

		for (;;) {

			IAbstractNode parent = currentNode.getParent();

			if (parent == null) {
				return "";
			}

			if ((parent instanceof MethodNode) || (parent instanceof RootNode) || (parent instanceof ClassNode)) {
				return prefix;
			}

			if (!(parent instanceof CompositeParameterNode)) {
				ExceptionHelper.reportRuntimeException("Composite parameter expected.");
			}

			prefix = parent.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR + prefix;

			currentNode = parent;
		}

	}

	public static List<ConstraintNode> makeDerandomizedCopyOfConstraintNodes(
			List<ConstraintNode> constraints) {

		List<ConstraintNode> clonedConstraintNodes = new ArrayList<ConstraintNode>();

		NodeMapper nodeMapper = new NodeMapper();

		for (ConstraintNode constraint : constraints) {

			ConstraintNode clonedConstraint = constraint.makeClone(Optional.of(nodeMapper));

			clonedConstraint.derandomize();
			clonedConstraintNodes.add(clonedConstraint);
		}

		return clonedConstraintNodes;
	}

	public static List<ConstraintNode> createListOfConstraintNodes(
			List<Constraint> constraints, 
			MethodNode methodNode) {

		List<ConstraintNode> constraintNodes = new ArrayList<>();

		for (Constraint constraint: constraints) {

			ConstraintNode constraintNode = new ConstraintNode(constraint.getName(), constraint, null);

			constraintNodes.add(constraintNode);
		}

		return constraintNodes;
	}

	public static List<Constraint> createListOfConstraints(List<ConstraintNode> constraintNodes) {

		List<Constraint> constraints = new ArrayList<>();

		for (ConstraintNode constraintNode : constraintNodes) {
			constraints.add(constraintNode.getConstraint());
		}

		return constraints;
	}

	public static void compareConstraintNodes(ConstraintNode constraint1, ConstraintNode constraint2) {

		NameHelper.compareNames(constraint1.getName(), constraint2.getName());
		ConstraintHelper.compareConstraints(constraint1.getConstraint(), constraint2.getConstraint());
	}

	public static List<ConstraintNode> getMentioningConstraintNodes(AbstractParameterNode abstractParameterNode) {

		if (!abstractParameterNode.isGlobalParameter()) {
			return getMentioningParameterNodesIntr(abstractParameterNode);
		}

		Set<ConstraintNode> resultConstraintNodes = new HashSet<>();

		List<BasicParameterNode> globalBasicParameterNodes = 
				createListOfChildBasicParameterNodes(abstractParameterNode);

		List<MethodNode> methodNodes = MethodNodeHelper.findMentioningMethodNodes(abstractParameterNode);
		List<ConstraintNode> constraintNodes = MethodNodeHelper.getConstraints(methodNodes);

		for (ConstraintNode constraintNode : constraintNodes) {

			if (constraintNode.mentionsAnyOfParameters(globalBasicParameterNodes)) {
				resultConstraintNodes.add(constraintNode);
			}
		}

		return new ArrayList<>(resultConstraintNodes);
	}

	private static List<ConstraintNode> getMentioningParameterNodesIntr(AbstractParameterNode abstractParameterNode) {

		List<ConstraintNode> resultConstraintNodes = new ArrayList<>();

		List<BasicParameterNode> basicParameterNodes = 
				createListOfChildBasicParameterNodes(abstractParameterNode);

		List<ConstraintNode> constraintsFromParentStructures = 
				getConstraintsFromParentCompositesAndMethod(abstractParameterNode);

		for (ConstraintNode constraintNode : constraintsFromParentStructures) {

			if (constraintNode.mentionsAnyOfParameters(basicParameterNodes)) {
				resultConstraintNodes.add(constraintNode);
			}
		}

		return resultConstraintNodes;
	}

	private static List<BasicParameterNode> createListOfChildBasicParameterNodes(
			AbstractParameterNode abstractParameterNode) {

		List<BasicParameterNode> result = new ArrayList<>();

		if (abstractParameterNode instanceof BasicParameterNode) {
			result.add((BasicParameterNode) abstractParameterNode);
			return result;
		}

		CompositeParameterNode compositeParameterNode = (CompositeParameterNode) abstractParameterNode;

		List<BasicParameterNode> basicParameterNodes = 
				CompositeParameterNodeHelper.getChildBasicParameterNodes(compositeParameterNode);

		return basicParameterNodes;
	}

	public static List<ConstraintNode> getMentioningConstraintNodes(

			List<CompositeParameterNode> compositeParameterNodes,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		List<ConstraintNode> resultConstraintNodesToDelete = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParameterNodes) {

			List<ConstraintNode> currentConstraintNodes = 
					getMentioningConstraintsForCompositeParameter(compositeParameterNode, basicParameterNodesToDelete);

			resultConstraintNodesToDelete.addAll(currentConstraintNodes);
		}

		return resultConstraintNodesToDelete;
	}

	private static List<ConstraintNode> getMentioningConstraintsForCompositeParameter(
			CompositeParameterNode compositeParameterNode,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		if (compositeParameterNode.isGlobalParameter()) {
			return getMentioningConstraintsForGlobalParameter(compositeParameterNode, basicParameterNodesToDelete);
		}

		return getMentioningConstraintsForLocalParameter(compositeParameterNode, basicParameterNodesToDelete);
	}

	private static List<ConstraintNode> getMentioningConstraintsForGlobalParameter(
			CompositeParameterNode globalCompositeParameterNode,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		List<ConstraintNode> resultConstraintNodes = new ArrayList<>();

		List<CompositeParameterNode> linkedCompositeParameterNodes =
				CompositeParameterNodeHelper.getLinkedCompositeParameters(globalCompositeParameterNode);

		for (CompositeParameterNode compositeParameterNode : linkedCompositeParameterNodes) {

			List<ConstraintNode> currentConstraintNodes = 
					getMentioningConstraintsForLocalParameter(
							compositeParameterNode, basicParameterNodesToDelete);

			resultConstraintNodes.addAll(currentConstraintNodes);
		}

		return resultConstraintNodes;
	}

	private static List<ConstraintNode> getMentioningConstraintsForLocalParameter(
			CompositeParameterNode compositeParameterNode, 
			List<BasicParameterNode> basicParameterNodesToDelete) {

		List<ConstraintNode> resultConstraintNodesToDelete = new ArrayList<>();

		List<ConstraintNode> constraintsFromParentStructures = 
				getConstraintsFromParentCompositesAndMethod(compositeParameterNode);

		for (ConstraintNode constraintNode : constraintsFromParentStructures) {

			if (constraintNode.mentionsAnyOfParameters(basicParameterNodesToDelete)) {
				resultConstraintNodesToDelete.add(constraintNode);
			}
		}

		return resultConstraintNodesToDelete;
	}

	private static List<ConstraintNode> getConstraintsFromParentCompositesAndMethod(
			AbstractParameterNode compositeParameterNode) {

		List<ConstraintNode> resultConstraintNodes = new ArrayList<>();

		IAbstractNode parent = compositeParameterNode.getParent();

		if (!(parent instanceof IConstraintsParentNode)) {
			return new ArrayList<>();
		}

		for(;;) {

			IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) parent;

			List<ConstraintNode> constraintNodes = constraintsParentNode.getConstraintNodes();

			resultConstraintNodes.addAll(constraintNodes);

			parent = constraintsParentNode.getParent();

			if (parent == null || !(parent instanceof IConstraintsParentNode)) {
				return resultConstraintNodes;
			}
		}
	}

}
