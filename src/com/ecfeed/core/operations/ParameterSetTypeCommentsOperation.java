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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ParameterSetTypeCommentsOperation extends AbstractModelOperation {

	private String fComments;
	private BasicParameterNode fTarget;
	private String fCurrentComments;

	public ParameterSetTypeCommentsOperation(BasicParameterNode target, String comments, IExtLanguageManager extLanguageManager) {
		super(OperationNames.SET_COMMENTS, extLanguageManager);
		fTarget = target;
		fComments = comments;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fTarget);
		fCurrentComments = fTarget.getTypeComments() != null ? fTarget.getTypeComments() : "";
		fTarget.setTypeComments(fComments);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ParameterSetTypeCommentsOperation(fTarget, fCurrentComments, getExtLanguageManager());
	}

}
