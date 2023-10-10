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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericOperationPopulateParameterWithChoices extends AbstractModelOperation {

	private BasicParameterNode fParentNode;
	private List<ChoiceNode> fChoiceNodes;

	public GenericOperationPopulateParameterWithChoices(
			BasicParameterNode parentNode, 
			List<ChoiceNode> choiceNodes,
			IExtLanguageManager extLanguageManager) {
		
		super(OperationNames.POPULATE_PARAMETER_WITH_CHOICES, extLanguageManager);
		
		fParentNode = parentNode;
		fChoiceNodes = choiceNodes;
	}

	@Override
	public void execute() {
		setOneNodeToSelect(fParentNode);
		
		int index = 0;
		for (ChoiceNode node : fChoiceNodes) {
			fParentNode.addChoice(node, index++);
		}
		
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(fParentNode, fChoiceNodes, getExtLanguageManager());
	}

	protected class ReverseOperation extends AbstractModelOperation{

		private BasicParameterNode fParentNode;
		private List<ChoiceNode> fChoiceNodes;
		
		public ReverseOperation(
				BasicParameterNode parentNode, 
				List<ChoiceNode> choiceNodes, 
				IExtLanguageManager extLanguageManager) {
			
			super("reverse " + OperationNames.POPULATE_PARAMETER_WITH_CHOICES, extLanguageManager);
			
			fParentNode = parentNode;
			fChoiceNodes = choiceNodes;
		}

		@Override
		public void execute() {
			setOneNodeToSelect(fParentNode);
			
			for (ChoiceNode node : fChoiceNodes) {
				fParentNode.removeChoice(node);
			}
			
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new GenericOperationPopulateParameterWithChoices(fParentNode, fChoiceNodes, getExtLanguageManager());
		}
	}
	
}
