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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.StringHelper;

public class MethodOperationAddTestCase extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestCaseNode fTestCaseNode;
	private int fIndex;
	private ITypeAdapterProvider fTypeAdapterProvider;

	public MethodOperationAddTestCase(
			MethodNode methodNode, 
			TestCaseNode testCaseNode, 
			ITypeAdapterProvider typeAdapterProvider, 
			int index,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_TEST_CASE, extLanguageManager);
		fMethodNode = methodNode;
		fTestCaseNode = testCaseNode;
		fIndex = index;
		fTypeAdapterProvider = typeAdapterProvider;
	}

	public MethodOperationAddTestCase(
			MethodNode target, 
			TestCaseNode testCase, 
			ITypeAdapterProvider typeAdapterProvider, 
			IExtLanguageManager extLanguageManager) {

		this(target, testCase, typeAdapterProvider, -1, extLanguageManager);
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fMethodNode);

		if (fIndex == -1) {
			fIndex = fMethodNode.getTestCases().size();
		}

		if (!testCaseNameOk(fTestCaseNode)) {
			ExceptionHelper.reportRuntimeException(OperationMessages.TEST_CASE_NOT_ALLOWED);
		}

		if (fTestCaseNode.correctTestCase(fMethodNode) == false) {
			ExceptionHelper.reportRuntimeException(OperationMessages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
		}

		fTestCaseNode.setParent(fMethodNode);

		for(ChoiceNode choice : fTestCaseNode.getTestData()) {

			//			BasicParameterNode parameter = fTestCaseNode.getBasicMethodParameter(choice);
			BasicParameterNode parameter = choice.getParameter();

			if (parameter.isExpected()) {

				String type = parameter.getType();

				ITypeAdapter<?> adapter = fTypeAdapterProvider.getAdapter(type);

				String newValue = 
						adapter.adapt(
								choice.getValueString(), 
								choice.isRandomizedValue(), 
								ERunMode.QUIET, 
								getExtLanguageManager());

				if (newValue == null) {
					ExceptionHelper.reportRuntimeException(OperationMessages.TEST_CASE_DATA_INCOMPATIBLE_WITH_METHOD);
				}
				
				choice.setValueString(newValue);
			}
		}

		fMethodNode.addTestCase(fTestCaseNode, fIndex);

		markModelUpdated();
	}

	private boolean testCaseNameOk(TestCaseNode testCaseNode) {

		String name = testCaseNode.getName();

		if (StringHelper.isNullOrEmpty(name)) {
			return true;
		}

		if (!name.matches(RegexHelper.REGEX_TEST_CASE_NODE_NAME)) {
			return false;
		}

		return true;
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnTestCaseOperationRemove(fMethodNode, fTestCaseNode, getExtLanguageManager());
	}

}
