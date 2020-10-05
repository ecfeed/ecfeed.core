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

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;

public abstract class AbstractNodeHelper  {

	public static String getName(AbstractNode abstractNode, IExtLanguageManager extLanguage) {

		String nodeName = abstractNode.getName();
		String text = extLanguage.convertTextFromIntrToExtLanguage(nodeName);

		return text;
	}

	public static void setName(AbstractNode abstractNode, String name, IExtLanguageManager extLanguage) {

		String text = extLanguage.convertTextFromExtToIntrLanguage(name);
		abstractNode.setName(text);
	}

}
