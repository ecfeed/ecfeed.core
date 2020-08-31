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
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodOperationAddParameter extends GenericOperationAddParameter {

	List<TestCaseNode> fRemovedTestCases;
	MethodNode fMethodNode;
	MethodParameterNode fMethodParameterNode;
	private int fNewIndex;

	public MethodOperationAddParameter(
			MethodNode methodNode, 
			MethodParameterNode methodParameterNode, 
			int index,
			ExtLanguage viewMode) {
		
		super(methodNode, methodParameterNode, index, true, viewMode);
		
		fRemovedTestCases = new ArrayList<TestCaseNode>(methodNode.getTestCases());
		fMethodNode = methodNode;
		fMethodParameterNode = methodParameterNode;
		fNewIndex = index != -1 ? index : methodNode.getParameters().size();
	}

	public MethodOperationAddParameter(MethodNode target, MethodParameterNode parameter, ExtLanguage viewMode) {
		this(target, parameter, -1, viewMode);
	}

	@Override
	public void execute() throws ModelOperationException {
		
		ExtLanguage viewMode = getViewMode();
		
		List<String> types = MethodNodeHelper.getMethodParameterTypes(fMethodNode, viewMode);
		
		String parameterType = AbstractParameterNodeHelper.createTypeLabel(fMethodParameterNode.getType(), viewMode);
		
		types.add(fNewIndex, parameterType);
		
		ClassNode parentClassNode = fMethodNode.getClassNode();
		
		if (parentClassNode != null) { 
				
			MethodNode foundMethodNode = ClassNodeHelper.findMethod(parentClassNode, fMethodNode.getName(), types, viewMode);
			
			if (foundMethodNode != null) {
				
				ModelOperationException.report(
						ClassNodeHelper.generateMethodSignatureDuplicateMessage(
								parentClassNode, foundMethodNode, viewMode));
			}
		}
		
		fMethodNode.removeTestCases();
		super.execute();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodReverseOperation(getViewMode());
	}
	
	private class MethodReverseOperation extends ReverseOperation{

		public MethodReverseOperation(ExtLanguage viewMode) {
			super(fMethodNode, fMethodParameterNode, viewMode);
		}

		@Override
		public void execute() throws ModelOperationException {
			fMethodNode.replaceTestCases(fRemovedTestCases);
			super.execute();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodOperationAddParameter(fMethodNode, fMethodParameterNode, getViewMode());
		}

	}

}
