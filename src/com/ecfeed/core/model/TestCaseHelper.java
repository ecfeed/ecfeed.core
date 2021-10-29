/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;

import java.util.ArrayList;
import java.util.List;

public class TestCaseHelper {

	public boolean isTestCaseAmbiguous(
			TestCase testCase,
			List<Constraint> constraints,
			MessageStack messageStack,
			IExtLanguageManager extLanguageManager) {

		List<List<ChoiceNode>> testDomainForOneTestCase = createTestDomainWithOneTestCase(testCase);

		for (Constraint constraint : constraints) {

			if (constraint.isAmbiguous(testDomainForOneTestCase, messageStack, extLanguageManager)) {
				return true;
			}
		}

		return false;
	}

	private List<List<ChoiceNode>> createTestDomainWithOneTestCase(TestCase testCase) {

		List<List<ChoiceNode>> testDomainWithOneTestCase = new ArrayList<>();

		List<ChoiceNode> listOfChoices = testCase.getListOfChoiceNodes();

		for (ChoiceNode choiceNode : listOfChoices) {

			List<ChoiceNode> listWithOneChoice = new ArrayList<>();

			listWithOneChoice.add(choiceNode);

			testDomainWithOneTestCase.add(listWithOneChoice);
		}

		return testDomainWithOneTestCase;
	}

}
