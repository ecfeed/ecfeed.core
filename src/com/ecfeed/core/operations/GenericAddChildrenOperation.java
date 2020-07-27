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

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ModelCompatibility;
import com.ecfeed.core.utils.SystemLogger;

public class GenericAddChildrenOperation extends BulkOperation {

	public GenericAddChildrenOperation(
			AbstractNode target, 
			Collection<? extends AbstractNode> children, 
			ITypeAdapterProvider adapterProvider, 
			boolean validate,
			ModelCompatibility modelCompatibility) {
		
		this(target, children, -1, adapterProvider, validate, modelCompatibility);
	}

	public GenericAddChildrenOperation(
			AbstractNode target, 
			Collection<? extends AbstractNode> children, 
			int index, 
			ITypeAdapterProvider adapterProvider, 
			boolean validate,
			ModelCompatibility modelCompatibility) {

		super(OperationNames.ADD_CHILDREN, false, target, target);

		for (AbstractNode child : children) {
			IModelOperation operation;
			try {
				if (index != -1) {
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, index++, adapterProvider, validate, modelCompatibility));
				} else {
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, adapterProvider, validate, modelCompatibility));
				}
				if (operation != null) {
					addOperation(operation);
				}
			} catch (Exception e) {SystemLogger.logCatch(e);}
		}
	}

	public boolean enabled(){
		return operations().isEmpty() == false;
	}
}
