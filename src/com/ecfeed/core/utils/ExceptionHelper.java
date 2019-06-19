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
	private static final String causedBy = "Caused by: ";
	private static final String fNoException = "NO-EXCEPTION";

	public static void reportRuntimeException(String message) {

		throw new RuntimeException(message);
	}

	public static void reportRuntimeException(String message, Exception e) {

		throw new RuntimeException(message, e);
	}

	public static void reportRuntimeException(Exception e) {

		String exceptionName = e.getClass().getName();
		reportRuntimeException(exceptionName, e);
	}

	public static void reportRuntimeExceptionCanNotCreateObject() {

		ExceptionHelper.reportRuntimeException("Can not create object.");
	}

	public static String createErrorMessage(Throwable e, boolean addClassName) {

		return createErrorMessage(e, addClassName, true);
	}

	public static String createErrorMessage(Exception e) {

		return createErrorMessage(e, true);
	}

	public static String createErrorMessage(String message, Exception e) {

		RuntimeException runtimeException = new RuntimeException(message, e);

		return createErrorMessage(runtimeException, true);
	}

	public static String createErrorMessage(Throwable e, boolean addClassName, boolean oneLine) {

		if (e == null) {
			return fNoException;
		}

		String exceptionSeparator = "\n";
		if (oneLine) {
			exceptionSeparator = " ";
		}

		String errorMessage = createExceptionMessage(e, addClassName, exceptionSeparator);

		Throwable deepestThrowable = getDeepestThrowable(e);

		String stack = getStack(deepestThrowable);

		return errorMessage + "\n" + stack;
	}

	private static String createExceptionMessage(Throwable e, boolean addClassName, String exceptionSeparator) {

		final String spaces = "    ";

		String message = "Exceptions stack: \n" + spaces + getMessage(e, addClassName);

		Throwable currentThrowable = (Throwable) e;
		int depth = 0;

		for ( ; ; ) {

			Throwable nextThrowable = currentThrowable.getCause();

			if (nextThrowable == null) {
				return message;
			}

			message += (exceptionSeparator + spaces + causedBy + getMessage(nextThrowable, addClassName));

			currentThrowable = nextThrowable;

			depth++;

			if (depth >= fMaxDepth) {
				return message;
			}
		}
	}

	private static String getStack(Throwable throwable) {

		String result = "Call stack of root cause: \n";

		StackTraceElement[] stackElements = throwable.getStackTrace();

		for (int index = stackElements.length - 1; index >= 0 ; index--) {

			StackTraceElement element = stackElements[index];

			result = result + 
					"    Class: " + element.getClassName() +
					" Method: " + element.getMethodName() +
					" Line: " + element.getLineNumber() + "\n";
		}

		return result;
	}

	private static Throwable getDeepestThrowable(Throwable e) {

		Throwable currentThrowable = (Throwable) e;
		int depth = 0;

		for (; ; ) {

			Throwable nextThrowable = currentThrowable.getCause();

			if (nextThrowable == null) {
				return currentThrowable;
			}

			currentThrowable = nextThrowable;

			depth++;

			if (depth >= fMaxDepth) {
				return null;
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
