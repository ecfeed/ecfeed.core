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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveInconsistentChildren;
import com.ecfeed.core.operations.nodes.OnTestCasesOperationRename;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class GenericMoveOperation extends CompositeOperation {

	public GenericMoveOperation(
			List<? extends IAbstractNode> nodesToBeMoved, 
			IAbstractNode newParent, 
			int newIndex,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.MOVE, true, newParent, getParent(nodesToBeMoved), extLanguageManager);

		try {
			addChildOperations(nodesToBeMoved, newParent, newIndex, extLanguageManager);

		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		setOneNodeToSelect(newParent);
	}

	private void addChildOperations(
			List<? extends IAbstractNode> nodesToBeMoved, 
			IAbstractNode newParent, 
			int newIndex,
			IExtLanguageManager extLanguageManager) throws Exception {

		if (allNodesHaveParentDifferentThan(newParent, nodesToBeMoved)) {

			addOperationsToMoveNodesBetweenParents(
					nodesToBeMoved, newParent, newIndex, extLanguageManager);

			return;
		} 

		if (allNodesHaveThisParent(newParent, nodesToBeMoved)) {

			addOperationsToMoveNodesUnderTheSameParent(
					nodesToBeMoved, newIndex, extLanguageManager);
			return;
		}

		ExceptionHelper.reportRuntimeException("Invalid parents of moved nodes.");
	}

	private void addOperationsToMoveNodesUnderTheSameParent(
			List<? extends IAbstractNode> nodesToBeMoved, 
			int newIndex,
			IExtLanguageManager extLanguageManager) {

		GenericShiftOperation operation = 
				FactoryShiftOperation.getShiftOperation(
						nodesToBeMoved, newIndex, extLanguageManager);

		addOperation(operation);
	}

	private void addOperationsToMoveNodesBetweenParents(
			List<? extends IAbstractNode> nodesToBeMoved,
			IAbstractNode newParent, 
			int newIndex, 
			IExtLanguageManager extLanguageManager) throws Exception {

		Set<MethodNode> methodsInvolved = new HashSet<>();

		for (IAbstractNode abstractNode : nodesToBeMoved) {

			abstractNode = addOperationsToMoveOneNodeBetweenParents(
					abstractNode, 
					newParent, 
					newIndex,
					methodsInvolved,
					extLanguageManager);
		}

		for (MethodNode method : methodsInvolved) {
			addOperation(new OnMethodOperationRemoveInconsistentChildren(method, extLanguageManager));
		}
	}

	private IAbstractNode addOperationsToMoveOneNodeBetweenParents(
			IAbstractNode nodeToMove, 
			IAbstractNode newParent,
			int newIndex,
			Set<MethodNode> inOutMethodsInvolved,
			IExtLanguageManager extLanguageManager) throws Exception {

		if (nodeToMove instanceof TestCaseNode && newParent instanceof TestSuiteNode) {

			processTestCaseNode((TestCaseNode) nodeToMove, (TestSuiteNode) newParent);
			return nodeToMove;
		}

		if (nodeToMove instanceof TestSuiteNode && newParent instanceof MethodNode) {

			processTestSuiteNode((TestSuiteNode) nodeToMove, (MethodNode) newParent);
			return nodeToMove;
		}

		if (nodeToMove instanceof IChoicesParentNode) {
			inOutMethodsInvolved.addAll(((IChoicesParentNode)nodeToMove).getParameter().getMethods());
		}

		addOperation((IModelOperation)nodeToMove.getParent().accept(
				new FactoryRemoveChildOperation(nodeToMove, false, extLanguageManager)));

		if ((nodeToMove instanceof BasicParameterNode && ((BasicParameterNode)nodeToMove).isGlobalParameter()) && newParent instanceof MethodNode){
			BasicParameterNode parameter = (BasicParameterNode)nodeToMove;
			nodeToMove = new BasicParameterNode(parameter, JavaLanguageHelper.getAdapter(parameter.getType()).getDefaultValue(), false, null);
		}

		if(newIndex != -1){
			addOperation(
					(IModelOperation)newParent.accept(
							new AddChildOperationCreator(nodeToMove, newIndex, false, extLanguageManager)));
		}
		else{
			addOperation(
					(IModelOperation)newParent.accept(
							new AddChildOperationCreator(nodeToMove, false, extLanguageManager)));
		}

		return nodeToMove;
	}

	private void processTestCaseNode(TestCaseNode node, TestSuiteNode newParent) throws Exception {

		Collection<TestCaseNode> element = new ArrayList<>();

		if (node.getParent() == newParent.getParent()) {
			element.add(node);
			addOperation(new OnTestCasesOperationRename(element, newParent.getSuiteName(), getExtLanguageManager()));
		} else {
			TestCaseNode nodeCopy = node.makeClone(Optional.empty());
			element.add(nodeCopy);

			addOperation(
					new OnTestCasesOperationRename(element, newParent.getSuiteName(), getExtLanguageManager()));

			addOperation(
					(IModelOperation)node.getParent().accept(
							new FactoryRemoveChildOperation(node, false, getExtLanguageManager())));

			addOperation((
					IModelOperation)newParent.getParent().accept(
							new AddChildOperationCreator(nodeCopy, false, getExtLanguageManager())));
		}
	}

	private void processTestSuiteNode(
			TestSuiteNode node, MethodNode newParent) throws Exception {

		if (node.getParent() == newParent) {
			return;
		} else {
			Collection<TestCaseNode> element = new ArrayList<>();

			TestSuiteNode nodeCopy = node.makeClone(Optional.empty());
			element.addAll(nodeCopy.getTestCaseNodes());

			addOperation(new OnTestCasesOperationRename(element, node.getSuiteName(), getExtLanguageManager()));

			addOperation((
					IModelOperation)node.getParent().accept(
							new FactoryRemoveChildOperation(node, false, getExtLanguageManager())));

			addOperation(
					(IModelOperation)newParent.accept(
							new AddChildOperationCreator(nodeCopy, false, getExtLanguageManager())));
		}
	}

	private static boolean allNodesHaveParentDifferentThan(
			IAbstractNode parent, List<? extends IAbstractNode> nodes) {

		for (IAbstractNode node : nodes) {

			if (node.getParent() == parent) {
				return false;
			}
		}

		return true;
	}

	private static boolean allNodesHaveThisParent(
			IAbstractNode parent, List<? extends IAbstractNode> nodes) {

		for (IAbstractNode node : nodes) {

			if (node.getParent() != parent) {
				return false;
			}
		}

		return true;
	}

	private static IAbstractNode getParent(List<? extends IAbstractNode> children) {
		return children.get(0).getParent();
	}
}
