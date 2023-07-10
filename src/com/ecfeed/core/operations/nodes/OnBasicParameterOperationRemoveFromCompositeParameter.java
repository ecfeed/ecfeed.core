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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnBasicParameterOperationRemoveFromCompositeParameter extends AbstractModelOperation {

	private static final String REMOVE_BASIC_PARAMETER = "Remove basic parameter";

	private CompositeParameterNode fParentCompositeParameter;
	private BasicParameterNode fBasicParameterNode;
	private int fParameterIndex;
	
	private MethodNode fMethodNode;

	private List<TestCaseNode> fOriginalTestCases;
	private List<BasicParameterNode> fOriginalDeployedParameters;

	public OnBasicParameterOperationRemoveFromCompositeParameter(
			CompositeParameterNode parentCompositeParameter,
			BasicParameterNode basicParameterNode,
			IExtLanguageManager extLanguageManager) {

		super(REMOVE_BASIC_PARAMETER, extLanguageManager);

		fParentCompositeParameter = parentCompositeParameter;
		fBasicParameterNode = basicParameterNode;
		
		fMethodNode = MethodNodeHelper.findMethodNode(basicParameterNode);
		
		if (fMethodNode == null) {
			ExceptionHelper.reportRuntimeException("Method node not found.");
		}

		fOriginalTestCases = new ArrayList<>();
		fOriginalDeployedParameters = new ArrayList<>();
	}

	@Override
	public void execute() {

		fParameterIndex = fBasicParameterNode.getMyIndex();

		fOriginalTestCases.clear();
		fOriginalTestCases.addAll(fMethodNode.getTestCases());
		fMethodNode.removeAllTestCases();

		fOriginalDeployedParameters.clear();
		fOriginalDeployedParameters.addAll(fMethodNode.getDeployedParameters());
		fMethodNode.removeAllDeployedParameters();

		fParentCompositeParameter.removeParameter(fBasicParameterNode);

		markModelUpdated();
	}

	@Override
	public String toString() {
		return createDescription(fBasicParameterNode.getName());
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OperationAddParameter(getExtLanguageManager());
	}

	private class OperationAddParameter extends AbstractModelOperation {

		public OperationAddParameter(IExtLanguageManager extLanguageManager) {
			super(AbstractModelOperation.createReverseOperationName(
					REMOVE_BASIC_PARAMETER), extLanguageManager);
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fParentCompositeParameter);

			OnParameterOperationAddToParent onParameterOperationAddToParent = 
					new OnParameterOperationAddToParent(
							fParentCompositeParameter,
							fBasicParameterNode,
							fParameterIndex,
							getExtLanguageManager());

			onParameterOperationAddToParent.execute();

			fMethodNode.setTestCases(fOriginalTestCases);
			fMethodNode.setDeployedParameters(fOriginalDeployedParameters);

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}

	}

}


