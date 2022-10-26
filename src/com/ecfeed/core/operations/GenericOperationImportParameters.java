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
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericOperationImportParameters extends AbstractModelOperation {

	private IParametersParentNode fParametersParentNode;
	private List<AbstractParameterNode> fAbstractParameterNodes;

	public GenericOperationImportParameters(
			IParametersParentNode parametersParentNode, 
			List<AbstractParameterNode> abstractParameterNodes,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.IMPORT_PARAMETERS, extLanguageManager);
		
		fParametersParentNode = parametersParentNode;
		fAbstractParameterNodes = abstractParameterNodes;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fParametersParentNode);
		
		int index = 0;
		for (AbstractParameterNode node : fAbstractParameterNodes) {
			fParametersParentNode.addParameter(node, index++);
		}
		
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(fParametersParentNode, fAbstractParameterNodes, getExtLanguageManager());
	}

	protected class ReverseOperation extends AbstractModelOperation{

		private IParametersParentNode fParametersParentNode;
		private List<AbstractParameterNode> fAbstractParameterNodes;
		
		public ReverseOperation(IParametersParentNode parametersParentNode, List<AbstractParameterNode> abstractParameterNodes, IExtLanguageManager extLanguageManager) {
			super("reverse " + OperationNames.IMPORT_PARAMETERS, extLanguageManager);
			
			fParametersParentNode = parametersParentNode;
			fAbstractParameterNodes = abstractParameterNodes;
		}

		@Override
		public void execute() {
			setOneNodeToSelect(fParametersParentNode);
			
			for (AbstractParameterNode node : fAbstractParameterNodes) {
				fParametersParentNode.removeParameter(node);
			}
			
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new GenericOperationImportParameters(fParametersParentNode, fAbstractParameterNodes, getExtLanguageManager());
		}
	}
	
}
