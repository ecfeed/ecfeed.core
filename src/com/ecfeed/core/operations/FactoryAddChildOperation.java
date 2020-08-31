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
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.ExtLanguage;

public class FactoryAddChildOperation implements IModelVisitor{

	private AbstractNode fChild;
	private int fIndex;
	private boolean fValidate;
	ExtLanguage fExtLanguage;
	private ITypeAdapterProvider fAdapterProvider;

	public FactoryAddChildOperation(
			AbstractNode child, 
			int index, 
			ITypeAdapterProvider adapterProvider, 
			boolean validate,
			ExtLanguage extLanguage) {

		fChild = child;
		fIndex = index;
		fValidate = validate;
		fExtLanguage = extLanguage;
		fAdapterProvider = adapterProvider;
	}

	public FactoryAddChildOperation(
			AbstractNode child, 
			ITypeAdapterProvider adapterProvider, 
			boolean validate,
			ExtLanguage extLanguage) {

		this(child, -1, adapterProvider, validate, extLanguage);
	}

	@Override
	public Object visit(RootNode rootNode) throws Exception {

		if (fChild instanceof ClassNode) {

			return createOperationAddClass(rootNode);

		} else if (fChild instanceof AbstractParameterNode) {

			return createOperationAddParameter(rootNode);
		}

		reportOperationNotSupportedException();
		return null;
	}

	private Object createOperationAddParameter(RootNode rootNode) {

		AbstractParameterNode abstractParameterNode = (AbstractParameterNode)fChild;

		//It might be problematic that we actually add a copy of the requested node, so the option to add
		//a MethodParameterNode to GlobalParameterParent (and vice versa) might be removed
		GlobalParameterNode globalParameter = new GlobalParameterNode(abstractParameterNode);

		if(fIndex == -1) {
			return new GenericOperationAddParameter(rootNode, globalParameter, true, fExtLanguage);
		}

		return new GenericOperationAddParameter(rootNode, globalParameter, fIndex, true, fExtLanguage);
	}

	private Object createOperationAddClass(RootNode rootNode) {

		ClassNode classNode = (ClassNode)fChild;

		generateUniqueNameForClass(rootNode, classNode);

		if (fIndex == -1) {
			return new RootOperationAddNewClass(rootNode, classNode, fExtLanguage);
		}

		return new RootOperationAddNewClass(rootNode, classNode, fIndex, fExtLanguage);
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
				return new ClassOperationAddMethod(node, (MethodNode)fChild, fExtLanguage);
			}
			return new ClassOperationAddMethod(node, (MethodNode)fChild, fIndex, fExtLanguage);
		}else if(fChild instanceof AbstractParameterNode){
			GlobalParameterNode globalParameter = new GlobalParameterNode((AbstractParameterNode)fChild);
			if(fIndex == -1){
				return new GenericOperationAddParameter(node, globalParameter, true, fExtLanguage);
			}
			return new GenericOperationAddParameter(node, globalParameter, fIndex, true, fExtLanguage);
		}

		reportOperationNotSupportedException();
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof GlobalParameterNode){
			GlobalParameterNode globalParameter = (GlobalParameterNode)fChild;
			String defaultValue = fAdapterProvider.getAdapter(globalParameter.getType()).getDefaultValue();
			MethodParameterNode parameter = new MethodParameterNode(globalParameter, defaultValue, false);

			if(fIndex == -1){
				return new MethodOperationAddParameter(node,parameter, fExtLanguage);
			}
			return new MethodOperationAddParameter(node, parameter, fIndex, fExtLanguage);
		}
		if(fChild instanceof MethodParameterNode){
			if(fIndex == -1){
				return new MethodOperationAddParameter(node, (MethodParameterNode)fChild, fExtLanguage);
			}
			return new MethodOperationAddParameter(node, (MethodParameterNode)fChild, fIndex, fExtLanguage);
		}
		if(fChild instanceof ConstraintNode){
			if(fIndex == -1){
				return new MethodOperationAddConstraint(node, (ConstraintNode)fChild, fExtLanguage);
			}
			return new MethodOperationAddConstraint(node, (ConstraintNode)fChild, fIndex, fExtLanguage);
		}
		if(fChild instanceof TestCaseNode){
			if(fIndex == -1){
				return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider, fExtLanguage);
			}
			return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider, fIndex, fExtLanguage);
		}

		reportOperationNotSupportedException();
		return null;
	}

	@Override
	public Object visit(MethodParameterNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguage);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate, fExtLanguage);
		}

		reportOperationNotSupportedException();
		return null;
	}

	@Override
	public Object visit(GlobalParameterNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguage);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate, fExtLanguage);
		}

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
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate, fExtLanguage);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate, fExtLanguage);
		}

		reportOperationNotSupportedException();
		return null;
	}

	private void reportOperationNotSupportedException() throws Exception {
		if (fValidate) {
			return;
		}

		ModelOperationException.report(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}
}
