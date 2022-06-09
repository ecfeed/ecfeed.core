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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

// TODO DE-NO remove?

public class MethodOperationUpdateChoicesInConstraints extends AbstractModelOperation {

	private static final String UPDATE_CHOICE_REFERENCES_IN_TEST_CASES = "Update choice references in test cases.";

	private ChoiceNode fOldChoiceNode; 
	private ChoiceNode fNewChoiceNode;
	private List<ConstraintNode> fConstraintNodes;
	private IExtLanguageManager fExtLanguageManager;

	public MethodOperationUpdateChoicesInConstraints(
			ChoiceNode oldChoiceNode, 
			ChoiceNode newChoiceNode,
			List<ConstraintNode> constraintNodes,
			IExtLanguageManager extLanguageManager) {

		super(UPDATE_CHOICE_REFERENCES_IN_TEST_CASES, extLanguageManager);

		fOldChoiceNode = oldChoiceNode;
		fNewChoiceNode = newChoiceNode;
		fConstraintNodes = constraintNodes;
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public void execute() {

		MethodNodeHelper.updateChoiceReferencesInConstraints(
				fOldChoiceNode, 
				fNewChoiceNode,
				fConstraintNodes,
				fExtLanguageManager);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(UPDATE_CHOICE_REFERENCES_IN_TEST_CASES, extLanguageManager);
		}

		@Override
		public void execute() {
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null; // TODO DE-NO
		}

	}

}
