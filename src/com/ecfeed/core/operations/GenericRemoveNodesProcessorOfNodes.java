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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.CompositeParameterNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNodeHelper;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesProcessorOfNodes {

	private final Set<IAbstractNode> fSelectedNodes;
	private NodesByType fAffectedNodesByType;

	public GenericRemoveNodesProcessorOfNodes(
			Set<IAbstractNode> selectedNodes, 
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fSelectedNodes = selectedNodes;
		fAffectedNodesByType = new NodesByType();

		removeNodesWithAncestorsOnList(fSelectedNodes);

		processNodes(
				fSelectedNodes, 
				fAffectedNodesByType,
				extLanguageManager, 
				validate);
	}

	public NodesByType getProcessedNodes() {
		return fAffectedNodesByType;
	}

	public Set<ConstraintNode> getAffectedConstraints() {
		return fAffectedNodesByType.getConstraints();
	}

	public Set<TestCaseNode> getAffectedTestCases() {
		return fAffectedNodesByType.getTestCaseNodes();
	}

	private void removeNodesWithAncestorsOnList(Set<IAbstractNode> selectedNodes) {

		Iterator<IAbstractNode> iterator = selectedNodes.iterator();

		while (iterator.hasNext()) {

			IAbstractNode currentNode = iterator.next();

			List<IAbstractNode> ancestors = currentNode.getAncestors();

			for (IAbstractNode ancestor : ancestors) {

				if (selectedNodes.contains(ancestor)) {

					// node is deleted because ancestor will be remove with the whole sub-tree which includes current node 
					iterator.remove(); 
					break;
				}
			}
		}
	}

	private static void processNodes(
			Set<IAbstractNode> selectedNodes, 
			NodesByType outAffectedNodes,
			IExtLanguageManager extLanguageManager, boolean validate) {

		NodesByType selectedNodesByType = new NodesByType(selectedNodes);

		processClassesAndMethods(selectedNodesByType, outAffectedNodes);

		processParametersAndChoices(selectedNodesByType, outAffectedNodes);

		processTestSuitesAndTestCases(selectedNodesByType, outAffectedNodes);

		processConstraints(selectedNodesByType, outAffectedNodes);
	}

	private static void processClassesAndMethods(
			NodesByType selectedNodesByType, 
			NodesByType outAffectedNodes) {

		Set<ClassNode> classNodes = selectedNodesByType.getClasses();

		if (!classNodes.isEmpty()) {
			processClasses(classNodes, outAffectedNodes);
		}

		Set<MethodNode> methods = selectedNodesByType.getMethods();

		if (!methods.isEmpty()) {
			processMethods(methods, outAffectedNodes);
		}
	}

	private static void processTestSuitesAndTestCases(
			NodesByType selectedNodesByType, NodesByType outAffectedNodes) {

		Set<TestSuiteNode> testSuiteNodes = selectedNodesByType.getTestSuiteNodes();

		if (!testSuiteNodes.isEmpty()) {
			processTestSuites(testSuiteNodes, outAffectedNodes);
		}

		Set<TestCaseNode> testCaseNodes = selectedNodesByType.getTestCaseNodes();

		if (!testCaseNodes.isEmpty()) {
			processTestCases(testCaseNodes, outAffectedNodes);
		}
	}

	private static void processParametersAndChoices(NodesByType selectedNodesByType, NodesByType inOutAffectedNodes) {

		processParameters(selectedNodesByType, inOutAffectedNodes);

		processChoices(selectedNodesByType,	inOutAffectedNodes);
	}

	private static void processChoices(
			NodesByType selectedNodesByType, 
			NodesByType inOutAffectedNodes) {

		Set<ChoiceNode> choiceNodes = selectedNodesByType.getChoices();

		if (!choiceNodes.isEmpty()) {
			processChoicesFilteringConstraintsAndTestCases(
					choiceNodes, 
					inOutAffectedNodes);
		}
	}

	private static void processParameters(
			NodesByType selectedNodesByType, NodesByType inOutAffectedNodes) {

		Set<BasicParameterNode> basicParameters = selectedNodesByType.getBasicParameters();

		if (!basicParameters.isEmpty()) {
			processBasicParameters(basicParameters, inOutAffectedNodes);
		}

		Set<CompositeParameterNode> compositeParameters = selectedNodesByType.getCompositeParameters();

		if (!compositeParameters.isEmpty()) {
			processCompositeParameters(compositeParameters, inOutAffectedNodes);
		}

	}

	private static void processConstraints(NodesByType selectedNodesByType, NodesByType outAffectedNodes) {

		Set<ConstraintNode> constraints = selectedNodesByType.getConstraints();

		if (!constraints.isEmpty()) {
			processConstraintsIntr(constraints, outAffectedNodes);
		}
	}

	private static void processConstraintsIntr(Set<ConstraintNode> constraintNodes, NodesByType inOutAffectedNodes) {

		for (ConstraintNode constraint : constraintNodes) {
			inOutAffectedNodes.addNode(constraint);
		}
	}

	private static void processMethods(Set<MethodNode> methods, NodesByType inOutAffectedNodes) {

		Iterator<MethodNode> methodItr = methods.iterator();

		while (methodItr.hasNext()) {
			MethodNode method = methodItr.next();
			inOutAffectedNodes.addNode(method);
		}
	}

	private static void processCompositeParameters(
			Set<CompositeParameterNode> compositeParameters, 
			NodesByType inOutAffectedNodes) {

		processLocalBasicChildrenOfCompositeParameters(compositeParameters,	inOutAffectedNodes);

		processLocalCompositeParameters(compositeParameters, inOutAffectedNodes);
	}

	private static void processLocalBasicChildrenOfCompositeParameters(
			Set<CompositeParameterNode> compositeParameters,
			NodesByType inOutAffectedNodes) {

		for (CompositeParameterNode compositeParameterNode : compositeParameters) {
			processAllChildBasicParametersOfCompositeParameter(compositeParameterNode, inOutAffectedNodes);
		}
	}

	private static void processAllChildBasicParametersOfCompositeParameter(
			CompositeParameterNode compositeParameterNode,
			NodesByType inOutAffectedNodes) {

		List<BasicParameterNode> basicParameterNodes = 
				CompositeParameterNodeHelper.getAllChildBasicParameters(compositeParameterNode);

		for (BasicParameterNode basicParameterNode : basicParameterNodes) {
			inOutAffectedNodes.addNode(basicParameterNode);
		}
	}

	private static void processLocalCompositeParameters(
			Set<CompositeParameterNode> compositeParameters,
			NodesByType inOutAffectedNodes) {

		for (CompositeParameterNode compositeParameterNode : compositeParameters) {

			if (!compositeParameterNode.isGlobalParameter()) {
				inOutAffectedNodes.addNode(compositeParameterNode);
			}
		}
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
					GlobalParameterNodeHelper.getLinkedParameters(basicParameterNode);

			for (AbstractParameterNode abstractParameterNode : linkedParameters) {

				MethodNode methodNode = MethodNodeHelper.findMethodNode(abstractParameterNode);

				inOutAffectedNodes.addTestCases(methodNode.getTestCases());
			}
		} else {

			MethodNode methodNode = MethodNodeHelper.findMethodNode(basicParameterNode);
			inOutAffectedNodes.addTestCases(methodNode.getTestCases());

		}
	}

	private static void processTestSuites(Set<TestSuiteNode> testSuiteNodes, NodesByType inOutAffectedNodes) {
		
		for (TestSuiteNode testSuiteNode : testSuiteNodes) {
			inOutAffectedNodes.addNode(testSuiteNode);
		}
	}

	private static void processTestCases(Set<TestCaseNode> testCaseNodes, NodesByType inOutAffectedNodes) {

		for (TestCaseNode testCaseNode : testCaseNodes) {
			inOutAffectedNodes.addNode(testCaseNode);
		}
	}

	private static void processChoicesFilteringConstraintsAndTestCases(
			Set<ChoiceNode> choiceNodes, 
			NodesByType inOutAffectedNodes) {

		for (ChoiceNode choiceNode : choiceNodes) {

			accumulateAffectedConstraints(choiceNode, inOutAffectedNodes);

			accumulateAffectedTestCases(choiceNode, inOutAffectedNodes);

			inOutAffectedNodes.addNode(choiceNode);
		}
	}

	private static void processClasses(Set<ClassNode> classsNodes, NodesByType outAffectedNodesByType) {

		for (ClassNode classNode : classsNodes) {
			outAffectedNodesByType.addNode(classNode);
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

	private static void accumulateAffectedTestCases(
			ChoiceNode choiceNode, NodesByType inOutAffectedNodes) {

		Set<TestCaseNode> mentioningTestCaseNodes = 
				ChoiceNodeHelper.getMentioningTestCases(choiceNode);

		inOutAffectedNodes.addTestCases(mentioningTestCaseNodes);
	}

}
