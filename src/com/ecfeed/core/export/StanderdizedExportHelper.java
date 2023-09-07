package com.ecfeed.core.export;

import java.util.HashMap;
import java.util.Map;

public class StanderdizedExportHelper {
	
	public static Map<String, String> getParameters(String template) {
		Map<String, String> parameters = new HashMap<>();
		
		String[] lines = template.split("\n");
		
		for (String line : lines) {
			
			if (line.contains(":")) {
				line = line.replaceAll(" ", "").replaceAll("\t", "");
				String[] elements = line.split(":");
				parameters.put(elements[0], elements[1]);
			}
		}
		
		return parameters;
	}

}
