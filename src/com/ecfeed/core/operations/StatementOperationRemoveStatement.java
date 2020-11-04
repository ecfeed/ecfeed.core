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

public class StatementOperationRemoveStatement extends AbstractModelOperation {

	private StatementArray fTarget;
	private AbstractStatement fStatement;
	private int fIndex;

	public StatementOperationRemoveStatement(StatementArray target, AbstractStatement statement, IExtLanguageManager extLanguageManager){
		super(OperationNames.REMOVE_STATEMENT, extLanguageManager);
		fTarget = target;
		fStatement = statement;
		fIndex = target.getChildren().indexOf(statement);
	}

	@Override
	public void execute() {
		fTarget.removeChild(fStatement);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new StatementOperationAddStatement(fTarget, fStatement, fIndex, getExtLanguageManager());
	}

}
