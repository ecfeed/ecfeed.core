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
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ViewMode;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.SystemLogger;

public class FactoryRenameOperation {

	private static final String PARTITION_NAME_NOT_UNIQUE_PROBLEM = "Choice name must be unique within a parameter or parent choice";
	private static final String CLASS_NAME_CONTAINS_KEYWORD_PROBLEM = "The new class name contains Java keyword";	

	private static class ClassOperationRename extends GenericOperationRename {

		ViewMode fViewMode;

		public ClassOperationRename(AbstractNode target, String newName, ViewMode viewMode) {
			super(target, newName, viewMode);
			fViewMode = viewMode;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ClassOperationRename(getOwnNode(), getOriginalName(), fViewMode);
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			for(String token : getNewNameInJavaConvention().split("\\.")){
				if(JavaLanguageHelper.isJavaKeyword(token)){
					ModelOperationException.report(CLASS_NAME_CONTAINS_KEYWORD_PROBLEM);
				}
			}
			if(getOwnNode().getSibling(getNewNameInJavaConvention()) != null){
				ModelOperationException.report(OperationMessages.CLASS_NAME_DUPLICATE_PROBLEM);
			}
		}
	}

	private static class MethodOperationRename extends GenericOperationRename {

		ViewMode fViewMode;

		public MethodOperationRename(MethodNode target, String newName, ViewMode viewMode) {

			super(target, newName, viewMode);

			fViewMode = viewMode;
		}

		@Override
		public IModelOperation getReverseOperation() {

			return new MethodOperationRename((MethodNode)getOwnNode(), getOriginalName(), fViewMode);
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			
			List<String> problems = new ArrayList<String>();
			
			MethodNode targetMethodNode = (MethodNode)getOwnNode();

			if (!ClassNodeHelper.isNewMethodSignatureValid(
					targetMethodNode.getClassNode(), 
					getNewNameInJavaConvention(), 
					targetMethodNode.getParameterTypes(),
					fViewMode,
					problems)) {
				
				ClassNodeHelper.updateNewMethodsSignatureProblemList(
						targetMethodNode.getClassNode(), getNewNameInJavaConvention(), targetMethodNode.getParameterTypes(), problems);
				
				ModelOperationException.report(StringHelper.convertToMultilineString(problems));
			}
		}
	}

	private static class GlobalParameterOperationRename extends GenericOperationRename {

		ViewMode fViewMode;

		public GlobalParameterOperationRename(AbstractNode target, String newName, ViewMode viewMode) {

			super(target, newName, viewMode);

			fViewMode = viewMode;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new GlobalParameterOperationRename(getOwnNode(), getOriginalName(), fViewMode);
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			GlobalParameterNode target = (GlobalParameterNode) getOwnNode();
			if(JavaLanguageHelper.isJavaKeyword(newName)){
				ModelOperationException.report(OperationMessages.CATEGORY_NAME_REGEX_PROBLEM);
			}
			if(target.getParametersParent().getParameter(newName) != null){
				ModelOperationException.report(OperationMessages.CATEGORY_NAME_DUPLICATE_PROBLEM);
			}
		}
	}

	private static class MethodParameterOperationRename extends GenericOperationRename {

		ViewMode fViewMode;

		public MethodParameterOperationRename(AbstractNode target, String newName, ViewMode viewMode) {
			super(target, newName, viewMode);
			fViewMode = viewMode;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodParameterOperationRename(getOwnNode(), getOriginalName(), fViewMode);
		}

		@Override
		protected void verifyNewName(String newName) throws ModelOperationException {
			MethodParameterNode target = (MethodParameterNode)getOwnNode();
			if(JavaLanguageHelper.isJavaKeyword(newName)){
				ModelOperationException.report(OperationMessages.CATEGORY_NAME_REGEX_PROBLEM);
			}
			if(target.getMethod().getParameter(newName) != null){
				ModelOperationException.report(OperationMessages.CATEGORY_NAME_DUPLICATE_PROBLEM);
			}
		}
	}

	private static class ChoiceOperationRename extends GenericOperationRename {

		ViewMode fViewMode;

		public ChoiceOperationRename(ChoiceNode target, String newName, ViewMode viewMode) {
			super(target, newName, viewMode);
			fViewMode = viewMode;
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationRename((ChoiceNode)getOwnNode(), getOriginalName(), fViewMode);
		}

		@Override
		protected void verifyNewName(String newName)throws ModelOperationException{
			if(getOwnNode().getSibling(getNewNameInJavaConvention()) != null){
				ModelOperationException.report(PARTITION_NAME_NOT_UNIQUE_PROBLEM);
			}
		}
	}

	private static class RenameOperationProvider implements IModelVisitor{

		private String fNewName;
		private ViewMode fViewMode;

		public RenameOperationProvider(String newName, ViewMode viewMode) {
			fNewName = newName;
			fViewMode = viewMode;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return new GenericOperationRename(node, fNewName, fViewMode);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new ClassOperationRename(node, fNewName, fViewMode);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new MethodOperationRename(node, fNewName, fViewMode);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return new MethodParameterOperationRename(node, fNewName, fViewMode);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return new GlobalParameterOperationRename(node, fNewName, fViewMode);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return new GenericOperationRename(node, fNewName,fViewMode);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return new GenericOperationRename(node, fNewName, fViewMode);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return new ChoiceOperationRename(node, fNewName, fViewMode);
		}
	}

	public static IModelOperation getRenameOperation(AbstractNode target, String newName, ViewMode viewMode){

		try{
			return (IModelOperation)target.accept(new RenameOperationProvider(newName, viewMode));
		} catch(Exception e) {
			SystemLogger.logCatch(e);
		}

		return null;
	}
}
