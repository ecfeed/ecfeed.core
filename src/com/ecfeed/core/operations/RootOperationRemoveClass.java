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
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class RootOperationRemoveClass extends AbstractModelOperation {

	private ClassNode fRemovedClass;
	private RootNode fTarget;
	private int fCurrentIndex;

	public RootOperationRemoveClass(RootNode target, ClassNode removedClass, IExtLanguageManager extLanguage) {
		super(OperationNames.REMOVE_CLASS, extLanguage);
		fTarget = target;
		fRemovedClass = removedClass;
		fCurrentIndex = removedClass.getMyClassIndex();
	}

	@Override
	public void execute() throws ModelOperationException {
		setOneNodeToSelect(fTarget);
		fCurrentIndex = fRemovedClass.getMyClassIndex();
		fTarget.removeClass(fRemovedClass);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new RootOperationAddNewClass(fTarget, fRemovedClass, fCurrentIndex, getExtLanguageManager());
	}

}
