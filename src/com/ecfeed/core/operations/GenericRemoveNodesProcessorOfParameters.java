/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.CompositeParameterNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IConstraintsParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesProcessorOfParameters {

	public static void processParameters(
			NodesByType selectedNodesByType, NodesByType inOutAffectedNodes) {

		List<BasicParameterNode> basicParameters = selectedNodesByType.getListOfBasicParameters();

		if (!basicParameters.isEmpty()) {
			processBasicParameters(basicParameters, inOutAffectedNodes);
		}

		List<CompositeParameterNode> compositeParameters = 
				selectedNodesByType.getListOfCompositeParameters();

		if (!compositeParameters.isEmpty()) {
			processCompositeParameters(compositeParameters, inOutAffectedNodes);
		}
	}

	private static void processBasicParameters(
			List<BasicParameterNode> basicParameterNodesToDelete,
			NodesByType inOutAffectedNodes) {

		List<ConstraintNode> calculatedConstraintNodesToDelete = 
				BasicParameterNodeHelper.getMentioningConstraints(basicParameterNodesToDelete);

		List<TestCaseNode> calculatedTestCaseNodesToDelete = 
				calculateTestCaseNodesToDelete(basicParameterNodesToDelete, 0);

		List<BasicParameterNode> calculatedBasicParameterNodesToDelete = 
				calculateBasicParameterNodesToDelete(basicParameterNodesToDelete, 0);

		inOutAffectedNodes.addConstraints(calculatedConstraintNodesToDelete);
		inOutAffectedNodes.addTestCases(calculatedTestCaseNodesToDelete);
		inOutAffectedNodes.addBasicParameters(calculatedBasicParameterNodesToDelete);
		inOutAffectedNodes.addBasicParameters(basicParameterNodesToDelete);
	}

	private static void processCompositeParameters(
			List<CompositeParameterNode> compositeParameterNodesToDelete, 
			NodesByType inOutAffectedNodes) {

		List<BasicParameterNode> calculatedBasicParameterNodesToDelete = 
				calculateBasicParameterNodesToDelete(compositeParameterNodesToDelete); // TODO MO-RE getMentioningBasicParameterNodes, to helper

		List<ConstraintNode> calculatedConstraintNodesToDelete = 
				calculateConstraintsToDelete(compositeParameterNodesToDelete, calculatedBasicParameterNodesToDelete);  // TODO MO-RE getMentioningConstraintNodes, to helper

		List<TestCaseNode> calculatedTestCaseNodesToDelete = 
				calculateTestCaseNodesToDelete(compositeParameterNodesToDelete);

		List<CompositeParameterNode> calculatedCompositeParameterNodesToDelete = 
				calculateCompositeParameterNodesToDelete(compositeParameterNodesToDelete);  // TODO MO-RE getMentioningCompositeParameterNodes, to helper

		inOutAffectedNodes.addConstraints(calculatedConstraintNodesToDelete);
		inOutAffectedNodes.addTestCases(calculatedTestCaseNodesToDelete);
		inOutAffectedNodes.addBasicParameters(calculatedBasicParameterNodesToDelete);
		inOutAffectedNodes.addCompositeParameters(calculatedCompositeParameterNodesToDelete);
		inOutAffectedNodes.addCompositeParameters(compositeParameterNodesToDelete);
	}

	private static List<CompositeParameterNode> calculateCompositeParameterNodesToDelete(
			List<CompositeParameterNode> compositeParameterNodes) {

		List<CompositeParameterNode> resultLinkedCompositeParameterNodes = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParameterNodes) {

			List<CompositeParameterNode> linkedCompositeParameterNodes = 
					CompositeParameterNodeHelper.getLinkedCompositeParameters(compositeParameterNode);

			resultLinkedCompositeParameterNodes.addAll(linkedCompositeParameterNodes);
		}

		return resultLinkedCompositeParameterNodes;
	}

	private static List<BasicParameterNode> calculateBasicParameterNodesToDelete(
			List<BasicParameterNode> basicParameterNodes, int dummy) {

		List<BasicParameterNode> resultBasicParameterNodes = new ArrayList<>();

		for (BasicParameterNode basicParameterNode : basicParameterNodes) {

			List<BasicParameterNode> currentBasicParameterNodes = 
					calculateBasicParameterNodesToDelete(basicParameterNode);

			resultBasicParameterNodes.addAll(currentBasicParameterNodes);
		}

		return resultBasicParameterNodes;
	}

	private static List<BasicParameterNode> calculateBasicParameterNodesToDelete(
			BasicParameterNode basicParameterNode) {

		List<BasicParameterNode> resultBasicParameterNodes = 
				BasicParameterNodeHelper.getLinkedBasicParameters(basicParameterNode);

		return resultBasicParameterNodes;
	}

	private static List<BasicParameterNode> calculateBasicParameterNodesToDelete(
			List<CompositeParameterNode> compositeParametesNodes) {

		List<BasicParameterNode> basicParameterNodesToReturn = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParametesNodes) {

			List<BasicParameterNode> currentBasicParameterNodes = 
					CompositeParameterNodeHelper.getAllChildBasicParameters(compositeParameterNode);

			basicParameterNodesToReturn.addAll(currentBasicParameterNodes);
		}

		return basicParameterNodesToReturn;
	}

	private static List<ConstraintNode> calculateConstraintsToDelete(
			List<CompositeParameterNode> compositeParameterNodes,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		List<ConstraintNode> resultConstraintNodesToDelete = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParameterNodes) {

			List<ConstraintNode> currentConstraintNodes = 
					calculateConstraintsToDeleteForCompositeParameter(compositeParameterNode, basicParameterNodesToDelete);

			resultConstraintNodesToDelete.addAll(currentConstraintNodes);
		}

		return resultConstraintNodesToDelete;
	}

	private static List<ConstraintNode> calculateConstraintsToDeleteForCompositeParameter(
			CompositeParameterNode compositeParameterNode,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		if (compositeParameterNode.isGlobalParameter()) {
			return calculateConstraintsToDeleteForGlobalParameter(compositeParameterNode, basicParameterNodesToDelete);
		}

		return calculateConstraintsToDeleteForLocalParameter(compositeParameterNode, basicParameterNodesToDelete);
	}

	private static List<ConstraintNode> calculateConstraintsToDeleteForGlobalParameter(
			CompositeParameterNode globalCompositeParameterNode,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		List<ConstraintNode> resultConstraintNodes = new ArrayList<>();

		List<CompositeParameterNode> linkedCompositeParameterNodes =
				CompositeParameterNodeHelper.getLinkedCompositeParameters(globalCompositeParameterNode);

		for (CompositeParameterNode compositeParameterNode : linkedCompositeParameterNodes) {

			List<ConstraintNode> currentConstraintNodes = 
					calculateConstraintsToDeleteForLocalParameter(
							compositeParameterNode, basicParameterNodesToDelete);

			resultConstraintNodes.addAll(currentConstraintNodes);
		}

		return resultConstraintNodes;
	}

	private static List<ConstraintNode> calculateConstraintsToDeleteForLocalParameter(
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
			CompositeParameterNode compositeParameterNode) {

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

	private static List<TestCaseNode> calculateTestCaseNodesToDelete(
			List<CompositeParameterNode> compositeParameterNodes) {

		List<TestCaseNode> resultConstraintNodesToDelete = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParameterNodes) {

			List<TestCaseNode> currentConstraintNodes = 
					calculateTestCasesToDeleteForDelete(compositeParameterNode);

			resultConstraintNodesToDelete.addAll(currentConstraintNodes);
		}

		return resultConstraintNodesToDelete;
	}

	private static List<TestCaseNode> calculateTestCaseNodesToDelete(
			List<BasicParameterNode> basicParameterNodes, int dummy) {

		List<TestCaseNode> resultTestCaseNodesToDelete = new ArrayList<>();

		for (BasicParameterNode basicParameterNode : basicParameterNodes) {

			List<TestCaseNode> currentTestCaseNodes = 
					calculateTestCasesToDeleteForSingleParameter(basicParameterNode);

			resultTestCaseNodesToDelete.addAll(currentTestCaseNodes);
		}

		return resultTestCaseNodesToDelete;
	}

	private static List<TestCaseNode> calculateTestCasesToDeleteForDelete(
			CompositeParameterNode compositeParameterNode) {

		if (compositeParameterNode.isGlobalParameter()) {
			return calculateTestCasesToDeleteForGlobalParameter(compositeParameterNode);
		}

		return calculateTestCasesToDeleteForLocalParameter(compositeParameterNode);
	}

	private static List<TestCaseNode> calculateTestCasesToDeleteForSingleParameter(
			BasicParameterNode basicParameterNode) {

		List<TestCaseNode> resultTestCaseNodes = new ArrayList<>();

		List<MethodNode> mentionininMethods = BasicParameterNodeHelper.getMentioningMethodNodes(basicParameterNode);

		for (MethodNode methodNode : mentionininMethods) {
			resultTestCaseNodes.addAll(methodNode.getTestCases());
		}

		return resultTestCaseNodes;
	}

	private static List<TestCaseNode> calculateTestCasesToDeleteForGlobalParameter(
			CompositeParameterNode globalCompositeParameterNode) {

		List<TestCaseNode> resultTestCaseNodesToDelete = new ArrayList<>();

		List<CompositeParameterNode> linkedCompositeParameterNodes =
				CompositeParameterNodeHelper.getLinkedCompositeParameters(globalCompositeParameterNode);


		for (CompositeParameterNode linkedCompositeParameterNode : linkedCompositeParameterNodes) {

			List<TestCaseNode> testCases = 
					calculateTestCasesToDeleteForLocalParameter(linkedCompositeParameterNode);



			resultTestCaseNodesToDelete.addAll(testCases);
		}

		return resultTestCaseNodesToDelete;
	}

	private static List<TestCaseNode> calculateTestCasesToDeleteForLocalParameter(
			CompositeParameterNode compositeParameterNode) {

		MethodNode methodNode = MethodNodeHelper.findMethodNode(compositeParameterNode);

		return methodNode.getTestCases();
	}


	//	private static void accumulateAffectedTestCases(
	//			BasicParameterNode basicParameterNode,
	//			NodesByType inOutAffectedNodes) {
	//
	//		if (basicParameterNode.isGlobalParameter()) {
	//
	//			List<AbstractParameterNode> linkedParameters = 
	//					AbstractParameterNodeHelper.getLinkedParameters(basicParameterNode);
	//
	//			for (AbstractParameterNode abstractParameterNode : linkedParameters) {
	//
	//				MethodNode methodNode = MethodNodeHelper.findMethodNode(abstractParameterNode);
	//
	//				inOutAffectedNodes.addTestCases(methodNode.getTestCases());
	//			}
	//		} else {
	//
	//			MethodNode methodNode = MethodNodeHelper.findMethodNode(basicParameterNode);
	//			inOutAffectedNodes.addTestCases(methodNode.getTestCases());
	//
	//		}
	//	}

	//	private static void accumulateAffectedConstraints(
	//			IAbstractNode abstractNode, NodesByType inOutAffectedNodes) {
	//
	//		if (abstractNode instanceof ChoiceNode) {
	//			Set<ConstraintNode> mentioningConstraintNodes = 
	//					ChoiceNodeHelper.getMentioningConstraints((ChoiceNode) abstractNode);
	//
	//			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
	//			return;
	//		} 
	//
	//		if (abstractNode instanceof BasicParameterNode) {
	//			Set<ConstraintNode> mentioningConstraintNodes = 
	//					BasicParameterNodeHelper.getMentioningConstraints((BasicParameterNode) abstractNode);
	//
	//			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
	//			return;
	//		}
	//	}

}
