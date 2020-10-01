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
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.SystemLogger;

public class GenericOperationRename extends AbstractModelOperation {

	private AbstractNode fTarget;
	private String fNewNameInExtLanguage;
	private String fOriginalName;
	private String fJavaNameRegex;
	private ExtLanguage fExtLanguage;

	public GenericOperationRename(
			AbstractNode target, 
			String newNameInExtLanguage, 
			ExtLanguage extLanguage) {

		super(OperationNames.RENAME, extLanguage);

		fTarget = target;
		fNewNameInExtLanguage = newNameInExtLanguage;
		fOriginalName = target.getName();
		fJavaNameRegex = getJavaNameRegex(target);
		fExtLanguage = extLanguage;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);
		String oldName = fTarget.getName();

		verifyNewName(fNewNameInExtLanguage);

		String newNameInIntrLanguage = convertTextFromExtToIntrLanguage(fNewNameInExtLanguage, fExtLanguage);
		
		if (!(fTarget instanceof RootNode)) {
			verifyNameWithJavaRegex(newNameInIntrLanguage, fJavaNameRegex, fTarget, ExtLanguage.JAVA);
		}
		
		fTarget.setName(newNameInIntrLanguage);

		RootNode rootNode = ModelHelper.findRoot(fTarget);
		String errorMessage = ExtLanguageHelper.checkIsModelCompatibleWithExtLanguage(rootNode, fExtLanguage);

		if (errorMessage != null) {
			fTarget.setName(oldName);
			ModelOperationException.report(errorMessage);
		}

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new GenericOperationRename(
				getOwnNode(), 
				getOriginalName(), 
				fExtLanguage);
	}

	protected AbstractNode getOwnNode(){
		return fTarget;
	}

	protected String getOriginalName(){
		return fOriginalName;
	}

	protected void verifyNewName(String newNameInIntrLanguage) throws ModelOperationException{
	}

	private static void verifyNameWithJavaRegex(
			String name, 
			String regex, 
			AbstractNode targetNode,
			ExtLanguage extLanguage) throws ModelOperationException {

		if (name.matches(regex) == false) {
			ModelOperationException.report(getRegexProblemMessage(targetNode, extLanguage));
		}
	}

	private String getJavaNameRegex(AbstractNode target) {
		try{
			return (String)fTarget.accept(new JavaNameRegexProvider());
		}catch(Exception e){SystemLogger.logCatch(e);}
		return "*";
	}

	private static String getRegexProblemMessage(AbstractNode abstractNode, ExtLanguage extLanguage){
		try{
			return (String)abstractNode.accept(new RegexProblemMessageProvider(extLanguage));
		}catch(Exception e){SystemLogger.logCatch(e);}
		return "";
	}

	private static class RegexProblemMessageProvider implements IModelVisitor {

		private ExtLanguage fExtLanguage;

		public RegexProblemMessageProvider(ExtLanguage extLanguage) {
			fExtLanguage = extLanguage;
		}

		@Override
		public Object visit(RootNode node) throws Exception {

			return RegexHelper.createMessageAllowedCharsForModel();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForClass(fExtLanguage);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForMethod(fExtLanguage);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForParameter(fExtLanguage);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForParameter(fExtLanguage);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForChoice();
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return OperationMessages.TEST_CASE_NOT_ALLOWED;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return OperationMessages.CONSTRAINT_NOT_ALLOWED;
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
