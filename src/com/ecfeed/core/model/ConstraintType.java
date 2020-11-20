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
	INVARIANT("Invariant");

	private final String fDescription;

	ConstraintType(String description) {

		fDescription = description;
	}

	public String getDescription() {

		return fDescription;
	}

	public static String[] getDescriptions() {

		return new String[] {IMPLICATION.getDescription(), INVARIANT.getDescription()};
	}

	public static String getDefauldDescription() {

		return IMPLICATION.getDescription();
	}

	public static ConstraintType parse(String text) {

		if (text == null) {
			reportExceptionInvalidConstraintType();
			return null;
		}

		if (text.equals(IMPLICATION.getDescription())) {
			return IMPLICATION;
		}

		reportExceptionInvalidConstraintType();
		return null;
	}

	private static void reportExceptionInvalidConstraintType() {

		ExceptionHelper.reportRuntimeException("Invalid constraint type.");
	}

}