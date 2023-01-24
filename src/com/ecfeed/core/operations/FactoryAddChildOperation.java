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
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public class FactoryAddChildOperation implements IModelVisitor{

	private IAbstractNode fChild;
	private int fIndex;
	private boolean fValidate;
	IExtLanguageManager fExtLanguageManager;
	private ITypeAdapterProvider fAdapterProvider;

	public FactoryAddChildOperation(
			IAbstractNode child, 
			int index, 
			ITypeAdapterProvider adapterProvider, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fChild = child;
		fIndex = index;
		fValidate = validate;
		fExtLanguageManager = extLanguageManager;
		fAdapterProvider = adapterProvider;
	}

	public FactoryAddChildOperation(
			IAbstractNode child, 
			ITypeAdapterProvider adapterProvider, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		this(child, -1, adapterProvider, validate, extLanguageManager);
	}

	@Override
	public Object visit(RootNode rootNode) throws Exception {

		if (fChild instanceof ClassNode) {

			return createOperationAddClass(rootNode);

		} else if (fChild instanceof BasicParameterNode) {

			return createOperationAddParameter(rootNode);
		}

		reportOperationNotSupportedException();
		return null;
	}

	private Object createOperationAddParameter(RootNode rootNode) {

		BasicParameterNode abstractParameterNode = (BasicParameterNode)fChild;

		BasicParameterNode globalParameter = new BasicParameterNode(abstractParameterNode);

		if(fIndex == -1) {
			return new GenericOperationAddParameter(rootNode, globalParameter, true, fExtLanguageManager);
		}

		return new GenericOperationAddParameter(rootNode, globalParameter, fIndex, true, fExtLanguageManager);
	}

	private Object createOperationAddClass(RootNode rootNode) {

		ClassNode classNode = (ClassNode)fChild;

		generateUniqueNameForClass(rootNode, classNode);

		if (fIndex == -1) {
			return new OnClassOperationAddToRoot(rootNode, classNode, fExtLanguageManager);
		}

		return new OnClassOperationAddToRoot(rootNode, classNode, fIndex, fExtLanguageManager);
	}
	
	private void generateUniqueNameForClass(RootNode rootNode, ClassNode classNode) {

		String oldName = classNode.getName();
		String oldNameCore = StringHelper.removeFromNumericPostfix(oldName);
		String newName = RootNodeHelper.generateNewClassName(rootNode, oldNameCore);

		classNode.setName(newName);
	}

	@Override
	public Object visit(ClassNode node) throws Exception {

		if(fChild instanceof MethodNode){
			if(fIndex == -1){
				return new ClassOperationAddMethod(node, (MethodNode)fChild, fExtLanguageManager);
			}
			return new ClassOperationAddMethod(node, (MethodNode)fChild, fIndex, fExtLanguageManager);
		}else if(fChild instanceof BasicParameterNode){
			BasicParameterNode globalParameter = new BasicParameterNode((BasicParameterNode)fChild);
			if(fIndex == -1){
				return new GenericOperationAddParameter(node, globalParameter, true, fExtLanguageManager);
			}
			return new GenericOperationAddParameter(node, globalParameter, fIndex, true, fExtLanguageManager);
		}

		reportOperationNotSupportedException();
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof BasicParameterNode){
			BasicParameterNode globalParameter = (BasicParameterNode)fChild;
			String defaultValue = fAdapterProvider.getAdapter(globalParameter.getType()).getDefaultValue();
			BasicParameterNode parameter = new BasicParameterNode(globalParameter, defaultValue, false);

			if(fIndex == -1){
				return new OnParameterOperationAddToMethod(node,parameter, fExtLanguageManager);
			}
			return new OnParameterOperationAddToMethod(node, parameter, fIndex, fExtLanguageManager);
		}
		if(fChild instanceof BasicParameterNode){
			if(fIndex == -1){
				return new OnParameterOperationAddToMethod(node, (BasicParameterNode)fChild, fExtLanguageManager);
			}
			return new OnParameterOperationAddToMethod(node, (BasicParameterNode)fChild, fIndex, fExtLanguageManager);
		}
		if(fChild instanceof ConstraintNode){
			if(fIndex == -1){
				return new OnConstraintOperationAdd(node, (ConstraintNode)fChild, fExtLanguageManager);
			}
			return new OnConstraintOperationAdd(node, (ConstraintNode)fChild, fIndex, fExtLanguageManager);
		}
		if(fChild instanceof TestSuiteNode) {
			return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider, fExtLanguageManager);
		}
		if(fChild instanceof TestCaseNode){
			if(fIndex == -1){
				return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider, fExtLanguageManager);
			}
			return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider, fIndex, fExtLanguageManager);
		}

		reportOperationNotSupportedException();
		return null;
	}

	@Override
	public Object visit(BasicParameterNode node) throws Exception {

		if (node.isGlobalParameter()) {

			if(fChild instanceof ChoiceNode){
				if(fIndex == -1){
					return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguageManager);
				}
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate, fExtLanguageManager);
			}

			reportOperationNotSupportedException();
			return null;


		} else {
			
			if(fChild instanceof ChoiceNode){
				if(fIndex == -1){
					return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguageManager);
				}
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate, fExtLanguageManager);
			}

			reportOperationNotSupportedException();
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
		reportOperationNotSupportedException();
		return null;
	}
	
	@Override
	public Object visit(TestCaseNode node) throws Exception {
		reportOperationNotSupportedException();
		return null;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception {
		reportOperationNotSupportedException();
		return null;
	}

	@Override
	public Object visit(ChoiceNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguageManager);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate, fExtLanguageManager);
		}

		reportOperationNotSupportedException();
		return null;
	}

	private void reportOperationNotSupportedException() throws Exception {
		if (fValidate) {
			return;
		}

		ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

}
