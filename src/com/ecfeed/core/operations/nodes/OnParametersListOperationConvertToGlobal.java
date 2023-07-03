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

import java.util.List;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnParametersListOperationConvertToGlobal extends CompositeOperation{

	public OnParametersListOperationConvertToGlobal(
			List<BasicParameterNode> parametersToConvert, 
			IParametersParentNode newParametersParentNode,
			IExtLanguageManager extLanguageManager) {

		super(
				OperationNames.REPLACE_PARAMETERS, 
				false, 
				newParametersParentNode, 
				newParametersParentNode, 
				extLanguageManager);

		for (BasicParameterNode parameter : parametersToConvert) {

			//	OnBasicParameterOperationConvertToGlobalOld operation = 
			//			new OnBasicParameterOperationConvertToGlobalOld(
			//					parameter, newParametersParentNode, extLanguageManager);

			OnBasicParameterOperationConvertToGlobalNew operation = 
					new OnBasicParameterOperationConvertToGlobalNew(
							parameter, newParametersParentNode, extLanguageManager);

			addOperation(operation);
		}
	}

}
