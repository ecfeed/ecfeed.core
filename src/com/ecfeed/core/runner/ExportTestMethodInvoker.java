/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.runner;

import static com.ecfeed.core.utils.CommonConstants.DEFAULT_NEW_TEST_SUITE_NAME;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;

public class ExportTestMethodInvoker implements ITestMethodInvoker {

	MethodNode fMethodNode;
	List<AbstractParameterNode> fMethodParameters;
	ArrayList<TestCaseNode> fTestCaseNodes;

	@Override
	public boolean isClassInstanceRequired() {
		return false;	
	}

	public ExportTestMethodInvoker(MethodNode methodNode) {
		fMethodNode = methodNode;
		fTestCaseNodes = new ArrayList<TestCaseNode>();
		fMethodParameters = fMethodNode.getMethodParameters();
	}

	@Override
	public void invoke(
			Method testMethod, 
			String className, 
			Object instance,
			Object[] arguments, 
			Object[] choiceNames,
			String argumentsDescription) throws RuntimeException {

		fTestCaseNodes.add(createTestCase(arguments));
	}

	private TestCaseNode createTestCase(Object[] arguments) {
		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();

		for (int cnt = 0; cnt < fMethodNode.getParametersCount(); ++cnt) {
			
			AbstractParameterNode methodParameterNode = fMethodParameters.get(cnt);

			Object argument = arguments[cnt];

			if (argument == null) {
				final String message = "Argument " + cnt + " must not be null when creating a test case.";
				ExceptionHelper.reportRuntimeException(message);
			}

			ChoiceNode choiceNode = 
					new ChoiceNode(
							methodParameterNode.getName(), arguments[cnt].toString(), 
							methodParameterNode.getModelChangeRegistrator());
			
			choiceNodes.add(choiceNode);
		}

		TestCaseNode testCaseNode = 
				new TestCaseNode(
						DEFAULT_NEW_TEST_SUITE_NAME, fMethodNode.getModelChangeRegistrator(), choiceNodes);
		
		testCaseNode.setParent(fMethodNode);

		return testCaseNode;
	}

	public Collection<TestCaseNode> getTestCasesToExport() {
		return fTestCaseNodes; 
	}

}