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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class SimpleOperationSetMethodParameterType extends AbstractModelOperation {

	private static final String SET_PARAMETER_TYPE = "Set parameter type";

	private BasicParameterNode fMethodParameterNode;
	private String fOldType;
	private String fNewType;

	public SimpleOperationSetMethodParameterType(
			BasicParameterNode methodParameterNode,
			String newType,
			IExtLanguageManager extLanguageManager) {

		super(SET_PARAMETER_TYPE, extLanguageManager);

		fMethodParameterNode = methodParameterNode;
		fNewType = newType;
		fOldType = methodParameterNode.getType();
	}

	@Override
	public void execute() {

		fMethodParameterNode.setType(fNewType);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(SET_PARAMETER_TYPE + " - reverse operation", extLanguageManager);
		}

		@Override
		public void execute() {

			fMethodParameterNode.setType(fOldType);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}

	}

}
