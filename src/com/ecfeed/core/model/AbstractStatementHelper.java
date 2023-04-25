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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public abstract class AbstractStatementHelper {

	public static String createSignature(AbstractStatement abstractStatement, IExtLanguageManager extLanguageManager) {

		if (abstractStatement == null) {
			return "EMPTY";
		}

		return abstractStatement.createSignature(extLanguageManager);
	}

	public static void compareStatements(AbstractStatement statement1, AbstractStatement statement2) {

		if (statement1 instanceof StaticStatement && statement2 instanceof StaticStatement) {
			StaticStatementHelper.compareStaticStatements((StaticStatement)statement1, (StaticStatement)statement2);
			return;
		}

		if (statement1 instanceof RelationStatement && statement2 instanceof RelationStatement) {
			RelationStatementHelper.compareRelationStatements((RelationStatement)statement1, (RelationStatement)statement2);
			return;
		}

		if (statement1 instanceof StatementArray && statement2 instanceof StatementArray) {
			StatementArrayHelper.compareStatementArrays((StatementArray)statement1, (StatementArray)statement2);
			return;
		}

		if (statement1 instanceof ExpectedValueStatement && statement2 instanceof ExpectedValueStatement) {
			ExpectedValueStatementHelper.compareExpectedValueStatements((ExpectedValueStatement)statement1, (ExpectedValueStatement)statement2);
			return;
		}

		if (statement1 instanceof AssignmentStatement && statement2 instanceof AssignmentStatement) {
			AssignmentStatementHelper.compareAssignmentStatements((AssignmentStatement)statement1, (AssignmentStatement)statement2);
			return;
		}

		ExceptionHelper.reportRuntimeException("Unknown type of statement or compared statements are of didderent types");
	}

	
}