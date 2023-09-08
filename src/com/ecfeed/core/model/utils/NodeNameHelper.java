/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.RegexHelper;

public class NodeNameHelper {

	private static final String REGEX_ROOT_NODE_NAME = RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_64;

	private static final String REGEX_PACKAGE_NAME = "(\\.|((" + RegexHelper.REGEX_JAVA_IDENTIFIER + ")\\.)*)";

	public static final String REGEX_CLASS_NODE_NAME = REGEX_PACKAGE_NAME + "*"+ RegexHelper.REGEX_JAVA_IDENTIFIER;

	private static final String REGEX_METHOD_NODE_NAME = RegexHelper.REGEX_JAVA_IDENTIFIER;

	private static final String REGEX_PARAMETER_NODE_NAME = RegexHelper.REGEX_JAVA_IDENTIFIER;

	private static final String REGEX_CHOICE_NODE_NAME = RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_64;

	private static final String REGEX_CONSTRAINT_NODE_NAME = RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_64;

	private static final String REGEX_TEST_CASE_NODE_NAME = RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_64;

	public static boolean classNameCompliesWithJavaNamingRules(String className) {

		if (className.matches(NodeNameHelper.REGEX_CLASS_NODE_NAME)) {
			return true;
		}

		return false;
	}

	public static String correctSyntaxClassNameWithoutPackage(String classNameInIntrLanguage) {

		String corrected = JavaLanguageHelper.correctJavaIdentifier(classNameInIntrLanguage);

		return corrected;
	}

	public static boolean methodNameCompliesWithNamingRules(String methodName) {

		if (methodName.matches(NodeNameHelper.REGEX_CLASS_NODE_NAME)) {
			return true;
		}

		return false;
	}

	public static boolean choiceNameCompliesWithNamingRules(String methodName) {

		if (methodName.matches(NodeNameHelper.REGEX_CLASS_NODE_NAME)) {
			return true;
		}

		return false;
	}
	
	public static String correctMethodNameSyntax(String classNameInIntrLanguage) {

		String corrected = JavaLanguageHelper.correctJavaIdentifier(classNameInIntrLanguage);

		return corrected;
	}	

	public static String correctParameterNameSyntax(String classNameInIntrLanguage) {

		String corrected = JavaLanguageHelper.correctJavaIdentifier(classNameInIntrLanguage);

		return corrected;
	}	
	
	public static boolean constraintNodeNameCompliesWithRules(String constraintName) {

		return constraintName.matches(NodeNameHelper.REGEX_CONSTRAINT_NODE_NAME);
	}

	public static boolean testCaseNodeNameCompliesWithRules(String name) {

		return name.matches(NodeNameHelper.REGEX_TEST_CASE_NODE_NAME);
	}

	public static String getRegex(IAbstractNode target) {

		try{
			return (String)target.accept(new RegexInternalProvider());

		} catch(Exception e) {

			LogHelperCore.logCatch(e);}
		return "*";
	}

	private static class RegexInternalProvider implements IModelVisitor {

		@Override
		public Object visit(RootNode node) throws Exception {
			return REGEX_ROOT_NODE_NAME;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return REGEX_CLASS_NODE_NAME;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return REGEX_METHOD_NODE_NAME;
		}

		@Override
		public Object visit(BasicParameterNode node) throws Exception {
			return REGEX_PARAMETER_NODE_NAME;
		}

		@Override
		public Object visit(CompositeParameterNode node) throws Exception {
			return REGEX_PARAMETER_NODE_NAME;
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			return REGEX_TEST_CASE_NODE_NAME;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return REGEX_TEST_CASE_NODE_NAME;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return REGEX_CONSTRAINT_NODE_NAME;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return REGEX_CHOICE_NODE_NAME;
		}
	}

}
