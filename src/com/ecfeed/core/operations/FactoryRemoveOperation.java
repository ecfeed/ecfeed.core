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

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.GlobalParametersParentNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class FactoryRemoveOperation {

	private static class UnsupportedModelOperation implements IModelOperation{
		@Override
		public void execute() {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new UnsupportedModelOperation();
		}

		@Override
		public boolean modelUpdated() {
			return false;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public List<AbstractNode> getNodesToSelect() {
			return null;
		}

		@Override
		public void setNodesToSelect(List<AbstractNode> nodes) {
		}


	}

	private static class RemoveOperationVisitor implements IModelVisitor{

		private boolean fValidate;
		private ITypeAdapterProvider fAdapterProvider;
		IExtLanguageManager fExtLanguageManager;

		public RemoveOperationVisitor(ITypeAdapterProvider adapterProvider, boolean validate, IExtLanguageManager extLanguageManager) {
			fValidate = validate;
			fAdapterProvider = adapterProvider;
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return new UnsupportedModelOperation();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new RootOperationRemoveClass(node.getRoot(), node, fExtLanguageManager);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new ClassOperationRemoveMethod(node.getClassNode(), node, fExtLanguageManager);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return new MethodOperationRemoveParameter(node.getMethod(), node, fValidate, fExtLanguageManager);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return new GenericOperationRemoveGlobalParameter(
					(GlobalParametersParentNode)node.getParametersParent(), 
					node,
					fExtLanguageManager);
		}
		
		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			return new MethodOperationRemoveTestSuite(node.getMethod(), node, fExtLanguageManager);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return new MethodOperationRemoveTestCase(node.getMethod(), node, fExtLanguageManager);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return new MethodOperationRemoveConstraint(node.getMethodNode(), node, fExtLanguageManager);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return new GenericOperationRemoveChoice(node.getParent(), node, fAdapterProvider, fValidate, fExtLanguageManager);
		}
	}

	public static IModelOperation getRemoveOperation(
			AbstractNode node, ITypeAdapterProvider adapterProvider, boolean validate, IExtLanguageManager extLanguageManager){
		try {
			return (IModelOperation)node.accept(new RemoveOperationVisitor(adapterProvider, validate, extLanguageManager));
		} catch (Exception e) {
			return new UnsupportedModelOperation();
		}
	}
}
