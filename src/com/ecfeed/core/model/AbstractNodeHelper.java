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

	public static String convertTextFromIntrToExtLanguage(
			String textInIntrLanguage,
			AbstractNode abstractNode, 
			IExtLanguageManager extLanguageManager) {

		String textInExtLanguage;

		if (abstractNode.isTheSameExtLanguageAndIntrLanguage()) {
			textInExtLanguage = textInIntrLanguage;
		} else {
			textInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(textInIntrLanguage);
		}

		return textInExtLanguage;
	}

	public static String convertTextFromExtToIntrLanguage(
			AbstractNode abstractNode, 
			String textInExtLanguage,
			IExtLanguageManager extLanguageManager) {

		String textInIntrLanguage;

		if (abstractNode.isTheSameExtLanguageAndIntrLanguage()) {
			textInIntrLanguage = textInExtLanguage;
		} else {
			textInIntrLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(textInExtLanguage);
		}

		return textInIntrLanguage;
	}

	public static String getName(AbstractNode abstractNode, IExtLanguageManager extLanguageManager) {

		String nameInIntrLanguage = abstractNode.getName();

		String nameInExtLanguage = convertTextFromIntrToExtLanguage(nameInIntrLanguage, abstractNode, extLanguageManager);

		return nameInExtLanguage;
	}


	public static void setName(AbstractNode abstractNode, String nameInExtLanguage, IExtLanguageManager extLanguageManager) {

		String nameInIntrLanguage;

		nameInIntrLanguage = convertTextFromExtToIntrLanguage(abstractNode, nameInExtLanguage, extLanguageManager);

		abstractNode.setName(nameInIntrLanguage);
	}

}
