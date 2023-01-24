/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.operations.GenericOperationAddParameter;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnParameterOperationAddToComposite extends GenericOperationAddParameter {

//	List<TestCaseNode> fRemovedTestCases;
	CompositeParameterNode fCompositeParameterNode;
	AbstractParameterNode fAbstractParameterNode;
//	private int fNewIndex;

	public OnParameterOperationAddToComposite(
			CompositeParameterNode compositeParameterNode, 
			AbstractParameterNode abstractParameterNode, 
			int index,
			IExtLanguageManager extLanguageManager) {

		super(compositeParameterNode, abstractParameterNode, index, true, extLanguageManager);

		fCompositeParameterNode = compositeParameterNode;
		fAbstractParameterNode = abstractParameterNode;
//		fNewIndex = index != -1 ? index : compositeParameterNode.getParameters().size();
	}

	public OnParameterOperationAddToComposite(
			CompositeParameterNode target, 
			AbstractParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {
		
		this(target, parameter, -1, extLanguageManager);
	}

	@Override
	public void execute() {

//		IExtLanguageManager extLanguageManager = getExtLanguageManager();
//
//		List<String> parameterTypesInExtLanguage = MethodNodeHelper.getParameterTypes(fCompositeParameterNode, extLanguageManager);
//
//		String newParameterType = AbstractParameterNodeHelper.getType(fMethodParameterNode, extLanguageManager);
//
//		parameterTypesInExtLanguage.add(fNewIndex, newParameterType);
//
//		ClassNode parentClassNode = fCompositeParameterNode.getClassNode();
//
//		if (parentClassNode != null) { 
//
//			String methodNameInExtLanguage = MethodNodeHelper.getName(fCompositeParameterNode, extLanguageManager);
//
//			MethodNode foundMethodNode = 
//					ClassNodeHelper.findMethodByExtLanguage(
//							parentClassNode, methodNameInExtLanguage, parameterTypesInExtLanguage, extLanguageManager);
//
//			if (foundMethodNode != null) {
//
//				ExceptionHelper.reportRuntimeException(
//						ClassNodeHelper.createMethodSignatureDuplicateMessage(
//								parentClassNode, foundMethodNode, false, extLanguageManager));
//			}
//		}
//
//		fCompositeParameterNode.removeTestCases();
		super.execute();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodReverseOperation(getExtLanguageManager());
	}

	private class MethodReverseOperation extends ReverseOperation{

		public MethodReverseOperation(IExtLanguageManager extLanguageManager) {
			super(fCompositeParameterNode, fAbstractParameterNode, extLanguageManager);
		}

		@Override
		public void execute() {
//			fCompositeParameterNode.replaceTestCases(fRemovedTestCases);
			super.execute();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OnParameterOperationAddToComposite(fCompositeParameterNode, fAbstractParameterNode, getExtLanguageManager());
		}

	}

}
