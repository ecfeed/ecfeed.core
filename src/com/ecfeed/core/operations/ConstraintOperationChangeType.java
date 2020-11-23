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

		fInitialConstraintCopy = fCurrentConstraintNode.getConstraint().makeClone();
	}

	@Override
	public void execute() {

		Constraint constraint = fCurrentConstraintNode.getConstraint();

		final ConstraintType constraintType = constraint.getType();

		if (constraintType == null) {
			ExceptionHelper.reportRuntimeException("Constraint type not set.");
		}

		if (constraintType == ConstraintType.INVARIANT && fNewConstraintType == ConstraintType.IMPLICATION) {
			constraint.setType(fNewConstraintType);
			markModelUpdated();
			return;
		}

		constraint.setType(fNewConstraintType);

		AbstractStatement newPrecondition = new StaticStatement(true, null);
		constraint.setPrecondition(newPrecondition);

		constraint.verify();

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

			constraint.setType(fInitialConstraintCopy.getType());

			constraint.setPrecondition(fInitialConstraintCopy.getPrecondition());
			constraint.setPostcondition(fInitialConstraintCopy.getPostcondition());

			constraint.verify();

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}
	}

}
