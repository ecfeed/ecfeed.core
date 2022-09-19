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
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;


public class GlobalParameterOperationSetType extends BulkOperation {

	public GlobalParameterOperationSetType(
			GlobalParameterNode target, 
			String newType, 
			ITypeAdapterProvider adapterProvider,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.SET_TYPE, true, target, target, extLanguageManager);
		
		if (newType == null) {
			ExceptionHelper.reportRuntimeException("New type should not be empty.");
		}

		AbstractParameterOperationSetType abstractParameterOperationSetType = 
				new AbstractParameterOperationSetType(
						target, 
						newType, 
						null, // TODO DE-NO 
						adapterProvider, 
						extLanguageManager);
		
		addOperation(abstractParameterOperationSetType);

		for (MethodNode method : target.getMethods()) {
			addOperation(new MethodOperationMakeConsistent(method, extLanguageManager));
		}
	}

}
