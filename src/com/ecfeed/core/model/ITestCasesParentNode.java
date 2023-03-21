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

import java.util.List;

public interface ITestCasesParentNode extends IAbstractNode {
	
	public List<TestCaseNode> getTestCases();
	public void replaceTestCases(List<TestCaseNode> testCases);
	public void removeAllTestCases();
	public void addTestCase(TestCaseNode testCase);
}
