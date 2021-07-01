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
import com.ecfeed.core.utils.IExtLanguageManager;

public class ClassOperationSetAndroidBaseRunner extends AbstractModelOperation { // TODO - REMOVE

	private ClassNode fTarget;
	private String fOriginalValue;

	public ClassOperationSetAndroidBaseRunner(ClassNode target, String newValue, IExtLanguageManager extLanguageManager) {
		super(OperationNames.SET_ANDROID_BASE_RUNNER, extLanguageManager);
		fTarget = target;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fTarget);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ClassOperationSetAndroidBaseRunner(fTarget, fOriginalValue, getExtLanguageManager());
	}

}
