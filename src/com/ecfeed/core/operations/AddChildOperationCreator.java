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
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.operations.nodes.OnClassOperationAddToRoot;
import com.ecfeed.core.operations.nodes.OnConstraintOperationAdd;
import com.ecfeed.core.operations.nodes.OnMethodOperationAddToClass;
import com.ecfeed.core.operations.nodes.OnParameterOperationAddToParent;
import com.ecfeed.core.operations.nodes.OnTestCaseOperationAddToMethod;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class AddChildOperationCreator implements IModelVisitor {

	private IAbstractNode fChild;
	private int fIndex;
	private boolean fValidate;
	private IExtLanguageManager fExtLanguageManager;

	public AddChildOperationCreator(
			IAbstractNode child, 
			int index, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fChild = child;
		fIndex = index;
		fValidate = validate;
		fExtLanguageManager = extLanguageManager;
	}

	public AddChildOperationCreator(
			IAbstractNode child, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		this(child, -1, validate, extLanguageManager);
	}

	@Override
	public Object visit(RootNode rootNode) throws Exception {

		if (fChild instanceof ClassNode) {

			return createOperationAddClass(rootNode);
		} 
		
		if (fChild instanceof AbstractParameterNode) {

			return createOperationAddParameterToRootNode(rootNode);
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
					((BasicParameterNode)fChild).makeClone(Optional.empty());
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

		if (fChild instanceof ConstraintNode ) {

			return new OnConstraintOperationAdd
					(node, (ConstraintNode)fChild, fIndex, fExtLanguageManager);
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

	private Object createOperationAddParameterToRootNode(RootNode rootNode) {

		AbstractParameterNode abstractParameterNode = (AbstractParameterNode)fChild;

		IAbstractNode globalParameter =
				((AbstractParameterNode)abstractParameterNode).makeClone(Optional.empty());

		return new GenericOperationAddParameter(
				rootNode, (AbstractParameterNode) globalParameter, fIndex, true, fExtLanguageManager);
	}

	private Object createOperationAddClass(RootNode rootNode) {

		ClassNode classNode = (ClassNode)fChild;

		return new OnClassOperationAddToRoot(rootNode, classNode, fIndex, true, fExtLanguageManager);
	}

	private void reportOperationNotSupportedException() throws Exception {

		if (fValidate) {
			return;
		}

		ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
	}

}
