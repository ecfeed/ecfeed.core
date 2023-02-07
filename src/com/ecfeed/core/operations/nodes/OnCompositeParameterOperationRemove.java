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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.GenericOperationRemoveParameter;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnCompositeParameterOperationRemove extends CompositeOperation { // TODO MO-RE change to AbstractModelOperation

	private IParametersParentNode fParentNode; 
	private AbstractParameterNode fAbstractParameterNode; 
	private boolean fValidate;

	private int fParametersIndex;
	private String fParameterName;

	public OnCompositeParameterOperationRemove(
			IParametersParentNode parentNode, 
			AbstractParameterNode abstractParameterNode, 
			boolean validate, // TODO MO-RE remove ?
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_PARAMETER, true, parentNode, parentNode, extLanguageManager);

		fParentNode = parentNode;
		fAbstractParameterNode = abstractParameterNode;
		fValidate = validate;

		fParameterName = abstractParameterNode.getName();

		addOperation(
				new RemoveCompositeParameterOperationPrivate(
						parentNode, abstractParameterNode, extLanguageManager));
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

			fParametersIndex = fAbstractParameterNode.getMyIndex();

			fParentNode.removeParameter(fAbstractParameterNode);

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation(){
			return new OnCompositeOperationAdd(
					fParentNode, fAbstractParameterNode, fParametersIndex, fValidate, getExtLanguageManager());
		}

	}

}
