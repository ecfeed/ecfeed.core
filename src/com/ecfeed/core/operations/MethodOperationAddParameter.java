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

import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodOperationAddParameter extends GenericOperationAddParameter {

	List<TestCaseNode> fRemovedTestCases;
	MethodNode fMethodNode;
	MethodParameterNode fMethodParameterNode;
	private int fNewIndex;

	public MethodOperationAddParameter(
			MethodNode methodNode, 
			MethodParameterNode methodParameterNode, 
			int index,
			IExtLanguageManager extLanguageManager) {

		super(methodNode, methodParameterNode, index, true, extLanguageManager);

		fRemovedTestCases = new ArrayList<TestCaseNode>(methodNode.getTestCases());
		fMethodNode = methodNode;
		fMethodParameterNode = methodParameterNode;
		fNewIndex = index != -1 ? index : methodNode.getParameters().size();
	}

	public MethodOperationAddParameter(MethodNode target, MethodParameterNode parameter, IExtLanguageManager extLanguageManager) {
		this(target, parameter, -1, extLanguageManager);
	}

	@Override
	public void execute() {

		IExtLanguageManager extLanguageManager = getExtLanguageManager();

		List<String> paremeterTypesInExtLanguage = MethodNodeHelper.getParameterTypes(fMethodNode, extLanguageManager);

		String newParameterType = AbstractParameterNodeHelper.getType(fMethodParameterNode, extLanguageManager);

		paremeterTypesInExtLanguage.add(fNewIndex, newParameterType);

		ClassNode parentClassNode = fMethodNode.getClassNode();

		if (parentClassNode != null) { 

			String methodNameInExtLanguage = MethodNodeHelper.getName(fMethodNode, extLanguageManager);

			MethodNode foundMethodNode = 
					ClassNodeHelper.findMethodByExtLanguage(
							parentClassNode, methodNameInExtLanguage, paremeterTypesInExtLanguage, extLanguageManager);

			if (foundMethodNode != null) {

				ExceptionHelper.reportRuntimeException(
						ClassNodeHelper.createMethodSignatureDuplicateMessage(
								parentClassNode, foundMethodNode, false, extLanguageManager));
			}
		}

		fMethodNode.removeTestCases();
		super.execute();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodReverseOperation(getExtLanguageManager());
	}

	private class MethodReverseOperation extends ReverseOperation{

		public MethodReverseOperation(IExtLanguageManager extLanguageManager) {
			super(fMethodNode, fMethodParameterNode, extLanguageManager);
		}

		@Override
		public void execute() {
			fMethodNode.replaceTestCases(fRemovedTestCases);
			super.execute();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodOperationAddParameter(fMethodNode, fMethodParameterNode, getExtLanguageManager());
		}

	}

}
