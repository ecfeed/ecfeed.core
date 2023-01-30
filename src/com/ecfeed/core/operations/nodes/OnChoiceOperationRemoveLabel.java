/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnChoiceOperationRemoveLabel extends CompositeOperation{

	private class RemoveLabelOperation extends AbstractModelOperation{

		private ChoiceNode fTarget;
		private String fLabel;

		public RemoveLabelOperation(ChoiceNode target, String label, IExtLanguageManager extLanguageManager) {
			super(OnChoiceOperationRemoveLabel.this.getName(), extLanguageManager);
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
			return new OnChoiceOperationAddLabel(fTarget, fLabel, getExtLanguageManager());
		}

	}

	public OnChoiceOperationRemoveLabel(ChoiceNode target, String label, IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_PARTITION_LABEL, true, target, target, extLanguageManager);
		
		addOperation(new RemoveLabelOperation(target, label, extLanguageManager));

		for (MethodNode method : target.getParameter().getMethods()) {
			if (method != null) {
				addOperation(new OnMethodOperationRemoveInconsistentChildren(method, extLanguageManager));
			}
		}
	}
}