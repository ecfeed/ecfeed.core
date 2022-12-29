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

import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.AbstractReverseOperation;
import com.ecfeed.core.operations.BulkOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.MethodOperationMakeConsistent;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodParameterOperationSetLinked extends BulkOperation{

	public MethodParameterOperationSetLinked(
			BasicParameterNode target, boolean linked, IExtLanguageManager extLanguageManager) {

		super(OperationNames.SET_LINKED, true, target, target, extLanguageManager);

		addOperation(new SetLinkedOperation(target, linked, extLanguageManager));

		MethodNode methodNode = (MethodNode) target.getParent();
		addOperation(new MethodOperationMakeConsistent(methodNode, extLanguageManager)); 
	}

	public void addOperation(int index, IModelOperation operation){
		operations().add(index, operation);
	}

	private class SetLinkedOperation extends AbstractModelOperation {

		private BasicParameterNode fTarget;
		private boolean fLinked;
		private List<TestCaseNode> fOriginalTestCases;
		private List<ConstraintNode> fOriginalConstraints;

		public SetLinkedOperation(BasicParameterNode target, boolean linked, IExtLanguageManager extLanguageManager) {
			super(OperationNames.SET_LINKED, extLanguageManager);
			fTarget = target;
			fLinked = linked;
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fTarget);

			MethodNode method = (MethodNode) fTarget.getParent();
			String newType;
			if(fLinked){
				AbstractParameterNode linkToGlobalParameter = fTarget.getLinkToGlobalParameter();

				if(linkToGlobalParameter == null){
					ExceptionHelper.reportRuntimeException(ClassNodeHelper.LINK_NOT_SET_PROBLEM);
				}

				if (linkToGlobalParameter instanceof BasicParameterNode) {

					BasicParameterNode link = (BasicParameterNode)linkToGlobalParameter;
					newType = link.getType();
				} else {

					newType = null;
				}
			}
			else{
				newType = fTarget.getRealType();
			}

			if(method.checkDuplicate(fTarget.getMyIndex(), newType)){

				ExceptionHelper.reportRuntimeException(
						ClassNodeHelper.createMethodSignatureDuplicateMessage(
								method.getClassNode(), method, false, getExtLanguageManager()));
			}

			fOriginalTestCases = new ArrayList<>(method.getTestCases());
			fOriginalConstraints = new ArrayList<>(method.getConstraintNodes());

			method.removeTestCases();
			method.removeMentioningConstraints(fTarget);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ReverseSetLinkedOperation(getExtLanguageManager());
		}

		private class ReverseSetLinkedOperation extends AbstractReverseOperation{

			public ReverseSetLinkedOperation(IExtLanguageManager extLanguageManager) {
				super(SetLinkedOperation.this, extLanguageManager);
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
				return new SetLinkedOperation(fTarget, fLinked, getExtLanguageManager());
			}

		}

	}

}
