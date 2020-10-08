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

import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ParameterOperationSetDefaultValue extends AbstractModelOperation {

	private MethodParameterNode fTarget;
	private ITypeAdapter<?> fTypeAdapter;
	private String fNewValue;
	private String fOriginalValue;

	public ParameterOperationSetDefaultValue(MethodParameterNode target, String newValue, ITypeAdapter<?> typeAdapter, IExtLanguageManager extLanguageManager) {
		super(OperationNames.SET_DEFAULT_VALUE, extLanguageManager);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getDefaultValue();
		fTypeAdapter = typeAdapter;
	}

	@Override
	public void execute() throws ModelOperationException {
		String convertedValue = fTypeAdapter.convert(fNewValue, false, ERunMode.QUIET);
		if(convertedValue == null){
			ModelOperationException.report(OperationMessages.CATEGORY_DEFAULT_VALUE_REGEX_PROBLEM);
		}

		fTarget.setDefaultValueString(convertedValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ParameterOperationSetDefaultValue(fTarget, fOriginalValue, fTypeAdapter, getExtLanguageManager());
	}

}
