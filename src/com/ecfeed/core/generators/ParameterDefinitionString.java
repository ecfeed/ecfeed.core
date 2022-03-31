/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.Arrays;

import com.ecfeed.core.generators.api.GeneratorExceptionHelper;
import com.ecfeed.core.utils.ExceptionHelper;

public class ParameterDefinitionString extends AbstractParameterDefinition {

	private String[] fAllowedValues = null;
	private String fDefaultValue;

	public ParameterDefinitionString(String name, String defaultValue, String[] allowedValues) {
		super(name, TYPE.STRING);
		fDefaultValue = defaultValue;
		fAllowedValues = allowedValues;
		if(!Arrays.asList(fAllowedValues).contains(fDefaultValue)){
			GeneratorExceptionHelper.reportException("Inconsistent parameter definition");
		}
	}

	public ParameterDefinitionString(String name, String defaultValue){
		super(name, TYPE.STRING);
		fDefaultValue = defaultValue;
	}

	@Override
	public Object[] getAllowedValues(){
		return fAllowedValues;
	}

	@Override
	public Object getDefaultValue() {
		return fDefaultValue;
	}

	@Override
	public boolean test(Object value){
		if (value instanceof String == false){
			return false;
		}
		if(getAllowedValues() != null){
			boolean isAllowed = false;
			for(Object allowed : getAllowedValues()){
				if(value.equals(allowed)){
					isAllowed = true;
				}
			}
			return isAllowed;
		}
		return true;
	}

	@Override
	public Object parse(String value)
	{
		String retValue;
		if(value == null)
			retValue = fDefaultValue;
		else
			retValue = value;

		if(test(retValue))
			return retValue;
		else
		{
			ExceptionHelper.reportRuntimeException("Integer value not allowed.");
			return null;
		}
	}

}
