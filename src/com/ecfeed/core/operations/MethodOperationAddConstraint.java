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
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.RegexHelper;

public class MethodOperationAddConstraint extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationAddConstraint(MethodNode methodNode, ConstraintNode constraint, int index, IExtLanguageManager extLanguageManager){
		
		super(OperationNames.ADD_CONSTRAINT, extLanguageManager);
		
		fMethodNode = methodNode;
		fConstraint = constraint;
		fIndex = index;
	}

	public MethodOperationAddConstraint(MethodNode target, ConstraintNode constraint, IExtLanguageManager extLanguageManager){
		this(target, constraint, -1, extLanguageManager);
	}

	@Override
	public String toString() {
		
		return "Add constraint: " + fConstraint.getName();
	}
	
	@Override
	public void execute() {

		setOneNodeToSelect(fMethodNode);

		if(fIndex == -1){
			fIndex = fMethodNode.getConstraintNodes().size();
		}
		if(fConstraint.getName().matches(RegexHelper.REGEX_CONSTRAINT_NODE_NAME) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.CONSTRAINT_NOT_ALLOWED);
		}
		if(fConstraint.updateReferences(fMethodNode) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.INCOMPATIBLE_CONSTRAINT_PROBLEM);
		}
		fMethodNode.addConstraint(fConstraint, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodOperationRemoveConstraint(fMethodNode, fConstraint, getExtLanguageManager());
	}

}
