/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import com.ecfeed.core.model.AbstractNodeHelper;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnMethodOperationConvertTo extends AbstractModelOperation {

	private MethodNode fTargetMethodNode;
	private MethodNode fSourceMethodNode;

	public OnMethodOperationConvertTo(MethodNode target, MethodNode source, IExtLanguageManager extLanguageManager) {

		super(OperationNames.CONVERT_METHOD, extLanguageManager);

		fTargetMethodNode = target;
		fSourceMethodNode = source;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fTargetMethodNode);

		ClassNode classNode = fTargetMethodNode.getClassNode();

		String methodName = AbstractNodeHelper.getName(fSourceMethodNode, getExtLanguageManager());
		//List<String> methodParameters = ParametersParentNodeHelper.getParameterTypes(fSourceMethodNode, getExtLanguageManager());

		if (ClassNodeHelper.findMethodByExtLanguage(
				classNode, 
				methodName, 
				getExtLanguageManager()) != null) {

			ExceptionHelper.reportRuntimeException(
					ClassNodeHelper.createMethodNameDuplicateMessage(
							classNode, fTargetMethodNode, false, getExtLanguageManager()));
		}

		if(fTargetMethodNode.getParameterTypes().equals(fSourceMethodNode.getParameterTypes()) == false){
			ExceptionHelper.reportRuntimeException(ClassNodeHelper.METHODS_INCOMPATIBLE_PROBLEM);
		}

		fTargetMethodNode.setName(methodName);

		for(int i = 0; i < fTargetMethodNode.getParameters().size(); i++){
			AbstractParameterNode targetParameter = fTargetMethodNode.getParameters().get(i);
			AbstractParameterNode sourceParameter = fSourceMethodNode.getParameters().get(i);

			targetParameter.setName(sourceParameter.getName());
		}

		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnMethodOperationConvertTo(fSourceMethodNode, fTargetMethodNode, getExtLanguageManager());
	}

}
