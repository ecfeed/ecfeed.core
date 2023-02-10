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
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnClassOperationRemove extends AbstractModelOperation {

	private ClassNode fRemovedClass;
	private RootNode fRootNode;
	private int fCurrentIndex;

	public OnClassOperationRemove(RootNode rootNode, ClassNode classNode, IExtLanguageManager extLanguageManager) {
		super(OperationNames.REMOVE_CLASS, extLanguageManager);
		fRootNode = rootNode;
		fRemovedClass = classNode;
		fCurrentIndex = classNode.getMyClassIndex();
	}

	@Override
	public void execute() {
		setOneNodeToSelect(fRootNode);
		fCurrentIndex = fRemovedClass.getMyClassIndex();
		fRootNode.removeClass(fRemovedClass);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnClassOperationAddToRoot(fRootNode, fRemovedClass, fCurrentIndex, getExtLanguageManager());
	}

}
