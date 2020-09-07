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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexHelper {
	
	public static final String REGEX_SPECIAL_CHARACTER = "\u039B";
	
	public static final String REGEX_JAVA_IDENTIFIER = "[" + REGEX_SPECIAL_CHARACTER + "A-Za-z_$][" + REGEX_SPECIAL_CHARACTER + "A-Za-z0-9_$]*";
	public static final String REGEX_ALPHANUMERIC_WITH_SPACES_64 = "[" + REGEX_SPECIAL_CHARACTER + "A-Za-z0-9_\\-][" + REGEX_SPECIAL_CHARACTER + "A-Za-z0-9_\\- ]{0,63}";	
	public static final String REGEX_ROOT_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PACKAGE_NAME = "(\\.|((" + REGEX_JAVA_IDENTIFIER + ")\\.)*)";
	public static final String REGEX_CLASS_NODE_NAME = REGEX_PACKAGE_NAME + "*"+ REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_METHOD_NODE_NAME = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_CATEGORY_NODE_NAME = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_CATEGORY_TYPE_NAME = REGEX_CLASS_NODE_NAME;
	public static final String REGEX_CONSTRAINT_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_TEST_CASE_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PARTITION_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PARTITION_LABEL = REGEX_ALPHANUMERIC_WITH_SPACES_64;

	public static final String REGEX_USER_TYPE_VALUE = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_STRING_TYPE_VALUE = "[" + REGEX_SPECIAL_CHARACTER + "A-Za-z1-9 !@#$%^&*()_+=;':,.<>/?]{0,1024}";
	public static final String REGEX_CHAR_TYPE_VALUE = "[" + REGEX_SPECIAL_CHARACTER + "A-Za-z1-9 !@#$%^&*()_+=;':,.<>/?]";
	
	public static final String JAVA_CHOICE_NAME_REGEX_PROBLEM = 
			"Choice name must be 1 to 64 characters long.\n"
			+ "It should contain alphanumeric characters, spaces, or -_ .\n"
			+ "It must not start with space.";
	
	public static final String JAVA_MODEL_NAME_REGEX_PROBLEM = 
			"Model name must contain between 1 and 64 alphanumeric characters or spaces.\n"
			+ " The model name must not start with space.";
	
	public static String createMessageAllowedCharsInClass(ExtLanguage extLanguage) { // TODO SIMPLE-VIEW - refactor similar methods

		String separator;
		
		if (extLanguage == ExtLanguage.JAVA) {
			separator = "_";
		} else {
			separator = "[SPACE]";
		}
		
		String message = "Class name should contain alphanumeric charactes or: " + separator	+ " $ .";	
		return message;
	}

	public static String createMessageAllowedCharsForMethod(ExtLanguage extLanguage) {

		String separator;
		
		if (extLanguage == ExtLanguage.JAVA) {
			separator = "_";
		} else {
			separator = "[SPACE]";
		}
		
		String message = "Method name should contain alphanumeric charactes or: " + separator	+ " $ .";	
		return message;
	}
	
	public static List<String> getMatchingSubstrings(String sourceString, String regexPattern) {

		List<String> substrings = new ArrayList<String>();

		Pattern pattern = Pattern.compile(regexPattern);
		Matcher matcher = pattern.matcher(sourceString);

		while(matcher.find()) {
			substrings.add(matcher.group());
		}

		return substrings;
	}


	public static String getOneMatchingSubstring(String sourceString, String regexPattern) {

		List<String> substrings = getMatchingSubstrings(sourceString, regexPattern);

		if (substrings.size() == 1) {
			return substrings.get(0);
		}

		return null;
	}

}
