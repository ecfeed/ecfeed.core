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

public abstract class AbstractNodeHelper  {

	public static String getName(AbstractNode abstractNode, IExtLanguageManager extLanguageManager) {

		String nameInIntrLanguage = abstractNode.getName();

		String nameInExtLanguage; 

		if (abstractNode.isTheSameExtLanguageAndIntrLanguage()) {
			nameInExtLanguage = nameInIntrLanguage;
		} else {
			nameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(nameInIntrLanguage);
		}

		return nameInExtLanguage;
	}

	public static void setName(AbstractNode abstractNode, String nameInExtLanguage, IExtLanguageManager extLanguageManager) {

		String nameInIntrLanguage;

		if (abstractNode.isTheSameExtLanguageAndIntrLanguage()) {
			nameInIntrLanguage = nameInExtLanguage;
		} else {
			nameInIntrLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(nameInExtLanguage);
		}

		abstractNode.setName(nameInIntrLanguage);
	}

}
