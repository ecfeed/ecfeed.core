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

	EXTENDED_FILTER("Implication"),
	BASIC_FILTER("Invariant"),
	ASSIGNMENT("Expected output");

	private final String fDescription;

	ConstraintType(String description) {

		fDescription = description;
	}

	public String getDescription() {

		return fDescription;
	}

	public static String[] getDescriptions() {

		return new String[] {EXTENDED_FILTER.getDescription(), BASIC_FILTER.getDescription(), ASSIGNMENT.getDescription()};
	}

	public static ConstraintType getDefaultType() {

		return EXTENDED_FILTER;
	}
	
	public static ConstraintType parse(String text) {

		if (text == null) {
			reportExceptionInvalidConstraintType();
			return null;
		}

		if (text.equals(EXTENDED_FILTER.getDescription())) {
			return EXTENDED_FILTER;
		}

		if (text.equals(BASIC_FILTER.getDescription())) {
			return BASIC_FILTER;
		}

		if (text.equals(ASSIGNMENT.getDescription())) {
			return ASSIGNMENT;
		}
		
		reportExceptionInvalidConstraintType();
		return null;
	}

	private static void reportExceptionInvalidConstraintType() {

		ExceptionHelper.reportRuntimeException("Invalid constraint type.");
	}

}