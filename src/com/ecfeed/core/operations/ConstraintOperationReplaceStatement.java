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
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ConstraintOperationReplaceStatement extends AbstractModelOperation{

	private AbstractStatement fNewStatement;
	private AbstractStatement fCurrentStatement;
	private ConstraintNode fTarget;

	public ConstraintOperationReplaceStatement(
			ConstraintNode target, 
			AbstractStatement current, 
			AbstractStatement newStatement,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.REPLACE_STATEMENT, extLanguageManager);
		
		fTarget = target;
		fCurrentStatement = current;
		fNewStatement = newStatement;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fTarget);
		Constraint constraint = fTarget.getConstraint();

		if (constraint.getPrecondition() == fCurrentStatement) {
			constraint.setPrecondition(fNewStatement);
		}
		else if (constraint.getPostcondition() == fCurrentStatement) {
			constraint.setPostcondition(fNewStatement);
		}
		else {
			ExceptionHelper.reportRuntimeException(OperationMessages.TARGET_STATEMENT_NOT_FOUND_PROBLEM);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ConstraintOperationReplaceStatement(fTarget, fNewStatement, fCurrentStatement, getExtLanguageManager());
	}

}
