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

import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodOperationConvertTo extends AbstractModelOperation {

	private MethodNode fTarget;
	private MethodNode fSource;

	public MethodOperationConvertTo(MethodNode target, MethodNode source, ExtLanguage viewMode) {
		
		super(OperationNames.CONVERT_METHOD, viewMode);
		
		fTarget = target;
		fSource = source;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);

		// TODO SIMPLE-VIEW  check - use method from helper instead of getMethod
		if(fTarget.getClassNode().getMethod(fSource.getName(), fSource.getParameterTypes()) != null){
			
			ModelOperationException.report(
					ClassNodeHelper.generateMethodSignatureDuplicateMessage(
							fTarget.getClassNode(), fTarget, getViewMode()));
		}

		if(fTarget.getParameterTypes().equals(fSource.getParameterTypes()) == false){
			ModelOperationException.report(ClassNodeHelper.METHODS_INCOMPATIBLE_PROBLEM);
		}

		fTarget.setName(fSource.getName());

		for(int i = 0; i < fTarget.getParameters().size(); i++){
			MethodParameterNode targetParameter = fTarget.getMethodParameters().get(i);
			MethodParameterNode sourceParameter = fSource.getMethodParameters().get(i);

			targetParameter.setName(sourceParameter.getName());
		}

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodOperationConvertTo(fSource, fTarget, getViewMode());
	}

}
