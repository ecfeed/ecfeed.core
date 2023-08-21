/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnClassOperationAddToRoot extends AbstractModelOperation {

	private RootNode fRootNode;
	private ClassNode fclassToAdd;
	private int fAddIndex;
	private boolean fGenerateUniqueName;

	public OnClassOperationAddToRoot(RootNode target, ClassNode classToAdd, boolean generateUniqeName, IExtLanguageManager extLanguageManager) {
		this(target, classToAdd, -1, generateUniqeName, extLanguageManager);
	}
	
	public OnClassOperationAddToRoot(
			RootNode rootNode, 
			ClassNode classToAdd, 
			int addIndex, 
			boolean generateUniqueName, 
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.ADD_CLASS, extLanguageManager);
		
		fRootNode = rootNode;
		fclassToAdd = classToAdd;
		fAddIndex = addIndex;
		fGenerateUniqueName = generateUniqueName;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fRootNode);

		final IExtLanguageManager extLanguageManager = getExtLanguageManager();
		
		if (fGenerateUniqueName) {
			String newName = RootNodeHelper.generateUniqueClassName(fRootNode, fclassToAdd.getName());
			fclassToAdd.setName(newName);
		}

		String name = ClassNodeHelper.getQualifiedName(fclassToAdd, extLanguageManager);

		if(fAddIndex == -1){
			fAddIndex = fRootNode.getClasses().size();
		}

		String errorMessage = ClassNodeHelper.validateClassName(name, extLanguageManager);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		errorMessage = RootNodeHelper.classWithNameExists(name, fRootNode, extLanguageManager);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}
		
		fRootNode.addClass(fclassToAdd, fAddIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnClassOperationRemove(fRootNode, fclassToAdd, getExtLanguageManager());
	}

}
