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

import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public class ParametersAndConstraintsParentNodeHelper { // TODO MO-RE divide into 2 helpers ?
	
	public static Set<String> getConstraintNames(IConstraintsParentNode methodNode, IExtLanguageManager extLanguageManager) {

		Set<String> constraintNames = methodNode.getConstraintsNames();

		// constraintNames = convertConstraintNamesToExtLanguage(constraintNames, extLanguageManager);

		return constraintNames;
	}
	
	public static BasicParameterNode findMethodParameterByName(
			String parameterNameToFindInExtLanguage, 
			IParametersParentNode methodNode,
			IExtLanguageManager extLanguageManager) {

		List<AbstractParameterNode> methodParameters = methodNode.getParameters();

		for (AbstractParameterNode parameter : methodParameters) {

			BasicParameterNode methodParameterNode = (BasicParameterNode)parameter;

			String parameterNameInExtLanguage = MethodParameterNodeHelper.getName(methodParameterNode, extLanguageManager);

			if (StringHelper.isEqual(parameterNameToFindInExtLanguage, parameterNameInExtLanguage)) {
				return methodParameterNode;
			}
		}
		
		return null;

	}
	
	
}
