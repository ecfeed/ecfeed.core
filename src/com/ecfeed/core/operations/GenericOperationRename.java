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

import com.ecfeed.core.model.AbstractNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.model.utils.NodeNameHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.QualifiedNameHelper;
import com.ecfeed.core.utils.RegexHelper;

public class GenericOperationRename extends AbstractModelOperation {

	private IAbstractNode fTargetAbstractNode;

	private String fNewPackageName;
	private String fNewNonQualifiedNameInExtLanguage;

	private String fOriginalPackageName;
	private String fOriginalNonQualifiedNameInExtLanguage;

	private String fJavaNameRegex;
	private IExtLanguageManager fExtLanguageManager;

	public GenericOperationRename(
			IAbstractNode target,
			String newPackageName,
			String newNonQualifiedNameInExtLanguage, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.RENAME, extLanguageManager);

		fTargetAbstractNode = target;

		fNewPackageName = newPackageName;
		fNewNonQualifiedNameInExtLanguage = newNonQualifiedNameInExtLanguage;

		fOriginalPackageName = QualifiedNameHelper.getPackage(target.getName());
		fOriginalNonQualifiedNameInExtLanguage = QualifiedNameHelper.getNonQualifiedName(target.getName());

		fJavaNameRegex = NodeNameHelper.getRegex(target);
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fTargetAbstractNode);

		String oldQualifiedNameInIntrLanguage = fTargetAbstractNode.getName();

		String newQualifiedNameInIntrLanguage = prepareNewQualifiedName();

		setNewNameWithCheck(newQualifiedNameInIntrLanguage, oldQualifiedNameInIntrLanguage);

		markModelUpdated();
	}

	private String prepareNewQualifiedName() {

		String newQualifiedNameInExtLanguage = 
				fExtLanguageManager.createQualifiedName(fNewPackageName, fNewNonQualifiedNameInExtLanguage);

		verifyNewName(newQualifiedNameInExtLanguage);

		String newNonQualifiedNameInIntrLanguage = 		
				AbstractNodeHelper.convertTextFromExtToIntrLanguage(
						fTargetAbstractNode, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);

		String newQualifiedNameInIntrLanguage = 
				JavaLanguageHelper.createQualifiedName(fNewPackageName, newNonQualifiedNameInIntrLanguage);

		if (!(fTargetAbstractNode instanceof RootNode)) {
			verifyNameWithRegex(
					newQualifiedNameInIntrLanguage, 
					fJavaNameRegex, 
					fTargetAbstractNode, 
					fExtLanguageManager);
		}

		return newQualifiedNameInIntrLanguage;
	}

	private void setNewNameWithCheck(
			String newQualifiedNameInIntrLanguage, 
			String oldQualifiedNameInIntrLanguage) {

		if (fTargetAbstractNode instanceof MethodNode) {
			
			MethodNode methodNode = (MethodNode) fTargetAbstractNode;
			ClassNode classNode = methodNode.getClassNode();
			
			MethodNode foundMethodNode = classNode.findMethodWithTheSameName(newQualifiedNameInIntrLanguage);
			
			if (foundMethodNode != null) {
				String message = "Method with name " + newQualifiedNameInIntrLanguage + " already exists in class.";
				ExceptionHelper.reportRuntimeException(message);
			}
		}
		
		if (fTargetAbstractNode instanceof TestSuiteNode) {
			setNewNameInChildTestCases(newQualifiedNameInIntrLanguage);
		}
		
		fTargetAbstractNode.setName(newQualifiedNameInIntrLanguage);		

		RootNode rootNode = RootNodeHelper.findRootNode(fTargetAbstractNode);

		String errorMessage = fExtLanguageManager.checkIsModelCompatibleWithExtLanguage(rootNode);

		if (errorMessage != null) {
			fTargetAbstractNode.setName(oldQualifiedNameInIntrLanguage);
			ExceptionHelper.reportRuntimeException(errorMessage);
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

	protected IAbstractNode getOwnNode(){
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

	protected void verifyNewName(String newNameInExtLanguage) {
	}

	private static void verifyNameWithRegex(
			String name, 
			String regex, 
			IAbstractNode targetNode,
			IExtLanguageManager extLanguageManagerForDisplayingErrorMessage) {

		if (name.matches(regex) == false) {

			String regexProblemMessage = getRegexProblemMessage(targetNode, extLanguageManagerForDisplayingErrorMessage);

			ExceptionHelper.reportRuntimeException(regexProblemMessage);
		}
	}

	private static String getRegexProblemMessage(IAbstractNode abstractNode, IExtLanguageManager extLanguageManager){
		try{
			return (String)abstractNode.accept(new RegexProblemMessageProvider(extLanguageManager));
		}catch(Exception e){LogHelperCore.logCatch(e);}
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
		public Object visit(BasicParameterNode node) throws Exception {
			return RegexHelper.createMessageAllowedCharsForParameter(fExtLanguageManager);
		}

		@Override
		public Object visit(CompositeParameterNode node) throws Exception {
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

}
