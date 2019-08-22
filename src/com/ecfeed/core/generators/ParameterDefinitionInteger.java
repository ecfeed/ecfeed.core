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

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.utils.ExceptionHelper;

public class ParameterDefinitionInteger extends AbstractParameterDefinition {

	private Integer[] fAllowedValues = null;
	private int fDefaultValue;
	private int fMinValue = Integer.MIN_VALUE;
	private int fMaxValue = Integer.MAX_VALUE;

	public ParameterDefinitionInteger(String name, int defaultValue){
		super(name, TYPE.INTEGER);
		fDefaultValue = defaultValue;
	}

	public ParameterDefinitionInteger(String name, int defaultValue, Integer[] allowedValues) throws GeneratorException {
		super(name, TYPE.INTEGER);
		fDefaultValue = defaultValue;
		fAllowedValues = allowedValues;
		checkAllowedValues(fDefaultValue, fAllowedValues);
	}

	private void checkAllowedValues(Integer defaultValue, Integer[] allowedValues) throws GeneratorException {
		if(!Arrays.asList(allowedValues).contains(defaultValue)){
			GeneratorException.report("Inconsistent parameter definition");
		}
	}

	public ParameterDefinitionInteger(String name, int defaultValue, int min, int max) throws GeneratorException {
		super(name, TYPE.INTEGER);
		fDefaultValue = defaultValue;
		fMinValue = min;
		fMaxValue = max;
		checkRange(fDefaultValue, fMinValue, fMaxValue);
	}

	private void checkRange(int value, int minValue, int maxValue) throws GeneratorException {
		if(value < minValue || value > maxValue){
			GeneratorException.report("Inconsistent parameter definition");
		}
	}

	@Override
	public Object[] getAllowedValues(){
		return fAllowedValues;
	}

	@Override
	public Object getDefaultValue() {
		return fDefaultValue;
	}

	public void setDefaultValue(Object defaultValue) throws GeneratorException {
		int tmpDefaultValue = (int)defaultValue;

		checkRange(tmpDefaultValue, fMinValue, fMaxValue);

		if (fAllowedValues != null) {
			checkAllowedValues(fDefaultValue, fAllowedValues);
		}

		fDefaultValue = (int)tmpDefaultValue;
	}	

	@Override
	public boolean test(Object value){
		if (value instanceof Integer == false){
			return false;
		}
		int intValue = (Integer)value;
		if(getAllowedValues() != null){
			boolean isAllowed = false;
			for(Object allowed : getAllowedValues()){
				if(value.equals(allowed)){
					isAllowed = true;
				}
			}
			return isAllowed;
		}
		return (intValue >= fMinValue && intValue <= fMaxValue);
	}

	@Override
	public Object parse(String value) throws GeneratorException
	{
		Integer retValue;
		if(value == null)
			retValue = fDefaultValue;
		else {
			try {
				retValue = Integer.parseInt(value);
			} catch (Exception e) {
				GeneratorException.report("Unable to parse to Integer.");
				return null;
			}
		}

		if(test(retValue))
			return retValue;
		else
		{
			GeneratorException.report("Integer value not allowed.");
			return null;
		}

	}
}
