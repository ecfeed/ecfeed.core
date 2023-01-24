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
import com.ecfeed.core.utils.IExtLanguageManager;

public class CompositeParameterOperationRemoveConstraint extends AbstractModelOperation { // TODO MO-RE merge with MethodOperationRemoveConstraint

	private CompositeParameterNode fCompositeParameterNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public CompositeParameterOperationRemoveConstraint(CompositeParameterNode target, ConstraintNode constraint, IExtLanguageManager extLanguageManager){
		super(OperationNames.REMOVE_CONSTRAINT, extLanguageManager);
		fCompositeParameterNode = target;
		fConstraint = constraint;
		fIndex = fConstraint.getMyIndex();
	}
	
	@Override
	public String toString() {
		
		return "Remove constraint: " + fConstraint.getName();
	}

	@Override
	public void execute() {
		
		setOneNodeToSelect(fCompositeParameterNode);
		fIndex = fConstraint.getMyIndex();
		fCompositeParameterNode.removeConstraint(fConstraint);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new CompositeParameterOperationAddConstraint(fCompositeParameterNode, fConstraint, fIndex, getExtLanguageManager());
	}

}
