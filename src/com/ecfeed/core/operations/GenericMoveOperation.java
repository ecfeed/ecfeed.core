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

import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericMoveOperation extends CompositeOperation {

	public GenericMoveOperation(
			List<? extends IAbstractNode> moved, 
			IAbstractNode newParent, 
			ITypeAdapterProvider adapterProvider,
			IExtLanguageManager extLanguageManager) {
		
		this(moved, newParent, adapterProvider, -1, extLanguageManager);
	}

	public GenericMoveOperation(
			List<? extends IAbstractNode> moved, 
			IAbstractNode newParent, 
			ITypeAdapterProvider adapterProvider, 
			int newIndex,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.MOVE, true, newParent, getParent(moved), extLanguageManager);

		Set<MethodNode> methodsInvolved = new HashSet<>();
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(IAbstractNode node : moved){
					
					if (node instanceof TestCaseNode && newParent instanceof TestSuiteNode) {
						
						processTestCaseNode((TestCaseNode) node, (TestSuiteNode) newParent, adapterProvider);
						continue;
					}
					
					
					if (node instanceof TestSuiteNode && newParent instanceof MethodNode) {
						
						processTestSuiteNode((TestSuiteNode) node, (MethodNode) newParent, adapterProvider);
						continue;
					}
					
					if(node instanceof IChoicesParentNode){
						methodsInvolved.addAll(((IChoicesParentNode)node).getParameter().getMethods());
					}
					addOperation((IModelOperation)node.getParent().accept(
							new FactoryRemoveChildOperation(node, adapterProvider, false, extLanguageManager)));

					if((node instanceof BasicParameterNode && ((BasicParameterNode)node).isGlobalParameter()) && newParent instanceof MethodNode){
						BasicParameterNode parameter = (BasicParameterNode)node;
						node = new BasicParameterNode(parameter, adapterProvider.getAdapter(parameter.getType()).getDefaultValue(), false);
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
						addOperation(new MethodOperationRemoveInconsistentChildren(method, extLanguageManager));
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
				addOperation(new OnTestCasesOperationRemove(element, newParent.getSuiteName(), getExtLanguageManager()));
			} else {
				TestCaseNode nodeCopy = node.makeClone();
				element.add(nodeCopy);
				
				addOperation(new OnTestCasesOperationRemove(element, newParent.getSuiteName(), getExtLanguageManager()));
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
				
				addOperation(new OnTestCasesOperationRemove(element, node.getSuiteName(), getExtLanguageManager()));
				addOperation((IModelOperation)node.getParent().accept(new FactoryRemoveChildOperation(node, adapterProvider, false, getExtLanguageManager())));
				addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(nodeCopy, adapterProvider, false, getExtLanguageManager())));
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
