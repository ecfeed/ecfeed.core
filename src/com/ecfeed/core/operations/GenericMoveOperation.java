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
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveInconsistentChildren;
import com.ecfeed.core.operations.nodes.OnTestCasesOperationRename;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class GenericMoveOperation extends CompositeOperation {

	public GenericMoveOperation(
			List<? extends IAbstractNode> moved, 
			IAbstractNode newParent, 
			Optional<NodeMapper> nodeMapper,
			IExtLanguageManager extLanguageManager) {
		
		this(moved, newParent, -1, nodeMapper, extLanguageManager);
	}

	public GenericMoveOperation(
			List<? extends IAbstractNode> moved, 
			IAbstractNode newParent, 
			int newIndex,
			Optional<NodeMapper> nodeMapper,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.MOVE, true, newParent, getParent(moved), extLanguageManager);

		Set<MethodNode> methodsInvolved = new HashSet<>();
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(IAbstractNode node : moved){
					
					if (node instanceof TestCaseNode && newParent instanceof TestSuiteNode) {
						
						processTestCaseNode((TestCaseNode) node, (TestSuiteNode) newParent, nodeMapper);
						continue;
					}
					
					
					if (node instanceof TestSuiteNode && newParent instanceof MethodNode) {
						
						processTestSuiteNode((TestSuiteNode) node, (MethodNode) newParent, nodeMapper);
						continue;
					}
					
					if(node instanceof IChoicesParentNode){
						methodsInvolved.addAll(((IChoicesParentNode)node).getParameter().getMethods());
					}
					addOperation((IModelOperation)node.getParent().accept(
							new FactoryRemoveChildOperation(node, false, extLanguageManager)));

					if((node instanceof BasicParameterNode && ((BasicParameterNode)node).isGlobalParameter()) && newParent instanceof MethodNode){
						BasicParameterNode parameter = (BasicParameterNode)node;
						node = new BasicParameterNode(parameter, JavaLanguageHelper.getAdapter(parameter.getType()).getDefaultValue(), false, null);
					}
					
					if(newIndex != -1){
						addOperation(
								(IModelOperation)newParent.accept(
										new AddChildOperationCreator(node, newIndex, false, nodeMapper, extLanguageManager)));
					}
					else{
						addOperation(
								(IModelOperation)newParent.accept(
										new AddChildOperationCreator(node, false, nodeMapper, extLanguageManager)));
					}
					
					for(MethodNode method : methodsInvolved){
						addOperation(new OnMethodOperationRemoveInconsistentChildren(method, extLanguageManager));
					}
				}
			}
			else if(internalNodes(moved, newParent)){
				GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(moved, newIndex, extLanguageManager);
				addOperation(operation);
			}
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		setOneNodeToSelect(newParent);
	}
	
	private void processTestCaseNode(
			TestCaseNode node, 
			TestSuiteNode newParent,
			Optional<NodeMapper> nodeMapper) throws Exception {
		
			Collection<TestCaseNode> element = new ArrayList<>();
			
			if (node.getParent() == newParent.getParent()) {
				element.add(node);
				addOperation(new OnTestCasesOperationRename(element, newParent.getSuiteName(), getExtLanguageManager()));
			} else {
				TestCaseNode nodeCopy = node.makeClone(nodeMapper);
				element.add(nodeCopy);
				
				addOperation(
						new OnTestCasesOperationRename(element, newParent.getSuiteName(), getExtLanguageManager()));
				
				addOperation(
						(IModelOperation)node.getParent().accept(
								new FactoryRemoveChildOperation(node, false, getExtLanguageManager())));
				
				addOperation((
						IModelOperation)newParent.getParent().accept(
								new AddChildOperationCreator(nodeCopy, false, nodeMapper, getExtLanguageManager())));
			}
	}
	
	private void processTestSuiteNode(
			TestSuiteNode node, 
			MethodNode newParent,
			Optional<NodeMapper> nodeMapper) throws Exception {
			
			if (node.getParent() == newParent) {
				return;
			} else {
				Collection<TestCaseNode> element = new ArrayList<>();
				
				TestSuiteNode nodeCopy = node.makeClone(nodeMapper);
				element.addAll(nodeCopy.getTestCaseNodes());
				
				addOperation(new OnTestCasesOperationRename(element, node.getSuiteName(), getExtLanguageManager()));
				
				addOperation((
						IModelOperation)node.getParent().accept(
								new FactoryRemoveChildOperation(node, false, getExtLanguageManager())));
				
				addOperation(
						(IModelOperation)newParent.accept(
								new AddChildOperationCreator(nodeCopy, false, nodeMapper, getExtLanguageManager())));
			}
	}

	protected boolean externalNodes(List<? extends IAbstractNode> moved, IAbstractNode newParent){
		for(IAbstractNode node : moved){
			if(node.getParent() == newParent){
				return false;
			}
		}
		return true;
	}

	protected boolean internalNodes(List<? extends IAbstractNode> moved, IAbstractNode newParent){
		for(IAbstractNode node : moved){
			if(node.getParent() != newParent){
				return false;
			}
		}
		return true;
	}

	private static IAbstractNode getParent(List<? extends IAbstractNode> children) {
		return children.get(0).getParent();
	}
}
