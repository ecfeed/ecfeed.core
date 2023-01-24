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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.BooleanHolder;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnCompositeParameterOperationRemoveInconsistentChildren extends AbstractModelOperation {

	private CompositeParameterNode fCompositeParameterNode;
	private List<ConstraintNode> fOriginalConstraints;

	public OnCompositeParameterOperationRemoveInconsistentChildren(
			CompositeParameterNode target, IExtLanguageManager extLanguageManager) {

		super(OperationNames.MAKE_CONSISTENT, extLanguageManager);

		fCompositeParameterNode = target;
		fOriginalConstraints = null;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fCompositeParameterNode);

		fOriginalConstraints = new ArrayList<ConstraintNode>(fCompositeParameterNode.getConstraintNodes());

		BooleanHolder modelUpdated = new BooleanHolder(false);

		ConstraintsParentNodeHelper.removeInconsistentConstraints(fCompositeParameterNode, modelUpdated);

		if (modelUpdated.get()) {
			markModelUpdated();
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OperationNames.MAKE_CONSISTENT, extLanguageManager);
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fCompositeParameterNode);
			fCompositeParameterNode.replaceConstraints(fOriginalConstraints);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OnCompositeParameterOperationRemoveInconsistentChildren(
					fCompositeParameterNode, getExtLanguageManager());
		}

	}

}
