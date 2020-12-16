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

import com.ecfeed.core.utils.ExceptionHelper;

public enum ConstraintType {

	EXTENDED_FILTER("EF", "Implication"),
	BASIC_FILTER("BF",  "Invariant"),
	ASSIGNMENT("AS",  "Expected output");

	private final String fDescription;
	private final String fCode;

	ConstraintType(String code, String description) {

		fCode = code;
		fDescription = description;
	}

	public String getCode() {
		
		return fCode;
	}
	
	public String getDescription() {

		return fDescription;
	}

	public static String[] getDescriptions() {

		return new String[] {EXTENDED_FILTER.getDescription(), BASIC_FILTER.getDescription(), ASSIGNMENT.getDescription()};
	}

	public static ConstraintType getDefaultType() {

		return BASIC_FILTER;
	}
	
	public static ConstraintType parseDescription(String description) {

		if (description == null) {
			reportExceptionInvalidConstraintType();
			return null;
		}

		if (description.equals(EXTENDED_FILTER.getDescription())) {
			return EXTENDED_FILTER;
		}

		if (description.equals(BASIC_FILTER.getDescription())) {
			return BASIC_FILTER;
		}

		if (description.equals(ASSIGNMENT.getDescription())) {
			return ASSIGNMENT;
		}
		
		reportExceptionInvalidConstraintType();
		return null;
	}

	public static ConstraintType parseCode(String code) {

		if (code == null) {
			reportExceptionInvalidConstraintType();
			return null;
		}

		if (code.equals(EXTENDED_FILTER.getCode())) {
			return EXTENDED_FILTER;
		}

		if (code.equals(BASIC_FILTER.getCode())) {
			return BASIC_FILTER;
		}

		if (code.equals(ASSIGNMENT.getCode())) {
			return ASSIGNMENT;
		}
		
		reportExceptionInvalidConstraintType();
		return null;
	}
	
	private static void reportExceptionInvalidConstraintType() {

		ExceptionHelper.reportRuntimeException("Invalid constraint type.");
	}

}