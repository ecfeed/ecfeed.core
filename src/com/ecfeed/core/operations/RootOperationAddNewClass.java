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
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.ExtLanguage;

public class RootOperationAddNewClass extends AbstractModelOperation {

	private RootNode fRootNode;
	private ClassNode fclassToAdd;
	private int fAddIndex;

	public RootOperationAddNewClass(RootNode target, ClassNode classToAdd, ExtLanguage extLanguage) {
		this(target, classToAdd, -1, extLanguage);
	}
	
	public RootOperationAddNewClass(RootNode rootNode, ClassNode classToAdd, int addIndex, ExtLanguage extLanguage) {
		super(OperationNames.ADD_CLASS, extLanguage);
		fRootNode = rootNode;
		fclassToAdd = classToAdd;
		fAddIndex = addIndex;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fRootNode);
		String name = fclassToAdd.getName();
		if(fAddIndex == -1){
			fAddIndex = fRootNode.getClasses().size();
		}

		String errorMessage = ClassNodeHelper.validateClassName(name, getExtLanguage());

		if (errorMessage != null) {
			ModelOperationException.report(errorMessage);
		}
		
		if(fRootNode.getClass(name) != null){
			ModelOperationException.report(OperationMessages.CLASS_NAME_DUPLICATE_PROBLEM);
		}
		
		fRootNode.addClass(fclassToAdd, fAddIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new RootOperationRemoveClass(fRootNode, fclassToAdd, getExtLanguage());
	}

}
