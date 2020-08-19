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

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ViewMode;

public class MethodOperationRemoveParameter extends BulkOperation{

	private class RemoveMethodParameterOperation extends GenericOperationRemoveParameter{

		private List<TestCaseNode> fOriginalTestCases;
		private boolean fIgnoreDuplicates;

		private class ReverseOperation extends AbstractReverseOperation {
			
			public ReverseOperation(ViewMode viewMode) {
				super(RemoveMethodParameterOperation.this, viewMode);
			}

			@Override
			public void execute() throws ModelOperationException {

				setOneNodeToSelect(getMethodTarget());
				getMethodTarget().replaceTestCases(fOriginalTestCases);
				RemoveMethodParameterOperation.super.getReverseOperation().execute();
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new MethodOperationRemoveParameter(getMethodTarget(), (MethodParameterNode)getParameter(), getViewMode());
			}

		}

		public RemoveMethodParameterOperation(MethodNode target, MethodParameterNode parameter, ViewMode viewMode) {
			super(target, parameter, viewMode);
			fOriginalTestCases = new ArrayList<>();
		}

		public RemoveMethodParameterOperation(
				MethodNode target, 
				MethodParameterNode parameter, 
				boolean ignoreDuplicates,
				ViewMode viewMode) {
			
			this(target, parameter, viewMode);
			
			fIgnoreDuplicates = ignoreDuplicates;
		}

		@Override
		public void execute() throws ModelOperationException{
			if(!fIgnoreDuplicates && validateNewSignature() == false){
				String methodName = getOwnNode().getName();
				ModelOperationException.report(ClassNodeHelper.generateMethodSignatureDuplicateMessage((ClassNode) getOwnNode().getParent(), methodName));
			}
			fOriginalTestCases.clear();
			for(TestCaseNode tcase : getMethodTarget().getTestCases()){
				fOriginalTestCases.add(tcase.getCopy(getMethodTarget()));
			}
			for(TestCaseNode tc : getMethodTarget().getTestCases()){
				tc.getTestData().remove(getParameter().getMyIndex());
			}
			super.execute();
		}

		@Override
		public IModelOperation getReverseOperation(){
			return new ReverseOperation(getViewMode());
		}

		private MethodNode getMethodTarget(){
			return (MethodNode) getOwnNode();
		}

		private boolean validateNewSignature() {
			List<String> types = getMethodTarget().getParameterTypes();
			int index = getParameter().getMyIndex();
			types.remove(index);
			
			return ClassNodeHelper.isNewMethodSignatureValid(
					getMethodTarget().getClassNode(), getMethodTarget().getName(), types);
		}
	}

	public MethodOperationRemoveParameter(
			MethodNode target, MethodParameterNode parameter, boolean validate, ViewMode viewMode) {
		
		super(OperationNames.REMOVE_METHOD_PARAMETER, true, target, target, viewMode);
		
		addOperation(new RemoveMethodParameterOperation(target, parameter, viewMode));
		
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target, viewMode));
		}
	}
	
	public MethodOperationRemoveParameter(MethodNode target, MethodParameterNode parameter, ViewMode viewMode) {
		this(target, parameter, true, viewMode);
	}

	public MethodOperationRemoveParameter(
			MethodNode target, 
			MethodParameterNode parameter, 
			boolean validate, 
			boolean ignoreDuplicates, 
			ViewMode viewMode){
		
		super(OperationNames.REMOVE_METHOD_PARAMETER, true, target, target, viewMode);
		
		addOperation(new RemoveMethodParameterOperation(target, parameter, ignoreDuplicates, viewMode));
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target, viewMode));
		}
	}

}
