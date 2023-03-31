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
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IBasicParameterVisitor;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationMessages;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.LogHelperCore;

public class OnChoiceOperationSetValue extends AbstractModelOperation {

	private String fNewValue;
	private String fOriginalValue;
	private String fOriginalDefaultValue;
	private ChoiceNode fOwnChoiceNode;

	public OnChoiceOperationSetValue(ChoiceNode target, String newValue, IExtLanguageManager extLanguageManager){
		
		super(OperationNames.SET_PARTITION_VALUE, extLanguageManager);
		
		fOwnChoiceNode = target;
		fNewValue = newValue;
		fOriginalValue = fOwnChoiceNode.getValueString();
	}

	@Override
	public void execute() {

		String convertedValue = adaptChoiceValue(fOwnChoiceNode.getParameter().getType(), fNewValue);
		
		if(convertedValue == null){
			ExceptionHelper.reportRuntimeException(OperationMessages.PARTITION_VALUE_PROBLEM(fNewValue));
		}
		
		fOwnChoiceNode.setValueString(convertedValue);
		adaptParameter(fOwnChoiceNode.getParameter());
		markModelUpdated();
	}

	private void adaptParameter(BasicParameterNode parameter) {
		try{
			parameter.accept(new ParameterAdapter());
		}catch(Exception e){
			LogHelperCore.logCatch(e);}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	@Override
	public String toString(){
		return "setValue[" + fOwnChoiceNode + "](" + fNewValue + ")";
	}

	private String adaptChoiceValue(String type, String value) {

		final int MAX_PARTITION_VALUE_STRING_LENGTH = 512;

		if (value.length() > MAX_PARTITION_VALUE_STRING_LENGTH) {
			return null;
		}

		ITypeAdapter<?> typeAdapter = JavaLanguageHelper.getAdapter(type); 

		try {
			return typeAdapter.adapt(
					value, 
					fOwnChoiceNode.isRandomizedValue(), 
					ERunMode.WITH_EXCEPTION,
					getExtLanguageManager());
			
		} catch (RuntimeException ex) {
			ExceptionHelper.reportRuntimeException(ex.getMessage());
		}
		return null;
	}

	private class ParameterAdapter implements IBasicParameterVisitor{

		@Override
		public Object visit(BasicParameterNode parameter) throws Exception {
			fOriginalDefaultValue = parameter.getDefaultValue();
			if(parameter != null && JavaLanguageHelper.isUserType(parameter.getType())){
				if(parameter.getLeafChoiceValues().contains(parameter.getDefaultValue()) == false){
					parameter.setDefaultValueString(fNewValue);
				}
			}
			return null;
		}

	}

	private class ReverseOperation extends AbstractModelOperation{

		private class ReverseParameterAdapter implements IBasicParameterVisitor{

			@Override
			public Object visit(BasicParameterNode parameter) throws Exception {
				parameter.setDefaultValueString(fOriginalDefaultValue);
				return null;
			}

		}

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OnChoiceOperationSetValue.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {
			fOwnChoiceNode.setValueString(fOriginalValue);
			adaptParameter(fOwnChoiceNode.getParameter());
			markModelUpdated();
		}

		private void adaptParameter(BasicParameterNode parameter) {
			try{
				parameter.accept(new ReverseParameterAdapter());
			}catch(Exception e){LogHelperCore.logCatch(e);}
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OnChoiceOperationSetValue(fOwnChoiceNode, fNewValue, getExtLanguageManager());
		}
	}


}