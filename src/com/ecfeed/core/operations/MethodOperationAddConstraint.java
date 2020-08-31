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
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodOperationAddConstraint extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationAddConstraint(MethodNode methodNode, ConstraintNode constraint, int index, ExtLanguage viewMode){
		
		super(OperationNames.ADD_CONSTRAINT, viewMode);
		
		fMethodNode = methodNode;
		fConstraint = constraint;
		fIndex = index;
	}

	public MethodOperationAddConstraint(MethodNode target, ConstraintNode constraint, ExtLanguage viewMode){
		this(target, constraint, -1, viewMode);
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fMethodNode);

		if(fIndex == -1){
			fIndex = fMethodNode.getConstraintNodes().size();
		}
		if(fConstraint.getName().matches(RegexHelper.REGEX_CONSTRAINT_NODE_NAME) == false){
			ModelOperationException.report(OperationMessages.CONSTRAINT_NAME_REGEX_PROBLEM);
		}
		if(fConstraint.updateReferences(fMethodNode) == false){
			ModelOperationException.report(OperationMessages.INCOMPATIBLE_CONSTRAINT_PROBLEM);
		}
		fMethodNode.addConstraint(fConstraint, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodOperationRemoveConstraint(fMethodNode, fConstraint, getViewMode());
	}

}
