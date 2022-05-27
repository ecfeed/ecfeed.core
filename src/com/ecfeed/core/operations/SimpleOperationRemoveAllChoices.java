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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class SimpleOperationRemoveAllChoices extends AbstractModelOperation {

	private static final String REMOVE_ALL_CHOICES_OF_PARAMETER = "Remove all choices of parameter";

	ChoicesParentNode fChoicesParentNode;
	List<ChoiceNode> fOldChoiceNodes;

	public SimpleOperationRemoveAllChoices(
			ChoicesParentNode choicesParentNode, IExtLanguageManager extLanguageManager) {

		super(REMOVE_ALL_CHOICES_OF_PARAMETER, extLanguageManager);

		fChoicesParentNode = choicesParentNode;
		fOldChoiceNodes = new ArrayList<ChoiceNode>(fChoicesParentNode.getChoices());
	}

	@Override
	public void execute() {

		List<ChoiceNode> copyOfChoiceNodes = new ArrayList<ChoiceNode>(fChoicesParentNode.getChoices());

		for (ChoiceNode choiceNode : copyOfChoiceNodes) {
			fChoicesParentNode.removeChoice(choiceNode);
		}

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(REMOVE_ALL_CHOICES_OF_PARAMETER + " - reverse operation", extLanguageManager);
		}

		@Override
		public void execute() {

			for (ChoiceNode choiceNode : fOldChoiceNodes) {

				fChoicesParentNode.addChoice(choiceNode);
			}

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new SimpleOperationRemoveAllChoices(fChoicesParentNode, getExtLanguageManager());
		}

	}

}
