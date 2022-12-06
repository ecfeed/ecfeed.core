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

public class CompositeParameterOperationRemoveConstraint extends AbstractModelOperation {

	private CompositeParameterNode fMethodNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public CompositeParameterOperationRemoveConstraint(CompositeParameterNode target, ConstraintNode constraint, IExtLanguageManager extLanguageManager){
		super(OperationNames.REMOVE_CONSTRAINT, extLanguageManager);
		fMethodNode = target;
		fConstraint = constraint;
		fIndex = fConstraint.getMyIndex();
	}

	@Override
	public void execute() {
		
		setOneNodeToSelect(fMethodNode);
		fIndex = fConstraint.getMyIndex();
		fMethodNode.removeConstraint(fConstraint);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new CompositeParameterOperationAddConstraint(fMethodNode, fConstraint, fIndex, getExtLanguageManager());
	}

}
