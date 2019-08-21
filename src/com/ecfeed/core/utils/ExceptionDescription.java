/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

public class ExceptionDescription {

	private static final String EXCEPTION_TAG = "Exception: ";
	private String fShortMessage;
	private String fFullMessage;

	public ExceptionDescription(Throwable throwable) {

		fFullMessage = throwable.getMessage();
		fShortMessage = createMessageDescr(throwable);
	}

	public String getShortMessage() {

		return fShortMessage;
	}

	public String getFullMessage() {

		return fFullMessage;
	}

	private String createMessageDescr(Throwable throwable) {

		if (throwable == null) {
			return ("Invalid throwable: null");
		}
		
		String rawMessage = throwable.getMessage();
		
		if (rawMessage == null) {
			return "No exception message.";
		}

		int index = rawMessage.lastIndexOf(EXCEPTION_TAG);

		if (index == -1) {
			return rawMessage;
		}

		return rawMessage.substring(index + EXCEPTION_TAG.length());
	}

}
