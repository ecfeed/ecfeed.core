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

import com.ecfeed.core.utils.StringTabHelper;

public class NodePropertyValueSet {
	private String fDefaultValue;
	private String[] fPossibleValues;

	public NodePropertyValueSet(String defaultValue, String[] possibleValues) {

		fDefaultValue = defaultValue;
		fPossibleValues = possibleValues;
	}

	public void setDefaultValue(String value) {
		fDefaultValue = value;
	}

	public String getDefaultValue() {

		return fDefaultValue;
	}	

	public String[] getPossibleValues() {

		return fPossibleValues;
	}

	public boolean isOneOfPossibleValues(String valueToMatch) {

		if (StringTabHelper.isOneOfValues(valueToMatch, fPossibleValues)) {
			return true;
		}
		return false;
	}

	public boolean isOneOfPossibleValuesIgnoreCase(String valueToMatch) {

		if (StringTabHelper.isOneOfValuesIgnoreCase(valueToMatch, fPossibleValues)) {
			return true;
		}
		return false;
	}	

	public static NodePropertyValueSet intersect(NodePropertyValueSet set1, NodePropertyValueSet set2) {

		String[] possibleValues = StringTabHelper.intersect(set1.getPossibleValues(), set2.getPossibleValues());

		String defaultValue = null;
		if (set1.getDefaultValue().equals(set2.getDefaultValue())) {
			defaultValue = set1.getDefaultValue();
		}

		return new NodePropertyValueSet(defaultValue, possibleValues); 
	}

}

