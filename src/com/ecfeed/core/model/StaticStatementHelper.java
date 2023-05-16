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

public class StaticStatementHelper {

	public static void compareStaticStatements(StaticStatement statement1, StaticStatement statement2) {
		if(statement1.getValue() != statement2.getValue()){
			ExceptionHelper.reportRuntimeException("Static statements different");
		}
	}
	
}
