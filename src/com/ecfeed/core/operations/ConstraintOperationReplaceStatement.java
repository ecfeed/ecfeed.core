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
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ConstraintOperationReplaceStatement extends AbstractModelOperation{

	private AbstractStatement fNewStatement;
	private AbstractStatement fCurrentStatement;
	private ConstraintNode fTarget;

	public ConstraintOperationReplaceStatement(
			ConstraintNode target, 
			AbstractStatement current, 
			AbstractStatement newStatement,
			IExtLanguageManager extLanguage) {
		
		super(OperationNames.REPLACE_STATEMENT, extLanguage);
		
		fTarget = target;
		fCurrentStatement = current;
		fNewStatement = newStatement;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);
		Constraint constraint = fTarget.getConstraint();

		if (constraint.getPremise() == fCurrentStatement) {
			constraint.setPremise(fNewStatement);
		}
		else if (constraint.getConsequence() == fCurrentStatement) {
			constraint.setConsequence(fNewStatement);
		}
		else {
			ModelOperationException.report(OperationMessages.TARGET_STATEMENT_NOT_FOUND_PROBLEM);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ConstraintOperationReplaceStatement(fTarget, fNewStatement, fCurrentStatement, getExtLanguage());
	}

}
