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

import com.ecfeed.core.model.IStatementCondition;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnStatementOperationSetCondition extends AbstractModelOperation {

	private RelationStatement fTarget;
	private IStatementCondition fCurrentCondition;
	private IStatementCondition fNewCondition;

	public OnStatementOperationSetCondition(RelationStatement target, IStatementCondition condition, IExtLanguageManager extLanguageManager) {
		super(OperationNames.SET_STATEMENT_CONDITION, extLanguageManager);
		fTarget = target;
		fNewCondition = condition;
		fCurrentCondition = target.getCondition();
	}

	@Override
	public void execute() {
		fTarget.setCondition(fNewCondition);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnStatementOperationSetCondition(fTarget, fCurrentCondition, getExtLanguageManager());
	}

}
