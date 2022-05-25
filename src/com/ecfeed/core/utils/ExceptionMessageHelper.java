package com.ecfeed.core.utils;

import java.util.ArrayList;
import java.util.List;

public class ExceptionMessageHelper {

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
	private static final String typeTokenNotFound = "Token not found";
	private static final String fNoException = "NO-EXCEPTION";	

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
			result += causedBy;
		}

		String message = getMessage(exceptionDescription, exceptionStackType);
		
		if (message.contains(typeTokenNotFound)) {
			result += (typeTokenNotFound + ".");
		} else {
			result += message;
		}
		
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

	public static String createSimpleErrorMessageWithoutStack(Exception e) {
		return createErrorMessage(
				e, LineSeparationType.ONE_LINE, ExceptionStackType.SIMPLE, CreateCallStack.NO);
	}

	public static String createErrorMessageWithFullStack(Exception e) {
		return createErrorMessage(
				e, LineSeparationType.MULTI_LINE, ExceptionStackType.FULL, CreateCallStack.YES);
	}
	

}
