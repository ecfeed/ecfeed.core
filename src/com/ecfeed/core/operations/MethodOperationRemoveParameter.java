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
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public class MethodOperationRemoveParameter extends BulkOperation{

	public MethodOperationRemoveParameter(
			MethodNode target, MethodParameterNode parameter, boolean validate, IExtLanguageManager extLanguage) {

		super(OperationNames.REMOVE_METHOD_PARAMETER, true, target, target, extLanguage);

		addOperation(new RemoveMethodParameterOperation(target, parameter, extLanguage));

		if (validate) {
			addOperation(new MethodOperationMakeConsistent(target, extLanguage));
		}
	}

	public MethodOperationRemoveParameter(MethodNode target, MethodParameterNode parameter, IExtLanguageManager extLanguage) {
		this(target, parameter, true, extLanguage);
	}

	public MethodOperationRemoveParameter(
			MethodNode target, 
			MethodParameterNode parameter, 
			boolean validate, 
			boolean ignoreDuplicates, 
			IExtLanguageManager extLanguage){

		super(OperationNames.REMOVE_METHOD_PARAMETER, true, target, target, extLanguage);

		addOperation(new RemoveMethodParameterOperation(target, parameter, ignoreDuplicates, extLanguage));
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target, extLanguage));
		}
	}

	private class RemoveMethodParameterOperation extends GenericOperationRemoveParameter{

		private List<TestCaseNode> fOriginalTestCases;
		private boolean fIgnoreDuplicates;

		public RemoveMethodParameterOperation(MethodNode target, MethodParameterNode parameter, IExtLanguageManager extLanguage) {
			super(target, parameter, extLanguage);
			fOriginalTestCases = new ArrayList<>();
		}

		public RemoveMethodParameterOperation(
				MethodNode target, 
				MethodParameterNode parameter, 
				boolean ignoreDuplicates,
				IExtLanguageManager extLanguage) {

			this(target, parameter, extLanguage);

			fIgnoreDuplicates = ignoreDuplicates;
		}

		@Override
		public void execute() throws ModelOperationException {

			List<String> paramTypesInExtLanguage = MethodNodeHelper.getMethodParameterTypes(getMethodTarget(), getExtLanguageManager());
			int index = getParameter().getMyIndex();
			paramTypesInExtLanguage.remove(index);

			List<String> problems = new ArrayList<String>();

			if (!fIgnoreDuplicates && validateNewSignature(paramTypesInExtLanguage, problems) == false) {

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
			return new ReverseOperation(getExtLanguageManager());
		}

		private MethodNode getMethodTarget(){
			return (MethodNode) getOwnNode();
		}

		private boolean validateNewSignature(List<String> newTypesInExtLanguage, List<String> problems) {

			ClassNode classNode = getMethodTarget().getClassNode();

			String methodNameInExtLanguage = MethodNodeHelper.getName(getMethodTarget(), getExtLanguageManager());

			String errorMessage =
					ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						classNode, methodNameInExtLanguage, newTypesInExtLanguage, getExtLanguageManager());

			if (errorMessage != null) {
				problems.add(errorMessage);
				return false;
			}

			return true;
		}


		private class ReverseOperation extends AbstractReverseOperation {

			public ReverseOperation(IExtLanguageManager extLanguage) {
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
				return new MethodOperationRemoveParameter(getMethodTarget(), (MethodParameterNode)getParameter(), getExtLanguageManager());
			}

		}

	}


}
