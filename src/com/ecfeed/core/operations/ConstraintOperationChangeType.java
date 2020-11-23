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

	private ConstraintNode fConstraintNode;
	private ConstraintType fNewConstraintType;
	private IExtLanguageManager fExtLanguageManager;

	public ConstraintOperationChangeType(
			ConstraintNode constraintNode,
			ConstraintType newConstraintType,
			IExtLanguageManager extLanguageManager) {
		super(OperationNames.CHANGE_CONSTRAINT_TYPE, extLanguageManager);
		fConstraintNode = constraintNode;
		fNewConstraintType = newConstraintType;
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public void execute() {

//		setOneNodeToSelect(fRootNode);
//
//		final IExtLanguageManager extLanguageManager = getExtLanguageManager();
//
//		String name = ClassNodeHelper.getQualifiedName(fclassToAdd, extLanguageManager);
//
//		if(fAddIndex == -1){
//			fAddIndex = fRootNode.getClasses().size();
//		}
//
//		String errorMessage = ClassNodeHelper.validateClassName(name, extLanguageManager);
//
//		if (errorMessage != null) {
//			ExceptionHelper.reportRuntimeException(errorMessage);
//		}
//
//		errorMessage = RootNodeHelper.classWithNameExists(name, fRootNode, extLanguageManager);
//
//		if (errorMessage != null) {
//			ExceptionHelper.reportRuntimeException(errorMessage);
//		}
//
//		fRootNode.addClass(fclassToAdd, fAddIndex);
//		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {

		final ConstraintType constraintType = fConstraintNode.getConstraint().getConstraintType();

		return new ConstraintOperationChangeType(fConstraintNode, constraintType, fExtLanguageManager);
	}

}
