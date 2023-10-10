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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecfeed.core.utils.BooleanHolder;

public class TestCaseParentNodeHelper {

	public static void addTestCaseToMethod(ITestCasesParentNode testCasesParentNode, ChoiceNode choiceNode) {

		List<ChoiceNode> listOfChoicesForTestCase = new ArrayList<ChoiceNode>();
		listOfChoicesForTestCase.add(choiceNode);

		TestCaseNode testCaseNode = new TestCaseNode("name", null, listOfChoicesForTestCase);
		testCasesParentNode.addTestCase(testCaseNode);
	}

	public static void removeInconsistentTestCases(MethodNode methodNode, BooleanHolder modelUpdated) {
		Iterator<TestCaseNode> tcIt = methodNode.getTestCases().iterator();
		while (tcIt.hasNext()) {
			if (tcIt.next().isConsistent() == false) {
				tcIt.remove();
				Boolean trueBl = true;
				modelUpdated.set(trueBl);
			}
		}
	}

}
