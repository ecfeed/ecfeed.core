/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

class NodePropertyDef {
	private String fName;
	private String fType;
	NodePropertyValueSet fValueSet;

	NodePropertyDef(String name, String type, String defaultValue, String[] possibleValues) {
		fName = name;
		fType = type;
		fValueSet = new NodePropertyValueSet(defaultValue, possibleValues);
	}

	NodePropertyDef(String name, String type, NodePropertyValueSet valueSet) {
		fName = name;
		fType = type;
		fValueSet = valueSet;
	}

	String getName() {
		return fName;
	}

	String getType() {
		return fType;
	}

	String getDefaultValue() {
		return fValueSet.getDefaultValue();
	}	

	String[] getPossibleValues() {
		return fValueSet.getPossibleValues();
	}

	NodePropertyValueSet getValueSet() {
		return fValueSet;
	}

	boolean matchesPossibleValue(String valueToMatch) {
		return fValueSet.isOneOfPossibleValues(valueToMatch);
	}

	boolean matchesPossibleValueIgnoreCase(String valueToMatch) {
		return fValueSet.isOneOfPossibleValuesIgnoreCase(valueToMatch);
	}	
}
