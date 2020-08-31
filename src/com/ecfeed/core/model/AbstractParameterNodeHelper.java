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

import com.ecfeed.core.utils.CoreViewModeHelper;
import com.ecfeed.core.utils.ExtLanguage;

public abstract class AbstractParameterNodeHelper {

	public static String createLabel(AbstractParameterNode abstractParameterNode, ExtLanguage viewMode) {

		String name = abstractParameterNode.getName();
		name = CoreViewModeHelper.convertTextToConvention(name, viewMode);

		String type = abstractParameterNode.getType();
		type = createTypeLabel(type, viewMode);

		String label = name + ": " + type;
		return label;
	}


	public static String createTypeLabel(String javaType, ExtLanguage viewMode) {

		String type = CoreViewModeHelper.convertTypeToConvention(javaType, viewMode);
		return type;
	}
}
