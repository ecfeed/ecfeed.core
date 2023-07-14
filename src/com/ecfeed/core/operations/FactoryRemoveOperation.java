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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.operations.nodes.OnClassOperationRemove;
import com.ecfeed.core.operations.nodes.OnConstraintOperationRemove;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveFromClass;
import com.ecfeed.core.operations.nodes.OnParameterOperationRemove;
import com.ecfeed.core.operations.nodes.OnTestCaseOperationRemove;
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
		public List<IAbstractNode> getNodesToSelect() {
			return null;
		}

		@Override
		public void setNodesToSelect(List<IAbstractNode> nodes) {
		}


	}

	private static class RemoveOperationVisitor implements IModelVisitor{

		private boolean fValidate;
		IExtLanguageManager fExtLanguageManager;

		public RemoveOperationVisitor(boolean validate, IExtLanguageManager extLanguageManager) {
			fValidate = validate;
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return new UnsupportedModelOperation();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new OnClassOperationRemove(node.getRoot(), node, fExtLanguageManager);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new OnMethodOperationRemoveFromClass(node.getClassNode(), node, fExtLanguageManager);
		}

		@Override
		public Object visit(BasicParameterNode node) throws Exception {

			IAbstractNode parent = node.getParent();

			if ((parent instanceof RootNode) || (parent instanceof ClassNode)) {

				return new GenericOperationRemoveGlobalParameter(
						(IParametersParentNode)node.getParametersParent(), 
						node,
						fExtLanguageManager);
			}

			if (parent instanceof MethodNode) {

				return new OnParameterOperationRemove(
						(MethodNode)node.getParent(), node, fExtLanguageManager);
			}

			if (parent instanceof CompositeParameterNode) {

				return new OnParameterOperationRemove(
						(CompositeParameterNode)node.getParent(), node, fExtLanguageManager);
			}

			ExceptionHelper.reportRuntimeException("Unexpected parent for basic parameter.");
			return null;
		}

		@Override
		public Object visit(CompositeParameterNode node) throws Exception {

			IAbstractNode parent = node.getParent();

			if (parent instanceof MethodNode) {

				return new OnParameterOperationRemove(
						(MethodNode)node.getParent(), 
						(CompositeParameterNode)node, 
						fExtLanguageManager);
			} 

			if (parent instanceof CompositeParameterNode) {

				return new OnParameterOperationRemove(
						(CompositeParameterNode)node.getParent(), node, fExtLanguageManager);
			}

			ExceptionHelper.reportRuntimeException("Unexpected parent for composite parameter.");
			return null;
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			ExceptionHelper.reportRuntimeException("Unexpected test suite removing operation.");
			//return new OnTestSuiteOperationRemoveFromMethod(node.getMethod(), node, fExtLanguageManager);
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return new OnTestCaseOperationRemove(node.getMethod(), node, fExtLanguageManager);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {

			IAbstractNode abstractParent = node.getParent();

			// TODO MO-RE MERGE
			if (abstractParent instanceof MethodNode) {

				//return new MethodOperationRemoveConstraint(
				//		(MethodNode) abstractParent, node, fExtLanguageManager);

				return new OnConstraintOperationRemove(
						(MethodNode) abstractParent, node, fExtLanguageManager);
			}

			if (abstractParent instanceof CompositeParameterNode) {
				//				return new CompositeParameterOperationRemoveConstraint(
				//						(CompositeParameterNode) abstractParent, node, fExtLanguageManager);

				return new OnConstraintOperationRemove(
						(CompositeParameterNode) abstractParent, node, fExtLanguageManager);

			}

			ExceptionHelper.reportRuntimeException("Invalid parent of constraint.");
			return null;
		}

		@Override
		public Object visit(ChoiceNode choiceNode) throws Exception {

			IAbstractNode abstractParent = choiceNode.getParent();

			if (!(abstractParent instanceof IChoicesParentNode)) {
				ExceptionHelper.reportRuntimeException("Invalid type of parent.");
			}

			IChoicesParentNode choicesParentNode = (IChoicesParentNode)abstractParent; 

			return new GenericOperationRemoveChoice(choicesParentNode, choiceNode, fValidate, fExtLanguageManager);
		}

	}

	public static IModelOperation getRemoveOperation(
			IAbstractNode node, 
			boolean validate, 
			IExtLanguageManager extLanguageManager){

		try {
			RemoveOperationVisitor removeOperationVisitor = 
					new RemoveOperationVisitor(validate, extLanguageManager);

			return (IModelOperation)node.accept(removeOperationVisitor);
		} catch (Exception e) {

			return new UnsupportedModelOperation();
		}
	}
}
