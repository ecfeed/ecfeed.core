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
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OperationSimpleAddChoice extends AbstractModelOperation {

	private static final String ADD_CHOICE = "Add choice";

	private ChoiceNode fChoiceNode;
	private ChoicesParentNode fChoicesParentNode;

	public OperationSimpleAddChoice(
			ChoiceNode choiceNode, 
			ChoicesParentNode choicesParentNode, 
			IExtLanguageManager extLanguageManager){

		super(ADD_CHOICE, extLanguageManager);

		fChoiceNode = choiceNode;
		fChoicesParentNode = choicesParentNode;
	}

	@Override
	public void execute() {

		fChoicesParentNode.addChoice(fChoiceNode);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OperationSimpleAddChoice.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {

			fChoicesParentNode.removeChoice(fChoiceNode);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OperationSimpleAddChoice(fChoiceNode, fChoicesParentNode, getExtLanguageManager());
		}

	}

}
