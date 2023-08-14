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

import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.utils.MethodsWithResultsOfGenerations;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnParameterOperationRemove extends AbstractModelOperation {

	private static final String REMOVE_BASIC_PARAMETER = "Remove basic parameter";

	private IParametersParentNode fParent;
	private AbstractParameterNode fBasicParameterNode;
	private int fParameterIndex;

	private MethodsWithResultsOfGenerations fMethodsWithResultsOfGenerations;

	public OnParameterOperationRemove(
			IParametersParentNode parent,
			AbstractParameterNode basicParameterNode,
			IExtLanguageManager extLanguageManager) {

		super(REMOVE_BASIC_PARAMETER, extLanguageManager);

		fParent = parent;
		fBasicParameterNode = basicParameterNode;

		fMethodsWithResultsOfGenerations = new MethodsWithResultsOfGenerations();
	}

	@Override
	public void execute() {

		List<MethodNode> methodNodes = MethodNodeHelper.findMentioningMethodNodes(fBasicParameterNode);
		fMethodsWithResultsOfGenerations.saveResultsForMethods(methodNodes);
		fMethodsWithResultsOfGenerations.clearResultsForAllMethods();

		fParameterIndex = fBasicParameterNode.getMyIndex();
		fParent.removeParameter(fBasicParameterNode);

		markModelUpdated();
	}

	@Override
	public String toString() {
		return createDescription(fBasicParameterNode.getName());
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OperationAddParameter(getExtLanguageManager());
	}

	private class OperationAddParameter extends AbstractModelOperation {

		public OperationAddParameter(IExtLanguageManager extLanguageManager) {
			super(AbstractModelOperation.createReverseOperationName(
					REMOVE_BASIC_PARAMETER), extLanguageManager);
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fParent);

			OnParameterOperationAddToParent onParameterOperationAddToParent = 
					new OnParameterOperationAddToParent(
							fParent,
							fBasicParameterNode,
							fParameterIndex,
							getExtLanguageManager());

			onParameterOperationAddToParent.execute();

			fMethodsWithResultsOfGenerations.restoreResultsForAllMethods();
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}

	}

}


