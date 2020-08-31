package com.ecfeed.core.utils;

public enum ExtLanguage {

	JAVA("JAVA"),
	SIMPLE("SIMPLE");

	private final String fCode;

	ExtLanguage(String code) {

		fCode = code;
	}

	public String getCode() {

		return fCode;
	}
	
	public static ExtLanguage parse(String viewModeName) {
		
		if (viewModeName == null) {
			reportExceptionInvalidModeName();
			return null;
		}
		
		if (viewModeName.equals(JAVA.getCode())) {
			return JAVA;
		}
		
		if (viewModeName.equals(SIMPLE.getCode())) {
			return SIMPLE;
		}
		
		reportExceptionInvalidModeName();
		return null;
	}

	private static void reportExceptionInvalidModeName() {
		
		ExceptionHelper.reportRuntimeException("Invalid external language name.");
	}

}
