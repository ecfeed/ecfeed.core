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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExtLanguageManager;

public class GenericMoveOperation extends BulkOperation {

	public GenericMoveOperation(
			List<? extends AbstractNode> moved, 
			AbstractNode newParent, 
			ITypeAdapterProvider adapterProvider,
			ExtLanguageManager extLanguage) throws ModelOperationException {
		
		this(moved, newParent, adapterProvider, -1, extLanguage);
	}

	public GenericMoveOperation(
			List<? extends AbstractNode> moved, 
			AbstractNode newParent, 
			ITypeAdapterProvider adapterProvider, 
			int newIndex,
			ExtLanguageManager extLanguage) throws ModelOperationException {

		super(OperationNames.MOVE, true, newParent, getParent(moved), extLanguage);

		Set<MethodNode> methodsInvolved = new HashSet<>();
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(AbstractNode node : moved){
					if(node instanceof ChoicesParentNode){
						methodsInvolved.addAll(((ChoicesParentNode)node).getParameter().getMethods());
					}
					addOperation((IModelOperation)node.getParent().accept(
							new FactoryRemoveChildOperation(node, adapterProvider, false, extLanguage)));

					if(node instanceof GlobalParameterNode && newParent instanceof MethodNode){
						GlobalParameterNode parameter = (GlobalParameterNode)node;
						node = new MethodParameterNode(parameter, adapterProvider.getAdapter(parameter.getType()).getDefaultValue(), false);
					}
					if(newIndex != -1){
						addOperation(
								(IModelOperation)newParent.accept(
										new FactoryAddChildOperation(node, newIndex, adapterProvider, false, extLanguage)));
					}
					else{
						addOperation(
								(IModelOperation)newParent.accept(
										new FactoryAddChildOperation(node, adapterProvider, false, extLanguage)));
					}
					for(MethodNode method : methodsInvolved){
						addOperation(new MethodOperationMakeConsistent(method, extLanguage));
					}
				}
			}
			else if(internalNodes(moved, newParent)){
				GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(moved, newIndex, extLanguage);
				addOperation(operation);
			}
		} catch (Exception e) {
			ModelOperationException.report(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		setOneNodeToSelect(newParent);
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
