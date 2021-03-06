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

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.utils.IExtLanguageManager;

public class StatementOperationAddStatement extends AbstractModelOperation {

	private AbstractStatement fStatement;
	private StatementArray fTarget;
	private int fIndex;

	public StatementOperationAddStatement(StatementArray parent, AbstractStatement statement, int index, IExtLanguageManager extLanguageManager) {
		super(OperationNames.ADD_STATEMENT, extLanguageManager);
		fTarget = parent;
		fStatement = statement;
		fIndex = index;
	}

	@Override
	public void execute() {
		fTarget.addStatement(fStatement, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new StatementOperationRemoveStatement(fTarget, fStatement, getExtLanguageManager());
	}

}
