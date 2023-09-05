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

import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.NodeNameHelper;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationMessages;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class OnTestCaseOperationAddToMethod extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestCaseNode fTestCaseNode;
	private int fIndex;
	private Optional<Integer> fIndexOfTestSuite;

	public OnTestCaseOperationAddToMethod(
			MethodNode methodNode, 
			TestCaseNode testCaseNode, 
			int index,
			Optional<Integer> indexOfTestSuite,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_TEST_CASE, extLanguageManager);
		fMethodNode = methodNode;
		fTestCaseNode = testCaseNode;
		fIndex = index;
		fIndexOfTestSuite = indexOfTestSuite;
	}

	public OnTestCaseOperationAddToMethod(
			MethodNode target, 
			TestCaseNode testCase, 
			IExtLanguageManager extLanguageManager) {

		this(target, testCase, -1, Optional.empty(), extLanguageManager);
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

				ITypeAdapter<?> adapter = JavaLanguageHelper.getTypeAdapter(type);

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

		fMethodNode.addTestCase(fTestCaseNode, fIndex, fIndexOfTestSuite);

		markModelUpdated();
	}

	private boolean testCaseNameOk(TestCaseNode testCaseNode) { // XYX remove ?? - r use java node name helper

		String name = testCaseNode.getName();

		if (StringHelper.isNullOrEmpty(name)) {
			return true;
		}

		if (!name.matches(NodeNameHelper.REGEX_TEST_CASE_NODE_NAME)) {
			return false;
		}

		return true;
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnTestCaseOperationRemove(fMethodNode, fTestCaseNode, getExtLanguageManager());
	}

}
