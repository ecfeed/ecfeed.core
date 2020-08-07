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

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.SourceViewMode;

public class ClassOperationRemoveMethod extends AbstractModelOperation {

	private ClassNode fTarget;
	private MethodNode fMethod;
	private int fCurrentIndex;
	private SourceViewMode fModelCompatibility;

	public ClassOperationRemoveMethod(ClassNode target, MethodNode method, SourceViewMode modelCompatibility) {
		
		super(OperationNames.REMOVE_METHOD);
		
		fTarget = target;
		fMethod = method;
		fCurrentIndex = fMethod.getMyMethodIndex();
		fModelCompatibility = modelCompatibility;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);
		fCurrentIndex = fMethod.getMyMethodIndex();

		if (fTarget.removeMethod(fMethod) == false) {
			ModelOperationException.report(OperationMessages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
		}

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ClassOperationAddMethod(fTarget, fMethod, fCurrentIndex, fModelCompatibility);
	}

}
