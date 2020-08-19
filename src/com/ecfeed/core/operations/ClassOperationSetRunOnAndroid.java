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
import com.ecfeed.core.utils.ViewMode;

public class ClassOperationSetRunOnAndroid extends AbstractModelOperation {

	private ClassNode fClassNode;
	private boolean fNewValue;
	private boolean fOriginalValue;

	public ClassOperationSetRunOnAndroid(ClassNode classNode, boolean newValue, ViewMode viewMode) {
		super(OperationNames.SET_ANDROID_BASE_RUNNER, viewMode);
		fClassNode = classNode;
		fNewValue = newValue;
		fOriginalValue = classNode.getRunOnAndroid();
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fClassNode);
		fClassNode.setRunOnAndroid(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ClassOperationSetRunOnAndroid(fClassNode, fOriginalValue, getViewMode());
	}

}
