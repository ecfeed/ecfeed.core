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

	private static final int fMaxDepth = 5;
	private static final String causedBy = " Caused by: ";
	private static final String fNoException = "NO-EXCEPTION";

	public static void reportRuntimeException(String message) {

		SystemLogger.logThrow(message);
		throw new RuntimeException(message);
	}

	public static void reportRuntimeException(String message, Exception e) {

		RuntimeException runtimeException = new RuntimeException(message, e);

		SystemLogger.logThrow(createErrorMessage(e, true));
		throw runtimeException;
	}

	public static void reportRuntimeException(Exception e) {

		String exceptionName = e.getClass().getName();
		reportRuntimeException(exceptionName, e);
	}

	public static void reportRuntimeExceptionCanNotCreateObject() {

		ExceptionHelper.reportRuntimeException("Can not create object.");
	}

	public static String createErrorMessage(Exception e) {

		return createErrorMessage(e, true);
	}
	
	public static String createErrorMessage(Exception e, boolean addClassName) {
		
		return createErrorMessage(e, addClassName, true);
	}

	public static String createErrorMessage(Exception e, boolean addClassName, boolean oneLine) {
		
		if (e == null) {
			return fNoException;
		}
		
		String exceptionSeparator = "\n";
		if (oneLine) {
			exceptionSeparator = " ";
		}

		String message = getMessage(e, addClassName);

		Throwable currentThrowable = (Throwable)e;
		int depth = 0;

		for (;;) {

			Throwable nextThrowable = currentThrowable.getCause();

			if (nextThrowable == null) {
				return message;
			}

			message += (exceptionSeparator + causedBy + getMessage(nextThrowable, addClassName) );

			currentThrowable = nextThrowable;

			depth++;

			if (depth >= fMaxDepth) {
				return message;
			}
		}
	}

	private static String getMessage(Throwable e, boolean addClassName) {

		String message = "";

		if (addClassName) {
			message += "[" + e.getClass().getName() + "] "; 
		}

		message += e.getMessage();

		return message;
	}

}
