/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.utils.TypeHelper.TypeCathegory;

public class JavaTypeHelper {

	public static String calculateJavaType(
			String newTypeInExtLanguage,
			TypeCathegory typeCathegory,
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager) {

		if (extLanguageManager.getLanguage() == ExtLanguage.JAVA) {
			return extLanguageManager.convertTypeFromExtToIntrLanguage(newTypeInExtLanguage);
		}

		String newTypeInIntrLanguage = calculateTypeForSimpleType(newTypeInExtLanguage, typeCathegory,
				parameterConversionDefinition, extLanguageManager);

		return newTypeInIntrLanguage;
	}

	private static String calculateTypeForSimpleType(
			String newTypeInExtLanguage, 
			TypeCathegory typeCathegory,
			ParameterConversionDefinition parameterConversionDefinition, 
			IExtLanguageManager extLanguageManager) {

		String newTypeInIntrLanguage = 
				AbstractParameterNodeHelper.getMaxJavaTypeFromConversionDefinition(
						typeCathegory,
						parameterConversionDefinition);

		if (newTypeInIntrLanguage == null) {
			newTypeInIntrLanguage = extLanguageManager.convertTypeFromExtToIntrLanguage(newTypeInExtLanguage);
		}

		return newTypeInIntrLanguage;
	}

}
