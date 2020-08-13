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

import com.ecfeed.core.utils.SimpleTypeHelper;
import com.ecfeed.core.utils.ViewMode;

public abstract class AbstractParameterNodeHelper {

	public static String createLabel(AbstractParameterNode abstractParameterNode, ViewMode viewMode) {
		
		String name = abstractParameterNode.getName();
		String type = abstractParameterNode.getType();
		
		if (viewMode == ViewMode.SIMPLE) {
			name = SimpleTypeHelper.convertTextFromJavaToSimpleConvention(name);
			type = SimpleTypeHelper.convertJavaTypeToSimpleType(type);
		}
		
		String label = name + ": " + type;
		return label;
	}
}
