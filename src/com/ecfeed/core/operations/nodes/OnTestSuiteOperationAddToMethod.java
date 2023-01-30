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

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCase;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnTestSuiteOperationAddToMethod extends CompositeOperation {

	public OnTestSuiteOperationAddToMethod(
			MethodNode methodNode, 
			String testSuiteName, 
			List<TestCase> testCases, 
			ITypeAdapterProvider typeAdapterProvider,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_TEST_SUITES, false, methodNode, methodNode, extLanguageManager);
		createTestSuites(testCases, testSuiteName, methodNode, typeAdapterProvider);
	}

	public OnTestSuiteOperationAddToMethod(
			MethodNode methodNode, 
			List<TestCaseNode> testCases, 
			ITypeAdapterProvider typeAdapterProvider,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_TEST_SUITES, false, methodNode, methodNode, extLanguageManager);
		createAndAddTestCaseOperations(testCases, methodNode, typeAdapterProvider);
	}

	private void createTestSuites(
			List<TestCase> testCases, 
			String testSuiteName, 
			MethodNode methodNode, 
			ITypeAdapterProvider typeAdapterProvider) {

		for (TestCase testCase : testCases) {

			TestCaseNode testCaseNode = 
					new TestCaseNode(
							testSuiteName, 
							methodNode.getModelChangeRegistrator(), 
							testCase.getListOfChoiceNodes());

			addOperation(
					new OnTestCaseOperationAddToMethod(
							methodNode, 
							testCaseNode, 
							typeAdapterProvider,
							getExtLanguageManager()));
		}
	}

	private void createAndAddTestCaseOperations(List<TestCaseNode> testCases, MethodNode methodNode, ITypeAdapterProvider typeAdapterProvider) {

		for (TestCaseNode testCase : testCases) {

			addOperation(
					new OnTestCaseOperationAddToMethod(
							methodNode, 
							testCase, 
							typeAdapterProvider,
							getExtLanguageManager()));
		}
	}

}