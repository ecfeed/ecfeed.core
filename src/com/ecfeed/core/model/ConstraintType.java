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

	IMPLICATION("IMPLICATION");

	private final String fCode;

	ConstraintType(String code) {

		fCode = code;
	}

	public String getCode() {

		return fCode;
	}

	public static ConstraintType parse(String viewModeName) {

		if (viewModeName == null) {
			reportExceptionInvalidModeName();
			return null;
		}

		if (viewModeName.equals(IMPLICATION.getCode())) {
			return IMPLICATION;
		}

		reportExceptionInvalidModeName();
		return null;
	}

	private static void reportExceptionInvalidModeName() {

		ExceptionHelper.reportRuntimeException("Invalid external language name.");
	}

}