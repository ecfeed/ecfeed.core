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
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.StringHelper;

public class MethodOperationRemoveParameter extends BulkOperation{

	private class RemoveMethodParameterOperation extends GenericOperationRemoveParameter{

		private List<TestCaseNode> fOriginalTestCases;
		private boolean fIgnoreDuplicates;

		private class ReverseOperation extends AbstractReverseOperation {

			public ReverseOperation(ExtLanguage extLanguage) {
				super(RemoveMethodParameterOperation.this, extLanguage);
			}

			@Override
			public void execute() throws ModelOperationException {

				setOneNodeToSelect(getMethodTarget());
				getMethodTarget().replaceTestCases(fOriginalTestCases);
				RemoveMethodParameterOperation.super.getReverseOperation().execute();
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new MethodOperationRemoveParameter(getMethodTarget(), (MethodParameterNode)getParameter(), getExtLanguage());
			}

		}

		public RemoveMethodParameterOperation(MethodNode target, MethodParameterNode parameter, ExtLanguage extLanguage) {
			super(target, parameter, extLanguage);
			fOriginalTestCases = new ArrayList<>();
		}

		public RemoveMethodParameterOperation(
				MethodNode target, 
				MethodParameterNode parameter, 
				boolean ignoreDuplicates,
				ExtLanguage extLanguage) {

			this(target, parameter, extLanguage);

			fIgnoreDuplicates = ignoreDuplicates;
		}

		@Override
		public void execute() throws ModelOperationException {

			List<String> types = getMethodTarget().getParameterTypes();
			int index = getParameter().getMyIndex();
			types.remove(index);

			List<String> problems = new ArrayList<String>();

			if (!fIgnoreDuplicates && validateNewSignature(types, problems) == false) {

				String message = createErrorMessage(problems);

				ModelOperationException.report(message);
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

		private String createErrorMessage(List<String> problems) {

			String errorMessage = "";
			boolean firstTime = true;


			for (String problem : problems) {

				if (!firstTime) {

					errorMessage += " \n";
					firstTime = false;
				}

				errorMessage += problem;
			}

			if (StringHelper.isNullOrEmpty(errorMessage)) {
				errorMessage += "Unknown problem.";
			}

			return errorMessage;
		}

		@Override
		public IModelOperation getReverseOperation(){
			return new ReverseOperation(getExtLanguage());
		}

		private MethodNode getMethodTarget(){
			return (MethodNode) getOwnNode();
		}

		private boolean validateNewSignature(List<String> newTypes, List<String> problems) {

			return ClassNodeHelper.isNewMethodSignatureValidAndUnique(
					getMethodTarget().getClassNode(), getMethodTarget().getName(), newTypes, problems, getExtLanguage());
		}
	}

	public MethodOperationRemoveParameter(
			MethodNode target, MethodParameterNode parameter, boolean validate, ExtLanguage extLanguage) {

		super(OperationNames.REMOVE_METHOD_PARAMETER, true, target, target, extLanguage);

		addOperation(new RemoveMethodParameterOperation(target, parameter, extLanguage));

		if(validate){
			addOperation(new MethodOperationMakeConsistent(target, extLanguage));
		}
	}

	public MethodOperationRemoveParameter(MethodNode target, MethodParameterNode parameter, ExtLanguage extLanguage) {
		this(target, parameter, true, extLanguage);
	}

	public MethodOperationRemoveParameter(
			MethodNode target, 
			MethodParameterNode parameter, 
			boolean validate, 
			boolean ignoreDuplicates, 
			ExtLanguage extLanguage){

		super(OperationNames.REMOVE_METHOD_PARAMETER, true, target, target, extLanguage);

		addOperation(new RemoveMethodParameterOperation(target, parameter, ignoreDuplicates, extLanguage));
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target, extLanguage));
		}
	}

}
