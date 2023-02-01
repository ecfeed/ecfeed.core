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

import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnTestCaseOperatopRename extends AbstractModelOperation {

	private TestCaseNode fTestCaseNode;
	private String fNewName;
	private String fOrginalName;

	public OnTestCaseOperatopRename(
			TestCaseNode testCaseNode,
			String newName,
			IExtLanguageManager extLanguageManager) { // XYX need this ?

		super(OperationNames.RENAME, extLanguageManager);

		fTestCaseNode = testCaseNode;
		fNewName = newName;
	}

	@Override
	public void execute() {

		
		setOneNodeToSelect(fTestCaseNode);
		
		fOrginalName = fTestCaseNode.getName();
		fTestCaseNode.setName(fNewName);
		
		

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnTestCaseOperatopRename(
				fTestCaseNode, 
				fOrginalName,
				getExtLanguageManager());
	}

}
