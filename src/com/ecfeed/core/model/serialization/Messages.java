/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import java.io.IOException;

import nu.xom.Element;
import nu.xom.ParsingException;

public class Messages {

	public static final String WRONG_ROOT_ELEMENT_TAG = 
			"Parsing error: The XML root element must be " + SerializationConstants.ROOT_NODE_NAME;

	public static final String PARSING_EXCEPTION(ParsingException e) {
		return "Parsing exception in line " + e.getLineNumber() + ": " + e.getMessage();
	}

	public static String IO_EXCEPTION(IOException e) {
		return "IO exception: " + e.getMessage();
	}

	public static String MISSING_ATTRIBUTE(Element element, String attribute) {
		return "Tag " + element.getLocalName() + " does not contain expected \"" + attribute + "\" attribute";
	}

	public static String WRONG_CHILD_ELEMENT_TYPE(Element element, String childLocalName) {
		return "Tag " + element.getLocalName() + " mustn't contain " + childLocalName + " children";
	}

	public static String MALFORMED_CONSTRAINT_NODE_DEFINITION(String methodName, String constraintName) {
		return "Error while parsing constraint " + constraintName + " in method " + methodName 
				+ ". Constraint tag must contain Precondition and Postcondition children, that consist of single statement.";
	}

	public static String WRONG_STATEMENT_ARRAY_OPERATOR(String methodName, String operator) {
		return "Error while parsing statement array in method " + methodName 
				+ ". Operator " + operator + " is not allowed";
	}

	public static String WRONG_STATIC_STATEMENT_VALUE(String value) {
		return "Forbidden value of static statement: " + value;
	}

	public static String WRONG_OR_MISSING_RELATION_FORMAT(String relation) {
		return "Forbidden relation value in statement: " + relation;
	}

	public static String WRONG_PARTITION_NAME(String choiceName, String parameterName, String methodName) {
		return "Choice " + choiceName + " does not exist for parameter " + parameterName + " in method " + methodName;
	}

	public static String WRONG_PARAMETER_NAME(String parameterName, String methodName) {
		return "Parameter " + parameterName + " does not exist in method " + methodName;
	}	

	public static String WRONG_NUMBER_OF_TEST_PAREMETERS(String testSuiteName){
		return "Number of test parameters in test case of " + testSuiteName 
				+ "suite is different than number of parameters in parent method";
	}

	public static String TEST_VALUE_NAME_ATTRIBUTE_MISSING(String testSuiteName) {
		return "Missing test value attribute in a test case in " + testSuiteName + " suite";
	}

	public static String PARTITION_DOES_NOT_EXIST(String parameterName, String choiceName) {
		return "Choice " + choiceName + " is not defined for parameter " + parameterName;
	}

	public static final String MISSING_VALUE_ATTRIBUTE_IN_TEST_CASE_ELEMENT = "The expected test parameter element misses value attribute";

}
