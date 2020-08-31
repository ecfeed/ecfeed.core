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
import java.util.Iterator;
import java.util.List;

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodOperationMakeConsistent extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private List<ConstraintNode> fOriginalConstraints;
	private List<TestCaseNode> fOriginalTestCases;

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation(ExtLanguage viewMode) {
			super(OperationNames.MAKE_CONSISTENT, viewMode);
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fMethodNode);
			fMethodNode.replaceTestCases(fOriginalTestCases);
			fMethodNode.replaceConstraints(fOriginalConstraints);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodOperationMakeConsistent(fMethodNode, getViewMode());
		}

	}

	public MethodOperationMakeConsistent(MethodNode target, ExtLanguage viewMode) {
		
		super(OperationNames.MAKE_CONSISTENT, viewMode);
		
		fMethodNode = target;
		fOriginalConstraints = new ArrayList<ConstraintNode>(target.getConstraintNodes());
		fOriginalTestCases = new ArrayList<TestCaseNode>(target.getTestCases());
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fMethodNode);

		boolean modelUpdated = false;

		Iterator<TestCaseNode> tcIt = fMethodNode.getTestCases().iterator();
		while (tcIt.hasNext()) {
			if (tcIt.next().isConsistent() == false) {
				tcIt.remove();
				modelUpdated = true;
			}
		}

		MethodNode.ConstraintsItr constraintItr = fMethodNode.getIterator();
		while (fMethodNode.hasNextConstraint(constraintItr)) {
			if (!fMethodNode.getNextConstraint(constraintItr).isConsistent()) {
				fMethodNode.removeConstraint(constraintItr);
				modelUpdated = true;
			}
		}		

		if (modelUpdated) {
			markModelUpdated();
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getViewMode());
	}

}
