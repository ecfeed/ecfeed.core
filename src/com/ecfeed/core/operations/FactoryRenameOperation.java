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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.*;

public class FactoryRenameOperation {

	public static final String PARTITION_NAME_NOT_UNIQUE_PROBLEM = "Choice name must be unique within a parameter or parent choice";
	public static final String CLASS_NAME_CONTAINS_KEYWORD_PROBLEM = "The new class name contains Java keyword";

	private static class ClassOperationRename extends GenericOperationRename {

		public ClassOperationRename(
				AbstractNode target, 
				String newPackageName, 
				String newNonQualifiedNameInExtLanguage, 
				IExtLanguageManager extLanguageManager) {

			super(target, newPackageName, newNonQualifiedNameInExtLanguage, extLanguageManager);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ClassOperationRename(getOwnNode(), getOriginalPackageName(), getOriginalNonQualifiedName(), getExtLanguageManager());
		}

		@Override
		protected void verifyNewName(String newNameInExtLanguage) {

			String[] tokens = newNameInExtLanguage.split("\\.");

			for (String token : tokens) {
				if(JavaLanguageHelper.isJavaKeyword(token)){
					ExceptionHelper.reportRuntimeException(CLASS_NAME_CONTAINS_KEYWORD_PROBLEM);
				}
			}

			if(getOwnNode().getSibling(newNameInExtLanguage) != null){
				ExceptionHelper.reportRuntimeException(OperationMessages.CLASS_NAME_DUPLICATE_PROBLEM);
			}
		}
	}

	private static class MethodOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;

		public MethodOperationRename(MethodNode target, String newName, IExtLanguageManager extLanguageManager) {

			super(target, null, newName, extLanguageManager);

			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public IModelOperation getReverseOperation() {

			return new MethodOperationRename((MethodNode)getOwnNode(), getOriginalNonQualifiedName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newNameInExtLanguage) {

			List<String> problems = new ArrayList<String>();

			MethodNode targetMethodNode = (MethodNode)getOwnNode();

			IExtLanguageManager extLanguageManager = getExtLanguageManager();

			String errorMessage =
					ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
							targetMethodNode.getClassNode(),
							newNameInExtLanguage,
							MethodNodeHelper.getParameterTypes(targetMethodNode, extLanguageManager),
							extLanguageManager);

			if (errorMessage != null) {
				problems.add(errorMessage);
				ExceptionHelper.reportRuntimeException(StringHelper.convertToMultilineString(problems));
			}
		}
	}

	private static class GlobalParameterOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;

		public GlobalParameterOperationRename(AbstractNode target, String newName, IExtLanguageManager extLanguageManager) {

			super(target, null, newName, extLanguageManager);

			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new GlobalParameterOperationRename(getOwnNode(), getOriginalNonQualifiedName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newNameInExtLanguage) {
			
			String errorMessage = AbstractParameterNodeHelper.validateParameterName(newNameInExtLanguage, fExtLanguageManager);
			
			if (errorMessage != null) {
				ExceptionHelper.reportRuntimeException(errorMessage);
			}

			GlobalParameterNode target = (GlobalParameterNode) getOwnNode();
			if(JavaLanguageHelper.isJavaKeyword(newNameInExtLanguage)){
				ExceptionHelper.reportRuntimeException(RegexHelper.createMessageAllowedCharsForMethod(fExtLanguageManager));
			}
			if(target.getParametersParent().findParameter(newNameInExtLanguage) != null){
				ExceptionHelper.reportRuntimeException(OperationMessages.PARAMETER_WITH_THIS_NAME_ALREADY_EXISTS);
			}
		}
	}
	
	private static class MethodParameterOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;

		public MethodParameterOperationRename(AbstractNode target, String newName, IExtLanguageManager extLanguageManager) {
			super(target, null, newName, extLanguageManager);
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodParameterOperationRename(getOwnNode(), getOriginalNonQualifiedName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newNameInExtLanguage) {

			MethodParameterNode target = (MethodParameterNode)getOwnNode();

			if(JavaLanguageHelper.isJavaKeyword(newNameInExtLanguage)){
				ExceptionHelper.reportRuntimeException(RegexHelper.createMessageAllowedCharsForMethod(fExtLanguageManager));
			}

			MethodNode method = target.getMethod();

			IExtLanguageManager extLanguageManager = getExtLanguageManager();
			String newNameInIntrLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(newNameInExtLanguage);

			if(method.findParameter(newNameInIntrLanguage) != null){
				ExceptionHelper.reportRuntimeException(OperationMessages.PARAMETER_WITH_THIS_NAME_ALREADY_EXISTS);
			}
		}
	}

	private static class ChoiceOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;

		public ChoiceOperationRename(ChoiceNode target, String newName, IExtLanguageManager extLanguageManager) {

			super(target, null, newName, extLanguageManager);

			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationRename((ChoiceNode)getOwnNode(), getOriginalNonQualifiedName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newNameInExtLanguage) {

			String newNameInIntrLanguage = newNameInExtLanguage;

			final AbstractNode ownNode = getOwnNode();

			if(ownNode.getSibling(newNameInIntrLanguage) != null){
				ExceptionHelper.reportRuntimeException(PARTITION_NAME_NOT_UNIQUE_PROBLEM);
			}
		}
	}

	private static class RenameOperationProvider implements IModelVisitor{

		private String fNewPackageName;
		private String fNewNonQualifiedNameInExtLanguage;
		private IExtLanguageManager fExtLanguageManager;

		public RenameOperationProvider(String newPackageName, String newNonQualifiedNameInExtLanguage, IExtLanguageManager extLanguageManager) {

			fNewPackageName  = newPackageName;
			fNewNonQualifiedNameInExtLanguage = newNonQualifiedNameInExtLanguage;
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return new GenericOperationRename(node, fNewPackageName, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new ClassOperationRename(node, fNewPackageName, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {

			return new MethodOperationRename(node, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			return new GenericOperationRename(node, null, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}
		
		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return new MethodParameterOperationRename(node, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return new GlobalParameterOperationRename(node, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return new GenericOperationRename(node, fNewPackageName, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return new GenericOperationRename(node, fNewPackageName, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return new ChoiceOperationRename(node, fNewNonQualifiedNameInExtLanguage, fExtLanguageManager);
		}
	}

	public static IModelOperation getRenameOperation(AbstractNode target, String newPackageName, String newNonQualifiedNameInExtLanguage, IExtLanguageManager extLanguageManager){

		try{
			return (IModelOperation)target.accept(new RenameOperationProvider(
					newPackageName, newNonQualifiedNameInExtLanguage, extLanguageManager));
		} catch(Exception e) {
			SystemLogger.logCatch(e);
		}

		return null;
	}
}
