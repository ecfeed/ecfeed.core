package com.ecfeed.core.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

	public static String parseObject(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		
		try {
			output = mapper.writeValueAsString(object).replaceAll("\"", "'");
		} catch (JsonProcessingException e) {
			throw new RuntimeException("The input could not be parsed to JSON");
		}
		
		return output;
		
	}
	
}
