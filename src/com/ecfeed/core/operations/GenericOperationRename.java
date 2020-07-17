/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.NodeNamingConvention;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.SystemLogger;

public class GenericOperationRename extends AbstractModelOperation {

	private AbstractNode fTarget;
	private String fNewName;
	private String fOriginalName;
	private String fNameRegex;
	private NodeNamingConvention fNodeNamingConvention;
	
	public GenericOperationRename(AbstractNode target, String newName, NodeNamingConvention nodeNamingConvention){
		
		super(OperationNames.RENAME);
		fTarget = target;
		fNewName = newName;
		fOriginalName = target.getFullName();
		fNameRegex = getJavaNameRegex(target);
		fNodeNamingConvention = nodeNamingConvention;
	}

	private class RegexProblemMessageProvider implements IModelVisitor {

		@Override
		public Object visit(RootNode node) throws Exception {
			return RegexHelper.MODEL_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return RegexHelper.CLASS_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return OperationMessages.METHOD_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return OperationMessages.CATEGORY_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return OperationMessages.CATEGORY_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return OperationMessages.TEST_CASE_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return OperationMessages.CONSTRAINT_NAME_REGEX_PROBLEM;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return RegexHelper.PARTITION_NAME_REGEX_PROBLEM;
		}
	}

	private class JavaNameRegexProvider implements IModelVisitor {

		@Override
		public Object visit(RootNode node) throws Exception {
			return RegexHelper.REGEX_ROOT_NODE_NAME;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return RegexHelper.REGEX_CLASS_NODE_NAME;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return RegexHelper.REGEX_METHOD_NODE_NAME;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return RegexHelper.REGEX_CATEGORY_NODE_NAME;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return RegexHelper.REGEX_CATEGORY_NODE_NAME;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return RegexHelper.REGEX_TEST_CASE_NODE_NAME;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return RegexHelper.REGEX_CONSTRAINT_NODE_NAME;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return RegexHelper.REGEX_PARTITION_NODE_NAME;
		}
	}

	@Override
	public void execute() throws ModelOperationException {
		
		setOneNodeToSelect(fTarget);
		
		if (fNodeNamingConvention == NodeNamingConvention.JAVA) {
			verifyNameWithJavaRegex();
		}
		
		verifyNewName(fNewName);
		
		fTarget.setFullName(fNewName);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new GenericOperationRename(getOwnNode(), getOriginalName(), fNodeNamingConvention);
	}

	protected AbstractNode getOwnNode(){
		return fTarget;
	}

	protected String getOriginalName(){
		return fOriginalName;
	}

	protected String getNewName(){
		return fNewName;
	}

	protected void verifyNewName(String newName) throws ModelOperationException{
	}

	protected void verifyNameWithJavaRegex() throws ModelOperationException {

		if (fNewName.matches(fNameRegex) == false) {
			ModelOperationException.report(getRegexProblemMessage());
		}
	}

	private String getJavaNameRegex(AbstractNode target) {
		try{
			return (String)fTarget.accept(new JavaNameRegexProvider());
		}catch(Exception e){SystemLogger.logCatch(e);}
		return "*";
	}

	private String getRegexProblemMessage(){
		try{
			return (String)fTarget.accept(new RegexProblemMessageProvider());
		}catch(Exception e){SystemLogger.logCatch(e);}
		return "";
	}

}
