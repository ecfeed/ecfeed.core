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

import java.util.Collection;

import com.ecfeed.core.utils.ExceptionHelper;

public class StatementHelper{
	
	public static void compareSizes(
			Collection<? extends IStatement> collection1, 
			Collection<? extends IStatement> collection2, 
			String errorMessage) {

		int size1 = collection1.size();

		int size2 = collection2.size();

		if (size1 != size2) {
			ExceptionHelper.reportRuntimeException(errorMessage + " " + collection1.size() + " vs " + collection2.size() + ".");
		}
	}

	
}
