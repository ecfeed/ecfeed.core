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

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodOperationConvertTo extends AbstractModelOperation {

	private MethodNode fTarget;
	private MethodNode fSource;

	public MethodOperationConvertTo(MethodNode target, MethodNode source, ExtLanguage extLanguage) {
		
		super(OperationNames.CONVERT_METHOD, extLanguage);
		
		fTarget = target;
		fSource = source;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);

		ClassNode classNode = fTarget.getClassNode();

		// TODO SIMPLE-VIEW convert method name and types to ext language
		if(ClassNodeHelper.findMethod(classNode, fSource.getName(), fSource.getParameterTypes(), getExtLanguage()) != null){
			
			ModelOperationException.report(
					ClassNodeHelper.createMethodSignatureDuplicateMessage(
							classNode, fTarget, getExtLanguage()));
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
		return new MethodOperationConvertTo(fSource, fTarget, getExtLanguage());
	}

}
