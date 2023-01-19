/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.link;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.AbstractReverseOperation;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.MethodOperationRemoveInconsistentChildren;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class HostMethodOperationPrepareParameterChange extends CompositeOperation {

	public HostMethodOperationPrepareParameterChange(
			BasicParameterNode target,
			String newType,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.SET_LINKED, true, target, target, extLanguageManager);

		IParametersParentNode parent = target.getParent();
		
		if (parent instanceof MethodNode) {
			
			addOperation(new OperationPrepareMethodForParameterTypeChange(target, newType, extLanguageManager));

			MethodNode methodNode = (MethodNode) parent;
			addOperation(new MethodOperationRemoveInconsistentChildren(methodNode, extLanguageManager));
		}
	}

	public void addOperation(int index, IModelOperation operation){
		
		if (operation == null) {
			return;
		}
		
		operations().add(index, operation);
	}

	private class OperationPrepareMethodForParameterTypeChange extends AbstractModelOperation {

		private BasicParameterNode fTarget;
		private String fNewType;
		private List<TestCaseNode> fOriginalTestCases;
		private List<ConstraintNode> fOriginalConstraints;

		public OperationPrepareMethodForParameterTypeChange(
				BasicParameterNode target,
				String newType,
				IExtLanguageManager extLanguageManager) {
			
			super(OperationNames.SET_LINKED, extLanguageManager);
			fTarget = target;
			fNewType = newType;
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fTarget);

			MethodNode method = (MethodNode) fTarget.getParent();
			
			if(method.checkDuplicate()){

				ExceptionHelper.reportRuntimeException(
						ClassNodeHelper.createMethodNameDuplicateMessage(
								method.getClassNode(), method, false, getExtLanguageManager()));
			}

			fOriginalTestCases = new ArrayList<>(method.getTestCases());
			fOriginalConstraints = new ArrayList<>(method.getConstraintNodes());

			method.removeAllTestCases();
			method.removeMentioningConstraints(fTarget);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ReverseSetLinkedOperation(getExtLanguageManager());
		}

		private class ReverseSetLinkedOperation extends AbstractReverseOperation{

			public ReverseSetLinkedOperation(IExtLanguageManager extLanguageManager) {
				super(OperationPrepareMethodForParameterTypeChange.this, extLanguageManager);
			}

			@Override
			public void execute() {

				setOneNodeToSelect(fTarget);
				MethodNode methodNode = (MethodNode) fTarget.getParent();

				methodNode.replaceTestCases(fOriginalTestCases);
				methodNode.replaceConstraints(fOriginalConstraints);
				reparentConstraints(methodNode);
			}

			private void reparentConstraints(MethodNode methodNode) {

				for (ConstraintNode constraint : fOriginalConstraints) {
					constraint.setParent(methodNode);
				}
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new OperationPrepareMethodForParameterTypeChange(fTarget, fNewType, getExtLanguageManager());
			}

		}

	}

}
