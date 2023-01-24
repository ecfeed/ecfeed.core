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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ParameterTransformer;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionDefinition;


public class GlobalParameterOperationSetType extends CompositeOperation {

	BasicParameterNode fGlobalParameterNode;
	ParameterConversionDefinition fParameterConversionDefinition;
	
	public GlobalParameterOperationSetType(
			BasicParameterNode globalParameterNode, 
			String newType, 
			ParameterConversionDefinition parameterConversionDefinition,
			ITypeAdapterProvider adapterProvider,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.SET_TYPE, true, globalParameterNode, globalParameterNode, extLanguageManager);

		if (newType == null) {
			ExceptionHelper.reportRuntimeException("New type should not be empty.");
		}

		fGlobalParameterNode = globalParameterNode;
		fParameterConversionDefinition = parameterConversionDefinition;
		
		OnAbstractParameterOperationSetType abstractParameterOperationSetType = 
				new OnAbstractParameterOperationSetType(
						globalParameterNode, 
						newType, 
						parameterConversionDefinition,
						adapterProvider, 
						extLanguageManager);

		addOperation(abstractParameterOperationSetType);

		//		for (MethodNode method : target.getMethods()) {
		//			addOperation(new MethodOperationMakeConsistent(method, extLanguageManager));
		//		}
	}
	
	@Override
	public void execute() {

		super.execute();

		ParameterTransformer.convertChoicesToType(fGlobalParameterNode, fParameterConversionDefinition);
		
		markModelUpdated();
	}
	

}
