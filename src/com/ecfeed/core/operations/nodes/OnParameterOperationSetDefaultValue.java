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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationMessages;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnParameterOperationSetDefaultValue extends AbstractModelOperation {

	private BasicParameterNode fTarget;
	private ITypeAdapter<?> fTypeAdapter;
	private String fNewValue;
	private String fOriginalValue;

	public OnParameterOperationSetDefaultValue(BasicParameterNode target, String newValue, ITypeAdapter<?> typeAdapter, IExtLanguageManager extLanguageManager) {
		super(OperationNames.SET_DEFAULT_VALUE, extLanguageManager);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getDefaultValue();
		fTypeAdapter = typeAdapter;
	}

	@Override
	public void execute() {
		String convertedValue = fTypeAdapter.adapt(fNewValue, false, ERunMode.QUIET, getExtLanguageManager());
		if(convertedValue == null){
			ExceptionHelper.reportRuntimeException(OperationMessages.CATEGORY_DEFAULT_VALUE_REGEX_PROBLEM);
		}

		fTarget.setDefaultValueString(convertedValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnParameterOperationSetDefaultValue(fTarget, fOriginalValue, fTypeAdapter, getExtLanguageManager());
	}

}
