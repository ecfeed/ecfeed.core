/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

public enum StatementArrayOperator {

	AND("AND"), 
	OR("OR"),
	ASSIGN("ASSIGN");

	public static final String OPERATOR_AND = "AND";
	public static final String OPERATOR_OR = "OR";
	public static final String OPERATOR_ASSIGN = "ASSIGN";

	String fValue;

	StatementArrayOperator(String value){
		fValue = value;
	}

	public String toString(){
		return fValue; 
	}

	public static StatementArrayOperator parse(String text) {

		switch(text){

		case OPERATOR_AND:
			return AND;
		case OPERATOR_OR:
			return OR;
		case OPERATOR_ASSIGN:
			return ASSIGN;
		}

		return null;
	}

}
