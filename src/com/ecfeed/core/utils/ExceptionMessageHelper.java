package com.ecfeed.core.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExceptionMessageHelper {

	public enum CreateCallStack {
		YES,
		NO
	};

	public enum ExceptionStackType {
		SIMPLE,
		FULL
	};

	private static final int fMaxDepth = 100;
	private static final String typeTokenNotFound = "Token not found";
	private static final String fNoException = "NO-EXCEPTION";

	public static String createErrorMessage(Throwable throwable, ExceptionStackType exceptionStackType, CreateCallStack createCallStack) {

		if (throwable == null) {
			return fNoException;
		}

		var result = new JSONObject();

		result.put("message", createExceptionMessage(throwable, exceptionStackType));

		if (createCallStack == CreateCallStack.YES) {
			Throwable deepestThrowable = getDeepestThrowable(throwable);
			result.put("stack", createStackMessage(deepestThrowable));
		}

		return result.toString();
	}

	private static JSONObject createExceptionMessage(Throwable throwable, ExceptionStackType exceptionStackType) {

		var exceptionDescriptions = createExceptionDescriptions(throwable, exceptionStackType);

		var array = new JSONArray();

		for (ExceptionDescription exceptionDescription : exceptionDescriptions) {
			array.put(createOneMessage(exceptionDescription, exceptionStackType));
		}

		var json = new JSONObject();

		json.put("cause", array);

		return json;
	}

	public static String createOneMessage(ExceptionDescription exceptionDescription, ExceptionStackType exceptionStackType) {

		String message = getMessage(exceptionDescription, exceptionStackType);
		
		if (message.contains(typeTokenNotFound)) {
			return typeTokenNotFound + ".";
		} else {
			return message;
		}
	}

	public static String getMessage(ExceptionDescription exceptionDescription, ExceptionStackType exceptionStackType) {

		if (exceptionStackType == ExceptionStackType.SIMPLE) {
			return exceptionDescription.getShortMessage();
		} else {
			return exceptionDescription.getFullMessage(); 
		}
	}

	private static List<ExceptionDescription> createExceptionDescriptions(Throwable e, ExceptionStackType exceptionStackType) {

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

		Throwable currentThrowable = e;

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

	private static JSONObject createStackMessage(Throwable throwable) {
		StackTraceElement[] stackElements = throwable.getStackTrace();

		var cause = new JSONArray();

		for (int index = 0; index < stackElements.length; index++) {

			var stack = new JSONObject();

			StackTraceElement element = stackElements[index];

			stack.put("class", element.getClassName());
			stack.put("method", element.getMethodName());
			stack.put("line", element.getLineNumber());

			cause.put(stack);
		}

		var root = new JSONObject();

		root.put("root", cause);

		return root;
	}

	private static Throwable getDeepestThrowable(Throwable e) {

		Throwable currentThrowable = e;
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
		return createErrorMessage(e, ExceptionStackType.SIMPLE, CreateCallStack.NO);
	}

	public static String createErrorMessageWithFullStack(Exception e) {
		return createErrorMessage(e, ExceptionStackType.FULL, CreateCallStack.YES);
	}

}
