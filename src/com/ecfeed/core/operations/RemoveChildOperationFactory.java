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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.operations.nodes.OnBasicParameterOperationRemoveFromCompositeParameter;
import com.ecfeed.core.operations.nodes.OnBasicParameterOperationRemoveFromMethod;
import com.ecfeed.core.operations.nodes.OnClassOperationRemove;
import com.ecfeed.core.operations.nodes.OnCompositeParameterOperationRemove;
import com.ecfeed.core.operations.nodes.OnConstraintOperationAdd;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveFromClass;
import com.ecfeed.core.operations.nodes.OnTestCaseOperationRemove;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class RemoveChildOperationFactory implements IModelVisitor{

	private IAbstractNode fChild;
	private boolean fValidate;
	private IExtLanguageManager fExtLanguageManager;

	public RemoveChildOperationFactory(
			IAbstractNode child, boolean validate, IExtLanguageManager extLanguageManager) {
		fChild = child;
		fValidate = validate;
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public Object visit(RootNode rootNode) throws Exception {

		if(fChild instanceof ClassNode){
			return new OnClassOperationRemove(rootNode, (ClassNode)fChild, fExtLanguageManager);
		}

		if(fChild instanceof BasicParameterNode && ((BasicParameterNode)(fChild)).isGlobalParameter()){
			return new GenericOperationRemoveParameter(rootNode, (BasicParameterNode)fChild, fExtLanguageManager);
		}

		return null;
	}

	@Override
	public Object visit(ClassNode classNode) throws Exception {

		if(fChild instanceof MethodNode){
			return new OnMethodOperationRemoveFromClass(classNode, (MethodNode)fChild, fExtLanguageManager);
		}

		if(fChild instanceof BasicParameterNode && ((BasicParameterNode)(fChild)).isGlobalParameter()){
			return new GenericOperationRemoveParameter(classNode, (BasicParameterNode)fChild, fExtLanguageManager);
		}
		return null;
	}

	@Override
	public Object visit(MethodNode methodNode) throws Exception {

		if(fChild instanceof BasicParameterNode){
			return new OnBasicParameterOperationRemoveFromMethod(methodNode, (BasicParameterNode)fChild, fExtLanguageManager);
		}

		if(fChild instanceof ConstraintNode){
			return new OnConstraintOperationAdd(methodNode, (ConstraintNode)fChild, fExtLanguageManager);
		}

		if(fChild instanceof TestSuiteNode) {
			return new OnTestCaseOperationRemove(methodNode, (TestCaseNode)fChild, fExtLanguageManager);
		}

		if(fChild instanceof TestCaseNode){
			return new OnTestCaseOperationRemove(methodNode, (TestCaseNode)fChild, fExtLanguageManager);
		}

		return null;
	}

	@Override
	public Object visit(BasicParameterNode node) throws Exception {

		if (node.isGlobalParameter()) {
			if(fChild instanceof ChoiceNode){
				return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fValidate, fExtLanguageManager);
			}
			return null;

		} else {

			if(fChild instanceof ChoiceNode){
				return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fValidate, fExtLanguageManager);
			}
			return null;
		}
	}

	@Override
	public Object visit(CompositeParameterNode compositeParameterNode) throws Exception {

		if (fChild instanceof BasicParameterNode) {

			return new OnBasicParameterOperationRemoveFromCompositeParameter(
					compositeParameterNode, 
					(BasicParameterNode)fChild, 
					fExtLanguageManager	);
		}
		
		if (fChild instanceof CompositeParameterNode) {

			return new OnCompositeParameterOperationRemove(
					compositeParameterNode, 
					(CompositeParameterNode)fChild, 
					false,
					fExtLanguageManager	);
		}

		ExceptionHelper.reportRuntimeException("Operation not allowed.");
		return null;
	}

	@Override
	public Object visit(TestSuiteNode node) throws Exception {
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

		if(fChild instanceof ChoiceNode){
			return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fValidate, fExtLanguageManager);
		}

		return null;
	}

}
