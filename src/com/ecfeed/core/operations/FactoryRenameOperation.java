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

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.*;

public class FactoryRenameOperation {

	private static final String PARTITION_NAME_NOT_UNIQUE_PROBLEM = "Choice name must be unique within a parameter or parent choice";
	private static final String CLASS_NAME_CONTAINS_KEYWORD_PROBLEM = "The new class name contains Java keyword";	

	private static class ClassOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;
		String fNewName;

		public ClassOperationRename(AbstractNode target, String newName, IExtLanguageManager extLanguageManager) {
			super(target, newName, extLanguageManager);
			fExtLanguageManager = extLanguageManager;
			fNewName = newName;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ClassOperationRename(getOwnNode(), getOriginalName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {

			String newNameInJavaConvention = getExtLanguageManager().convertTextFromExtToIntrLanguage(fNewName);

			String[] tokens = newNameInJavaConvention.split("\\.");

			for (String token : tokens) {
				if(JavaLanguageHelper.isJavaKeyword(token)){
					ModelOperationException.report(CLASS_NAME_CONTAINS_KEYWORD_PROBLEM);
				}
			}
			if(getOwnNode().getSibling(newNameInJavaConvention) != null){
				ModelOperationException.report(OperationMessages.CLASS_NAME_DUPLICATE_PROBLEM);
			}
		}
	}

	private static class MethodOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;

		public MethodOperationRename(MethodNode target, String newName, IExtLanguageManager extLanguageManager) {

			super(target, newName, extLanguageManager);

			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public IModelOperation getReverseOperation() {

			return new MethodOperationRename((MethodNode)getOwnNode(), getOriginalName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newNameInExtLanguage) throws ModelOperationException {

			List<String> problems = new ArrayList<String>();

			MethodNode targetMethodNode = (MethodNode)getOwnNode();

			IExtLanguageManager extLanguageManager = getExtLanguageManager();

			String errorMessage =
					ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
							targetMethodNode.getClassNode(),
							newNameInExtLanguage,
							MethodNodeHelper.getMethodParameterTypes(targetMethodNode, extLanguageManager),
							extLanguageManager);

			if (errorMessage != null) {
				problems.add(errorMessage);
				ModelOperationException.report(StringHelper.convertToMultilineString(problems));
			}
		}
	}

	private static class GlobalParameterOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;

		public GlobalParameterOperationRename(AbstractNode target, String newName, IExtLanguageManager extLanguageManager) {

			super(target, newName, extLanguageManager);

			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new GlobalParameterOperationRename(getOwnNode(), getOriginalName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			GlobalParameterNode target = (GlobalParameterNode) getOwnNode();
			if(JavaLanguageHelper.isJavaKeyword(newName)){
				ModelOperationException.report(RegexHelper.createMessageAllowedCharsForMethod(fExtLanguageManager));
			}
			if(target.getParametersParent().getParameter(newName) != null){
				ModelOperationException.report(OperationMessages.CATEGORY_NAME_DUPLICATE_PROBLEM);
			}
		}
	}

	private static class MethodParameterOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;

		public MethodParameterOperationRename(AbstractNode target, String newName, IExtLanguageManager extLanguageManager) {
			super(target, newName, extLanguageManager);
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodParameterOperationRename(getOwnNode(), getOriginalName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newNameInExtLanguage) throws ModelOperationException {
			
			MethodParameterNode target = (MethodParameterNode)getOwnNode();
			
			if(JavaLanguageHelper.isJavaKeyword(newNameInExtLanguage)){
				ModelOperationException.report(RegexHelper.createMessageAllowedCharsForMethod(fExtLanguageManager));
			}
			
			MethodNode method = target.getMethod();
			
			IExtLanguageManager extLanguageManager = getExtLanguageManager();
			String newNameInIntrLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(newNameInExtLanguage);
			
			if(method.getParameter(newNameInIntrLanguage) != null){
				ModelOperationException.report(OperationMessages.CATEGORY_NAME_DUPLICATE_PROBLEM);
			}
		}
	}

	private static class ChoiceOperationRename extends GenericOperationRename {

		IExtLanguageManager fExtLanguageManager;

		public ChoiceOperationRename(ChoiceNode target, String newName, IExtLanguageManager extLanguageManager) {

			super(target, newName, extLanguageManager);

			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationRename((ChoiceNode)getOwnNode(), getOriginalName(), fExtLanguageManager);
		}

		@Override
		protected void verifyNewName(String newNameInIntrLanguage)throws ModelOperationException{

			//			newNameInIntrLanguage = ExtLanguageHelper.convertTextFromExtToIntrLanguage(newNameInIntrLanguage, fExtLanguageManager);

			if(getOwnNode().getSibling(newNameInIntrLanguage) != null){
				ModelOperationException.report(PARTITION_NAME_NOT_UNIQUE_PROBLEM);
			}
		}
	}

	private static class RenameOperationProvider implements IModelVisitor{

		private String fNewName;
		private IExtLanguageManager fExtLanguageManager;

		public RenameOperationProvider(String newName, IExtLanguageManager extLanguageManager) {
			fNewName = newName;
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return new GenericOperationRename(node, fNewName, fExtLanguageManager);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new ClassOperationRename(node, fNewName, fExtLanguageManager);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new MethodOperationRename(node, fNewName, fExtLanguageManager);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return new MethodParameterOperationRename(node, fNewName, fExtLanguageManager);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return new GlobalParameterOperationRename(node, fNewName, fExtLanguageManager);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return new GenericOperationRename(node, fNewName,fExtLanguageManager);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return new GenericOperationRename(node, fNewName, fExtLanguageManager);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return new ChoiceOperationRename(node, fNewName, fExtLanguageManager);
		}
	}

	public static IModelOperation getRenameOperation(AbstractNode target, String newName, IExtLanguageManager extLanguageManager){

		try{
			return (IModelOperation)target.accept(new RenameOperationProvider(newName, extLanguageManager));
		} catch(Exception e) {
			SystemLogger.logCatch(e);
		}

		return null;
	}
}
