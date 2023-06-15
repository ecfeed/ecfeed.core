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

import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ParametersParentNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.MethodsWithTestCasesContainer;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericOperationAddParameter extends AbstractModelOperation {

	private IParametersParentNode fParametersParentNode;
	private AbstractParameterNode fAbstractParameterNode;
	private int fNewIndex;
	private boolean fGenerateUniqueName;
	private MethodsWithTestCasesContainer fMethodsWithTestCasesContainer;

	public GenericOperationAddParameter(
			IParametersParentNode target, 
			AbstractParameterNode parameter, 
			int index, 
			boolean generateUniqueName,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.ADD_PARAMETER, extLanguageManager);
		fParametersParentNode = target;
		fAbstractParameterNode = parameter;
		fNewIndex = (index == -1)? target.getParameters().size() : index;
		fGenerateUniqueName = generateUniqueName;
		fMethodsWithTestCasesContainer = new MethodsWithTestCasesContainer();
	}

	public GenericOperationAddParameter(
			IParametersParentNode target, 
			BasicParameterNode parameter, 
			boolean generateUniqueName,
			IExtLanguageManager extLanguageManager) {
		
		this(target, parameter, -1, generateUniqueName, extLanguageManager);
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fParametersParentNode);

		if (fGenerateUniqueName) {
			generateUniqueParameterName(fAbstractParameterNode);
		}
		
		String parameterName = fAbstractParameterNode.getName();

		if(fNewIndex < 0){
			ExceptionHelper.reportRuntimeException(OperationMessages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fNewIndex > fParametersParentNode.getParameters().size()){
			ExceptionHelper.reportRuntimeException(OperationMessages.TOO_HIGH_INDEX_PROBLEM);
		}
		if(fParametersParentNode.findParameter(parameterName) != null){
			ExceptionHelper.reportRuntimeException(OperationMessages.PARAMETER_WITH_THIS_NAME_ALREADY_EXISTS);
		}

		fMethodsWithTestCasesContainer = saveMentioningMethodsAndTestCases(fParametersParentNode);
		
		deleteTestCasesForMentioningMethods(fMethodsWithTestCasesContainer.getMethods());
		
		fParametersParentNode.addParameter(fAbstractParameterNode, fNewIndex);
		markModelUpdated();
	}

	private void deleteTestCasesForMentioningMethods(List<MethodNode> methodNodes) {
		
		for (MethodNode methodNode : methodNodes) {
			
			methodNode.removeAllTestCases();
		}
	}

	private static MethodsWithTestCasesContainer saveMentioningMethodsAndTestCases(
			IParametersParentNode parametersParentNode) {
		
		MethodsWithTestCasesContainer methodsWithTestCasesContainer = 
				new MethodsWithTestCasesContainer();
		
		List<MethodNode> methodNodes = 
				ParametersParentNodeHelper.getMentioningMethodNodes(parametersParentNode);
		
		for (MethodNode methodNode : methodNodes) {
			
			List<TestCaseNode> testCaseNodes = methodNode.getTestCases();
			methodsWithTestCasesContainer.putMethodWithTestCases(methodNode, testCaseNodes);
		}
		
		return methodsWithTestCasesContainer;
	}

	private void generateUniqueParameterName(AbstractParameterNode abstractParameterNode) {

		String newName = fParametersParentNode.generateNewParameterName(abstractParameterNode.getName());
		abstractParameterNode.setName(newName);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(fParametersParentNode, fAbstractParameterNode, getExtLanguageManager());
	}

	protected class ReverseOperation extends AbstractModelOperation{

		private int fOriginalIndex;
		private AbstractParameterNode fReversedParameter;
		private IParametersParentNode fReversedTarget;

		public ReverseOperation(
				IParametersParentNode target, 
				AbstractParameterNode parameter, 
				IExtLanguageManager extLanguageManager) {
			
			super("reverse " + OperationNames.ADD_PARAMETER, extLanguageManager);
			fReversedTarget = target;
			fReversedParameter = parameter;
		}

		@Override
		public void execute() {
			setOneNodeToSelect(fParametersParentNode);
			fOriginalIndex = fReversedParameter.getMyIndex();
			fReversedTarget.removeParameter(fReversedParameter);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new GenericOperationAddParameter(fReversedTarget, fReversedParameter, fOriginalIndex, true, getExtLanguageManager());
		}
	}
	
}
