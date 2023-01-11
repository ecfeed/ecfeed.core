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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ChoiceOperationRemoveLabel extends CompositeOperation{

	private class RemoveLabelOperation extends AbstractModelOperation{

		private ChoiceNode fTarget;
		private String fLabel;

		public RemoveLabelOperation(ChoiceNode target, String label, IExtLanguageManager extLanguageManager) {
			super(ChoiceOperationRemoveLabel.this.getName(), extLanguageManager);
			fTarget = target;
			fLabel = label;
		}

		@Override
		public void execute() {
			setOneNodeToSelect(fTarget);
			fTarget.removeLabel(fLabel);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationAddLabel(fTarget, fLabel, getExtLanguageManager());
		}

	}

	public ChoiceOperationRemoveLabel(ChoiceNode target, String label, IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_PARTITION_LABEL, true, target, target, extLanguageManager);
		
		addOperation(new RemoveLabelOperation(target, label, extLanguageManager));

		for (MethodNode method : target.getParameter().getMethods()) {
			if (method != null) {
				addOperation(new MethodOperationRemoveInconsistentChildren(method, extLanguageManager));
			}
		}
	}
}
