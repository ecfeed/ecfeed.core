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
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.ParametersParentNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class CompositeParameterOperationRemoveParameter extends CompositeOperation {
	
	
	private String fParameterName;

	public CompositeParameterOperationRemoveParameter(
			CompositeParameterNode compositeParameterNode, 
			AbstractParameterNode parameter, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_PARAMETER, true, compositeParameterNode, compositeParameterNode, extLanguageManager);

		fParameterName  = parameter.getName();
		
		addOperation(new RemoveCompositeParameterOperationPrivate(compositeParameterNode, parameter, extLanguageManager));

		addOperation(new CompositeParameterOperationRemoveInconsistentChildren(compositeParameterNode, extLanguageManager));
	}

	@Override
	public String toString() {
	
		return "Operation remove parameter:" + fParameterName;
	}
	
	private class RemoveCompositeParameterOperationPrivate extends GenericOperationRemoveParameter {

		private AbstractParameterNode fAbstractParameterNode;
		private List<TestCaseNode> fOriginalTestCases;
		private List<BasicParameterNode> fOriginalDeployedParameters;
		private MethodNode fMethodNode;

		public RemoveCompositeParameterOperationPrivate(
				CompositeParameterNode parentCompositeParameterNode, 
				AbstractParameterNode abstractParameterNode, 
				IExtLanguageManager extLanguageManager) {

			super(parentCompositeParameterNode, abstractParameterNode, extLanguageManager);

			fAbstractParameterNode = abstractParameterNode;
			fOriginalTestCases = new ArrayList<>();
			fOriginalDeployedParameters = new ArrayList<>();
		}

		@Override
		public void execute() {

			List<String> paramTypesInExtLanguage = 
					ParametersParentNodeHelper.getParameterTypes(getCompositeParameter(), getExtLanguageManager());

			int index = getParameter().getMyIndex();
			paramTypesInExtLanguage.remove(index);

			fMethodNode = MethodNodeHelper.findMethodNode(fAbstractParameterNode);

			if (fMethodNode != null) {
				fOriginalTestCases.clear();
				fOriginalTestCases.addAll(fMethodNode.getTestCases());
				fMethodNode.removeAllTestCases();

				fOriginalDeployedParameters.clear();
				fOriginalDeployedParameters.addAll(fMethodNode.getDeployedMethodParameters());
				fMethodNode.removeAllDeployedParameters();
			}

			super.execute();
		}

		@Override
		public IModelOperation getReverseOperation(){
			return new ReverseOperation(getExtLanguageManager());
		}

		private CompositeParameterNode getCompositeParameter(){
			return (CompositeParameterNode) getOwnNode();
		}

		private class ReverseOperation extends AbstractReverseOperation {

			public ReverseOperation(IExtLanguageManager extLanguageManager) {
				super(RemoveCompositeParameterOperationPrivate.this, extLanguageManager);
			}

			@Override
			public void execute() {

				setOneNodeToSelect(getCompositeParameter());

				if (fMethodNode != null) {
					fMethodNode.setTestCases(fOriginalTestCases);
					fMethodNode.setDeployedParameters(fOriginalDeployedParameters);
				}

				RemoveCompositeParameterOperationPrivate.super.getReverseOperation().execute();
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new CompositeParameterOperationRemoveParameter(getCompositeParameter(), (BasicParameterNode)getParameter(), getExtLanguageManager());
			}

		}

	}


}
