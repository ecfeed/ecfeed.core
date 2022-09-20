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

import java.util.Map;

import com.ecfeed.core.model.ConstraintHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ParameterTransformer;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionDefinition;

public class MethodParameterOperationConvertValues extends AbstractModelOperation { // TODO DE-NO delete ??

	private MethodNode fMethodNode;
	private MethodParameterNode fMethodParameterNode;
	private ParameterConversionDefinition fParameterConversionDefinition;
	private IExtLanguageManager fExtLanguageManager;

	private Map<Integer, String> fOriginalConstraintValues;
	//	private Map<Integer, String> fOriginalChoiceValues;  // TODO DE-NO

	public MethodParameterOperationConvertValues(
			MethodParameterNode methodParameterNode, 
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.CONVERT_VALUES, extLanguageManager);

		fMethodParameterNode = methodParameterNode;
		fParameterConversionDefinition = parameterConversionDefinition;
		fExtLanguageManager = extLanguageManager;

		fMethodNode = fMethodParameterNode.getMethod();

		fOriginalConstraintValues = ConstraintHelper.getOriginalConstraintValues(fMethodNode);
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fMethodNode);

		ParameterTransformer.convertChoicesAndConstraintsToType(
				fMethodParameterNode, fParameterConversionDefinition);		

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

			ConstraintHelper.restoreOriginalConstraintValues(fMethodNode, fOriginalConstraintValues);

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodParameterOperationConvertValues(
					fMethodParameterNode, 
					fParameterConversionDefinition,
					fExtLanguageManager);
		}

	}

}
