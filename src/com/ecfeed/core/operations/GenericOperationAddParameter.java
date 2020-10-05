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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ParametersParentNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericOperationAddParameter extends AbstractModelOperation {

	private ParametersParentNode fParametersParentNode;
	private AbstractParameterNode fAbstractParameterNode;
	private int fNewIndex;
	private boolean fGenerateUniqueName;

	public GenericOperationAddParameter(
			ParametersParentNode target, 
			AbstractParameterNode parameter, 
			int index, 
			boolean generateUniqueName,
			IExtLanguageManager extLanguage) {
		
		super(OperationNames.ADD_PARAMETER, extLanguage);
		fParametersParentNode = target;
		fAbstractParameterNode = parameter;
		fNewIndex = (index == -1)? target.getParameters().size() : index;
		fGenerateUniqueName = generateUniqueName;
	}

	public GenericOperationAddParameter(
			ParametersParentNode target, 
			AbstractParameterNode parameter, 
			boolean generateUniqueName,
			IExtLanguageManager extLanguage) {
		this(target, parameter, -1, generateUniqueName, extLanguage);
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fParametersParentNode);

		if (fGenerateUniqueName) {
			generateUniqueParameterName(fAbstractParameterNode);
		}
		
		String parameterName = fAbstractParameterNode.getName();

		if(fNewIndex < 0){
			ModelOperationException.report(OperationMessages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fNewIndex > fParametersParentNode.getParameters().size()){
			ModelOperationException.report(OperationMessages.TOO_HIGH_INDEX_PROBLEM);
		}
		if(fParametersParentNode.getParameter(parameterName) != null){
			ModelOperationException.report(OperationMessages.CATEGORY_NAME_DUPLICATE_PROBLEM);
		}

		fParametersParentNode.addParameter(fAbstractParameterNode, fNewIndex);
		markModelUpdated();
	}

	private void generateUniqueParameterName(AbstractParameterNode abstractParameterNode) {

		String newName = ParametersParentNode.generateNewParameterName(fParametersParentNode, abstractParameterNode.getName());
		abstractParameterNode.setName(newName);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(fParametersParentNode, fAbstractParameterNode, getExtLanguageManager());
	}

	protected class ReverseOperation extends AbstractModelOperation{

		private int fOriginalIndex;
		private AbstractParameterNode fReversedParameter;
		private ParametersParentNode fReversedTarget;

		public ReverseOperation(ParametersParentNode target, AbstractParameterNode parameter, IExtLanguageManager extLanguage) {
			super("reverse " + OperationNames.ADD_PARAMETER, extLanguage);
			fReversedTarget = target;
			fReversedParameter = parameter;
		}

		@Override
		public void execute() throws ModelOperationException {
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
