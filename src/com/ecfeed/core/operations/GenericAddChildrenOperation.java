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

import java.util.Collection;

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.LogHelperCore;

public class GenericAddChildrenOperation extends CompositeOperation {

	public GenericAddChildrenOperation(
			IAbstractNode target, 
			Collection<? extends IAbstractNode> children, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {
		
		this(target, children, -1, validate, extLanguageManager);
	}

	public GenericAddChildrenOperation(
			IAbstractNode target, 
			Collection<? extends IAbstractNode> children, 
			int index, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_CHILDREN, false, target, target, extLanguageManager);

		for (IAbstractNode child : children) {
			IModelOperation operation;
			try {
				if (index != -1) {
					operation = 
							(IModelOperation)target.accept(
									new FactoryAddChildOperation(
											child, index++, validate, getExtLanguageManager()));
				} else {
					operation = 
							(IModelOperation)target.accept(
									new FactoryAddChildOperation(child, validate, getExtLanguageManager()));
				}
				if (operation != null) {
					addOperation(operation);
				}
			} catch (Exception e) {
				LogHelperCore.logCatch(e);}
		}
	}

	public boolean enabled(){
		return getOperations().isEmpty() == false;
	}
}
