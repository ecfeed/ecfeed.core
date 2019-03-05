/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MapTypeTextFile {

	private final Map<String, String> fConfigMap;

	public MapTypeTextFile(String filePath) throws Exception {

		fConfigMap = createMapFromFile(filePath);
	}

	public String getValue(String tag) {

		return fConfigMap.get(tag);
	}

	public boolean getValueAsBoolean(String tag) {

		String value = fConfigMap.get(tag);

		if (value == null) {
			return false;
		}

		if (value.equals("true")) {
			return true;
		}

		return false;
	}

	private Map<String, String> createMapFromFile(String filePath) throws Exception {

		try (InputStream inputStream = new FileInputStream(filePath)) {

			return createMapFromStream(inputStream);
		}
	}

	private Map<String, String> createMapFromStream(InputStream inputStream) throws Exception {

		Map<String, String> configMap = new HashMap<>();

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

			String line = bufferedReader.readLine();

			while (line != null) {
				addLineToConfigMap(line, configMap);
				line = bufferedReader.readLine();
			}	

			return configMap;
		}
	}

	private void addLineToConfigMap(String line, Map<String, String> inOutConfigMap) {

		final String separator = ":";

		if (!line.contains(separator)) {
			return;
		}

		String[] splitted =  line.split(separator);

		if (splitted.length == 0) {
			return;
		}

		if (splitted.length == 1) {
			inOutConfigMap.put(splitted[0], new String());
			return;
		}

		inOutConfigMap.put(splitted[0], splitted[1]);
	}

}
