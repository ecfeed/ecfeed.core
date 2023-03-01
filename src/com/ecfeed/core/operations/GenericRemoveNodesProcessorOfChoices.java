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

import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.CompositeParameterNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesProcessorOfChoices {

	public static void processChoices(Set<ChoiceNode> choiceNodes, NodesByType inOutAffectedNodes) {

		for (ChoiceNode choiceNode : choiceNodes) {

			processChoice(choiceNode, inOutAffectedNodes);
		}
	}

	private static void processChoice(ChoiceNode choiceNode, NodesByType inOutAffectedNodes) {

		List<ConstraintNode> calculatedConstraintNodesToDelete = 
				ChoiceNodeHelper.getMentioningConstraints(choiceNode);

		List<TestCaseNode> calculatedTestCaseNodesToDelete = 
				calculateTestCasesToDelete(choiceNode);  

		inOutAffectedNodes.addConstraints(calculatedConstraintNodesToDelete);
		inOutAffectedNodes.addTestCases(calculatedTestCaseNodesToDelete);

		inOutAffectedNodes.addNode(choiceNode);
	}

	private static List<TestCaseNode> calculateTestCasesToDelete(ChoiceNode choiceNode) { // TODO MO-RE to choice node helper - getMentioningTestCases

		if (choiceNode.isPartOfGlobalParameter()) {
			return calculateTestCasesToDeleteForGlobalChoiceNode(choiceNode);
		}

		return calculateTestCasesToDeleteForLocalNode(choiceNode);
	}

	private static List<TestCaseNode> calculateTestCasesToDeleteForGlobalChoiceNode(ChoiceNode globalChoiceNode) {

		CompositeParameterNode compositeParameterNode = AbstractParameterNodeHelper.getTopComposite(globalChoiceNode);

		if (compositeParameterNode != null) {
			return calculateTestCasesForChoiceOfGlobalComposite(compositeParameterNode);
		}

		BasicParameterNode basicParameterNode = BasicParameterNodeHelper.findBasicParameter(globalChoiceNode);

		if (basicParameterNode != null) {
			return calculateTestCasesForChoiceOfGlobalBasicParameter(basicParameterNode);
		}

		return new ArrayList<>();
	}

	private static List<TestCaseNode> calculateTestCasesForChoiceOfGlobalBasicParameter(
			BasicParameterNode basicParameterNode) {

		List<TestCaseNode> resultTestCaseNodesToDelete = new ArrayList<>();

		List<BasicParameterNode> linkedBasicParameterNodes =
				BasicParameterNodeHelper.getLinkedBasicParameters(basicParameterNode);


		for (BasicParameterNode linkedBasicParameterNode : linkedBasicParameterNodes) {

			List<TestCaseNode> testCases = 
					calculateTestCasesToDeleteForLocalNode(linkedBasicParameterNode);

			resultTestCaseNodesToDelete.addAll(testCases);
		}

		return resultTestCaseNodesToDelete;
	}

	private static List<TestCaseNode> calculateTestCasesForChoiceOfGlobalComposite(
			CompositeParameterNode compositeParameterNode) {

		List<TestCaseNode> resultTestCaseNodesToDelete = new ArrayList<>();

		List<CompositeParameterNode> linkedCompositeParameterNodes =
				CompositeParameterNodeHelper.getLinkedCompositeParameters(compositeParameterNode);


		for (CompositeParameterNode linkedCompositeParameterNode : linkedCompositeParameterNodes) {

			List<TestCaseNode> testCases = 
					calculateTestCasesToDeleteForLocalNode(linkedCompositeParameterNode);

			resultTestCaseNodesToDelete.addAll(testCases);
		}

		return resultTestCaseNodesToDelete;
	}

	private static List<TestCaseNode> calculateTestCasesToDeleteForLocalNode(IAbstractNode abstractNode) {

		MethodNode methodNode = MethodNodeHelper.findMethodNode(abstractNode);

		return methodNode.getTestCases();
	}

	//	private static void accumulateAffectedConstraints(
	//			IAbstractNode abstractNode, NodesByType inOutAffectedNodes) {
	//
	//		if (abstractNode instanceof ChoiceNode) {
	//
	//			List<ConstraintNode> mentioningConstraintNodes = 
	//					ChoiceNodeHelper.getMentioningConstraints((ChoiceNode) abstractNode);
	//
	//			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
	//			return;
	//		} 
	//
	//		if (abstractNode instanceof BasicParameterNode) {
	//
	//			List<ConstraintNode> mentioningConstraintNodes = 
	//					BasicParameterNodeHelper.getMentioningConstraints((BasicParameterNode) abstractNode);
	//
	//			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
	//			return;
	//		}
	//	}
	//	
	//	private static void accumulateAffectedTestCases(
	//			ChoiceNode choiceNode, NodesByType inOutAffectedNodes) {
	//
	//		Set<TestCaseNode> mentioningTestCaseNodes = 
	//				ChoiceNodeHelper.getMentioningTestCases(choiceNode);
	//
	//		inOutAffectedNodes.addTestCases(mentioningTestCaseNodes);
	//	}

}
