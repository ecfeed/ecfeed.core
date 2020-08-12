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
import com.ecfeed.core.utils.ViewMode;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.SimpleTypeHelper;
import com.ecfeed.core.utils.SystemLogger;

public class GenericOperationRename extends AbstractModelOperation {

	private AbstractNode fTarget;
	private String fNewName;
	private String fOriginalName;
	private String fJavaNameRegex;
	private ViewMode fSourceViewMode;
	
	public GenericOperationRename(
			AbstractNode target, 
			String newName, 
			ViewMode modelCompatibility // TODO SIMPLE-VIEW remove
			) {
		
		super(OperationNames.RENAME);
		fTarget = target;
		fNewName = newName;
		fOriginalName = target.getFullName();
		fJavaNameRegex = getJavaNameRegex(target);
		fSourceViewMode = modelCompatibility;
	}

	@Override
	public void execute() throws ModelOperationException {
		
		setOneNodeToSelect(fTarget);
		
		String newName = fNewName;
		
		if (fSourceViewMode == ViewMode.SIMPLE) {
			
			if (newName.contains("_")) {
				ModelOperationException.report("Underline chars are not allowed in simple view.");
			}
			
			newName = newName.replace(" ", "_");
		}
		
		verifyNameWithJavaRegex(newName, fJavaNameRegex, fTarget); // TODO SIMPLE-VIEW for source view simple error messages should be different
		
		verifyNewName(newName);
		
		fTarget.setFullName(newName);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new GenericOperationRename(getOwnNode(), getOriginalName(), fSourceViewMode);
	}

	protected AbstractNode getOwnNode(){
		return fTarget;
	}

	protected String getOriginalName(){
		return fOriginalName;
	}

	protected String getNewNameInJavaConvention(){
		
		if (fSourceViewMode == ViewMode.SIMPLE) {
			
			String result = SimpleTypeHelper.convertTextFromSimpleToJavaConvention(fNewName);
			
			return result; 
		}
		
		return fNewName;
	}

	protected void verifyNewName(String newName) throws ModelOperationException{
	}

	private static void verifyNameWithJavaRegex(String name, String regex, AbstractNode targetNode) throws ModelOperationException {

		if (name.matches(regex) == false) {
			ModelOperationException.report(getRegexProblemMessage(targetNode));
		}
	}

	private String getJavaNameRegex(AbstractNode target) {
		try{
			return (String)fTarget.accept(new JavaNameRegexProvider());
		}catch(Exception e){SystemLogger.logCatch(e);}
		return "*";
	}

	private static String getRegexProblemMessage(AbstractNode abstractNode){
		try{
			return (String)abstractNode.accept(new RegexProblemMessageProvider());
		}catch(Exception e){SystemLogger.logCatch(e);}
		return "";
	}
	
	private static class RegexProblemMessageProvider implements IModelVisitor {

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
	

}
