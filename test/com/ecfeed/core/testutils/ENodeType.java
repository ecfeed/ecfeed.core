/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.testutils;

import com.ecfeed.core.model.*;

public enum ENodeType {
	PROJECT, CLASS, METHOD, PARAMETER, METHOD_PARAMETER, GLOBAL_PARAMETER, CONSTRAINT, TEST_CASE, CHOICE;

	@SuppressWarnings("unused")
	private class DummyClassJustToRememberToUpdateTheEnumWhenNewNodeTypeIsAddedToModel implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return null;
		}
	}
}
