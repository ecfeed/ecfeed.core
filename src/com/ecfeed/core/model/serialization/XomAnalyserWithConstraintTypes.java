/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

public abstract class XomAnalyserWithConstraintTypes extends XomAnalyserWithNewNodeNames {

	public XomAnalyserWithConstraintTypes() {
		super();
	}

//	// TODO - REMOVE
//	@Override
//	protected ConstraintType getConstraintType(Element element, ListOfStrings errorList) throws ParserException {
//		
//		String type = element.getAttributeValue(SerializationConstants.PROPERTY_ATTRIBUTE_TYPE);
//		
//		if (type == null) {
//			return ConstraintType.EXTENDED_FILTER;
//		}
//		
//		ConstraintType constraintType = null;
//		
//		try {
//			constraintType = ConstraintType.parseCode(type);
//		} catch (Exception e) {
//			errorList.add("Cannot parse constraint type.");
//			ParserException.create();
//		}
//		
//		return constraintType;
//	}

}
