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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
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

		if (fAffectedNodesByType.getTestSuiteNodes().size() > 0) {
			ExceptionHelper.reportRuntimeException("Test suites not expected.");
		}
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

		GenericRemoveNodesProcessorOfParameters.processParameters(selectedNodesByType, inOutAffectedNodes);

		processChoices(selectedNodesByType,	inOutAffectedNodes);
	}

	private static void processChoices(
			NodesByType selectedNodesByType, 
			NodesByType inOutAffectedNodes) {

		Set<ChoiceNode> choiceNodes = selectedNodesByType.getChoices();

		if (!choiceNodes.isEmpty()) {
			GenericRemoveNodesProcessorOfChoices.processChoices(choiceNodes, inOutAffectedNodes);
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

	private static void processTestSuites(Set<TestSuiteNode> testSuiteNodes, NodesByType inOutAffectedNodes) {

		for (TestSuiteNode testSuiteNode : testSuiteNodes) {
			addTestCasesOfTestSuite(testSuiteNode, inOutAffectedNodes);
		}
	}

	private static void addTestCasesOfTestSuite(TestSuiteNode testSuiteNode, NodesByType inOutAffectedNodes) {

		List<TestCaseNode> testCaseNodes = testSuiteNode.getTestCaseNodes();

		for (TestCaseNode testCaseNode : testCaseNodes) {
			inOutAffectedNodes.addNode(testCaseNode);
		}
	}

	private static void processTestCases(Set<TestCaseNode> testCaseNodes, NodesByType inOutAffectedNodes) {

		for (TestCaseNode testCaseNode : testCaseNodes) {
			inOutAffectedNodes.addNode(testCaseNode);
		}
	}


	private static void processClasses(Set<ClassNode> classsNodes, NodesByType outAffectedNodesByType) {

		for (ClassNode classNode : classsNodes) {
			outAffectedNodesByType.addNode(classNode);
		}
	}

}
