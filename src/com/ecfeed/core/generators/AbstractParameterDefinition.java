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
import com.ecfeed.core.generators.api.IParameterDefinition;

public class AbstractParameterDefinition implements IParameterDefinition {

	private String fName;
	private TYPE fType;

	public AbstractParameterDefinition(String name, TYPE type){
		fName = name;
		fType = type;
	}

	@Override
	public Object parse(String value) {
		GeneratorExceptionHelper.reportException("parsing into abstract value");
		return null;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public TYPE getType() {
		return fType;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public Object[] getAllowedValues() {
		return null;
	}

	@Override
	public boolean test(Object value){
		return false;
	}

	@Override
	public String toString(){
		return fName + "[" + fType + "]";
	}
}
