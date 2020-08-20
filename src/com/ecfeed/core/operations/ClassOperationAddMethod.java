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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.ViewMode;

public class ClassOperationAddMethod extends AbstractModelOperation{

	private ClassNode fClassNode;
	private MethodNode fMethod;
	private int fIndex;
	
	private static final String UNEXPECTED_PROBLEM_WHILE_ADDING_ELEMENT = "Element could not be added to the model";

	public ClassOperationAddMethod(ClassNode target, MethodNode method, int index, ViewMode viewMode) {
		super(OperationNames.ADD_METHOD, viewMode);
		fClassNode = target;
		fMethod = method;
		fIndex = index;
	}

	public ClassOperationAddMethod(ClassNode target, MethodNode method, ViewMode viewMode) {
		this(target, method, -1, viewMode);
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fClassNode);
		List<String> problems = new ArrayList<String>();

		if(fIndex == -1){
			fIndex = fClassNode.getMethods().size();
		}

		generateUniqeMethodName(fMethod);

		if(ClassNodeHelper.isNewMethodSignatureValid(
				fClassNode, 
				fMethod.getName(), 
				fMethod.getParameterTypes(),
				problems) == false){
			
			ClassNodeHelper.updateNewMethodsSignatureProblemList(
					fClassNode, fMethod.getName(), fMethod.getParameterTypes(), problems, getViewMode());
			
			ModelOperationException.report(StringHelper.convertToMultilineString(problems));
		}

		if(fClassNode.addMethod(fMethod, fIndex) == false){
			ModelOperationException.report(UNEXPECTED_PROBLEM_WHILE_ADDING_ELEMENT);
		}

		markModelUpdated();
	}

	private void generateUniqeMethodName(MethodNode methodNode) {
		
		String newName = 
				ClassNodeHelper.generateNewMethodName(
						fClassNode, methodNode.getName(), methodNode.getParameterTypes());
		
		methodNode.setName(newName);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ClassOperationRemoveMethod(fClassNode, fMethod, getViewMode());
	}

}
