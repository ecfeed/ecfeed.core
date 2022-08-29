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

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ParameterTransformer;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionDefinition;

public class MethodParameterOperationConvertValues extends AbstractModelOperation {

	private MethodNode fMethodNode;
	private MethodParameterNode fMethodParameterNode;
	private String fNewType;
	private ParameterConversionDefinition fParameterConversionDefinition;
	private IExtLanguageManager fExtLanguageManager;

	private List<ConstraintNode> fOriginalConstraints;
	private List<TestCaseNode> fOriginalTestCases;

	public MethodParameterOperationConvertValues(
			MethodParameterNode methodParameterNode, 
			String newType,
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.MAKE_CONSISTENT, extLanguageManager);

		fMethodParameterNode = methodParameterNode;
		fNewType = newType;
		fParameterConversionDefinition = parameterConversionDefinition;
		fExtLanguageManager = extLanguageManager;

		fMethodNode = fMethodParameterNode.getMethod();
		fOriginalConstraints = new ArrayList<ConstraintNode>(fMethodNode.getConstraintNodes());
		fOriginalTestCases = new ArrayList<TestCaseNode>(fMethodNode.getTestCases());
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fMethodNode);

		ParameterTransformer.convertParameterToType(
				fMethodParameterNode, fNewType, fParameterConversionDefinition);		

		markModelUpdated();
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

			setOneNodeToSelect(fMethodNode);
			fMethodNode.replaceTestCases(fOriginalTestCases);
			fMethodNode.replaceConstraints(fOriginalConstraints);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodParameterOperationConvertValues(
					fMethodParameterNode, 
					fNewType,
					fParameterConversionDefinition,
					fExtLanguageManager);
		}

	}


}
