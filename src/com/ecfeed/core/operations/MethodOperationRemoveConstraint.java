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
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodOperationRemoveConstraint extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationRemoveConstraint(MethodNode target, ConstraintNode constraint, ExtLanguage extLanguage){
		super(OperationNames.REMOVE_CONSTRAINT, extLanguage);
		fMethodNode = target;
		fConstraint = constraint;
		fIndex = fConstraint.getMyIndex();
	}

	@Override
	public void execute() throws ModelOperationException {
		
		setOneNodeToSelect(fMethodNode);
		fIndex = fConstraint.getMyIndex();
		fMethodNode.removeConstraint(fConstraint);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodOperationAddConstraint(fMethodNode, fConstraint, fIndex, getExtLanguage());
	}

}
