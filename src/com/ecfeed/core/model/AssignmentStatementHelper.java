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

public class AssignmentStatementHelper {

	public static void compareAssignmentStatements(
			AssignmentStatement statement1, AssignmentStatement statement2) {

		if (statement1.isEqualTo(statement1)) {
			ExceptionHelper.reportRuntimeException("Assignment statements do not match");
		}
	}

}

