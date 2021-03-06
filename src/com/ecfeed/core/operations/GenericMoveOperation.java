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
import java.util.Set;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericMoveOperation extends BulkOperation {

	public GenericMoveOperation(
			List<? extends AbstractNode> moved, 
			AbstractNode newParent, 
			ITypeAdapterProvider adapterProvider,
			IExtLanguageManager extLanguageManager) {
		
		this(moved, newParent, adapterProvider, -1, extLanguageManager);
	}

	public GenericMoveOperation(
			List<? extends AbstractNode> moved, 
			AbstractNode newParent, 
			ITypeAdapterProvider adapterProvider, 
			int newIndex,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.MOVE, true, newParent, getParent(moved), extLanguageManager);

		Set<MethodNode> methodsInvolved = new HashSet<>();
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(AbstractNode node : moved){
					
					if (node instanceof TestCaseNode && newParent instanceof TestSuiteNode) {
						
						processTestCaseNode((TestCaseNode) node, (TestSuiteNode) newParent, adapterProvider);
						continue;
					}
					
					
					if (node instanceof TestSuiteNode && newParent instanceof MethodNode) {
						
						processTestSuiteNode((TestSuiteNode) node, (MethodNode) newParent, adapterProvider);
						continue;
					}
					
					if(node instanceof ChoicesParentNode){
						methodsInvolved.addAll(((ChoicesParentNode)node).getParameter().getMethods());
					}
					addOperation((IModelOperation)node.getParent().accept(
							new FactoryRemoveChildOperation(node, adapterProvider, false, extLanguageManager)));

					if(node instanceof GlobalParameterNode && newParent instanceof MethodNode){
						GlobalParameterNode parameter = (GlobalParameterNode)node;
						node = new MethodParameterNode(parameter, adapterProvider.getAdapter(parameter.getType()).getDefaultValue(), false);
					}
					
					if(newIndex != -1){
						addOperation(
								(IModelOperation)newParent.accept(
										new FactoryAddChildOperation(node, newIndex, adapterProvider, false, extLanguageManager)));
					}
					else{
						addOperation(
								(IModelOperation)newParent.accept(
										new FactoryAddChildOperation(node, adapterProvider, false, extLanguageManager)));
					}
					
					for(MethodNode method : methodsInvolved){
						addOperation(new MethodOperationMakeConsistent(method, extLanguageManager));
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
	
	private void processTestCaseNode(TestCaseNode node, TestSuiteNode newParent, ITypeAdapterProvider adapterProvider) throws Exception {
			Collection<TestCaseNode> element = new ArrayList<>();
			
			if (node.getParent() == newParent.getParent()) {
				element.add(node);
				addOperation(new MethodOperationRenameTestCases(element, newParent.getSuiteName(), getExtLanguageManager()));
			} else {
				TestCaseNode nodeCopy = node.makeClone();
				element.add(nodeCopy);
				
				addOperation(new MethodOperationRenameTestCases(element, newParent.getSuiteName(), getExtLanguageManager()));
				addOperation((IModelOperation)node.getParent().accept(new FactoryRemoveChildOperation(node, adapterProvider, false, getExtLanguageManager())));
				addOperation((IModelOperation)newParent.getParent().accept(new FactoryAddChildOperation(nodeCopy, adapterProvider, false, getExtLanguageManager())));
			}
	}
	
	private void processTestSuiteNode(TestSuiteNode node, MethodNode newParent, ITypeAdapterProvider adapterProvider) throws Exception {
			
			if (node.getParent() == newParent) {
				return;
			} else {
				Collection<TestCaseNode> element = new ArrayList<>();
				
				TestSuiteNode nodeCopy = node.makeClone();
				element.addAll(nodeCopy.getTestCaseNodes());
				
				addOperation(new MethodOperationRenameTestCases(element, node.getSuiteName(), getExtLanguageManager()));
				addOperation((IModelOperation)node.getParent().accept(new FactoryRemoveChildOperation(node, adapterProvider, false, getExtLanguageManager())));
				addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(nodeCopy, adapterProvider, false, getExtLanguageManager())));
			}
	}

	protected boolean externalNodes(List<? extends AbstractNode> moved, AbstractNode newParent){
		for(AbstractNode node : moved){
			if(node.getParent() == newParent){
				return false;
			}
		}
		return true;
	}

	protected boolean internalNodes(List<? extends AbstractNode> moved, AbstractNode newParent){
		for(AbstractNode node : moved){
			if(node.getParent() != newParent){
				return false;
			}
		}
		return true;
	}

	private static AbstractNode getParent(List<? extends AbstractNode> children) {
		return children.get(0).getParent();
	}
}
