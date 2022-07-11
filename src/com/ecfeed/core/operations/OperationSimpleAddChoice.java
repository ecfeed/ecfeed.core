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

public class OperationSimpleAddChoice extends AbstractOneWayModelOperation {

	private static final String ADD_CHOICE = "Add choice";

	private ChoiceNode fChoiceNode;
	private int fIndexOfChoice;
	private ChoicesParentNode fChoicesParentNode;

	public OperationSimpleAddChoice(
			ChoiceNode choiceNode, 
			int indexOfChoice,
			ChoicesParentNode choicesParentNode, 
			IExtLanguageManager extLanguageManager){

		super(ADD_CHOICE, extLanguageManager);

		fChoiceNode = choiceNode;
		fIndexOfChoice = indexOfChoice;
		fChoicesParentNode = choicesParentNode;
	}

	@Override
	public void execute() {

		fChoicesParentNode.addChoice(fChoiceNode, fIndexOfChoice);
		markModelUpdated();
	}

	@Override
	public String toString() {

		return "Add choice " + fChoiceNode.getName() + " at index " + fIndexOfChoice +
				" to parent " + fChoicesParentNode.getName() + ".";
	}

}
