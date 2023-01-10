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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ParametersParentNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public class CompositeParameterOperationRemoveParameter extends CompositeOperation{

	public CompositeParameterOperationRemoveParameter(
			CompositeParameterNode target, 
			AbstractParameterNode parameter, 
			boolean validate, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_PARAMETER, true, target, target, extLanguageManager);

		addOperation(new RemoveCompositeParameterOperation(target, parameter, extLanguageManager));

		// TODO MO-RE
		//		if (validate) {
		//			addOperation(new MethodOperationMakeConsistent(target, extLanguageManager));
		//		}
	}

	public CompositeParameterOperationRemoveParameter(CompositeParameterNode target, AbstractParameterNode parameter, IExtLanguageManager extLanguageManager) {
		this(target, parameter, true, extLanguageManager);
	}

	public CompositeParameterOperationRemoveParameter(
			CompositeParameterNode target, 
			AbstractParameterNode parameter, 
			boolean validate, 
			boolean ignoreDuplicates, 
			IExtLanguageManager extLanguageManager){

		super(OperationNames.REMOVE_PARAMETER, true, target, target, extLanguageManager);

		addOperation(new RemoveCompositeParameterOperation(target, parameter, ignoreDuplicates, extLanguageManager));

		// TODO MO-RE		
		//		if(validate){
		//			addOperation(new MethodOperationMakeConsistent(target, extLanguageManager));
		//		}
	}

	private class RemoveCompositeParameterOperation extends GenericOperationRemoveParameter{

		private List<TestCaseNode> fOriginalTestCases;
		private boolean fIgnoreDuplicates;

		public RemoveCompositeParameterOperation(CompositeParameterNode target, AbstractParameterNode parameter, IExtLanguageManager extLanguageManager) {
			super(target, parameter, extLanguageManager);
			fOriginalTestCases = new ArrayList<>();
		}

		public RemoveCompositeParameterOperation(
				CompositeParameterNode target, 
				AbstractParameterNode parameter, 
				boolean ignoreDuplicates,
				IExtLanguageManager extLanguageManager) {

			this(target, parameter, extLanguageManager);

			fIgnoreDuplicates = ignoreDuplicates;
		}

		@Override
		public void execute() {

			List<String> paramTypesInExtLanguage = ParametersParentNodeHelper.getParameterTypes(getMethodTarget(), getExtLanguageManager());
			int index = getParameter().getMyIndex();
			paramTypesInExtLanguage.remove(index);

			List<String> problems = new ArrayList<String>();

			if (!fIgnoreDuplicates && validateNewSignature(paramTypesInExtLanguage, problems) == false) {

				String message = createErrorMessage(problems);

				ExceptionHelper.reportRuntimeException(message);
			}

			fOriginalTestCases.clear();

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

		private CompositeParameterNode getMethodTarget(){
			return (CompositeParameterNode) getOwnNode();
		}

		private boolean validateNewSignature(List<String> newTypesInExtLanguage, List<String> problems) {

			// TODO MO-RE
			//			ClassNode classNode = getMethodTarget().getClassNode();
			//
			//			String methodNameInExtLanguage = AbstractNodeHelper.getName(getMethodTarget(), getExtLanguageManager());
			//
			//			String errorMessage =
			//					ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
			//							classNode, methodNameInExtLanguage, newTypesInExtLanguage, getExtLanguageManager());
			//
			//			if (errorMessage != null) {
			//				problems.add(errorMessage);
			//				return false;
			//			}

			return true;
		}


		private class ReverseOperation extends AbstractReverseOperation {

			public ReverseOperation(IExtLanguageManager extLanguageManager) {
				super(RemoveCompositeParameterOperation.this, extLanguageManager);
			}

			@Override
			public void execute() {

				setOneNodeToSelect(getMethodTarget());
				//				getMethodTarget().replaceTestCases(fOriginalTestCases);
				RemoveCompositeParameterOperation.super.getReverseOperation().execute();
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new CompositeParameterOperationRemoveParameter(getMethodTarget(), (BasicParameterNode)getParameter(), getExtLanguageManager());
			}

		}

	}


}
