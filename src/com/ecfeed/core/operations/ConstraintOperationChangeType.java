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

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ConstraintOperationChangeType extends AbstractModelOperation {

	private ConstraintNode fCurrentConstraintNode;
	private ConstraintType fNewConstraintType;
	private IExtLanguageManager fExtLanguageManager;

	private Constraint fInitialConstraintCopy;

	public ConstraintOperationChangeType(
			ConstraintNode constraintNode,
			ConstraintType newConstraintType,
			IExtLanguageManager extLanguageManager) {
		super(OperationNames.CHANGE_CONSTRAINT_TYPE, extLanguageManager);
		fCurrentConstraintNode = constraintNode;
		fNewConstraintType = newConstraintType;
		fExtLanguageManager = extLanguageManager;

		fInitialConstraintCopy = fCurrentConstraintNode.getConstraint().getCopy();
	}

	@Override
	public void execute() {

		Constraint constraint = fCurrentConstraintNode.getConstraint();

		if (constraint.getConstraintType() == null) {
			ExceptionHelper.reportRuntimeException("Constraint type not set.");
		}

		AbstractStatement newPrecondition = new StaticStatement(true, null);

		constraint.setPrecondition(newPrecondition);
		constraint.setConstratintType(fNewConstraintType);

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {

		return new ReverseOperationChangeType(fExtLanguageManager);
	}

	private class ReverseOperationChangeType extends AbstractModelOperation {

		public ReverseOperationChangeType(IExtLanguageManager extLanguageManager) {
			super(OperationNames.CHANGE_CONSTRAINT_TYPE, extLanguageManager);
		}

		@Override
		public void execute() {

			Constraint constraint = fCurrentConstraintNode.getConstraint();

			final AbstractStatement initialPrecondition = fInitialConstraintCopy.getPrecondition();

			constraint.setPrecondition(initialPrecondition);
			constraint.setConstratintType(fInitialConstraintCopy.getConstraintType());

			markModelUpdated();

		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}
	}

}
