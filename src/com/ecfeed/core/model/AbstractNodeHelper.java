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
			IAbstractNode abstractNode, 
			IExtLanguageManager extLanguageManager) {

		String textInExtLanguage;

		if (isTheSameExtAndIntrLanguage(abstractNode)) {
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

		if (isTheSameExtAndIntrLanguage(abstractNode)) {
			textInIntrLanguage = textInExtLanguage;
		} else {
			textInIntrLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(textInExtLanguage);
		}

		return textInIntrLanguage;
	}

	public static String getName(IAbstractNode abstractNode, IExtLanguageManager extLanguageManager) {

		String nameInIntrLanguage = abstractNode.getName();

		String nameInExtLanguage = convertTextFromIntrToExtLanguage(nameInIntrLanguage, abstractNode, extLanguageManager);

		return nameInExtLanguage;
	}


	public static void setName(AbstractNode abstractNode, String nameInExtLanguage, IExtLanguageManager extLanguageManager) {

		String nameInIntrLanguage;

		nameInIntrLanguage = convertTextFromExtToIntrLanguage(abstractNode, nameInExtLanguage, extLanguageManager);

		abstractNode.setName(nameInIntrLanguage);
	}

	public static boolean isTheSameExtAndIntrLanguage(IAbstractNode abstractNode) {

		boolean isTheSameExtAndIntrLanguage;

		try {
			isTheSameExtAndIntrLanguage = (boolean) abstractNode.accept(new IsTheSameExtAndIntrLanguageProvider());
		} catch (Exception e) {
			return false;
		}

		return isTheSameExtAndIntrLanguage;
	}

	private static class IsTheSameExtAndIntrLanguageProvider  implements IModelVisitor {

		@Override
		public Object visit(RootNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return false;
		}

	}


}
