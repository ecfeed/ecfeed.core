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

import com.ecfeed.core.exception.GeneratorExceptionClient;
import com.ecfeed.core.exception.GeneratorExceptionServer;

import java.util.ArrayList;
import java.util.List;

// TODO SIMPLE-VIEW remove from code: ExceptionHelper.reportRuntimeException(e.getMessage()); and replace with ExceptionHelper.reportRuntimeException(e.getMessage()); or ExceptionHelper.reportRuntimeException(message2, e);

public class ExceptionHelper {

	public enum LineSeparationType {
		ONE_LINE,
		MULTI_LINE
	};

	public enum CreateCallStack {
		YES,
		NO
	};

	public enum ExceptionStackType {
		SIMPLE,
		FULL
	};

	private static final int fMaxDepth = 100;
	private static final String causedBy = "Caused by: ";
	private static final String fNoException = "NO-EXCEPTION";

	public static void reportClientException(String message) {

		throw new GeneratorExceptionClient(message);
	}

	public static void reportClientException(String message, Throwable e) {

		throw new GeneratorExceptionClient(message, e);
	}

	public static void reportServerException(String message) {

		throw new GeneratorExceptionServer(message);
	}

	public static void reportServerException(String message, Throwable e) {

		throw new GeneratorExceptionServer(message, e);
	}

	public static void reportRuntimeException(String message) {

		throw new RuntimeException(message);
	}

	public static void reportRuntimeException(String message, Exception e) {

		throw new RuntimeException(message, e);
	}

	public static void reportRuntimeException(Exception e) {

		//		String exceptionName = e.getClass().getName();
		//		reportRuntimeException(exceptionName, e);
		throw new RuntimeException(e);
	}

	public static void reportRuntimeExceptionCanNotCreateObject() {

		ExceptionHelper.reportRuntimeException("Can not create object.");
	}

	public static String createErrorMessage(Throwable e) {

		return createErrorMessage(e, LineSeparationType.ONE_LINE, ExceptionStackType.FULL, CreateCallStack.YES);
	}

	public static String createErrorMessage(Exception e) {

		return createErrorMessage(e, LineSeparationType.ONE_LINE, ExceptionStackType.FULL, CreateCallStack.YES);
	}

	public static String createErrorMessage(String message, Exception e) {

		RuntimeException runtimeException = new RuntimeException(message, e);

		return createErrorMessage(runtimeException);
	}

	public static String createErrorMessage(
			Throwable throwable, 
			LineSeparationType lineSeparationType,
			ExceptionStackType exceptionStackType,
			CreateCallStack createCallStack) {

		if (throwable == null) {
			return fNoException;
		}

		String exceptionSeparator = createExceptionSeparator(lineSeparationType);

		String errorMessage = createExceptionMessage(throwable, exceptionStackType, exceptionSeparator);

		if (createCallStack == CreateCallStack.YES) {

			Throwable deepestThrowable = getDeepestThrowable(throwable);

			String stackMessage = createStackMessage(deepestThrowable);

			return errorMessage + "\n" + stackMessage;
		}

		return errorMessage;
	}

	public static String createExceptionSeparator(LineSeparationType lineSeparationType) {

		if (lineSeparationType == LineSeparationType.ONE_LINE) {
			return " ";
		}

		return "\n";
	}

	private static String createExceptionMessage(
			Throwable throwable, 
			ExceptionStackType exceptionStackType, 
			String exceptionSeparator) {

		List<ExceptionDescription> exceptionDescriptions = 
				createExceptionDescriptions(throwable, exceptionStackType);

		String message = "";
		boolean isFirstMessage = true;

		for (ExceptionDescription exceptionDescription : exceptionDescriptions) {
			message += createOneMessage(exceptionDescription, exceptionSeparator, exceptionStackType, isFirstMessage);
			isFirstMessage = false;
		}

		return message; 
	}

	public static String createOneMessage(
			ExceptionDescription exceptionDescription, 
			String exceptionSeparator,
			ExceptionStackType exceptionStackType, 
			boolean isFirstMessage) {

		String result = "";

		if (!isFirstMessage) {
			result += ("  " + causedBy);
		}

		result += getMessage(exceptionDescription, exceptionStackType);
		result += exceptionSeparator;

		return result;
	}

	public static String getMessage(ExceptionDescription exceptionDescription, ExceptionStackType exceptionStackType) {

		if (exceptionStackType == ExceptionStackType.SIMPLE) {
			return exceptionDescription.getShortMessage();
		} else {
			return exceptionDescription.getFullMessage(); 
		}
	}

	private static List<ExceptionDescription> createExceptionDescriptions(
			Throwable e, ExceptionStackType exceptionStackType) {

		List<ExceptionDescription> exceptionDescriptions = createExceptionDescriptions(e);

		if (exceptionStackType == ExceptionStackType.SIMPLE) {
			exceptionDescriptions = compressDescriptions(exceptionDescriptions);
		}

		return exceptionDescriptions;
	}

	private static List<ExceptionDescription> createExceptionDescriptions(Throwable e) {

		List<ExceptionDescription> exceptionDescriptions = new ArrayList<>();

		Throwable currentThrowable = (Throwable) e;

		for ( ; ; ) {

			ExceptionDescription exceptionDescription = new ExceptionDescription(currentThrowable);

			exceptionDescriptions.add(exceptionDescription);

			Throwable nextThrowable = currentThrowable.getCause();

			if (nextThrowable == null) {
				return exceptionDescriptions;
			}

			currentThrowable = nextThrowable;
		}
	}

	private static List<ExceptionDescription> compressDescriptions(List<ExceptionDescription> exceptionDescriptions) {

		List<ExceptionDescription> result = new ArrayList<>();

		String lastMessage = null;

		for (ExceptionDescription exceptionDescription : exceptionDescriptions) {

			String curentMessage = exceptionDescription.getShortMessage();

			if (!StringHelper.isEqual(lastMessage, curentMessage)) {
				result.add(exceptionDescription);
				lastMessage = curentMessage;
			}
		}

		return result;
	}

	private static String createStackMessage(Throwable throwable) {

		String result = "Call stack of root cause: \n";

		StackTraceElement[] stackElements = throwable.getStackTrace();

		for (int index = 0; index < stackElements.length; index++) {

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

}
