package com.ecfeed.core.json;

import com.ecfeed.core.utils.TestCasesRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestCasesRequestParser {

	public static TestCasesRequest parseRequest(String request) { 

		try {
			ObjectMapper mapper = new ObjectMapper();

			return mapper.reader().forType(TestCasesRequest.class).readValue(request);
		} catch (Exception e) {
			throw new RuntimeException("Can not parse request for test cases. " + e.getMessage());
		}

	}	

}