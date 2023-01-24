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

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IParametersAndConstraintsParentNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnConstraintOperationRemove extends AbstractModelOperation {

	private IParametersAndConstraintsParentNode fParentNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public OnConstraintOperationRemove(
			IParametersAndConstraintsParentNode parentNode,
			ConstraintNode constraint, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_CONSTRAINT, extLanguageManager);

		fParentNode = parentNode;
		fConstraint = constraint;
		fIndex = fConstraint.getMyIndex();
	}

	@Override
	public String toString() {

		return "Remove constraint: " + fConstraint.getName();
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fParentNode);

		fIndex = fConstraint.getMyIndex();
		fParentNode.removeConstraint(fConstraint);

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {

		return new OnConstraintOperationAdd(fParentNode, fConstraint, fIndex, getExtLanguageManager());
	}

}
