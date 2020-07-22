package com.ecfeed.core.utils;

public enum ViewMode {

	JAVA("JAVA"),
	SIMPLE("SIMPLE");

	private final String fCode;

	ViewMode(String code) {

		fCode = code;
	}

	public String getCode() {

		return fCode;
	}
	
	public static ViewMode parse(String viewModeName) {
		
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
		
		ExceptionHelper.reportRuntimeException("Invalid view mode name.");
	}

}
