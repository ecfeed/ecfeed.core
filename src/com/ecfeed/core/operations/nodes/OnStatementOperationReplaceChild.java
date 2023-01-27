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

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationMessages;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnStatementOperationReplaceChild extends AbstractModelOperation {

	private AbstractStatement fNewChild;
	private AbstractStatement fCurrentChild;
	private StatementArray fTarget;

	public OnStatementOperationReplaceChild(StatementArray target, AbstractStatement child, AbstractStatement newStatement, IExtLanguageManager extLanguageManager) {
		super(OperationNames.REPLACE_STATEMENT, extLanguageManager);
		fTarget = target;
		fCurrentChild = child;
		fNewChild = newStatement;
	}

	@Override
	public void execute() {
		if(fTarget == null){
			ExceptionHelper.reportRuntimeException(OperationMessages.NULL_POINTER_TARGET);
		}
		fTarget.replaceChild(fCurrentChild, fNewChild);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnStatementOperationReplaceChild(fTarget, fNewChild, fCurrentChild, getExtLanguageManager());
	}

}
