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

import java.util.Collection;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnMethodsOperationAddToClass extends CompositeOperation{

	public OnMethodsOperationAddToClass(ClassNode target, Collection<MethodNode> methods, int index, IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.ADD_METHODS, false, target, target, extLanguageManager);
		
		for(MethodNode method : methods){
			addOperation(new OnMethodOperationAddToClass(target, method, index++, extLanguageManager));
		}
	}
}
