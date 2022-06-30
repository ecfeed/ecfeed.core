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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IParameterVisitor;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.*;

public class ChoiceOperationSetValue extends AbstractModelOperation {

	private String fNewValue;
	private String fOriginalValue;
	private String fOriginalDefaultValue;
	private ChoiceNode fOwnChoiceNode;

	private ITypeAdapterProvider fAdapterProvider;

	public ChoiceOperationSetValue(ChoiceNode target, String newValue, ITypeAdapterProvider adapterProvider, IExtLanguageManager extLanguageManager){
		
		super(OperationNames.SET_PARTITION_VALUE, extLanguageManager);
		
		fOwnChoiceNode = target;
		fNewValue = newValue;
		fOriginalValue = fOwnChoiceNode.getValueString();
		fAdapterProvider = adapterProvider;
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

	private void adaptParameter(AbstractParameterNode parameter) {
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

		ITypeAdapter<?> typeAdapter = fAdapterProvider.getAdapter(type); 

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

	private class ParameterAdapter implements IParameterVisitor{

		@Override
		public Object visit(MethodParameterNode parameter) throws Exception {
			fOriginalDefaultValue = parameter.getDefaultValue();
			if(parameter != null && JavaLanguageHelper.isUserType(parameter.getType())){
				if(parameter.getLeafChoiceValues().contains(parameter.getDefaultValue()) == false){
					parameter.setDefaultValueString(fNewValue);
				}
			}
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

	}

	private class ReverseOperation extends AbstractModelOperation{

		private class ReverseParameterAdapter implements IParameterVisitor{

			@Override
			public Object visit(MethodParameterNode parameter) throws Exception {
				parameter.setDefaultValueString(fOriginalDefaultValue);
				return null;
			}

			@Override
			public Object visit(GlobalParameterNode parameter) throws Exception {
				return null;
			}

		}

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(ChoiceOperationSetValue.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {
			fOwnChoiceNode.setValueString(fOriginalValue);
			adaptParameter(fOwnChoiceNode.getParameter());
			markModelUpdated();
		}

		private void adaptParameter(AbstractParameterNode parameter) {
			try{
				parameter.accept(new ReverseParameterAdapter());
			}catch(Exception e){LogHelperCore.logCatch(e);}
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationSetValue(fOwnChoiceNode, fNewValue, fAdapterProvider, getExtLanguageManager());
		}
	}


}