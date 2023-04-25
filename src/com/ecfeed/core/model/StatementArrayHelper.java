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

public class StatementArrayHelper {


	public static void compareStatementArrays(StatementArray array1, StatementArray array2) {

		if(array1.getOperator() != array2.getOperator()){
			ExceptionHelper.reportRuntimeException("Operator of compared statement arrays differ");
		}

		StatementHelper.compareSizes(array1.getChildren(), array2.getChildren(), "Number of statements differs.");
		for(int i = 0; i < array1.getChildren().size(); ++i){
			AbstractStatementHelper.compareStatements(array1.getChildren().get(i), array2.getChildren().get(i));
		}
	}

	
}
