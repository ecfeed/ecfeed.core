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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.RegexHelper;

public class MethodOperationAddTestCase extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private TestCaseNode fTestCase;
	private int fIndex;
	private ITypeAdapterProvider fTypeAdapterProvider;

	public MethodOperationAddTestCase(
			MethodNode target, 
			TestCaseNode testCase, 
			ITypeAdapterProvider typeAdapterProvider, 
			int index,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_TEST_CASE, extLanguageManager);
		fMethodNode = target;
		fTestCase = testCase;
		fIndex = index;
		fTypeAdapterProvider = typeAdapterProvider;
	}

	public MethodOperationAddTestCase(
			MethodNode target, 
			TestCaseNode testCase, 
			ITypeAdapterProvider typeAdapterProvider, 
			IExtLanguageManager extLanguageManager) {

		this(target, testCase, typeAdapterProvider, -1, extLanguageManager);
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fMethodNode);

		if(fIndex == -1){
			fIndex = fMethodNode.getTestCases().size();
		}
		if(fTestCase.getName().matches(RegexHelper.REGEX_TEST_CASE_NODE_NAME) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.TEST_CASE_NOT_ALLOWED);
		}
		if(fTestCase.updateReferences(fMethodNode) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
		}

		//following must be done AFTER references are updated
		fTestCase.setParent(fMethodNode);

		for(ChoiceNode choice : fTestCase.getTestData()) {

			MethodParameterNode parameter = fTestCase.getMethodParameter(choice);

			if(parameter.isExpected()){
				
				String type = parameter.getType();
				
				ITypeAdapter<?> adapter = fTypeAdapterProvider.getAdapter(type);
				
				String newValue = 
						adapter.adapt(
								choice.getValueString(), 
								choice.isRandomizedValue(), 
								ERunMode.QUIET, 
								getExtLanguageManager());
				
				if(newValue == null){
					ExceptionHelper.reportRuntimeException(OperationMessages.TEST_CASE_DATA_INCOMPATIBLE_WITH_METHOD);
				}
				choice.setValueString(newValue);
			}
		}

		fMethodNode.addTestCase(fTestCase, fIndex);
		
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodOperationRemoveTestCase(fMethodNode, fTestCase, getExtLanguageManager());
	}

}
