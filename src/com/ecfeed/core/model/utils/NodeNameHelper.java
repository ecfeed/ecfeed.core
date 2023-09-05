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
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.RegexHelper;

public class NodeNameHelper  {

	private static final String REGEX_ROOT_NODE_NAME = RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_64;

	private static final String REGEX_PACKAGE_NAME = "(\\.|((" + RegexHelper.REGEX_JAVA_IDENTIFIER + ")\\.)*)";
	
	// XYX make private
	public static final String REGEX_CLASS_NODE_NAME = REGEX_PACKAGE_NAME + "*"+ RegexHelper.REGEX_JAVA_IDENTIFIER;

	private static final String REGEX_METHOD_NODE_NAME = RegexHelper.REGEX_JAVA_IDENTIFIER;
	//private static final String REGEX_CATEGORY_TYPE_NAME = REGEX_CLASS_NODE_NAME; // XYX ??
	private static final String REGEX_CATEGORY_NODE_NAME = RegexHelper.REGEX_JAVA_IDENTIFIER;

	private static final String REGEX_CHOICE_NODE_NAME = RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_64;

	// XYX make private
	public static final String REGEX_CONSTRAINT_NODE_NAME = RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_64;

	// XYX make private
	public static final String REGEX_TEST_CASE_NODE_NAME = RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_64;


	public static String correctExtNodeName(String nodeName, IAbstractNode node) {
		return null; // XYX TODO
	}

	public static String getRegex(IAbstractNode target) { // XYX

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
			return REGEX_CATEGORY_NODE_NAME;
		}

		@Override
		public Object visit(CompositeParameterNode node) throws Exception {
			return REGEX_CATEGORY_NODE_NAME;
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
