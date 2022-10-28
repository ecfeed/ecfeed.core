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
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericOperationRemoveGlobalParameter extends BulkOperation {

	public GenericOperationRemoveGlobalParameter(
			IParametersParentNode target, 
			GlobalParameterNode parameter,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target, extLanguageManager);
		
		for(MethodParameterNode linker : parameter.getLinkedMethodParameters()){
			addOperation(new MethodOperationRemoveParameter(linker.getMethod(), linker, extLanguageManager));
		}
		
		addOperation(new GenericOperationRemoveParameter(target, parameter, extLanguageManager));
	}

	public GenericOperationRemoveGlobalParameter(
			IParametersParentNode target, 
			GlobalParameterNode parameter, 
			boolean ignoreDuplicates,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target, extLanguageManager);
		
		for(MethodParameterNode linker : parameter.getLinkedMethodParameters()){
			addOperation(new MethodOperationRemoveParameter(linker.getMethod(), linker, true, ignoreDuplicates, extLanguageManager));
		}
		
		addOperation(new GenericOperationRemoveParameter(target, parameter, extLanguageManager));
	}

}
