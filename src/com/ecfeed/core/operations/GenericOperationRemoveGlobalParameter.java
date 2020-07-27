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
import com.ecfeed.core.utils.ModelCompatibility;

public class GenericOperationRemoveGlobalParameter extends BulkOperation {

	public GenericOperationRemoveGlobalParameter(
			GlobalParametersParentNode target, GlobalParameterNode parameter, ModelCompatibility modelCompatibility) {
		
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target);
		
		for(MethodParameterNode linker : parameter.getLinkers()){
			addOperation(new MethodOperationRemoveParameter(linker.getMethod(), linker, modelCompatibility));
		}
		
		addOperation(new GenericOperationRemoveParameter(target, parameter));
	}

	public GenericOperationRemoveGlobalParameter(
			GlobalParametersParentNode target, GlobalParameterNode parameter, boolean ignoreDuplicates, ModelCompatibility modelCompatibility) {
		
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target);
		
		for(MethodParameterNode linker : parameter.getLinkers()){
			addOperation(new MethodOperationRemoveParameter(linker.getMethod(), linker, true, ignoreDuplicates, modelCompatibility));
		}
		
		addOperation(new GenericOperationRemoveParameter(target, parameter));
	}

}
