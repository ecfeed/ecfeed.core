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
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.ParametersParentNodeHelper;
import com.ecfeed.core.operations.GenericOperationAddParameter;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnCompositeOperationAdd extends GenericOperationAddParameter {

	IParametersParentNode fIParametersParentNode;
	AbstractParameterNode fParameterNode;
	private boolean fValidate;
	private int fNewIndex;

	public OnCompositeOperationAdd(
			IParametersParentNode parametersParentNode,
			AbstractParameterNode abstractParameterNode,
			int index,
			boolean validate, 			
			IExtLanguageManager extLanguageManager) {

		super(parametersParentNode, abstractParameterNode, index, true, extLanguageManager);

		fIParametersParentNode = parametersParentNode;
		fParameterNode = abstractParameterNode;
		fNewIndex = index != -1 ? index : parametersParentNode.getParameters().size();
	}

	public OnCompositeOperationAdd(
			IParametersParentNode parametersParentNode,
			AbstractParameterNode abstractParameterNode,
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		this(parametersParentNode, abstractParameterNode, -1, validate, extLanguageManager);
	}

	@Override
	public void execute() {

		IExtLanguageManager extLanguageManager = getExtLanguageManager();

		List<String> parameterTypesInExtLanguage = 
				ParametersParentNodeHelper.getParameterTypes(fIParametersParentNode, extLanguageManager);

		String newParameterType = AbstractParameterNodeHelper.getType(fParameterNode, extLanguageManager);

		parameterTypesInExtLanguage.add(fNewIndex, newParameterType);

		super.execute();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnCompositeParameterOperationRemove(
				fIParametersParentNode, fParameterNode, fValidate, getExtLanguageManager());
	}

}
