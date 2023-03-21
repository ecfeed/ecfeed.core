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

import com.ecfeed.core.utils.JavaLanguageHelper;

public class RelationStatementHelper {

	public static boolean isRightParameterTypeAllowed(String rightParameterType, String leftParameterType) {

		if (JavaLanguageHelper.isBooleanTypeName(leftParameterType) 
				&& !JavaLanguageHelper.isBooleanTypeName(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isBooleanTypeName(leftParameterType) 
				&& JavaLanguageHelper.isBooleanTypeName(rightParameterType)) {

			return false;
		}

		if (JavaLanguageHelper.isTypeWithChars(leftParameterType)
				&& !JavaLanguageHelper.isTypeWithChars(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isTypeWithChars(leftParameterType)
				&& JavaLanguageHelper.isTypeWithChars(rightParameterType)) {

			return false;
		}

		if (JavaLanguageHelper.isNumericTypeName(leftParameterType)
				&& !JavaLanguageHelper.isNumericTypeName(rightParameterType)) {

			return false;
		}

		if (!JavaLanguageHelper.isNumericTypeName(leftParameterType)
				&& JavaLanguageHelper.isNumericTypeName(rightParameterType)) {

			return false;
		}

		return true;
	}

}

