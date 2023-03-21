/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.link;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ListOfModelOperations;
import com.ecfeed.core.model.ParameterTransformer;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionDefinition;

public class MethodOperationLinkToGlobalParameter extends AbstractModelOperation {

	BasicParameterNode fSrcMethodParameterNode;
	BasicParameterNode fDstParameterForChoices;
	ParameterConversionDefinition fParameterConversionDefinition;

	ListOfModelOperations fReverseOperations;

	IExtLanguageManager fExtLanguageManager;

	public MethodOperationLinkToGlobalParameter(
			BasicParameterNode srcMethodParameterNode,
			BasicParameterNode dstParameterForChoices, 
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager) {

		super("Link to global parameter", extLanguageManager);

		fSrcMethodParameterNode = srcMethodParameterNode;
		fDstParameterForChoices = dstParameterForChoices;
		fParameterConversionDefinition = parameterConversionDefinition;
		fExtLanguageManager = extLanguageManager;

		fReverseOperations = new ListOfModelOperations();
	}

	@Override
	public void execute() {

		fReverseOperations = new ListOfModelOperations();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				fSrcMethodParameterNode,
				fDstParameterForChoices, 
				fParameterConversionDefinition,
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
			super(MethodOperationLinkToGlobalParameter.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {

			fReverseOperations.executeFromTail();
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodOperationLinkToGlobalParameter(
					fSrcMethodParameterNode,
					fDstParameterForChoices, 
					fParameterConversionDefinition,
					fExtLanguageManager);
		}

	}

}
