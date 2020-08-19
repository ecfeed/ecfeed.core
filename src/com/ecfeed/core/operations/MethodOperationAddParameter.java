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

public class MethodOperationAddParameter extends GenericOperationAddParameter {

	List<TestCaseNode> fRemovedTestCases;
	MethodNode fMethodNode;
	MethodParameterNode fMethodParameterNode;
	private int fNewIndex;

	public MethodOperationAddParameter(
			MethodNode methodNode, 
			MethodParameterNode methodParameterNode, 
			int index,
			ViewMode viewMode) {
		
		super(methodNode, methodParameterNode, index, true, viewMode);
		
		fRemovedTestCases = new ArrayList<TestCaseNode>(methodNode.getTestCases());
		fMethodNode = methodNode;
		fMethodParameterNode = methodParameterNode;
		fNewIndex = index != -1 ? index : methodNode.getParameters().size();
	}

	public MethodOperationAddParameter(MethodNode target, MethodParameterNode parameter, ViewMode viewMode) {
		this(target, parameter, -1, viewMode);
	}

	@Override
	public void execute() throws ModelOperationException {
		
		List<String> types = fMethodNode.getParameterTypes();
		types.add(fNewIndex, fMethodParameterNode.getType());
		
		ClassNode parentClassNode = fMethodNode.getClassNode();
		
		if (parentClassNode != null) { 
				
			MethodNode existingMethodNode = parentClassNode.getMethod(fMethodNode.getName(), types);
			
			if (existingMethodNode != null) {
				ModelOperationException.report(
						ClassNodeHelper.generateMethodSignatureDuplicateMessage(
								parentClassNode, existingMethodNode.getName()));
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

		public MethodReverseOperation(ViewMode viewMode) {
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
