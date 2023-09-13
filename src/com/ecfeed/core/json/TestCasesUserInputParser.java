package com.ecfeed.core.json;

import com.ecfeed.core.utils.TestCasesUserInput;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestCasesUserInputParser {

	public static TestCasesUserInput parseRequest(String request) { 

		try {
			ObjectMapper mapper = new ObjectMapper();

			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			return mapper.reader().forType(TestCasesUserInput.class).readValue(request);
		} catch (Exception e) {
			throw new RuntimeException("Can not parse user input! " + e.getMessage());
		}

	}	

}