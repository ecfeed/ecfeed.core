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

import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ViewMode;


public class GlobalParameterOperationSetType extends BulkOperation {

	public GlobalParameterOperationSetType(
			GlobalParameterNode target, 
			String newType, 
			ITypeAdapterProvider adapterProvider,
			ViewMode viewMode) {

		super(OperationNames.SET_TYPE, true, target, target, viewMode);

		addOperation(new AbstractParameterOperationSetType(target, newType, adapterProvider, viewMode));

		for (MethodNode method : target.getMethods()) {
			addOperation(new MethodOperationMakeConsistent(method, viewMode));
		}
	}

}
