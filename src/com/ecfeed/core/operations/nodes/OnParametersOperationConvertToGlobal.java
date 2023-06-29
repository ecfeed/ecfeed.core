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

public class OnParametersOperationConvertToGlobal extends CompositeOperation{

	public OnParametersOperationConvertToGlobal(
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

			OnParameterOperationConvertToGlobal operation = 
					new OnParameterOperationConvertToGlobal(
							parameter, newParametersParentNode, extLanguageManager);

			addOperation(operation);
		}
	}

}
