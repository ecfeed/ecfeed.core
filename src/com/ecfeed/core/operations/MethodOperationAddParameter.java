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

public class MethodOperationAddParameter extends GenericOperationAddParameter {

	List<TestCaseNode> fRemovedTestCases;
	MethodNode fTarget;
	MethodParameterNode fParameter;
	private int fNewIndex;

	private class MethodReverseOperation extends ReverseOperation{

		public MethodReverseOperation() {
			super(fTarget, fParameter);
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.replaceTestCases(fRemovedTestCases);
			super.execute();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodOperationAddParameter(fTarget, fParameter);
		}

	}

	public MethodOperationAddParameter(MethodNode target, MethodParameterNode parameter, int index) {
		super(target, parameter, index, true);
		fRemovedTestCases = new ArrayList<TestCaseNode>(target.getTestCases());
		fTarget = target;
		fParameter = parameter;
		fNewIndex = index != -1 ? index : target.getParameters().size();
	}

	public MethodOperationAddParameter(MethodNode target, MethodParameterNode parameter) {
		this(target, parameter, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		List<String> types = fTarget.getParameterTypes();
		types.add(fNewIndex, fParameter.getType());
		if(fTarget.getClassNode() != null && fTarget.getClassNode().getMethod(fTarget.getFullName(), types) != null){
			String methodName =  fTarget.getClassNode().getMethod(fTarget.getFullName(), types).getFullName();
			ModelOperationException.report(ClassNodeHelper.generateMethodSignatureDuplicateMessage(fTarget.getClassNode(), methodName));
		}
		fTarget.removeTestCases();
		super.execute();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodReverseOperation();
	}

}
