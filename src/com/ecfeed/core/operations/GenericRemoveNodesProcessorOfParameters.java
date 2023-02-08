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
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
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

		Set<BasicParameterNode> basicParameters = selectedNodesByType.getBasicParameters();

		if (!basicParameters.isEmpty()) {
			processBasicParameters(basicParameters, inOutAffectedNodes);
		}

		Set<CompositeParameterNode> compositeParameters = selectedNodesByType.getCompositeParameters();

		if (!compositeParameters.isEmpty()) {

			List<CompositeParameterNode> compositeParametersList = 
					new ArrayList<CompositeParameterNode>(compositeParameters);

			processCompositeParameters(compositeParametersList, inOutAffectedNodes);
		}
	}

	private static void processCompositeParameters(
			List<CompositeParameterNode> compositeParameterNodes, 
			NodesByType inOutAffectedNodes) {

		List<BasicParameterNode> basicParameterNodesToDelete = 
				getBasicParameterNodesToDelete(compositeParameterNodes);

		List<ConstraintNode> constraintNodesToDelete = 
				getConstraintsToDelete(compositeParameterNodes, basicParameterNodesToDelete);

		List<TestCaseNode> testCaseNodesToDelete = 
				getTestCaseNodesToDelete(compositeParameterNodes);
		
		List<CompositeParameterNode> compositeParameterNodesToDelete = 
				getCompositeParameterNodesToDelete(compositeParameterNodes);

		inOutAffectedNodes.addConstraints(constraintNodesToDelete);
		inOutAffectedNodes.addTestCases(testCaseNodesToDelete);
		inOutAffectedNodes.addBasicParameters(basicParameterNodesToDelete);
		inOutAffectedNodes.addCompositeParameters(compositeParameterNodesToDelete);
		inOutAffectedNodes.addCompositeParameters(compositeParameterNodes);
	}

	private static List<CompositeParameterNode> getCompositeParameterNodesToDelete(
			List<CompositeParameterNode> compositeParameterNodes) {
		
		List<CompositeParameterNode> resultLinkedCompositeParameterNodes = new ArrayList<>();
		
		for (CompositeParameterNode compositeParameterNode : compositeParameterNodes) {
		
			List<CompositeParameterNode> linkedCompositeParameterNodes = 
					CompositeParameterNodeHelper.getLinkedCompositeParameters(compositeParameterNode);
			
			resultLinkedCompositeParameterNodes.addAll(linkedCompositeParameterNodes);
		}
		
		return resultLinkedCompositeParameterNodes;
	}

	private static List<BasicParameterNode> getBasicParameterNodesToDelete(
			List<CompositeParameterNode> compositeParametesNodes) {

		List<BasicParameterNode> basicParameterNodesToReturn = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParametesNodes) {

			List<BasicParameterNode> currentBasicParameterNodes = 
					CompositeParameterNodeHelper.getAllChildBasicParameters(compositeParameterNode);

			basicParameterNodesToReturn.addAll(currentBasicParameterNodes);
		}

		return basicParameterNodesToReturn;
	}

	private static List<ConstraintNode> getConstraintsToDelete(
			List<CompositeParameterNode> compositeParameterNodes,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		List<ConstraintNode> resultConstraintNodesToDelete = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParameterNodes) {

			List<ConstraintNode> currentConstraintNodes = 
					getConstraintsToDeleteForCompositeParameter(compositeParameterNode, basicParameterNodesToDelete);

			resultConstraintNodesToDelete.addAll(currentConstraintNodes);
		}

		return resultConstraintNodesToDelete;
	}

	private static List<ConstraintNode> getConstraintsToDeleteForCompositeParameter(
			CompositeParameterNode compositeParameterNode,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		if (compositeParameterNode.isGlobalParameter()) {
			return getConstraintsToDeleteForGlobalParameter(compositeParameterNode, basicParameterNodesToDelete);
		}

		return getConstraintsToDeleteForLocalParameter(compositeParameterNode, basicParameterNodesToDelete);
	}

	private static List<ConstraintNode> getConstraintsToDeleteForGlobalParameter(
			CompositeParameterNode globalCompositeParameterNode,
			List<BasicParameterNode> basicParameterNodesToDelete) {

		List<ConstraintNode> resultConstraintNodes = new ArrayList<>();

		List<CompositeParameterNode> linkedCompositeParameterNodes =
				CompositeParameterNodeHelper.getLinkedCompositeParameters(globalCompositeParameterNode);

		for (CompositeParameterNode compositeParameterNode : linkedCompositeParameterNodes) {

			List<ConstraintNode> currentConstraintNodes = 
					getConstraintsToDeleteForLocalParameter(
							compositeParameterNode, basicParameterNodesToDelete);

			resultConstraintNodes.addAll(currentConstraintNodes);
		}

		return resultConstraintNodes;
	}

	private static List<ConstraintNode> getConstraintsToDeleteForLocalParameter(
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

	private static List<TestCaseNode> getTestCaseNodesToDelete(
			List<CompositeParameterNode> compositeParameterNodes) {

		List<TestCaseNode> resultConstraintNodesToDelete = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeParameterNodes) {

			List<TestCaseNode> currentConstraintNodes = 
					getTestCasesToDeleteForDelete(compositeParameterNode);

			resultConstraintNodesToDelete.addAll(currentConstraintNodes);
		}

		return resultConstraintNodesToDelete;
	}

	private static List<TestCaseNode> getTestCasesToDeleteForDelete(
			CompositeParameterNode compositeParameterNode) {

		if (compositeParameterNode.isGlobalParameter()) {
			return getTestCasesToDeleteForGlobalParameter(compositeParameterNode);
		}

		return getTestCasesToDeleteForLocalParameter(compositeParameterNode);
	}

	private static List<TestCaseNode> getTestCasesToDeleteForGlobalParameter(
			CompositeParameterNode globalCompositeParameterNode) {

		List<TestCaseNode> resultTestCaseNodesToDelete = new ArrayList<>();

		List<CompositeParameterNode> linkedCompositeParameterNodes =
				CompositeParameterNodeHelper.getLinkedCompositeParameters(globalCompositeParameterNode);


		for (CompositeParameterNode linkedCompositeParameterNode : linkedCompositeParameterNodes) {

			List<TestCaseNode> testCases = 
					getTestCasesToDeleteForLocalParameter(linkedCompositeParameterNode);



			resultTestCaseNodesToDelete.addAll(testCases);
		}

		return resultTestCaseNodesToDelete;
	}

	private static List<TestCaseNode> getTestCasesToDeleteForLocalParameter(
			CompositeParameterNode compositeParameterNode) {

		MethodNode methodNode = MethodNodeHelper.findMethodNode(compositeParameterNode);

		return methodNode.getTestCases();
	}


	private static void processBasicParameters(
			Set<BasicParameterNode> basicParameters,
			NodesByType inOutAffectedNodes) {

		for (BasicParameterNode basicParameterNode : basicParameters) {

			accumulateAffectedConstraints(basicParameterNode, inOutAffectedNodes);
			accumulateAffectedTestCases(basicParameterNode, inOutAffectedNodes);

			inOutAffectedNodes.addNode(basicParameterNode);
		}
	}

	private static void accumulateAffectedTestCases(
			BasicParameterNode basicParameterNode,
			NodesByType inOutAffectedNodes) {

		if (basicParameterNode.isGlobalParameter()) {

			List<AbstractParameterNode> linkedParameters = 
					AbstractParameterNodeHelper.getLinkedParameters(basicParameterNode);

			for (AbstractParameterNode abstractParameterNode : linkedParameters) {

				MethodNode methodNode = MethodNodeHelper.findMethodNode(abstractParameterNode);

				inOutAffectedNodes.addTestCases(methodNode.getTestCases());
			}
		} else {

			MethodNode methodNode = MethodNodeHelper.findMethodNode(basicParameterNode);
			inOutAffectedNodes.addTestCases(methodNode.getTestCases());

		}
	}

	private static void accumulateAffectedConstraints(
			IAbstractNode abstractNode, NodesByType inOutAffectedNodes) {

		if (abstractNode instanceof ChoiceNode) {
			Set<ConstraintNode> mentioningConstraintNodes = 
					ChoiceNodeHelper.getMentioningConstraints((ChoiceNode) abstractNode);

			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
			return;
		} 

		if (abstractNode instanceof BasicParameterNode) {
			Set<ConstraintNode> mentioningConstraintNodes = 
					BasicParameterNodeHelper.getMentioningConstraints((BasicParameterNode) abstractNode);

			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
			return;
		}
	}

}
