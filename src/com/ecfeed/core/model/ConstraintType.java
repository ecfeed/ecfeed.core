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

	IMPLICATION("Implication"),
	INVARIANT("Invariant"),
	EXPECTED_OUTPUT("Expected output");

	private final String fDescription;

	ConstraintType(String description) {

		fDescription = description;
	}

	public String getDescription() {

		return fDescription;
	}

	public static String[] getDescriptions() {

		return new String[] {IMPLICATION.getDescription(), INVARIANT.getDescription(), EXPECTED_OUTPUT.getDescription()};
	}

	public static ConstraintType getDefaultType() {

		return IMPLICATION;
	}
	
	public static ConstraintType parse(String text) {

		if (text == null) {
			reportExceptionInvalidConstraintType();
			return null;
		}

		if (text.equals(IMPLICATION.getDescription())) {
			return IMPLICATION;
		}

		if (text.equals(INVARIANT.getDescription())) {
			return INVARIANT;
		}

		if (text.equals(EXPECTED_OUTPUT.getDescription())) {
			return EXPECTED_OUTPUT;
		}
		
		reportExceptionInvalidConstraintType();
		return null;
	}

	private static void reportExceptionInvalidConstraintType() {

		ExceptionHelper.reportRuntimeException("Invalid constraint type.");
	}

}