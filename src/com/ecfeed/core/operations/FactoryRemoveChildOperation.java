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
import com.ecfeed.core.operations.nodes.OnBasicParameterOperationRemove;
import com.ecfeed.core.operations.nodes.OnClassOperationRemove;
import com.ecfeed.core.operations.nodes.OnConstraintOperationAdd;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveFromClass;
import com.ecfeed.core.operations.nodes.OnTestCaseOperationRemove;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class FactoryRemoveChildOperation implements IModelVisitor{

	private IAbstractNode fChild;
	private boolean fValidate;
	private ITypeAdapterProvider fAdapterProvider;
	private IExtLanguageManager fExtLanguageManager;

	public FactoryRemoveChildOperation(
			IAbstractNode child, ITypeAdapterProvider adapterProvider, boolean validate, IExtLanguageManager extLanguageManager) {
		fChild = child;
		fValidate = validate;
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		if(fChild instanceof ClassNode){
			return new OnClassOperationRemove(node, (ClassNode)fChild, fExtLanguageManager);
		}
		if(fChild instanceof BasicParameterNode && ((BasicParameterNode)(fChild)).isGlobalParameter()){
			return new GenericOperationRemoveParameter(node, (BasicParameterNode)fChild, fExtLanguageManager);
		}
		return null;
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		if(fChild instanceof MethodNode){
			return new OnMethodOperationRemoveFromClass(node, (MethodNode)fChild, fExtLanguageManager);
		}
		if(fChild instanceof BasicParameterNode && ((BasicParameterNode)(fChild)).isGlobalParameter()){
			return new GenericOperationRemoveParameter(node, (BasicParameterNode)fChild, fExtLanguageManager);
		}
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof BasicParameterNode){
			return new OnBasicParameterOperationRemove(node, (BasicParameterNode)fChild, fExtLanguageManager);
		}
		if(fChild instanceof ConstraintNode){
			return new OnConstraintOperationAdd(node, (ConstraintNode)fChild, fExtLanguageManager);
		}
		if(fChild instanceof TestSuiteNode) {
			return new OnTestCaseOperationRemove(node, (TestCaseNode)fChild, fExtLanguageManager);
		}
		if(fChild instanceof TestCaseNode){
			return new OnTestCaseOperationRemove(node, (TestCaseNode)fChild, fExtLanguageManager);
		}
		return null;
	}

	@Override
	public Object visit(BasicParameterNode node) throws Exception {

		if (node.isGlobalParameter()) {
			if(fChild instanceof ChoiceNode){
				return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguageManager);
			}
			return null;

		} else {

			if(fChild instanceof ChoiceNode){
				return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguageManager);
			}
			return null;
		}
	}

	@Override
	public Object visit(CompositeParameterNode node) throws Exception {
		ExceptionHelper.reportRuntimeException("TODO"); // TODO MO-RE
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
			return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguageManager);
		}
		return null;
	}

}
