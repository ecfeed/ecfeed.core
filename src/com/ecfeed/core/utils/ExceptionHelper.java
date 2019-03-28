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

public class ExceptionHelper {

	public static void reportRuntimeException(String message) {
		SystemLogger.logThrow(message);
		throw new RuntimeException(message);
	}

	public static void reportRuntimeException(String message, Exception e) {

		RuntimeException runtimeException = new RuntimeException(message);
		runtimeException.addSuppressed(e);

		SystemLogger.logThrow(message);
		throw runtimeException;
	}

	public static void reportRuntimeException(Exception e) {

		String exceptionName = e.getClass().getName();
		reportRuntimeException(exceptionName, e);
	}

	public static void reportRuntimeExceptionCanNotCreateObject() {
		ExceptionHelper.reportRuntimeException("Can not create object.");
	}

	public static String createErrorMessage(String basicMessage, Exception e) {

		String causedBy = " Caused by: ";

		String message = basicMessage + causedBy + e.getMessage();

		if (e.getCause() != null) {
			message += (causedBy + e.getCause().getMessage());
		}

		return message;
	}

}
