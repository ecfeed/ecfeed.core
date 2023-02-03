/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.GlobalParameterNodeHelper;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.GenericOperationRemoveParameter;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnCompositeParameterOperationRemove extends CompositeOperation{

	private IParametersParentNode fParentNode; 
	private AbstractParameterNode fAbstractParameterNode; 
	private boolean fValidate;

	private int fParametersIndex;
	private String fParameterName;

	public OnCompositeParameterOperationRemove(
			IParametersParentNode parentNode, 
			AbstractParameterNode abstractParameterNode, 
			boolean validate, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_PARAMETER, true, parentNode, parentNode, extLanguageManager);

		fParentNode = parentNode;
		fAbstractParameterNode = abstractParameterNode;
		fValidate = validate;

		fParametersIndex = abstractParameterNode.getMyIndex();
		fParameterName = abstractParameterNode.getName();

		addOperation(
				new RemoveCompositeParameterOperationPrivate(
						parentNode, abstractParameterNode, extLanguageManager));

		if (!validate) {
			return;
		}

		if (!abstractParameterNode.isGlobalParameter()) {
			addOperationsForLocalParameter(abstractParameterNode);
			return;
		}

		addOperationsForGlobalParameter(parentNode, abstractParameterNode);
	}

	private void addOperationsForLocalParameter(AbstractParameterNode abstractParameterNode) {

		MethodNode methodNode = MethodNodeHelper.findMethodNode(abstractParameterNode);

		if (methodNode != null) {
			addOperation(new OnMethodOperationRemoveInconsistentChildren(methodNode, getExtLanguageManager()));
		}
	}

	private void addOperationsForGlobalParameter(
			IParametersParentNode parentNode, 
			AbstractParameterNode parameter) {

		List<CompositeParameterNode> compositeLocalParameters = 
				GlobalParameterNodeHelper.getLocalCompositeParametersLinkedToGlobal(
						(CompositeParameterNode) parameter);

		addOperationsForRemovingLocalCompositeParameters(compositeLocalParameters, getExtLanguageManager());

		List<MethodNode> linkedMethodNodes = 
				GlobalParameterNodeHelper.getMethodsForCompositeParameters(compositeLocalParameters);

		addOperationsForRemovingInconsistentChildrenFromMethods(linkedMethodNodes, getExtLanguageManager());
	}

	private void addOperationsForRemovingInconsistentChildrenFromMethods(
			List<MethodNode> linkedMethodNodes,
			IExtLanguageManager extLanguageManager) {

		for (MethodNode methodNode : linkedMethodNodes) {
			addOperation(new OnMethodOperationRemoveInconsistentChildren(methodNode, extLanguageManager));
		}
	}

	private void addOperationsForRemovingLocalCompositeParameters(
			List<CompositeParameterNode> compositeLocalParameters,
			IExtLanguageManager extLanguageManager) {

		for (CompositeParameterNode compositeLocalParameterNode : compositeLocalParameters) {
			addOperation(
					new RemoveCompositeParameterOperationPrivate(
							compositeLocalParameterNode.getParent(), compositeLocalParameterNode, extLanguageManager));
		}
	}

	@Override
	public String toString() {

		return "Operation remove parameter:" + fParameterName;
	}

	private class RemoveCompositeParameterOperationPrivate extends GenericOperationRemoveParameter{

		public RemoveCompositeParameterOperationPrivate(
				IParametersParentNode parentNode,
				AbstractParameterNode parameter,
				IExtLanguageManager extLanguageManager) {

			super(parentNode, parameter, extLanguageManager);
		}

		@Override
		public void execute() {

			super.execute();
		}

		@Override
		public IModelOperation getReverseOperation(){
			return new OnCompositeOperationAdd(
					fParentNode, fAbstractParameterNode, fParametersIndex, fValidate, getExtLanguageManager());
		}

	}

}
