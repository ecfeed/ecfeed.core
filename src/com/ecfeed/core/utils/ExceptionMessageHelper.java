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
	
	public enum MessageFormat {
		TEXT,
		JSON
	};
	
	public enum StackType {
		SIMPLE,
		FULL
	};

	private static final int fMaxDepth = 100;
	private static final String typeTokenNotFound = "Token not found";
	private static final String fNoException = "NO-EXCEPTION";

	public static String createErrorMessage(Throwable throwable, StackType stackType, 
			CreateCallStack createCallStack, MessageFormat messageFormat) {

		if (throwable == null) {
			return fNoException;
		}

		switch (messageFormat) {
		case JSON:
			return createExceptionMessageJSON(throwable, createCallStack, stackType);
		case TEXT:
			return createExceptionMessageTEXT(throwable, createCallStack, stackType);
		}
		
		return "";
	}

	private static String createExceptionMessageJSON(Throwable throwable, CreateCallStack createCallStack, StackType exceptionStackType) {
		List<ExceptionDescription> exceptionDescriptions = createExceptionDescriptions(throwable, exceptionStackType);
		JSONObject result = new JSONObject();

		JSONArray cause = new JSONArray();
		for (ExceptionDescription exceptionDescription : exceptionDescriptions) {
			cause.put(createOneMessage(exceptionDescription, exceptionStackType));
		}

		JSONObject message = new JSONObject();

		message.put("cause", cause);
		result.put("message", createExceptionMessage(throwable, exceptionStackType));

		if (createCallStack == CreateCallStack.YES) {
			Throwable deepestThrowable = getDeepestThrowable(throwable);
			result.put("stack", createStackMessageJSON(deepestThrowable));
		}

		return result.toString();
	}
	
	private static String createExceptionMessageTEXT(Throwable throwable, CreateCallStack createCallStack, StackType exceptionStackType) {
		List<ExceptionDescription> exceptionDescriptions = createExceptionDescriptions(throwable, exceptionStackType);
		StringBuilder result = new StringBuilder();
		
		for (ExceptionDescription exceptionDescription : exceptionDescriptions) {
			result.append(createOneMessage(exceptionDescription, exceptionStackType) + "\n");
		}

		if (createCallStack == CreateCallStack.YES) {
			Throwable deepestThrowable = getDeepestThrowable(throwable);
			result.append(createStackMessageTEXT(deepestThrowable));
		}

		return result.toString();
		
	}
	
	private static JSONObject createExceptionMessage(Throwable throwable, StackType exceptionStackType) {

		List<ExceptionDescription> exceptionDescriptions = createExceptionDescriptions(throwable, exceptionStackType);

		JSONArray array = new JSONArray();

		for (ExceptionDescription exceptionDescription : exceptionDescriptions) {
			array.put(createOneMessage(exceptionDescription, exceptionStackType));
		}

		JSONObject json = new JSONObject();

		json.put("cause", array);

		return json;
	}

	public static String createOneMessage(ExceptionDescription exceptionDescription, StackType exceptionStackType) {

		String message = getMessage(exceptionDescription, exceptionStackType);
		
		if (message.contains(typeTokenNotFound)) {
			return typeTokenNotFound + ".";
		} else {
			return message;
		}
	}

	public static String getMessage(ExceptionDescription exceptionDescription, StackType exceptionStackType) {

		if (exceptionStackType == StackType.SIMPLE) {
			return exceptionDescription.getShortMessage();
		} else {
			return exceptionDescription.getFullMessage(); 
		}
	}

	private static List<ExceptionDescription> createExceptionDescriptions(Throwable e, StackType exceptionStackType) {

		List<ExceptionDescription> exceptionDescriptions = createExceptionDescriptions(e);

		if (exceptionStackType == StackType.SIMPLE) {
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

	private static JSONObject createStackMessageJSON(Throwable throwable) {
		StackTraceElement[] stackElements = throwable.getStackTrace();

		JSONArray cause = new JSONArray();

		for (int index = 0; index < stackElements.length; index++) {

			JSONObject stack = new JSONObject();

			StackTraceElement element = stackElements[index];

			stack.put("class", element.getClassName());
			stack.put("method", element.getMethodName());
			stack.put("line", element.getLineNumber());

			cause.put(stack);
		}

		JSONObject root = new JSONObject();

		root.put("root", cause);

		return root;
	}
	
	private static String createStackMessageTEXT(Throwable throwable) {
		StackTraceElement[] stackElements = throwable.getStackTrace();
		StringBuilder message = new StringBuilder();

		for (int index = 0; index < stackElements.length; index++) {

			StackTraceElement element = stackElements[index];

			message.append("Class: " + element.getClassName() + ", ");
			message.append("Method: " + element.getMethodName() + ", ");
			message.append("Line: " + element.getLineNumber() + "\n");
		}

		return message.toString();
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

	public static String createErrorMessage(Exception e, MessageFormat format, boolean fullStack) {

		if (fullStack) {
			return createErrorMessage(e, StackType.FULL, CreateCallStack.YES, format);
		} else {
			return createErrorMessage(e, StackType.SIMPLE, CreateCallStack.NO, format);
		}
	}
	
	public static String createErrorMessage(Throwable e) {

		return createErrorMessage(e, StackType.FULL, CreateCallStack.NO, MessageFormat.TEXT);
	}

	public static String createErrorMessage(String message, Throwable e) {
		RuntimeException runtimeException = new RuntimeException(message, e);

		return createErrorMessage(runtimeException);
	}

}
