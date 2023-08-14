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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.generators.api.GeneratorExceptionHelper;

public class ParameterDefinitionInteger extends AbstractParameterDefinition {

	private Integer[] fAllowedValues = null;
	private int fDefaultValue;
	private int fMinValue = Integer.MIN_VALUE;
	private int fMaxValue = Integer.MAX_VALUE;

	public ParameterDefinitionInteger(String name, int defaultValue){
		super(name, TYPE.INTEGER);
		fDefaultValue = defaultValue;
	}

	public ParameterDefinitionInteger(String name, int defaultValue, Integer[] allowedValues) {
		super(name, TYPE.INTEGER);
		fDefaultValue = defaultValue;
		fAllowedValues = allowedValues;
		checkAllowedValues(fDefaultValue, fAllowedValues);
	}

	private void checkAllowedValues(Integer defaultValue, Integer[] allowedValues) {
		if(!Arrays.asList(allowedValues).contains(defaultValue)){
			GeneratorExceptionHelper.reportException("Inconsistent parameter definition");
		}
	}

	public ParameterDefinitionInteger(String name, int defaultValue, int min, int max) {
		super(name, TYPE.INTEGER);
		fDefaultValue = defaultValue;
		fMinValue = min;
		fMaxValue = max;
		checkRange(fDefaultValue, fMinValue, fMaxValue);
	}

	private void checkRange(int value, int minValue, int maxValue) {
		if(value < minValue || value > maxValue){
			GeneratorExceptionHelper.reportException("Inconsistent parameter definition");
		}
	}

	public int getMin()
	{
		return fMinValue;
	}

	public int getMax()
	{
		return fMaxValue;
	}

	@Override
	public Object[] getAllowedValues(){
		return fAllowedValues;
	}

	@Override
	public void setAllowedValues(Object[] allowedValues) {
		
		List<Integer> allowedIntegers = new ArrayList<>();
		
		for (Object allowedValue : allowedValues) {
			
			allowedIntegers.add((Integer) allowedValue);
		}
		
		fAllowedValues = allowedIntegers.toArray(new Integer[allowedIntegers.size()]);
	}
	
	@Override
	public Object getDefaultValue() {
		return fDefaultValue;
	}

	public void setDefaultValue(Integer defaultValue) {
		
		int tmpDefaultValue = (int)defaultValue;

		checkRange(tmpDefaultValue, fMinValue, fMaxValue);

		if (fAllowedValues != null) {
			checkAllowedValues(defaultValue, fAllowedValues);
		}

		fDefaultValue = (int)tmpDefaultValue;
	}

	@Override
	public boolean test(Object value) {
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
	public Object parse(String value) {
		Integer retValue;
		if(value == null)
			retValue = fDefaultValue;
		else {
			try {
				retValue = Integer.parseInt(value);
			} catch (Exception e) {
				GeneratorExceptionHelper.reportException("Cannot convert parameter " + getName() + " to integer.");
				return null;
			}
		}

		if(test(retValue))
			return retValue;
		else
		{
			GeneratorExceptionHelper.reportException("Integer value not allowed for parameter: " + getName() + ".");
			return null;
		}
	}
}
