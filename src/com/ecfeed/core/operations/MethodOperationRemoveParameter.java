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
import com.ecfeed.core.utils.SourceViewMode;

public class MethodOperationRemoveParameter extends BulkOperation{

	private class RemoveMethodParameterOperation extends GenericOperationRemoveParameter{

		private List<TestCaseNode> fOriginalTestCases;
		private boolean fIgnoreDuplicates;
		private SourceViewMode fModelCompatibility;

		private class ReverseOperation extends AbstractReverseOperation {
			public ReverseOperation() {
				super(RemoveMethodParameterOperation.this);
			}

			@Override
			public void execute() throws ModelOperationException {

				setOneNodeToSelect(getMethodTarget());
				getMethodTarget().replaceTestCases(fOriginalTestCases);
				RemoveMethodParameterOperation.super.getReverseOperation().execute();
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new MethodOperationRemoveParameter(getMethodTarget(), (MethodParameterNode)getParameter(), fModelCompatibility);
			}

		}

		public RemoveMethodParameterOperation(MethodNode target, MethodParameterNode parameter, SourceViewMode modelCompatibility) {
			super(target, parameter);
			fOriginalTestCases = new ArrayList<>();
			fModelCompatibility = modelCompatibility;
		}

		public RemoveMethodParameterOperation(
				MethodNode target, 
				MethodParameterNode parameter, 
				boolean ignoreDuplicates, 
				SourceViewMode modelCompatibility) {
			
			this(target, parameter, modelCompatibility);
			
			fIgnoreDuplicates = ignoreDuplicates;
		}

		@Override
		public void execute() throws ModelOperationException{
			if(!fIgnoreDuplicates && validateNewSignature() == false){
				String methodName = getOwnNode().getFullName();
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
			return new ReverseOperation();
		}

		private MethodNode getMethodTarget(){
			return (MethodNode) getOwnNode();
		}

		private boolean validateNewSignature() {
			List<String> types = getMethodTarget().getParameterTypes();
			int index = getParameter().getMyIndex();
			types.remove(index);
			
			return ClassNodeHelper.isNewMethodSignatureValid(
					getMethodTarget().getClassNode(), getMethodTarget().getFullName(), types, fModelCompatibility);
		}
	}

	public MethodOperationRemoveParameter(
			MethodNode target, MethodParameterNode parameter, boolean validate, SourceViewMode modelCompatibility) {
		
		super(OperationNames.REMOVE_METHOD_PARAMETER, true, target, target);
		
		addOperation(new RemoveMethodParameterOperation(target, parameter, modelCompatibility));
		
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target));
		}
	}
	
	public MethodOperationRemoveParameter(MethodNode target, MethodParameterNode parameter, SourceViewMode modelCompatibility) {
		this(target, parameter, true, modelCompatibility);
	}

	public MethodOperationRemoveParameter(
			MethodNode target, MethodParameterNode parameter, boolean validate, boolean ignoreDuplicates, SourceViewMode modelCompatibility){
		
		super(OperationNames.REMOVE_METHOD_PARAMETER, true, target, target);
		
		addOperation(new RemoveMethodParameterOperation(target, parameter, ignoreDuplicates, modelCompatibility));
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target));
		}
	}

}
