package com.ecfeed.core.json;

import com.ecfeed.core.utils.TestCasesUserInput;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestCasesUserInputParser {

	public static TestCasesUserInput parseRequest(String request) { 

		try {
			ObjectMapper mapper = new ObjectMapper();

			// TODO - remove if not needed
			// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			return mapper.reader().forType(TestCasesUserInput.class).readValue(request);
		} catch (Exception e) {
			throw new RuntimeException("Can not parse user input. " + e.getMessage());
		}

	}	

}