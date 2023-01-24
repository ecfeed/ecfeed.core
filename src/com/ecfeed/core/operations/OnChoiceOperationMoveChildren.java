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
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnChoiceOperationMoveChildren extends AbstractModelOperation {

	private static final String MOVE_CHOICES = "Move choices.";

	private ChoiceNode fSrcChoiceNode; 
	private ChoiceNode fDstChoiceNode;
	IExtLanguageManager fExtLanguageManager;

	public OnChoiceOperationMoveChildren(
			ChoiceNode srcChoiceNode, 
			ChoiceNode dstChoiceNode,
			IExtLanguageManager extLanguageManager) {

		super(MOVE_CHOICES, extLanguageManager);

		fSrcChoiceNode = srcChoiceNode;
		fDstChoiceNode = dstChoiceNode;

		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public void execute() {

		ChoiceNodeHelper.moveChildChoices(fSrcChoiceNode, fDstChoiceNode);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(MOVE_CHOICES, extLanguageManager);
		}

		@Override
		public void execute() {
			ChoiceNodeHelper.moveChildChoices(fDstChoiceNode, fSrcChoiceNode);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OnChoiceOperationMoveChildren(fSrcChoiceNode, fDstChoiceNode, fExtLanguageManager);
		}

	}

}
