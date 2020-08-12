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
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.ViewMode;

public class RootOperationAddNewClass extends AbstractModelOperation {

	private RootNode fRootNode;
	private ClassNode fclassToAdd;
	private int fAddIndex;
	private ViewMode fModelCompatibility;

	public RootOperationAddNewClass(RootNode target, ClassNode classToAdd, ViewMode modelCompatibility) {
		this(target, classToAdd, -1, modelCompatibility);
	}
	
	public RootOperationAddNewClass(RootNode rootNode, ClassNode classToAdd, int addIndex, ViewMode modelCompatibility) {
		super(OperationNames.ADD_CLASS);
		fRootNode = rootNode;
		fclassToAdd = classToAdd;
		fAddIndex = addIndex;
		fModelCompatibility = modelCompatibility;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fRootNode);
		String name = fclassToAdd.getFullName();
		if(fAddIndex == -1){
			fAddIndex = fRootNode.getClasses().size();
		}
		
		if (fModelCompatibility == ViewMode.JAVA) {
			if(!ClassNodeHelper.classNameCompliesWithJavaNamingRules(name)){
				ModelOperationException.report(RegexHelper.CLASS_NAME_REGEX_PROBLEM);
			}
		}
		
		if(fRootNode.getClass(name) != null){
			ModelOperationException.report(OperationMessages.CLASS_NAME_DUPLICATE_PROBLEM);
		}
		
		fRootNode.addClass(fclassToAdd, fAddIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new RootOperationRemoveClass(fRootNode, fclassToAdd, fModelCompatibility);
	}

}
