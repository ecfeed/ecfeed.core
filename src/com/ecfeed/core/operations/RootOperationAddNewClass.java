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
import com.ecfeed.core.utils.NodeNamingConvention;

public class RootOperationAddNewClass extends AbstractModelOperation {

	private RootNode fRootNode;
	private ClassNode fclassToAdd;
	private int fAddIndex;
	private NodeNamingConvention fNodeNamingConvention;

	public RootOperationAddNewClass(RootNode target, ClassNode classToAdd, NodeNamingConvention nodeNamingConvention) {
		this(target, classToAdd, -1, nodeNamingConvention);
	}
	
	public RootOperationAddNewClass(RootNode rootNode, ClassNode classToAdd, int addIndex, NodeNamingConvention nodeNamingConvention) {
		super(OperationNames.ADD_CLASS);
		fRootNode = rootNode;
		fclassToAdd = classToAdd;
		fAddIndex = addIndex;
		fNodeNamingConvention = nodeNamingConvention;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fRootNode);
		String name = fclassToAdd.getFullName();
		if(fAddIndex == -1){
			fAddIndex = fRootNode.getClasses().size();
		}
		
		if (fNodeNamingConvention == NodeNamingConvention.JAVA) {
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
		return new RootOperationRemoveClass(fRootNode, fclassToAdd, fNodeNamingConvention);
	}

}
