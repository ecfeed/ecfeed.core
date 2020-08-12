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
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.ViewMode;

public class GenericOperationRemoveGlobalParameter extends BulkOperation {

	public GenericOperationRemoveGlobalParameter(
			GlobalParametersParentNode target, GlobalParameterNode parameter, ViewMode viewMode) {
		
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target);
		
		for(MethodParameterNode linker : parameter.getLinkers()){
			addOperation(new MethodOperationRemoveParameter(linker.getMethod(), linker, viewMode));
		}
		
		addOperation(new GenericOperationRemoveParameter(target, parameter));
	}

	public GenericOperationRemoveGlobalParameter(
			GlobalParametersParentNode target, GlobalParameterNode parameter, boolean ignoreDuplicates, ViewMode viewMode) {
		
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target);
		
		for(MethodParameterNode linker : parameter.getLinkers()){
			addOperation(new MethodOperationRemoveParameter(linker.getMethod(), linker, true, ignoreDuplicates, viewMode));
		}
		
		addOperation(new GenericOperationRemoveParameter(target, parameter));
	}

}
