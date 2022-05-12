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

import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.ListOfModelOperations;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ParameterLinker;
import com.ecfeed.core.utils.ChoiceConversionList;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodOperationLikToGlobalParameter extends AbstractModelOperation {

	MethodParameterNode fSrcMethodParameterNode;
	GlobalParameterNode fDstParameterForChoices;
	ChoiceConversionList fChoiceConversionList;

	ListOfModelOperations fReverseOperations;

	IExtLanguageManager fExtLanguageManager;

	public MethodOperationLikToGlobalParameter(
			MethodParameterNode srcMethodParameterNode,
			GlobalParameterNode dstParameterForChoices, 
			ChoiceConversionList choiceConversionList,
			IExtLanguageManager extLanguageManager) {

		super("Link to global parameter", extLanguageManager);

		fSrcMethodParameterNode = srcMethodParameterNode;
		fDstParameterForChoices = dstParameterForChoices;
		fChoiceConversionList = choiceConversionList;
		fExtLanguageManager = extLanguageManager;

		fReverseOperations = new ListOfModelOperations();
	}

	@Override
	public void execute() {

		fReverseOperations = new ListOfModelOperations();

		ParameterLinker.linkMethodParameteToGlobalParameter(
				fSrcMethodParameterNode,
				fDstParameterForChoices, 
				fChoiceConversionList,
				fReverseOperations,
				fExtLanguageManager);

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(MethodOperationLikToGlobalParameter.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {

			fReverseOperations.executeFromTail();
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodOperationLikToGlobalParameter(
					fSrcMethodParameterNode,
					fDstParameterForChoices, 
					fChoiceConversionList,
					fExtLanguageManager);
		}

	}

}
