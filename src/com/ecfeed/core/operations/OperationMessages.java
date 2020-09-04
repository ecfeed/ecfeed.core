/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

public class OperationMessages {

	public static final String PARTITION_VALUE_PROBLEM(String value){
		return "Value " + value + " is not valid for given parameter.\n\n" +
				"Choice value must fit to type and range of the represented parameter.\n" +
				"Choices of user defined type must follow Java enum defining rules.";
	}

	public static final String NEGATIVE_INDEX_PROBLEM = "The index of the element must be non-negative";	
	public static final String TOO_HIGH_INDEX_PROBLEM = "The index of the element is too high";

	public static final String CLASS_NAME_DUPLICATE_PROBLEM = "The model already contains a class with this name";

	public static final String JAVA_METHOD_NAME_REGEX_PROBLEM = "The method name should fulfill all rules for naming method in Java";

	public static final String UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT = "Element could not be removed from the model";

	public static final String CATEGORY_NAME_DUPLICATE_PROBLEM = "A parameter with this name already exists in the element.";

	public static final String JAVA_METHOD_PARAMETER_NAME_REGEX_PROBLEM = "Parameter name must be a valid Java identifier (only alphanumeric characters or underscore, no spaces, should not begin with a digit).";

	public static final String CATEGORY_TYPE_REGEX_PROBLEM = "Parameter type must be a valid type identifier in Java, i.e. it must be either a primitive type name or String or a valid qualified type name of user type";

	public static final String CATEGORY_DEFAULT_VALUE_REGEX_PROBLEM = "The entered value is not compatible with parameter type";

	public static final String JAVA_CONSTRAINT_NAME_REGEX_PROBLEM = "Constraint name not allowed";
	public static final String JAVA_TEST_CASE_NAME_REGEX_PROBLEM = "Test case name not allowed";
	public static final String INCOMPATIBLE_CONSTRAINT_PROBLEM = "The added constraint does not match the method model";
	public static final String DIALOG_UNALLOWED_RELATION_MESSAGE = "This relation is not allowed for given statement";
	public static final String NULL_POINTER_TARGET = "The target of operation is invalid";
	public static final String TARGET_STATEMENT_NOT_FOUND_PROBLEM = "The target statement for this operation could not be found";

	public static final String OPERATION_NOT_SUPPORTED_PROBLEM = "Operation not supported";
	public static final String TEST_CASE_INCOMPATIBLE_WITH_METHOD = "Target method must have the same number of parameters and corresponding choice names as added test case.";
	public static final String TEST_CASE_DATA_INCOMPATIBLE_WITH_METHOD = "One of expected value couln'd be converted to new type";
	public static final String TEST_DATA_CATEGORY_MISMATCH_PROBLEM = "New test value has wrong parent parameter.";
	public static final String EXPECTED_USER_TYPE_CATEGORY_LAST_PARTITION_PROBLEM = "User type expected parameters must have at least one choice. It's value will define the default expected value of the parameter";
	
}
