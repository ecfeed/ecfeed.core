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

import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericOperationRemoveGlobalParameter extends CompositeOperation {

	public GenericOperationRemoveGlobalParameter(
			IParametersParentNode target, 
			BasicParameterNode parameter,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target, extLanguageManager);
		
		if (!parameter.isGlobalParameter()) {
			ExceptionHelper.reportRuntimeException("Invalid type of parameter.");
		}
		
		for (BasicParameterNode linkedBasicParameterNode : parameter.getLinkedMethodParameters()) {
			
			MethodOperationRemoveParameter operation = 
					new MethodOperationRemoveParameter(
							(MethodNode) linkedBasicParameterNode.getParent(), 
							linkedBasicParameterNode, 
							extLanguageManager);
			addOperation(operation);
		}
		
		addOperation(new GenericOperationRemoveParameter(target, parameter, extLanguageManager));
	}

	public GenericOperationRemoveGlobalParameter(
			IParametersParentNode target, 
			BasicParameterNode parameter, 
			boolean ignoreDuplicates,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target, extLanguageManager);
		
		if (!parameter.isGlobalParameter()) {
			ExceptionHelper.reportRuntimeException("Invalid type of parameter.");
		}
		
		for(BasicParameterNode linker : parameter.getLinkedMethodParameters()){
			MethodOperationRemoveParameter operation = 
					new MethodOperationRemoveParameter(
							(MethodNode)linker.getParent(), linker, true, ignoreDuplicates, extLanguageManager);
			
			addOperation(operation);
		}
		
		addOperation(new GenericOperationRemoveParameter(target, parameter, extLanguageManager));
	}

}
