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

import com.ecfeed.core.generators.api.GeneratorExceptionHelper;

public class ParameterDefinitionBoolean extends AbstractParameterDefinition {

	private boolean fDefaultValue;

	public ParameterDefinitionBoolean(String name, boolean defaultValue){
		super(name, TYPE.BOOLEAN);
		fDefaultValue = defaultValue;
	}

	@Override
	public Object getDefaultValue() {
		return fDefaultValue;
	}

	@Override
	public boolean test(Object value){
		if (value instanceof Boolean == false){
			return false;
		}
		return true;
	}

	@Override
	public Object parse(String value) {
		if(value == null)
			return fDefaultValue;

		if(value.equals("true"))
			return true;

		if(value.equals("false"))
			return false;

		GeneratorExceptionHelper.reportException("Cannot convert parameter " + getName() + " to boolean.");
		return null;
	}
}
