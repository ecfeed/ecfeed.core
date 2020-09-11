package com.ecfeed.core.utils;

public class TestHelper {

	public static void checkExceptionMessage(Exception e, String... expectedItems) { 

		String message = e.getMessage();

		int index = 0;
		int itemIndex = 0;

		for (String expectedItem : expectedItems ) {

			if (!message.contains(expectedItem)) {
				ExceptionHelper.reportRuntimeException("Expected item: " + expectedItem + " not found.");
			}

			itemIndex = message.indexOf(expectedItem);

			if (itemIndex < index) {
				ExceptionHelper.reportRuntimeException("Invalid order of expected items.");
			}

			index = itemIndex;
		}
	}

}
