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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ConstraintHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForLabel;

public class ChoiceOperationRenameLabel extends AbstractModelOperation {

	public ChoiceNode fChoiceNode;
	public String fCurrentLabel;
	public String fNewLabel;
	public IExtLanguageManager fExtLanguageManager;

	public ChoiceOperationRenameLabel(
			ChoiceNode choiceNode, String currentLabel, String newLabel, IExtLanguageManager extLanguageManager) {

		super(OperationNames.RENAME_LABEL, extLanguageManager);

		fChoiceNode = choiceNode;
		fCurrentLabel = currentLabel;
		fNewLabel = newLabel;
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public void execute() {

		renameLabel(fChoiceNode, fCurrentLabel, fNewLabel); 
	}

	private void renameLabel(ChoiceNode choiceNode, String currentLabel, String newLabel) {
		MethodNode methodNode = choiceNode.getMethodNode();

		if (methodNode == null) {
			ExceptionHelper.reportRuntimeException("Renaming label of global node is not allowed.");
		}

		AbstractParameterNode abstractParameterNode = choiceNode.getParameter();
		MethodParameterNode methodParameterNode = (MethodParameterNode)abstractParameterNode;

		choiceNode.renameLabel(currentLabel, newLabel);

		ParameterConversionDefinition parameterConversionDefinition = 
				createParameterConversionDefinition(currentLabel, newLabel);


		ConstraintHelper.convertValuesOfConstraintsToType(methodParameterNode, parameterConversionDefinition);
	}

	private ParameterConversionDefinition createParameterConversionDefinition(String currentLabel, String newLabel) {

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		IParameterConversionItemPart srcPart = new ParameterConversionItemPartForLabel(currentLabel);
		IParameterConversionItemPart dstPart = new ParameterConversionItemPartForLabel(newLabel);

		ParameterConversionItem parameterConversionItem = new ParameterConversionItem(srcPart, dstPart, (String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItem);

		return parameterConversionDefinition;
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation();
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation() {
			super(OperationNames.RENAME_LABEL, fExtLanguageManager);
		}

		@Override
		public void execute() {
			renameLabel(fChoiceNode, fNewLabel, fCurrentLabel);

		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}

	}
}
