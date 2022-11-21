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

public class BasicParameterNodeHelper {
	
	public static void compareParameters(
			BasicParameterNode basicParameterNode1, 
			BasicParameterNode basicParameterNode2) {
		
		if (basicParameterNode1.isExpected() != basicParameterNode2.isExpected()) {
			ExceptionHelper.reportRuntimeException("Expected property does not match.");
		}
		
		ModelCompareHelper.compareTypes(basicParameterNode1.getType(), basicParameterNode1.getType());
		ModelCompareHelper.compareSizes(basicParameterNode1.getChoices(), basicParameterNode2.getChoices());
		for(int i = 0; i < basicParameterNode1.getChoices().size(); ++i){
			ModelCompareHelper.compareChoices(basicParameterNode1.getChoices().get(i), basicParameterNode2.getChoices().get(i));
		}
		
	}
	
}
