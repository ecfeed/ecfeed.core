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

import java.util.List;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCase;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;

public class MethodOperationAddTestSuite extends BulkOperation {

	public MethodOperationAddTestSuite(
			MethodNode methodNode, 
			String testSuiteName, 
			List<TestCase> testCases, 
			ITypeAdapterProvider typeAdapterProvider) {

		super(OperationNames.ADD_TEST_SUITES, false, methodNode, methodNode);
		createTestSuites(testCases, testSuiteName, methodNode, typeAdapterProvider);
		
	}
	
	public MethodOperationAddTestSuite(
			MethodNode methodNode, 
			List<TestCaseNode> testCases, 
			ITypeAdapterProvider typeAdapterProvider) {

		super(OperationNames.ADD_TEST_SUITES, false, methodNode, methodNode);
		createTestSuites(testCases, methodNode, typeAdapterProvider);
		
	}
	
	private void createTestSuites(List<TestCase> testCases, String testSuiteName, MethodNode methodNode, ITypeAdapterProvider typeAdapterProvider) {
		
		for (TestCase testCase : testCases) {

			TestCaseNode testCaseNode = 
					new TestCaseNode(
							testSuiteName, 
							methodNode.getModelChangeRegistrator(), 
							testCase.getListOfChoiceNodes());

			addOperation(
					new MethodOperationAddTestCase(
							methodNode, 
							testCaseNode, 
							typeAdapterProvider));
		}
	}
	
	private void createTestSuites(List<TestCaseNode> testCases, MethodNode methodNode, ITypeAdapterProvider typeAdapterProvider) {
		
		for (TestCaseNode testCase : testCases) {

			addOperation(
					new MethodOperationAddTestCase(
							methodNode, 
							testCase, 
							typeAdapterProvider));
		}
	}

}
