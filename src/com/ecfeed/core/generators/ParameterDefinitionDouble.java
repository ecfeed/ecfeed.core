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

public class ParameterDefinitionDouble extends AbstractParameterDefinition {

	private Double[] fAllowedValues = null;
	private double fDefaultValue;
	private double fMinValue = -Double.MAX_VALUE;
	private double fMaxValue = Double.MAX_VALUE;

	public ParameterDefinitionDouble(String name, double defaultValue){
		super(name, TYPE.DOUBLE);
		fDefaultValue = defaultValue;
	}

	public ParameterDefinitionDouble(String name, double defaultValue, Double[] allowedValues) throws GeneratorException {
		super(name, TYPE.DOUBLE);
		fDefaultValue = defaultValue;
		fAllowedValues = allowedValues;
		if(!Arrays.asList(fAllowedValues).contains(fDefaultValue)){
			GeneratorException.report("Inconsistent parameter definition");
		}
	}

	public ParameterDefinitionDouble(String name, double defaultValue, double min, double max) throws GeneratorException {
		super(name, TYPE.DOUBLE);
		fDefaultValue = defaultValue;
		fMinValue = min;
		fMaxValue = max;
		if(fDefaultValue <= fMinValue || fDefaultValue >= fMaxValue){
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

	@Override
	public boolean test(Object value){
		if (value instanceof Double == false){
			return false;
		}
		double intValue = (double)value;
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

	public double getMin(){
		return fMinValue;
	}

	public double getMax(){
		return fMaxValue;
	}

	@Override
	public Object parse(String value) throws GeneratorException
	{
		Double retValue;

		if (value == null) {
			retValue = fDefaultValue;
		} else {
			try {
				retValue = Double.parseDouble(value);
			} catch (Exception e) {
				GeneratorException.report("Unable to parse to Double.");
				return null;
			}
		}

		if (test(retValue)) {
			return retValue;
		} else {
			GeneratorException.report("Illegal value Double type parameter.");
			return null;
		}

	}
}
