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

public enum AbstractStatementFunction {

	PRECONDITION("PRECONDITION"),
	POSTCONDITION("POSTCONDITION");

	private final String fCode;

	AbstractStatementFunction(String code) {

		fCode = code;
	}

	public String getCode() {

		return fCode;
	}

	public static AbstractStatementFunction parse(String text) {

		if (text == null) {
			reportExceptionInvalidStatementFunction();
			return null;
		}

		if (text.equals(PRECONDITION.getCode())) {
			return PRECONDITION;
		}

		if (text.equals(POSTCONDITION.getCode())) {
			return POSTCONDITION;
		}
		
		reportExceptionInvalidStatementFunction();
		return null;
	}

	private static void reportExceptionInvalidStatementFunction() {

		ExceptionHelper.reportRuntimeException("Invalid statement function.");
	}

}