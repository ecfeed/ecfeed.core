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

import java.util.Set;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ModelOperationException;

public class ChoiceOperationAddLabel extends AbstractModelOperation {

	private ChoiceNode fTarget;
	private String fLabel;
	private Set<ChoiceNode> fLabeledDescendants;

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(ChoiceOperationAddLabel.this.getName());
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fTarget);
			fTarget.removeLabel(fLabel);

			for(ChoiceNode p : fLabeledDescendants){
				p.addLabel(fLabel);
			}

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationAddLabel(fTarget, fLabel);
		}

	}

	public ChoiceOperationAddLabel(ChoiceNode target, String label){
		super(OperationNames.ADD_PARTITION_LABEL);

		fTarget = target;
		fLabel = label;
		fLabeledDescendants = target.getLabeledChoices(fLabel);
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);
		fTarget.addLabel(fLabel);

		for(ChoiceNode p : fLabeledDescendants){
			p.removeLabel(fLabel);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation();
	}

}
