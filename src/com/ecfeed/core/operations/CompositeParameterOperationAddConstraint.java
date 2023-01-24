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
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.RegexHelper;

public class CompositeParameterOperationAddConstraint extends AbstractModelOperation { // TODO MO-RE add merge with MethodOperationAddConstraint

	private CompositeParameterNode fCompositeParameterNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public CompositeParameterOperationAddConstraint(
			CompositeParameterNode compositeParameterNode, 
			ConstraintNode constraint, 
			int index, 
			IExtLanguageManager extLanguageManager){
		
		super(OperationNames.ADD_CONSTRAINT, extLanguageManager);
		
		fCompositeParameterNode = compositeParameterNode;
		fConstraint = constraint;
		fIndex = index;
	}

	@Override
	public String toString() {
		
		return "Add constraint: " + fConstraint.getName();
	}
	
	public CompositeParameterOperationAddConstraint(
			CompositeParameterNode target, 
			ConstraintNode constraint, 
			IExtLanguageManager extLanguageManager){
		this(target, constraint, -1, extLanguageManager);
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fCompositeParameterNode);

		if(fIndex == -1){
			fIndex = fCompositeParameterNode.getConstraintNodes().size();
		}
		if(fConstraint.getName().matches(RegexHelper.REGEX_CONSTRAINT_NODE_NAME) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.CONSTRAINT_NOT_ALLOWED);
		}
		if(fConstraint.updateReferences(fCompositeParameterNode) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.INCOMPATIBLE_CONSTRAINT_PROBLEM);
		}
		fCompositeParameterNode.addConstraint(fConstraint, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new CompositeParameterOperationRemoveConstraint(fCompositeParameterNode, fConstraint, getExtLanguageManager());
	}

}
