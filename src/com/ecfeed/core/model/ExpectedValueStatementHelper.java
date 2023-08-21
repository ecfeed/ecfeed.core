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

import com.ecfeed.core.utils.StringHelper;

public class ExpectedValueStatementHelper {


	public static void compareExpectedValueStatements(
			ExpectedValueStatement statement1,
			ExpectedValueStatement statement2) {

		AbstractParameterNodeHelper.compareParameters(statement1.getLeftMethodParameterNode(), statement2.getLeftMethodParameterNode());
		StringHelper.compareStrings(statement1.getChoice().getValueString(), statement2.getChoice().getValueString(), "Conditions differ.");
	}

	
}
