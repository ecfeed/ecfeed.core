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

import java.util.List;
import java.util.stream.Stream;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractNodeHelper;
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
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.QualifiedNameHelper;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.SystemLogger;

public class GenericOperationRename extends AbstractModelOperation {

	private AbstractNode fTargetAbstractNode;

	private String fNewPackageName;
	private String fNewNonQualifiedNameInExtLanguage;

	private String fOriginalPackageName;
	private String fOriginalNonQualifiedNameInExtLanguage;

	private String fJavaNameRegex;
	private IExtLanguageManager fExtLanguageManager;

	public GenericOperationRename(
			AbstractNode target,
			String newPackageName,
			String newNonQualifiedNameInExtLanguage, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.RENAME, extLanguageManager);

		fTargetAbstractNode = target;

		fNewPackageName = newPackageName;
		fNewNonQualifiedNameInExtLanguage = newNonQualifiedNameInExtLanguage;

		fOriginalPackageName = QualifiedNameHelper.getPackage(target.getName());
		fOriginalNonQualifiedNameInExtLanguage = QualifiedNameHelper.getNonQualifiedName(target.getName());

		fJavaNameRegex = getJavaNameRegex(target);
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTargetAbstractNode);

		String oldQualifiedNameInIntrLanguage = fTargetAbstractNode.getName();

		String newQualifiedNameInIntrLanguage = prepareNewQualifiedName();

		setNewNameWithCheck(newQualifiedNameInIntrLanguage, oldQualifiedNameInIntrLanguage);

		markModelUpdated();
	}

	private String prepareNewQualifiedName() throws ModelOperationException {

		String newQualifiedNameInExtLanguage = 
				fExtLanguageManager.createQualifiedName(fNewPackageName, fNewNonQualifiedNameInExtLanguage);

		verifyNewName(newQualifiedNameInExtLanguage);

		String newNonQualifiedNameInIntrLanguage = 		
				AbstractNodeHelper.convertTextFromExtToIntrLanguage(
						fTargetAbstractNode, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);

		String newQualifiedNameInIntrLanguage = 
				JavaLanguageHelper.createQualifiedName(fNewPackageName, newNonQualifiedNameInIntrLanguage);

		if (!(fTargetAbstractNode instanceof RootNode)) {
			verifyNameWithJavaRegex(
					newQualifiedNameInIntrLanguage, 
					fJavaNameRegex, 
					fTargetAbstractNode, 
					new ExtLanguageManagerForJava());
		}

		return newQualifiedNameInIntrLanguage;
	}

	private void setNewNameWithCheck(
			String newQualifiedNameInIntrLanguage, 
			String oldQualifiedNameInIntrLanguage) throws ModelOperationException {

		fTargetAbstractNode.setName(newQualifiedNameInIntrLanguage);

		if (fTargetAbstractNode instanceof TestSuiteNode) {
			setNewNameInChildTestCases(newQualifiedNameInIntrLanguage);
		} 

		RootNode rootNode = ModelHelper.findRoot(fTargetAbstractNode);

		String errorMessage = fExtLanguageManager.checkIsModelCompatibleWithExtLanguage(rootNode);

		if (errorMessage != null) {
			fTargetAbstractNode.setName(oldQualifiedNameInIntrLanguage);
			ModelOperationException.report(errorMessage);
		}
	}

	private void setNewNameInChildTestCases(String qualifiedNameInIntrLanguage) {

		TestSuiteNode testSuiteNode = (TestSuiteNode) fTargetAbstractNode;

		List<TestCaseNode> testCaseNodes = testSuiteNode.getTestCaseNodes();

		Stream<TestCaseNode> stream = testCaseNodes.stream();

		stream.forEach(testCaseNode -> testCaseNode.setName(qualifiedNameInIntrLanguage));
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new GenericOperationRename(
				getOwnNode(), 
				getOriginalPackageName(),
				getOriginalNonQualifiedName(),
				fExtLanguageManager);
	}

	protected AbstractNode getOwnNode(){
		return fTargetAbstractNode;
	}

	protected String getOriginalNonQualifiedName(){
		return fOriginalNonQualifiedNameInExtLanguage;
	}

	protected String getOriginalPackageName(){
		return fOriginalPackageName;
	}

	public String getNewPackageName() {
		return fNewPackageName;
	}

	protected void verifyNewName(String newNameInExtLanguage) throws ModelOperationException{
	}

	private static void verifyNameWithJavaRegex(
			String name, 
			String regex, 
			AbstractNode targetNode,
			IExtLanguageManager extLanguageManager) throws ModelOperationException {

		if (name.matches(regex) == false) {

			String regexProblemMessage = getRegexProblemMessage(targetNode, extLanguageManager);

			ModelOperationException.report(regexProblemMessage);
		}
	}

	private String getJavaNameRegex(AbstractNode target) {
		try{
			return (String)fTargetAbstractNode.accept(new JavaNameRegexProvider());
		}catch(Exception e){SystemLogger.logCatch(e);}
		return "*";
	}

	private static String getRegexProblemMessage(AbstractNode abstractNode, IExtLanguageManager extLanguageManager){
		try{
			return (String)abstractNode.accept(new RegexProblemMessageProvider(extLanguageManager));
		}catch(Exception e){SystemLogger.logCatch(e);}
		return "";
	}

	private static class RegexProblemMessageProvider implements IModelVisitor {

		private IExtLanguageManager fExtLanguageManager;

		public RegexProblemMessageProvider(IExtLanguageManager extLanguageManager) {
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public Object visit(RootNode node) throws Exception {

			return RegexHelper.createMessageAllowedCharsForModel();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForClass(fExtLanguageManager);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForMethod(fExtLanguageManager);
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {

			String nodeNameInExtLanguage = AbstractNodeHelper.getName(node, fExtLanguageManager);

			return RegexHelper.createMessageAllowedCharsForNode(nodeNameInExtLanguage, fExtLanguageManager);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForParameter(fExtLanguageManager);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForParameter(fExtLanguageManager);
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
		public Object visit(TestSuiteNode node) throws Exception {
			return RegexHelper.REGEX_TEST_CASE_NODE_NAME;
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
