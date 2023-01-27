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
import com.ecfeed.core.operations.nodes.OnBasicParameterOperationRemove;

import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.GlobalParameterNodeHelper;
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

		List<AbstractParameterNode> linkedParameters = GlobalParameterNodeHelper.getLinkedParameters(parameter);

		for (AbstractParameterNode linkedBasicParameterNode : linkedParameters) {

			OnBasicParameterOperationRemove operation = 
					new OnBasicParameterOperationRemove(
							(MethodNode) linkedBasicParameterNode.getParent(), 
							linkedBasicParameterNode, 
							extLanguageManager);
			addOperation(operation);
		}

		addOperation(new GenericOperationRemoveParameter(target, parameter, extLanguageManager));
	}

	//	public GenericOperationRemoveGlobalParameter(
	//			IParametersParentNode target, 
	//			BasicParameterNode parameter, 
	//			IExtLanguageManager extLanguageManager) {
	//		
	//		super(OperationNames.REMOVE_GLOBAL_PARAMETER, true, target, target, extLanguageManager);
	//		
	//		if (!parameter.isGlobalParameter()) {
	//			ExceptionHelper.reportRuntimeException("Invalid type of parameter.");
	//		}
	//		
	//		List<AbstractParameterNode> linkedParameters = GlobalParameterNodeHelper.getLinkedParameters(parameter);
	//		
	//		for(AbstractParameterNode linker : linkedParameters){
	//			OnBasicParameterOperationRemove operation = 
	//					new OnBasicParameterOperationRemove(
	//							(MethodNode)linker.getParent(), linker, true, extLanguageManager);
	//			
	//			addOperation(operation);
	//		}
	//		
	//		addOperation(new GenericOperationRemoveParameter(target, parameter, extLanguageManager));
	//	}

}
