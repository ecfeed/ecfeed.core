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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodParameterOperationSetLinked extends BulkOperation{

	private class SetLinkedOperation extends AbstractModelOperation {

		private MethodParameterNode fTarget;
		private boolean fLinked;
		private List<TestCaseNode> fOriginalTestCases;
		private List<ConstraintNode> fOriginalConstraints;

		private class ReverseSetLinkedOperation extends AbstractReverseOperation{

			public ReverseSetLinkedOperation(ExtLanguage extLanguage) {
				super(SetLinkedOperation.this, extLanguage);
			}

			@Override
			public void execute() throws ModelOperationException {

				setOneNodeToSelect(fTarget);
				MethodNode methodNode = fTarget.getMethod();

				methodNode.replaceTestCases(fOriginalTestCases);
				methodNode.replaceConstraints(fOriginalConstraints);
				reparentConstraints(methodNode);
				fTarget.setLinked(!fLinked);
			}

			private void reparentConstraints(MethodNode methodNode) {

				for (ConstraintNode constraint : fOriginalConstraints) {
					constraint.setParent(methodNode);
				}
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new SetLinkedOperation(fTarget, fLinked, getExtLanguage());
			}

		}

		public SetLinkedOperation(MethodParameterNode target, boolean linked, ExtLanguage extLanguage) {
			super(OperationNames.SET_LINKED, extLanguage);
			fTarget = target;
			fLinked = linked;
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fTarget);

			// TODO SIMPLE-VIEW  check - use method from helper instead of getMethod
			MethodNode method = fTarget.getMethod();
			String newType;
			if(fLinked){
				if(fTarget.getLink() == null){
					ModelOperationException.report(ClassNodeHelper.LINK_NOT_SET_PROBLEM);
				}
				newType = fTarget.getLink().getType();
			}
			else{
				newType = fTarget.getRealType();
			}

			if(method.checkDuplicate(fTarget.getMyIndex(), newType)){

				ModelOperationException.report(
						ClassNodeHelper.generateMethodSignatureDuplicateMessage(
								method.getClassNode(), method, getExtLanguage()));
			}

			fTarget.setLinked(fLinked);
			fOriginalTestCases = new ArrayList<>(method.getTestCases());
			fOriginalConstraints = new ArrayList<>(method.getConstraintNodes());

			method.removeTestCases();
			method.removeConstraintsWithParameter(fTarget);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ReverseSetLinkedOperation(getExtLanguage());
		}

	}

	public MethodParameterOperationSetLinked(MethodParameterNode target, boolean linked, ExtLanguage extLanguage) {
		super(OperationNames.SET_LINKED, true, target, target, extLanguage);
		addOperation(new SetLinkedOperation(target, linked, extLanguage));
		addOperation(new MethodOperationMakeConsistent(target.getMethod(), extLanguage)); 
	}

	public void addOperation(int index, IModelOperation operation){
		operations().add(index, operation);
	}

}
