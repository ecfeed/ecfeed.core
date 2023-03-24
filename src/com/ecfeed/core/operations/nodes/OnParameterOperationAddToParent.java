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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterSignatureHelper;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.ITestCasesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.ParametersParentNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.GenericOperationAddParameter;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnParameterOperationAddToParent extends GenericOperationAddParameter {

	List<TestCaseNode> fRemovedTestCases;
	IParametersParentNode fIParametersParentNode;
	ITestCasesParentNode fTestCasesParentNode;
	AbstractParameterNode fParameterNode;
	private int fNewIndex;

	public OnParameterOperationAddToParent(
			IParametersParentNode parametersParentNode,
			AbstractParameterNode abstractParameterNode,
			int index,
			IExtLanguageManager extLanguageManager) {

		super(parametersParentNode, abstractParameterNode, index, true, extLanguageManager);

		fIParametersParentNode = parametersParentNode;
		fParameterNode = abstractParameterNode;
		fNewIndex = index != -1 ? index : parametersParentNode.getParameters().size();

		fTestCasesParentNode = MethodNodeHelper.findMethodNode(parametersParentNode);
		fRemovedTestCases = getCurrentTestCases();
	}

	private ArrayList<TestCaseNode> getCurrentTestCases() {

		if (fTestCasesParentNode == null) {
			return new ArrayList<>();
		}

		return new ArrayList<TestCaseNode>(fTestCasesParentNode.getTestCases());
	}

	public OnParameterOperationAddToParent(
			MethodNode target,
			AbstractParameterNode parameter,
			IExtLanguageManager extLanguageManager) {

		this(target, parameter, -1, extLanguageManager);
	}

	@Override
	public void execute() {

		IExtLanguageManager extLanguageManager = getExtLanguageManager();

		List<String> parameterTypesInExtLanguage = ParametersParentNodeHelper.getParameterTypes(fIParametersParentNode, extLanguageManager);

		String newParameterType = AbstractParameterSignatureHelper.createSignatureOfParameterTypeNewStandard(fParameterNode, extLanguageManager);

		parameterTypesInExtLanguage.add(fNewIndex, newParameterType);

		if (fTestCasesParentNode != null) {
			fTestCasesParentNode.removeAllTestCases();
		}
		super.execute();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodReverseOperation(getExtLanguageManager());
	}

	private class MethodReverseOperation extends ReverseOperation{

		public MethodReverseOperation(IExtLanguageManager extLanguageManager) {
			super(fIParametersParentNode, fParameterNode, extLanguageManager);
		}

		@Override
		public void execute() {
			fTestCasesParentNode.replaceTestCases(fRemovedTestCases);
			super.execute();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}

	}

}
