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

import com.ecfeed.core.model.AbstractNodeHelper;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ParametersParentNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public class RemoveBasicParameterOperation extends CompositeOperation{

	public RemoveBasicParameterOperation(
			MethodNode target, AbstractParameterNode parameter, boolean validate, IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_PARAMETER, true, target, target, extLanguageManager);

		addOperation(new RemoveBasicParameterOperationPrivate(target, parameter, extLanguageManager));

		if (validate) {
			addOperation(new MethodOperationRemoveInconsistentChildren(target, extLanguageManager));
		}
	}

	public RemoveBasicParameterOperation(MethodNode target, AbstractParameterNode parameter, IExtLanguageManager extLanguageManager) {
		this(target, parameter, true, extLanguageManager);
	}

	public RemoveBasicParameterOperation(
			MethodNode target, 
			AbstractParameterNode parameter, 
			boolean validate, 
			boolean ignoreDuplicates, 
			IExtLanguageManager extLanguageManager){

		super(OperationNames.REMOVE_PARAMETER, true, target, target, extLanguageManager);

		addOperation(new RemoveBasicParameterOperationPrivate(target, parameter, ignoreDuplicates, extLanguageManager));
		if(validate){
			addOperation(new MethodOperationRemoveInconsistentChildren(target, extLanguageManager));
		}
	}

	private class RemoveBasicParameterOperationPrivate extends GenericOperationRemoveParameter{

		private List<TestCaseNode> fOriginalTestCases;

		public RemoveBasicParameterOperationPrivate(
				MethodNode target,
				AbstractParameterNode parameter,
				IExtLanguageManager extLanguageManager) {

			super(target, parameter, extLanguageManager);
			fOriginalTestCases = new ArrayList<>();
		}

		public RemoveBasicParameterOperationPrivate(
				MethodNode target, 
				AbstractParameterNode parameter, 
				boolean ignoreDuplicates, // TODO MO-RE remove
				IExtLanguageManager extLanguageManager) {

			this(target, parameter, extLanguageManager);
		}

		@Override
		public void execute() {

			MethodNode targetMethodNode = getTargetMethod();
			
			List<String> paramTypesInExtLanguage = 
					ParametersParentNodeHelper.getParameterTypes(targetMethodNode, getExtLanguageManager());
			
			int index = getParameter().getMyIndex();
			paramTypesInExtLanguage.remove(index);

			List<String> problems = new ArrayList<String>();

			if (!validateNewSignature(problems)) {

				String message = createErrorMessage(problems);

				ExceptionHelper.reportRuntimeException(message);
			}

			fOriginalTestCases.clear();

			for(TestCaseNode tcase : targetMethodNode.getTestCases()){
				fOriginalTestCases.add(tcase.getCopy(targetMethodNode));
			}

			targetMethodNode.removeAllTestCases();
			targetMethodNode.removeAllDeployedParameters();
			
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

		private MethodNode getTargetMethod(){
			return (MethodNode) getOwnNode();
		}

		private boolean validateNewSignature(List<String> problems) {

			ClassNode classNode = getTargetMethod().getClassNode();

			String methodNameInExtLanguage = AbstractNodeHelper.getName(getTargetMethod(), getExtLanguageManager());

			String errorMessage =
					ClassNodeHelper.verifyNewMethodSignatureIsValid(
						classNode, methodNameInExtLanguage, getExtLanguageManager());

			if (errorMessage != null) {
				problems.add(errorMessage);
				return false;
			}

			return true;
		}


		private class ReverseOperation extends AbstractReverseOperation {

			public ReverseOperation(IExtLanguageManager extLanguageManager) {
				super(RemoveBasicParameterOperationPrivate.this, extLanguageManager);
			}

			@Override
			public void execute() {

				setOneNodeToSelect(getTargetMethod());
				getTargetMethod().replaceTestCases(fOriginalTestCases);
				RemoveBasicParameterOperationPrivate.super.getReverseOperation().execute();
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new RemoveBasicParameterOperation(getTargetMethod(), (BasicParameterNode)getParameter(), getExtLanguageManager());
			}

		}

	}


}
