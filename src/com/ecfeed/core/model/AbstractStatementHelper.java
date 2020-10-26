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

import java.util.List;

public abstract class AbstractStatementHelper {

	public static String createSignature(AbstractStatement abstractStatement) { // TODO SIMPLE-VIEW test

		if (abstractStatement == null) {
			return "EMPTY";
		}

		return abstractStatement.toString();
	}


	public static String createSignature9(StatementArray statementArray) { // TODO SIMPLE-VIEW 

		String result = new String("(");
		
		List<AbstractStatement> statements = statementArray.getChildren();
		
		int statementsSize = statements.size();
		
		for (int i = 0; i < statementsSize; i++) {
			
			AbstractStatement abstractStatement = statements.get(i);
			
			result += abstractStatement.toString();
			
			if (i < statementsSize - 1) {
				
				EStatementOperator operator2 = statementArray.getOperator();
				
				switch(operator2) {
				case AND:
					result += " \u2227 ";
					break;
				case OR:
					result += " \u2228 ";
					break;
				}
			}
		}
		
		return result + ")";
	}
	
}