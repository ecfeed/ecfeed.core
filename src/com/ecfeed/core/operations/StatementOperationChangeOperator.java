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

import com.ecfeed.core.model.StatementArrayOperator;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.utils.IExtLanguageManager;

public class StatementOperationChangeOperator extends AbstractModelOperation {

	private StatementArray fTarget;
	private StatementArrayOperator fNewOperator;
	private StatementArrayOperator fCurrentOperator;

	public StatementOperationChangeOperator(StatementArray target, StatementArrayOperator operator, IExtLanguageManager extLanguageManager) {
		super(OperationNames.CHANGE_STATEMENT_OPERATOR, extLanguageManager);
		fTarget = target;
		fNewOperator = operator;
		fCurrentOperator = target.getOperator();
	}

	@Override
	public void execute() {
		fTarget.setOperator(fNewOperator);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new StatementOperationChangeOperator(fTarget, fCurrentOperator, getExtLanguageManager());
	}

}
