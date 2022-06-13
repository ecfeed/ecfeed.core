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
import com.ecfeed.core.model.ParameterTransformer;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodOperationUnlinkFromGlobalParameter extends AbstractModelOperation {

	MethodParameterNode fMethodParameterNode;
	GlobalParameterNode fGlobalParameterNode;

	ListOfModelOperations fReverseOperations;

	IExtLanguageManager fExtLanguageManager;

	public MethodOperationUnlinkFromGlobalParameter(
			GlobalParameterNode globalParameterNode,
			MethodParameterNode methodParameterNode, 
			IExtLanguageManager extLanguageManager) {

		super("Unlink from global parameter", extLanguageManager);

		fMethodParameterNode = methodParameterNode;
		fGlobalParameterNode = globalParameterNode;
		fExtLanguageManager = extLanguageManager;

		fReverseOperations = new ListOfModelOperations();
	}

	@Override
	public void execute() {

		fReverseOperations = new ListOfModelOperations();

		ParameterTransformer.unlinkMethodParameteFromGlobalParameter(
				fMethodParameterNode,
				fGlobalParameterNode, 
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
			super(MethodOperationUnlinkFromGlobalParameter.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {

			fReverseOperations.executeFromTail();
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new MethodOperationUnlinkFromGlobalParameter(
					fGlobalParameterNode, 
					fMethodParameterNode,
					fExtLanguageManager);
		}

	}

}
