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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.IExtLanguageManager;

public class ParametersParentNodeHelper {

	public static List<String> getParameterTypes(IParametersParentNode parameterParentNode, IExtLanguageManager extLanguageManager) {

		List<String> result = new ArrayList<String>();

		for (AbstractParameterNode abstractParameterNode : parameterParentNode.getParameters()) {

			if (abstractParameterNode instanceof BasicParameterNode) {
				
				BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;
				
				String type = basicParameterNode.getType();
	
				type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);
	
				result.add(type);
				continue;
			}
			
			if (abstractParameterNode instanceof CompositeParameterNode) {
				
				result.add(CompositeParameterNode.COMPOSITE_PARAMETER_TYPE);
				continue;
			}
		}

		return result;
	}

}
