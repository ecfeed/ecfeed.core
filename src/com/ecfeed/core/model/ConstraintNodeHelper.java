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

import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageHelper;

public class ConstraintNodeHelper {

	// TODO SIMPLE-VIEW unit tests
	public static String createSignature(ConstraintNode constraintNode, ExtLanguage extLanguage) {

		String result = constraintNode.toString();

		return ExtLanguageHelper.convertTextFromIntrToExtLanguage(result, extLanguage);
	}

}
