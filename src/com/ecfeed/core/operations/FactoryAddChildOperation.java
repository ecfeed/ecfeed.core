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

import java.util.Optional;

import com.ecfeed.core.model.AbstractParameterNode;
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
import com.ecfeed.core.operations.nodes.OnClassOperationAddToRoot;
import com.ecfeed.core.operations.nodes.OnConstraintOperationAdd;
import com.ecfeed.core.operations.nodes.OnMethodOperationAddToClass;
import com.ecfeed.core.operations.nodes.OnParameterOperationAddToParent;
import com.ecfeed.core.operations.nodes.OnTestCaseOperationAddToMethod;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public class FactoryAddChildOperation implements IModelVisitor {

	private IAbstractNode fChild;
	private int fIndex;
	private boolean fValidate;
	IExtLanguageManager fExtLanguageManager;

	public FactoryAddChildOperation(
			IAbstractNode child, 
			int index, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fChild = child;
		fIndex = index;
		fValidate = validate;
		fExtLanguageManager = extLanguageManager;
	}

	public FactoryAddChildOperation(
			IAbstractNode child, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		this(child, -1, validate, extLanguageManager);
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

	@Override
	public Object visit(ClassNode node) throws Exception {

		if(fChild instanceof MethodNode){
			if(fIndex == -1){
				return new OnMethodOperationAddToClass(node, (MethodNode)fChild, fExtLanguageManager);
			}
			return new OnMethodOperationAddToClass(node, (MethodNode)fChild, fIndex, fExtLanguageManager);
		}else if(fChild instanceof BasicParameterNode){
			BasicParameterNode globalParameter = 
					((BasicParameterNode)fChild).makeClone();
			//					new BasicParameterNode((BasicParameterNode)fChild);
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

		if (fChild instanceof AbstractParameterNode ) {
			return new OnParameterOperationAddToParent(node, (AbstractParameterNode)fChild, fIndex, fExtLanguageManager);
		}

		if (fChild instanceof ConstraintNode) {
			return new OnConstraintOperationAdd(node, (ConstraintNode)fChild, fIndex, fExtLanguageManager);
		}

		if (fChild instanceof TestSuiteNode) {
			return new OnTestCaseOperationAddToMethod(node, (TestCaseNode)fChild, fExtLanguageManager);
		}

		if (fChild instanceof TestCaseNode) {
			return new OnTestCaseOperationAddToMethod(
					node, (TestCaseNode)fChild, fIndex, Optional.empty(), fExtLanguageManager);
		}

		reportOperationNotSupportedException();
		return null;
	}

	@Override
	public Object visit(BasicParameterNode node) throws Exception {

		if (node.isGlobalParameter()) {

			if(fChild instanceof ChoiceNode){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fIndex, fValidate, fExtLanguageManager);
			}

			reportOperationNotSupportedException();
			return null;


		} else {

			if(fChild instanceof ChoiceNode){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fIndex, fValidate, fExtLanguageManager);
			}

			reportOperationNotSupportedException();
			return null;
		}
	}

	@Override
	public Object visit(CompositeParameterNode node) throws Exception {

		if (fChild instanceof AbstractParameterNode ) {

			return new OnParameterOperationAddToParent(
					node, (AbstractParameterNode)fChild, fIndex, fExtLanguageManager);
		}

		reportOperationNotSupportedException();
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
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fIndex, fValidate, fExtLanguageManager);
		}

		reportOperationNotSupportedException();
		return null;
	}

	private Object createOperationAddParameter(RootNode rootNode) {

		BasicParameterNode abstractParameterNode = (BasicParameterNode)fChild;

		BasicParameterNode globalParameter =
				((BasicParameterNode)abstractParameterNode).makeClone();
				// new BasicParameterNode(abstractParameterNode);

		return new GenericOperationAddParameter(rootNode, globalParameter, fIndex, true, fExtLanguageManager);
	}

	private Object createOperationAddClass(RootNode rootNode) {

		ClassNode classNode = (ClassNode)fChild;

		generateUniqueNameForClass(rootNode, classNode);

		return new OnClassOperationAddToRoot(rootNode, classNode, fIndex, fExtLanguageManager);
	}

	private void generateUniqueNameForClass(RootNode rootNode, ClassNode classNode) {

		String oldName = classNode.getName();
		String oldNameCore = StringHelper.removeFromNumericPostfix(oldName);
		String newName = RootNodeHelper.generateNewClassName(rootNode, oldNameCore);

		classNode.setName(newName);
	}

	private void reportOperationNotSupportedException() throws Exception {
		if (fValidate) {
			return;
		}

		ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

}
