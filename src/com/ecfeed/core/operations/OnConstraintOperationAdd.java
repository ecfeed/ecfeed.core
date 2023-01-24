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

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IParametersAndConstraintsParentNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.RegexHelper;

public class OnConstraintOperationAdd extends AbstractModelOperation {

	private IParametersAndConstraintsParentNode fParentNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public OnConstraintOperationAdd(
			IParametersAndConstraintsParentNode parentNode, 
			ConstraintNode constraint, 
			int index, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_CONSTRAINT, extLanguageManager);

		fParentNode = parentNode;
		fConstraint = constraint;
		fIndex = index;
	}

	public OnConstraintOperationAdd(
			IParametersAndConstraintsParentNode target, 
			ConstraintNode constraint, 
			IExtLanguageManager extLanguageManager){

		this(target, constraint, -1, extLanguageManager);
	}

	@Override
	public String toString() {

		return "Add constraint: " + fConstraint.getName();
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fParentNode);

		if(fIndex == -1){
			fIndex = fParentNode.getConstraintNodes().size();
		}

		if(fConstraint.getName().matches(RegexHelper.REGEX_CONSTRAINT_NODE_NAME) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.CONSTRAINT_NOT_ALLOWED);
		}

		if(fConstraint.updateReferences(fParentNode) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.INCOMPATIBLE_CONSTRAINT_PROBLEM);
		}

		fParentNode.addConstraint(fConstraint, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {

		return new OnConstraintOperationRemove(fParentNode, fConstraint, getExtLanguageManager());
	}

}
