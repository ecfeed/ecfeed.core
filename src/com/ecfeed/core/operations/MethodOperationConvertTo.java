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

import java.util.List;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodOperationConvertTo extends AbstractModelOperation {

	private MethodNode fTargetMethodNode;
	private MethodNode fSourceMethodNode;

	public MethodOperationConvertTo(MethodNode target, MethodNode source, IExtLanguageManager extLanguageManager) {

		super(OperationNames.CONVERT_METHOD, extLanguageManager);

		fTargetMethodNode = target;
		fSourceMethodNode = source;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTargetMethodNode);

		ClassNode classNode = fTargetMethodNode.getClassNode();

		String methodName = MethodNodeHelper.getName(fSourceMethodNode, getExtLanguageManager());
		List<String> methodParameters = MethodNodeHelper.getParameterTypes(fSourceMethodNode, getExtLanguageManager());

		if (ClassNodeHelper.findMethodByExtLanguage(
				classNode, 
				methodName, 
				methodParameters, 
				getExtLanguageManager()) != null) {

			ModelOperationException.report(
					ClassNodeHelper.createMethodSignatureDuplicateMessage(
							classNode, fTargetMethodNode, false, getExtLanguageManager()));
		}

		if(fTargetMethodNode.getParameterTypes().equals(fSourceMethodNode.getParameterTypes()) == false){
			ModelOperationException.report(ClassNodeHelper.METHODS_INCOMPATIBLE_PROBLEM);
		}

		fTargetMethodNode.setName(methodName);

		for(int i = 0; i < fTargetMethodNode.getParameters().size(); i++){
			MethodParameterNode targetParameter = fTargetMethodNode.getMethodParameters().get(i);
			MethodParameterNode sourceParameter = fSourceMethodNode.getMethodParameters().get(i);

			targetParameter.setName(sourceParameter.getName());
		}

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new MethodOperationConvertTo(fSourceMethodNode, fTargetMethodNode, getExtLanguageManager());
	}

}
